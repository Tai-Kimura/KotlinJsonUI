package com.kotlinjsonui.dynamic

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The composed half of HandlersCalledAsDeclaredTest (JVM): an id-less node's
 * `(String)` handler is called with its position name, on a tap and on
 * onAppear; onAppear is called on an `invisible` view and not on a `gone`
 * one (DynamicView applies it inside the visibility wrapper); the entries of
 * DynamicViews are named apart (`0_<i>`; they were all `0`).
 */
@RunWith(AndroidJUnit4::class)
class HandlersCalledAsDeclaredDeviceTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun anIdLessViewsTapHandsItsPositionName() {
        val ids = mutableListOf<String>()
        val json = JsonParser.parseString(
            """{"type": "View", "child": [{"type": "Label", "text": "first"},
               {"type": "View", "onClick": "@{tapped}", "child": [{"type": "Label", "text": "tap me"}]}]}"""
        ).asJsonObject
        rule.setContent { DynamicView(json = json, data = mapOf("tapped" to { id: String -> ids += id })) }
        rule.onNode(hasText("tap me")).performClick()
        rule.waitForIdle()
        assertEquals(listOf("view_0_1"), ids)
    }

    @Test
    fun onAppearIsCalledAsDeclared_onAnInvisibleView_andNotOnAGoneOne() {
        val appeared = mutableListOf<String>()
        var bare = 0
        val json = JsonParser.parseString(
            """{"type": "View", "child": [
                 {"type": "Label", "text": "a", "onAppear": "@{seen}"},
                 {"type": "Button", "text": "b", "onAppear": "seen:"},
                 {"type": "Switch", "onAppear": "counted"},
                 {"type": "Label", "text": "c", "visibility": "invisible", "onAppear": "@{seen}"},
                 {"type": "Label", "text": "d", "visibility": "gone", "onAppear": "@{seen}"}
               ]}"""
        ).asJsonObject
        rule.setContent {
            DynamicView(json = json, data = mapOf("seen" to { id: String -> appeared += id }, "counted" to { bare += 1 }))
        }
        rule.waitForIdle()
        assertEquals(listOf("button_0_1", "label_0_0", "label_0_3"), appeared.sorted())
        assertEquals(1, bare)
    }

    @Test
    fun theEntriesOfAListOfRootsAreNamedApart() {
        val ids = mutableListOf<String>()
        val entries = listOf(
            JsonParser.parseString("""{"type": "Switch", "testTag": "a", "onValueChange": "@{changed}"}""").asJsonObject,
            JsonParser.parseString("""{"type": "Switch", "testTag": "b", "onValueChange": "@{changed}"}""").asJsonObject,
        )
        // In a Column, the container DynamicViews draws a list of roots into.
        // Drawn straight into the test's content the two Switches lay on the
        // same bounds (measured: both (0, 16, 104, 80)), so a touch on the
        // first reached the second, drawn on top — [switch_0_1, switch_0_1].
        rule.setContent {
            Column {
                DynamicViews(components = entries, data = mapOf("changed" to { id: String, _: Boolean -> ids += id }))
            }
        }
        val bounds = rule.onAllNodes(isToggleable(), useUnmergedTree = true).fetchSemanticsNodes().map { it.boundsInRoot }
        assertEquals("two Switches, apart", false, bounds[0].overlaps(bounds[1]))
        rule.onAllNodes(isToggleable(), useUnmergedTree = true)[0].performClick()
        rule.onAllNodes(isToggleable(), useUnmergedTree = true)[1].performClick()
        rule.waitForIdle()
        assertEquals(listOf("switch_0_0", "switch_0_1"), ids)
        // the caller's objects were not stamped
        assertEquals(false, entries.any { it.has("_layoutPath") })
    }
}
