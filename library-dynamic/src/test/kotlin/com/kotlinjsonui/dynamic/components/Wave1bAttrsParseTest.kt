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

    /** The warnings the generated parse says while [block] runs. */
    private fun said(block: () -> Unit): List<String> {
        val out = mutableListOf<String>()
        com.kotlinjsonui.dynamic.generated.AttrWarnings.handler = { out += it }
        try { block() } finally { com.kotlinjsonui.dynamic.generated.AttrWarnings.handler = null }
        return out
    }

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
    fun `indicator indicatorStyle matches as declared and passes unknown through`() {
        val known = IndicatorAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Indicator","indicatorStyle":"large"}"""))
        )
        assertEquals("large", TypedAttrs.enumString(known.indicatorStyle) { it.json })

        // another case is not the declared spelling (jsonui-cli 1.9.0): passed
        // through as written, and named with the declared one
        lateinit var near: IndicatorAttributes
        assertEquals(
            listOf("Indicator.indicatorStyle: unknown enum value 'Large' — did you mean 'large'?"),
            said { near = IndicatorAttributes.parse(TypedAttrs.toAttrMap(obj("""{"type":"Indicator","indicatorStyle":"Large"}"""))) },
        )
        assertEquals("Large", TypedAttrs.enumString(near.indicatorStyle) { it.json })

        // "huge" is not a declared enum value; the raw spelling passes through
        // ("small" was the example until it was declared, jsonui-cli ea985526)
        val unknown = IndicatorAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Indicator","indicatorStyle":"huge"}"""))
        )
        assertEquals("huge", TypedAttrs.enumString(unknown.indicatorStyle) { it.json })
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
    fun `gradientview gradientDirection matches as declared and passes unknown through`() {
        val known = GradientViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"GradientView","gradientDirection":"Vertical"}"""))
        )
        assertEquals("Vertical", TypedAttrs.enumString(known.gradientDirection) { it.json })
        val lower = GradientViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"GradientView","gradientDirection":"vertical"}"""))
        )
        assertEquals("vertical", TypedAttrs.enumString(lower.gradientDirection) { it.json })

        // "leftToRight" is not a declared enum value; the legacy reader honored it
        val unknown = GradientViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"GradientView","gradientDirection":"leftToRight"}"""))
        )
        assertEquals("leftToRight", TypedAttrs.enumString(unknown.gradientDirection) { it.json })
    }

    // ── Blur (both "Blur" and "BlurView" spellings parse with BlurAttributes) ──

    @Test
    fun `blur effectStyle matches declared spellings as written`() {
        val a = BlurAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Blur","effectStyle":"Light"}"""))
        )
        assertEquals("Light", TypedAttrs.enumString(a.effectStyle) { it.json })
        val lower = BlurAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Blur","effectStyle":"light"}"""))
        )
        assertEquals("light", TypedAttrs.enumString(lower.effectStyle) { it.json })
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
