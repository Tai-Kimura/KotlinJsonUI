package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.ButtonAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Parse-level coverage for the typed Button component (pilot 2/5). */
class ButtonAttrsParseTest {

    private fun parse(json: String, canonicalOnly: Boolean = false): ButtonAttributes =
        ButtonAttributes.parse(
            TypedAttrs.toAttrMap(JsonParser.parseString(json).asJsonObject),
            canonicalOnly
        )

    @Test
    fun `text and fontSize parse to typed values`() {
        val a = parse("""{"type":"Button","text":"Tap","fontSize":18}""")
        assertEquals("Tap", TypedAttrs.rawString(a.text))
        assertEquals(18.0, a.fontSize)
    }

    @Test
    fun `enabled binding resolves through the data map`() {
        val a = parse("""{"type":"Button","enabled":"@{canSubmit}"}""")
        assertEquals(false, TypedAttrs.boolean(a.common.enabled, mapOf("canSubmit" to false)))
        assertEquals(true, TypedAttrs.boolean(a.common.enabled, mapOf("canSubmit" to true)))
    }

    @Test
    fun `textAlign passes undeclared spellings through`() {
        // "start" is not a declared enum value; the legacy reader honored it
        val a = parse("""{"type":"Button","textAlign":"start"}""")
        assertEquals("start", TypedAttrs.enumString(a.textAlign) { it.json })

        val declared = parse("""{"type":"Button","textAlign":"Center"}""")
        assertEquals("Center", TypedAttrs.enumString(declared.textAlign) { it.json })
    }

    @Test
    fun `highlightBackground and tapBackground are distinct attributes`() {
        // highlightBackground (highlighted state) is NOT an alias of
        // tapBackground (tap state) — the definitions treat them as two
        // standalone rows, so neither spelling fills the other.
        val highlighted = parse("""{"type":"Button","highlightBackground":"#000000"}""")
        assertNull(highlighted.tapBackground)
        assertEquals("#000000", highlighted.highlightBackground)

        val canonicalOnly = parse(
            """{"type":"Button","highlightBackground":"#000000"}""",
            canonicalOnly = true
        )
        assertNull(canonicalOnly.tapBackground)
        assertEquals("#000000", canonicalOnly.highlightBackground)

        val canonical = parse("""{"type":"Button","tapBackground":"#FFFFFF"}""")
        assertEquals("#FFFFFF", canonical.tapBackground)
        assertNull(canonical.highlightBackground)
    }

    @Test
    fun `onClick binding raw representation is preserved`() {
        val a = parse("""{"type":"Button","onClick":"@{submit}"}""")
        assertEquals("@{submit}", TypedAttrs.raw(a.common.onClick))
    }
}
