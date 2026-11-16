package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.ViewAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Parse-level coverage for the View-family layout containers
 * (View / HStack / VStack / ZStack — wave 1c). All four render through
 * ViewAttributes; these tests pin the attribute reads the container
 * component performs (orientation / direction / distribution / spacing)
 * plus a binding and a boolean case.
 */
class Wave1cAttrsParseTest {

    private fun obj(json: String) = JsonParser.parseString(json).asJsonObject

    private fun parse(json: String): ViewAttributes =
        ViewAttributes.parse(TypedAttrs.toAttrMap(obj(json)))

    // ── orientation ──

    @Test
    fun `orientation declared spellings map to the canonical json value`() {
        val vertical = parse("""{"type":"View","orientation":"vertical"}""")
        assertEquals("vertical", TypedAttrs.enumString(vertical.orientation) { it.json })

        val horizontal = parse("""{"type":"View","orientation":"horizontal"}""")
        assertEquals("horizontal", TypedAttrs.enumString(horizontal.orientation) { it.json })
    }

    @Test
    fun `orientation unknown spelling passes through raw for the Box fallback`() {
        val a = parse("""{"type":"View","orientation":"diagonal"}""")
        // Unknown passthrough keeps the raw spelling → matches no layout → Box
        assertEquals("diagonal", TypedAttrs.enumString(a.orientation) { it.json })

        val absent = parse("""{"type":"View"}""")
        assertNull(TypedAttrs.enumString(absent.orientation) { it.json })
    }

    // ── direction ──

    @Test
    fun `direction parses the reverse-order spellings`() {
        val bottomToTop = parse("""{"type":"View","direction":"bottomToTop"}""")
        assertEquals("bottomToTop", TypedAttrs.enumString(bottomToTop.direction) { it.json })

        val rightToLeft = parse("""{"type":"View","direction":"rightToLeft"}""")
        assertEquals("rightToLeft", TypedAttrs.enumString(rightToLeft.direction) { it.json })
    }

    // ── distribution ──

    @Test
    fun `distribution parses all declared modes`() {
        for (mode in listOf("fill", "fillEqually", "equalSpacing", "equalCentering")) {
            val a = parse("""{"type":"View","distribution":"$mode"}""")
            assertEquals(mode, TypedAttrs.enumString(a.distribution) { it.json })
        }
    }

    // ── spacing ──

    @Test
    fun `spacing static number resolves as float`() {
        val a = parse("""{"type":"View","spacing":8}""")
        assertEquals(8f, TypedAttrs.float(a.spacing, emptyMap())!!, 0f)

        val zero = parse("""{"type":"View","spacing":0}""")
        assertEquals(0f, TypedAttrs.float(zero.spacing, emptyMap())!!, 0f)
    }

    @Test
    fun `spacing binding keeps the expression and resolves through data`() {
        val a = parse("""{"type":"View","spacing":"@{gap}"}""")
        assertEquals("gap", TypedAttrs.binding(a.spacing))
        assertEquals(12f, TypedAttrs.float(a.spacing, mapOf("gap" to 12))!!, 0f)
        assertNull(TypedAttrs.float(a.spacing, emptyMap()))
    }

    // ── booleans / common section ──

    @Test
    fun `draggable boolean and common centerHorizontal parse`() {
        val a = parse("""{"type":"View","draggable":true,"centerHorizontal":true}""")
        assertEquals(true, a.draggable)
        assertEquals(true, TypedAttrs.static(a.common.centerHorizontal))
    }

    // ── structural child arrays stay declared rows ──

    @Test
    fun `child and children arrays parse as lists`() {
        val a = parse(
            """{"type":"View","child":[{"type":"Label","text":"a"},{"type":"Label","text":"b"}]}"""
        )
        assertEquals(2, a.child?.size)
        assertTrue(a.child?.get(0) is Map<*, *>)
        assertNull(a.children)
    }
}
