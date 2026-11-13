package com.kotlinjsonui.dynamic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.data.CollectionDataSection
import com.kotlinjsonui.data.CollectionDataSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A reorderable Collection on the Dynamic face (ticket jui-collection-drag-to-reorder): `reorderable`, `onReorder`,
 * `reorderHandle` and `reorderLockedProperty` read from the layout, on the NONE column and the LAZY (grid) route,
 * with the cell's root carrying the accessibility moves. Rows of 64 dp (reorder_probe_cell.json, its root clickable).
 */
@RunWith(AndroidJUnit4::class)
class DynamicCollectionReorderTest {
    @get:Rule val rule = createComposeRule()

    private val moves = mutableListOf<List<Int>>()
    private val handler: (Int, Int, Int) -> Unit = { s, f, t -> moves.add(listOf(s, f, t)) }

    private fun items(n: Int, fixed: Int? = null) = CollectionDataSource(
        sections = listOf(CollectionDataSection(cells = CollectionDataSection.CellData(
            viewName = "reorder_probe_cell",
            data = List(n) { mapOf<String, Any>("title" to "row$it", "cellId" to "c$it", "fixed" to (it == fixed), "onRowTap" to {}) },
        ))),
    )

    private fun collection(lazy: String, extra: String = ""): JsonObject = JsonParser.parseString(
        """
        { "type": "Collection", "id": "rows", "width": "matchParent", ${if (lazy == "none") "\"height\": \"wrapContent\"" else "\"height\": 600"},
          "lazy": "$lazy", "items": "@{items}", "sections": [{ "cell": "reorder_probe_cell" }], "cellIdProperty": "cellId",
          "reorderable": "@{editing}", "onReorder": "@{onReorder}" $extra }
        """.trimIndent(),
    ).asJsonObject

    private fun show(json: JsonObject, data: Map<String, Any>, outerScroll: Boolean = false) = rule.setContent {
        if (outerScroll) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) { DynamicView(json = json, data = data) }
        } else DynamicView(json = json, data = data)
    }

    private fun drag(tag: String, rows: Float, at: Offset? = null) {
        rule.onNodeWithTag(tag, true).performTouchInput {
            down(at ?: center)
            advanceEventTime(viewConfiguration.longPressTimeoutMillis + 100)
            repeat(20) { moveBy(Offset(0f, 64.dp.toPx() * rows / 20)); advanceEventTime(16) }
            up()
        }
        rule.waitForIdle()
    }

    private fun data(n: Int, editing: Boolean = true, fixed: Int? = null) =
        mapOf("items" to items(n, fixed), "editing" to editing, "onReorder" to handler)

    @Test fun a_none_column_in_an_outer_scroll_reports_the_move() {
        show(collection("none"), data(6), outerScroll = true)
        drag("rows_item_1", 2.2f)
        assertEquals(listOf(listOf(0, 1, 3)), moves)
    }

    @Test fun the_lazy_route_reports_the_move() {
        show(collection("lazy"), data(30))
        drag("rows_item_1", 2.2f)
        assertEquals(listOf(listOf(0, 1, 3)), moves)
    }

    @Test fun control_reorderable_false_moves_nothing() {
        show(collection("lazy"), data(30, editing = false))
        drag("rows_item_1", 2.2f)
        assertTrue("moves $moves", moves.isEmpty())
    }

    @Test fun with_a_handle_only_the_handle_picks_a_row_up() {
        show(collection("none", ", \"reorderHandle\": \"grip\""), data(6), outerScroll = true)
        drag("rows_item_1", 2.2f, at = Offset(40f, 60f))            // the row's left: not the grip
        assertTrue("moves $moves", moves.isEmpty())
        rule.onAllNodesWithTag("grip", true)[0].performTouchInput {  // row 0's grip
            down(center)
            advanceEventTime(viewConfiguration.longPressTimeoutMillis + 100)
            repeat(20) { moveBy(Offset(0f, 64.dp.toPx() * 2.2f / 20)); advanceEventTime(16) }
            up()
        }
        rule.waitForIdle()
        assertEquals(listOf(listOf(0, 0, 2)), moves)
    }

    @Test fun a_locked_row_by_the_cell_data_is_not_passed() {
        show(collection("none", ", \"reorderLockedProperty\": \"fixed\""), data(6, fixed = 2), outerScroll = true)
        drag("rows_item_0", 4f)
        assertEquals(listOf(listOf(0, 0, 1)), moves)
    }

    @Test fun the_cells_root_offers_the_accessibility_moves() {
        show(collection("lazy"), data(5))
        val actions = rule.onNodeWithTag("rows_item_1").fetchSemanticsNode().config.getOrNull(SemanticsActions.CustomActions)
        val labels = actions?.map { it.label }.orEmpty()
        assertEquals(2, labels.size)
        rule.runOnIdle { actions!![1].action() }
        assertEquals(listOf(listOf(0, 1, 2)), moves)
    }
}
