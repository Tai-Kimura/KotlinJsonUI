package com.kotlinjsonui.components

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
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

    private val x1 = Density(1f)
    private val x2 = Density(2f)

    /** m x L: 1.8 x 24 = 43.2 (on a 1x grid, 43). */
    @Test
    fun aMultipleMultipliesL() {
        assertEquals(43f, LabelLineHeight.multiple(null, 1.8f, bodyLarge, x1).value, 0.001f)
        assertEquals(43.0f, LabelLineHeight.multiple(null, 1.8f, bodyLarge, x2).value, 0.001f)
    }

    /** L + spacing per line; lineSpacingBetween takes the last one back. */
    @Test
    fun aSpacingIsAddedToL() {
        assertEquals(40f, LabelLineHeight.spaced(null, 16f, bodyLarge, x2).value, 0.001f)
    }

    /**
     * 43.2sp at density 2 is 86.4 px. Compose rounds a line height UP (87 px,
     * 43.5), so five lines drifted 1.5 from 216. To the nearest pixel it is
     * 86 px, 43.0: a line is off by half a pixel at most.
     */
    @Test
    fun aLineHeightIsRoundedToTheNearestPixel() {
        for (d in listOf(1f, 2f, 2.625f, 3f, 3.5f)) {
            val density = Density(d)
            val px = LabelLineHeight.onPixelGrid(43.2f, density).value * d
            assertEquals("whole pixels at density $d", Math.round(px).toFloat(), px, 0.001f)
            assertEquals("within half a pixel at density $d", 43.2f * d, px, 0.5f)
        }
        // The font scale is part of sp -> px.
        val scaled = Density(2f, fontScale = 1.15f)
        val px = LabelLineHeight.onPixelGrid(43.2f, scaled).value * 2f * 1.15f
        assertEquals(Math.round(px).toFloat(), px, 0.001f)
    }
}
