package com.kotlinjsonui.embed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

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
 * v1 contracts:
 * - [params] is a flat Map<String, Any>. VMs that implement
 *   [InitParamsReceiver] receive them on first composition AND on every
 *   change. Other VMs silently ignore.
 * - [eventBridge] receives [EmbeddedEvent]s emitted by the embedded VM
 *   via the [LocalEmbeddedScreenContext] (see [EmbeddedScreenContext.emit]).
 * - [navigationMode] is currently always Delegate (Isolated is v1.5).
 *   Delegate mode forwards push() to the parent NavController but bounds
 *   pop/dismiss/navigateBack at the embed via [EmbeddedNavigationDelegate].
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
    // CRITICAL: a fresh ViewModelStoreOwner per embed slot.
    // Without this, multiple embeds of the same screen in the same parent
    // would share the same VM instance (Compose's `viewModel()` resolves by
    // class within the ambient LocalViewModelStoreOwner).
    val owner = remember(embedId) { EmbedViewModelStoreOwner() }
    DisposableEffect(embedId) {
        onDispose { owner.viewModelStore.clear() }
    }

    val navigationDelegate = remember(embedId, navigationMode) {
        EmbeddedNavigationDelegate(boundedAtEmbed = navigationMode == EmbedNavigationMode.Delegate)
    }

    val context = remember(embedId, params, eventBridge, navigationDelegate) {
        EmbeddedScreenContext(
            embedId = embedId,
            params = params,
            navigationDelegate = navigationDelegate,
            eventBridge = eventBridge
        )
    }

    val scope = remember(embedId, owner) {
        EmbedScope(embedId = embedId, viewModelStoreOwner = owner)
    }

    CompositionLocalProvider(
        LocalViewModelStoreOwner provides owner,
        LocalEmbeddedScreenContext provides context
    ) {
        content(scope)
    }
}

enum class EmbedNavigationMode {
    /** Embedded screen shares the parent's NavController; pop bounded at the embed. */
    Delegate,

    /** (v1.5) Embedded screen has its own private navigation stack. */
    Isolated
}

/**
 * Scope handed to the embedded content. Surfaces the per-embed
 * [ViewModelStoreOwner] needed for `viewModel(viewModelStoreOwner = ...)`.
 */
class EmbedScope(
    val embedId: String,
    val viewModelStoreOwner: ViewModelStoreOwner
)

/** Minimal [ViewModelStoreOwner] backed by a fresh [ViewModelStore]. */
private class EmbedViewModelStoreOwner : ViewModelStoreOwner {
    private val store = ViewModelStore()
    override val viewModelStore: ViewModelStore get() = store
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
    private val eventBridge: ((EmbeddedEvent) -> Unit)?
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
