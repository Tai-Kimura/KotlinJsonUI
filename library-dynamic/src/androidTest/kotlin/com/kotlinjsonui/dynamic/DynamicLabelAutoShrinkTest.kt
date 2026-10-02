package com.kotlinjsonui.dynamic

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.TextLayoutResult
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `minimumScaleFactor` / `autoShrink` shrink a Label's text, never grow it:
 * the drawn size is `fontSize` when the text fits and goes down to
 * `fontSize × factor` when it does not — iOS's `.minimumScaleFactor`
 * (jsonui-cli ticket kjui-minimum-scale-factor-grows-text-past-its-font-size).
 * TextAutoSize.StepBased was given a minFontSize only, and its maxFontSize
 * default (112.sp) let a short text grow to fill its box.
 *
 * The size read is the width of the first glyph as laid out, against a
 * reference Label of the same text at 12sp with no shrink.
 */
@RunWith(AndroidJUnit4::class)
class DynamicLabelAutoShrinkTest {
    @get:Rule val rule = createComposeRule()

    // The width of the first glyph as laid out — it scales with the size
    // the text was set at. (The style's fontSize reads the declared 12
    // either way, and the line height stayed 32px for a text set near 6sp —
    // both measured, so neither tells the size.)
    private fun glyphWidth(tag: String): Float {
        val results = mutableListOf<TextLayoutResult>()
        rule.onNodeWithTag(tag).fetchSemanticsNode().config[SemanticsActions.GetTextLayoutResult].action?.invoke(results)
        return results.first().getBoundingBox(0).width
    }

    // The target beside a reference Label: the same text at fontSize 12 with
    // no shrink, in the same width, so the glyph widths compare the sizes.
    private fun show(text: String, extra: String) {
        rule.setContent {
            DynamicView(
                json = JsonParser.parseString(
                    """{"type":"View","orientation":"vertical","child":[
                      {"type":"Label","id":"ref","width":200,"height":60,"text":"$text","fontSize":12,"lines":1},
                      {"type":"Label","id":"l","width":200,"height":60,"text":"$text","fontSize":12$extra}]}"""
                ).asJsonObject,
                data = emptyMap()
            )
        }
        rule.waitForIdle()
    }

    @Test
    fun aShortTextStaysAtItsFontSize() {
        show("Hi", ""","minimumScaleFactor":0.5""")
        assertEquals(glyphWidth("ref"), glyphWidth("l"), 0.5f)
    }

    @Test
    fun aShortTextStaysAtItsFontSizeWithAutoShrink() {
        show("Hi", ""","autoShrink":true""")
        assertEquals(glyphWidth("ref"), glyphWidth("l"), 0.5f)
    }

    @Test
    fun aLongTextShrinksDownToTheFloor() {
        // 12sp lays this out wider than the box (a shorter one fit in one
        // line at 12sp and rightly did not shrink — measured).
        show("A text far too long to fit in a box of two hundred dp at twelve sp in one line", ""","minimumScaleFactor":0.5""")
        val ref = glyphWidth("ref")
        val w = glyphWidth("l")
        assertTrue("first glyph $w px wide against $ref at 12sp", w < ref - 0.5f && w >= ref * 0.5f - 0.5f)
    }
}
