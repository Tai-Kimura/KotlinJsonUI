package com.kotlinjsonui.dynamic.components

import androidx.compose.ui.graphics.Color
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.RadioAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * A Radio draws its declared fontSize / fontColor (and font) on every label
 * it draws — the item's, the items group's header and items, each option —
 * and its button colours the same way on every path, `tintColor` being the
 * selected button's accent (4f ruling, 2026-09-26: the accent of what can be
 * operated, not a text colour).
 *
 * Colours go through bindings to a Color: hex parsing needs Android's colour
 * parser, which the JVM stubs.
 */
class RadioLabelAndButtonColorsTest {

    private fun node(s: String): JsonObject = JsonParser.parseString(s).asJsonObject
    private fun attrs(n: JsonObject) = RadioAttributes.parse(TypedAttrs.toAttrMap(n))
    private val red = Color(0xFFFF0000)
    private val blue = Color(0xFF0000FF)
    private val green = Color(0xFF00FF00)

    @Test
    fun theLabelStyleIsTheDeclaredFontSizeAndFontColor_staticAndBound() {
        val static = node("""{"type":"Radio","items":["a","b"],"fontSize":30,"fontColor":"@{c}","font":"bold"}""")
        val s = DynamicRadioComponent.labelStyle(attrs(static), static, mapOf("c" to red), null)
        assertEquals(30f, s.size)
        assertEquals(red, s.color)
        assertEquals(androidx.compose.ui.text.font.FontWeight.Bold, s.weight)
        val bound = node("""{"type":"Radio","items":["a","b"],"fontSize":"@{size}","fontColor":"@{c}"}""")
        val b = DynamicRadioComponent.labelStyle(attrs(bound), bound, mapOf("size" to 18, "c" to blue), null)
        assertEquals(18f, b.size)
        assertEquals(blue, b.color)
        // none declared: nothing of its own (each path keeps its fallback)
        val none = node("""{"type":"Radio","items":["a"]}""")
        val n = DynamicRadioComponent.labelStyle(attrs(none), none, emptyMap(), null)
        assertNull(n.size); assertNull(n.color); assertNull(n.weight)
        // the legacy `textColor` after the declared colour
        val legacy = node("""{"type":"Radio","items":["a"],"textColor":"@{c}"}""")
        assertEquals(green, DynamicRadioComponent.labelStyle(attrs(legacy), legacy, mapOf("c" to green), null).color)
    }

    @Test
    fun theSelectedButtonIsCheckedColor_thenTheLegacySpelling_thenTintColor() {
        fun selected(json: String, data: Map<String, Any>) =
            node(json).let { DynamicRadioComponent.buttonColorValues(attrs(it), it, data, null).first }
        val tintOnly = """{"type":"Radio","items":["a"],"tintColor":"@{t}"}"""
        assertEquals(red, selected(tintOnly, mapOf("t" to red)))
        assertEquals(blue, selected("""{"type":"Radio","items":["a"],"tintColor":"@{t}","checkedColor":"@{k}"}""",
            mapOf("t" to red, "k" to blue)))
        assertEquals(green, selected("""{"type":"Radio","items":["a"],"tintColor":"@{t}","selectedColor":"@{s}"}""",
            mapOf("t" to red, "s" to green)))
        assertNull(selected("""{"type":"Radio","items":["a"]}""", emptyMap()))
        // the others: uncheckedColor, iconColor, the legacy unselectedColor — never the tint
        val others = node("""{"type":"Radio","items":["a"],"tintColor":"@{t}","iconColor":"@{i}"}""")
        assertEquals(blue, DynamicRadioComponent.buttonColorValues(attrs(others), others, mapOf("t" to red, "i" to blue), null).second)
        val tinted = node(tintOnly)
        assertNull(DynamicRadioComponent.buttonColorValues(attrs(tinted), tinted, mapOf("t" to red), null).second)
    }
}
