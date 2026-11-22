package com.kotlinjsonui.components

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * The Slider the kjui codegen emits (ticket kjui-slider-deprecated-in-material3-1-5): it draws, follows a drag through
 * onValueChange within its range, and reports the end of the drag through onValueChangeFinished — on whichever
 * material3 the app brings. KotlinJsonUI compiles JsonUISlider against its own compose-bom; run with an app's newer
 * one (the androidTest bom raised to 2026.11.01, material3 1.5) this is the check that material3 still carries the
 * value form it calls.
 */
class JsonUISliderTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun aDragMovesTheValueWithinItsRangeAndFinishes() {
        var value by mutableFloatStateOf(2f)
        var finished = 0
        rule.setContent {
            var shown by remember { mutableFloatStateOf(2f) }
            JsonUISlider(
                value = shown,
                onValueChange = { shown = it; value = it },
                modifier = Modifier.width(300.dp).testTag("slider"),
                valueRange = 0f..10f,
                onValueChangeFinished = { finished++ }
            )
        }
        rule.onNodeWithTag("slider").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo(2f, 0f..10f, 0))
        )
        rule.onNodeWithTag("slider").performTouchInput { swipeRight() }
        rule.waitForIdle()
        assertTrue("the value followed the drag: $value", value > 2f && value <= 10f)
        assertEquals("one end of the drag", 1, finished)
    }

    @Test
    fun stepsSnapTheValue() {
        var value by mutableFloatStateOf(0f)
        rule.setContent {
            var shown by remember { mutableFloatStateOf(0f) }
            JsonUISlider(
                value = shown,
                onValueChange = { shown = it; value = it },
                modifier = Modifier.width(300.dp).testTag("slider"),
                valueRange = 0f..10f,
                steps = 9
            )
        }
        rule.onNodeWithTag("slider").performTouchInput { swipeRight() }
        rule.waitForIdle()
        assertEquals("a whole step", value, kotlin.math.round(value), 0.0001f)
    }
}
