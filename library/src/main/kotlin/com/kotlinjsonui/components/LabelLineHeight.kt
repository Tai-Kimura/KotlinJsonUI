package com.kotlinjsonui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextStyle
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

    /** Every line m x L. */
    fun multiple(fontSize: Float?, multiple: Float, style: TextStyle): TextUnit =
        (base(fontSize, style) * multiple).sp

    /**
     * The line height that, with [lineSpacingBetween], gives L per line and
     * [spacing] between lines: L + spacing on every line, the extra spacing
     * after the last line taken away by the modifier.
     */
    fun spaced(fontSize: Float?, spacing: Float, style: TextStyle): TextUnit =
        (base(fontSize, style) + spacing).sp
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
