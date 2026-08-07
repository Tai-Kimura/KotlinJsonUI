package com.kotlinjsonui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp

/**
 * The OBJECT face of `underline` / `strikethrough` carries a `color` that
 * Compose's [androidx.compose.ui.text.style.TextDecoration] cannot express —
 * the native decoration always draws in the text colour. The ios face draws
 * the declared colour through SwiftUI's `.underline(_, color:)` /
 * `.strikethrough(_, color:)`; this is the android equivalent: the caller
 * captures the [TextLayoutResult] from `onTextLayout`, and the modifier draws
 * one rule per visual line in the declared colour, under the baseline for an
 * underline and across the line centre for a strikethrough.
 *
 * `lineStyle` Double/Thick stay undistinguished on purpose — the same gap
 * the ruling keeps open on every platform (coverage ledger, 51-E
 * textDecoration). When a custom colour is drawn the caller must SUPPRESS
 * the native decoration for that face, or the text-coloured native line
 * doubles the coloured one.
 */
class StyledLineState {
    var layout: TextLayoutResult? = null
}

fun Modifier.styledTextLines(
    state: StyledLineState,
    underlineColor: Color? = null,
    strikethroughColor: Color? = null
): Modifier {
    if (underlineColor == null && strikethroughColor == null) return this
    return drawWithContent {
        drawContent()
        val layout = state.layout ?: return@drawWithContent
        val stroke = 1.5.dp.toPx()
        for (line in 0 until layout.lineCount) {
            val left = layout.getLineLeft(line)
            val right = layout.getLineRight(line)
            if (underlineColor != null) {
                val y = layout.getLineBaseline(line) + stroke
                drawLine(underlineColor, Offset(left, y), Offset(right, y), stroke)
            }
            if (strikethroughColor != null) {
                val top = layout.getLineTop(line)
                val bottom = layout.getLineBottom(line)
                val y = (top + bottom) / 2f
                drawLine(strikethroughColor, Offset(left, y), Offset(right, y), stroke)
            }
        }
    }
}
