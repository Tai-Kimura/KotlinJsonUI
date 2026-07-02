package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.CollectionAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Parse-level coverage for the Collection / Table conversion (wave 2e). */
class Wave2eAttrsParseTest {

    private fun obj(json: String) = JsonParser.parseString(json).asJsonObject

    private fun parse(json: String, canonicalOnly: Boolean = false) =
        CollectionAttributes.parse(TypedAttrs.toAttrMap(obj(json)), canonicalOnly)

    // ── Static values ──

    @Test
    fun `collection static grid attributes parse typed`() {
        val a = parse(
            """{"type":"Collection","columns":3,"itemSpacing":8,"lineSpacing":12,
               "cellIdProperty":"id","layout":"horizontal","reverseLayout":true}"""
        )
        assertEquals(3, TypedAttrs.int(a.columns, emptyMap()))
        assertEquals(8f, a.itemSpacing?.toFloat())
        assertEquals(12f, a.lineSpacing?.toFloat())
        assertEquals("id", a.cellIdProperty)
        assertEquals("horizontal", TypedAttrs.enumString(a.layout) { it.json })
        assertEquals(true, a.reverseLayout)
    }

    @Test
    fun `collection scrollAnchor and orientation enums keep declared spellings`() {
        val a = parse("""{"type":"Collection","scrollAnchor":"top","orientation":"horizontal"}""")
        assertEquals("top", TypedAttrs.enumString(a.scrollAnchor) { it.json })
        assertEquals("horizontal", TypedAttrs.enumString(a.orientation) { it.json })
    }

    // ── Bindings ──

    @Test
    fun `collection items and scrollEnabled bindings keep expressions`() {
        val a = parse("""{"type":"Collection","items":"@{users}","scrollEnabled":"@{canScroll}"}""")
        assertEquals("@{users}", TypedAttrs.raw(a.items))
        assertEquals("canScroll", TypedAttrs.binding(a.scrollEnabled))
        assertEquals(false, TypedAttrs.boolean(a.scrollEnabled, mapOf("canScroll" to false)))
        assertEquals(true, TypedAttrs.boolean(a.scrollEnabled, mapOf("canScroll" to true)))
    }

    @Test
    fun `collection scrollTo binding recovers the raw representation`() {
        val a = parse("""{"type":"Collection","scrollTo":"@{scrollSignal}"}""")
        assertEquals("@{scrollSignal}", TypedAttrs.raw(a.scrollTo) as? String)
    }

    // ── onValueChange alias chain (pager callback) ──

    @Test
    fun `onPageChanged and onValueChanged aliases resolve to canonical onValueChange`() {
        val pageChanged = parse("""{"type":"Collection","onPageChanged":"@{pageChanged}"}""")
        assertEquals("@{pageChanged}", TypedAttrs.raw(pageChanged.onValueChange) as? String)

        val valueChanged = parse("""{"type":"Collection","onValueChanged":"@{valueChanged}"}""")
        assertEquals("@{valueChanged}", TypedAttrs.raw(valueChanged.onValueChange) as? String)
    }

    @Test
    fun `canonical onValueChange wins over alias spellings`() {
        val a = parse(
            """{"type":"Collection","onValueChange":"@{canonical}","onPageChanged":"@{alias}"}"""
        )
        assertEquals("@{canonical}", TypedAttrs.raw(a.onValueChange) as? String)
    }

    @Test
    fun `canonicalOnly parse skips alias spellings but keeps the canonical one`() {
        val aliasOnly = parse(
            """{"type":"Collection","onPageChanged":"@{pageChanged}"}""",
            canonicalOnly = true
        )
        assertNull(aliasOnly.onValueChange)

        val canonical = parse(
            """{"type":"Collection","onValueChange":"@{pageChanged}"}""",
            canonicalOnly = true
        )
        assertEquals("@{pageChanged}", TypedAttrs.raw(canonical.onValueChange) as? String)
    }

    // ── Declared-contract boundaries (Table path shares this module) ──

    @Test
    fun `table legacy runtime extras stay outside the declared contract`() {
        assertFalse(CollectionAttributes.isDeclared("rowHeight"))
        assertFalse(CollectionAttributes.isDeclared("separatorStyle"))
        assertFalse(CollectionAttributes.isDeclared("contentPadding"))
        assertTrue(CollectionAttributes.isDeclared("items"))
        assertTrue(CollectionAttributes.isDeclared("onPageChanged"))
        assertTrue(CollectionAttributes.isDeclared("bind")) // via common
    }
}
