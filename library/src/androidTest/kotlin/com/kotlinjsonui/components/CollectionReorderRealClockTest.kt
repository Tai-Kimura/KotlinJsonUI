package com.kotlinjsonui.components

import android.content.Intent
import android.graphics.Rect
import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A finger on the real clock (ticket jui-collection-drag-to-reorder): touches injected in real time through
 * UiAutomation, as jsonui-test-runner's moveItem drives a row — press 1.0 s, move at 250 dp a second, hold 0.5 s,
 * release over the to row at 3/4 of its height going down and 1/4 going up. A compose test rule drives its own clock,
 * under which a real long press never fires, so the rows are ReorderRealClockActivity's.
 *
 * A row dropped and picked up again at once landed one place further in 5 of 12 (measured with the driver's moveItem
 * on KotlinJsonUI f44c1de): the gesture's amounts are mapped through the dragged row's own moving layer and ran on
 * after the finger stopped. The finger is now read on the screen (CollectionReorder's RawFinger).
 */
@RunWith(AndroidJUnit4::class)
class CollectionReorderRealClockTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val automation = instrumentation.uiAutomation
    private val density = instrumentation.targetContext.resources.displayMetrics.density

    private fun bounds(tag: String): Rect? {
        val root = automation.rootInActiveWindow ?: return null
        val queue = ArrayDeque<AccessibilityNodeInfo>().apply { add(root) }
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            if (node.viewIdResourceName == tag) return Rect().also { node.getBoundsInScreen(it) }
            for (i in 0 until node.childCount) node.getChild(i)?.let { queue.add(it) }
        }
        return null
    }

    /** The rows' bounds once two reads 200 ms apart agree (the list has settled after its launch or a move). */
    private fun settled(tag: String): Rect {
        var last: Rect? = null
        val until = SystemClock.uptimeMillis() + 10_000
        while (SystemClock.uptimeMillis() < until) {
            val now = bounds(tag)
            if (now != null && now == last) return now
            last = now
            SystemClock.sleep(200)
        }
        throw AssertionError("'$tag' did not settle: $last")
    }

    private fun launch() {
        ReorderRealClockActivity.reset()
        val ctx = instrumentation.targetContext
        ctx.startActivity(
            Intent(ctx, ReorderRealClockActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        val until = SystemClock.uptimeMillis() + 10_000
        while (!ReorderRealClockActivity.resumed && SystemClock.uptimeMillis() < until) SystemClock.sleep(50)
        assertNotNull("the rows did not come up", settled("stops_item_3"))
    }

    private fun move(from: Int, to: Int) {
        val fromRow = settled("stops_item_$from")
        val toRow = settled("stops_item_$to")
        val x = fromRow.exactCenterX()
        val y0 = fromRow.exactCenterY()
        val y1 = toRow.top + toRow.height() * (if (to > from) 0.75f else 0.25f)
        val down = SystemClock.uptimeMillis()
        fun send(action: Int, y: Float) {
            val e = MotionEvent.obtain(down, SystemClock.uptimeMillis(), action, x, y, 0)
            e.source = InputDevice.SOURCE_TOUCHSCREEN
            try { automation.injectInputEvent(e, true) } finally { e.recycle() }
        }
        fun hold(y: Float, ms: Long) {
            val until = SystemClock.uptimeMillis() + ms
            while (SystemClock.uptimeMillis() < until) { SystemClock.sleep(16); send(MotionEvent.ACTION_MOVE, y) }
        }
        send(MotionEvent.ACTION_DOWN, y0)
        hold(y0, 1000)
        val frames = maxOf(1, Math.ceil(Math.abs(y1 - y0) / (250.0 * density * 0.016)).toInt())
        for (k in 1..frames) { SystemClock.sleep(16); send(MotionEvent.ACTION_MOVE, y0 + (y1 - y0) * k / frames) }
        hold(y1, 500)
        send(MotionEvent.ACTION_UP, y1)
        instrumentation.waitForIdleSync()
    }

    @Test fun a_row_moved_and_moved_back_at_once_lands_where_the_finger_lets_go_every_time() {
        val runs = (0 until 12).map { _ ->
            launch()
            move(2, 3)
            move(3, 2)
            ReorderRealClockActivity.moves.toList()
        }
        val expected = listOf(Triple(0, 2, 3), Triple(0, 3, 2))
        assertEquals("runs that did not land where the finger let go: $runs", 12, runs.count { it == expected })
    }

    @Test fun up_and_down_a_fresh_row_lands_where_the_finger_lets_go() {
        launch()
        move(5, 2)
        move(1, 4)
        assertEquals(listOf(Triple(0, 5, 2), Triple(0, 1, 4)), ReorderRealClockActivity.moves.toList())
    }
}
