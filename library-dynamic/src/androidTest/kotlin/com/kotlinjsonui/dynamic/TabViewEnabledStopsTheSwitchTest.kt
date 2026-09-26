package com.kotlinjsonui.dynamic

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A disabled TabView does not switch tabs: `enabled` is its items' own
 * parameter (NavigationBarItem's `enabled`), and each item reads disabled.
 * Only the Scaffold's semantics read it, so a tab still switched (jsonui-cli
 * docs/bugs/kjui-dynamic-components-that-skip-the-common-modifiers.md, B7;
 * the kjui codegen passes the same `enabled`).
 *
 * Two TabViews, one enabled (the control: the same tap switches it) and one
 * with `enabled: false`; the second tab of each is tapped once.
 */
@RunWith(AndroidJUnit4::class)
class TabViewEnabledStopsTheSwitchTest {

    @get:Rule
    val rule = createComposeRule()

    private fun tabs(enabled: Boolean): String =
        "{\"type\": \"TabView\", \"id\": \"tv_$enabled\", \"enabled\": $enabled, \"height\": 200, " +
            "\"tabs\": [{\"title\": \"one_$enabled\"}, {\"title\": \"two_$enabled\"}]}"

    @Test
    fun aDisabledTabViewStaysOnItsTab_andItsItemsReadDisabled() {
        val json = JsonParser.parseString(
            "{\"type\": \"View\", \"orientation\": \"vertical\", \"child\": [${tabs(true)}, ${tabs(false)}]}"
        ).asJsonObject
        rule.setContent { DynamicView(json = json, data = emptyMap()) }
        rule.waitForIdle()

        val items = rule.onAllNodes(
            SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab), useUnmergedTree = true
        )
        // Composition order: the enabled TabView's two items, then the disabled one's.
        fun selected(): List<Boolean> = (0 until 4).map {
            items[it].fetchSemanticsNode().config.getOrNull(SemanticsProperties.Selected) == true
        }
        fun disabled(): List<Boolean> = (0 until 4).map {
            items[it].fetchSemanticsNode().config.getOrNull(SemanticsProperties.Disabled) != null
        }
        assertEquals(listOf(true, false, true, false), selected())
        assertEquals(listOf(false, false, true, true), disabled())

        items[1].performClick()
        items[3].performClick()
        rule.waitForIdle()
        assertEquals("the enabled TabView switched, the disabled one did not",
            listOf(false, true, true, false), selected())
    }
}
