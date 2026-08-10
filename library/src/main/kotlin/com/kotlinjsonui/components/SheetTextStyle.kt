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
