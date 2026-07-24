package com.kotlinjsonui.embed

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import java.io.Serializable

/**
 * Container for the `Embed` view type. Hosts another screen as a region of
 * the parent layout. The embedded screen owns its own ViewModel via an
 * isolated [ViewModelStoreOwner] keyed by [embedId] — this is what makes
 * multiple embeds of the same screen yield distinct VM instances.
 *
 * Lives in the main `library` module (NOT `library-dynamic`) because the
 * static codegen path emits `EmbedContainer(...)` calls into every consumer
 * `<Screen>GeneratedView.kt`, which has to compile in release builds where
 * `library-dynamic` is not on the classpath.
 *
 * Contracts:
 * - [params] is a Map<String, Any> tree (nested literal objects allowed;
 *   bindings resolve at leaves — validated by the CLI). VMs that implement
 *   [InitParamsReceiver] receive them on first composition AND on every
 *   change. Other VMs silently ignore.
 * - [eventBridge] receives [EmbeddedEvent]s emitted by the embedded VM
 *   via the [LocalEmbeddedScreenContext] (see [EmbeddedScreenContext.emit]).
 * - [navigationMode] Delegate (default) forwards push() to the parent
 *   NavController but bounds pop/dismiss/navigateBack at the embed via
 *   [EmbeddedNavigationDelegate]. Isolated (>= 2.12.0, via the
 *   `isolatedNavigation:` overload) gives the embed a private stack
 *   ([EmbedNavigator]): push stays inside the embed, pop stops at the
 *   stack root, and a BackHandler routes system back to the deepest
 *   non-empty isolated stack.
 *
 * @param embedId Unique identifier for this embed slot within the parent
 *                layout. Maps to the `id` attribute on the Embed JSON node.
 *                Used as the key for the embed-local ViewModelStoreOwner.
 * @param params Init params handed to the embedded screen.
 * @param navigationMode How navigation from the embedded screen is wired.
 * @param eventBridge Receives events emitted by the embedded VM (or null).
 * @param modifier Compose modifier applied to the container.
 * @param content The embedded screen's composable. Receives an [EmbedScope]
 *                that exposes the embed-local [ViewModelStoreOwner].
 */
