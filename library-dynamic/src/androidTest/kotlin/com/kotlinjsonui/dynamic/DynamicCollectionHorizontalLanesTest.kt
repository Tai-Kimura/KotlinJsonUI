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
 * Eager and lazy:none horizontal Collections draw lanes (4f ruling,
 * 2026-09-26), as the lazy route's LazyHorizontalGrid does: `columns` lanes
 * (a section's own `columns` for its block), cells top to bottom then the
 * next column, each section a new column, lanes spaced by columnSpacing and
 * the scroll axis by lineSpacing. Before, they drew one Row.
 *
 * The JVM pins the placement arithmetic (CollectionHorizontalLanesTest).
 */
@RunWith(AndroidJUnit4::class)
class DynamicCollectionHorizontalLanesTest {

    @get:Rule
    val rule = createComposeRule()

    private val cell = "collection_probe_title_cell"
    private var density = 1f

    private var shown by mutableStateOf<Pair<JsonObject, Map<String, Any>>?>(null)
    private var composed = false

    private fun show(sections: List<List<String>>, attrs: String, sectionColumns: List<Int?>? = null) {
        val configs = sections.indices.joinToString(", ") { i ->
            sectionColumns?.get(i)?.let { """{"cell": "$cell", "columns": $it}""" } ?: """{"cell": "$cell"}"""
        }
        val json = JsonParser.parseString(
            """{"type": "Collection", "id": "lanes", "width": "matchParent", "height": 200, "layout": "horizontal",
               "items": "@{items}", "sections": [$configs] $attrs}"""
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

    private val routes = listOf(""", "lazy": "eager"""", """, "lazy": "none"""")

    @Test
    fun columnsAreLanesFilledColumnByColumn() {
        for (route in routes) {
            show(listOf(listOf("row0", "row1", "row2")), """, "columns": 2""" + route)
            assertTrue("row1 under row0$route", abs(x("row1") - x("row0")) < 1f && y("row1") > y("row0"))
            assertTrue("row2 at the top of the next column$route", abs(y("row2") - y("row0")) < 1f && x("row2") > x("row0"))
        }
    }

    @Test
    fun eachSectionStartsANewColumn() {
        for (route in routes) {
            show(listOf(listOf("row0"), listOf("row1", "row2")), """, "columns": 2""" + route)
            assertTrue("row1 at the top of a new column$route", abs(y("row1") - y("row0")) < 1f && x("row1") > x("row0"))
            assertTrue("row2 under row1$route", abs(x("row2") - x("row1")) < 1f && y("row2") > y("row1"))
        }
    }

    /** In a 1-lane Collection a section's own `columns` gives its block lanes. */
    @Test
    fun aSectionsOwnColumnsAreItsBlocksLanes() {
        for (route in routes) {
            show(listOf(listOf("row0"), listOf("row1", "row2")), route, sectionColumns = listOf(null, 2))
            assertTrue("row2 under row1$route", abs(x("row2") - x("row1")) < 1f && y("row2") > y("row1"))
        }
    }

    /** Two equal lanes in 200dp put row1 (200 + lanes) / 2 under row0; the scroll axis is right(row0) to row2. */
    @Test
    fun lanesByColumnSpacingAndTheScrollAxisByLineSpacing() {
        for (route in routes) {
            val three = listOf(listOf("row0", "row1", "row2"))
            show(three, """, "columns": 2, "lineSpacing": 12, "columnSpacing": 30""" + route)
            val laneStep = y("row1") - y("row0")
            val alongScroll = x("row2") - right("row0")
            show(three, """, "columns": 2, "lineSpacing": 12""" + route)
            val laneStep0 = y("row1") - y("row0")
            assertEquals("between lanes: columnSpacing$route", 30f, 2 * (laneStep - laneStep0), 1f)
            assertEquals("along the scroll axis: lineSpacing$route", 12f, alongScroll, 1f)
        }
    }

    /** Control: one lane is the Row as before, the cells side by side. */
    @Test
    fun oneLaneIsTheRow() {
        for (route in routes) {
            show(listOf(listOf("row0", "row1")), """, "lineSpacing": 12""" + route)
            assertEquals("side by side$route", y("row0"), y("row1"), 1f)
            assertEquals("lineSpacing apart$route", 12f, x("row1") - right("row0"), 1f)
        }
    }
}
