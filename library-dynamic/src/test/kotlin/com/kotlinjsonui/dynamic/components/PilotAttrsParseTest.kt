package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.ImageAttributes
import com.kotlinjsonui.dynamic.generated.SwitchAttributes
import com.kotlinjsonui.dynamic.generated.TextFieldAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Parse-level coverage for the TextField / Image / Switch pilots (3-5/5). */
class PilotAttrsParseTest {

    private fun obj(json: String) = JsonParser.parseString(json).asJsonObject

    // ── TextField ──

    @Test
    fun `textfield hint and placeholder are both declared rows`() {
        val a = TextFieldAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"TextField","placeholder":"Mail"}"""))
        )
        assertNull(a.hint)
        assertEquals("Mail", a.placeholder)
    }

    @Test
    fun `textfield secure resolves from secure, input and contentType`() {
        val secure = TextFieldAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"TextField","secure":true}"""))
        )
        assertEquals(true, TypedAttrs.static(secure.secure))

        val input = TextFieldAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"TextField","input":"password"}"""))
        )
        assertEquals("password", TypedAttrs.enumString(input.input) { it.json }?.lowercase())

        val content = TextFieldAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"TextField","contentType":"newPassword"}"""))
        )
        assertEquals("newpassword", TypedAttrs.static(content.contentType)?.lowercase())
    }

    @Test
    fun `textfield two-way text binding keeps the raw representation`() {
        val a = TextFieldAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"TextField","text":"@{email}"}"""))
        )
        assertEquals("@{email}", TypedAttrs.rawString(a.text))
    }

    // ── Image ──

    @Test
    fun `image source priority fields parse independently`() {
        val a = ImageAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Image","srcName":"icon_a","src":"icon_b"}"""))
        )
        assertEquals("icon_a", TypedAttrs.rawString(a.srcName))
        assertEquals("icon_b", TypedAttrs.rawString(a.src))
    }

    @Test
    fun `image contentMode resolves static spellings and bindings`() {
        val static = ImageAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Image","contentMode":"AspectFill"}"""))
        )
        assertEquals(
            "aspectfill",
            TypedAttrs.enumStringResolved(static.contentMode, emptyMap()) { it.json }?.lowercase()
        )

        val bound = ImageAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Image","contentMode":"@{mode}"}"""))
        )
        assertEquals(
            "center",
            TypedAttrs.enumStringResolved(bound.contentMode, mapOf("mode" to "center")) { it.json }
        )
    }

    // ── Switch ──

    @Test
    fun `switch state priority isOn over value over checked`() {
        val a = SwitchAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Switch","isOn":true,"value":false}"""))
        )
        assertEquals(true, TypedAttrs.static(a.isOn ?: a.value ?: a.checked))
    }

    @Test
    fun `switch binding state exposes the expression`() {
        val a = SwitchAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Switch","checked":"@{agreed}"}"""))
        )
        assertEquals("agreed", TypedAttrs.binding(a.isOn ?: a.value ?: a.checked))
    }

    @Test
    fun `switch tint trio stays canonical-order`() {
        val a = SwitchAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Switch","tintColor":"#FF0000","onTintColor":"#00FF00"}"""))
        )
        assertEquals("#00FF00", a.onTintColor)
        assertEquals("#FF0000", a.tintColor)
    }
}
