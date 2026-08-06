package com.kotlinjsonui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * `distribution: "fill"` — children grow FROM THEIR CONTENT to consume the
 * axis, leaving no free space (attribute_semantics.json#distribution).
 *
 * Compose's `Modifier.weight` cannot express this. `weight(1f)` is
 * fillEqually — equal shares regardless of content, a zero flex-basis. And
 * `weight(1f, fill = false)` merely lets a child stay content-sized inside its
 * share, after which the Row packs children by their measured size — which is
 * pixel-identical to declaring no distribution at all (measured: the fixture
 * went inert against its control). `fill` is CSS `flex-grow: 1` with an auto
 * basis: content size plus an equal split of the leftover, so a child with
 * more content ends up wider and nothing is left over. That needs its own
 * measurement pass, which is what this layout is.
 *
 * It lives in the base library so both renderers spell it identically: the
 * dynamic renderer calls it directly and the kjui codegen emits a call to it.
 * Two implementations of "grow" would drift the way the four distribution
 * values originally did.
 *
 * [grows] marks which children may grow — a child with an explicitly declared
 * size on this axis keeps it (the size topic's explicit > bounds > fill: the
 * size half of distribution is a fill instruction and sits at the bottom of
 * that order). When the list's size does not match the composed children
 * (e.g. a visibility-gone child composed nothing), every child grows.
 *
 * [gap] is the declared `spacing`: it pins the GAP and says nothing about
 * size, so growth happens in the space the gaps leave (spacingWins, 49-E).
 */
@Composable
fun DistributionFillRow(
    modifier: Modifier = Modifier,
    gap: Dp = 0.dp,
    grows: List<Boolean> = emptyList(),
    content: @Composable () -> Unit
) {
    Layout(content, modifier) { measurables, constraints ->
        val gapPx = gap.roundToPx()
        val g = if (grows.size == measurables.size) grows else List(measurables.size) { true }
        // Pass 1 is intrinsics, not a measure — Compose forbids measuring a
        // measurable twice, and the content width is exactly what an
        // unconstrained max-intrinsic answers.
        val intrinsics = measurables.map { it.maxIntrinsicWidth(constraints.maxHeight) }
        val widths =
            if (constraints.hasBoundedWidth) {
                growSizes(intrinsics, g, constraints.maxWidth, gapPx)
            } else {
                intrinsics // an unbounded axis has no leftover to distribute
            }
        val placeables = measurables.mapIndexed { i, m ->
            m.measure(
                Constraints(
                    minWidth = widths[i], maxWidth = widths[i],
                    minHeight = 0, maxHeight = constraints.maxHeight
                )
            )
        }
        val width =
            if (constraints.hasBoundedWidth) constraints.maxWidth
            else widths.sum() + gapPx * (placeables.size - 1).coerceAtLeast(0)
        val height = (placeables.maxOfOrNull { it.height } ?: 0)
            .coerceIn(constraints.minHeight, constraints.maxHeight)
        layout(width, height) {
            var x = 0
            placeables.forEach { p ->
                p.placeRelative(x, 0)
                x += p.width + gapPx
            }
        }
    }
}

/** The vertical spelling of [DistributionFillRow]. */
@Composable
fun DistributionFillColumn(
    modifier: Modifier = Modifier,
    gap: Dp = 0.dp,
    grows: List<Boolean> = emptyList(),
    content: @Composable () -> Unit
) {
    Layout(content, modifier) { measurables, constraints ->
        val gapPx = gap.roundToPx()
        val g = if (grows.size == measurables.size) grows else List(measurables.size) { true }
        val intrinsics = measurables.map { it.maxIntrinsicHeight(constraints.maxWidth) }
        val heights =
            if (constraints.hasBoundedHeight) {
                growSizes(intrinsics, g, constraints.maxHeight, gapPx)
            } else {
                intrinsics
            }
        val placeables = measurables.mapIndexed { i, m ->
            m.measure(
                Constraints(
                    minWidth = 0, maxWidth = constraints.maxWidth,
                    minHeight = heights[i], maxHeight = heights[i]
                )
            )
        }
        val height =
            if (constraints.hasBoundedHeight) constraints.maxHeight
            else heights.sum() + gapPx * (placeables.size - 1).coerceAtLeast(0)
        val width = (placeables.maxOfOrNull { it.width } ?: 0)
            .coerceIn(constraints.minWidth, constraints.maxWidth)
        layout(width, height) {
            var y = 0
            placeables.forEach { p ->
                p.placeRelative(0, y)
                y += p.height + gapPx
            }
        }
    }
}

/**
 * The grow arithmetic, kept pure so a test exercises the REAL policy: each
 * growable child gets its content size plus an equal integer share of the
 * leftover, with the remainder pixels going to the first children so the sum
 * lands exactly on the axis. No leftover (or no growable child) means content
 * sizes stand — fill never shrinks anyone.
 */
internal fun growSizes(
    intrinsics: List<Int>,
    grows: List<Boolean>,
    available: Int,
    gapPx: Int
): List<Int> {
    val n = intrinsics.size
    if (n == 0) return emptyList()
    val leftover = available - gapPx * (n - 1) - intrinsics.sum()
    val growers = (0 until n).count { grows.getOrElse(it) { true } }
    if (leftover <= 0 || growers == 0) return intrinsics
    val share = leftover / growers
    var remainder = leftover % growers
    return intrinsics.mapIndexed { i, w ->
        if (grows.getOrElse(i) { true }) w + share + (if (remainder-- > 0) 1 else 0) else w
    }
}
