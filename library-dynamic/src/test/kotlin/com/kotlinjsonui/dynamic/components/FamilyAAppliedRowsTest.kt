package com.kotlinjsonui.dynamic.components

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.SelectBoxAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Plan 49-G family A — declared rows whose spelling appeared NOWHERE in the
 * owning dynamic component, so the attribute could not reach the render no
 * matter what the codegen emitted.
 *
 * The rendering itself is Compose and belongs to the instrumented layer; what
 * is pinned here is the part that made these silent: the component must
 * declare it APPLIES the row. `UnappliedAttributes.check` walks the applied
 * set on every debug composition, so a row missing from it is exactly the
 * "parsed but never read" state this wave was measuring.
 */
class FamilyAAppliedRowsTest {

    private fun obj(json: String): JsonObject = Gson().fromJson(json, JsonObject::class.java)

    // ── SelectBox.labelAttributes ────────────────────────────────────

    @Test
    fun selectBoxLabelAttributesKeysWinOverTheComponentLevelRows() {
        // The `SelectBox/labelAttributes__static` fixture shape.
        val a = SelectBoxAttributes.parse(
            TypedAttrs.toAttrMap(
                obj(
                    """{"type":"SelectBox","items":["One","Two"],"fontColor":"#0000FF","fontSize":10,
                       "labelAttributes":{"font":"bold","fontSize":24,"fontColor":"#FF0000","textAlign":"Center"}}"""
                )
            )
        )
        assertEquals("#FF0000", DynamicSelectBoxComponent.labelAttr(a, "fontColor"))
        assertEquals(24.0, DynamicSelectBoxComponent.labelAttr(a, "fontSize"))
    }

    @Test
    fun selectBoxLabelAttributesAbsentLeavesTheComponentRows() {
        val a = SelectBoxAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"SelectBox","items":["One"],"fontColor":"#0000FF"}"""))
        )
        assertNull(DynamicSelectBoxComponent.labelAttr(a, "fontColor"))
        assertNull(DynamicSelectBoxComponent.labelAttr(a, "fontSize"))
    }

    @Test
    fun selectBoxLabelAttributesIsDeclaredAsAnObject() {
        val a = SelectBoxAttributes.parse(
            TypedAttrs.toAttrMap(
                obj("""{"type":"SelectBox","items":["One"],"labelAttributes":{"fontSize":24}}""")
            )
        )
        assertTrue(a.labelAttributes is Map<*, *>)
    }
}
