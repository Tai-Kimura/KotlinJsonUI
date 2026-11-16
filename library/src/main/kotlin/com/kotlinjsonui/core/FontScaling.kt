package com.kotlinjsonui.core

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp

/**
 * The size a component's text is drawn at (ticket sjui-label-font-size-ignores-dynamic-type; the rule is jsonui-cli
 * shared/core/font_scale_vectors.json, which FontScaleRuleVectorsTest holds this to):
 *  - a text that does not follow the system text size is drawn at its declared fontSize in dp, whatever the setting
 *    (`size.dp.toSp()`: the sp that converts back to exactly that many dp);
 *  - a following text is `.sp`, which the system grows — on Android 14+ non-linearly, large sizes less;
 *  - with a cap, at most `fontSize × maxFontScale` dp: `min(size.sp.toDp(), (size × max).dp).toSp()`, the cap taken
 *    after the system's own conversion.
 * The component's `fontScaling` / `maxFontScale` decide; absent, [Configuration.Font.scaling] /
 * [Configuration.Font.maxScale]. partialAttributes' sizes follow their component's declaration.
 */
object FontScaling {
    /** The rule in dp, for a [scale] the system applies to this size (FontScaleRuleVectorsTest). */
    fun drawn(size: Double, scale: Double, follows: Boolean, max: Double?): Double = when {
        !follows -> size
        max == null -> size * scale
        else -> minOf(size * scale, size * max)
    }

    /** [size] (sp, as declared) drawn by the rule in [density]. An unspecified size stays unspecified. */
    /**
     * [line] (an sp line height) as the line box the rule gives, in em of [drawnFont] (the size the text is drawn at,
     * from [apply]): unchanged when the text follows with no cap (see jsonUILineHeight for why em).
     */
    fun lineHeight(density: Density, line: TextUnit, drawnFont: TextUnit, fontScaling: Boolean?, maxFontScale: Float?): TextUnit {
        if (!line.isSpecified || !line.isSp) return line
        val follows = fontScaling ?: Configuration.Font.scaling
        val max = maxFontScale ?: Configuration.Font.maxScale
        if (follows && max == null) return line
        if (!drawnFont.isSpecified || !drawnFont.isSp || drawnFont.value <= 0f) return apply(density, line, fontScaling, maxFontScale)
        return with(density) {
            val box = if (!follows) line.value.dp else minOf(line.toDp(), (line.value * max!!).dp)
            (box.value / drawnFont.toDp().value).em
        }
    }

    fun apply(density: Density, size: TextUnit, fontScaling: Boolean?, maxFontScale: Float?): TextUnit {
        // Unspecified stays unspecified; an em size is relative to the font size, which the rule has drawn already.
        if (!size.isSpecified || !size.isSp) return size
        val follows = fontScaling ?: Configuration.Font.scaling
        val max = maxFontScale ?: Configuration.Font.maxScale
        val value = size.value
        return with(density) {
            when {
                !follows -> value.dp.toSp()
                max == null -> size
                else -> minOf(value.sp.toDp(), (value * max).dp).toSp()
            }
        }
    }
}

/**
 * A component's font size as the rule draws it (FontScaling): [size] is the declared fontSize as sp, [fontScaling] /
 * [maxFontScale] the component's declarations (null: the app's defaults in Configuration.Font). The generated code
 * and the library's components pass every text size through this.
 *
 * A component with no fontSize that declares fontScaling or maxFontScale has the rule applied to the size it draws
 * without one — the current text style's (a Label's Material3 bodyLarge, 16 sp unless the app's theme says
 * otherwise; a TextField / TextView pass Configuration.TextField.defaultFontSize, so theirs is specified already):
 * shared/core/font_scale_vectors.json noFontSize. Declaring nothing, an unspecified size stays the theme's.
 */
@Composable
@ReadOnlyComposable
fun jsonUIFontSize(size: TextUnit, fontScaling: Boolean? = null, maxFontScale: Float? = null): TextUnit {
    val declared = fontScaling != null || maxFontScale != null
    val base = if (!size.isSpecified && declared) LocalTextStyle.current.fontSize else size
    return FontScaling.apply(LocalDensity.current, base, fontScaling, maxFontScale)
}

/**
 * A component's letter spacing by the same rule as its size: the text style's letterSpacing (Material's is in sp,
 * which the system grows with the font scale) drawn as the rule draws a size — the declared spacing in dp when the
 * text does not follow, at most spacing × maxFontScale when capped — so a text that does not follow keeps its look
 * at every text size, its spacing included (ruling 2026-11-14). An em spacing follows the drawn size by itself.
 * Every Text whose size goes through [jsonUIFontSize] takes this as its letterSpacing.
 */
@Composable
@ReadOnlyComposable
fun jsonUILetterSpacing(fontScaling: Boolean? = null, maxFontScale: Float? = null): TextUnit =
    FontScaling.apply(LocalDensity.current, LocalTextStyle.current.letterSpacing, fontScaling, maxFontScale)

/**
 * A component's line height by the same rule as its size (ticket kjui-theme-line-height-scales-under-font-scaling-false;
 * iOS and web keep it so): the text style's lineHeight (Material's is sp) as the line box the rule gives — the same
 * as at 1.0 when the text does not follow, at most lineHeight x maxFontScale when capped. A text that follows keeps the
 * style's line height unchanged.
 *
 * [lineHeight]: a line height of the text's own (sp, as at 1.0) in place of the style's — a declared size's line, a
 * lineHeightMultiple's — drawn by the same rule; drawing it through [jsonUIFontSize] was off under a cap for the same
 * reason (a Label of 17 capped at 1.3 drew a line 1.49 x).
 *
 * Returned in em of [fontSize], the size the text is drawn at (what [jsonUIFontSize] gave it; unspecified: the text
 * style's, by the same declarations): Compose draws an sp line height scaled by the ratio its font size was scaled by,
 * not through the font scale's own table, so a line box in sp cannot be pinned (measured: a capped 31.2dp asked as
 * 18.6sp was drawn 37.3dp at 2.0). Relative to the drawn font, it is the line box asked for at every scale.
 */
@Composable
@ReadOnlyComposable
fun jsonUILineHeight(
    fontSize: TextUnit = TextUnit.Unspecified,
    fontScaling: Boolean? = null,
    maxFontScale: Float? = null,
    lineHeight: TextUnit = TextUnit.Unspecified
): TextUnit {
    val style = LocalTextStyle.current
    // A line height of the text's own (a declared size x 1.3, lineHeightMultiple) or, unspecified, the style's.
    val line = if (lineHeight.isSpecified) lineHeight else style.lineHeight
    if (!line.isSpecified || !line.isSp) return line
    val follows = fontScaling ?: Configuration.Font.scaling
    val max = maxFontScale ?: Configuration.Font.maxScale
    if (follows && max == null) return line
    val density = LocalDensity.current
    val font = if (fontSize.isSpecified) fontSize else FontScaling.apply(density, style.fontSize, fontScaling, maxFontScale)
    return FontScaling.lineHeight(density, line, font, fontScaling, maxFontScale)
}
