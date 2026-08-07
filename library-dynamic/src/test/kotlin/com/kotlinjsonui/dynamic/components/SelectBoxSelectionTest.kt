package com.kotlinjsonui.dynamic.components

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.SelectBoxAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * What a closed SelectBox shows, and which data key it is bound to.
 *
 * `selectedValue` is declared two-way but was not a binding candidate, so a
 * bound one fell through to the literal seed — where the raw read returns the
 * LAYOUT spelling and the closed box drew the characters `@{boundSelectedValue}`
 * on screen. That is the bound-literal-leak family plan 41 closed on the
 * codegen side and `2cf42df` closed for the Radio and CheckBox labels; the
 * SelectBox was the one left.
 */
class SelectBoxSelectionTest {

    private fun attrs(json: String): SelectBoxAttributes =
        SelectBoxAttributes.parse(TypedAttrs.toAttrMap(Gson().fromJson(json, JsonObject::class.java)))

    // ── the leak ─────────────────────────────────────────────────────

    @Test
    fun aBoundSelectedValueIsNeverPrintedAsItsExpression() {
        // The `SelectBox/selectedValue__binding` fixture shape.
        val a = attrs("""{"type":"SelectBox","items":["One","Two"],"selectedValue":"@{boundSelectedValue}"}""")
        val shown = DynamicSelectBoxComponent.initialSelection(a, emptyMap())
        assertFalse("the binding expression reached the screen", shown.contains("@{"))
        assertEquals("", shown)
    }

    @Test
    fun aBoundSelectedValueResolvesThroughTheDataMap() {
        val a = attrs("""{"type":"SelectBox","items":["One","Two"],"selectedValue":"@{boundSelectedValue}"}""")
        assertEquals(
            "Two",
            DynamicSelectBoxComponent.initialSelection(a, mapOf("boundSelectedValue" to "Two"))
        )
    }

    @Test
    fun aBoundSelectedValueIsTheDataKeyWrittenBackTo() {
        // Two-way: the same key the change handler updates. Being a binding
        // variable is what makes the write-back reach the ViewModel.
        val a = attrs("""{"type":"SelectBox","items":["One"],"selectedValue":"@{boundSelectedValue}"}""")
        assertEquals("boundSelectedValue", DynamicSelectBoxComponent.bindingVariableOf(a))
    }

    // ── the seed still works ─────────────────────────────────────────

    @Test
    fun aLiteralSelectedValueStillSeedsTheClosedBox() {
        val a = attrs("""{"type":"SelectBox","items":["One","Two"],"selectedValue":"Two"}""")
        assertEquals("Two", DynamicSelectBoxComponent.initialSelection(a, emptyMap()))
    }

    @Test
    fun aLiteralSelectedItemSeedsTheClosedBox() {
        // The `SelectBox/selectedItem__static` fixture shape. `selectedItem` is
        // declared `["string","binding"]`, but only its BOUND face was read
        // (as the data key); the static face reached no seed, so the closed box
        // drew empty on android where ios showed the selection.
        val a = attrs("""{"type":"SelectBox","items":["One","Two"],"selectedItem":"Two"}""")
        assertEquals("Two", DynamicSelectBoxComponent.initialSelection(a, emptyMap()))
    }

    @Test
    fun aLiteralSelectedItemOutranksALiteralSelectedValue() {
        // Same order as the bound faces (selectedItem > selectedValue) and the
        // same order the kjui codegen seeds in (selectbox_component.rb:54-60).
        val a = attrs(
            """{"type":"SelectBox","items":["One","Two"],"selectedItem":"Two","selectedValue":"One"}"""
        )
        assertEquals("Two", DynamicSelectBoxComponent.initialSelection(a, emptyMap()))
    }

    @Test
    fun selectedIndexStillSeedsTheClosedBox() {
        val a = attrs("""{"type":"SelectBox","items":["One","Two"],"selectedIndex":1}""")
        assertEquals("Two", DynamicSelectBoxComponent.initialSelection(a, emptyMap()))
    }

    @Test
    fun nothingDeclaredShowsNothing() {
        val a = attrs("""{"type":"SelectBox","items":["One","Two"]}""")
        assertEquals("", DynamicSelectBoxComponent.initialSelection(a, emptyMap()))
        assertNull(DynamicSelectBoxComponent.bindingVariableOf(a))
    }

    // ── precedence ───────────────────────────────────────────────────

    @Test
    fun selectedItemOutranksSelectedValueAsTheBoundKey() {
        val a = attrs(
            """{"type":"SelectBox","items":["One"],"selectedItem":"@{chosen}","selectedValue":"@{other}"}"""
        )
        assertEquals("chosen", DynamicSelectBoxComponent.bindingVariableOf(a))
    }

    @Test
    fun theBoundValueOutranksTheLiteralSeed() {
        val a = attrs(
            """{"type":"SelectBox","items":["One","Two"],"selectedItem":"@{chosen}","selectedIndex":1}"""
        )
        assertEquals(
            "One",
            DynamicSelectBoxComponent.initialSelection(a, mapOf("chosen" to "One"))
        )
    }

    @Test
    fun anUnresolvedBoundValueFallsBackToTheSeed() {
        // The data map does not carry the key yet; the seed is what the box
        // starts with, not an empty row.
        val a = attrs(
            """{"type":"SelectBox","items":["One","Two"],"selectedItem":"@{chosen}","selectedIndex":1}"""
        )
        assertEquals("Two", DynamicSelectBoxComponent.initialSelection(a, emptyMap()))
    }

    // ── the same family, found by counting ───────────────────────────

    @Test
    fun boundDateBoundsResolveInsteadOfReachingThePickerAsText() {
        // Declared binding-supported, and handed to the picker raw — so a
        // bound bound arrived as `@{expr}` and constrained nothing. Turned up
        // by counting every raw read, not by stopping at the reported one.
        val a = attrs(
            """{"type":"SelectBox","items":["One"],"selectItemType":"Date",
                "minimumDate":"@{from}","maximumDate":"@{to}"}"""
        )
        assertEquals("2026-01-01", TypedAttrs.string(a.minimumDate, mapOf("from" to "2026-01-01")))
        assertEquals("2026-12-31", TypedAttrs.string(a.maximumDate, mapOf("to" to "2026-12-31")))
        assertNull("an unresolved bound date must not become its expression",
            TypedAttrs.string(a.minimumDate, emptyMap()))
    }

    @Test
    fun staticDateBoundsAreUnchanged() {
        val a = attrs(
            """{"type":"SelectBox","items":["One"],"selectItemType":"Date",
                "minimumDate":"2026-01-01","maximumDate":"2026-12-31"}"""
        )
        assertEquals("2026-01-01", TypedAttrs.string(a.minimumDate, emptyMap()))
        assertEquals("2026-12-31", TypedAttrs.string(a.maximumDate, emptyMap()))
    }
}
