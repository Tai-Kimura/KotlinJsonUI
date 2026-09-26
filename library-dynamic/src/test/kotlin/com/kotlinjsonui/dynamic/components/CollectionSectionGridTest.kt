package com.kotlinjsonui.dynamic.components

import com.kotlinjsonui.dynamic.components.DynamicCollectionComponent.Companion.GridRow
import com.kotlinjsonui.dynamic.components.DynamicCollectionComponent.Companion.SectionShape
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Declared sections with columns > 1 are a grid each (4f ruling,
 * 2026-09-26; sjui draws a LazyVGrid per section, on the lazy and the
 * non-lazy routes, and SwiftJsonUI Dynamic was measured drawing the same).
 *
 * Before: the lazy route put every section's cells into one
 * LazyVerticalGrid with nothing between them, so a section's first cell
 * continued the previous section's last row; the eager / lazy:none /
 * wrapContent route drew a Column of one cell per row, whatever `columns`
 * said — and kjui's codegen draws a grid for eager, so Android's Debug and
 * Release differed there.
 *
 * What is pinned here is the arithmetic the two routes draw from; the
 * drawing is Compose and is pinned on a device (DynamicCollectionSectionGridTest).
 */
class CollectionSectionGridTest {

    private fun breaks(gridColumns: Int, vararg shapes: SectionShape) =
        DynamicCollectionComponent.sectionBreakSpans(shapes.toList(), gridColumns)

    private fun cells(n: Int, span: Int = 1, header: Boolean = false, footer: Boolean = false) =
        SectionShape(cells = n, itemSpan = span, header = header, footer = footer)

    // ── the lazy route: a section starts a new row ───────────────────

    /** [a] then [b c] in 2 columns: a leaves one column empty, taken before b. */
    @Test
    fun aSectionAfterAPartFilledRowStartsANewRow() {
        assertEquals(listOf(0, 1), breaks(2, cells(1), cells(2)))
        assertEquals(listOf(0, 2, 1), breaks(3, cells(4), cells(2), cells(1)))
    }

    /** A full last row needs nothing: adding an item would add a row. */
    @Test
    fun aSectionAfterAFullRowNeedsNoBreak() {
        assertEquals(listOf(0, 0), breaks(2, cells(2), cells(3)))
    }

    /** A header (full span) or a footer already starts a new row. */
    @Test
    fun aHeaderOrFooterAlreadyBreaksTheRow() {
        assertEquals(listOf(0, 0), breaks(2, cells(1), cells(2, header = true)))
        assertEquals(listOf(0, 0), breaks(2, cells(1, footer = true), cells(2)))
    }

    /** A section that draws nothing leaves the row as it was; the next one breaks it. */
    @Test
    fun anEmptySectionDoesNotHideAPartFilledRow() {
        assertEquals(listOf(0, 0, 1), breaks(2, cells(1), cells(0), cells(1)))
    }

    /** A section's own `columns` (declared: sections[].columns) sets its span in the LCM grid. */
    @Test
    fun aSectionsOwnColumnsAreHonoured() {
        // Sections of 3 and 2 columns in a 6-span grid: 1 cell of span 2 leaves 4.
        assertEquals(listOf(0, 4), breaks(6, cells(1, span = 2), cells(2, span = 3)))
    }

    // ── the non-lazy route: a grid per section ───────────────────────

    private fun rows(counts: List<Int>, columns: List<Int>, oneGrid: Boolean = false) =
        DynamicCollectionComponent.nonLazyGridRows(counts, columns, oneGrid)

    @Test
    fun eachSectionIsAGridOfItsOwnColumns() {
        assertEquals(
            listOf(
                listOf(GridRow(listOf(0 to 0), 2)),
                listOf(GridRow(listOf(1 to 0, 1 to 1), 2), GridRow(listOf(1 to 2), 2)),
            ),
            rows(listOf(1, 3), listOf(2, 2))
        )
        // Mixed columns: each section keeps its own.
        assertEquals(
            listOf(
                listOf(GridRow(listOf(0 to 0), 1), GridRow(listOf(0 to 1), 1)),
                listOf(GridRow(listOf(1 to 0, 1 to 1, 1 to 2), 3)),
            ),
            rows(listOf(2, 3), listOf(1, 3))
        )
    }

    /** Control: one column is one cell per row, as before. */
    @Test
    fun oneColumnIsOneCellPerRow() {
        assertEquals(
            listOf(listOf(GridRow(listOf(0 to 0), 1), GridRow(listOf(0 to 1), 1))),
            rows(listOf(2), listOf(1))
        )
    }

    /** The legacy shape (no declared sections) stays one grid across data sections. */
    @Test
    fun theLegacyShapeIsOneGrid() {
        assertEquals(
            listOf(
                listOf(GridRow(listOf(0 to 0, 1 to 0), 2), GridRow(listOf(1 to 1), 2)),
                emptyList(),
            ),
            rows(listOf(1, 2), listOf(2, 2), oneGrid = true)
        )
    }
}
