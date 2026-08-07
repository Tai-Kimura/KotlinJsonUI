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
    fun groupStateComparesAgainstValueNotTheIdWhenValueIsDeclared() {
        // C's codegen compares against `value || id`
        // (radio_component.rb#radio_selected_expr).
        val a = attrs("group" to "g", "text" to "Sample", "value" to "sample")
        assertTrue(DynamicRadioComponent.itemIsSelected(a, "target", mapOf("selectedG" to "sample")))
        assertFalse(DynamicRadioComponent.itemIsSelected(a, "target", mapOf("selectedG" to "target")))
    }

    @Test
    fun aDeclaredGroupDoesNotCancelTheSeed() {
        // `checked` is a SEED, not an override — and the declared precedence
        // (SSoT Radio.checked) is `bound selectedValue > literal selectedValue
        // > checked`, with NO group term above the seed. `group` only picks
        // WHICH key holds the selection.
        //
        // This test previously asserted the opposite (a declared group cancels
        // the seed outright). That rule was never declared, and it made android
        // the deviant: `Radio/checked__true_with_group` rendered an unselected
        // glyph while ios and web drew the seed (cross_effect, 51 CG-android).
        val a = attrs("group" to "g", "text" to "Sample", "checked" to true)
        assertTrue(DynamicRadioComponent.itemIsSelected(a, "target", emptyMap()))
        assertTrue(DynamicRadioComponent.itemIsSelected(a, "target", mapOf("selectedG" to "target")))
        // The original concern still holds: once the GROUP has chosen, the seed
        // is out of the way, so it cannot pin a radio the user is driving.
        assertFalse(DynamicRadioComponent.itemIsSelected(a, "target", mapOf("selectedG" to "other")))
    }

    @Test
    fun literalCheckedStillSeedsAnUnsetGroup() {
        val a = attrs("text" to "Sample", "checked" to true)
        assertTrue(DynamicRadioComponent.itemIsSelected(a, "target", emptyMap()))
    }

    @Test
    fun boundCheckedSeedsTheGlyphThroughTheDataMap() {
        // The bound face of `checked` is declared, and the SSoT says it "seeds
        // the glyph rather than the state ... it selects only while the group
        // has made no choice yet". `raw(...) as? Boolean` saw the `"@{expr}"`
        // String and dropped it (`Radio/checked__binding` inert on android).
        val a = attrs("text" to "Sample", "checked" to "@{boundChecked}")
        assertTrue(DynamicRadioComponent.itemIsSelected(a, "target", mapOf("boundChecked" to true)))
        assertFalse(DynamicRadioComponent.itemIsSelected(a, "target", mapOf("boundChecked" to false)))
    }

    // ── groupInitialSelection (items mode) ───────────────────────────

    @Test
    fun aLiteralSelectedValueSeedsAnItemsGroup() {
        // The `Radio/selectedValue__gamma` fixture shape. This path reached for
        // the row through the `undeclared` hatch and only asked whether it was
        // a binding, so a literal named nothing and the group rendered with no
        // option selected.
        val a = attrs("items" to listOf("Alpha", "Beta", "Gamma"), "selectedValue" to "Gamma")
        assertEquals("Gamma", DynamicRadioComponent.groupInitialSelection(a, emptyMap()))
    }

    @Test
    fun aBoundSelectedValueIsTheGroupsChannel() {
        val a = attrs("items" to listOf("Alpha", "Beta"), "selectedValue" to "@{chosen}")
        assertEquals("Beta", DynamicRadioComponent.groupInitialSelection(a, mapOf("chosen" to "Beta")))
        assertEquals(
            "an unresolved channel selects nothing — it does not fall back to printing the expression",
            "",
            DynamicRadioComponent.groupInitialSelection(a, emptyMap())
        )
    }

    @Test
    fun anItemsGroupWithNoSelectedValueStartsUnselected() {
        val a = attrs("items" to listOf("Alpha", "Beta"))
        assertEquals("", DynamicRadioComponent.groupInitialSelection(a, emptyMap()))
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
