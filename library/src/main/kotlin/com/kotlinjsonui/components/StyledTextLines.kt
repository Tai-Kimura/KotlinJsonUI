package com.kotlinjsonui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp

/**
 * The OBJECT face of `underline` / `strikethrough` carries `color` and a
 * `lineStyle` (Single/Double/Thick) that Compose's
 * [androidx.compose.ui.text.style.TextDecoration] cannot express — the
 * native decoration always draws a single rule in the text colour. The
 * caller captures the [TextLayoutResult] from `onTextLayout`, and the
 * modifier draws per visual line: under the baseline for an underline,
 * across the line centre for a strikethrough; `Double` draws two thin
 * rules, `Thick` one heavier rule. When this modifier draws a face the
 * caller must SUPPRESS the native decoration for it, or the native line
 * doubles the drawn one.
 */
class StyledLineState {
    var layout: TextLayoutResult? = null
}

/** One drawn face: the resolved colour and the declared line style. */
data class StyledLine(
    val color: Color,
    val style: String = "single"
)

fun Modifier.styledTextLines(
    state: StyledLineState,
    underline: StyledLine? = null,
    strikethrough: StyledLine? = null
): Modifier {
    if (underline == null && strikethrough == null) return this
    return drawWithContent {
        drawContent()
        val layout = state.layout ?: return@drawWithContent
        fun strokeFor(style: String) = when (style.lowercase()) {
            "thick" -> 2.5.dp.toPx()
            "double" -> 1.2.dp.toPx()
            else -> 1.5.dp.toPx()
        }
        for (line in 0 until layout.lineCount) {
            val left = layout.getLineLeft(line)
            val right = layout.getLineRight(line)
            fun drawFace(face: StyledLine, y: Float) {
                val stroke = strokeFor(face.style)
                drawLine(face.color, Offset(left, y), Offset(right, y), stroke)
                if (face.style.equals("double", ignoreCase = true)) {
                    val y2 = y + stroke * 2f
                    drawLine(face.color, Offset(left, y2), Offset(right, y2), stroke)
                }
            }
            underline?.let { drawFace(it, layout.getLineBaseline(line) + strokeFor(it.style)) }
            strikethrough?.let {
                val top = layout.getLineTop(line)
                val bottom = layout.getLineBottom(line)
                drawFace(it, (top + bottom) / 2f)
            }
        }
    }
}

/** Back-compat colour-only entry points (2.21.0 shipped the named-colour pair). */
fun Modifier.styledTextLines(
    state: StyledLineState,
    underlineColor: Color? = null,
    strikethroughColor: Color? = null
): Modifier = styledTextLines(
    state,
    underline = underlineColor?.let { StyledLine(it) },
    strikethrough = strikethroughColor?.let { StyledLine(it) }
)
