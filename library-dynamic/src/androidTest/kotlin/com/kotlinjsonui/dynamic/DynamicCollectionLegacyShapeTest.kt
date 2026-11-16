package com.kotlinjsonui.dynamic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
 * A Collection without `sections` — cellClasses / headerClasses /
 * footerClasses on the Collection itself — drawn route by route the way
 * sjui's codegen draws it (4f ruling, 2026-09-26). Before, it drew nothing:
 * no data source without `sections`, and headerClasses / footerClasses were
 * read into locals nothing used.
 *
 * The JVM pins the plan the routes draw from (CollectionLegacyShapeTest);
 * this pins that each route draws from it.
 */
@RunWith(AndroidJUnit4::class)
class DynamicCollectionLegacyShapeTest {

    @get:Rule
    val rule = createComposeRule()

    // wrapContent: a full-width cell in a Row would leave its neighbours no width.
    private val cell = "collection_probe_title_cell"

    private fun count(text: String): Int = rule.onAllNodesWithText(text).fetchSemanticsNodes().size

    /** What is on screen; one composition per test (setContent runs once), keyed per layout. */
    private var shown by mutableStateOf<Pair<JsonObject, Map<String, Any>>?>(null)
    private var composed = false

    private fun show(attrs: String, data: Map<String, Any>) {
        val json = JsonParser.parseString(
            """{"type": "Collection", "id": "legacy", "width": "matchParent", "height": 300 $attrs}"""
        ).asJsonObject
        if (!composed) {
            rule.setContent {
                shown?.let { (j, d) -> key(j) { DynamicView(json = j, data = d) } }
            }
            composed = true
        }
        rule.runOnIdle { shown = json to data }
        rule.waitForIdle()
    }

    private val cellClass = """, "items": "@{items}", "cellClasses": ["$cell"]"""
    private val edges = """, "headerClasses": ["collection_probe_header"], "footerClasses": ["collection_probe_footer"]"""

    private val drawingRoutes = listOf(
        "",                                             // lazy vertical, one column
        """, "columns": 2""",                           // lazy vertical grid
        """, "layout": "horizontal"""",                 // single-lane LazyRow
        """, "horizontalScroll": true""",
        """, "layout": "horizontal", "columns": 2""",   // LazyHorizontalGrid
        """, "layout": "flow"""",
        """, "lazy": "none"""",                         // Column
        """, "lazy": "eager"""",
        """, "lazy": "none", "layout": "horizontal"""", // Row
    )

    @Test
    fun aSingleCellClassDrawsEveryItemOnEveryDrawingRoute() {
        for (route in drawingRoutes) {
            show(cellClass + route, mapOf("items" to probeItems(3, cell)))
            assertEquals("row0$route", 1, count("row0"))
            assertEquals("row2$route", 1, count("row2"))
        }
    }

    /**
     * 4f ruling (2026-09-26, round 6): paging draws the class-list shape as
     * one section, a page per cell — it drew no cell (DynamicPagingSectionsTest
     * reads the pages).
     */
    @Test
    fun pagingDrawsTheCellsWithoutSections() {
        show(cellClass + """, "layout": "horizontal", "paging": true""", mapOf("items" to probeItems(3, cell)))
        assertEquals(1, count("row0"))
    }

    @Test
    fun theHeaderAndFooterAreDrawnOnTheVerticalRoutes() {
        for (route in listOf("", """, "columns": 2""", """, "lazy": "none"""")) {
            show(cellClass + edges + route, mapOf("items" to probeItems(3, cell)))
            assertEquals("header$route", 1, count("headerProbe"))
            assertEquals("footer$route", 1, count("footerProbe"))
            assertEquals("cells$route", 1, count("row1"))
        }
    }

    @Test
    fun theHorizontalFlowAndPagingRoutesDrawNoHeaderOrFooter() {
        for (route in listOf(
            """, "layout": "horizontal"""", """, "layout": "flow"""",
            """, "layout": "horizontal", "paging": true""", """, "lazy": "none", "layout": "horizontal""""
        )) {
            show(cellClass + edges + route, mapOf("items" to probeItems(3, cell)))
            assertEquals("header$route", 0, count("headerProbe"))
            assertEquals("footer$route", 0, count("footerProbe"))
        }
    }

    /** No items source: the codegen still draws the container, header and footer. */
    @Test
    fun withNoItemsTheHeaderAndFooterAreStillDrawn() {
        for (route in listOf("", """, "columns": 2""", """, "lazy": "none"""")) {
            show(edges + route, emptyMap())
            assertEquals("header$route", 1, count("headerProbe"))
            assertEquals("footer$route", 1, count("footerProbe"))
        }
    }

    /**
     * Two columns, data sections [row0] and [row1, row2]: one grid for every
     * data section (sjui's legacy grid) puts row1 beside row0 and row2 under
     * it. Control: with one column row1 is under row0, so the placement read
     * can tell the two apart.
     */
    @Test
    fun severalDataSectionsShareOneGrid() {
        val two = CollectionDataSource(
            sections = listOf(
                CollectionDataSection(cells = CollectionDataSection.CellData(cell, listOf(mapOf("title" to "row0")))),
                CollectionDataSection(
                    cells = CollectionDataSection.CellData(
                        cell, listOf(mapOf("title" to "row1"), mapOf("title" to "row2"))
                    )
                ),
            )
        )
        fun top(text: String) = rule.onAllNodesWithText(text).fetchSemanticsNodes().single().positionInRoot
        show(cellClass + """, "columns": 2""", mapOf("items" to two))
        val (r0, r1, r2) = listOf(top("row0"), top("row1"), top("row2"))
        assertTrue("row1 beside row0: $r0 $r1", abs(r1.y - r0.y) < 1f && r1.x > r0.x)
        assertTrue("row2 under row0: $r0 $r2", abs(r2.x - r0.x) < 1f && r2.y > r0.y)

        show(cellClass, mapOf("items" to two))
        val (c0, c1) = listOf(top("row0"), top("row1"))
        assertTrue("one column: row1 under row0: $c0 $c1", c1.y > c0.y)
    }
}
