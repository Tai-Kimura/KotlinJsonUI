package com.kotlinjsonui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * `contentInsetAdjustmentBehavior` on a Compose scrollable: the safe area the
 * scrollable's frame actually OVERLAPS, as its content inset.
 *
 * That is what UIKit's `.always` adds — a scroll view's `safeAreaInsets` are
 * the part of the window's safe area its own frame covers, so inside a
 * SafeAreaView, or below a header, it is 0. KotlinJsonUI added the WHOLE
 * window's inset (`WindowInsets.safeDrawing.asPaddingValues()`) wherever the
 * scrollable sat: inside a SafeAreaView that already reserved the status bar,
 * a Collection drew an empty band of the status bar's height above its first
 * row (jsonui-cli ticket kjui-collection-content-inset-always-adds-whole-
 * window-insets). Consumed insets alone would fix the SafeAreaView case but
 * not a scrollable below a header on a screen with no SafeAreaView, where iOS
 * adds 0 as well — the overlap is the definition, so the overlap is computed.
 *
 * Usage (codegen and Dynamic alike):
 * ```
 * val safeArea = rememberSafeAreaOverlap(WindowInsetsSides.Vertical)  // or no argument: every side
 * LazyColumn(modifier = modifier.then(safeArea.modifier), contentPadding = safeArea.contentPadding(own)) { … }
 * ```
 *
 * The padding is READ IN MEASURE, not in composition: [contentPadding] is a
 * PaddingValues whose sides read the scrollable's measured frame and the
 * insets when the scrollable measures. A move (rotation, a split window, a
 * parent that grows) or an inset change (the IME, a bar appearing)
 * re-measures the scrollable and nothing recomposes; and the padding is
 * INSIDE the scrollable, so changing it never moves the frame it is computed
 * from — one remeasure, no loop. Until the frame is first known the overlap
 * is 0.
 */
@Stable
class SafeAreaOverlap internal constructor(
    private val insets: WindowInsets,
    private val density: Density,
    private val windowSize: () -> IntSize,
) {
    /** The scrollable's frame in its window, and the window's size, as last placed. */
    internal var frame by mutableStateOf<Rect?>(null)
    internal var window by mutableStateOf(IntSize.Zero)

    internal fun place(frame: Rect, window: IntSize) {
        // Equal values write nothing, so a re-placement at the same frame
        // invalidates nothing.
        if (this.frame != frame) this.frame = frame
        if (this.window != window) this.window = window
    }

    /** Attach to the scrollable whose frame the overlap is measured from. */
    val modifier: Modifier = Modifier.onGloballyPositioned { coordinates ->
        // The WINDOW's size, which the insets are measured against — not the
        // composition's root, which a window that is not edge-to-edge places
        // below the status bar.
        place(coordinates.boundsInWindow(), windowSize())
    }

    /** The overlap on each side plus [own] (the scrollable's declared content padding). */
    fun contentPadding(own: PaddingValues = PaddingValues(0.dp)): PaddingValues = OverlapPadding(this, own)

    internal fun topPx(): Float {
        val f = frame ?: return 0f
        return overlap(insets.getTop(density).toFloat() - f.top, f.height)
    }

    internal fun bottomPx(): Float {
        val f = frame ?: return 0f
        val inset = insets.getBottom(density).toFloat()
        if (window.height == 0) return 0f
        return overlap(f.bottom - (window.height - inset), f.height, inset)
    }

    internal fun leftPx(direction: LayoutDirection): Float {
        val f = frame ?: return 0f
        return overlap(insets.getLeft(density, direction).toFloat() - f.left, f.width)
    }

    internal fun rightPx(direction: LayoutDirection): Float {
        val f = frame ?: return 0f
        val inset = insets.getRight(density, direction).toFloat()
        if (window.width == 0) return 0f
        return overlap(f.right - (window.width - inset), f.width, inset)
    }

    internal fun toDp(px: Float): Dp = with(density) { px.toDp() }

    private companion object {
        /** How far into the band the frame reaches: never negative, never past the band or the frame. */
        fun overlap(reach: Float, extent: Float, band: Float = Float.MAX_VALUE): Float =
            reach.coerceAtLeast(0f).coerceAtMost(minOf(extent, band))
    }
}

private class OverlapPadding(private val safe: SafeAreaOverlap, private val own: PaddingValues) : PaddingValues {
    override fun calculateTopPadding(): Dp = own.calculateTopPadding() + safe.toDp(safe.topPx())
    override fun calculateBottomPadding(): Dp = own.calculateBottomPadding() + safe.toDp(safe.bottomPx())
    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        own.calculateLeftPadding(layoutDirection) + safe.toDp(safe.leftPx(layoutDirection))
    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        own.calculateRightPadding(layoutDirection) + safe.toDp(safe.rightPx(layoutDirection))
}

/**
 * The safe-area overlap of the scrollable this is attached to. [sides] limits
 * it (`scrollableAxes`: the scrolled axis only); every side by default.
 */
@Composable
fun rememberSafeAreaOverlap(sides: WindowInsetsSides? = null): SafeAreaOverlap {
    // Keyed on the window's own safeDrawing (one object per window) and the
    // sides — `only(...)` builds a new object on every call, and a key that
    // changed on every composition would forget the frame each time.
    val safeDrawing = WindowInsets.safeDrawing
    val density = LocalDensity.current
    val view = LocalView.current
    return remember(safeDrawing, sides, density, view) {
        SafeAreaOverlap(if (sides == null) safeDrawing else safeDrawing.only(sides), density) {
            view.rootView.let { IntSize(it.width, it.height) }
        }
    }
}
