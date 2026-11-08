package com.kotlinjsonui.dynamic.components

import androidx.compose.ui.text.input.KeyboardType
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.TextFieldAttributes
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The keyboard comes from `input`, and from `contentType` only when `input`
 * names none — the order iOS and the web keep (they read `input` alone).
 *
 * Through 2.50.0 contentType came first, and a value with no keyboard
 * (username, usernameWebauthn, name, ...) turned `input: email` into the plain
 * keyboard: a login field marked for passkey autofill lost its @ key on
 * Android only.
 */
class TextFieldKeyboardInputFirstTest {

    private fun keyboard(json: String): KeyboardType =
        DynamicTextFieldComponent.resolveKeyboardType(
            TextFieldAttributes.parse(TypedAttrs.toAttrMap(JsonParser.parseString(json).asJsonObject))
        )

    @Test
    fun `input email keeps the email keyboard under a contentType with no keyboard`() {
        for (ct in listOf("username", "usernameWebauthn", "name", "givenName", "familyName",
                          "oneTimeCode", "streetAddress", "country")) {
            assertEquals(ct, KeyboardType.Email,
                keyboard("""{"type":"TextField","input":"email","contentType":"$ct"}"""))
        }
    }

    @Test
    fun `input wins over a contentType that names another keyboard`() {
        assertEquals(KeyboardType.Number,
            keyboard("""{"type":"TextField","input":"number","contentType":"email"}"""))
        assertEquals(KeyboardType.Email,
            keyboard("""{"type":"TextField","input":"email","contentType":"URL"}"""))
    }

    @Test
    fun `contentType picks the keyboard when input is absent or default`() {
        assertEquals(KeyboardType.Email, keyboard("""{"type":"TextField","contentType":"email"}"""))
        assertEquals(KeyboardType.Phone, keyboard("""{"type":"TextField","input":"default","contentType":"tel"}"""))
        assertEquals(KeyboardType.Number, keyboard("""{"type":"TextField","contentType":"postalCode"}"""))
    }

    @Test
    fun `neither naming a keyboard is the plain keyboard`() {
        assertEquals(KeyboardType.Text, keyboard("""{"type":"TextField","contentType":"usernameWebauthn"}"""))
        assertEquals(KeyboardType.Text, keyboard("""{"type":"TextField"}"""))
        assertEquals(KeyboardType.Uri, keyboard("""{"type":"TextField","input":"url"}"""))
    }
}
