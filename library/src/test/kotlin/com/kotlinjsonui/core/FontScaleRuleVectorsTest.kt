package com.kotlinjsonui.core

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The text size rule against jsonui-cli's shared/core/font_scale_vectors.json (copied to src/test/resources, held to
 * its source at the pinned ref by CI's vendored-fixtures step), and FontScaling.apply's three branches in a density.
 */
class FontScaleRuleVectorsTest {
    private val doc: JsonObject = JsonParser.parseReader(
        requireNotNull(javaClass.classLoader?.getResourceAsStream("font_scale_vectors.json")).reader(),
    ).asJsonObject

    @After fun restore() { Configuration.Font.scaling = true; Configuration.Font.maxScale = null }

    @Test fun every_drawn_case_is_the_rule() {
        val cases = doc.getAsJsonArray("drawn").map { it.asJsonObject }
        assertEquals(13, cases.size)
        for (c in cases) {
            val declared = c.get("declared")?.asBoolean
            val appDefault = c.get("appDefault")?.asBoolean ?: true
            val drawn = FontScaling.drawn(
                c.get("size").asDouble, c.get("scale").asDouble, declared ?: appDefault, c.get("max")?.asDouble,
            )
            assertEquals(c.get("name").asString, c.get("drawn").asDouble, drawn, 1e-9)
        }
    }

    /** A density at [fontScale]; its sp -> dp is Compose's own (non-linear for large sizes, as on Android 14+). */
    private fun density(fontScale: Float) = object : Density {
        override val density = 2f
        override val fontScale = fontScale
    }

    private fun dp(d: Density, size: TextUnit): Float = with(d) { size.toDp().value }

    /** What the system draws a following [sp] at, in dp. */
    private fun system(d: Density, sp: Int): Float = with(d) { sp.sp.toDp().value }

    @Test fun not_following_is_the_declared_size_in_dp_at_any_text_size() {
        for (scale in listOf(1f, 1.3f, 2f)) {
            assertEquals(13f, dp(density(scale), FontScaling.apply(density(scale), 13.sp, fontScaling = false, maxFontScale = null)), 1e-3f)
        }
    }

    @Test fun following_is_the_systems_and_a_cap_holds_it() {
        val d = density(2f)
        val grown = system(d, 15)
        assertTrue("the system grows 15 sp at 2.0: $grown", grown > 20f)
        assertEquals(grown, dp(d, FontScaling.apply(d, 15.sp, true, null)), 1e-3f)
        assertEquals(minOf(grown, 22.5f), dp(d, FontScaling.apply(d, 15.sp, true, 1.5f)), 1e-3f)
        // A cap above what the system does changes nothing.
        assertEquals(grown, dp(d, FontScaling.apply(d, 15.sp, true, 2.5f)), 1e-3f)
        // A cap of 1 never grows.
        assertEquals(15f, dp(d, FontScaling.apply(d, 15.sp, true, 1f)), 1e-3f)
    }

    @Test fun the_app_defaults_apply_where_the_component_declares_nothing_and_a_declaration_wins() {
        val d = density(2f)
        val grown = system(d, 17)
        Configuration.Font.scaling = false
        assertEquals(17f, dp(d, FontScaling.apply(d, 17.sp, null, null)), 1e-3f)
        assertEquals(grown, dp(d, FontScaling.apply(d, 17.sp, true, null)), 1e-3f)
        Configuration.Font.scaling = true
        Configuration.Font.maxScale = 1.5f
        assertEquals(minOf(grown, 25.5f), dp(d, FontScaling.apply(d, 17.sp, null, null)), 1e-3f)
        assertEquals(minOf(grown, 34f), dp(d, FontScaling.apply(d, 17.sp, null, 2f)), 1e-3f)
        assertEquals(17f, dp(d, FontScaling.apply(d, 17.sp, false, null)), 1e-3f)
    }

    @Test fun an_unspecified_size_stays_unspecified() {
        assertTrue(FontScaling.apply(density(2f), TextUnit.Unspecified, false, 1.5f) == TextUnit.Unspecified)
    }
}
