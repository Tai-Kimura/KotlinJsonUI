package com.kotlinjsonui.dynamic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
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
 * Declared sections with columns > 1 are a grid each (4f ruling,
 * 2026-09-26; sjui's LazyVGrid per section, measured the same on
 * SwiftJsonUI Dynamic): section 2 starts a new row, on the lazy route and on
 * the eager / lazy:none / wrapContent route alike. Before, the lazy route ran
 * section 2 on into section 1's last row, and the other route drew one cell
 * per row. The legacy shape (no sections) stays one grid.
 *
 * The JVM pins the arithmetic (CollectionSectionGridTest); this pins the
 * placement.
 */
@RunWith(AndroidJUnit4::class)
class DynamicCollectionSectionGridTest {

    @get:Rule
    val rule = createComposeRule()

    private val cell = "collection_probe_title_cell"

    private var shown by mutableStateOf<Pair<JsonObject, Map<String, Any>>?>(null)
    private var composed = false

    private fun show(attrs: String, data: Map<String, Any>) {
        val json = JsonParser.parseString(
            """{"type": "Collection", "id": "grid", "width": "matchParent", "items": "@{items}" $attrs}"""
        ).asJsonObject
        if (!composed) {
            rule.setContent { shown?.let { (j, d) -> key(j) { DynamicView(json = j, data = d) } } }
            composed = true
        }
        rule.runOnIdle { shown = json to data }
        rule.waitForIdle()
    }

    /** Data sections [row0] and [row1, row2]. */
    private val twoSections = CollectionDataSource(
        sections = listOf(
            CollectionDataSection(cells = CollectionDataSection.CellData(cell, listOf(mapOf("title" to "row0")))),
            CollectionDataSection(
                cells = CollectionDataSection.CellData(cell, listOf(mapOf("title" to "row1"), mapOf("title" to "row2")))
            ),
        )
    )

    private fun at(text: String): Offset =
        rule.onAllNodesWithText(text).fetchSemanticsNodes().single().positionInRoot

    private val declared = """, "sections": [{"cell": "$cell"}, {"cell": "$cell"}]"""

    private val routes = listOf(
        """, "height": 300""",                    // lazy (LazyVerticalGrid)
        """, "height": 300, "lazy": "eager"""",   // Column route
        """, "lazy": "none"""",
        """, "height": "wrapContent"""",
    )

    @Test
    fun declaredSectionsAreAGridEachOnEveryRoute() {
        for (route in routes) {
            show(declared + """, "columns": 2""" + route, mapOf("items" to twoSections))
            val (r0, r1, r2) = listOf(at("row0"), at("row1"), at("row2"))
            assertTrue("row1 starts a row under row0$route: $r0 $r1", abs(r1.x - r0.x) < 1f && r1.y > r0.y)
            assertTrue("row2 beside row1$route: $r1 $r2", abs(r2.y - r1.y) < 1f && r2.x > r1.x)
        }
    }

    /** A section's own `columns` (sections[].columns) sets its grid, in a 1-column Collection. */
    @Test
    fun aSectionsOwnColumnsAreHonoured() {
        val mixed = """, "sections": [{"cell": "$cell", "columns": 1}, {"cell": "$cell", "columns": 2}]"""
        for (route in listOf(""", "height": 300""", """, "lazy": "none"""")) {
            show(mixed + route, mapOf("items" to twoSections))
            val (r1, r2) = listOf(at("row1"), at("row2"))
            assertTrue("row2 beside row1$route: $r1 $r2", abs(r2.y - r1.y) < 1f && r2.x > r1.x)
        }
    }

    /** Control: the legacy shape stays one grid across data sections (row1 beside row0). */
    @Test
    fun theLegacyShapeStaysOneGrid() {
        for (route in listOf(""", "height": 300""", """, "lazy": "none"""")) {
            show(""", "cellClasses": ["$cell"], "columns": 2""" + route, mapOf("items" to twoSections))
            val (r0, r1) = listOf(at("row0"), at("row1"))
            assertTrue("row1 beside row0$route: $r0 $r1", abs(r1.y - r0.y) < 1f && r1.x > r0.x)
        }
    }

    /** Control: one column is one cell per row. */
    @Test
    fun oneColumnIsOneCellPerRow() {
        show(declared + """, "height": 300""", mapOf("items" to twoSections))
        val (r1, r2) = listOf(at("row1"), at("row2"))
        assertTrue("row2 under row1: $r1 $r2", abs(r2.x - r1.x) < 1f && r2.y > r1.y)
        assertEquals(1, rule.onAllNodesWithText("row0").fetchSemanticsNodes().size)
    }
}
