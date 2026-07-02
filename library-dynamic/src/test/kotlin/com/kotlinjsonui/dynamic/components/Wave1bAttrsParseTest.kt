package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.BlurAttributes
import com.kotlinjsonui.dynamic.generated.CircleViewAttributes
import com.kotlinjsonui.dynamic.generated.GradientViewAttributes
import com.kotlinjsonui.dynamic.generated.IndicatorAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Parse-level coverage for the Indicator / CircleView / GradientView / Blur wave (1b). */
class Wave1bAttrsParseTest {

    private fun obj(json: String) = JsonParser.parseString(json).asJsonObject

    // ── Indicator ──

    @Test
    fun `indicator static color and hidesWhenStopped parse to typed values`() {
        val a = IndicatorAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Indicator","color":"#FF0000","hidesWhenStopped":true}"""))
        )
        assertEquals("#FF0000", TypedAttrs.rawString(a.color))
        assertEquals(true, a.hidesWhenStopped)
    }

    @Test
    fun `indicator color binding keeps the raw representation and expression`() {
        val a = IndicatorAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Indicator","color":"@{spinnerColor}"}"""))
        )
        assertEquals("@{spinnerColor}", TypedAttrs.rawString(a.color))
        assertEquals("spinnerColor", TypedAttrs.binding(a.color))
    }

    @Test
    fun `indicator indicatorStyle matches case-insensitively and passes unknown through`() {
        val known = IndicatorAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Indicator","indicatorStyle":"Large"}"""))
        )
        assertEquals("large", TypedAttrs.enumString(known.indicatorStyle) { it.json })

        // "small" is not a declared enum value; the raw spelling passes through
        val unknown = IndicatorAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Indicator","indicatorStyle":"small"}"""))
        )
        assertEquals("small", TypedAttrs.enumString(unknown.indicatorStyle) { it.json })
    }

    // ── CircleView ──

    @Test
    fun `circleview child array parses to a typed list`() {
        val a = CircleViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"CircleView","child":[{"type":"Label","text":"A"}]}"""))
        )
        assertEquals(1, a.child?.size)
        assertNull(a.children)
    }

    @Test
    fun `circleview border trio parses through the common rows`() {
        val a = CircleViewAttributes.parse(
            TypedAttrs.toAttrMap(
                obj("""{"type":"CircleView","borderWidth":2,"borderStyle":"dashed","borderColor":"#000000"}""")
            )
        )
        assertEquals(2f, TypedAttrs.float(a.common.borderWidth, emptyMap()))
        assertEquals("dashed", TypedAttrs.enumString(a.common.borderStyle) { it.json })
        assertEquals("#000000", TypedAttrs.rawString(a.common.borderColor))
    }

    @Test
    fun `circleview background binding keeps the raw representation`() {
        val a = CircleViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"CircleView","background":"@{fill}"}"""))
        )
        assertEquals("@{fill}", TypedAttrs.rawString(a.common.background))
        assertEquals("fill", TypedAttrs.binding(a.common.background))
    }

    // ── GradientView ──

    @Test
    fun `gradientview gradient color array parses to a typed list`() {
        val a = GradientViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"GradientView","gradient":["#FF0000","#00FF00"]}"""))
        )
        assertEquals(listOf("#FF0000", "#00FF00"), a.gradient)
    }

    @Test
    fun `gradientview locations parse as numeric stops`() {
        val a = GradientViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"GradientView","locations":[0.0,0.5,1.0]}"""))
        )
        assertEquals(listOf(0.0, 0.5, 1.0), a.locations)
    }

    @Test
    fun `gradientview gradientDirection matches case-insensitively and passes unknown through`() {
        val known = GradientViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"GradientView","gradientDirection":"vertical"}"""))
        )
        assertEquals("Vertical", TypedAttrs.enumString(known.gradientDirection) { it.json })

        // "leftToRight" is not a declared enum value; the legacy reader honored it
        val unknown = GradientViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"GradientView","gradientDirection":"leftToRight"}"""))
        )
        assertEquals("leftToRight", TypedAttrs.enumString(unknown.gradientDirection) { it.json })
    }

    // ── Blur (both "Blur" and "BlurView" spellings parse with BlurAttributes) ──

    @Test
    fun `blur effectStyle matches declared spellings case-insensitively`() {
        val a = BlurAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Blur","effectStyle":"light"}"""))
        )
        assertEquals("Light", TypedAttrs.enumString(a.effectStyle) { it.json })
        // the component lowercases before matching its overlay table
        assertEquals("light", TypedAttrs.enumString(a.effectStyle) { it.json }?.lowercase())
    }

    @Test
    fun `blur unknown effectStyle passes the raw spelling through`() {
        val a = BlurAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Blur","effectStyle":"prominent"}"""))
        )
        assertEquals("prominent", TypedAttrs.enumString(a.effectStyle) { it.json })
    }

    @Test
    fun `blurview spelling parses common background and opacity binding`() {
        val a = BlurAttributes.parse(
            TypedAttrs.toAttrMap(
                obj("""{"type":"BlurView","background":"#00000080","opacity":"@{fade}"}""")
            )
        )
        assertEquals("#00000080", TypedAttrs.rawString(a.common.background))
        assertEquals(0.5f, TypedAttrs.float(a.common.opacity, mapOf("fade" to 0.5)))
    }
}
