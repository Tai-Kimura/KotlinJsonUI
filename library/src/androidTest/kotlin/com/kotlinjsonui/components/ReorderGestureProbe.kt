package com.kotlinjsonui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.testTag
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
 * MEASUREMENT ONLY (ticket jui-collection-drag-to-reorder, before the build): where a long-press drag on a row meets
 * the row's own onClick and the scroll around it. A minimal stand-in of the drag: long press, follow the finger,
 * swap when the dragged row's centre passes a neighbour's centre, report (from, to) once on release.
 */
@RunWith(AndroidJUnit4::class)
class ReorderGestureProbe {
    @get:Rule val rule = createComposeRule()

    private class Probe(n: Int) {
        val order = mutableStateListOf<Int>().apply { addAll(0 until n) }
        val clicks = mutableListOf<Int>()
        val moves = mutableListOf<Pair<Int, Int>>()
        val tops = HashMap<Int, Float>()
        val heights = HashMap<Int, Float>()
        var dragging by mutableStateOf<Int?>(null)
        var startPos = 0
        var dy by mutableFloatStateOf(0f)        // the finger's travel since the press
        private var base = 0f                    // the first row's top when the drag started
        private val h = HashMap<Int, Float>()    // each row's height when the drag started
        private var startTop = 0f

        // Where each slot is, from the order and the heights held at the start — not from the layout, which lags a
        // swap by a frame.
        fun slotTop(pos: Int): Float = base + (0 until pos).sumOf { (h[order[it]] ?: 0f).toDouble() }.toFloat()
        fun offsetOf(item: Int): Float = if (dragging == item) startTop + dy - slotTop(order.indexOf(item)) else 0f

        fun start(item: Int) {
            dragging = item; startPos = order.indexOf(item); dy = 0f
            h.clear(); order.forEach { h[it] = heights[it] ?: 0f }
            base = tops[order.first()] ?: 0f
            startTop = slotTop(startPos)
        }
        fun drag(delta: Float) {
            val item = dragging ?: return
            dy += delta
            while (true) {
                val pos = order.indexOf(item)
                val centre = startTop + dy + (h[item] ?: 0f) / 2
                val next = order.getOrNull(pos + 1)
                val prev = order.getOrNull(pos - 1)
                if (next != null && centre > slotTop(pos + 1) + (h[next] ?: 0f) / 2) {
                    order.removeAt(pos); order.add(pos + 1, item)
                } else if (prev != null && centre < slotTop(pos - 1) + (h[prev] ?: 0f) / 2) {
                    order.removeAt(pos); order.add(pos - 1, item)
                } else break
            }
        }
        fun end() {
            val item = dragging ?: return
            val to = order.indexOf(item)
            if (to != startPos) moves.add(startPos to to)
            dragging = null; dy = 0f
        }
    }

    @Composable
    private fun Row(p: Probe, item: Int, handleOnly: Boolean) {
        val gesture = Modifier.pointerInput(item) {
            detectDragGesturesAfterLongPress(
                onDragStart = { p.start(item) },
                onDrag = { change, amount -> change.consume(); p.drag(amount.y) },
                onDragEnd = { p.end() },
                onDragCancel = { p.end() },
            )
        }
        Row(
            Modifier.fillMaxWidth().height(64.dp).testTag("row_$item")
                .onGloballyPositioned { c -> p.tops[item] = c.positionInRoot().y; p.heights[item] = c.size.height.toFloat() }
                .graphicsLayer { translationY = p.offsetOf(item) }
                .background(if (item % 2 == 0) Color(0xFFEEEEEE) else Color.White)
                .clickable { p.clicks.add(item) }
                .then(if (handleOnly) Modifier else gesture),
        ) {
            Text("row $item", Modifier.weight(1f))
            Box(Modifier.size(48.dp).testTag("handle_$item").background(Color.Gray).then(if (handleOnly) gesture else Modifier))
        }
    }

    private fun lazyList(p: Probe, handleOnly: Boolean = false) = rule.setContent {
        val state = rememberLazyListState()
        LazyColumn(Modifier.fillMaxSize().testTag("list"), state = state) {
            itemsIndexed(p.order, key = { _, it -> it }) { _, item -> Row(p, item, handleOnly) }
        }
    }

    private fun columnInScroll(p: Probe, scrolled: (Int) -> Unit) = rule.setContent {
        val scroll = rememberScrollState()
        scrolled(scroll.value)
        Column(Modifier.fillMaxSize().verticalScroll(scroll).testTag("list")) {
            Box(Modifier.fillMaxWidth().height(120.dp))
            Column { p.order.forEach { item -> androidx.compose.runtime.key(item) { Row(p, item, false) } } }
            Box(Modifier.fillMaxWidth().height(1200.dp))
        }
    }

    private fun longPressDrag(tag: String, rows: Float) {
        rule.onNodeWithTag(tag, true).performTouchInput {
            down(center)
            advanceEventTime(viewConfiguration.longPressTimeoutMillis + 100)
            val step = 64.dp.toPx() * rows / 20
            repeat(20) { moveBy(Offset(0f, step)); advanceEventTime(16) }
            up()
        }
        rule.waitForIdle()
    }

    @Test fun a_tap_is_the_rows_click_and_no_move() {
        val p = Probe(30); lazyList(p)
        rule.onNodeWithTag("row_3").performClick(); rule.waitForIdle()
        assertEquals(listOf(3), p.clicks); assertTrue(p.moves.isEmpty())
    }

    @Test fun a_quick_swipe_scrolls_the_list_and_moves_nothing() {
        val p = Probe(30); lazyList(p)
        rule.onNodeWithTag("list").performTouchInput { swipeUp(durationMillis = 200) }; rule.waitForIdle()
        assertTrue("moves ${p.moves}", p.moves.isEmpty()); assertTrue("clicks ${p.clicks}", p.clicks.isEmpty())
        assertTrue("row_0 still at the top after a swipe", (p.tops[0] ?: 0f) < 0f || p.order.isNotEmpty())
    }

    @Test fun a_long_press_and_drag_moves_the_row_and_is_no_click() {
        val p = Probe(30); lazyList(p)
        longPressDrag("row_1", 2.2f)
        assertEquals(listOf(1 to 3), p.moves); assertTrue("clicks ${p.clicks}", p.clicks.isEmpty())
    }

    @Test fun inside_an_outer_vertical_scroll_the_drag_moves_and_the_page_holds_still() {
        val p = Probe(8); var scrolled = 0
        columnInScroll(p) { scrolled = it }
        longPressDrag("row_2", 3.2f)
        assertEquals(listOf(2 to 5), p.moves); assertEquals(0, scrolled); assertTrue(p.clicks.isEmpty())
    }

    @Test fun with_a_handle_a_long_press_elsewhere_on_the_row_moves_nothing() {
        val p = Probe(30); lazyList(p, handleOnly = true)
        rule.onNodeWithTag("row_1").performTouchInput {
            down(Offset(width * 0.25f, height / 2f))
            advanceEventTime(viewConfiguration.longPressTimeoutMillis + 100)
            repeat(20) { moveBy(Offset(0f, 64.dp.toPx() * 2.2f / 20)); advanceEventTime(16) }
            up()
        }
        rule.waitForIdle()
        assertTrue("moves ${p.moves}", p.moves.isEmpty())
    }

    @Test fun with_a_handle_the_handle_starts_a_drag() {
        val p = Probe(30); lazyList(p, handleOnly = true)
        longPressDrag("handle_1", 2.2f)
        assertEquals(listOf(1 to 3), p.moves); assertTrue("clicks ${p.clicks}", p.clicks.isEmpty())
    }
}
