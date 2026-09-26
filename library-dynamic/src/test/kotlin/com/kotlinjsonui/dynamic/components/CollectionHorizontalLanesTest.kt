package com.kotlinjsonui.dynamic.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Eager and lazy:none horizontal Collections draw lanes, as the lazy route's
 * LazyHorizontalGrid does and as SwiftJsonUI draws them on every horizontal
 * route (4f ruling, 2026-09-26): `columns` is the number of lanes, a
 * section's own `columns` its block's, cells fill a column top to bottom and
 * then the next, each section starts a new column, lanes are spaced by
 * columnSpacing (else itemSpacing) and the scroll axis by lineSpacing (else
 * itemSpacing).
 *
 * Before: these routes drew one Row whatever `columns` said.
 *
 * The arithmetic is pinned here (laneGridPlacement); the drawing is Compose
 * and is pinned on a device (DynamicCollectionHorizontalLanesTest).
 */
class CollectionHorizontalLanesTest {

    private fun place(widths: List<Int>, heights: List<Int>, lanes: Int, laneHeight: Int? = null) =
        DynamicCollectionComponent.laneGridPlacement(widths, heights, lanes, laneSpacing = 30, columnSpacing = 12, laneHeight = laneHeight)

    /** Three 40x20 cells in 2 lanes: a and b in column 0 (b under a), c at the top of column 1. */
    @Test
    fun cellsFillAColumnTopToBottomThenTheNext() {
        val p = place(listOf(40, 40, 40), listOf(20, 20, 20), lanes = 2)
        assertEquals(listOf(0, 0, 52), p.x)       // column 1 is 40 + 12 (lineSpacing's role) to the right
        assertEquals(listOf(0, 50, 0), p.y)       // lane 1 is 20 + 30 (columnSpacing's role) down
        assertEquals(92, p.width)
        assertEquals(70, p.height)
    }

    /**
     * A bounded height gives every lane an equal share, as GridCells.Fixed
     * does on the lazy route — whatever the cells' own heights (LaneGrid
     * measures them to the lane, so this also holds for a shorter cell).
     */
    @Test
    fun aBoundedHeightSharesItOutAmongTheLanes() {
        val p = place(listOf(40, 40, 40), listOf(20, 20, 20), lanes = 2, laneHeight = 85)
        assertEquals(listOf(0, 115, 0), p.y)
        assertEquals(200, p.height)
    }

    /** Unbounded, a lane is its tallest cell and a column its widest. */
    @Test
    fun lanesAndColumnsFitTheirLargestCell() {
        val p = place(listOf(40, 60, 30), listOf(20, 30, 25), lanes = 2)
        // Lane 0 holds cells 0 and 2 (max height 25); column 0 holds 0 and 1 (max width 60).
        assertEquals(listOf(0, 0, 72), p.x)
        assertEquals(listOf(0, 55, 0), p.y)
    }

    /** Three lanes, seven cells: columns of three. */
    @Test
    fun threeLanes() {
        val p = place(List(7) { 10 }, List(7) { 10 }, lanes = 3)
        assertEquals(listOf(0, 0, 0, 22, 22, 22, 44), p.x)
        assertEquals(listOf(0, 40, 80, 0, 40, 80, 0), p.y)
    }

    /** Control: one lane is one row, cells side by side — as the Row drew every Collection. */
    @Test
    fun oneLaneIsOneRow() {
        val p = place(listOf(40, 40, 40), listOf(20, 20, 20), lanes = 1)
        assertEquals(listOf(0, 52, 104), p.x)
        assertEquals(listOf(0, 0, 0), p.y)
    }

    // ── the wiring ───────────────────────────────────────────────────

    private val source = File(
        "src/main/kotlin/com/kotlinjsonui/dynamic/components/DynamicCollectionComponent.kt"
    )

    private fun code(): List<String> {
        assertTrue("source missing: ${source.absolutePath}", source.isFile)
        return source.readLines().map { it.trim() }.filterNot { it.startsWith("//") || it.startsWith("*") }
    }

    @Test
    fun theNonLazyRowDrawsLanesFromTheSectionOrTheCollectionColumns() {
        val lines = code()
        val start = lines.indexOfFirst { it.startsWith("private fun renderNonLazyRow(") }
        assertTrue("renderNonLazyRow not found", start >= 0)
        val end = lines.subList(start + 1, lines.size).indexOfFirst { it.startsWith("private fun ") || it.startsWith("@Composable") }
        val body = lines.subList(start, start + 1 + end)
        assertTrue(body.joinToString("\n"), body.contains("val lanes = maxOf(1, sectionObj.get(\"columns\")?.asInt ?: defaultColumns)"))
        assertTrue(body.joinToString("\n"), body.any { it.startsWith("LaneGrid(lanes = lanes, laneSpacing = laneSpacing, columnSpacing = columnSpacing)") })
        // The call: lanes by columnSpacing (else itemSpacing), the scroll axis by the rule.
        assertTrue(lines.contains("laneSpacing = horizontal.betweenLanes.dp,"))
        assertTrue(lines.contains("columnSpacing = scrollAxisSpacing,"))
        assertTrue(lines.contains("defaultColumns = defaultColumns,"))
    }
}
