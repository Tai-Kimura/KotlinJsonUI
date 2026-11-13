package com.kotlinjsonui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * After a move the rows outside the moved range stay where they were (ticket jui-collection-drag-to-reorder). Asked
 * after jsonui-test-runner's moveItem said "the list scrolled during the drag" on a probe built with Compose 1.6.8,
 * whose lazy list keeps its first visible item in place BY KEY: row 0 dragged to 2 moved row 3 by 144 dp there. The
 * library's own Compose holds a lazy list's place when its first visible item is moved — [control] pins that, with
 * no reorder involved — so the library draws no jump and adds nothing for it. The rows here are a list the "view model" really reorders on onReorder, in the three containers a
 * Collection is drawn in: a lazy list, an EAGER column that scrolls itself, and a NONE column an outer scroll carries.
 * Each moves row 0 down and the last row up, by drag and by the accessibility move, and reads a row outside the range
 * before and after.
 */
@RunWith(AndroidJUnit4::class)
class CollectionReorderScrollAnchorTest {
    @get:Rule val rule = createComposeRule()

    private val ids = mutableStateListOf<String>()
    private val reorder = CollectionReorder(isEnabled = true) { _, from, to -> ids.add(to, ids.removeAt(from)) }

    @Composable
    private fun Cell(index: Int) {
        Box(
            Modifier.collectionReorderActions().fillMaxWidth().height(64.dp).testTag("row_$index")
                .background(if (index % 2 == 0) Color(0xFFEEEEEE) else Color.White),
        ) { Text(ids[index]) }
    }

    private fun setRows(n: Int) { ids.clear(); ids.addAll((0 until n).map { "id$it" }) }

    /** [scrolledDp]: the list scrolled that far, so row 0 is its first visible item but not at its top. */
    private fun lazy(n: Int, scrolledDp: Int = 0) {
        setRows(n)
        rule.setContent {
            CollectionReorderContainer(reorder, Modifier.fillMaxSize()) {
                val state = rememberCollectionReorderState()
                val offset = with(androidx.compose.ui.platform.LocalDensity.current) { scrolledDp.dp.roundToPx() }
                LazyColumn(Modifier.fillMaxSize(), state = rememberLazyListState(0, offset)) {
                    collectionReorderRows(state, 0, ids.toList()) { i -> Cell(i) }
                }
            }
        }
    }

    private fun eager(n: Int) {
        setRows(n)
        rule.setContent {
            CollectionReorderContainer(reorder, Modifier.fillMaxWidth().height(600.dp), scrollsItself = true) {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    CollectionReorderRows(0, ids.toList()) { i -> Cell(i) }
                }
            }
        }
    }

    private fun outerScroll(n: Int) {
        setRows(n)
        rule.setContent {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                Box(Modifier.fillMaxWidth().height(80.dp))
                CollectionReorderContainer(reorder, Modifier, scrollsItself = false) {
                    Column { CollectionReorderRows(0, ids.toList()) { i -> Cell(i) } }
                }
                Box(Modifier.fillMaxWidth().height(1200.dp))
            }
        }
    }

    private fun top(index: Int) = rule.onNodeWithTag("row_$index").fetchSemanticsNode().boundsInRoot.top

    private fun drag(index: Int, rows: Float) {
        rule.onNodeWithTag("row_$index").performTouchInput {
            down(center)
            advanceEventTime(viewConfiguration.longPressTimeoutMillis + 100)
            val step = 64.dp.toPx() * rows / 20
            repeat(20) { moveBy(Offset(0f, step)); advanceEventTime(16) }
            repeat(10) { moveBy(Offset(0f, 0f)); advanceEventTime(16) }
            up()
        }
        rule.waitForIdle()
    }

    private fun moveDown(index: Int) {
        val node = rule.onNodeWithTag("row_$index").fetchSemanticsNode()
        val action = node.config.getOrNull(SemanticsActions.CustomActions).orEmpty()
            .first { it.label == CollectionReorderText.moveDown }
        rule.runOnUiThread { action.action() }
        rule.waitForIdle()
    }

    /** Moves [from] to [to] by [how] and checks the row [outside] (outside the range) did not move. */
    private fun holds(outside: Int, from: Int, to: Int, how: () -> Unit) {
        val before = top(outside)
        how()
        val order = (0 until ids.size).map { "id$it" }.toMutableList().also { it.add(to, it.removeAt(from)) }
        assertEquals("the move $from -> $to", order, ids.toList())
        assertEquals("row $outside, outside $from..$to, stays where it was (px)", before, top(outside), 1f)
    }

    @Test fun a_lazy_list_holds_still_when_row_0_is_dragged_down() { lazy(12); holds(3, 0, 2) { drag(0, 2.2f) } }
    // Row 0 the first visible item, 20 dp of it scrolled away: the lazy list keeps that item in place by key.
    @Test fun a_lazy_list_scrolled_a_little_holds_still_when_row_0_is_dragged_down() { lazy(12, 20); holds(3, 0, 2) { drag(0, 2.2f) } }
    @Test fun a_lazy_list_scrolled_a_little_holds_still_when_row_0_is_moved_down_by_the_accessibility_move() {
        lazy(12, 20); holds(2, 0, 1) { moveDown(0) }
    }
    @Test fun a_lazy_list_holds_still_when_its_last_row_is_dragged_up() { lazy(8); holds(4, 7, 5) { drag(7, -2.2f) } }
    @Test fun a_lazy_list_holds_still_when_row_0_is_moved_down_by_the_accessibility_move() { lazy(12); holds(2, 0, 1) { moveDown(0) } }

    /**
     * The Compose behaviour the library relies on: in a lazy list scrolled 20 dp (row 0 its first visible item), row 0
     * moved to 2 by the data alone leaves row 3 where it was. Red here means Compose follows the moved key again, and
     * a reorder of the first visible row scrolls the list.
     */
    @Test fun control() {
        lazy(12, 20)
        val before = top(3)
        rule.runOnUiThread { ids.add(2, ids.removeAt(0)) }
        rule.waitForIdle()
        assertEquals("row 3 after row 0 moved to 2 by the data alone (px)", before, top(3), 1f)
    }

    // A row dropped and picked up again at once (its index just changed) lands where the finger lets go.
    @Test fun a_row_moved_back_right_after_its_move_lands_where_it_was() {
        lazy(12)
        drag(2, 1.3f)
        drag(3, -1.3f)
        assertEquals((0 until 12).map { "id$it" }, ids.toList())
    }

    @Test fun an_eager_column_holds_still_when_row_0_is_dragged_down() { eager(12); holds(3, 0, 2) { drag(0, 2.2f) } }
    @Test fun an_eager_column_holds_still_when_its_last_row_is_dragged_up() { eager(8); holds(4, 7, 5) { drag(7, -2.2f) } }

    @Test fun a_column_in_an_outer_scroll_holds_still_when_row_0_is_dragged_down() { outerScroll(12); holds(3, 0, 2) { drag(0, 2.2f) } }
    @Test fun a_column_in_an_outer_scroll_holds_still_when_its_last_row_is_dragged_up() { outerScroll(8); holds(4, 7, 5) { drag(7, -2.2f) } }
}
