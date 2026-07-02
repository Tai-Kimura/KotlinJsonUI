package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.TabViewAttributes
import com.kotlinjsonui.dynamic.generated.ViewAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Parse-level coverage for the TabView / ConstraintLayout wave (2d). */
class Wave2dAttrsParseTest {

    private fun obj(json: String) = JsonParser.parseString(json).asJsonObject

    // ── TabView ──

    @Test
    fun `tabview selectedTabIndex alias resolves on L0 and is skipped on L1`() {
        val l0 = TabViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"TabView","selectedTabIndex":2}"""))
        )
        assertEquals(2, TypedAttrs.static(l0.selectedIndex)?.toInt())

        val l1 = TabViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"TabView","selectedTabIndex":2}""")),
            canonicalOnly = true
        )
        assertNull(l1.selectedIndex)
    }

    @Test
    fun `tabview selectedIndex canonical wins over alias and exposes bindings`() {
        val both = TabViewAttributes.parse(
            TypedAttrs.toAttrMap(
                obj("""{"type":"TabView","selectedIndex":1,"selectedTabIndex":3}""")
            )
        )
        assertEquals(1, TypedAttrs.static(both.selectedIndex)?.toInt())

        val bound = TabViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"TabView","selectedIndex":"@{tab}"}"""))
        )
        assertEquals("tab", TypedAttrs.binding(bound.selectedIndex))
    }

    @Test
    fun `tabview onValueChange alias chain resolves on L0 and is skipped on L1`() {
        val alias = TabViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"TabView","onPageChanged":"@{onTab}"}"""))
        )
        assertEquals("@{onTab}", TypedAttrs.rawString(alias.onValueChange))
        assertEquals("onTab", TypedAttrs.binding(alias.onValueChange))

        val l1 = TabViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"TabView","onTabChange":"@{onTab}"}""")),
            canonicalOnly = true
        )
        assertNull(l1.onValueChange)
    }

    // ── ConstraintLayout (shared View section) ──

    @Test
    fun `constraintlayout node parses through the View section`() {
        val a = ViewAttributes.parse(
            TypedAttrs.toAttrMap(
                obj("""{"type":"ConstraintLayout","id":"root","children":[{"type":"View","id":"a"}]}""")
            )
        )
        assertEquals("root", a.common.id)
        assertEquals(1, a.children?.size)
    }

    @Test
    fun `constraintlayout common background and enum rows parse typed`() {
        val a = ViewAttributes.parse(
            TypedAttrs.toAttrMap(
                obj("""{"type":"ConstraintLayout","background":"#FF0000","orientation":"vertical"}""")
            )
        )
        assertEquals("#FF0000", TypedAttrs.rawString(a.common.background))
        assertEquals("vertical", TypedAttrs.enumString(a.orientation) { it.json })
    }
}
