package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.data.CollectionDataSection
import com.kotlinjsonui.data.CollectionDataSource
import com.kotlinjsonui.dynamic.components.DynamicCollectionComponent.Companion.ScrollSection
import com.kotlinjsonui.dynamic.components.DynamicCollectionComponent.Companion.ScrollCell
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * What a Collection's `scrollTo` names across sections (4f ruling
 * 2026-09-27; jsonui-cli 1.9.0, the SSoT's Collection.scrollTo): an Int is
 * a CELL counted across the drawn sections in section order — a header or
 * footer item, and the grid's row break, is not counted; a String with
 * cellIdProperty is the first cell, in section order, whose key it is. A
 * String that is no cell's key is read, as before 1.9.0, as the lazy item
 * index (the Kotlin paths' own reading, ruled by 4f in round 11) — only
 * `<digits>` / `<digits>#<anything>`, and a debuggable app is told.
 *
 * Until 1.9.0 this renderer read only a `SharedFlow<Int>` and scrolled to
 * that lazy item: headers, footers and breaks counted, a plain value never
 * scrolled. What is pinned here is the arithmetic, over the sections as the
 * grid emits them (emittedScrollSections); the scroll is pinned on a device
 * (DynamicCollectionScrollToTest).
 */
class CollectionScrollToTest {

    private fun cells(vararg keys: String) = keys.map { mapOf<String, Any>("key" to it) }

    /** Section A: header, a0…a4 (k0…k4), footer; section B: header, b0…b7 (k3, x1…x7). */
    private val a = cells("k0", "k1", "k2", "k3", "k4")
    private val b = cells("k3", "x1", "x2", "x3", "x4", "x5", "x6", "x7")
    private val list = listOf(
        ScrollSection(0, breakBefore = false, header = true, cells = a, footer = true),
        ScrollSection(1, breakBefore = false, header = true, cells = b, footer = false),
    )

    private fun item(value: Any?, sections: List<ScrollSection> = list, key: String? = null, leading: Int = 0) =
        DynamicCollectionComponent.scrollItemIndex(value, sections, key, leading)

    // Items: H a0 a1 a2 a3 a4 F H b0 b1 … — item 6 is F, item 3 a2.
    @Test
    fun anIntIsACellCountedAcrossTheSections() {
        assertEquals(1, item(0))
        assertEquals(4, item(3))
        assertEquals(9, item(6))      // b1
        assertEquals(15, item(12))    // b7, the last cell
        assertNull(item(13))          // no 14th cell
        assertEquals(9, item(6L))     // any number
        // A String is a key (round 14), with or without cellIdProperty: no
        // cell here has a cellId, so "6" is no key — the legacy lazy item 6.
        assertEquals(6, item("6"))
        assertEquals(6, item("6#1727"))
        assertNull(item(""))
        assertNull(item("six"))
        assertNull(item("-1"))
        assertNull(item(-1))
    }

    /**
     * Where the item lands: its start at the viewport's start (top), its
     * middle at the middle (center), its end at the end (bottom); reversed,
     * top and bottom trade places (4f round 11). Until 1.9.0 center and
     * bottom landed it at the top here, and kjui's codegen put its top at
     * the viewport's middle / bottom edge.
     */
    @Test
    fun anAnchorLandsTheItemsStartMiddleOrEnd() {
        assertEquals(0, DynamicCollectionComponent.anchorOffset("top", false, 100, 28))
        assertEquals(-36, DynamicCollectionComponent.anchorOffset("center", false, 100, 28))
        assertEquals(-72, DynamicCollectionComponent.anchorOffset("bottom", false, 100, 28))
        assertEquals(-72, DynamicCollectionComponent.anchorOffset("top", true, 100, 28))
        assertEquals(0, DynamicCollectionComponent.anchorOffset("bottom", true, 100, 28))
        assertEquals(-36, DynamicCollectionComponent.anchorOffset("center", true, 100, 28))
    }

    /** Keys two cells of one section share are written apart; the first keeps its own. */
    @Test
    fun aSectionsSharedKeysAreWrittenApart() {
        assertEquals(listOf("a", "b", "a#2", "a#3"), DynamicCollectionComponent.uniqueKeys(listOf("a", "b", "a", "a")))
        assertEquals(listOf("a", "a#2", "a#2#2"), DynamicCollectionComponent.uniqueKeys(listOf("a", "a", "a#2")))
        assertEquals(listOf("0", "1"), DynamicCollectionComponent.uniqueKeys(listOf("0", "1")))
    }

    /** A flow's cell: no legacy reading — a String that is no key names no cell. */
    @Test
    fun aFlowsCellHasNoLegacyReading() {
        val cells = a + b
        assertEquals(ScrollCell.Cell(3), DynamicCollectionComponent.scrollCell("k3", cells, "key", legacy = false))
        assertNull(DynamicCollectionComponent.scrollCell("0#1", cells, "key", legacy = false))
        assertEquals(ScrollCell.Legacy("0#1", 0), DynamicCollectionComponent.scrollCell("0#1", cells, "key"))
        assertEquals(ScrollCell.Cell(12), DynamicCollectionComponent.scrollCell(12, cells, null))
        assertNull(DynamicCollectionComponent.scrollCell(13, cells, null))
    }

    @Test
    fun aKeyIsTheFirstCellInSectionOrderThatHasIt() {
        assertEquals(4, item("k3", key = "key"))   // a3, not b0
        assertEquals(10, item("x2", key = "key"))  // b2
        assertEquals(2, item("k1", key = "key"))
        // No cell's key: the lazy item index, as before 1.9.0 — `<digits>` or
        // `<digits>#<anything>` only, and the caller is told (4f round 11).
        val told = mutableListOf<Pair<String, Int>>()
        assertEquals(0, DynamicCollectionComponent.scrollItemIndex("0#1727", list, "key", 0) { raw, index -> told += raw to index })
        assertEquals(listOf("0#1727" to 0), told)
        assertEquals(7, item("7", key = "key"))
        assertNull(item("nothing", key = "key"))
        assertNull(item("-3#1", key = "key"))
        assertNull(item("3a", key = "key"))
        assertEquals(1, told.size)
        // A number is the counted cell with cellIdProperty too (round 14).
        assertEquals(4, item(3, key = "key"))   // a3
        assertEquals(9, item(6, key = "key"))   // b1
        // Without cellIdProperty a String is matched against the cellId.
        val withIds = listOf(ScrollSection(0, false, true, listOf(mapOf("cellId" to "c0"), mapOf("cellId" to "c1")), false))
        assertEquals(2, item("c1", withIds))
        assertEquals(1, item(0, withIds))
        // An enriched cellId is the key when there is one.
        val enriched = listOf(ScrollSection(0, false, false, listOf(mapOf("key" to "k0", "cellId" to "k0_x")), false))
        assertEquals(0, item("k0_x", enriched, key = "key"))
        assertNull(item("k0", enriched, key = "key"))
    }

    /** Emitted in reverse (reverseLayout): section B's items come first; the cells are still counted A then B. */
    @Test
    fun reversedEmissionCountsCellsInSectionOrder() {
        val reversed = list.reversed()
        // Items: H b0 … b7 H a0 … a4 F
        assertEquals(10, item(0, reversed))   // a0
        assertEquals(2, item(6, reversed))    // b1
    }

    /** A grid's row break (an empty item) is not a cell; the legacy header item leads. */
    @Test
    fun aRowBreakAndALeadingHeaderAreNotCells() {
        val grid = listOf(
            ScrollSection(0, breakBefore = false, header = true, cells = a, footer = false),
            ScrollSection(1, breakBefore = true, header = false, cells = b, footer = false),
        )
        // Items: H a0 … a4 _ b0 …
        assertEquals(8, item(6, grid))
        assertEquals(9, item(6, grid, leading = 1))
    }

    /** The grid's sections as generateCollectionItems emits them, row breaks from sectionBreakSpans. */
    @Test
    fun theGridsSectionsAsEmitted() {
        val declared = JsonParser.parseString(
            """[{"cell": "c", "header": "h"}, {"cell": "c"}]"""
        ).asJsonArray
        val source = CollectionDataSource(
            sections = listOf(
                CollectionDataSection(
                    header = CollectionDataSection.HeaderFooterData("h", emptyMap()),
                    cells = CollectionDataSection.CellData("c", a)
                ),
                CollectionDataSection(cells = CollectionDataSection.CellData("c", b)),
            )
        )
        val emitted = DynamicCollectionComponent.emittedScrollSections(declared, source, 2, 2, reverseLayout = false, breakRowsBetweenSections = true)
        // a0 … a4 leave a row part filled: a break before section B.
        assertEquals(listOf(false, true), emitted.map { it.breakBefore })
        assertEquals(listOf(true, false), emitted.map { it.header })
        assertEquals(8, item(6, emitted))
        val reversed = DynamicCollectionComponent.emittedScrollSections(declared, source, 2, 2, reverseLayout = true, breakRowsBetweenSections = true)
        assertEquals(listOf(1, 0), reversed.map { it.section })
        assertEquals(1, item(6, reversed))  // b1: B first, its cells from item 0
    }

    // ── round 12 (4f rulings 2026-09-27) ─────────────────────────────

    /**
     * defaultScrollAnchor as the list rests: under reverseLayout top and
     * bottom trade places (the visual bottom is where a reversed list starts).
     */
    @Test
    fun aReversedListRestsAtItsVisualBottom() {
        assertEquals("bottom", DynamicCollectionComponent.restingAnchor("bottom", false))
        assertEquals("top", DynamicCollectionComponent.restingAnchor("bottom", true))
        assertEquals("bottom", DynamicCollectionComponent.restingAnchor("top", true))
        assertEquals("center", DynamicCollectionComponent.restingAnchor("center", true))
        assertNull(DynamicCollectionComponent.restingAnchor(null, true))
    }

    /**
     * The cell a resting anchor names: center the middle cell; bottom the
     * cell drawn last — the last section's last cell, or, emitted
     * last-first (reverseLayout), the first section's last cell. Until
     * jsonui-cli 1.9.0 a reversed bottom went to the last cell (b7), which a
     * reversed list draws at its visual top.
     */
    @Test
    fun theRestingAnchorsCell() {
        assertEquals(12, DynamicCollectionComponent.restingAnchorCell("bottom", list))            // b7
        assertEquals(6, DynamicCollectionComponent.restingAnchorCell("center", list))             // b1
        assertEquals(4, DynamicCollectionComponent.restingAnchorCell("bottom", list.reversed()))  // a4, drawn last
        val emptyFirst = listOf(list[0].copy(cells = emptyList()), list[1])
        assertEquals(7, DynamicCollectionComponent.restingAnchorCell("bottom", emptyFirst.reversed()))  // b7: A has none
        assertNull(DynamicCollectionComponent.restingAnchorCell("bottom", emptyList()))
    }

    /** The non-lazy routes' drawn cells: a section's when it names a cell, in section order. */
    @Test
    fun theNonLazyRoutesDrawnCells() {
        val declared = JsonParser.parseString("""[{"cell": "c"}, {"header": "h"}, {"cell": "c"}]""").asJsonArray
        val source = CollectionDataSource(
            sections = listOf(
                CollectionDataSection(cells = CollectionDataSection.CellData("c", a)),
                CollectionDataSection(cells = CollectionDataSection.CellData("c", cells("z0"))),
                CollectionDataSection(cells = CollectionDataSection.CellData("c", b)),
            )
        )
        val drawn = DynamicCollectionComponent.drawnCells(declared, source)
        assertEquals(a + b, drawn)
        assertEquals(ScrollCell.Cell(6), DynamicCollectionComponent.scrollCell("x1", drawn, "key", legacy = false))
    }

    /** The non-lazy routes' drawn sections' sizes, for the reversed EAGER Column's resting cell (round 13). */
    @Test
    fun theNonLazyRoutesDrawnSectionSizes() {
        val declared = JsonParser.parseString("""[{"cell": "c"}, {"header": "h"}, {"cell": "c"}]""").asJsonArray
        val source = CollectionDataSource(
            sections = listOf(
                CollectionDataSection(cells = CollectionDataSection.CellData("c", emptyList())),
                CollectionDataSection(cells = CollectionDataSection.CellData("c", cells("z0"))),
                CollectionDataSection(cells = CollectionDataSection.CellData("c", b)),
            )
        )
        assertEquals(listOf(0, 8), DynamicCollectionComponent.drawnSectionSizes(declared, source))
    }
}
