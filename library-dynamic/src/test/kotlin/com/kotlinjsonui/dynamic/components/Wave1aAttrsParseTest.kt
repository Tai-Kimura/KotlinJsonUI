package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.CheckBoxAttributes
import com.kotlinjsonui.dynamic.generated.ProgressAttributes
import com.kotlinjsonui.dynamic.generated.RadioAttributes
import com.kotlinjsonui.dynamic.generated.ToggleAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/** Parse-level coverage for the Toggle / CheckBox / Radio / Progress wave (1a). */
class Wave1aAttrsParseTest {

    private fun obj(json: String) = JsonParser.parseString(json).asJsonObject

    // ── Toggle ──

    @Test
    fun `toggle static isOn and tintColor parse to typed values`() {
        val a = ToggleAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Toggle","isOn":true,"tintColor":"#FF0000"}"""))
        )
        assertEquals(true, TypedAttrs.boolean(a.isOn, emptyMap()))
        assertEquals("#FF0000", a.tintColor)
    }

    @Test
    fun `toggle enabled binding resolves through the data map`() {
        val a = ToggleAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Toggle","enabled":"@{isEditable}"}"""))
        )
        assertEquals(false, TypedAttrs.boolean(a.enabled, mapOf("isEditable" to false)))
        assertEquals(true, TypedAttrs.boolean(a.enabled, mapOf("isEditable" to true)))
    }

    @Test
    fun `toggle labelPosition enum and labelAttributes map`() {
        val a = ToggleAttributes.parse(
            TypedAttrs.toAttrMap(
                obj(
                    """{"type":"Toggle","labelPosition":"trailing",
                       "labelAttributes":{"text":"Wi-Fi","fontSize":14}}"""
                )
            )
        )
        assertEquals("trailing", TypedAttrs.enumString(a.labelPosition) { it.json })
        assertEquals("Wi-Fi", a.labelAttributes?.get("text") as? String)
        assertEquals(14f, (a.labelAttributes?.get("fontSize") as? Number)?.toFloat())

        // Unknown spellings pass through (legacy compared raw strings)
        val unknown = ToggleAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Toggle","labelPosition":"top"}"""))
        )
        assertEquals("top", TypedAttrs.enumString(unknown.labelPosition) { it.json })
    }

    // ── CheckBox ──

    @Test
    fun `checkbox label rows and static state parse for both type spellings`() {
        val a = CheckBoxAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"CheckBox","label":"Agree","isOn":true}"""))
        )
        assertEquals("Agree", TypedAttrs.rawString(a.label))
        assertEquals(true, TypedAttrs.static(a.isOn))

        // "Check" is a stub alias spelling — still parsed with CheckBoxAttributes
        val alias = CheckBoxAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Check","text":"Agree"}"""))
        )
        assertEquals("Agree", TypedAttrs.rawString(alias.text))
    }

    @Test
    fun `checkbox binding state priority isOn over checked, bind stays raw`() {
        val a = CheckBoxAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"CheckBox","isOn":"@{a}","checked":"@{b}"}"""))
        )
        assertEquals("a", TypedAttrs.binding(a.isOn ?: a.checked))

        val bound = CheckBoxAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"CheckBox","bind":"@{c}"}"""))
        )
        assertEquals("@{c}", TypedAttrs.raw(bound.bind))
    }

    @Test
    fun `checkbox spacing resolves statics and bindings as float`() {
        val static = CheckBoxAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"CheckBox","spacing":10}"""))
        )
        assertEquals(10f, TypedAttrs.float(static.spacing, emptyMap()))

        val bound = CheckBoxAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"CheckBox","spacing":"@{gap}"}"""))
        )
        assertEquals(12f, TypedAttrs.float(bound.spacing, mapOf("gap" to 12)))
    }

    // ── Radio ──

    @Test
    fun `radio group, text and icon parse to typed values`() {
        val a = RadioAttributes.parse(
            TypedAttrs.toAttrMap(
                obj("""{"type":"Radio","group":"g1","text":"Option A","icon":"square"}""")
            )
        )
        assertEquals("g1", a.group)
        assertEquals("Option A", TypedAttrs.rawString(a.text))
        assertEquals("square", a.icon)
    }

    @Test
    fun `radio fontColor binding keeps the raw representation`() {
        val a = RadioAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Radio","fontColor":"@{labelColor}"}"""))
        )
        assertEquals("@{labelColor}", TypedAttrs.rawString(a.fontColor))
    }

    @Test
    fun `radio items and selectedValue stay undeclared runtime extras`() {
        assertFalse("items" in RadioAttributes.declaredAttributes)
        assertFalse("selectedValue" in RadioAttributes.declaredAttributes)

        val node = obj("""{"type":"Radio","items":["a","b"],"selectedValue":"@{sel}"}""")
        assertNotNull(TypedAttrs.undeclared(node, "items"))
        assertEquals("@{sel}", TypedAttrs.undeclared(node, "selectedValue")?.asString)
    }

    // ── Progress ──

    @Test
    fun `progress tint colors parse statics and binding raws`() {
        val a = ProgressAttributes.parse(
            TypedAttrs.toAttrMap(
                obj(
                    """{"type":"Progress","progressTintColor":"#FF0000",
                       "trackTintColor":"@{trackColor}"}"""
                )
            )
        )
        assertEquals("#FF0000", TypedAttrs.rawString(a.progressTintColor))
        assertEquals("@{trackColor}", TypedAttrs.rawString(a.trackTintColor))
    }

    @Test
    fun `progress indicatorStyle enum passes unknown spellings through`() {
        val known = ProgressAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Progress","indicatorStyle":"large"}"""))
        )
        assertEquals("large", TypedAttrs.enumString(known.indicatorStyle) { it.json })

        val unknown = ProgressAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Progress","indicatorStyle":"huge"}"""))
        )
        assertEquals("huge", TypedAttrs.enumString(unknown.indicatorStyle) { it.json })
    }

    @Test
    fun `progress value stays an undeclared runtime extra, progress row parses`() {
        assertFalse("value" in ProgressAttributes.declaredAttributes)

        val node = obj("""{"type":"Progress","value":0.3,"progress":0.5}""")
        assertNotNull(TypedAttrs.undeclared(node, "value"))

        val a = ProgressAttributes.parse(TypedAttrs.toAttrMap(node))
        assertEquals(0.5f, TypedAttrs.float(a.progress, emptyMap()))

        val absent = ProgressAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Progress"}"""))
        )
        assertNull(absent.progress)
    }
}
