package com.kotlinjsonui.dynamic

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.LocalInteractionStopped
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * DynamicView hands `userInteractionEnabled` down (InteractionMarking): a
 * node whose flag is false, or a binding that is false, provides
 * LocalInteractionStopped to what it composes, and a node composed under it
 * attaches no click. The composition half of InteractionMarkingTest (JVM):
 * an app component composed inside a stop reads the local, a Label with
 * onClick inside it has no click action, and one beside it keeps its own.
 */
@RunWith(AndroidJUnit4::class)
class DynamicViewInteractionStopTest {
    @get:Rule
    val rule = createComposeRule()

    private var saved: (@Composable (String, JsonObject, Map<String, Any>) -> Boolean)? = null
    private val seen = mutableMapOf<String, Boolean>()

    @Before
    fun save() {
        saved = Configuration.customComponentHandler
        Configuration.customComponentHandler = { type, json, _ ->
            if (type == "InteractionStopProbe") {
                seen[json.get("id").asString] = LocalInteractionStopped.current
                Text("probe")
                true
            } else {
                false
            }
        }
    }

    @After
    fun restore() { Configuration.customComponentHandler = saved }

    private fun draw(json: String, data: Map<String, Any> = emptyMap()) {
        seen.clear()
        rule.setContent { DynamicView(json = JsonParser.parseString(json).asJsonObject, data = data) }
        rule.waitForIdle()
    }

    private val probe = { id: String -> """{"type": "InteractionStopProbe", "id": "$id"}""" }
    private val hasClick = SemanticsMatcher.keyIsDefined(SemanticsActions.OnClick)
    private val hasNoClick = SemanticsMatcher.keyNotDefined(SemanticsActions.OnClick)

    @Test
    fun falseHandsTheStopDownAndTakesTheClickAway() {
        draw(
            """{"type": "View", "child": [
                {"type": "View", "userInteractionEnabled": false, "child": [
                    ${probe("in")},
                    {"type": "Label", "id": "tap_in", "text": "In", "onClick": "@{onTap}"}
                ]},
                ${probe("beside")},
                {"type": "Label", "id": "tap_beside", "text": "Beside", "onClick": "@{onTap}"}
            ]}""",
            mapOf("onTap" to {})
        )
        assertEquals(true, seen["in"])
        assertEquals(false, seen["beside"])
        rule.onNodeWithTag("tap_in", useUnmergedTree = true).assert(hasNoClick)
        rule.onNodeWithTag("tap_beside", useUnmergedTree = true).assert(hasClick)
    }

    // One composition whose data moves, as a screen's does: a test rule takes
    // setContent once per test, and the second draw() threw ("has already set
    // content") before the binding was read true.
    @Test
    fun aBindingHandsItDownWhileItIsFalse() {
        val json = JsonParser.parseString(
            """{"type": "View", "userInteractionEnabled": "@{u}", "child": [${probe("in")}]}"""
        ).asJsonObject
        var data by mutableStateOf<Map<String, Any>>(mapOf("u" to false))
        seen.clear()
        rule.setContent { DynamicView(json = json, data = data) }
        rule.waitForIdle()
        assertEquals(true, seen["in"])
        data = mapOf("u" to true)
        rule.waitForIdle()
        assertEquals(false, seen["in"])
        data = mapOf("u" to false)
        rule.waitForIdle()
        assertEquals(true, seen["in"])
    }
}
