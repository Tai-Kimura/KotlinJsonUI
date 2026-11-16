package com.kotlinjsonui.dynamic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
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
 * A cell's address, `<id>_item_<n>`, counts the cells of the drawn sections
 * before its own, then its index there — the n a scrollTo Int names (jsonui-cli
 * 2.5.39, the orchestrator's ruling (a2), ticket jui-collection-item-identifier-
 * repeats-per-section). Until then a second section's cells took `_item_0`,
 * `_item_1`, ... again on every route but the pager, and a test addressing
 * `list_item_0` reached whichever of two cells it found first.
 *
 * Two sections, section 0 one cell (A0) and section 1 two (B0, B1): on each
 * route `list_item_0` holds A0, `_item_1` B0, `_item_2` B1, one node each,
 * and there is no `_item_3`. The pager's own arm is DynamicPagingSectionsTest.
 * The cell's root is the Label itself (collection_probe_title_cell), so the
 * title is on the addressed node or under it.
 */
@RunWith(AndroidJUnit4::class)
class DynamicCollectionCellAddressTest {

    @get:Rule
    val rule = createComposeRule()

    private val cell = "collection_probe_title_cell"

    private var shown by mutableStateOf<Pair<JsonObject, Map<String, Any>>?>(null)
    private var composed = false

    private val items = CollectionDataSource(
        sections = listOf(listOf("A0"), listOf("B0", "B1")).map { titles ->
            CollectionDataSection(cells = CollectionDataSection.CellData(cell, titles.map { mapOf("title" to it) }))
        }
    )

    /** For `_item_0` … `_item_3`: the titles each address holds (one node per address, else the count). */
    private fun addresses(attrs: String): List<String> {
        val json = JsonParser.parseString(
            """{"type": "Collection", "id": "list", "width": 320, "height": 480, "items": "@{items}",
               "sections": [{"cell": "$cell"}, {"cell": "$cell"}] $attrs}"""
        ).asJsonObject
        if (!composed) {
            rule.setContent { shown?.let { (j, d) -> key(j, d) { DynamicView(json = j, data = d) } } }
            composed = true
        }
        rule.runOnIdle { shown = json to mapOf("items" to items) }
        rule.waitForIdle()
        return (0..3).map { n ->
            val tag = "list_item_$n"
            val nodes = rule.onAllNodes(hasTestTag(tag), useUnmergedTree = true).fetchSemanticsNodes()
            when (nodes.size) {
                0 -> "-"
                1 -> listOf("A0", "B0", "B1").filter { title ->
                    rule.onAllNodes(hasTestTag(tag) and (hasText(title) or hasAnyDescendant(hasText(title))), useUnmergedTree = true)
                        .fetchSemanticsNodes().isNotEmpty()
                }.joinToString("+").ifEmpty { "?" }
                else -> "x${nodes.size}"
            }
        }
    }

    private val expected = listOf("A0", "B0", "B1", "-")

    @Test fun lazyVertical() = assertEquals(expected, addresses(""))
    @Test fun lazyVerticalTwoColumns() = assertEquals(expected, addresses(""", "columns": 2"""))
    @Test fun lazyVerticalReversed() = assertEquals(expected, addresses(""", "reverseLayout": true"""))
    @Test fun lazyHorizontal() = assertEquals(expected, addresses(""", "layout": "horizontal""""))
    @Test fun lazyHorizontalTwoLanes() = assertEquals(expected, addresses(""", "layout": "horizontal", "columns": 2"""))
    @Test fun nonLazyVertical() = assertEquals(expected, addresses(""", "lazy": "none""""))
    @Test fun nonLazyVerticalTwoColumns() = assertEquals(expected, addresses(""", "lazy": "none", "columns": 2"""))
    @Test fun nonLazyHorizontal() = assertEquals(expected, addresses(""", "layout": "horizontal", "lazy": "none""""))
    @Test fun nonLazyHorizontalTwoLanes() = assertEquals(expected, addresses(""", "layout": "horizontal", "lazy": "none", "columns": 2"""))
    @Test fun flow() = assertEquals(expected, addresses(""", "layout": "flow""""))
    @Test fun eager() = assertEquals(expected, addresses(""", "lazy": "eager""""))
}
