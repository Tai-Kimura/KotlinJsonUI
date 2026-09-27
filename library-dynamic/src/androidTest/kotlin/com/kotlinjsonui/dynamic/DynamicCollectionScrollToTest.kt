package com.kotlinjsonui.dynamic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.data.CollectionDataSection
import com.kotlinjsonui.data.CollectionDataSource
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * What a Collection's `scrollTo` names across sections (4f ruling
 * 2026-09-27; jsonui-cli 1.9.0, the SSoT's Collection.scrollTo), drawn: the
 * declared plain value, an Int counted in cells — headers, footers and the
 * grid's row breaks not counted — and a String, with cellIdProperty, the
 * first cell in section order whose key it is.
 *
 * Until 1.9.0 this renderer read only a `SharedFlow<Int>`, so the declared
 * plain value did not scroll at all (and a flow's value was the lazy item
 * index). The JVM pins the arithmetic (CollectionScrollToTest); this pins
 * that the list moves to the cell.
 *
 * Each list is at the top of the composition, anchor top, and short enough
 * (40dp; the row 60dp) for its target to reach its start: the cell it
 * scrolled to is the one at y 0 (x 0 on the row). A key two sections share
 * (k3) also has both of its cells composed at once, which Compose refused
 * with "Key "k3" was already used" before the later section's key was
 * "1:k3".
 */
@RunWith(AndroidJUnit4::class)
class DynamicCollectionScrollToTest {

    @get:Rule
    val rule = createComposeRule()

    private val cell = "collection_probe_title_cell"

    private var shown by mutableStateOf<Pair<JsonObject, Map<String, Any>>?>(null)
    private var composed = false

    private fun show(json: JsonObject, data: Map<String, Any>) {
        if (!composed) {
            rule.setContent { shown?.let { (j, d) -> key(j) { DynamicView(json = j, data = d) } } }
            composed = true
        }
        rule.runOnIdle { shown = json to data }
        rule.waitForIdle()
    }

    /** Section A: header H0, a0…a4 (keys k0…k4), footer F0; section B: header H1, b0…b7 (keys k3, x1…x7). */
    private val items = CollectionDataSource(
        sections = listOf(
            CollectionDataSection(
                header = CollectionDataSection.HeaderFooterData(cell, mapOf("title" to "H0")),
                cells = CollectionDataSection.CellData(cell, (0..4).map { mapOf<String, Any>("title" to "a$it", "key" to "k$it") }),
                footer = CollectionDataSection.HeaderFooterData(cell, mapOf("title" to "F0")),
            ),
            CollectionDataSection(
                header = CollectionDataSection.HeaderFooterData(cell, mapOf("title" to "H1")),
                cells = CollectionDataSection.CellData(
                    cell,
                    listOf("k3", "x1", "x2", "x3", "x4", "x5", "x6", "x7").mapIndexed { i, k -> mapOf<String, Any>("title" to "b$i", "key" to k) }
                ),
            ),
        )
    )

    private fun layout(extra: String): JsonObject = JsonParser.parseString(
        """{"type": "Collection", "id": "list", "items": "@{items}", "scrollTo": "@{target}",
            "scrollAnchor": "top", "scrollAnimated": false,
            "sections": [{"cell": "$cell", "header": "$cell", "footer": "$cell"}, {"cell": "$cell", "header": "$cell"}] $extra}"""
    ).asJsonObject

    /** The same sections, section A's keys k0, k1, k1, k3, k4 — two cells of one section share k1. */
    private val sharedInASection = CollectionDataSource(
        sections = listOf(
            CollectionDataSection(
                header = CollectionDataSection.HeaderFooterData(cell, mapOf("title" to "H0")),
                cells = CollectionDataSection.CellData(cell, listOf("k0", "k1", "k1", "k3", "k4").mapIndexed { i, k -> mapOf<String, Any>("title" to "a$i", "key" to k) }),
            ),
        )
    )

    /** Where the text's node is laid out — unclipped: boundsInRoot clips a node outside the list to nothing at 0,0. */
    private fun bounds(text: String): Rect? =
        rule.onAllNodesWithText(text).fetchSemanticsNodes().singleOrNull()?.let { Rect(it.positionInRoot, it.size.toSize()) }

