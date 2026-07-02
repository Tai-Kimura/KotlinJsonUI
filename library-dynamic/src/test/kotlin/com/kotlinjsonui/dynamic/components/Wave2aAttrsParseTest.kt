package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.AttrValue
import com.kotlinjsonui.dynamic.generated.SegmentAttributes
import com.kotlinjsonui.dynamic.generated.SelectBoxAttributes
import com.kotlinjsonui.dynamic.generated.SliderAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Parse-level coverage for the Slider / Segment / SelectBox conversions (wave 2a). */
class Wave2aAttrsParseTest {

    private fun obj(json: String) = JsonParser.parseString(json).asJsonObject

    // ── Slider ──

    @Test
    fun `slider minimumValue alias resolves into minimum on L0 and is skipped when canonicalOnly`() {
        val map = TypedAttrs.toAttrMap(obj("""{"type":"Slider","minimumValue":10}"""))

        val l0 = SliderAttributes.parse(map)
        assertEquals(AttrValue.Value(10.0), l0.minimum)

        val l1 = SliderAttributes.parse(map, canonicalOnly = true)
        assertNull(l1.minimum)
    }

    @Test
    fun `slider canonical minimum wins over the alias spellings`() {
        val a = SliderAttributes.parse(
            TypedAttrs.toAttrMap(
                obj("""{"type":"Slider","minimum":5,"minimumValue":10,"minValue":20}""")
            )
        )
        assertEquals(AttrValue.Value(5.0), a.minimum)
        // The standalone declared row keeps its own value.
        assertEquals(AttrValue.Value(20.0), a.minValue)
    }

    @Test
    fun `slider onValueChanged alias resolves into onValueChange and value binding exposes the expression`() {
        val map = TypedAttrs.toAttrMap(
            obj("""{"type":"Slider","value":"@{volume}","onValueChanged":"@{onSlide}"}""")
        )

        val l0 = SliderAttributes.parse(map)
        assertEquals("volume", TypedAttrs.binding(l0.value))
        assertEquals("@{onSlide}", TypedAttrs.raw(l0.onValueChange))

        val l1 = SliderAttributes.parse(map, canonicalOnly = true)
        assertNull(l1.onValueChange)
    }

    // ── Segment ──

    @Test
    fun `segment selectedIndex parses static number and binding expression`() {
        val static = SegmentAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Segment","selectedIndex":2}"""))
        )
        assertEquals(2, TypedAttrs.int(static.selectedIndex, emptyMap()))

        val bound = SegmentAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Segment","selectedIndex":"@{tab}"}"""))
        )
        assertEquals("tab", TypedAttrs.binding(bound.selectedIndex))
    }

    @Test
    fun `segment items array parses as declared list`() {
        val a = SegmentAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Segment","items":["A","B"]}"""))
        )
        assertEquals(listOf("A", "B"), a.items)
    }

    @Test
    fun `segment color rows keep binding representation and tintColor stays plain`() {
        val a = SegmentAttributes.parse(
            TypedAttrs.toAttrMap(
                obj("""{"type":"Segment","normalColor":"@{nc}","selectedColor":"#FF0000","tintColor":"#00FF00"}""")
            )
        )
        assertEquals("@{nc}", TypedAttrs.rawString(a.normalColor))
        assertEquals("#FF0000", TypedAttrs.rawString(a.selectedColor))
        assertEquals("#00FF00", a.tintColor)
    }

    // ── SelectBox ──

    @Test
    fun `selectbox selectItemType resolves to the declared spelling case-insensitively`() {
        val canonical = SelectBoxAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"SelectBox","selectItemType":"Date"}"""))
        )
        assertEquals("Date", TypedAttrs.enumString(canonical.selectItemType) { it.json })

        val lower = SelectBoxAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"SelectBox","selectItemType":"date"}"""))
        )
        assertEquals("Date", TypedAttrs.enumString(lower.selectItemType) { it.json })
    }

    @Test
    fun `selectbox prompt, hint and placeholder are independent declared rows`() {
        val a = SelectBoxAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"SelectBox","hint":"Choose"}"""))
        )
        assertNull(a.prompt)
        assertEquals("Choose", a.hint)
        assertNull(a.placeholder)
    }

    @Test
    fun `selectbox date picker fields parse typed`() {
        val a = SelectBoxAttributes.parse(
            TypedAttrs.toAttrMap(
                obj(
                    """{"type":"SelectBox","selectedDate":"@{birthday}",""" +
                        """"dateStringFormat":"yyyy/MM/dd","minuteInterval":5,"minimumDate":"2020-01-01"}"""
                )
            )
        )
        assertEquals("birthday", TypedAttrs.binding(a.selectedDate))
        assertEquals("yyyy/MM/dd", a.dateStringFormat)
        assertEquals(5, a.minuteInterval?.toInt())
        assertEquals("2020-01-01", TypedAttrs.rawString(a.minimumDate))
    }
}
