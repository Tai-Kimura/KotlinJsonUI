package com.kotlinjsonui.core

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class FontSpecTest {

    @Before
    fun setUp() {
        Configuration.Font.fontProvider = null
    }

    @After
    fun tearDown() {
        Configuration.Font.fontProvider = null
    }

    @Test
    fun `FontSpec defaults are all null or false`() {
        val spec = FontSpec()
        assertNull(spec.family)
        assertNull(spec.weight)
        assertNull(spec.size)
        assertEquals(false, spec.italic)
    }

    @Test
    fun `ResolvedFont defaults are all null`() {
        val resolved = ResolvedFont()
        assertNull(resolved.family)
        assertNull(resolved.weight)
        assertNull(resolved.size)
        assertNull(resolved.style)
    }

    @Test
    fun `resolve with no provider falls back to defaultResolved`() {
        val spec = FontSpec(
            family = "Inter",
            weight = FontWeight.Bold,
            size = 16.sp,
            italic = false
        )
        val resolved = Configuration.Font.resolve(spec)

        // family is intentionally dropped when no provider is registered.
        assertNull(resolved.family)
        assertEquals(FontWeight.Bold, resolved.weight)
        assertEquals(16.sp, resolved.size)
        assertEquals(FontStyle.Normal, resolved.style)
    }

    @Test
    fun `resolve with provider returning ResolvedFont uses provider result`() {
        val customFamily = FontFamily.Monospace
        Configuration.Font.fontProvider = { spec ->
            assertEquals("Inter", spec.family)
            assertEquals(FontWeight.Medium, spec.weight)
            ResolvedFont(
                family = customFamily,
                weight = spec.weight,
                size = spec.size,
                style = FontStyle.Normal
            )
        }

        val resolved = Configuration.Font.resolve(
            FontSpec(family = "Inter", weight = FontWeight.Medium, size = 14.sp)
        )
        assertEquals(customFamily, resolved.family)
        assertEquals(FontWeight.Medium, resolved.weight)
        assertEquals(14.sp, resolved.size)
        assertEquals(FontStyle.Normal, resolved.style)
    }

    @Test
    fun `resolve with provider returning null falls back to defaultResolved`() {
        Configuration.Font.fontProvider = { null }

        val resolved = Configuration.Font.resolve(
            FontSpec(family = "Unknown", weight = FontWeight.Bold)
        )
        // Falls through to defaultResolved: family dropped, weight passed through.
        assertNull(resolved.family)
        assertEquals(FontWeight.Bold, resolved.weight)
        assertEquals(FontStyle.Normal, resolved.style)
    }

    @Test
    fun `defaultResolved with italic true produces FontStyle Italic`() {
        val resolved = Configuration.Font.defaultResolved(
            FontSpec(weight = FontWeight.Normal, italic = true)
        )
        assertEquals(FontStyle.Italic, resolved.style)
    }

    @Test
    fun `defaultResolved with italic false produces FontStyle Normal`() {
        val resolved = Configuration.Font.defaultResolved(
            FontSpec(weight = FontWeight.Normal, italic = false)
        )
        assertEquals(FontStyle.Normal, resolved.style)
    }

    @Test
    fun `resolve passes spec through to provider for italic flag`() {
        var capturedItalic: Boolean? = null
        Configuration.Font.fontProvider = { spec ->
            capturedItalic = spec.italic
            null
        }

        Configuration.Font.resolve(FontSpec(italic = true))
        assertEquals(true, capturedItalic)

        Configuration.Font.resolve(FontSpec(italic = false))
        assertEquals(false, capturedItalic)
    }

    @Test
    fun `resolve via provider supports italic to FontStyle Italic mapping`() {
        Configuration.Font.fontProvider = { spec ->
            ResolvedFont(
                style = if (spec.italic) FontStyle.Italic else FontStyle.Normal
            )
        }
        val italic = Configuration.Font.resolve(FontSpec(italic = true))
        assertEquals(FontStyle.Italic, italic.style)

        val normal = Configuration.Font.resolve(FontSpec(italic = false))
        assertEquals(FontStyle.Normal, normal.style)
    }
}
