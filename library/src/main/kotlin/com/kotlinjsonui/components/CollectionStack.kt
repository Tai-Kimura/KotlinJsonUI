package com.kotlinjsonui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Mode for [CollectionStack]. Mirrors `SwiftJsonUI.CollectionStackMode`.
 *
 * Why this exists: Compose's `LazyColumn` / `LazyRow` virtualize cell composition
 * as the viewport changes. With heavy cells (rich text, images) this can cause
 * stutter / re-composition cascades. `CollectionStack` lets layout JSON pick:
 *
 *   - LAZY  : LazyColumn / LazyRow (default, virtualized)
 *   - EAGER : Column(verticalScroll) / Row(horizontalScroll) (no virtualization,
 *             smooth for heavy cells)
 *   - NONE  : Column / Row only (parent already provides scrolling)
 */
enum class CollectionStackMode {
    LAZY,
    EAGER,
    NONE;

    companion object {
        fun fromJson(value: Any?): CollectionStackMode = when (value) {
            "lazy" -> LAZY
            "eager" -> EAGER
            "none", false -> NONE
            true, null -> LAZY
            else -> if (value is String) {
                runCatching { valueOf(value.uppercase()) }.getOrDefault(LAZY)
            } else LAZY
        }
    }
}

enum class CollectionStackAxis {
    VERTICAL,
    HORIZONTAL
}

/**
 * Stack-based collection container with lazy / eager / none modes.
 *
 * Compose's `LazyListScope` (used by `LazyColumn`/`LazyRow`) is incompatible
 * with the `@Composable` lambdas used by `Column`/`Row`, so callers must
 * supply both [lazyContent] and [eagerContent]. The mode-axis switch picks
 * the right closure at runtime; the cell-emission code lives in the caller's
 * generated view.
 *
 * For [CollectionStackMode.NONE] (no scroll container), [lazyContent] is
 * ignored.
 *
 * [eagerScrollState] is the EAGER container's scroll: a caller that scrolls
 * it (a Collection's scrollTo / defaultScrollAnchor, jsonui-cli 1.9.0) hands
 * its own; otherwise the container keeps one of its own, as before.
 *
 * [reverseLayout] (4f rulings 2026-09-27, round 13): a reversed list whose
 * content is shorter than the container sits at its bottom (the end, on a
 * row) — where iOS draws such a list, bottom-anchored — and the vertical
 * EAGER container draws reverseLayout as the lazy one does: its first child
 * at the bottom (ReversedColumn), resting at its bottom. Until then a short
 * reversed list sat at the top, and EAGER drew no reverseLayout.
 *
 * [userScrollEnabled] false stops the user's scrolling only: the EAGER
 * container keeps its scroll, so a programmatic scroll still moves it, as a
 * lazy list's does (round 13). It dropped the scroll until then.
 *
 * [contentAtBottom]: a vertical list whose content is shorter than the
 * container sits at its bottom, not reversed — a Collection's
 * defaultScrollAnchor bottom, where iOS draws such a list (4f ruling
 * 2026-09-27, round 14). The EAGER container pads its content with
 * [contentPadding] as the lazy one does (round 14; it applied none), and so
 * does the NONE one (round 15; it applied none either) — on a row, the
 * insets' spacers stand in for it when set, as on the other rows.
 *
 * [rowContentAlignment]: where a horizontal list whose content is shorter
 * than the container sits along its axis, on the LAZY and EAGER rows — a
 * Collection's defaultScrollAnchor center (its middle) or bottom (its end),
 * where iOS draws such a list (4f ruling 2026-09-27, round 15; it sat at the
 * start). Null keeps the start, the end under [reverseLayout]. The NONE row
 * does not scroll and takes none, as iOS's does not.
 */
