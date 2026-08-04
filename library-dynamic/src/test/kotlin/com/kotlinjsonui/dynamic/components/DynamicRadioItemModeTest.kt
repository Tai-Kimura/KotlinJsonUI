package com.kotlinjsonui.dynamic.components

import com.kotlinjsonui.dynamic.generated.RadioAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Single-radio (item mode) decisions.
 *
 * Both behaviours pinned here were measured pixel-identical to their control
 * on android by plan 34 while the codegen output changed — the gap was in the
 * dynamic path:
 *
 * - `label` never routed to item mode and never reached the row text, so a
 *   label-only Radio rendered an empty Column.
 * - `selectedValue` was read only in the items-array branch, so a single
 *   Radio declaring it could never render selected.
 */
class DynamicRadioItemModeTest {

    private fun attrs(vararg pairs: Pair<String, Any?>): RadioAttributes =
        RadioAttributes.parse(mapOf(*pairs))

    // ── rendersAsItem ────────────────────────────────────────────────

    @Test
    fun textRoutesToItemMode() {
        assertTrue(
            DynamicRadioComponent.rendersAsItem(
                attrs("text" to "Sample"), hasOptions = false
            )
        )
    }

    @Test
    fun labelRoutesToItemMode() {
        assertTrue(
            DynamicRadioComponent.rendersAsItem(
                attrs("label" to "sample"), hasOptions = false
            )
        )
    }

    @Test
    fun groupRoutesToItemModeEvenWithOptions() {
        assertTrue(
            DynamicRadioComponent.rendersAsItem(
                attrs("group" to "g"), hasOptions = true
            )
        )
    }

    @Test
    fun labelWithOptionsStaysAGroup() {
        assertFalse(
            DynamicRadioComponent.rendersAsItem(
                attrs("label" to "sample"), hasOptions = true
            )
        )
    }

    @Test
    fun bareNodeIsNotAnItem() {
        assertFalse(DynamicRadioComponent.rendersAsItem(attrs(), hasOptions = false))
    }

    // ── itemText ─────────────────────────────────────────────────────

    @Test
    fun itemTextFallsBackToLabel() {
        assertEquals("sample", DynamicRadioComponent.itemText(attrs("label" to "sample")))
    }

    @Test
    fun itemTextPrefersTextOverLabel() {
        assertEquals(
            "Sample",
            DynamicRadioComponent.itemText(attrs("text" to "Sample", "label" to "sample"))
        )
    }

    @Test
    fun itemTextEmptyWhenNeitherDeclared() {
        assertEquals("", DynamicRadioComponent.itemText(attrs()))
    }

    // ── itemValue ────────────────────────────────────────────────────

    @Test
    fun itemValuePrefersDeclaredValue() {
        assertEquals(
            "sample",
            DynamicRadioComponent.itemValue(attrs("value" to "sample"), "target")
        )
    }

    @Test
    fun itemValueFallsBackToId() {
        assertEquals("target", DynamicRadioComponent.itemValue(attrs(), "target"))
    }

    // ── itemIsSelected ───────────────────────────────────────────────

    @Test
    fun staticSelectedValueMatchingValueSelects() {
        // The `Radio/selectedValue__static` fixture shape exactly.
        val a = attrs("text" to "Sample", "value" to "sample", "selectedValue" to "sample")
        assertTrue(DynamicRadioComponent.itemIsSelected(a, "target", emptyMap()))
    }

    @Test
    fun staticSelectedValueNotMatchingValueDoesNotSelect() {
        val a = attrs("text" to "Sample", "value" to "sample", "selectedValue" to "other")
        assertFalse(DynamicRadioComponent.itemIsSelected(a, "target", emptyMap()))
    }

    @Test
    fun staticSelectedValueFallsBackToIdIdentity() {
        val a = attrs("text" to "Sample", "selectedValue" to "target")
        assertTrue(DynamicRadioComponent.itemIsSelected(a, "target", emptyMap()))
    }

    @Test
    fun boundSelectedValueResolvesThroughData() {
        val a = attrs("text" to "Sample", "value" to "sample", "selectedValue" to "@{choice}")
        assertTrue(DynamicRadioComponent.itemIsSelected(a, "target", mapOf("choice" to "sample")))
        assertFalse(DynamicRadioComponent.itemIsSelected(a, "target", mapOf("choice" to "other")))
    }

    @Test
    fun selectedValueWinsOverGroupState() {
        val a = attrs("group" to "g", "value" to "sample", "selectedValue" to "other")
        // Group data names this row, but the declared selection does not.
        assertFalse(DynamicRadioComponent.itemIsSelected(a, "target", mapOf("selectedG" to "target")))
    }

    @Test
    fun withoutSelectedValueGroupStateStillDrivesSelection() {
        val a = attrs("group" to "g", "text" to "Sample")
        assertTrue(DynamicRadioComponent.itemIsSelected(a, "target", mapOf("selectedG" to "target")))
        assertFalse(DynamicRadioComponent.itemIsSelected(a, "target", mapOf("selectedG" to "other")))
    }

    @Test
    fun literalCheckedStillSeedsAnUnsetGroup() {
        val a = attrs("text" to "Sample", "checked" to true)
        assertTrue(DynamicRadioComponent.itemIsSelected(a, "target", emptyMap()))
    }

    // ── selectedVarName ──────────────────────────────────────────────

    @Test
    fun selectedVarNameDefaultsToRadiogroup() {
        assertEquals("selectedRadiogroup", DynamicRadioComponent.selectedVarName("default"))
    }

    @Test
    fun selectedVarNameCapitalizesTheGroup() {
        assertEquals("selectedSample", DynamicRadioComponent.selectedVarName("sample"))
    }
}
