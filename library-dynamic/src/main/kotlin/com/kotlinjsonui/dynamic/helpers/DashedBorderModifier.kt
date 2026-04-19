package com.kotlinjsonui.dynamic.helpers

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

/**
 * Extension function to draw dashed or dotted borders
 *
 * @param width The border width
 * @param color The border color
 * @param shape The shape of the border (used to determine corner radius)
 * @param dashPattern The dash pattern as [dashLength, gapLength]
 * @param cap The stroke cap style (use StrokeCap.Round for dotted borders)
 */
fun Modifier.dashedBorder(
    width: Dp,
    color: Color,
    shape: Shape?,
    dashPattern: FloatArray,
    cap: androidx.compose.ui.graphics.StrokeCap = androidx.compose.ui.graphics.StrokeCap.Butt
): Modifier = this.drawWithContent {
    drawContent()

    val cornerRadius = when (shape) {
        is RoundedCornerShape -> {
            // Get the corner radius from the shape
            val topStart = shape.topStart
            val radiusPx = topStart.toPx(Size(size.width, size.height), this)
            CornerRadius(radiusPx, radiusPx)
        }
        else -> CornerRadius.Zero
    }

    drawRoundRect(
        color = color,
        style = Stroke(
            width = width.toPx(),
            pathEffect = PathEffect.dashPathEffect(dashPattern),
            cap = cap
        ),
        cornerRadius = cornerRadius
    )
}

/**
 * Convenience function for dashed border with default pattern [6, 3]
 */
fun Modifier.dashedBorder(
    width: Dp,
    color: Color,
    shape: Shape? = null
): Modifier = dashedBorder(width, color, shape, floatArrayOf(6f, 3f))

/**
 * Convenience function for dotted border
 * Uses small dash with gap and round cap for dot effect
 */
fun Modifier.dottedBorder(
    width: Dp,
    color: Color,
    shape: Shape? = null
): Modifier = dashedBorder(
    width = width,
    color = color,
    shape = shape,
    dashPattern = floatArrayOf(width.value, width.value * 2),
    cap = androidx.compose.ui.graphics.StrokeCap.Round
)
