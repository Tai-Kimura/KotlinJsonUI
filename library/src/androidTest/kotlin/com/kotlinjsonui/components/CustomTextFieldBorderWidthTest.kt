package com.kotlinjsonui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The outline's width, written on a TextField. It reached nothing: an outline
 * was always Material's 1dp, 2dp under focus. A declared 1 draws what it drew,
 * focused or not, so the layouts that declare 1 do not change.
 */
@RunWith(AndroidJUnit4::class)
class CustomTextFieldBorderWidthTest {
    @get:Rule val rule = createComposeRule()

    private var width by mutableStateOf<Dp?>(null)

    private fun start() {
        rule.setContent {
            val state = rememberTextFieldState()
            Box(Modifier.testTag("root").width(240.dp).background(Color.White).padding(16.dp)) {
                CustomTextField(
                    state = state,
                    modifier = Modifier.testTag("field").width(200.dp),
                    shape = RoundedCornerShape(0.dp),
                    backgroundColor = Color.White,
                    borderColor = Color.Red,
                    isOutlined = true,
                    cursorColor = Color.Black,
                    borderWidth = width
                )
            }
        }
    }

    /** Red pixels down a column through the field's left edge. */
    private fun thicknessOnTheTop(): Int {
        val px = rule.onNodeWithTag("root").captureToImage().toPixelMap()
        val x = px.width / 2
        var n = 0
        for (y in 0 until px.height / 2) {
            val c = px[x, y]
            if (c.red > 0.8f && c.green < 0.4f && c.blue < 0.4f) n++
        }
        return n
    }

    private fun thickness(w: Dp?): Int {
        width = w
        rule.waitForIdle()
        return thicknessOnTheTop()
    }

    @Test
    fun aWidthIsDrawnAndADeclaredOneIsWhatMaterialDraws() {
        start()
        val none = thickness(null)
        assertTrue("the outline did not draw", none > 0)
        assertEquals("a declared 1 changed what is drawn", none, thickness(1.dp))
        assertTrue("a declared 4 is not thicker", thickness(4.dp) > none)
        assertEquals("a declared 0 draws a line", 0, thickness(0.dp))
    }

    @Test
    fun underFocusADeclaredOneKeepsTheFocusCueAndZeroStaysNoLine() {
        start()
        rule.onNodeWithTag("field").performClick()
        rule.waitForIdle()
        val focused = thickness(null)
        assertEquals("a declared 1 changed the focus cue", focused, thickness(1.dp))
        assertTrue("a declared 4 is not thicker under focus", thickness(4.dp) > focused)
        assertEquals("a declared 0 draws a line under focus", 0, thickness(0.dp))
    }
}
