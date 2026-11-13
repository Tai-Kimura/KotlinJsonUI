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
import com.kotlinjsonui.dynamic.components.DynamicSelectBoxComponent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A select's text by the text size rule when it declares fontScaling / maxFontScale (ticket
 * kjui-selectbox-text-ignores-font-scaling-declaration): the Dynamic SelectBox and date select handed the library
 * the size alone, so their text followed the system's text size whatever was declared (conformance
 * SelectBox/fontScaling__false drew the control's picture). At a font scale of 2.0 the text that does not follow is
 * as wide as at 1.0, the following one wider, the one capped at 1.3 between. Read as each text's first line, from its
 * TextLayoutResult (the Text fills the row's width, and its height carries the theme's line height, so neither of
 * its bounds says what size the glyphs are).
 */
@RunWith(AndroidJUnit4::class)
class DynamicSelectBoxFontScalingTest {
    @get:Rule val rule = createComposeRule()

    private val scale = mutableStateOf(1f)

    private fun node(kind: String, text: String, extra: String) = JsonParser.parseString(
        when (kind) {
            "list" -> """{"type":"SelectBox","items":["$text"],"selectedIndex":0$extra}"""
            else -> """{"type":"SelectBox","selectItemType":"Date","hint":"$text"$extra}"""
        }
    ).asJsonObject

    private fun content(kind: String, text: String) = rule.setContent {
        val d = LocalDensity.current
        MaterialTheme {
            CompositionLocalProvider(LocalDensity provides Density(d.density, fontScale = scale.value)) {
                Column {
                    DynamicSelectBoxComponent.create(node(kind, text, ""))
                    DynamicSelectBoxComponent.create(node(kind, text, ""","fontScaling":false"""))
                    DynamicSelectBoxComponent.create(node(kind, text, ""","maxFontScale":1.3"""))
                }
            }
        }
    }

    private fun widths(text: String, at: Float): List<Float> {
        rule.runOnIdle { scale.value = at }
        rule.waitForIdle()
        val nodes = rule.onAllNodesWithText(text, useUnmergedTree = true).fetchSemanticsNodes()
        assertEquals("three texts", 3, nodes.size)
        return nodes.map { node ->
            val layouts = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
            node.config[androidx.compose.ui.semantics.SemanticsActions.GetTextLayoutResult].action?.invoke(layouts)
            val layout = layouts.single()
            layout.getLineRight(0) - layout.getLineLeft(0)
        }
    }

    private fun check(kind: String, text: String) {
        content(kind, text)
        val fixedAt1 = widths(text, 1f)[1]
        val (follows, fixed, capped) = widths(text, 2f)
        assertEquals("$kind fontScaling false: as wide as at 1.0", fixedAt1, fixed, 1f)
        assertTrue("$kind following is wider: $follows vs $fixed", follows > fixed * 1.3f)
        assertTrue("$kind capped at 1.3 lies between: $fixed < $capped < $follows", capped > fixed * 1.1f && capped < follows)
    }

    @Test fun a_selects_text_follows_its_declarations() = check("list", "One")

    @Test fun a_date_selects_text_follows_its_declarations() = check("date", "Pick a day")
}
