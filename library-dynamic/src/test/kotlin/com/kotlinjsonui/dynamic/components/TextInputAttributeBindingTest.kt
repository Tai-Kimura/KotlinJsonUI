package com.kotlinjsonui.dynamic.components

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.AttrValue
import com.kotlinjsonui.dynamic.generated.TextFieldAttributes
import com.kotlinjsonui.dynamic.generated.TextViewAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * TextField's and TextView's input attributes take a binding: a bound value
 * resolves through the data map and reads through the same table as a written
 * one, so a value outside the table falls where an unknown written value falls.
 * Until this they parsed as plain enums, and a `"@{...}"` was an unknown value
 * that always reached the fallback.
 */
class TextInputAttributeBindingTest {

    private fun field(json: String) =
        TextFieldAttributes.parse(TypedAttrs.toAttrMap(JsonParser.parseString(json).asJsonObject))

    private fun view(json: String) =
        TextViewAttributes.parse(TypedAttrs.toAttrMap(JsonParser.parseString(json).asJsonObject))

    private fun fieldOptions(json: String, data: Map<String, Any>, nextFocus: String? = null): KeyboardOptions =
        DynamicTextFieldComponent.buildKeyboardOptions(field(json), data, nextFocus)

    private fun viewOptions(json: String, data: Map<String, Any>): KeyboardOptions =
        DynamicTextViewComponent.buildKeyboardOptions(view(json), data)

    @Test
    fun `the rows parse as a binding`() {
        val a = field("""{"type":"TextField","input":"@{kind}","maxLength":"@{limit}","returnKeyType":"@{key}",
            "autocapitalizationType":"@{cap}","autocorrectionType":"@{correct}","editable":"@{canEdit}"}""")
        for (v in listOf(a.input, a.maxLength, a.returnKeyType, a.autocapitalizationType, a.autocorrectionType, a.editable)) {
            assertTrue(v.toString(), v is AttrValue.Binding)
        }
        val t = view("""{"type":"TextView","input":"@{kind}","keyboardType":"@{kb}","maxLength":"@{limit}",
            "returnKeyType":"@{key}","editable":"@{canEdit}"}""")
        for (v in listOf(t.input, t.keyboardType, t.maxLength, t.returnKeyType, t.editable)) {
            assertTrue(v.toString(), v is AttrValue.Binding)
        }
    }

    @Test
    fun `a bound input picks the keyboard the written value picks`() {
        val json = """{"type":"TextField","input":"@{kind}"}"""
        for ((value, keyboard) in listOf(
            "number" to KeyboardType.Number, "email" to KeyboardType.Email,
            "phone" to KeyboardType.Phone, "url" to KeyboardType.Uri, "alphabet" to KeyboardType.Text
        )) {
            assertEquals(value, keyboard, fieldOptions(json, mapOf("kind" to value)).keyboardType)
            assertEquals(value, keyboard,
                fieldOptions("""{"type":"TextField","input":"$value"}""", emptyMap()).keyboardType)
        }
        assertEquals("unknown", KeyboardType.Text, fieldOptions(json, mapOf("kind" to "nonsense")).keyboardType)
        assertEquals("unbound", KeyboardType.Text, fieldOptions(json, emptyMap()).keyboardType)
    }

    @Test
    fun `a bound input that names no keyboard leaves it to contentType, as the written default does`() {
        val json = """{"type":"TextField","input":"@{kind}","contentType":"tel"}"""
        assertEquals(KeyboardType.Phone, fieldOptions(json, mapOf("kind" to "default")).keyboardType)
        assertEquals(KeyboardType.Number, fieldOptions(json, mapOf("kind" to "number")).keyboardType)
    }

    @Test
    fun `a bound returnKeyType reads the written table`() {
        val json = """{"type":"TextField","returnKeyType":"@{key}"}"""
        assertEquals(ImeAction.Done, fieldOptions(json, mapOf("key" to "Done")).imeAction)
        assertEquals(ImeAction.Search, fieldOptions(json, mapOf("key" to "Search")).imeAction)
        assertEquals("outside the table", ImeAction.Default, fieldOptions(json, mapOf("key" to "Return")).imeAction)
        assertEquals(ImeAction.Default,
            fieldOptions("""{"type":"TextField","returnKeyType":"Return"}""", emptyMap()).imeAction)
    }

    @Test
    fun `a bound autocapitalizationType reads the written table`() {
        val json = """{"type":"TextField","autocapitalizationType":"@{cap}"}"""
        assertEquals(KeyboardCapitalization.Words, fieldOptions(json, mapOf("cap" to "words")).capitalization)
        assertEquals(KeyboardCapitalization.Characters, fieldOptions(json, mapOf("cap" to "allCharacters")).capitalization)
        assertEquals(KeyboardCapitalization.Characters, fieldOptions(json, mapOf("cap" to "characters")).capitalization)
        assertEquals("unknown", KeyboardCapitalization.None, fieldOptions(json, mapOf("cap" to "nonsense")).capitalization)
    }

    @Test
    fun `a bound autocorrectionType reads the written table`() {
        val json = """{"type":"TextField","autocorrectionType":"@{correct}"}"""
        for (off in listOf("no", "off", "false")) {
            assertEquals(off, false, fieldOptions(json, mapOf("correct" to off)).autoCorrectEnabled)
        }
        for (on in listOf("yes", "on", "true", "default")) {
            assertEquals(on, true, fieldOptions(json, mapOf("correct" to on)).autoCorrectEnabled)
        }
        assertEquals(false,
            fieldOptions("""{"type":"TextField","autocorrectionType":"no"}""", emptyMap()).autoCorrectEnabled)
    }

    @Test
    fun `a bound TextView keyboardType and input read the written table`() {
        assertEquals(KeyboardType.Email,
            viewOptions("""{"type":"TextView","keyboardType":"@{kb}"}""", mapOf("kb" to "email")).keyboardType)
        assertEquals(KeyboardType.Number,
            viewOptions("""{"type":"TextView","input":"@{kind}"}""", mapOf("kind" to "number")).keyboardType)
        assertEquals(KeyboardType.Text,
            viewOptions("""{"type":"TextView","input":"@{kind}"}""", mapOf("kind" to "nonsense")).keyboardType)
        val key = """{"type":"TextView","returnKeyType":"@{key}"}"""
        assertEquals(ImeAction.Done, viewOptions(key, mapOf("key" to "Done")).imeAction)
        assertEquals(ImeAction.Next, viewOptions(key, mapOf("key" to "Next")).imeAction)
        assertEquals("outside the table", ImeAction.Default, viewOptions(key, mapOf("key" to "Search")).imeAction)
    }

    @Test
    fun `maxLength reads written and bound, and nothing is no limit`() {
        assertEquals(5, TypedAttrs.int(field("""{"type":"TextField","maxLength":5}""").maxLength, emptyMap()))
        assertEquals(7, TypedAttrs.int(field("""{"type":"TextField","maxLength":"@{limit}"}""").maxLength, mapOf("limit" to 7)))
        assertNull(TypedAttrs.int(field("""{"type":"TextField","maxLength":"@{limit}"}""").maxLength, emptyMap()))
        assertNull(TypedAttrs.int(field("""{"type":"TextField"}""").maxLength, emptyMap()))
    }
}
