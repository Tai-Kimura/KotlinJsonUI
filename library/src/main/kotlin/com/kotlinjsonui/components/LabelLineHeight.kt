package com.kotlinjsonui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * A Label's line height, as attribute_semantics lineHeightMultipleBase and
 * lineSpacingBetween declare it (2026-10-05 user rulings). The base L is one
 * line of a Label that declares no lineHeight: `fontSize * 1.3` when a font
 * size is declared, the theme's line height when it is not (Material3
 * bodyLarge: 24sp). `lineHeightMultiple` makes every line m x L;
 * `lineSpacing` is added between lines only (see [lineSpacingBetween]).
 *
 * Both used to start from the font size, with 14 when none was declared,
 * while an undeclared Label drew 16sp on a 24sp line: three lines at
 * lineHeightMultiple 1.8 measured 76.5 where L-based is 129.6.
 *
 * The dynamic face and the codegen emit both call this, so the two cannot
 * drift.
 */
object LabelLineHeight {
    /** The ratio an undeclared Label's line has to a declared font size. */
    const val DECLARED_FONT_SIZE_RATIO = 1.3f

    /**
     * L, in sp. When neither a font size nor the theme gives one (a theme
     * without a line height), it falls back to the theme's font size, or 14,
     * times [DECLARED_FONT_SIZE_RATIO].
     */
    fun base(fontSize: Float?, style: TextStyle): Float {
        if (fontSize != null) return fontSize * DECLARED_FONT_SIZE_RATIO
        if (style.lineHeight.isSpecified && style.lineHeight.isSp) return style.lineHeight.value
        val themeSize = if (style.fontSize.isSpecified && style.fontSize.isSp) style.fontSize.value else 14f
        return themeSize * DECLARED_FONT_SIZE_RATIO
    }

    /** Every line m x L, on the device's pixel grid ([onPixelGrid]). */
    fun multiple(fontSize: Float?, multiple: Float, style: TextStyle, density: Density): TextUnit =
        onPixelGrid(base(fontSize, style) * multiple, density)

    /**
     * The line height that, with [lineSpacingBetween], gives L per line and
     * [spacing] between lines: L + spacing on every line, the extra spacing
     * after the last line taken away by the modifier.
     */
    fun spaced(fontSize: Float?, spacing: Float, style: TextStyle, density: Density): TextUnit =
        onPixelGrid(base(fontSize, style) + spacing, density)

    /**
     * [sp] rounded to the nearest whole pixel. Compose draws a line height
     * a whole number of pixels tall, rounding UP: 43.2sp at density 2 is
     * 86.4 px and drew 87, so five lines measured 217.5 against the declared
     * 216 and the error grew by 0.3 a line. Rounded to the nearest pixel
     * first, a line is off by half a pixel at most (0.25 at density 2).
     */
    fun onPixelGrid(sp: Float, density: Density): TextUnit {
        val pxPerSp = density.density * density.fontScale
        return (kotlin.math.round(sp * pxPerSp) / pxPerSp).sp
    }
}

/**
 * Takes [spacing] (sp) back from a Text whose line height is L + spacing, so
 * the spacing sits between lines only: the text is measured `spacing`
 * taller, reported `spacing` shorter, and placed `spacing / 2` up. Each line
 * box centres its glyphs, so the first line's glyphs land where an
 * unspaced line's do, the lines are L + spacing apart, and n lines measure
 * n x L + (n - 1) x spacing. Put it innermost (after background and
 * padding), so those see the reported size.
 */
fun Modifier.lineSpacingBetween(spacing: Float): Modifier {
    if (spacing == 0f) return this
    return layout { measurable, constraints ->
        val px = spacing.sp.toPx().roundToInt()
        val placeable = measurable.measure(constraints.offset(vertical = px))
        val height = (placeable.height - px).coerceIn(constraints.minHeight, constraints.maxHeight)
        layout(placeable.width, height) {
            placeable.place(0, -px / 2)
        }
    }
}
