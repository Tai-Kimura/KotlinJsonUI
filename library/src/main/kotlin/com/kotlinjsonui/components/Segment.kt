package com.kotlinjsonui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.platform.LocalDensity
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.jsonUITintOrNull

/**
 * A segmented control drawn as iOS draws it (the requester's ruling 2026-11-15, jsonui-cli ticket
 * jui-segment-android-draws-no-track, option (a)): a capsule track, and the selected segment a
 * capsule filled with tintColor inside it, 2dp in; each segment the same width, its label on one
 * line. It was Material's TabRow: no track, the selection an underline.
 *
 * - [containerColor]: the track (the layout's `background`); null is iOS's: the system's
 *   translucent grey, 118,118,128 at 0.12 (light) / 0.24 (dark).
 * - [indicatorColor]: the selected segment's fill (`tintColor`); null is the tint a container
 *   handed down, else iOS's: white (light) / 99,99,102 (dark).
 * - [contentColor] / [selectedContentColor]: the unselected / selected label (`fontColor` /
 *   `selectedFontColor`); null is iOS's: black (light) / white (dark).
 *
 * - [cornerRadius]: not read. The track and the selected fill are capsules whatever the layout
 *   declares, as iOS draws them (the requester's ruling 2026-11-16: a Segment does not read
 *   cornerRadius). Kept so code kjui generated before still compiles.
 *
 * The segments are [SegmentTab]s; their labels are iOS's: 13 dp at every font scale, Medium on the
 * selected segment and Regular on the others (an app's fontProvider is asked with that weight).
 */
@Composable
fun Segment(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color? = null,
    contentColor: Color? = null,
    selectedContentColor: Color? = null,
    indicatorColor: Color? = null,
    cornerRadius: Dp? = null,
    tabs: @Composable () -> Unit
) {
    val dark = isSystemInDarkTheme()
    val track = containerColor ?: Configuration.Segment.defaultBackgroundColor
        ?: Color(118, 118, 128).copy(alpha = if (dark) 0.24f else 0.12f)
    val fill = indicatorColor ?: jsonUITintOrNull() ?: Configuration.Segment.defaultSelectedBackgroundColor
        ?: if (dark) Color(99, 99, 102) else Color.White
    val label = contentColor ?: Configuration.Segment.defaultTextColor ?: if (dark) Color.White else Color.Black
    val selectedLabel = selectedContentColor ?: Configuration.Segment.defaultSelectedTextColor ?: label
    val position by animateFloatAsState(selectedTabIndex.toFloat(), tween(200), label = "segment")
    val count = remember { IntArray(1) }
    val inset = 2.dp
    val pill = RoundedCornerShape(percent = 50)

    CompositionLocalProvider(LocalSegmentLabels provides SegmentLabels(label, selectedLabel)) {
        Layout(
            content = tabs,
            modifier = modifier
                .alpha(if (enabled) 1f else 0.6f)
                .clip(pill)
                .background(track, pill)
                .drawBehind {
                    val n = count[0]
                    if (n <= 0 || selectedTabIndex !in 0 until n) return@drawBehind
                    val i = inset.toPx()
                    val w = (size.width - 2 * i) / n
                    val h = size.height - 2 * i
                    val r = h / 2
                    drawRoundRect(fill, Offset(i + w * position, i), Size(w, h), CornerRadius(r, r))
                },
        ) { measurables, constraints ->
            val n = measurables.size
            count[0] = n
            val i = inset.roundToPx()
            val width = if (constraints.hasBoundedWidth) constraints.maxWidth
                else measurables.maxOfOrNull { it.maxIntrinsicWidth(Constraints.Infinity) }?.let { it * n + 2 * i } ?: 0
            val segment = if (n > 0) (width - 2 * i) / n else 0
            val natural = (measurables.maxOfOrNull { it.minIntrinsicHeight(segment) } ?: 0) + 2 * i
            val height = maxOf(natural, 31.dp.roundToPx()).coerceIn(constraints.minHeight, constraints.maxHeight)
            val inner = (height - 2 * i).coerceAtLeast(0)
            val placeables = measurables.map { it.measure(Constraints.fixed(segment.coerceAtLeast(0), inner)) }
            layout(width, height) {
                placeables.forEachIndexed { index, p -> p.place(i + index * segment, i) }
            }
        }
    }
}

/**
 * One segment of a [Segment], in place of Material's Tab (the same parameters kjui writes): its
 * label in the middle of the segment, in the Segment's label colours. A selectable node with the
 * Tab role, as Material's Tab is: it says whether it is selected, a click selects it, and
 * [enabled] false reads disabled and takes no click.
 */
@Composable
fun SegmentTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
) {
    val labels = LocalSegmentLabels.current
    Box(
        modifier
            .selectable(selected = selected, enabled = enabled, role = Role.Tab, onClick = onClick)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        // iOS's label (UISegmentedControl's own font, measured on iOS 27.2 for jsonui-cli ticket
        // kjui-segment-label-font-larger-than-ios): 13 pt, Medium on the selected segment and Regular
        // on the others, the same 13 at every Dynamic Type size (small to AX5). So 13 dp here, the
        // same at every font scale (asked in sp as 13 dp is at this density), the line the font's own
        // and no letter spacing: Material's bodyLarge drew 16 sp Medium with a 24 sp line, and a
        // label iOS fits in a 156 wide Segment ran to "…".
        val density = LocalDensity.current
        CompositionLocalProvider(
            LocalContentColor provides if (selected) labels.selected else labels.normal,
            // copy, not merge: merge keeps the theme's line height when the other's is unspecified.
            LocalTextStyle provides LocalTextStyle.current.copy(
                fontSize = with(density) { LABEL_SIZE.toSp() },
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                lineHeight = TextUnit.Unspecified,
                letterSpacing = 0.sp,
            ),
        ) { text() }
    }
}

/** iOS's segment label size (UISegmentedControl, 13 pt at every Dynamic Type size). */
private val LABEL_SIZE = 13.dp

internal data class SegmentLabels(val normal: Color, val selected: Color)

internal val LocalSegmentLabels = staticCompositionLocalOf { SegmentLabels(Color.Black, Color.Black) }
