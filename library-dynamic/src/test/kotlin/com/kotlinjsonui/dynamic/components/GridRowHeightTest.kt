package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * A non-lazy grid's row sizes to its tallest cell when a cell root is
 * `height: matchParent` (jsonui-cli ticket kjui-collection-grid-row-
 * matchparent-cell-height-unbounded): the decision the row draws from. The
 * drawing (the row at its tallest cell, not the screen's height) is pinned on
 * a device; this pins which rows take `height(IntrinsicSize.Min)`.
 */
class GridRowHeightTest {

    private fun json(s: String): JsonObject = JsonParser.parseString(s).asJsonObject

    private val fills = json("""{"type":"View","height":"matchParent","minHeight":56,"child":[{"type":"Label","text":"a"}]}""")
    private val wraps = json("""{"type":"View","height":"wrapContent","child":[{"type":"Label","text":"a"}]}""")

    @Test
    fun aRowWithAMatchParentRootEqualizes() {
        assertTrue(GridRowHeight.equalizes(listOf(fills, fills)))
        assertTrue(GridRowHeight.equalizes(listOf(fills, wraps)))
    }

    @Test
    fun aRowWithoutOneIsLeftAsItWas() {
        assertFalse(GridRowHeight.equalizes(listOf(wraps, wraps)))
        assertFalse(GridRowHeight.equalizes(listOf(null, null)))
        assertFalse(GridRowHeight.equalizes(listOf(json("""{"type":"View","height":120}"""))))
    }

    @Test
    fun aFrameHeightCountsAsTheRootsHeight() {
        assertTrue(GridRowHeight.rootFillsHeight(json("""{"type":"View","frame":{"height":"matchParent"}}""")))
    }

    /** IntrinsicSize.Min throws on a SubcomposeLayout, so a row holding one is not asked. */
    @Test
    fun aCellHoldingASubcomposeLayoutIsNotAsked() {
        val lazyList = json("""{"type":"View","height":"matchParent","child":[{"type":"View","children":[{"type":"Collection","height":200}]}]}""")
        assertFalse(GridRowHeight.equalizes(listOf(lazyList, fills)))
        val scroll = json("""{"type":"View","height":"matchParent","child":[{"type":"ScrollView"}]}""")
        assertFalse(GridRowHeight.equalizes(listOf(scroll)))
    }

    /** The eager shapes kjui_tools' subcompose_node? reads as no SubcomposeLayout. */
    @Test
    fun eagerCollectionsAreNotSubcompose() {
        val cases = mapOf(
            """{"type":"Collection","lazy":"none"}""" to false,
            """{"type":"Collection","height":"wrapContent"}""" to false,
            """{"type":"Collection","orientation":"horizontal","height":"wrapContent"}""" to true,
            """{"type":"Collection","layout":"flow","height":"wrapContent"}""" to false,
            """{"type":"Collection","layout":"flow","height":"matchParent"}""" to true,
            """{"type":"Collection","layout":"flow","height":"@{h}"}""" to true,
            """{"type":"Collection","layout":"flow","height":"matchParent","maxHeight":300}""" to false,
            """{"type":"Table"}""" to true,
            """{"type":"View"}""" to false,
        )
        cases.forEach { (node, expected) -> assertEquals(node, expected, GridRowHeight.subcomposes(json(node))) }
    }

    // ── a cell holding an image its picture sizes: its own height (jsonui-cli kjui-collection-lazy-grid-and-flow-
    // rows-do-not-align-cell-heights; the table is shared/core/grid_row_height.json, vendored) ──

    private fun withImage(image: String) =
        json("""{"type":"View","height":"matchParent","child":[{"type":"View","height":"matchParent","child":[$image]}]}""")

    @Test
    fun anImageItsPictureSizesMakesTheCellsItsOwnHeight() {
        val network = withImage("""{"type":"NetworkImage","width":"matchParent","height":"matchParent","src":"@{u}"}""")
        assertEquals(GridRowHeight.Mode.OWN, GridRowHeight.mode(listOf(network, network)))
        assertEquals(GridRowHeight.Mode.OWN, GridRowHeight.mode(listOf(fills, network)))
        assertFalse(GridRowHeight.equalizes(listOf(network)))
        listOf(
            """{"type":"Image","width":"matchParent","srcName":"x"}""",
            """{"type":"ImageView","height":"wrapContent","srcName":"x"}""",
            """{"type":"CircleImage","width":40,"height":"matchParent","src":"@{u}"}""",
            """{"type":"AsyncImage","width":"matchParent","height":"@{h}","src":"@{u}"}""",
        ).forEach { assertEquals(it, GridRowHeight.Mode.OWN, GridRowHeight.mode(listOf(withImage(it)))) }
    }

    @Test
    fun anImageOfAFixedHeightLeavesTheRowAligned() {
        listOf("120", "\"120\"", "\"96.5\"").forEach { h ->
            val cell = withImage("""{"type":"NetworkImage","width":"matchParent","height":$h,"src":"@{u}"}""")
            assertEquals(h, GridRowHeight.Mode.FILL, GridRowHeight.mode(listOf(cell)))
        }
    }

    @Test
    fun aRowWithNoMatchParentRootOrASubcomposeLayoutIsAsItWas() {
        val network = """{"type":"NetworkImage","height":"matchParent","src":"@{u}"}"""
        assertEquals(GridRowHeight.Mode.NONE, GridRowHeight.mode(listOf(json("""{"type":"View","height":"wrapContent","child":[$network]}"""))))
        val lazy = json("""{"type":"View","height":"matchParent","child":[$network,{"type":"Collection","items":"@{rows}"}]}""")
        assertEquals(GridRowHeight.Mode.NONE, GridRowHeight.mode(listOf(lazy)))
    }
}