    private fun listBounds(tag: String = "list"): Rect =
        rule.onNodeWithTag(tag, useUnmergedTree = true).fetchSemanticsNode().boundsInRoot

    /** Where the text is drawn, or null when it is not composed. */
    private fun at(text: String): Offset? =
        rule.onAllNodesWithText(text).fetchSemanticsNodes().singleOrNull()?.positionInRoot

    /**
     * Scrolls the list [json] to [target] and names what sits on its start
     * line, joined by spaces (a grid's row holds two), or "none".
     */
    private fun landed(json: JsonObject, target: Any, candidates: List<String>, horizontal: Boolean = false): String {
        show(json, mapOf("items" to items, "target" to -1))
        show(json, mapOf("items" to items, "target" to target))
        return candidates.filter { t -> at(t)?.let { if (horizontal) it.x else it.y }?.let { kotlin.math.abs(it) < 1f } == true }
            .joinToString(" ").ifEmpty { "none" }
    }

    private val cellsAndEdges = listOf("H0", "F0", "H1") + (0..4).map { "a$it" } + (0..7).map { "b$it" }

    @Test
    fun anIntIsACellCountedAcrossTheSections() {
        // A one-column list (the vertical grid route) and a two-column grid,
        // whose section B follows a part-filled row: an empty item breaks it.
        val list = layout(""", "width": 200, "height": 40""")
        assertEquals("scrollTo 6", "b1", landed(list, 6, cellsAndEdges))
        assertEquals("scrollTo 3", "a3", landed(list, 3, cellsAndEdges))
        // The grid's rows: H0 | a0 a1 | a2 a3 | a4 _ | F0 | H1 | b0 b1 | …
        val grid = layout(""", "width": 200, "height": 40, "columns": 2""")
        assertEquals("scrollTo 6 (grid)", "b0 b1", landed(grid, 6, cellsAndEdges))
        assertEquals("scrollTo 3 (grid)", "a2 a3", landed(grid, 3, cellsAndEdges))
    }

