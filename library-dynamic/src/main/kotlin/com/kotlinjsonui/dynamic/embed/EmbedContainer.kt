package com.kotlinjsonui.dynamic.embed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
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
 * v1 (P1): delegate navigation only. params/events bridging is added in P2.
 * isolated navigation mode is added in P3.
 *
 * See jsonui-cli/docs/plans/2026-05-11-embed-feature.md.
 *
 * @param embedId Unique identifier for this embed slot within the parent layout.
 *                Maps to the `id` attribute on the Embed JSON node. Used as the
 *                key for the embed-local ViewModelStoreOwner.
 * @param navigationMode How navigation calls from the embedded screen are wired.
 * @param modifier Compose modifier applied to the container.
 * @param content The embedded screen's composable. Receives an [EmbedScope]
 *                that exposes the embed-local [ViewModelStoreOwner] for the
 *                embedded view's `viewModel(viewModelStoreOwner = ...)` call.
 */
@Composable
fun EmbedContainer(
    embedId: String,
    navigationMode: EmbedNavigationMode = EmbedNavigationMode.Delegate,
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

    val scope = remember(embedId, owner) {
        EmbedScope(embedId = embedId, viewModelStoreOwner = owner)
    }

    CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
        content(scope)
    }
}

enum class EmbedNavigationMode {
    /** Embedded screen shares the parent's NavController. */
    Delegate,

    /** Embedded screen has its own internal navigation stack. (Wired in P3.) */
    Isolated
}

/**
 * Scope handed to the embedded content. v1 surfaces only the
 * [ViewModelStoreOwner] for VM isolation; navigation hooks land in P3.
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
