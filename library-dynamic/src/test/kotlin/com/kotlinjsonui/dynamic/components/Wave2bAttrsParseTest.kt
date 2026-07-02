package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.CommonAttributes
import com.kotlinjsonui.dynamic.generated.IconLabelAttributes
import com.kotlinjsonui.dynamic.generated.TextViewAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Parse-level coverage for the TextView / IconLabel / Triangle wave (2b). */
class Wave2bAttrsParseTest {

    private fun obj(json: String) = JsonParser.parseString(json).asJsonObject

    // ── TextView ──

    @Test
    fun `textview two-way text binding keeps raw representation, hint and placeholder stay separate`() {
        val a = TextViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"TextView","text":"@{memo}","hint":"Memo"}"""))
        )
        assertEquals("@{memo}", TypedAttrs.rawString(a.text))
        assertEquals("Memo", a.hint)
        assertNull(a.placeholder)
    }

    @Test
    fun `textview keyboard fields parse as string, open enum and declared spelling`() {
        val a = TextViewAttributes.parse(
            TypedAttrs.toAttrMap(
                obj("""{"type":"TextView","keyboardType":"email","input":"number","returnKeyType":"Done"}""")
            )
        )
        assertEquals("email", a.keyboardType)
        assertEquals("number", TypedAttrs.enumString(a.input) { it.json })
        assertEquals("Done", TypedAttrs.enumString(a.returnKeyType) { it.json })
    }

    @Test
    fun `textview containerInset keeps both number and array shapes`() {
        val arr = TextViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"TextView","containerInset":[8,16,8,16]}"""))
        )
        assertEquals(listOf(8.0, 16.0, 8.0, 16.0), arr.containerInset)

        val single = TextViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"TextView","containerInset":12}"""))
        )
        assertEquals(12.0, single.containerInset)
    }

    // ── IconLabel ──

    @Test
    fun `iconlabel text binding and plain style fields parse`() {
        val a = IconLabelAttributes.parse(
            TypedAttrs.toAttrMap(
                obj("""{"type":"IconLabel","text":"@{label}","fontColor":"#FF0000","fontSize":18}""")
            )
        )
        assertEquals("@{label}", TypedAttrs.rawString(a.text))
        assertEquals("#FF0000", a.fontColor)
        assertEquals(18.0, a.fontSize)
    }

    @Test
    fun `iconlabel iconPosition resolves declared spellings and passes unknowns through`() {
        val known = IconLabelAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"IconLabel","iconPosition":"right"}"""))
        )
        assertEquals("right", TypedAttrs.enumString(known.iconPosition) { it.json }?.lowercase())

        val unknown = IconLabelAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"IconLabel","iconPosition":"diagonal"}"""))
        )
        assertEquals("diagonal", TypedAttrs.enumString(unknown.iconPosition) { it.json })
    }

    @Test
    fun `iconlabel legacy runtime extras are not declared`() {
        assertFalse(IconLabelAttributes.isDeclared("icon"))
        assertFalse(IconLabelAttributes.isDeclared("iconSize"))
        assertFalse(IconLabelAttributes.isDeclared("spacing"))
        assertTrue(IconLabelAttributes.isDeclared("icon_on"))
        assertTrue(IconLabelAttributes.isDeclared("iconMargin"))
    }

    // ── Triangle (CommonAttributes-level; no definitions section) ──

    @Test
    fun `triangle background keeps static and binding raw representations`() {
        val static = CommonAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Triangle","background":"#00FF00"}"""))
        )
        assertEquals("#00FF00", TypedAttrs.rawString(static.background))

        val bound = CommonAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Triangle","background":"@{fill}"}"""))
        )
        assertEquals("@{fill}", TypedAttrs.rawString(bound.background))
    }

    @Test
    fun `triangle specific keys are undeclared while dimensions stay common rows`() {
        assertFalse(CommonAttributes.isDeclared("size"))
        assertFalse(CommonAttributes.isDeclared("direction"))
        assertFalse(CommonAttributes.isDeclared("color"))
        assertTrue(CommonAttributes.isDeclared("width"))
        assertTrue(CommonAttributes.isDeclared("height"))
        assertTrue(CommonAttributes.isDeclared("background"))
    }
}
