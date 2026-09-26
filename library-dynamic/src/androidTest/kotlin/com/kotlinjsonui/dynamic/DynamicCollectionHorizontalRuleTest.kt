package com.kotlinjsonui.dynamic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.data.CollectionDataSection
import com.kotlinjsonui.data.CollectionDataSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

/**
 * One rule for every horizontal Collection (4f ruling, 2026-09-26): along
 * the scroll axis lineSpacing, else itemSpacing; between lanes columnSpacing,
 * else itemSpacing; and a declared section starts a new column. Before: the
 * scroll axis read lineSpacing, else columnSpacing; the lanes were 0 apart;
 * a section ran on into the previous section's part-filled column.
 *
 * The JVM pins the rule and the wiring (CollectionHorizontalRuleTest).
 */
@RunWith(AndroidJUnit4::class)
class DynamicCollectionHorizontalRuleTest {

    @get:Rule
    val rule = createComposeRule()

    private val cell = "collection_probe_title_cell"
    private var density = 1f

    private var shown by mutableStateOf<Pair<JsonObject, Map<String, Any>>?>(null)
    private var composed = false

    private fun show(sections: List<List<String>>, attrs: String) {
        val json = JsonParser.parseString(
            """{"type": "Collection", "id": "h", "width": "matchParent", "height": 200, "layout": "horizontal",
               "items": "@{items}", "sections": [${sections.joinToString(", ") { """{"cell": "$cell"}""" }}] $attrs}"""
        ).asJsonObject
        val source = CollectionDataSource(sections = sections.map { titles ->
            CollectionDataSection(cells = CollectionDataSection.CellData(cell, titles.map { mapOf("title" to it) }))
        })
        if (!composed) {
            rule.setContent {
                density = LocalDensity.current.density
                shown?.let { (j, d) -> key(j) { DynamicView(json = j, data = d) } }
            }
            composed = true
        }
        rule.runOnIdle { shown = json to mapOf("items" to source) }
        rule.waitForIdle()
    }

    private fun node(text: String): SemanticsNode = rule.onAllNodesWithText(text).fetchSemanticsNodes().single()
    private fun x(text: String) = node(text).positionInRoot.x / density
    private fun y(text: String) = node(text).positionInRoot.y / density
    private fun right(text: String) = (node(text).positionInRoot.x + node(text).size.width) / density

    @Test
    fun aSectionStartsANewColumnOnTheHorizontalGrid() {
        show(listOf(listOf("row0"), listOf("row1", "row2")), """, "columns": 2""")
        assertTrue("row1 at the top of a new column", abs(y("row1") - y("row0")) < 1f && x("row1") > x("row0"))
        assertTrue("row2 under row1", abs(x("row2") - x("row1")) < 1f && y("row2") > y("row1"))
    }

    /** Two equal lanes put row1 (H + lanes) / 2 under row0: twice the change against 0 is the lane spacing. */
    @Test
    fun theLanesAreColumnSpacingAndTheScrollAxisLineSpacing() {
        val three = listOf(listOf("row0", "row1", "row2"))
        show(three, """, "columns": 2, "lineSpacing": 12, "columnSpacing": 30""")
        val laneStep = y("row1") - y("row0")
        val alongScroll = x("row2") - right("row0")
        show(three, """, "columns": 2, "lineSpacing": 12""")
        val laneStep0 = y("row1") - y("row0")
        assertEquals("between lanes: columnSpacing", 30f, 2 * (laneStep - laneStep0), 1f)
        assertEquals("along the scroll axis: lineSpacing", 12f, alongScroll, 1f)
    }

    @Test
    fun oneLaneSpacesTheScrollAxisByTheSameRule() {
        for (route in listOf("", """, "lazy": "none"""")) {
            show(listOf(listOf("row0", "row1")), """, "lineSpacing": 12, "columnSpacing": 30""" + route)
            assertEquals("lineSpacing$route", 12f, x("row1") - right("row0"), 1f)
            show(listOf(listOf("row0", "row1")), """, "columnSpacing": 30""" + route)
            assertEquals("columnSpacing is not on the scroll axis$route", 0f, x("row1") - right("row0"), 1f)
            show(listOf(listOf("row0", "row1")), """, "itemSpacing": 20""" + route)
            assertEquals("itemSpacing stands in$route", 20f, x("row1") - right("row0"), 1f)
        }
    }
}
