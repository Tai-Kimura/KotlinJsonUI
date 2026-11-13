package com.kotlinjsonui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A reorderable Collection on Android (ticket jui-collection-drag-to-reorder): the library's drag next to a row's own
 * click and the scrolls around it, the rule (CollectionReorderRules) as it is drawn, and the accessibility moves.
 * Rows of 64 dp; the finger is the test's own touch input, measured on an emulator.
 */
@RunWith(AndroidJUnit4::class)
class CollectionReorderTest {
    @get:Rule val rule = createComposeRule()

    private val moves = mutableListOf<Triple<Int, Int, Int>>()
    private val clicks = mutableListOf<Int>()

    private fun reorder(usesHandle: Boolean = false) =
        CollectionReorder(isEnabled = true, usesHandle = usesHandle) { s, f, t -> moves.add(Triple(s, f, t)) }

    @Composable
    private fun Cell(index: Int, withHandle: Boolean = false) {
        Row(
            Modifier.collectionReorderActions().fillMaxWidth().height(64.dp).testTag("row_$index")
                .background(if (index % 2 == 0) Color(0xFFEEEEEE) else Color.White)
                .clickable { clicks.add(index) },
        ) {
            Text("row $index", Modifier.weight(1f))
            Box(Modifier.size(48.dp).testTag("handle_$index").background(Color.Gray)
                .then(if (withHandle) Modifier.collectionReorderHandle() else Modifier))
        }
    }

    private fun lazy(n: Int, locked: Set<Int> = emptySet(), usesHandle: Boolean = false) = rule.setContent {
        CollectionReorderContainer(reorder(usesHandle), Modifier.fillMaxSize()) {
            val state = rememberCollectionReorderState()
            LazyColumn(Modifier.fillMaxSize().testTag("list"), state = rememberLazyListState()) {
                collectionReorderRows(state, 0, (0 until n).map { "id$it" }, locked) { i -> Cell(i, usesHandle) }
            }
        }
    }

    private var pageScroll = 0

    private fun columnInAScroll(n: Int, containerHeight: Int? = null) = rule.setContent {
        val scroll = rememberScrollState()
        pageScroll = scroll.value
        Column(Modifier.fillMaxSize().verticalScroll(scroll)) {
            Box(Modifier.fillMaxWidth().height(80.dp))
            CollectionReorderContainer(reorder(), Modifier.testTag("container"), scrollsItself = false) {
                Column { CollectionReorderRows(0, (0 until n).map { "id$it" }) { i -> Cell(i) } }
            }
            Box(Modifier.fillMaxWidth().height((containerHeight ?: 1200).dp))
        }
    }

    private fun drag(tag: String, rows: Float, holdMs: Long = 0) {
        rule.onNodeWithTag(tag, true).performTouchInput {
            down(center)
            advanceEventTime(viewConfiguration.longPressTimeoutMillis + 100)
            val step = 64.dp.toPx() * rows / 20
            repeat(20) { moveBy(Offset(0f, step)); advanceEventTime(16) }
            if (holdMs > 0) { var t = 0L; while (t < holdMs) { moveBy(Offset(0f, 0f)); advanceEventTime(16); t += 16 } }
            up()
        }
        rule.waitForIdle()
    }

    // The row lands where the finger lets go, up as down: dy is the finger's own travel (ticket
    // jui-collection-drag-to-reorder; jsonui-test-runner's moveItem landed an upward move one row further).
    @Test fun up_and_down_the_row_lands_where_the_finger_lets_go() {
        lazy(30)
        for ((rows, to) in listOf(-1.3f to 2, -2.3f to 1, 1.3f to 4, 2.3f to 5)) {
            moves.clear()
            drag("row_3", rows, holdMs = 500)
            assertEquals("row 3 dragged $rows rows", listOf(Triple(0, 3, to)), moves)
        }
    }

