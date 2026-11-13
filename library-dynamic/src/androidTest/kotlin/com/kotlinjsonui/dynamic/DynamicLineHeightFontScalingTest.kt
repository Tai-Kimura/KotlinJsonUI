package com.kotlinjsonui.dynamic

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A text's line box by the text size rule (ticket kjui-theme-line-height-scales-under-font-scaling-false): the
 * theme's line height is sp, so a text that does not follow kept its glyphs at 1.0 but grew its line box with the
 * system (a SelectBox's text 63px at 1.0, 126px at 2.0). At a font scale of 2.0 the line box of a text declared
 * fontScaling false is as tall as at 1.0, and one capped at 1.3 at most 1.3 times; each component that draws text
 * and declares the two, through DynamicView. Read as the text node's height.
 */
@RunWith(AndroidJUnit4::class)
class DynamicLineHeightFontScalingTest {
    @get:Rule val rule = createComposeRule()

    private val scale = mutableStateOf(1f)

    private val nodes = listOf(
        """{"type":"Label","text":"%s"%s}""",
        // A declared size: its line height is the size x 1.3 (LabelLineHeight), by the same rule.
        """{"type":"Label","text":"%s","fontSize":17%s}""",
        """{"type":"Label","text":"%s","fontSize":17,"lineHeightMultiple":1.6%s}""",
        """{"type":"Button","text":"%s","onClick":"@{onTap}"%s}""",
        """{"type":"CheckBox","text":"%s"%s}""",
        """{"type":"Switch","label":"%s"%s}""",
        """{"type":"IconLabel","text":"%s"%s}""",
        """{"type":"Radio","text":"%s","group":"g"%s}""",
        """{"type":"SelectBox","items":["%s"],"selectedIndex":0%s}"""
    )

    private fun content() = rule.setContent {
        val d = LocalDensity.current
        MaterialTheme {
            CompositionLocalProvider(LocalDensity provides Density(d.density, fontScale = scale.value)) {
                Column {
                    nodes.forEachIndexed { i, shape ->
                        val fixed = JsonParser.parseString(shape.format("F$i", ""","fontScaling":false""")).asJsonObject
                        val capped = JsonParser.parseString(shape.format("C$i", ""","maxFontScale":1.3""")).asJsonObject
                        DynamicView(json = fixed, data = mapOf("onTap" to {}))
                        DynamicView(json = capped, data = mapOf("onTap" to {}))
                    }
                }
            }
        }
    }

    private fun heights(at: Float): Map<String, Float> {
        rule.runOnIdle { scale.value = at }
        rule.waitForIdle()
        return nodes.indices.flatMap { listOf("F$it", "C$it") }.associateWith { text ->
            rule.onAllNodesWithText(text, useUnmergedTree = true).fetchSemanticsNodes().first().boundsInRoot.height
        }
    }

    @Test fun a_text_that_does_not_follow_keeps_its_line_box() {
        content()
        val at1 = heights(1f)
        val at2 = heights(2f)
        for (i in nodes.indices) {
            val type = JsonParser.parseString(nodes[i].format("x", "")).asJsonObject.get("type").asString
            assertEquals("$type fontScaling false: its line box as at 1.0", at1.getValue("F$i"), at2.getValue("F$i"), 1f)
            assertTrue("$type capped at 1.3: at most 1.3 x (${at2.getValue("C$i")} vs ${at1.getValue("C$i")})",
                at2.getValue("C$i") <= at1.getValue("C$i") * 1.3f + 1f)
        }
    }
}
