package com.kotlinjsonui.dynamic

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `maxLength` on the Dynamic TextField and TextView, written and bound.
 *
 * The rules (attribute_definitions, TextField.maxLength): 0 or unset is no
 * limit; a limit that shrinks cuts the text at the next edit, and a change of
 * the limit alone cuts nothing. Until this neither component read maxLength,
 * written or bound.
 */
@RunWith(AndroidJUnit4::class)
class TextInputMaxLengthTest {

    @get:Rule
    val rule = createComposeRule()

    private val state = mutableStateOf<Map<String, Any>>(emptyMap())

    private fun render(type: String) {
        val write: (Map<String, Any>) -> Unit = { m -> state.value = state.value + m }
        state.value = mapOf("updateData" to write, "bound" to "", "fixed" to "", "limit" to 5)
        val json = JsonParser.parseString(
            """{"type":"View","orientation":"vertical","child":[
              {"type":"$type","id":"bound_field","width":240,"text":"@{bound}","maxLength":"@{limit}"},
              {"type":"$type","id":"fixed_field","width":240,"text":"@{fixed}","maxLength":3}
            ]}"""
        ).asJsonObject
        rule.setContent { Column { DynamicView(json = json, data = state.value) } }
        rule.waitForIdle()
    }

    private fun field(tag: String): SemanticsNodeInteraction =
        rule.onNode(hasSetTextAction() and (hasTestTag(tag) or hasAnyAncestor(hasTestTag(tag))), useUnmergedTree = true)

    private fun text(tag: String): String =
        field(tag).fetchSemanticsNode().config.getOrNull(SemanticsProperties.EditableText)?.text ?: ""

    private fun checkFamily(type: String) {
        render(type)

        // Written limit: an edit past it is cut.
        field("fixed_field").performTextReplacement("abcdef")
        rule.waitForIdle()
        assertEquals("$type written", "abc", text("fixed_field"))

        // Bound limit: an edit within it stays.
        field("bound_field").performTextReplacement("abcd")
        rule.waitForIdle()
        assertEquals("$type within the bound limit", "abcd", text("bound_field"))

        // The limit shrinks: nothing is cut until the next edit.
        state.value = state.value + ("limit" to 2)
        rule.waitForIdle()
        assertEquals("$type a change of the limit alone", "abcd", text("bound_field"))

        field("bound_field").performTextInput("x")
        rule.waitForIdle()
        assertEquals("$type the next edit", "ab", text("bound_field"))

        // 0 is no limit.
        state.value = state.value + ("limit" to 0)
        rule.waitForIdle()
        field("bound_field").performTextReplacement("abcdefghij")
        rule.waitForIdle()
        assertEquals("$type limit 0", "abcdefghij", text("bound_field"))
    }

    @Test
    fun textFieldMaxLength() = checkFamily("TextField")

    @Test
    fun textViewMaxLength() = checkFamily("TextView")
}
