package com.kotlinjsonui.dynamic

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A Segment's tabs carry `<id>_tab_<n>` on Dynamic, the testTag the Android
 * driver's selectTab waits for, as kjui codegen's tabs do (jsonui-cli
 * 11dfe22c; ticket jui-segment-tabs-carry-no-tab-ids-so-selecttab-cannot-
 * reach-them). A tap on the tagged tab selects it.
 */
@RunWith(AndroidJUnit4::class)
class DynamicSegmentTabTagsTest {
    @get:Rule val rule = createComposeRule()

    private fun show(segment: String, data: Map<String, Any> = emptyMap()) {
        rule.setContent {
            DynamicView(json = JsonParser.parseString("""{"type":"View","child":[$segment]}""").asJsonObject, data = data)
        }
        rule.waitForIdle()
    }

    @Test
    fun eachTabCarriesItsTagAndATapOnItSelects() {
        val calls = mutableListOf<String>()
        val h: (Any) -> Unit = { calls += it.toString() }
        show("""{"type":"Segment","id":"plan","items":["a","b","c"],"selectedIndex":0,"onValueChange":"@{h}"}""", mapOf("h" to h))
        listOf(0, 1, 2).forEach { rule.onAllNodesWithTag("plan_tab_$it").fetchSemanticsNodes().let { n -> assertEquals("plan_tab_$it", 1, n.size) } }
        rule.onNodeWithTag("plan_tab_2").performClick()
        rule.waitForIdle()
        rule.onNodeWithTag("plan_tab_2").assertIsSelected()
        assertEquals(listOf("2"), calls)
    }

    @Test
    fun aSegmentWithNoIdTagsNoTab() {
        show("""{"type":"Segment","items":["a","b"],"selectedIndex":0}""")
        assertEquals(0, rule.onAllNodesWithTag("null_tab_0").fetchSemanticsNodes().size)
        assertEquals(0, rule.onAllNodesWithTag("_tab_0").fetchSemanticsNodes().size)
    }
}