    @Test fun a_tap_is_the_rows_click_and_a_quick_swipe_scrolls_and_neither_moves_a_row() {
        lazy(30)
        rule.onNodeWithTag("row_3").performClick()
        rule.onNodeWithTag("list").performTouchInput { swipeUp(durationMillis = 200) }
        rule.waitForIdle()
        assertEquals(listOf(3), clicks); assertTrue("moves $moves", moves.isEmpty())
    }

    @Test fun a_long_press_and_drag_reports_one_move_and_no_click() {
        lazy(30)
        drag("row_1", 2.2f)
        assertEquals(listOf(Triple(0, 1, 3)), moves); assertTrue("clicks $clicks", clicks.isEmpty())
    }

    @Test fun inside_an_outer_scroll_the_column_reorders_and_the_page_holds_still() {
        columnInAScroll(8)
        drag("row_2", 3.2f)
        assertEquals(listOf(Triple(0, 2, 5)), moves); assertEquals(0, pageScroll)
    }

    @Test fun a_locked_row_is_not_passed_and_is_not_picked_up() {
        lazy(8, locked = setOf(3))
        drag("row_0", 6f)
        assertEquals(listOf(Triple(0, 0, 2)), moves)
        moves.clear()
        drag("row_3", -2f)
        assertTrue("moves $moves", moves.isEmpty())
    }

    @Test fun released_outside_the_container_the_move_is_cancelled_and_below_the_last_row_it_lands_last() {
        columnInAScroll(4)                 // the container: 4 rows of 64 dp, a long box after it
        drag("row_0", 3.4f)                // past the last row's centre, the finger still on the rows: last
        assertEquals(listOf(Triple(0, 0, 3)), moves)
        moves.clear()
        drag("row_0", 7f)                  // the finger past the container's bottom: cancelled
        assertTrue("moves $moves", moves.isEmpty())
    }

    @Test fun with_a_handle_only_the_handle_picks_a_row_up() {
        lazy(30, usesHandle = true)
        rule.onNodeWithTag("row_1", true).performTouchInput {
            down(Offset(width * 0.2f, height / 2f)); advanceEventTime(viewConfiguration.longPressTimeoutMillis + 100)
            repeat(20) { moveBy(Offset(0f, 64.dp.toPx() * 2.2f / 20)); advanceEventTime(16) }
            up()
        }
        rule.waitForIdle()
        assertTrue("moves $moves", moves.isEmpty())
        drag("handle_1", 2.2f)
        assertEquals(listOf(Triple(0, 1, 3)), moves)
    }

    @Test fun held_at_the_bottom_edge_the_list_scrolls_and_the_row_lands_past_what_was_shown() {
        lazy(60)
        val shown = rule.onNodeWithTag("list").fetchSemanticsNode().size.height / (64 * rule.density.density)
        drag("row_1", shown - 1.8f, holdMs = 2_000)   // into the bottom band, still inside the container
        assertEquals(1, moves.size)
        assertTrue("landed at ${moves[0].third}, ${shown} rows shown", moves[0].third > shown.toInt())
    }

    private fun SemanticsNodeInteraction.action(label: String): Boolean {
        val actions = fetchSemanticsNode().config.getOrNull(SemanticsActions.CustomActions) ?: return false
        val a = actions.firstOrNull { it.label == label } ?: return false
        rule.runOnIdle { a.action() }
        return true
    }

    @Test fun the_accessibility_moves_report_one_step_and_stop_at_the_ends_and_at_a_locked_row() {
        lazy(6, locked = setOf(3))
        val up = CollectionReorderText.moveUp(null)
        val down = CollectionReorderText.moveDown(null)
        assertTrue(rule.onNodeWithTag("row_1").action(down))
        assertTrue(rule.onNodeWithTag("row_1").action(up))
        assertEquals(listOf(Triple(0, 1, 2), Triple(0, 1, 0)), moves)
        assertTrue(!rule.onNodeWithTag("row_0").action(up))       // the first row: no up
        assertTrue(!rule.onNodeWithTag("row_2").action(down))     // the row below is locked
        assertTrue(!rule.onNodeWithTag("row_3").action(up))       // locked: no move at all
    }
}
