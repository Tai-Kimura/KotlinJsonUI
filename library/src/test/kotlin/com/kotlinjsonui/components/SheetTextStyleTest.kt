package com.kotlinjsonui.components

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

/**
 * JVM pin for the Configuration sheet-typography merge contract: every
 * null override keeps the base field, and a fully-unset Configuration
 * returns the base style untouched (the sheet renders exactly as it did
 * before the overrides existed).
 */
class SheetTextStyleTest {

    private val base = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    )

    @Test
    fun allNullOverridesReturnBaseUnchanged() {
        assertSame(base, sheetTextStyle(base, null, null, null))
    }

    @Test
    fun familyOverrideKeepsSizeAndWeight() {
        val merged = sheetTextStyle(base, FontFamily.Monospace, null, null)
        assertEquals(FontFamily.Monospace, merged.fontFamily)
        assertEquals(16.sp, merged.fontSize)
        assertEquals(FontWeight.Normal, merged.fontWeight)
    }

    @Test
    fun sizeOverrideKeepsFamilyAndWeight() {
        val merged = sheetTextStyle(base, null, 22, null)
        assertEquals(FontFamily.Serif, merged.fontFamily)
        assertEquals(22.sp, merged.fontSize)
        assertEquals(FontWeight.Normal, merged.fontWeight)
    }

    @Test
    fun weightOverrideKeepsFamilyAndSize() {
        val merged = sheetTextStyle(base, null, null, FontWeight.Bold)
        assertEquals(FontFamily.Serif, merged.fontFamily)
        assertEquals(16.sp, merged.fontSize)
        assertEquals(FontWeight.Bold, merged.fontWeight)
    }

    @Test
    fun allOverridesApplyTogether() {
        val merged = sheetTextStyle(base, FontFamily.SansSerif, 20, FontWeight.SemiBold)
        assertEquals(FontFamily.SansSerif, merged.fontFamily)
        assertEquals(20.sp, merged.fontSize)
        assertEquals(FontWeight.SemiBold, merged.fontWeight)
    }
}
