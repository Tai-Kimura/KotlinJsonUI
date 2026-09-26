package com.kotlinjsonui.dynamic

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Collection rows the dynamic face drew differently from what is declared
 * (audit 2026-09-26), measured in composition:
 *
 * - `scrollAnimated: false` moves the list to the scrollTo target at once;
 *   it was not read, so every scroll animated.
 * - A node-level `cell` template is not declared and no codegen draws it;
 *   this face drew ten cells from it. It draws none now — cells come from
 *   `sections[].cell` with an `items` data source.
 *
 * The JVM pins the parts that decide these (CollectionDeclaredRowsTest).
 */
@RunWith(AndroidJUnit4::class)
class DynamicCollectionDeclaredRowsTest {

    @get:Rule
    val rule = createComposeRule()

    private fun rows(scrollAnimated: String?) = JsonParser.parseString(
        """
        {
          "type": "Collection",
          "id": "rows",
          "width": "matchParent",
          "height": 200,
          "items": "@{items}",
          "sections": [{ "cell": "collection_probe_row_cell" }],
          "scrollTo": "@{target}"
          ${scrollAnimated?.let { ""","scrollAnimated": $it""" } ?: ""}
        }
        """.trimIndent()
    ).asJsonObject

    private fun composed(tag: String): Boolean =
        rule.onAllNodesWithTag(tag, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()

    /**
     * Frames after a scrollTo to row 80 (3200dp down a 200dp list) until the
     * row is composed. Instant: the next layout pass lands on it (≤ 3 frames
     * allows for the collector's dispatch). Animated: the list travels there
     * over many frames (a spring over 3000dp; ≥ 5 is far below it).
     */
    private fun framesToReachRow80(scrollAnimated: String?): Int {
        val target = MutableSharedFlow<Int>(extraBufferCapacity = 1)
        val json = rows(scrollAnimated)
        rule.setContent {
            DynamicView(
                json = json,
                data = mapOf(
                    "items" to probeItems(100, "collection_probe_row_cell"),
                    "target" to target
                )
            )
        }
        rule.waitForIdle()
        assertTrue("row 0 must be composed before the scroll", composed("rows_item_0"))
        assertTrue("row 80 must not be composed before the scroll", !composed("rows_item_80"))

        rule.mainClock.autoAdvance = false
        rule.runOnIdle { assertTrue(target.tryEmit(80)) }
        var frames = 0
        while (!composed("rows_item_80") && frames < 120) {
            rule.mainClock.advanceTimeByFrame()
            frames++
        }
        rule.mainClock.autoAdvance = true
        assertTrue("row 80 was never reached ($frames frames)", composed("rows_item_80"))
        return frames
    }

    @Test
    fun scrollAnimatedFalseReachesTheTargetAtOnce() {
        val frames = framesToReachRow80("false")
        assertTrue("an immediate scroll took $frames frames", frames <= 3)
    }

    /** Control: the declared default animates, so the same scroll takes many frames. */
    @Test
    fun scrollAnimatedDefaultAnimatesToTheTarget() {
        val frames = framesToReachRow80(null)
        assertTrue("an animated scroll took only $frames frames", frames >= 5)
    }

    @Test
    fun aNodeLevelCellTemplateDrawsNoCell() {
        val json = JsonParser.parseString(
            """
            {
              "type": "Collection",
              "id": "templated",
              "width": "matchParent",
              "height": 200,
              "cell": { "type": "Label", "text": "templateCell" }
            }
            """.trimIndent()
        ).asJsonObject
        rule.setContent { DynamicView(json = json, data = emptyMap()) }
        rule.waitForIdle()
        assertEquals(0, rule.onAllNodesWithText("templateCell").fetchSemanticsNodes().size)
    }

    /** Control: the declared shape draws its cells, so the count above can be non-zero. */
    @Test
    fun sectionsWithItemsDrawTheirCells() {
        val json = JsonParser.parseString(
            """
            {
              "type": "Collection",
              "id": "declared",
              "width": "matchParent",
              "height": 200,
              "items": "@{items}",
              "sections": [{ "cell": "collection_probe_page_cell" }]
            }
            """.trimIndent()
        ).asJsonObject
        rule.setContent {
            DynamicView(json = json, data = mapOf("items" to probeItems(3, "collection_probe_page_cell")))
        }
        rule.waitForIdle()
        assertEquals(3, rule.onAllNodesWithText("page").fetchSemanticsNodes().size)
    }
}
