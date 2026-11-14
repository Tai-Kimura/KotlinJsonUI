package com.kotlinjsonui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kotlinjsonui.core.jsonUIFontSize
import com.kotlinjsonui.core.jsonUILetterSpacing
import com.kotlinjsonui.core.jsonUILineHeight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A text drawn by the text size rule (ticket sjui-label-font-size-ignores-dynamic-type): one that does not follow is
 * the same width at a font scale of 2.0 as at 1.0 — its size AND its letter spacing (Material's 0.5 sp, which the
 * system grows) held; one that follows is wider; one capped at 1.3 is between. Read as each text's width.
 */
@RunWith(AndroidJUnit4::class)
class FontScalingDrawnTest {
    @get:Rule val rule = createComposeRule()

    private val text = "Plan the order of the stops"

    private val scale = androidx.compose.runtime.mutableStateOf(1f)

    private fun content() = rule.setContent {
        val d = LocalDensity.current
        // MaterialTheme: its bodyLarge carries the 0.5 sp letterSpacing the system grows (a bare TextStyle has none).
        androidx.compose.material3.MaterialTheme {
        CompositionLocalProvider(LocalDensity provides Density(d.density, fontScale = scale.value)) {
            Column {
                Text(text, fontSize = jsonUIFontSize(17.sp), letterSpacing = jsonUILetterSpacing(), lineHeight = jsonUILineHeight(jsonUIFontSize(17.sp)),
                    modifier = Modifier.testTag("follows"))
                Text(text, fontSize = jsonUIFontSize(17.sp, fontScaling = false), letterSpacing = jsonUILetterSpacing(fontScaling = false),
                    lineHeight = jsonUILineHeight(jsonUIFontSize(17.sp, fontScaling = false), fontScaling = false),
                    modifier = Modifier.testTag("fixed"))
                Text(text, fontSize = jsonUIFontSize(17.sp, maxFontScale = 1.3f), letterSpacing = jsonUILetterSpacing(maxFontScale = 1.3f),
                    lineHeight = jsonUILineHeight(jsonUIFontSize(17.sp, maxFontScale = 1.3f), maxFontScale = 1.3f),
                    modifier = Modifier.testTag("capped"))
            }
        }
        }
    }

    private fun widths(at: Float): Triple<Float, Float, Float> {
        rule.runOnIdle { scale.value = at }
        rule.waitForIdle()
        fun width(tag: String) = rule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot.width
        return Triple(width("follows"), width("fixed"), width("capped"))
    }

    /** Each text's height (its line box): the theme's line height is sp, so it grows with the system unless the
     *  rule draws it too (ticket kjui-theme-line-height-scales-under-font-scaling-false). */
    private fun heights(at: Float): Triple<Float, Float, Float> {
        rule.runOnIdle { scale.value = at }
        rule.waitForIdle()
        fun height(tag: String) = rule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot.height
        return Triple(height("follows"), height("fixed"), height("capped"))
    }

    @Test fun at_2_0_the_fixed_text_is_as_tall_as_at_1_0_the_capped_at_most_its_cap() {
        content()
        val (followsAt1, fixedAt1, cappedAt1) = heights(1f)
        val (follows, fixed, capped) = heights(2f)
        assertEquals("fontScaling false: its line box as at 1.0", fixedAt1, fixed, 1f)
        assertTrue("capped at 1.3: its line box at most 1.3 x ($capped vs $cappedAt1)", capped <= cappedAt1 * 1.3f + 1f)
        assertTrue("following grows: $follows vs $followsAt1", follows > followsAt1 * 1.3f)
    }

    @Test fun at_2_0_the_fixed_text_is_as_wide_as_at_1_0_the_following_wider_the_capped_between() {
        content()
        val (_, fixedAt1, _) = widths(1f)
        val (follows, fixed, capped) = widths(2f)
        assertEquals("fontScaling false: the same width as at 1.0 (size and letter spacing held)", fixedAt1, fixed, 1f)
        assertTrue("following is wider: $follows vs $fixed", follows > fixed * 1.3f)
        assertTrue("capped at 1.3 lies between: $fixed < $capped < $follows", capped > fixed * 1.15f && capped < follows)
    }
}
