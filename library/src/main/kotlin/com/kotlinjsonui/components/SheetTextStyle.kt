package com.kotlinjsonui.components

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Merge Configuration sheet-typography overrides into a MaterialTheme base
 * style. Every null override keeps the corresponding base field, so a
 * fully-unset Configuration returns `base` unchanged — the sheet renders
 * exactly as it did before the overrides existed.
 *
 * Kept as a pure function so the merge contract is JVM-testable without
 * composition.
 */
internal fun sheetTextStyle(
    base: TextStyle,
    fontFamily: FontFamily?,
    fontSize: Int?,
    fontWeight: FontWeight?
): TextStyle {
    if (fontFamily == null && fontSize == null && fontWeight == null) return base
    return base.copy(
        fontFamily = fontFamily ?: base.fontFamily,
        fontSize = fontSize?.sp ?: base.fontSize,
        fontWeight = fontWeight ?: base.fontWeight
    )
}

/**
 * A sheet's text style with its size drawn by the app's text size default (Configuration.Font.scaling / maxScale):
 * the sheet is the library's own, with no declaration of its own (FontScaling).
 */
@androidx.compose.runtime.Composable
@androidx.compose.runtime.ReadOnlyComposable
internal fun sheetFontScaled(style: TextStyle): TextStyle {
    val density = androidx.compose.ui.platform.LocalDensity.current
    val drawn = com.kotlinjsonui.core.jsonUIFontSize(style.fontSize)
    return style.copy(
        fontSize = drawn,
        letterSpacing = com.kotlinjsonui.core.jsonUILetterSpacing(),
        // The sheet style's own line height, by the same rule as its size (jsonUILineHeight reads LocalTextStyle).
        lineHeight = com.kotlinjsonui.core.FontScaling.lineHeight(density, style.lineHeight, drawn, null, null)
    )
}
