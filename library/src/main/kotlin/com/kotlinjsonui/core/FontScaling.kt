package com.kotlinjsonui.core

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
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
