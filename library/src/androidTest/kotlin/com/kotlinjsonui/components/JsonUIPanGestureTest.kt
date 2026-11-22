package com.kotlinjsonui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * onPan inside a scroll: the pan owns the drag, the scroll around it does not move — also while the finger is held
 * still, whose moves Compose UI 1.13 dispatches (they went to the scroll and scrolled it by the distance the finger
 * had come: ticket kjui-slider-deprecated-in-material3-1-5).
 */
class JsonUIPanGestureTest {
    @get:Rule val rule = createComposeRule()

    private fun panIn(scroll: ScrollState, hold: Boolean): List<Offset> {
        val pans = mutableListOf<Offset>()
        rule.setContent {
            Column(Modifier.fillMaxSize().verticalScroll(scroll)) {
                Box(Modifier.fillMaxWidth().height(200.dp).testTag("pan").jsonUIPanGesture(Unit) { pans += it })
                Box(Modifier.fillMaxWidth().height(3000.dp))
            }
        }
        rule.onNodeWithTag("pan").performTouchInput {
            down(center)
            repeat(10) { moveBy(Offset(0f, -15f)); advanceEventTime(16) }
            if (hold) repeat(20) { moveBy(Offset(0f, 0f)); advanceEventTime(16) }
            up()
        }
        rule.waitForIdle()
        return pans
    }

    @Test fun a_pan_held_still_inside_a_scroll_does_not_scroll_it() {
        val scroll = ScrollState(0)
        val pans = panIn(scroll, hold = true)
        assertEquals(0, scroll.value)
        // The translation since the gesture began, past the touch slop: up, less than the 150 px the finger came.
        val last = pans.last()
        assertTrue("last pan $last", last.y < -100f && last.y > -150f)
    }

    @Test fun a_pan_let_go_at_once_inside_a_scroll_does_not_scroll_it() {
        val scroll = ScrollState(0)
        val pans = panIn(scroll, hold = false)
        assertEquals(0, scroll.value)
        assertTrue("pans $pans", pans.isNotEmpty())
    }
}
