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
