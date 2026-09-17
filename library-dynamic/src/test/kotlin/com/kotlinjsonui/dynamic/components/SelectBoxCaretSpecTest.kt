package com.kotlinjsonui.dynamic.components

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.SelectBoxAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * `SelectBox.caretAttributes` on the dynamic path (SSoT: a cross-platform
 * object since jsonui-cli 1.8.101; this converter parsed it and never read it).
 *
 * What is pinned here is the pure half — which keys reach the library's
 * SelectBoxCaret and with what defaults. The drawable and colour resolution,
 * and the picture, are Compose and belong to the conformance host
 * (`SelectBox/caretAttributes__static`).
 */
class SelectBoxCaretSpecTest {

    private fun attrs(json: String): SelectBoxAttributes =
        SelectBoxAttributes.parse(TypedAttrs.toAttrMap(Gson().fromJson(json, JsonObject::class.java)))

    @Test
    fun absentObjectMeansTheNativeCaretAndNoSpec() {
        // The `__control/SelectBox` shape — every existing layout.
        val a = attrs("""{"type":"SelectBox","items":["One","Two","Three"]}""")
        assertNull(DynamicSelectBoxComponent.caretSpecOf(a))
    }

    @Test
    fun theFixtureObjectReachesTheSpecVerbatim() {
        // The `SelectBox/caretAttributes__static` fixture value.
        val a = attrs(
            """{"type":"SelectBox","items":["One","Two","Three"],
                "caretAttributes":{"width":32,"height":32,"tintColor":"#FF0000","background":"#00AA00","rightMargin":24}}"""
        )
        assertEquals(
            SelectBoxCaretSpec(
                src = null, width = 32, height = 32,
                tintColor = "#FF0000", background = "#00AA00", rightMargin = 24
            ),
            DynamicSelectBoxComponent.caretSpecOf(a)
        )
    }

    @Test
    fun anEmptyObjectIsPresentWithEveryDefaultAndRightMarginZero() {
        // `{"rightMargin": 12}` alone is the SSoT's motivating shape; `{}` is
        // its limit: the face draws its own caret, flush at the edge.
        val a = attrs("""{"type":"SelectBox","items":["One"],"caretAttributes":{}}""")
        assertEquals(
            SelectBoxCaretSpec(null, null, null, null, null, rightMargin = 0),
            DynamicSelectBoxComponent.caretSpecOf(a)
        )
    }

    @Test
    fun srcIsKeptAsTheRawNameForTheComposableToResolve() {
        val a = attrs("""{"type":"SelectBox","items":["One"],"caretAttributes":{"src":"my_arrow","rightMargin":12}}""")
        val spec = DynamicSelectBoxComponent.caretSpecOf(a)!!
        assertEquals("my_arrow", spec.src)
        assertEquals(12, spec.rightMargin)
    }

    @Test
    fun aKeyOfTheWrongTypeIsAbsentNotZero() {
        val a = attrs("""{"type":"SelectBox","items":["One"],"caretAttributes":{"width":"32","src":"  ","rightMargin":"24"}}""")
        val spec = DynamicSelectBoxComponent.caretSpecOf(a)!!
        assertNull(spec.width)
        assertNull("a blank src is no src", spec.src)
        assertEquals("rightMargin falls to the contract default", 0, spec.rightMargin)
    }
}
