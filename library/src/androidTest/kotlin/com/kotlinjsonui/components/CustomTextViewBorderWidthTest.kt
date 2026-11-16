package com.kotlinjsonui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A TextView is the field drawn over several lines, with the outline always on.
 * The width did not reach it, so layouts that declare 1 drew Material's outline:
 * 1dp, 2dp under focus. Declaring 1 must draw that, pixel for pixel, in the shapes
 * those layouts use (a low bar with a large corner, a tall box, margins). The caret
 * is transparent so that a focused frame does not depend on when it blinks.
 */
@RunWith(AndroidJUnit4::class)
class CustomTextViewBorderWidthTest {
    @get:Rule val rule = createComposeRule()

    private var width by mutableStateOf<Dp?>(null)
    private var corner by mutableStateOf(8.dp)
    private var tall by mutableStateOf(120.dp)
    private var margins by mutableStateOf(false)
    private var outline by mutableStateOf(Color(0xFFFF0000))

    private fun start() {
        rule.setContent {
            val state = rememberTextFieldState()
            Box(Modifier.testTag("root").width(320.dp).background(Color(0xFF101010)).padding(16.dp)) {
                if (margins) {
                    CustomTextFieldWithMargins(
                        state = state,
                        boxModifier = Modifier.padding(horizontal = 12.dp),
                        textFieldModifier = Modifier.testTag("field").width(260.dp).height(tall),
                        shape = RoundedCornerShape(corner),
                        backgroundColor = Color(0xFF202020),
                        borderColor = outline,
                        isOutlined = true,
                        maxLines = Int.MAX_VALUE,
                        singleLine = false,
                        cursorColor = Color.Transparent,
                        borderWidth = width
                    )
                } else {
                    CustomTextField(
                        state = state,
                        modifier = Modifier.testTag("field").width(260.dp).height(tall),
                        shape = RoundedCornerShape(corner),
                        backgroundColor = Color(0xFF202020),
                        borderColor = outline,
                        isOutlined = true,
                        maxLines = Int.MAX_VALUE,
                        singleLine = false,
                        cursorColor = Color.Transparent,
                        borderWidth = width
                    )
                }
            }
        }
    }

    private fun capture(): IntArray =
        rule.onNodeWithTag("root").captureToImage().toPixelMap().buffer.copyOf()

    /**
     * Two frames alike in a row. Focus brings the keyboard up, which moves the
     * window on its own clock, not the test's: a frame taken during that came back
     * black from the first pixel.
     */
    private fun shot(w: Dp?): IntArray {
        width = w
        rule.waitForIdle()
        var last = capture()
        repeat(40) {
            Thread.sleep(100)
            rule.waitForIdle()
            val next = capture()
            if (next.contentEquals(last)) return next
            last = next
        }
        throw AssertionError("the frame never settled")
    }

    private fun assertSameFrame(shape: String, expected: IntArray, actual: IntArray) {
        val differing = expected.indices.count { expected[it] != actual.getOrNull(it) }
        assertEquals("a declared 1 changed the frame in $differing of ${expected.size} pixels: $shape", 0, differing)
    }

    private fun theShapesLayoutsUse(check: (String) -> Unit) {
        for (m in listOf(false, true)) for ((c, h) in listOf(22.dp to 44.dp, 8.dp to 120.dp, 4.dp to 180.dp)) {
            for (o in listOf(Color(0xFFFF0000), Color(0x33D4AF37))) {
                margins = m; corner = c; tall = h; outline = o
                rule.waitForIdle()
                check("margins=$m corner=$c height=$h outline=${o.value.toString(16)}")
            }
        }
    }

    private fun aDeclaredOneIsTheSameFrame(shape: String, before: () -> Unit) {
        before()
        val none = shot(null)
        before()
        assertSameFrame(shape, none, shot(1.dp))
        before()
        assertFalse("a declared 3 did not change the frame: $shape", none.contentEquals(shot(3.dp)))
    }

    @Test
    fun unfocusedADeclaredOneDrawsWhatItDrew() {
        start()
        theShapesLayoutsUse { shape -> aDeclaredOneIsTheSameFrame(shape) {} }
    }

    /** The other composable under `margins` is a new field, so focus is taken again in each shape, and proved. */
    @Test
    fun focusedADeclaredOneDrawsWhatItDrew() {
        start()
        theShapesLayoutsUse { shape ->
            val focus = {
                rule.onNode(hasSetTextAction()).performClick()
                rule.waitForIdle()
                rule.onNode(hasSetTextAction()).assertIsFocused()
                Unit
            }
            aDeclaredOneIsTheSameFrame(shape, focus)
        }
    }

    /** Declared 1 and undeclared are the same under focus, and focus still shows: 2dp is not 1dp. */
    @Test
    fun theFocusCueIsThereWithADeclaredOne() {
        start()
        val resting = shot(1.dp)
        rule.onNode(hasSetTextAction()).performClick()
        rule.waitForIdle()
        assertFalse("focus did not change the outline", resting.contentEquals(shot(1.dp)))
    }
}
