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
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A flow Collection with two or more sections draws each section as its own
 * wrap, one under the other (4f ruling, 2026-09-26; sjui's FlowLayout per
 * section in a VStack, kjui codegen's FlowRow per section in a Column):
 * section 2 starts a new line. Between the section blocks, the line spacing
 * (jsonui-cli attribute_semantics.json -> collectionSpacing). Before, one
 * FlowRow held every section, so section 2 continued section 1's last line.
 *
 * Cells are 40 x 20 dp (cellWidth / cellHeight) in a 300 dp wide flow — a
 * line holds far more than section 1's three.
 */
@RunWith(AndroidJUnit4::class)
class DynamicCollectionFlowSectionsTest {

    @get:Rule
    val rule = createComposeRule()

    private val cell = "collection_probe_title_cell"

    private var shown by mutableStateOf<Pair<JsonObject, Map<String, Any>>?>(null)
    private var composed = false

    private fun show(attrs: String) {
        val json = JsonParser.parseString(
            """{"type": "Collection", "id": "chips", "layout": "flow", "width": 300, "items": "@{items}",
               "cellWidth": 40, "cellHeight": 20,
               "sections": [{"cell": "$cell"}, {"cell": "$cell"}] $attrs}"""
        ).asJsonObject
        if (!composed) {
            rule.setContent { shown?.let { (j, d) -> key(j) { DynamicView(json = j, data = d) } } }
            composed = true
        }
        rule.runOnIdle { shown = json to mapOf("items" to twoSections) }
        rule.waitForIdle()
    }

    /** Data sections [a0, a1, a2] and [b0, b1]. */
    private val twoSections = CollectionDataSource(
        sections = listOf(
            CollectionDataSection(cells = CollectionDataSection.CellData(cell, listOf("a0", "a1", "a2").map { mapOf("title" to it) })),
            CollectionDataSection(cells = CollectionDataSection.CellData(cell, listOf("b0", "b1").map { mapOf("title" to it) })),
        )
    )

    /** The cell box's top-left: the label sits at the box's top-left (gravity top-start). */
    private fun at(text: String): Offset =
        rule.onAllNodesWithText(text).fetchSemanticsNodes().single().positionInRoot

    private fun dp(px: Float): Float = px / rule.density.density

    private val routes = listOf(
        """, "height": 300""",            // lazy in effect, self-bounded: scrolls
        """, "lazy": "none"""",            // wraps, the parent scrolls
        """, "height": "wrapContent"""",
    )

    /** Every route is measured before any is judged, so a failure names them all. */
    @Test
    fun eachSectionIsItsOwnWrapOnEveryRoute() {
        val wrong = mutableListOf<String>()
        for (route in routes) {
            show(route)
            val (a0, a2, b0, b1) = listOf(at("a0"), at("a2"), at("b0"), at("b1"))
            val placed = "a0=(${dp(a0.x)}, ${dp(a0.y)}) b0=(${dp(b0.x)}, ${dp(b0.y)}) dp"
            if (kotlin.math.abs(a0.y - a2.y) > 1f) wrong += "a2 not on a0's line$route: $placed"
            if (kotlin.math.abs(a0.x - b0.x) > 1f || b0.y <= a0.y + 1f) wrong += "b0 does not start a line under section 1$route: $placed"
            if (kotlin.math.abs(b0.y - b1.y) > 1f) wrong += "b1 not beside b0$route: $placed"
        }
        assertTrue(wrong.joinToString("\n"), wrong.isEmpty())
    }

    @Test
    fun theSectionBlocksAreSpacedAsTheLines() {
        val wrong = mutableListOf<String>()
        fun expect(what: String, want: Float, got: Float) {
            if (kotlin.math.abs(want - got) > 0.5f) wrong += "$what: want $want dp, drew $got dp"
        }
        for (route in routes) {
            show(route)
            expect("undeclared, a0 to b0$route", 20f, dp(at("b0").y - at("a0").y))
            show(route + """, "lineSpacing": 6, "columnSpacing": 10""")
            expect("lineSpacing 6, a0 to b0$route", 26f, dp(at("b0").y - at("a0").y))
            expect("columnSpacing 10, a0 to a1$route", 50f, dp(at("a1").x - at("a0").x))
            show(route + """, "itemSpacing": 4""")
            expect("itemSpacing 4, a0 to b0$route", 24f, dp(at("b0").y - at("a0").y))
        }
        assertTrue(wrong.joinToString("\n"), wrong.isEmpty())
    }

    /**
     * A section's declared header and footer are rows of their own around its
     * wrap (4f ruling 2026-09-26, round 7): the header above at the leading
     * edge, the footer below, section 2's header under section 1's footer,
     * each a line (6 dp) apart. The flow drew neither until then. Section 2's
     * header is the title cell with "h2", so it reads apart from section 1's.
     */
    @Test
    fun aSectionsHeaderAndFooterAreRowsAroundItsWrap() {
        val edged = CollectionDataSource(
            sections = listOf(
                CollectionDataSection(
                    header = CollectionDataSection.HeaderFooterData("collection_probe_header", emptyMap()),
                    cells = CollectionDataSection.CellData(cell, listOf("a0", "a1").map { mapOf("title" to it) }),
                    footer = CollectionDataSection.HeaderFooterData("collection_probe_footer", emptyMap()),
                ),
                CollectionDataSection(
                    header = CollectionDataSection.HeaderFooterData(cell, mapOf("title" to "h2")),
                    cells = CollectionDataSection.CellData(cell, listOf(mapOf("title" to "b0"))),
                ),
            )
        )
        val wrong = mutableListOf<String>()
        fun expect(what: String, want: Float, got: Float) {
            if (kotlin.math.abs(want - got) > 0.5f) wrong += "$what: want $want dp, drew $got dp"
        }
        for (route in routes) {
            val json = JsonParser.parseString(
                """{"type": "Collection", "id": "chips", "layout": "flow", "width": 300, "items": "@{items}",
                   "cellWidth": 40, "cellHeight": 20, "lineSpacing": 6,
                   "sections": [{"cell": "$cell", "header": "collection_probe_header", "footer": "collection_probe_footer"},
                                {"cell": "$cell", "header": "$cell"}] $route}"""
            ).asJsonObject
            if (!composed) {
                rule.setContent { shown?.let { (j, d) -> key(j) { DynamicView(json = j, data = d) } } }
                composed = true
            }
            rule.runOnIdle { shown = json to mapOf("items" to edged) }
            rule.waitForIdle()
            val nodes = listOf("headerProbe", "footerProbe", "h2").associateWith {
                rule.onAllNodesWithText(it).fetchSemanticsNodes().singleOrNull()
            }
            if (nodes.values.any { it == null }) { wrong += "not drawn$route: ${nodes.filterValues { it == null }.keys}"; continue }
            val header = nodes.getValue("headerProbe")!!; val footer = nodes.getValue("footerProbe")!!; val h2 = nodes.getValue("h2")!!
            val (a0, a1, b0) = listOf(at("a0"), at("a1"), at("b0"))
            expect("the header at the leading edge$route", dp(a0.x), dp(header.positionInRoot.x))
            expect("header, then the wrap$route", 6f, dp(a0.y - (header.positionInRoot.y + header.size.height)))
            expect("the cells share their line$route", dp(a0.y), dp(a1.y))
            expect("the wrap, then the footer$route", 6f, dp(footer.positionInRoot.y - (a0.y + 20f * rule.density.density)))
            expect("section 2's header under section 1's footer$route", 6f, dp(h2.positionInRoot.y - (footer.positionInRoot.y + footer.size.height)))
            expect("section 2's wrap under its header$route", 6f, dp(b0.y - (h2.positionInRoot.y + h2.size.height)))
        }
        // One declared section with a header and footer: the same rows (the
        // single-section flow is one FlowRow otherwise).
        for (route in routes) {
            val json = JsonParser.parseString(
                """{"type": "Collection", "id": "chips", "layout": "flow", "width": 300, "items": "@{items}",
                   "cellWidth": 40, "cellHeight": 20, "lineSpacing": 6,
                   "sections": [{"cell": "$cell", "header": "collection_probe_header", "footer": "collection_probe_footer"}] $route}"""
            ).asJsonObject
            rule.runOnIdle { shown = json to mapOf("items" to edged) }
            rule.waitForIdle()
            val header = rule.onAllNodesWithText("headerProbe").fetchSemanticsNodes().singleOrNull()
            val footer = rule.onAllNodesWithText("footerProbe").fetchSemanticsNodes().singleOrNull()
            if (header == null || footer == null) { wrong += "one section, not drawn$route"; continue }
            expect("one section: header, then the wrap$route", 6f, dp(at("a0").y - (header.positionInRoot.y + header.size.height)))
            expect("one section: the wrap, then the footer$route", 6f, dp(footer.positionInRoot.y - (at("a0").y + 20f * rule.density.density)))
        }
        assertTrue(wrong.joinToString("\n"), wrong.isEmpty())
    }
}
