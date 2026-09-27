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
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
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

    // ── round 12 (4f rulings 2026-09-27) ─────────────────────────────

    /**
     * The EAGER Column scrolls to the cell by the rule the lazy routes follow:
     * an Int the counted cell, a String a key. The legacy form names no cell
     * here (no lazy item). Until jsonui-cli 1.9.0 its scrollTo moved nothing.
     */
    @Test
    fun theEagerColumnScrollsToTheCell() {
        val eager = layout(""", "lazy": "eager", "width": 200, "height": 40, "cellIdProperty": "key"""")
        assertEquals("scrollTo 6", "b1", landed(eager, 6, cellsAndEdges))
        assertEquals("scrollTo x2", "b2", landed(eager, "x2", cellsAndEdges))
        show(eager, mapOf("items" to items, "target" to "0#1"))
        assertEquals("0#1 moved nothing: b2 still at the top", 0f, at("b2")?.y ?: -999f, 1f)
    }

    /**
     * A value that arrives with its cells (a consumer screen's refresh and
     * its request in one turn): the EAGER Column's cell is not laid out yet
     * when the request runs, and is waited for.
     */
    @Test
    fun theEagerColumnScrollsToACellThatArrivesWithTheValue() {
        val eager = layout(""", "lazy": "eager", "width": 200, "height": 40""")
        show(eager, mapOf("items" to CollectionDataSource(), "target" to -1))
        show(eager, mapOf("items" to items, "target" to 6))
        assertEquals("b1 at the top", 0f, at("b1")?.y ?: -999f, 1f)
    }

    /** The EAGER Column lands its anchor: bottom the cell's end at the list's end, center its middle. */
    @Test
    fun theEagerColumnLandsItsAnchor() {
        for (anchor in listOf("bottom", "center")) {
            val json = JsonParser.parseString(
                layout(""", "lazy": "eager", "width": 200, "height": 100""").toString()
                    .replace("\"scrollAnchor\":\"top\"", "\"scrollAnchor\":\"$anchor\"")
            ).asJsonObject
            landed(json, 6, cellsAndEdges)
            val cell = bounds("b1") ?: error("b1 is not drawn ($anchor)")
            val list = listBounds()
            if (anchor == "bottom") assertEquals("b1's end at the list's end", list.bottom, cell.bottom, 1f)
            else assertEquals("b1's middle at the list's middle", list.center.y, cell.center.y, 1f)
        }
    }

    /** The EAGER Row scrolls to the cell along x. */
    @Test
    fun theEagerRowScrollsToTheCell() {
        val json = JsonParser.parseString(
            """{"type": "Collection", "id": "row", "layout": "horizontal", "lazy": "eager", "width": 60, "height": 40, "items": "@{items}",
                "scrollTo": "@{target}", "scrollAnchor": "top", "scrollAnimated": false,
                "sections": [{"cell": "$cell"}, {"cell": "$cell"}]}"""
        ).asJsonObject
        assertEquals("b1", landed(json, 6, cellsAndEdges, horizontal = true))
    }

    private fun wrapped(parent: String, height: Int): JsonObject = JsonParser.parseString(
        """{"type": "$parent", "id": "box", "width": 200, "height": $height, "child": [
             {"type": "View", "id": "inner", "width": "matchParent", "child": [
               {"type": "Collection", "id": "list", "width": "matchParent", "height": "wrapContent", "items": "@{items}",
                "scrollTo": "@{target}", "scrollAnchor": "top", "scrollAnimated": false,
                "sections": [{"cell": "$cell", "header": "$cell", "footer": "$cell"}, {"cell": "$cell", "header": "$cell"}]}]}]}"""
    ).asJsonObject

    /**
     * A vertical Collection whose height wraps its content scrolls inside the
     * height its parent bounds it to, as iOS and the web draw it (measured,
     * 4f round 12). Until jsonui-cli 1.9.0 it never scrolled: its cells ran
     * past the parent, and a scrollTo moved nothing.
     */
    @Test
    fun aWrapContentCollectionScrollsInsideItsParentsHeight() {
        val json = wrapped("View", 60)
        show(json, mapOf("items" to items, "target" to -1))
        assertEquals("as tall as its parent lets it be", listBounds("box").height, listBounds().height, 1f)
        show(json, mapOf("items" to items, "target" to 6))
        val cell = bounds("b1") ?: error("b1 is not drawn")
        assertEquals("b1 at the list's top", listBounds().top, cell.top, 1f)
    }

    /**
     * Under a parent that does not bound its height (a ScrollView) it is its
     * content's height, with nothing of its own to scroll: a scrollTo moves
     * nothing, as on iOS and the web — and it draws, where a bare
     * verticalScroll would throw on the unbounded height.
     */
    @Test
    fun aWrapContentCollectionUnderAScrollingParentIsItsContentsHeight() {
        val json = wrapped("ScrollView", 60)
        show(json, mapOf("items" to items, "target" to -1))
        assertEquals("every cell is laid out", true, at("b7") != null)
        // Laid-out heights, unclipped (boundsInRoot clips the list to the ScrollView's viewport).
        fun height(tag: String) = rule.onNodeWithTag(tag, useUnmergedTree = true).fetchSemanticsNode().size.height
        assert(height("list") > height("box")) { "the list is its content's height (${height("list")}), taller than the parent (${height("box")})" }
        val before = at("H0")?.y
        show(json, mapOf("items" to items, "target" to 6))
        assertEquals("nothing moved", before ?: -999f, at("H0")?.y ?: -998f, 1f)
    }

    /**
     * defaultScrollAnchor under reverseLayout: bottom is where a reversed list
     * rests — its first emitted item at the visual bottom — and top goes to the
     * visual top (4f ruling, round 12: where iOS lands). Until jsonui-cli 1.9.0
     * bottom went to the last cell, which a reversed list draws at its top.
     */
    @Test
    fun aReversedListsDefaultAnchorLandsWhereItNames() {
        fun json(anchor: String) = JsonParser.parseString(
            """{"type": "Collection", "id": "list", "items": "@{items}", "width": 200, "height": 60, "reverseLayout": true,
                "defaultScrollAnchor": "$anchor",
                "sections": [{"cell": "$cell", "header": "$cell", "footer": "$cell"}, {"cell": "$cell", "header": "$cell"}]}"""
        ).asJsonObject
        // Emitted last-first: H1 b0 … b7 H0 a0 … a4 F0 — H1 at the visual bottom, F0 at the visual top.
        show(json("bottom"), mapOf("items" to items))
        val h1 = bounds("H1") ?: error("H1 is not drawn: the list left its visual bottom")
        assertEquals("bottom: H1 at the list's bottom", listBounds().bottom, h1.bottom, 1f)
        show(json("top"), mapOf("items" to items))
        val f0 = bounds("F0") ?: error("F0 is not drawn: the list did not reach its visual top")
        assertEquals("top: F0 at the list's top", listBounds().top, f0.top, 1f)
    }

    /**
     * A list with a consuming message list's attributes — three sections (messages, a
     * prompt, a streaming cell), reverseLayout, defaultScrollAnchor and
     * scrollAnchor bottom, cellIdProperty cellId, autoChangeTrackingId, a
     * bound `lazy` — fed that screen's Android order (newest first, the
     * load-more cell last): it draws top to bottom L, m0 … m5 (the order iOS
     * draws from its own order: L first, oldest first) and opens with the
     * newest message at its bottom edge, as iOS does (4f round 12, measured on
     * both). Until jsonui-cli 1.9.0 defaultScrollAnchor bottom took it to the
     * load-more cell, the visual top.
     */
    @Test
    fun aMessageListOpensAtItsNewestMessage() {
        fun messageList(height: Int) = JsonParser.parseString(
            """{"type": "Collection", "id": "list", "lazy": "@{mode}", "width": 200, "height": $height, "items": "@{messages}",
                "sections": [{"cell": "$cell"}, {"cell": "$cell"}, {"cell": "$cell"}], "lineSpacing": 0, "insets": [12, 0, 0, 0],
                "scrollTo": "@{target}", "scrollAnchor": "bottom", "reverseLayout": true, "scrollAnimated": false,
                "cellIdProperty": "cellId", "autoChangeTrackingId": true, "defaultScrollAnchor": "bottom"}"""
        ).asJsonObject
        val messages = CollectionDataSource(
            sections = listOf(
                CollectionDataSection(cells = CollectionDataSection.CellData(cell,
                    (5 downTo 0).map { mapOf<String, Any>("title" to "m$it", "cellId" to "msg$it") } + mapOf("title" to "L", "cellId" to "conv_load_more"))),
                CollectionDataSection(cells = CollectionDataSection.CellData(cell, emptyList())),
                CollectionDataSection(cells = CollectionDataSection.CellData(cell, emptyList())),
            )
        )
        val order = listOf("L") + (0..5).map { "m$it" }
        show(messageList(400), mapOf("messages" to messages, "mode" to "lazy", "target" to ""))
        val tall = order.map { at(it)?.y ?: error("$it is not drawn in the tall list") }
        assertEquals("top to bottom: L, m0 … m5", tall.sorted(), tall)
        show(messageList(60), mapOf("messages" to messages, "mode" to "lazy", "target" to ""))
        val newest = bounds("m5") ?: error("m5 is not drawn: the list did not open at its newest message")
        assertEquals("m5 at the list's bottom", listBounds().bottom, newest.bottom, 1f)
        // A new conversation's welcome, two cells, in the tall list: at its bottom, L above w0, as iOS
        // draws it (round 13; it sat at the top).
        val welcome = CollectionDataSource(
            sections = listOf(
                CollectionDataSection(cells = CollectionDataSection.CellData(cell, listOf(
                    mapOf<String, Any>("title" to "w0", "cellId" to "welcome0"), mapOf<String, Any>("title" to "L", "cellId" to "new_load_more")))),
                CollectionDataSection(cells = CollectionDataSection.CellData(cell, emptyList())),
                CollectionDataSection(cells = CollectionDataSection.CellData(cell, emptyList())),
            )
        )
        show(messageList(400), mapOf("messages" to welcome, "mode" to "lazy", "target" to ""))
        assertEquals("w0 at the list's bottom", listBounds().bottom, bounds("w0")?.bottom ?: -999f, 1f)
        assert((bounds("L")?.bottom ?: 999f) <= (bounds("w0")?.top ?: -999f) + 1f) { "L above w0" }
    }

    // ── round 13 (4f rulings 2026-09-27) ─────────────────────────────

    private val twoCells = CollectionDataSource(
        sections = listOf(CollectionDataSection(cells = CollectionDataSection.CellData(cell, listOf("s0", "s1").map { mapOf<String, Any>("title" to it) })))
    )

    /**
     * A reversed list whose content is shorter than its viewport sits at its
     * bottom — where iOS draws such a list, bottom-anchored — with or without
     * lineSpacing. It sat at the top until then (spacedBy packs to the top).
     */
    @Test
    fun aShortReversedListSitsAtItsBottom() {
        for (spacing in listOf("", """, "lineSpacing": 4""")) {
            val json = JsonParser.parseString(
                """{"type": "Collection", "id": "list", "items": "@{items}", "width": 200, "height": 200, "reverseLayout": true,
                    "sections": [{"cell": "$cell"}] $spacing}"""
            ).asJsonObject
            show(json, mapOf("items" to twoCells))
            val s0 = bounds("s0") ?: error("s0 is not drawn")
            val s1 = bounds("s1") ?: error("s1 is not drawn")
            assertEquals("s0, the first cell, at the bottom ($spacing)", listBounds().bottom, s0.bottom, 1f)
            assert(s1.bottom <= s0.top + 1f) { "s1 above s0 ($spacing)" }
        }
    }

    /**
     * The EAGER Column draws reverseLayout as the lazy list does: its first
     * cell at the bottom, the sections in order top to bottom, a short list at
     * its bottom, resting at its bottom when long; a scrollTo lands on the
     * edge it names. It drew no reverseLayout until then.
     */
    @Test
    fun theEagerColumnDrawsReverseLayout() {
        fun eager(height: Int) = JsonParser.parseString(
            """{"type": "Collection", "id": "list", "lazy": "eager", "items": "@{items}", "width": 200, "height": $height, "reverseLayout": true,
                "scrollTo": "@{target}", "scrollAnchor": "top", "scrollAnimated": false,
                "sections": [{"cell": "$cell", "header": "$cell", "footer": "$cell"}, {"cell": "$cell", "header": "$cell"}]}"""
        ).asJsonObject
        // The whole of it: F0 a4 … a0 H0 (section A) above b7 … b0 H1 (section B), H1 at the bottom
        // of a list taller than its content (and inside the window).
        show(eager(300), mapOf("items" to items, "target" to -1))
        val order = listOf("F0") + (4 downTo 0).map { "a$it" } + "H0" + (7 downTo 0).map { "b$it" } + "H1"
        val ys = order.map { at(it)?.y ?: error("$it is not drawn") }
        assertEquals("top to bottom: $order", ys.sorted(), ys)
        assertEquals("H1, the first item, at the bottom", listBounds().bottom, bounds("H1")!!.bottom, 1f)
        // Long: it rests at its bottom; scrollTo 3 (a3) lands a3 at the top.
        show(eager(60), mapOf("items" to items, "target" to -1))
        assertEquals("resting at the bottom: H1 there", listBounds().bottom, bounds("H1")!!.bottom, 1f)
        show(eager(60), mapOf("items" to items, "target" to 3))
        assertEquals("a3 at the top", listBounds().top, bounds("a3")!!.top, 1f)
    }

    /**
     * scrollEnabled false stops the user's scrolling only: a swipe moves
     * nothing, a scrollTo still lands — the EAGER Column, the flow, the
     * wrapContent Column. KotlinJsonUI Dynamic ignored scrollEnabled there
     * until then (a swipe scrolled them).
     */
    @Test
    fun scrollEnabledFalseStopsTheUserOnly() {
        val shapes = mapOf(
            "eager" to """, "lazy": "eager", "width": 200, "height": 60""",
            // One cell a row (as theFlowScrollsToTheCell), so b1 can reach the top.
            "flow" to """, "layout": "flow", "width": 60, "height": 40""",
        )
        for ((name, extra) in shapes) {
            val json = layout(""", "scrollEnabled": false $extra""")
            show(json, mapOf("items" to items, "target" to -1))
            val before = at("H0")?.y ?: error("$name: H0 is not drawn")
            rule.onNodeWithTag("list", useUnmergedTree = true).performTouchInput { swipeUp() }
            rule.waitForIdle()
            assertEquals("$name: a swipe moved nothing", before, at("H0")?.y ?: -999f, 1f)
            show(json, mapOf("items" to items, "target" to 6))
            assertEquals("$name: scrollTo 6 still lands b1 at the top", listBounds().top, bounds("b1")!!.top, 1f)
        }
    }

    /** The single-lane row applies defaultScrollAnchor: bottom, the last cell at the row's end. It drew none until then. */
    @Test
    fun theRowAppliesItsDefaultAnchor() {
        val json = JsonParser.parseString(
            """{"type": "Collection", "id": "row", "layout": "horizontal", "width": 60, "height": 40, "items": "@{items}",
                "defaultScrollAnchor": "bottom", "sections": [{"cell": "$cell"}, {"cell": "$cell"}]}"""
        ).asJsonObject
        show(json, mapOf("items" to items))
        val last = bounds("b7") ?: error("b7 is not drawn: the row did not reach its end")
        assertEquals("b7 at the row's end", listBounds("row").right, last.right, 1f)
    }

    /**
     * CollectionStack (the kjui codegen's container): the EAGER one keeps its
     * scroll with the user's scrolling off — a programmatic scroll moves it —
     * and a short reversed LAZY one sits at its bottom. Until then EAGER
     * dropped its scroll with userScrollEnabled false, and a short reversed
     * stack sat at its top.
     */
    @Test
    fun theStackKeepsItsScrollAndSitsAtItsBottom() {
        val state = ScrollState(0)
        var mode by mutableStateOf(com.kotlinjsonui.components.CollectionStackMode.EAGER)
        var count by mutableStateOf(20)
        rule.setContent {
            com.kotlinjsonui.components.CollectionStack(
                mode = mode,
                modifier = Modifier.testTag("stack").width(100.dp).height(60.dp),
                userScrollEnabled = false,
                reverseLayout = mode == com.kotlinjsonui.components.CollectionStackMode.LAZY,
                eagerScrollState = state,
                lazyContent = { items(count) { Text("r$it", Modifier.height(20.dp)) } },
                eagerContent = { repeat(count) { Text("r$it", Modifier.height(20.dp)) } }
            )
        }
        rule.waitForIdle()
        val step = with(rule.density) { 40.dp.roundToPx() }
        rule.runOnIdle { kotlinx.coroutines.runBlocking { state.scrollTo(step) } }
        rule.waitForIdle()
        assertEquals("EAGER: the programmatic scroll moved r2 to the top", listBounds("stack").top, bounds("r2")?.top ?: -999f, 1f)
        rule.runOnIdle { mode = com.kotlinjsonui.components.CollectionStackMode.LAZY; count = 2 }
        rule.waitForIdle()
        assertEquals("reversed LAZY, 2 rows: r0 at the bottom", listBounds("stack").bottom, bounds("r0")?.bottom ?: -999f, 1f)
    }

    // ── round 14 (4f rulings 2026-09-27) ─────────────────────────────

    /**
     * The single-lane row draws reverseLayout, as kjui's horizontal
     * CollectionStack does: its first cell at its end, a short row at its end,
     * and a scrollTo landing on the edge it names. It drew none until then.
     */
    @Test
    fun theRowDrawsReverseLayout() {
        val short = JsonParser.parseString(
            """{"type": "Collection", "id": "row", "layout": "horizontal", "width": 200, "height": 40, "items": "@{items}",
                "reverseLayout": true, "sections": [{"cell": "$cell"}]}"""
        ).asJsonObject
        show(short, mapOf("items" to twoCells))
        val s0 = bounds("s0") ?: error("s0 is not drawn")
        val s1 = bounds("s1") ?: error("s1 is not drawn")
        assertEquals("s0, the first cell, at the row's end", listBounds("row").right, s0.right, 1f)
        assert(s1.right <= s0.left + 1f) { "s1 before s0" }
        val long = JsonParser.parseString(
            """{"type": "Collection", "id": "row", "layout": "horizontal", "width": 60, "height": 40, "items": "@{items}",
                "reverseLayout": true, "scrollTo": "@{target}", "scrollAnchor": "top", "scrollAnimated": false,
                "sections": [{"cell": "$cell"}, {"cell": "$cell"}]}"""
        ).asJsonObject
        show(long, mapOf("items" to items, "target" to -1))
        // Emitted last-first, B's cells from the end: a3 is near the start; top lands it at the row's start.
        show(long, mapOf("items" to items, "target" to 3))
        assertEquals("scrollTo 3: a3 at the row's start", listBounds("row").left, bounds("a3")?.left ?: -999f, 1f)
    }

    /**
     * Not reversed, defaultScrollAnchor bottom: content shorter than the list
     * sits at its bottom, as iOS draws it — the grid route and the EAGER
     * Column. It sat at the top until then.
     */
    @Test
    fun aShortListWithABottomAnchorSitsAtItsBottom() {
        for (extra in listOf("", """, "lazy": "eager"""")) {
            val json = JsonParser.parseString(
                """{"type": "Collection", "id": "list", "items": "@{items}", "width": 200, "height": 200, "defaultScrollAnchor": "bottom",
                    "sections": [{"cell": "$cell"}] $extra}"""
            ).asJsonObject
            show(json, mapOf("items" to twoCells))
            val s1 = bounds("s1") ?: error("s1 is not drawn ($extra)")
            assertEquals("s1, the last cell, at the bottom ($extra)", listBounds().bottom, s1.bottom, 1f)
            assert((bounds("s0")?.bottom ?: 999f) <= s1.top + 1f) { "s0 above s1 ($extra)" }
        }
    }

    /**
     * CollectionStack's EAGER container pads its content with contentPadding,
     * as the LAZY one does (a consuming message list's top inset of 12 was lost
     * in eager mode), and contentAtBottom puts short content at its bottom on
     * both modes.
     */
    @Test
    fun theEagerStackPadsItsContentAndSitsAtItsBottom() {
        var mode by mutableStateOf(com.kotlinjsonui.components.CollectionStackMode.EAGER)
        var atBottom by mutableStateOf(false)
        rule.setContent {
            com.kotlinjsonui.components.CollectionStack(
                mode = mode,
                modifier = Modifier.testTag("stack").width(100.dp).height(200.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 12.dp),
                contentAtBottom = atBottom,
                lazyContent = { items(2) { Text("r$it", Modifier.height(20.dp)) } },
                eagerContent = { repeat(2) { Text("r$it", Modifier.height(20.dp)) } }
            )
        }
        rule.waitForIdle()
        val inset = with(rule.density) { 12.dp.toPx() }
        assertEquals("EAGER: r0 below the top inset", listBounds("stack").top + inset, bounds("r0")?.top ?: -999f, 1f)
        for (m in listOf(com.kotlinjsonui.components.CollectionStackMode.EAGER, com.kotlinjsonui.components.CollectionStackMode.LAZY)) {
            rule.runOnIdle { mode = m; atBottom = true }
            rule.waitForIdle()
            assertEquals("$m, contentAtBottom: r1 at the bottom", listBounds("stack").bottom, bounds("r1")?.bottom ?: -999f, 1f)
        }
    }

    // ── round 15 (4f rulings 2026-09-27) ─────────────────────────────

    /**
     * A short horizontal list sits where defaultScrollAnchor says, as iOS
     * draws it (sjui codegen and SwiftJsonUI Dynamic, measured): bottom at
     * its end, center in its middle — on the single-lane row, the lanes'
     * grid and the EAGER Row. A `lazy: none` Row does not scroll and stays at
     * its start, as iOS's none route; a reversed row keeps its end (round 13)
     * but for center. Every row sat at its start whatever the anchor said.
     */
    @Test
    fun aShortRowSitsWhereItsDefaultAnchorSays() {
        val routes = listOf("row" to "", "grid" to """, "columns": 2""", "eager" to """, "lazy": "eager"""",
            "none" to """, "lazy": "none"""", "reversed" to """, "reverseLayout": true""")
        val drawn = mutableListOf<String>()
        val wanted = mutableListOf<String>()
        for ((route, extra) in routes) {
            for (anchor in listOf("top", "center", "bottom")) {
                val json = JsonParser.parseString(
                    """{"type": "Collection", "id": "row", "layout": "horizontal", "width": 300, "height": 80, "items": "@{items}",
                        "defaultScrollAnchor": "$anchor", "sections": [{"cell": "$cell"}] $extra}"""
                ).asJsonObject
                show(json, mapOf("items" to twoCells))
                val row = listBounds("row")
                val s0 = bounds("s0") ?: error("$route $anchor: s0 is not drawn")
                val s1 = bounds("s1") ?: error("$route $anchor: s1 is not drawn")
                val left = minOf(s0.left, s1.left)
                val right = maxOf(s0.right, s1.right)
                val at = when {
                    kotlin.math.abs(left - row.left) < 1f -> "start"
                    kotlin.math.abs(right - row.right) < 1f -> "end"
                    kotlin.math.abs((left + right) / 2 - row.center.x) < 1f -> "center"
                    else -> "x ${left - row.left}..${right - row.left} of ${row.width}"
                }
                drawn += "$route $anchor: $at"
                wanted += "$route $anchor: " + when {
                    route == "none" -> "start"
                    anchor == "center" -> "center"
                    anchor == "bottom" || route == "reversed" -> "end"
                    else -> "start"
                }
            }
        }
        assertEquals(wanted.joinToString("\n"), drawn.joinToString("\n"))
    }

    /**
     * CollectionStack (the kjui codegen's container): the NONE container pads
     * its content with contentPadding, as the LAZY and EAGER ones do (it
     * applied none), on both axes.
     */
    @Test
    fun theNoneStackPadsItsContent() {
        var axis by mutableStateOf(com.kotlinjsonui.components.CollectionStackAxis.VERTICAL)
        rule.setContent {
            com.kotlinjsonui.components.CollectionStack(
                mode = com.kotlinjsonui.components.CollectionStackMode.NONE,
                axis = axis,
                modifier = Modifier.testTag("stack").width(200.dp).height(100.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 12.dp, start = 16.dp),
                eagerContent = { repeat(2) { Text("r$it", Modifier.height(20.dp)) } }
            )
        }
        val top = with(rule.density) { 12.dp.toPx() }
        val start = with(rule.density) { 16.dp.toPx() }
        for (a in com.kotlinjsonui.components.CollectionStackAxis.values()) {
            rule.runOnIdle { axis = a }
            rule.waitForIdle()
            val r0 = bounds("r0") ?: error("$a: r0 is not drawn")
            assertEquals("$a: r0 below the top inset", listBounds("stack").top + top, r0.top, 1f)
            assertEquals("$a: r0 after the start inset", listBounds("stack").left + start, r0.left, 1f)
        }
    }

    /**
     * CollectionStack's rows take rowContentAlignment: a short LAZY or EAGER
     * row at its end or in its middle, with and without spacing; the NONE
     * row, which does not scroll, at its start. Null is the start.
     */
    @Test
    fun theStackRowSitsWhereItsAlignmentSays() {
        var mode by mutableStateOf(com.kotlinjsonui.components.CollectionStackMode.LAZY)
        var alignment by mutableStateOf<androidx.compose.ui.Alignment.Horizontal?>(null)
        var spacing by mutableStateOf(0.dp)
        rule.setContent {
            com.kotlinjsonui.components.CollectionStack(
                mode = mode,
                axis = com.kotlinjsonui.components.CollectionStackAxis.HORIZONTAL,
                modifier = Modifier.testTag("stack").width(300.dp).height(40.dp),
                spacing = spacing,
                rowContentAlignment = alignment,
                lazyContent = { items(2) { Text("r$it", Modifier.width(60.dp)) } },
                eagerContent = { repeat(2) { Text("r$it", Modifier.width(60.dp)) } }
            )
        }
        val px = { dp: Int -> with(rule.density) { dp.dp.toPx() } }
        val drawn = mutableListOf<String>()
        val wanted = mutableListOf<String>()
        for (m in com.kotlinjsonui.components.CollectionStackMode.values()) {
            for (gap in listOf(0, 8)) {
                for ((name, a) in listOf("null" to null, "End" to androidx.compose.ui.Alignment.End,
                        "CenterHorizontally" to androidx.compose.ui.Alignment.CenterHorizontally)) {
                    rule.runOnIdle { mode = m; spacing = gap.dp; alignment = a }
                    rule.waitForIdle()
                    val left = (bounds("r0")?.left ?: -999f) - listBounds("stack").left
                    val free = px(300) - px(120) - px(gap)
                    val at = when {
                        kotlin.math.abs(left) < 1f -> "start"
                        kotlin.math.abs(left - free) < 1f -> "end"
                        kotlin.math.abs(left - free / 2) < 1f -> "middle"
                        else -> "x $left"
                    }
                    drawn += "$m gap $gap $name: $at"
                    wanted += "$m gap $gap $name: " + when {
                        m == com.kotlinjsonui.components.CollectionStackMode.NONE || a == null -> "start"
                        name == "End" -> "end"
                        else -> "middle"
                    }
                }
            }
        }
        assertEquals(wanted.joinToString("\n"), drawn.joinToString("\n"))
    }
}