@Composable
fun CollectionStack(
    mode: CollectionStackMode,
    axis: CollectionStackAxis = CollectionStackAxis.VERTICAL,
    modifier: Modifier = Modifier,
    spacing: Dp = 0.dp,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    // Top, not CenterVertically: undeclared gravity means the platform
    // default (top|start) — the dynamic renderer top-aligns horizontal
    // collections, and centering here was the parity deviation on every
    // horizontal Collection fixture. Callers wanting centering pass it.
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    userScrollEnabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    insetLeading: Dp = 0.dp,
    insetTrailing: Dp = 0.dp,
    reverseLayout: Boolean = false,
    lazyState: LazyListState? = null,
    eagerScrollState: ScrollState? = null,
    contentAtBottom: Boolean = false,
    rowContentAlignment: Alignment.Horizontal? = null,
    lazyContent: LazyListScope.() -> Unit = {},
    eagerContent: @Composable () -> Unit = {}
) {
    when {
        axis == CollectionStackAxis.VERTICAL && mode == CollectionStackMode.LAZY -> {
            val state = lazyState ?: rememberLazyListState()
            LazyColumn(
                modifier = modifier,
                state = state,
                contentPadding = contentPadding,
                reverseLayout = reverseLayout,
                verticalArrangement = when {
                    spacing > 0.dp -> Arrangement.spacedBy(spacing, if (reverseLayout || contentAtBottom) Alignment.Bottom else Alignment.Top)
                    reverseLayout || contentAtBottom -> Arrangement.Bottom
                    else -> Arrangement.Top
                },
                horizontalAlignment = horizontalAlignment,
                userScrollEnabled = userScrollEnabled,
                content = lazyContent
            )
        }
        axis == CollectionStackAxis.VERTICAL && mode == CollectionStackMode.EAGER -> {
            val scrollState = eagerScrollState ?: rememberScrollState()
            if (reverseLayout) {
                ReversedColumn(
                    modifier = modifier.verticalScroll(scrollState, enabled = userScrollEnabled, reverseScrolling = true)
                        .padding(contentPadding),
                    spacing = spacing,
                    horizontalAlignment = horizontalAlignment
                ) { eagerContent() }
            } else {
                Column(
                    modifier = modifier.verticalScroll(scrollState, enabled = userScrollEnabled).padding(contentPadding),
                    verticalArrangement = when {
                        spacing > 0.dp -> Arrangement.spacedBy(spacing, if (contentAtBottom) Alignment.Bottom else Alignment.Top)
                        contentAtBottom -> Arrangement.Bottom
                        else -> Arrangement.Top
                    },
                    horizontalAlignment = horizontalAlignment
                ) { eagerContent() }
            }
        }
        axis == CollectionStackAxis.VERTICAL && mode == CollectionStackMode.NONE -> {
            Column(
                modifier = modifier.padding(contentPadding),
                verticalArrangement = if (spacing > 0.dp) Arrangement.spacedBy(spacing) else Arrangement.Top,
                horizontalAlignment = horizontalAlignment
            ) { eagerContent() }
        }
        axis == CollectionStackAxis.HORIZONTAL && mode == CollectionStackMode.LAZY -> {
            val state = lazyState ?: rememberLazyListState()
            // contentInset spacers are emulated via leading/trailing PaddingValues
            // so that the LazyRow itself can still measure correctly.
            val resolvedPadding = if (insetLeading > 0.dp || insetTrailing > 0.dp) {
                PaddingValues(start = insetLeading, end = insetTrailing)
            } else {
                contentPadding
            }
            LazyRow(
                modifier = modifier,
                state = state,
                contentPadding = resolvedPadding,
                reverseLayout = reverseLayout,
                horizontalArrangement = when {
                    spacing > 0.dp -> Arrangement.spacedBy(spacing, rowContentAlignment ?: if (reverseLayout) Alignment.End else Alignment.Start)
                    rowContentAlignment != null -> Arrangement.aligned(rowContentAlignment)
                    reverseLayout -> Arrangement.End
                    else -> Arrangement.Start
                },
                verticalAlignment = verticalAlignment,
                userScrollEnabled = userScrollEnabled,
                content = lazyContent
            )
        }
        axis == CollectionStackAxis.HORIZONTAL && mode == CollectionStackMode.EAGER -> {
            val scrollState = eagerScrollState ?: rememberScrollState()
            Row(
                // contentPadding, as the LAZY row takes it: the insets' spacers stand in for it when set.
                modifier = modifier.horizontalScroll(scrollState, enabled = userScrollEnabled)
                    .then(if (insetLeading > 0.dp || insetTrailing > 0.dp) Modifier else Modifier.padding(contentPadding)),
                horizontalArrangement = when {
                    spacing > 0.dp && rowContentAlignment != null -> Arrangement.spacedBy(spacing, rowContentAlignment)
                    spacing > 0.dp -> Arrangement.spacedBy(spacing)
                    rowContentAlignment != null -> Arrangement.aligned(rowContentAlignment)
                    else -> Arrangement.Start
                },
                verticalAlignment = verticalAlignment
            ) {
                if (insetLeading > 0.dp) Spacer(Modifier.width(insetLeading))
                eagerContent()
                if (insetTrailing > 0.dp) Spacer(Modifier.width(insetTrailing))
            }
        }
        axis == CollectionStackAxis.HORIZONTAL && mode == CollectionStackMode.NONE -> {
            Row(
                // contentPadding, as the EAGER row takes it (round 15).
                modifier = modifier.then(if (insetLeading > 0.dp || insetTrailing > 0.dp) Modifier else Modifier.padding(contentPadding)),
                horizontalArrangement = if (spacing > 0.dp) Arrangement.spacedBy(spacing) else Arrangement.Start,
                verticalAlignment = verticalAlignment
            ) {
                if (insetLeading > 0.dp) Spacer(Modifier.width(insetLeading))
                eagerContent()
                if (insetTrailing > 0.dp) Spacer(Modifier.width(insetTrailing))
            }
        }
    }
}

/**
 * A Column that lays its children out from the bottom up — the first at the
 * bottom, as a reverseLayout LazyColumn draws its items — and, shorter than
 * its height, sits at its bottom. The vertical EAGER container under
 * reverseLayout (CollectionStack; KotlinJsonUI Dynamic's Column route), 4f
 * ruling 2026-09-27, round 13. A caller that emits its children in the order
 * a reversed lazy list's content emits them draws the same picture.
 */
@Composable
fun ReversedColumn(
    modifier: Modifier = Modifier,
    spacing: Dp = 0.dp,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable () -> Unit
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val gap = spacing.roundToPx()
        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
        val width = (placeables.maxOfOrNull { it.width } ?: 0).coerceIn(constraints.minWidth, constraints.maxWidth)
        val contentHeight = placeables.sumOf { it.height } + gap * (placeables.size - 1).coerceAtLeast(0)
        val height = contentHeight.coerceIn(constraints.minHeight, constraints.maxHeight)
        layout(width, height) {
            var bottom = height
            placeables.forEach { placeable ->
                placeable.place(horizontalAlignment.align(placeable.width, width, layoutDirection), bottom - placeable.height)
                bottom -= placeable.height + gap
            }
        }
    }
}
