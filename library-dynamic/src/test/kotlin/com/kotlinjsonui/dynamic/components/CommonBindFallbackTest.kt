package com.kotlinjsonui.dynamic.components

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.ProgressAttributes
import com.kotlinjsonui.dynamic.generated.RadioAttributes
import com.kotlinjsonui.dynamic.generated.SegmentAttributes
import com.kotlinjsonui.dynamic.generated.SelectBoxAttributes
import com.kotlinjsonui.dynamic.generated.SliderAttributes
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * `bind` is the common two-way spelling for a component's primary value, and
 * its KDoc names exactly these components. It never worked on any of them:
 * the row holds an `AttrValue<Any>`, and every one of these call sites read
 * it with `a.common.bind as? String`, a cast that cannot match — so the
 * fallback returned null and a layout that declared only `bind` silently
 * bound to nothing. Kotlin 2.4 called the cast impossible; before that the
 * branch was just dead.
 *
 * CheckBox and Switch escaped because they read the same row through
 * `TypedAttrs.raw(...)` first, which is the shape these now use.
 */
class CommonBindFallbackTest {

    private inline fun <reified T> parse(json: String, parser: (Map<String, Any?>) -> T): T =
        parser(TypedAttrs.toAttrMap(Gson().fromJson(json, JsonObject::class.java)))

    @Test
    fun progressResolvesTheCommonBindRow() {
        val a = parse("""{"type":"Progress","bind":"@{downloadProgress}"}""") { ProgressAttributes.parse(it) }
        assertEquals("downloadProgress", DynamicProgressComponent.bindingVariableOf(a))
    }

    @Test
    fun radioResolvesTheCommonBindRow() {
        val a = parse("""{"type":"Radio","bind":"@{chosenPlan}"}""") { RadioAttributes.parse(it) }
        assertEquals("chosenPlan", DynamicRadioComponent.bindingVariableOf(a))
    }

    @Test
    fun segmentResolvesTheCommonBindRow() {
        val a = parse("""{"type":"Segment","bind":"@{tabIndex}"}""") { SegmentAttributes.parse(it) }
        assertEquals("tabIndex", DynamicSegmentComponent.bindingVariableOf(a))
    }

    @Test
    fun sliderResolvesTheCommonBindRow() {
        val a = parse("""{"type":"Slider","bind":"@{volume}"}""") { SliderAttributes.parse(it) }
        assertEquals("volume", DynamicSliderComponent.bindingVariableOf(a))
    }

    @Test
    fun selectBoxResolvesTheCommonBindRow() {
        val a = parse("""{"type":"SelectBox","items":["a","b"],"bind":"@{picked}"}""") { SelectBoxAttributes.parse(it) }
        assertEquals("picked", DynamicSelectBoxComponent.bindingVariableOf(a))
    }

    @Test
    fun selectBoxDateVariantResolvesTheCommonBindRow() {
        val a = parse("""{"type":"SelectBox","selectBoxType":"date","bind":"@{when}"}""") { SelectBoxAttributes.parse(it) }
        assertEquals("when", DynamicSelectBoxComponent.dateBindingVariableOf(a))
    }

    /** The component's own attribute still wins over the common spelling. */
    @Test
    fun theComponentsOwnAttributeTakesPrecedence() {
        val slider = parse("""{"type":"Slider","value":"@{ownValue}","bind":"@{commonValue}"}""") {
            SliderAttributes.parse(it)
        }
        assertEquals("ownValue", DynamicSliderComponent.bindingVariableOf(slider))

        val segment = parse("""{"type":"Segment","selectedIndex":"@{ownIndex}","bind":"@{commonValue}"}""") {
            SegmentAttributes.parse(it)
        }
        assertEquals("ownIndex", DynamicSegmentComponent.bindingVariableOf(segment))
    }

    /** A static `bind` is a value, not a binding, and names no data key. */
    @Test
    fun aStaticBindNamesNoVariable() {
        val a = parse("""{"type":"Slider","bind":"notABinding"}""") { SliderAttributes.parse(it) }
        assertEquals(null, DynamicSliderComponent.bindingVariableOf(a))
    }
}