    @Test
    fun aKeyIsTheFirstCellInSectionOrderThatHasIt() {
        val json = layout(""", "width": 200, "height": 40, "cellIdProperty": "key"""")
        assertEquals("k3 is a3's key and b0's: a3", "a3", landed(json, "k3", cellsAndEdges))
        assertEquals("x2 is b2's", "b2", landed(json, "x2", cellsAndEdges))
        // The class decides (round 14): an Int is the counted cell, with cellIdProperty too.
        assertEquals("an Int 6 with cellIdProperty is b1", "b1", landed(json, 6, cellsAndEdges))
    }

    /**
     * Both cells of a key two sections share are drawn at once: the later
     * section's item key is "1:k3". Until jsonui-cli 1.9.0 both were "k3",
     * and Compose threw "Key "k3" was already used" as soon as both were
     * composed — a list tall enough to show them both did not draw.
     */
    @Test
    fun aKeyTwoSectionsShareDrawsBothCells() {
        val tall = layout(""", "width": 200, "height": 600, "cellIdProperty": "key"""")
        show(tall, mapOf("items" to items, "target" to -1))
        val drawn = listOf("a3", "b0").map { at(it) != null }
        assertEquals("a3 and b0 (both keyed k3) are drawn", listOf(true, true), drawn)
    }

    // ── round 11 (4f ruling 2026-09-27) ─────────────────────────────

    /**
     * The value the Collection first composes with scrolls nowhere; a change
     * does (as SwiftUI's onChange). Until jsonui-cli 1.9.0 the first
     * composition scrolled too.
     */
    @Test
    fun aScrollRunsOnAChangeOnly() {
        val json = layout(""", "width": 200, "height": 40""")
        show(json, mapOf("items" to items, "target" to 6))
        assertEquals("the initial 6 did not scroll: H0 at the top", 0f, at("H0")?.y ?: -999f, 1f)
        show(json, mapOf("items" to items, "target" to 7))
        assertEquals("a change to 7 scrolled to b2", 0f, at("b2")?.y ?: -999f, 1f)
    }

    /**
     * scrollAnchor lands the cell: bottom its end at the list's end, center
     * its middle at the middle. Until jsonui-cli 1.9.0 both landed it at the
     * top here.
     */
    @Test
    fun anAnchorLandsTheCellsEndOrMiddle() {
        for (anchor in listOf("bottom", "center")) {
            val json = JsonParser.parseString(
                layout(""", "width": 200, "height": 100""").toString().replace("\"scrollAnchor\":\"top\"", "\"scrollAnchor\":\"$anchor\"")
            ).asJsonObject
            landed(json, 6, cellsAndEdges)
            val cell = bounds("b1") ?: error("b1 is not drawn ($anchor)")
            val list = listBounds()
            if (anchor == "bottom") assertEquals("b1's end at the list's end", list.bottom, cell.bottom, 1f)
            else assertEquals("b1's middle at the list's middle", list.center.y, cell.center.y, 1f)
        }
    }

    /**
     * Two cells of one section sharing a key both draw: the later one's item
     * key is "k1#2". Compose threw "Key "k1" was already used" before.
     */
    @Test
    fun aKeyTwoCellsOfASectionShareDrawsBothCells() {
        val tall = layout(""", "width": 200, "height": 600, "cellIdProperty": "key"""")
        show(tall, mapOf("items" to sharedInASection, "target" to -1))
        assertEquals(listOf(true, true, true), listOf("a0", "a1", "a2").map { at(it) != null })
    }

    /** The flow scrolls to the cell (it read no scrollTo before 1.9.0). */
    @Test
    fun theFlowScrollsToTheCell() {
        val flow = layout(""", "layout": "flow", "width": 60, "height": 40""")
        show(flow, mapOf("items" to items, "target" to -1))
        show(flow, mapOf("items" to items, "target" to 6))
        val cell = bounds("b1") ?: error("b1 is not drawn")
        assertEquals("b1 at the flow's top", listBounds().top, cell.top, 1f)
    }

    /** The pager scrolls to the page the cell is (it read no scrollTo before 1.9.0). */
    @Test
    fun thePagerScrollsToThePage() {
        val pager = JsonParser.parseString(
            """{"type": "Collection", "id": "list", "layout": "horizontal", "paging": true, "width": 60, "height": 40,
                "items": "@{items}", "scrollTo": "@{target}", "scrollAnimated": false,
                "sections": [{"cell": "$cell"}, {"cell": "$cell"}]}"""
        ).asJsonObject
        assertEquals("b1", landed(pager, 6, cellsAndEdges, horizontal = true))
    }

    /**
     * defaultScrollAnchor bottom scrolls to the last CELL, counted as
     * scrollTo counts them: b7, at the list's end. Until 1.9.0 it counted
     * the first section only and scrolled to that lazy item (a3).
     */
    @Test
    fun theDefaultAnchorCountsCells() {
        val json = JsonParser.parseString(
            """{"type": "Collection", "id": "list", "items": "@{items}", "width": 200, "height": 60, "defaultScrollAnchor": "bottom",
                "sections": [{"cell": "$cell", "header": "$cell", "footer": "$cell"}, {"cell": "$cell", "header": "$cell"}]}"""
        ).asJsonObject
        show(json, mapOf("items" to items))
        val last = bounds("b7") ?: error("b7 is not drawn: the list did not reach its end")
        assertEquals("b7 at the list's end", listBounds().bottom, last.bottom, 1f)
    }

    /** The single-lane row (LazyRow): its items are the cells. */
    @Test
    fun aRowScrollsToTheCell() {
        val json = JsonParser.parseString(
            """{"type": "Collection", "id": "row", "layout": "horizontal", "width": 60, "height": 40, "items": "@{items}",
                "scrollTo": "@{target}", "scrollAnchor": "top", "scrollAnimated": false,
                "sections": [{"cell": "$cell"}, {"cell": "$cell"}]}"""
        ).asJsonObject
        assertEquals("b1", landed(json, 6, cellsAndEdges, horizontal = true))
    }
}
