package com.kotlinjsonui.components

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusTargetModifierNode
import androidx.compose.ui.focus.Focusability
import androidx.compose.ui.focus.getFocusedRect
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.LayoutAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/**
 * `ScrollView.keyboardAvoidance` / `keyboardAvoidancePadding` on Android,
 * in one place for both renders of a layout (the codegen emits this, the
 * dynamic ScrollView calls it).
 *
 * Two halves. The VIEWPORT: `imePadding()` ends the scrollable at the IME,
 * and the clearance band ends it `clearanceDp` above that while the IME is
 * up — so a field that stops at the viewport's edge stops that far above
 * the keyboard (SSoT: number, default 20; iOS reads the same attribute).
 * The FOLLOW: once the IME is up, if the focused field's bottom is below
 * the viewport's bottom, the list is scrolled by the shortfall.
 *
 * ⚠️ WHY THE FOLLOW IS HERE AT ALL. Until 2.38.0 both renders relied on
 * Compose to do it: `Focusable` brings a field into view when it gains
 * focus, and the scrollable's `ContentInViewNode` re-tracks a focused child
 * when its viewport shrinks (b/216842427) — the IME shrinking it through
 * `imePadding` is exactly that. Measured 2026-09-22 on an API 35 emulator
 * with the reporting face's window (`adjustNothing` + edge-to-edge, a
 * `weight(1f)` LazyColumn beside a fixed footer, the field fully or partly
 * visible, cold or warm IME): the field ended `clearanceDp` above the
 * viewport's edge in every arm. The same day, on the reporting user's
 * device, both apps left the field under the keyboard and only the band
 * appeared. Which variable differs on that device is NOT identified. So
 * the follow no longer depends on the built-in tracking's preconditions
 * (it needs the child to have been fully visible before the shrink, and
 * stands down while its own animation runs); this one asks a single
 * question after the IME has moved — is the focused field below the
 * viewport now? — and scrolls if so.
 *
 * 📌 One direction. It never scrolls a field DOWN into view: the IME only
 * takes space from the bottom, and a field above the viewport is where the
 * user put it. It runs only while the IME is up, and only for a vertical
 * list — a horizontal ScrollView keeps the viewport half and skips the
 * follow, because scrolling a LazyRow by a vertical shortfall is nonsense.
 *
 * 📌 The focused rect comes from `FocusTargetModifierNode.getFocusedRect()`
 * on a never-focusable target placed INSIDE the padding, so its coordinate
 * space is the padded viewport and "below the bottom" is `rect.bottom >
 * height`. `Modifier.onFocusedBoundsChanged` would have been the older
 * route; it is deprecated in favour of exactly this query.
 */
@Composable
fun Modifier.keyboardAvoidance(
    listState: LazyListState,
    clearanceDp: Int = DEFAULT_KEYBOARD_CLEARANCE_DP,
): Modifier {
    val density = LocalDensity.current
    // Read here, not inside the effect: this is the state whose every change
    // (per IME animation frame) re-arms the follow.
    val imeBottomPx = WindowInsets.ime.getBottom(density)
    val probe = remember { FocusedRectProbe() }
    LaunchedEffect(imeBottomPx) {
        if (imeBottomPx <= 0) return@LaunchedEffect
        if (listState.layoutInfo.orientation != Orientation.Vertical) return@LaunchedEffect
        // This frame's layout (the band below, the shrunk viewport) has to
        // land before the rect is read against the viewport's height.
        withFrameNanos { }
        val shortfall = probe.shortfallBelowViewport() ?: return@LaunchedEffect
        if (shortfall > 0f) listState.scrollBy(shortfall)
    }
    return this
        .imePadding()
        .padding(bottom = if (imeBottomPx > 0) clearanceDp.dp else 0.dp)
        .then(probe.element)
}

/** `keyboardAvoidancePadding`'s SSoT default, spelled once for both renders. */
const val DEFAULT_KEYBOARD_CLEARANCE_DP = 20

/**
 * A never-focusable focus target whose only job is to answer "where is the
 * focused descendant, in my coordinates, and how far below my bottom edge
 * does it reach?" — the query the follow needs, through the API Compose
 * recommends for it.
 */
internal class FocusedRectProbe {
    private var node: FocusedRectNode? = null
    private var size: IntSize by mutableStateOf(IntSize.Zero)

    val element: Modifier = FocusedRectElement(this)

    /** Focused descendant's bottom minus the viewport height, or null when nothing under this node has focus. */
    fun shortfallBelowViewport(): Float? {
        val rect: Rect = node?.focusedRect() ?: return null
        return rect.bottom - size.height
    }

    internal fun attach(n: FocusedRectNode) { node = n }
    internal fun detach(n: FocusedRectNode) { if (node === n) node = null }
    internal fun sized(s: IntSize) { size = s }
}

internal class FocusedRectNode(private val probe: FocusedRectProbe) : DelegatingNode(), LayoutAwareModifierNode {
    private val target = delegate(FocusTargetModifierNode(focusability = Focusability.Never))

    fun focusedRect(): Rect? = target.getFocusedRect()

    override fun onAttach() { probe.attach(this) }
    override fun onDetach() { probe.detach(this) }
    override fun onRemeasured(size: IntSize) { probe.sized(size) }
}

private data class FocusedRectElement(val probe: FocusedRectProbe) : ModifierNodeElement<FocusedRectNode>() {
    override fun create() = FocusedRectNode(probe)
    override fun update(node: FocusedRectNode) {}
}