@Composable
fun EmbedContainer(
    embedId: String,
    params: Map<String, Any> = emptyMap(),
    navigationMode: EmbedNavigationMode = EmbedNavigationMode.Delegate,
    eventBridge: ((EmbeddedEvent) -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable (EmbedScope) -> Unit
) {
    EmbedContainerImpl(
        embedId = embedId,
        params = params,
        navigationMode = navigationMode,
        eventBridge = eventBridge,
        modifier = modifier,
        isolatedNavigation = null,
        destinationContent = null,
        content = content
    )
}

/**
 * Isolated-mode overload (KotlinJsonUI >= 2.12.0). Generated code for
 * `navigationMode:"isolated"` calls this overload and references
 * [EmbedIsolatedNavigation] deliberately, so that compiling against an
 * older library fails instead of silently degrading to delegate mode.
 *
 * @param isolatedNavigation How the embed obtains its private stack.
 * @param destinationContent Renders a pushed [EmbedStackEntry]. When null,
 *                           a pushed entry renders an explicit error box
 *                           (never a silent no-op) — library-dynamic passes
 *                           a DynamicView-backed resolver here.
 */
@Composable
fun EmbedContainer(
    embedId: String,
    params: Map<String, Any> = emptyMap(),
    navigationMode: EmbedNavigationMode,
    isolatedNavigation: EmbedIsolatedNavigation,
    destinationContent: (@Composable (EmbedStackEntry) -> Unit)? = null,
    eventBridge: ((EmbeddedEvent) -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable (EmbedScope) -> Unit
) {
    EmbedContainerImpl(
        embedId = embedId,
        params = params,
        navigationMode = navigationMode,
        eventBridge = eventBridge,
        modifier = modifier,
        isolatedNavigation = isolatedNavigation,
        destinationContent = destinationContent,
        content = content
    )
}

@Composable
private fun EmbedContainerImpl(
    embedId: String,
    params: Map<String, Any>,
    navigationMode: EmbedNavigationMode,
    eventBridge: ((EmbeddedEvent) -> Unit)?,
    modifier: Modifier,
    isolatedNavigation: EmbedIsolatedNavigation?,
    destinationContent: (@Composable (EmbedStackEntry) -> Unit)?,
    content: @Composable (EmbedScope) -> Unit
) {
    // Inherit the parent's default ViewModelProvider.Factory and extras so
    // `hiltViewModel(viewModelStoreOwner = embedScope.viewModelStoreOwner)`
    // engages HiltViewModelFactory for @HiltViewModel-annotated VMs.
    // `hiltViewModel()` only wraps the factory in HiltViewModelFactory when
    // `viewModelStoreOwner is HasDefaultViewModelProviderFactory`; without
    // that, Hilt VMs fall through to NewInstanceFactory and crash on a
    // missing no-arg ctor. The parent activity (ComponentActivity) already
    // implements HasDefaultViewModelProviderFactory; we forward its factory
    // through unchanged. Non-Hilt projects route through whatever default
    // their host already provides.
    val parentOwner = LocalViewModelStoreOwner.current
    val parentFactory: ViewModelProvider.Factory? =
        (parentOwner as? HasDefaultViewModelProviderFactory)?.defaultViewModelProviderFactory
    val parentExtras: CreationExtras =
        (parentOwner as? HasDefaultViewModelProviderFactory)?.defaultViewModelCreationExtras
            ?: CreationExtras.Empty

    // CRITICAL: a fresh ViewModelStoreOwner per embed slot.
    // Without this, multiple embeds of the same screen in the same parent
    // would share the same VM instance (Compose's `viewModel()` resolves by
    // class within the ambient LocalViewModelStoreOwner).
    val owner = remember(embedId, parentFactory) {
        EmbedViewModelStoreOwner(parentFactory, parentExtras)
    }
    DisposableEffect(embedId) {
        onDispose { owner.viewModelStore.clear() }
    }

    val navigationDelegate = remember(embedId, navigationMode) {
        EmbeddedNavigationDelegate(boundedAtEmbed = navigationMode == EmbedNavigationMode.Delegate)
    }

    // Isolated mode: the embed owns a private stack. `.Automatic` navigators
    // are rememberSaveable-backed so the stack survives configuration
    // changes; it resets when the embed leaves the composition (stack
    // lifetime == container identity, per the cross-platform contract).
    val navigator: EmbedNavigator? = when {
        navigationMode != EmbedNavigationMode.Isolated || isolatedNavigation == null -> null
        isolatedNavigation is EmbedIsolatedNavigation.Custom -> isolatedNavigation.navigator
        else -> rememberSaveable(embedId, saver = EmbedNavigator.StateSaver) { EmbedNavigator() }
    }

    if (navigator != null) {
        DisposableEffect(embedId, navigator) {
            EmbedNavigatorRegistry.register(embedId, navigator)
            onDispose { EmbedNavigatorRegistry.unregister(embedId, navigator) }
        }
    }

    val context = remember(embedId, params, eventBridge, navigationDelegate, navigator) {
        EmbeddedScreenContext(
            embedId = embedId,
            params = params,
            navigationDelegate = navigationDelegate,
            eventBridge = eventBridge,
            navigator = navigator
        )
    }

    val scope = remember(embedId, owner) {
        EmbedScope(embedId = embedId, viewModelStoreOwner = owner)
    }

    CompositionLocalProvider(
        LocalViewModelStoreOwner provides owner,
        LocalEmbeddedScreenContext provides context
    ) {
        // Apply the caller-supplied [modifier] to a Box wrapper so the value
        // actually reaches a Layout. Scope-bound modifiers (Row/Column
        // `.weight(n)`, Box `.align(...)`, etc.) are CREATED at the call site
        // inside the parent's scope but only take effect when attached to a
        // Layout node — without the Box, the modifier was silently dropped
        // and `Row { EmbedContainer(modifier = Modifier.weight(1f)) }`
        // emit-from-codegen had no layout effect.
        Box(modifier = modifier) {
            if (navigator == null) {
                content(scope)
            } else {
                IsolatedEmbedBody(
                    navigator = navigator,
                    destinationContent = destinationContent,
                    scope = scope,
                    content = content
                )
            }
        }
    }
}

/**
 * Body of an isolated embed: renders the top of the private stack (or the
 * embed root when the stack is empty) and registers a [BackHandler] that is
 * enabled only while the stack is non-empty. Compose resolves nested
 * BackHandlers innermost-first, so with nested isolated embeds the deepest
 * non-empty stack wins; when every isolated stack is empty, back is
 * delegated to the parent — exactly the cross-platform gesture contract.
 *
 * A [rememberSaveableStateHolder] keys each stack level so the embed root's
 * UI state survives push/pop round trips.
 */
@Composable
private fun IsolatedEmbedBody(
    navigator: EmbedNavigator,
    destinationContent: (@Composable (EmbedStackEntry) -> Unit)?,
    scope: EmbedScope,
    content: @Composable (EmbedScope) -> Unit
) {
    val stateHolder = rememberSaveableStateHolder()
    BackHandler(enabled = navigator.depth > 0) {
        navigator.pop()
    }
    val top = navigator.topEntry
    if (top == null) {
        stateHolder.SaveableStateProvider("jsonui-embed-root") {
            content(scope)
        }
    } else {
        stateHolder.SaveableStateProvider("jsonui-embed-${navigator.depth}-${top.screen}") {
            if (destinationContent != null) {
                destinationContent(top)
            } else {
                Text(
                    text = "Embed: no destinationContent for pushed screen `${top.screen}`",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

enum class EmbedNavigationMode {
    /** Embedded screen shares the parent's NavController; pop bounded at the embed. */
    Delegate,

    /**
     * Embedded screen has its own private navigation stack. Codegen must
     * call the `isolatedNavigation:` overload — this value alone does not
     * create a stack.
     */
    Isolated
}

/**
 * How an isolated embed obtains its private stack. This type is new in
 * KotlinJsonUI 2.12.0 — generated code for `navigationMode:"isolated"`
 * references it deliberately so that building against an older library
 * fails at compile time instead of silently degrading to delegate mode.
 */
sealed class EmbedIsolatedNavigation {
    /** The container creates and owns the navigator (codegen default). */
    object Automatic : EmbedIsolatedNavigation()

    /** The host supplies a navigator (e.g. to drive the stack from outside). */
    class Custom(val navigator: EmbedNavigator) : EmbedIsolatedNavigation()
}

/**
 * A pushable entry on an isolated embed's private stack: the target screen
 * (layout JSON name, snake_case) plus init params for it.
 */
data class EmbedStackEntry(
    val screen: String,
    val params: Map<String, Any> = emptyMap()
)

/**
 * The private navigation stack of an isolated embed. Owned by the
 * [EmbedContainer] (`Automatic`) or supplied by the host (`Custom`).
 *
 * The cross-platform contract (conformance-tested): [push] appends to THIS
 * stack, [pop] stops at the stack root (the embed itself never closes),
 * [depth] reports the number of pushed entries. Back handling: the
 * container registers a BackHandler enabled while `depth > 0`.
 *
 * Backed by Compose snapshot state, so reads inside composition subscribe
 * to changes automatically.
 */
class EmbedNavigator {
    private val stack = mutableStateListOf<EmbedStackEntry>()

    /** Number of entries pushed above the embed root. */
    val depth: Int get() = stack.size

    /** The entry currently on top, or null when at the embed root. */
    val topEntry: EmbedStackEntry? get() = stack.lastOrNull()

    /** Push a screen by layout JSON name (snake_case) with optional params. */
    fun push(screen: String, params: Map<String, Any> = emptyMap()) {
        stack.add(EmbedStackEntry(screen = screen, params = params))
    }

    /**
     * Pop one entry. Bounded at the embed stack's root: popping at depth 0
     * is a no-op — the embed never closes itself.
     */
    fun pop() {
        if (stack.isNotEmpty()) stack.removeAt(stack.lastIndex)
    }

    /** Pop everything back to the embed root. */
    fun popToRoot() {
        stack.clear()
    }

    companion object {
        /**
         * Config-change saver for `Automatic` navigators. Screen names and
         * [Serializable] param values survive; non-serializable params
         * (e.g. callback lambdas passed through nested params) are dropped
         * at save time — documented best-effort behavior.
         */
        val StateSaver: Saver<EmbedNavigator, Any> = listSaver(
            save = { navigator ->
                navigator.stack.map { entry ->
                    arrayListOf<Serializable>(
                        entry.screen,
                        HashMap(entry.params.filterValues { it is Serializable }
                            .mapValues { it.value as Serializable })
                    )
                }
            },
            restore = { saved ->
                EmbedNavigator().apply {
                    saved.forEach { raw ->
                        val parts = raw as ArrayList<*>
                        @Suppress("UNCHECKED_CAST")
                        push(
                            screen = parts[0] as String,
                            params = parts[1] as? Map<String, Any> ?: emptyMap()
                        )
                    }
                }
            }
        )
    }
}

/**
 * Scope handed to the embedded content. Surfaces the per-embed
 * [ViewModelStoreOwner] needed for `viewModel(viewModelStoreOwner = ...)`.
 */
class EmbedScope(
    val embedId: String,
    val viewModelStoreOwner: ViewModelStoreOwner
)

/**
 * Minimal [ViewModelStoreOwner] backed by a fresh [ViewModelStore].
 *
 * Also implements [HasDefaultViewModelProviderFactory] so that
 * `hiltViewModel(viewModelStoreOwner = ...)` can wrap the host's default
 * factory in a `HiltViewModelFactory` (it consults `is
 * HasDefaultViewModelProviderFactory` to decide). The factory comes from
 * the embed's parent (typically the ComponentActivity), so:
 * - In Hilt projects, the parent's factory is `HiltViewModelFactory` —
 *   forwarding it lets `@HiltViewModel` VMs resolve their `@Inject` ctor.
 * - In non-Hilt projects, the parent's factory is the plain
 *   AndroidViewModelFactory and `hiltViewModel()` falls back to the
 *   delegate, preserving the original behavior.
 *
 * The fresh [ViewModelStore] keeps embed-local VMs scoped to this slot
 * (so multiple Embeds of the same screen get distinct VM instances).
 */
internal class EmbedViewModelStoreOwner(
    parentFactory: ViewModelProvider.Factory?,
    parentExtras: CreationExtras
) : ViewModelStoreOwner, HasDefaultViewModelProviderFactory {
    private val store = ViewModelStore()
    override val viewModelStore: ViewModelStore get() = store

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory =
        parentFactory ?: ViewModelProvider.NewInstanceFactory()

    override val defaultViewModelCreationExtras: CreationExtras = parentExtras
}

// MARK: - EmbeddedEvent

/**
 * Event emitted by an embedded screen, surfaced to the parent via
 * `eventBridge`. `Named` is the generic envelope; named events follow the
 * `on[A-Z]...` convention.
 */
sealed class EmbeddedEvent {
    data class Named(val name: String, val payload: Map<String, Any>) : EmbeddedEvent()
}

// MARK: - EmbeddedScreenContext (CompositionLocal-propagated)

/**
 * Context propagated to the embedded screen via Compose. VMs read this to
 * access init params, emit events back to the parent, and consult the
 * navigation boundary.
 */
class EmbeddedScreenContext(
    val embedId: String,
    val params: Map<String, Any>,
    val navigationDelegate: EmbeddedNavigationDelegate,
    private val eventBridge: ((EmbeddedEvent) -> Unit)?,
    /**
     * Private stack of an isolated embed; null in delegate mode (and when
     * the screen is shown standalone). Embedded VMs route push/pop here
     * when present instead of the parent's NavController.
     */
    val navigator: EmbedNavigator? = null
) {
    /** Emit an event to the parent. No-op when not embedded. */
    fun emit(name: String, payload: Map<String, Any> = emptyMap()) {
        eventBridge?.invoke(EmbeddedEvent.Named(name = name, payload = payload))
    }
}

/**
 * Compose [androidx.compose.runtime.CompositionLocal] that exposes the
 * embed context to the embedded screen's composables. Null when the screen
 * is shown standalone (not embedded).
 */
val LocalEmbeddedScreenContext = compositionLocalOf<EmbeddedScreenContext?> { null }

// MARK: - InitParamsReceiver

/**
 * Optional interface: ViewModels that want to react to embed init params
 * implement this and override [applyInitParams]. The container calls it on
 * first composition AND whenever the bound params change.
 */
interface InitParamsReceiver {
    fun applyInitParams(params: Map<String, Any>)
}

/**
 * Helper composable: drives [InitParamsReceiver.applyInitParams] on the
 * given VM from the ambient embed context. Generated screen composables
 * call this once near the top of their body so init params reach the VM
 * reactively.
 *
 * No-op when [viewModel] doesn't implement [InitParamsReceiver] or when
 * the screen is shown standalone (context is null).
 */
@Composable
fun DriveEmbedInitParams(viewModel: Any) {
    val context = LocalEmbeddedScreenContext.current
    if (viewModel !is InitParamsReceiver) return
    LaunchedEffect(context?.embedId, context?.params) {
        val params = context?.params ?: return@LaunchedEffect
        if (params.isNotEmpty()) viewModel.applyInitParams(params)
    }
}

// MARK: - Navigation boundary

/**
 * Bounds pop/dismiss/navigateBack at the embed when navigationMode is
 * Delegate. The embedded VM/navigator should consult this before invoking
 * the parent's `navController.popBackStack()` etc.
 */
class EmbeddedNavigationDelegate(
    /** True when in delegate mode — pop is bounded at the embed. */
    val boundedAtEmbed: Boolean
) {
    /**
     * Returns true if the caller should silently swallow this pop.
     * @param internalPushDepth How many pushes deep the embed's internal
     *                          nav state is. 0 means "at the root of the
     *                          embed" — pop should be bounded.
     */
    fun shouldBoundPop(internalPushDepth: Int = 0): Boolean {
        return boundedAtEmbed && internalPushDepth == 0
    }
}
