package com.kotlinjsonui.dynamic

import androidx.compose.ui.text.font.FontWeight
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Numeric `fontWeight` — the css column of `font_weight_mapping.json`,
 * inverted, and nothing more. The SSoT declares the attribute string|number on
 * all three platforms; run 6 measured ios drawing 600 while android and web
 * dropped it, and 49-E routed the fix through the shared table so the
 * implementations cannot diverge (`600` IS semibold — not an eleventh
 * vocabulary).
 */
class NumericFontWeightTest {

    @Test
    fun theCssColumnInverted() {
        // Value for value from shared/core/font_weight_mapping.json.
        val expected = mapOf(
            100 to FontWeight.Thin,
            200 to FontWeight.ExtraLight,
            300 to FontWeight.Light,
            400 to FontWeight.Normal,   // css "normal", numeric form
            500 to FontWeight.Medium,
            600 to FontWeight.SemiBold,
            700 to FontWeight.Bold,     // css "bold", numeric form
            900 to FontWeight.Black
        )
        assertEquals(expected, ResourceResolver.NUMERIC_WEIGHTS)
    }

    @Test
    fun aNumberOutsideTheTableIsUnknownNotInvented() {
        // The table has no 800 — unknown falls back at the caller (regular),
        // exactly as the mapping's own comment prescribes for unknown names.
        assertNull(ResourceResolver.fontWeightOf(800))
        assertNull(ResourceResolver.fontWeightFor("800"))
        assertNull(ResourceResolver.fontWeightOf(650))
    }

    @Test
    fun bothDeclaredShapesResolve() {
        // string|number: the typed rows hand either shape through.
        assertEquals(FontWeight.SemiBold, ResourceResolver.fontWeightOf(600))
        assertEquals(FontWeight.SemiBold, ResourceResolver.fontWeightOf(600.0))
        assertEquals(FontWeight.SemiBold, ResourceResolver.fontWeightOf("600"))
        assertEquals(FontWeight.SemiBold, ResourceResolver.fontWeightOf("semibold"))
        assertEquals(FontWeight.Bold, ResourceResolver.fontWeightOf("bold"))
        assertNull(ResourceResolver.fontWeightOf(null))
    }

    @Test
    fun theNamePathIsUntouched() {
        // The regression the reverter would cause: names still resolve first.
        assertEquals(FontWeight.Bold, ResourceResolver.fontWeightFor("Bold"))
        assertEquals(FontWeight.Normal, ResourceResolver.fontWeightFor("regular"))
        assertNull("a family name is not a weight", ResourceResolver.fontWeightFor("serif"))
    }

    @Test
    fun aGsonDoubleNeverBecomesTheSpellingNoTableHas() {
        // gson parses JSON 600 as Double; stringifying it minted "600.0" and
        // the Label path stayed dropped even after the table existed.
        assertEquals(FontWeight.SemiBold, ResourceResolver.fontWeightOf(600.0))
        assertNull(ResourceResolver.fontWeightFor("600.0"))
    }
}
