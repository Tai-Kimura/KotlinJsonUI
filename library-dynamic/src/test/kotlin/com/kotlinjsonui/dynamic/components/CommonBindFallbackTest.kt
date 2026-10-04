package com.kotlinjsonui.dynamic.components

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.BindFold
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.CheckBoxAttributes
import com.kotlinjsonui.dynamic.generated.ProgressAttributes
import com.kotlinjsonui.dynamic.generated.RadioAttributes
import com.kotlinjsonui.dynamic.generated.SegmentAttributes
import com.kotlinjsonui.dynamic.generated.SelectBoxAttributes
import com.kotlinjsonui.dynamic.generated.SliderAttributes
import com.kotlinjsonui.dynamic.generated.SwitchAttributes
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * `bind` is the common two-way spelling for a component's primary value.
 * DynamicView folds it into the attribute it stands for (BindFold, SSoT
 * common.bind primaryValue) before the component reads the node, so each
 * component is bound through its own value attribute: a lone `bind` reaches
 * the key a component writes; beside the component's own value, it is gone.
 * (Every one of these read `bind` itself, in its own order: `bind` won over a
 * static own value, and on Progress over a bound one too.)
 */
class CommonBindFallbackTest {

    private fun node(json: String): JsonObject = Gson().fromJson(json, JsonObject::class.java)

    /** The node DynamicView hands the component: folded, as the drawn type. */
    private inline fun <reified T> drawn(json: String, parser: (Map<String, Any?>) -> T): T {
        val n = node(json)
        return parser(TypedAttrs.toAttrMap(BindFold.fold(n, n.get("type").asString)))
    }

    @Test
    fun aLoneBindIsTheKeyEachComponentWrites() {
        assertEquals("on", DynamicSwitchComponent.resolveBindingVariable(drawn("""{"type":"Switch","bind":"@{on}"}""") { SwitchAttributes.parse(it) }))
        assertEquals("c", DynamicCheckBoxComponent.resolveBindingVariable(drawn("""{"type":"CheckBox","bind":"@{c}"}""") { CheckBoxAttributes.parse(it) }))
        assertEquals("c", DynamicCheckBoxComponent.resolveBindingVariable(drawn("""{"type":"Check","bind":"@{c}"}""") { CheckBoxAttributes.parse(it) }))
        // Toggle is drawn by the Switch renderer (its `_alias_of`).
        val toggle = node("""{"type":"Toggle","bind":"@{t}"}""")
        val folded = BindFold.fold(toggle, "Toggle")
        assertEquals("t", DynamicSwitchComponent.resolveBindingVariable(SwitchAttributes.parse(TypedAttrs.toAttrMap(folded))))
        assertEquals("downloadProgress", DynamicProgressComponent.bindingVariableOf(drawn("""{"type":"Progress","bind":"@{downloadProgress}"}""") { ProgressAttributes.parse(it) }))
        assertEquals("tabIndex", DynamicSegmentComponent.bindingVariableOf(drawn("""{"type":"Segment","bind":"@{tabIndex}"}""") { SegmentAttributes.parse(it) }))
        assertEquals("volume", DynamicSliderComponent.bindingVariableOf(drawn("""{"type":"Slider","bind":"@{volume}"}""") { SliderAttributes.parse(it) }))
        assertEquals("picked", DynamicSelectBoxComponent.bindingVariableOf(drawn("""{"type":"SelectBox","items":["a","b"],"bind":"@{picked}"}""") { SelectBoxAttributes.parse(it) }))
        assertEquals("when", DynamicSelectBoxComponent.dateBindingVariableOf(drawn("""{"type":"SelectBox","selectItemType":"Date","bind":"@{when}"}""") { SelectBoxAttributes.parse(it) }))
    }

    @Test
    fun aRadiosLoneBindIsItsSelectedValue() {
        val a = drawn("""{"type":"Radio","options":["a","b"],"bind":"@{chosenPlan}"}""") { RadioAttributes.parse(it) }
        assertEquals("chosenPlan", DynamicRadioComponent.bindingVariableOf(a))
        // unfolded, the options group does not read bind
        val raw = RadioAttributes.parse(TypedAttrs.toAttrMap(node("""{"type":"Radio","options":["a","b"],"bind":"@{chosenPlan}"}""")))
        assertEquals(null, DynamicRadioComponent.bindingVariableOf(raw))
    }

    /** The component's own attribute wins over the common spelling — a static one too. */
    @Test
    fun theComponentsOwnAttributeTakesPrecedence() {
        assertEquals("ownValue", DynamicSliderComponent.bindingVariableOf(drawn("""{"type":"Slider","value":"@{ownValue}","bind":"@{commonValue}"}""") { SliderAttributes.parse(it) }))
        assertEquals("ownIndex", DynamicSegmentComponent.bindingVariableOf(drawn("""{"type":"Segment","selectedIndex":"@{ownIndex}","bind":"@{commonValue}"}""") { SegmentAttributes.parse(it) }))
        assertEquals("own", DynamicProgressComponent.bindingVariableOf(drawn("""{"type":"Progress","progress":"@{own}","bind":"@{commonValue}"}""") { ProgressAttributes.parse(it) }))
        assertEquals(null, DynamicSwitchComponent.resolveBindingVariable(drawn("""{"type":"Switch","isOn":true,"bind":"@{commonValue}"}""") { SwitchAttributes.parse(it) }))
        assertEquals(null, DynamicSliderComponent.bindingVariableOf(drawn("""{"type":"Slider","value":0.5,"bind":"@{commonValue}"}""") { SliderAttributes.parse(it) }))
    }

    /** A static `bind` is a value, not a binding, and names no data key. */
    @Test
    fun aStaticBindNamesNoVariable() {
        assertEquals(null, DynamicSliderComponent.bindingVariableOf(drawn("""{"type":"Slider","bind":"notABinding"}""") { SliderAttributes.parse(it) }))
    }
}
