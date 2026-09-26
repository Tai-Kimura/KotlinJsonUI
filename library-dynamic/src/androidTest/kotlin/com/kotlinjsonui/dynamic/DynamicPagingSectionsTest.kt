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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A paging Collection draws one page per cell, every drawn section's cells in
 * order (4f ruling, 2026-09-26, round 6). A page is found by `currentPage`,
 * which counts across the sections. Pinned here:
 * - the page count is the pages drawn — with more data sections than
 *   declared ones the pager counted every data section's cells while it drew
 *   the declared ones', and a page past them read past the end of its list;
 * - the class-list shape (cellClasses, no `sections`) is one section — the
 *   first data section — as on the other one-section routes; it drew no page.
 *
 * Each case shows the pager at a bound `currentPage` and reads the cell
 * drawn there (`collection_probe_title_cell`: a Label of the item's title).
 */
@RunWith(AndroidJUnit4::class)
class DynamicPagingSectionsTest {

    @get:Rule
    val rule = createComposeRule()

    private val cell = "collection_probe_title_cell"

    private fun source(vararg sections: List<String>) = CollectionDataSource(
        sections = sections.map { titles ->
            CollectionDataSection(cells = CollectionDataSection.CellData(cell, titles.map { mapOf("title" to it) }))
        }
    )

    private var shown by mutableStateOf<Pair<JsonObject, Map<String, Any>>?>(null)
    private var composed = false

    /** The titles drawn with the pager at [page]. */
    private fun drawnAt(attrs: String, items: CollectionDataSource, page: Int): List<String> {
        val json = JsonParser.parseString(
            """{"type": "Collection", "id": "pager", "layout": "horizontal", "paging": true,
               "width": 300, "height": 120, "items": "@{items}", "currentPage": "@{page}" $attrs}"""
        ).asJsonObject
        if (!composed) {
            rule.setContent { shown?.let { (j, d) -> key(j, d) { DynamicView(json = j, data = d) } } }
            composed = true
        }
        rule.runOnIdle { shown = json to mapOf("items" to items, "page" to page) }
        rule.waitForIdle()
        return listOf("a0", "a1", "b0", "b1", "c0").filter {
            rule.onAllNodesWithText(it).fetchSemanticsNodes().any { node -> node.positionInRoot.x in -1f..1f }
        }
    }

    @Test
    fun moreDataSectionsThanDeclaredPagesTheDeclaredOnes() {
        // One declared section, two data sections: pages a0, a1. Page 1 is a1;
        // the pager's page count is 2, so a bound 3 lands on the last page.
        val items = source(listOf("a0", "a1"), listOf("b0", "b1"))
        val one = """, "sections": [{"cell": "$cell"}]"""
        assertEquals(listOf("a1"), drawnAt(one, items, 1))
        assertEquals(listOf("a1"), drawnAt(one, items, 3))
    }

    @Test
    fun everyDeclaredSectionInOrder() {
        val items = source(listOf("a0", "a1"), listOf("b0", "b1"))
        val two = """, "sections": [{"cell": "$cell"}, {"cell": "$cell"}]"""
        assertEquals(listOf("a1"), drawnAt(two, items, 1))
        assertEquals(listOf("b0"), drawnAt(two, items, 2))
        assertEquals(listOf("b1"), drawnAt(two, items, 3))
    }

    @Test
    fun theClassListShapeIsOneSection() {
        val items = source(listOf("a0", "a1"), listOf("b0"))
        val classList = """, "cellClasses": ["$cell"]"""
        assertEquals(listOf("a0"), drawnAt(classList, items, 0))
        assertEquals(listOf("a1"), drawnAt(classList, items, 1))
        assertEquals(listOf("a1"), drawnAt(classList, items, 2))
    }
}
