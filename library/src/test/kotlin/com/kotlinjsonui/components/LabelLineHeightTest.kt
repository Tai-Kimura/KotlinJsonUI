package com.kotlinjsonui.components

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * attribute_semantics lineHeightMultipleBase / lineSpacingBetween
 * (2026-10-05 user rulings): L is one line of a Label that declares no
 * lineHeight — the declared fontSize x 1.3, or the theme's line height.
 */
class LabelLineHeightTest {

    private val bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp)

    @Test
    fun withoutAFontSizeLIsTheThemeLine() {
        assertEquals(24f, LabelLineHeight.base(null, bodyLarge), 0.001f)
    }

    @Test
    fun aDeclaredFontSizeGivesItsOwnUndeclaredLine() {
        assertEquals(15.6f, LabelLineHeight.base(12f, bodyLarge), 0.001f)
    }

    @Test
    fun aThemeWithoutALineHeightFallsBackToItsFontSize() {
        assertEquals(20.8f, LabelLineHeight.base(null, TextStyle(fontSize = 16.sp)), 0.001f)
        assertEquals(18.2f, LabelLineHeight.base(null, TextStyle(lineHeight = TextUnit.Unspecified)), 0.001f)
    }

    /** Three lines at 1.8 with no fontSize: 3 x 1.8 x 24 = 129.6. */
    @Test
    fun aMultipleMultipliesL() {
        assertEquals(43.2f, LabelLineHeight.multiple(null, 1.8f, bodyLarge).value, 0.001f)
    }

    /** L + spacing per line; lineSpacingBetween takes the last one back. */
    @Test
    fun aSpacingIsAddedToL() {
        assertEquals(40f, LabelLineHeight.spaced(null, 16f, bodyLarge).value, 0.001f)
    }
}
