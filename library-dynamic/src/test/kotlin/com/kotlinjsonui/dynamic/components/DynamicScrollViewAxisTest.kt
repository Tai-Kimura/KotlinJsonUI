package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.ScrollViewAttributes
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The axis a dynamic ScrollView scrolls on: sideways when horizontalScroll is
 * true or orientation is horizontal, vertically otherwise — never from its
 * children, and neither key outranks the other (attribute_definitions.json
 * ScrollView, user ruling 2026-10-14; the rule sjui / kjui codegen and rjui
 * share). jui build refuses a pair that disagrees (ERROR [scrollview]).
 *
 * Until 2.43.11 a written horizontalScroll outranked orientation
 * (jui-scrollview-horizontalscroll-false-disagrees) and, with neither
 * written, the first child View's orientation decided
 * (jui-scrollview-axis-from-child-web-disagrees): the first and third cases
 * below answered the other way then.
 */
class DynamicScrollViewAxisTest {
    private fun horizontal(json: String): Boolean {
        val a = ScrollViewAttributes.parse(TypedAttrs.toAttrMap(JsonParser.parseString(json).asJsonObject), true)
        return DynamicScrollViewComponent.scrollsHorizontally(a)
    }

    @Test
    fun aHorizontalChildViewLeavesTheScrollVertical() {
        assertFalse(horizontal("""{"type":"ScrollView","child":[{"type":"View","orientation":"horizontal"}]}"""))
        assertFalse(horizontal("""{"type":"ScrollView","child":[{"type":"Label"},{"type":"View","orientation":"horizontal"}]}"""))
    }

    @Test
    fun orientationHorizontalOrHorizontalScrollTrueScrollsSideways() {
        assertTrue(horizontal("""{"type":"ScrollView","orientation":"horizontal"}"""))
        assertTrue(horizontal("""{"type":"ScrollView","horizontalScroll":true}"""))
    }

    @Test
    fun horizontalScrollFalseDoesNotOutrankOrientationHorizontal() {
        assertTrue(horizontal("""{"type":"ScrollView","orientation":"horizontal","horizontalScroll":false}"""))
        assertTrue(horizontal("""{"type":"ScrollView","orientation":"vertical","horizontalScroll":true}"""))
    }

    @Test
    fun anythingElseIsVertical() {
        assertFalse(horizontal("""{"type":"ScrollView"}"""))
        assertFalse(horizontal("""{"type":"ScrollView","orientation":"vertical"}"""))
        assertFalse(horizontal("""{"type":"ScrollView","horizontalScroll":false}"""))
    }
}
