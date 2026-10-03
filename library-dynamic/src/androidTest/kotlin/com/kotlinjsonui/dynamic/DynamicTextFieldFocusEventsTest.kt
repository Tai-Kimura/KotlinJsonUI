package com.kotlinjsonui.dynamic

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Collections

/**
 * A TextField's onFocus / onBlur / onBeginEditing / onEndEditing are called
 * on Dynamic as kjui codegen calls them (jsonui-cli ticket kjui-dynamic-
 * textfield-focus-events-are-never-called): they were watched only when the
 * undeclared `fieldId` was set, so a field as normally written called none
 * (0 in all 24 census cases on conf_ci).
 */
@RunWith(AndroidJUnit4::class)
class DynamicTextFieldFocusEventsTest {
    @get:Rule val rule = createComposeRule()

    private fun run(firstExtra: String): List<String> {
        val log = Collections.synchronizedList(mutableListOf<String>())
        val handlers: Map<String, Any> = listOf("f", "b", "be", "ee").associateWith { n -> { log += n; Unit } as () -> Unit }
        rule.setContent {
            DynamicView(
                json = JsonParser.parseString(
                    """{"type":"View","orientation":"vertical","child":[
                      {"type":"TextField","id":"t1","width":200,"height":60,
                       "onFocus":"@{f}","onBlur":"@{b}","onBeginEditing":"@{be}","onEndEditing":"@{ee}"$firstExtra},
                      {"type":"TextField","id":"t2","width":200,"height":60}]}"""
                ).asJsonObject,
                data = handlers
            )
        }
        rule.waitForIdle()
        rule.onNodeWithTag("t1").performClick(); rule.waitForIdle()
        val focused = log.sorted()
        rule.onNodeWithTag("t2").performClick(); rule.waitForIdle()
        return focused + listOf("|") + log.drop(focused.size).sorted()
    }

    @Test
    fun focusThenBlurCallsEachHandlerOnce() {
        assertEquals(listOf("be", "f", "|", "b", "ee"), run(""))
    }

    @Test
    fun aFieldWithAFieldIdCallsEachOnceToo() {
        // The legacy focus chain's field: it watched focus itself, and must
        // not call the handlers a second time.
        assertEquals(listOf("be", "f", "|", "b", "ee"), run(""","fieldId":"one""""))
    }
}
