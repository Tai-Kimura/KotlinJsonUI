package com.kotlinjsonui.dynamic.components

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.kotlinjsonui.data.CollectionDataSection
import com.kotlinjsonui.data.CollectionDataSource
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.components.DynamicCollectionComponent.Companion.CellRoute
import com.kotlinjsonui.dynamic.generated.CollectionAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * A Collection without `sections` — the cells, header and footer named on
 * the Collection itself (cellClasses / headerClasses / footerClasses) — read
 * the way sjui's SwiftUI codegen draws it (4f ruling, 2026-09-26: KotlinJsonUI
 * Dynamic follows sjui's table, route by route; SwiftJsonUI Dynamic reads it
 * the same way).
 *
 * Before: a Collection without `sections` had no data source, so a single
 * cellClass drew nothing on any route, and headerClasses / footerClasses were
 * read into locals nothing used.
 *
 * What is pinned here is the plan every route draws from; the drawing is
 * Compose and is pinned on a device (DynamicCollectionLegacyShapeTest).
 */
class CollectionLegacyShapeTest {

    private fun attrs(json: String): Pair<CollectionAttributes, JsonArray?> {
        val node = Gson().fromJson(json, JsonObject::class.java)
        val sections = if (node.has("sections")) node.getAsJsonArray("sections") else null
        return CollectionAttributes.parse(TypedAttrs.toAttrMap(node)) to sections
    }

    private fun plan(json: String, source: CollectionDataSource?): DynamicCollectionComponent.Companion.CellPlan {
        val (a, sections) = attrs(json)
        return DynamicCollectionComponent.cellPlan(a, sections, source)
    }

    /** Three data sections of 1, 2 and 1 cells. */
    private val threeSections = CollectionDataSource(
        sections = listOf(1, 2, 1).map { n ->
            CollectionDataSection(
                cells = CollectionDataSection.CellData("probe_cell", List(n) { mapOf<String, Any>("t" to it) })
            )
        }
    )

    /** The cell each section config names, per route. */
    private fun cells(p: DynamicCollectionComponent.Companion.CellPlan, route: CellRoute): List<String?>? =
        p.sectionsFor(route)?.map { it.asJsonObject.get("cell")?.asString }

    private val vertical = listOf(CellRoute.NON_LAZY_COLUMN, CellRoute.LAZY_VERTICAL_GRID)
    private val horizontalOrFlow =
        listOf(CellRoute.FLOW, CellRoute.NON_LAZY_ROW, CellRoute.LAZY_ROW, CellRoute.LAZY_HORIZONTAL_GRID)

    // ── cellClasses ──────────────────────────────────────────────────

    @Test
    fun aSingleCellClassDrawsEveryDataSectionOnTheVerticalRoutes() {
        val p = plan("""{"type":"Collection","items":"@{items}","cellClasses":["probe_cell"]}""", threeSections)
        assertNotNull("the data source is read without sections", p.dataSource)
        for (route in vertical) {
            assertEquals(route.name, List(3) { "probe_cell" }, cells(p, route))
        }
    }

    @Test
    fun theHorizontalAndFlowRoutesDrawTheFirstDataSection() {
        val p = plan("""{"type":"Collection","items":"@{items}","cellClasses":["probe_cell"]}""", threeSections)
        for (route in horizontalOrFlow) {
            assertEquals(route.name, listOf("probe_cell"), cells(p, route))
        }
    }

    @Test
    fun pagingDrawsNothingWithoutSections() {
        val p = plan("""{"type":"Collection","items":"@{items}","cellClasses":["probe_cell"]}""", threeSections)
        assertNull(p.sectionsFor(CellRoute.PAGING))
    }

    @Test
    fun aClassNameObjectNamesTheCellToo() {
        val p = plan("""{"type":"Collection","items":"@{items}","cellClasses":[{"className":"probe_cell"}]}""", threeSections)
        assertEquals("probe_cell", p.legacyCell)
    }

    /** Several cellClasses and no sections: the build refuses the layout; no cell is guessed. */
    @Test
    fun severalCellClassesNameNoCell() {
        val p = plan("""{"type":"Collection","items":"@{items}","cellClasses":["probe_cell","other"]}""", threeSections)
        assertNull(p.legacyCell)
        for (route in CellRoute.values()) {
            assertTrue(route.name, cells(p, route).isNullOrEmpty())
        }
    }

    /** Declared sections decide, on every route, as before. */
    @Test
    fun sectionsWinOverCellClassesAndTheirEdges() {
        val p = plan(
            """{"type":"Collection","items":"@{items}","cellClasses":["other"],"headerClasses":["h"],
               "footerClasses":["f"],"sections":[{"cell":"probe_cell"}]}""",
            threeSections
        )
        for (route in CellRoute.values()) {
            assertEquals(route.name, listOf("probe_cell"), cells(p, route))
            assertNull(route.name, p.headerFor(route))
            assertNull(route.name, p.footerFor(route))
        }
    }

    // ── headerClasses / footerClasses ────────────────────────────────

    @Test
    fun theHeaderAndFooterAreDrawnOnTheVerticalRoutesOnly() {
        val p = plan(
            """{"type":"Collection","items":"@{items}","cellClasses":["probe_cell"],
               "headerClasses":["probe_header"],"footerClasses":["probe_footer"]}""",
            threeSections
        )
        for (route in vertical) {
            assertEquals(route.name, "probe_header", p.headerFor(route))
            assertEquals(route.name, "probe_footer", p.footerFor(route))
        }
        for (route in horizontalOrFlow + CellRoute.PAGING) {
            assertNull(route.name, p.headerFor(route))
            assertNull(route.name, p.footerFor(route))
        }
    }

    // ── no items source ──────────────────────────────────────────────

    /** The codegen draws the legacy container whatever `items` says. */
    @Test
    fun withNoItemsTheLegacyShapeStillHasItsContainerAndEdges() {
        val p = plan("""{"type":"Collection","headerClasses":["probe_header"],"footerClasses":["probe_footer"]}""", null)
        val source = p.dataSource
        assertNotNull(source)
        assertEquals(0, source!!.sections.size)
        assertEquals("probe_header", p.headerFor(CellRoute.LAZY_VERTICAL_GRID))
        assertEquals("probe_footer", p.footerFor(CellRoute.NON_LAZY_COLUMN))
        for (route in CellRoute.values().filter { it != CellRoute.PAGING }) {
            assertEquals(route.name, 0, p.sectionsFor(route)?.size())
        }
    }

    /** Control: a Collection that declares neither keeps no source, as before. */
    @Test
    fun withNothingDeclaredThereIsNoSource() {
        val p = plan("""{"type":"Collection","items":"@{items}"}""", threeSections)
        assertNull(p.dataSource)
        for (route in CellRoute.values()) assertNull(route.name, p.sectionsFor(route))
    }

    /** Control: a sectioned Collection with no source keeps none, as before. */
    @Test
    fun aSectionedCollectionWithNoItemsKeepsNoSource() {
        val p = plan("""{"type":"Collection","items":"@{items}","sections":[{"cell":"probe_cell"}]}""", null)
        assertNull(p.dataSource)
    }
}
