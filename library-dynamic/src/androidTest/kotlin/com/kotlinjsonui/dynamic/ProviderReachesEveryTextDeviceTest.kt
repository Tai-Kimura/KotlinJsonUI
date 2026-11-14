package com.kotlinjsonui.dynamic

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.kotlinjsonui.core.jsonUIFontSize
import com.kotlinjsonui.core.jsonUILetterSpacing
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Every Dynamic text goes through the app's fontProvider, a font attribute or not (the requester's P1, ticket
 * jui-font-provider-not-reached-without-font-attributes-android-web): with a provider that answers 40sp italic, the
 * text of each of the ten components that write no font attribute is drawn at 40sp in italic. Before, only a
 * component naming fontFamily reached the res/font lookup and none called the provider (measured: every one drew its
 * own size). Without a provider each draws as it did (the control).
 */
@RunWith(AndroidJUnit4::class)
class ProviderReachesEveryTextDeviceTest {
    @get:Rule
    val rule = createComposeRule()

    private val asked = mutableListOf<FontSpec>()

    @After
    fun clear() {
        Configuration.Font.fontProvider = null
    }

    /** Each component with no font attribute, and the text it draws. */
    private val cases = listOf(
        "Label" to """{"type":"Label","text":"LabelText"}""",
        "Button" to """{"type":"Button","text":"ButtonText"}""",
        "TextField" to """{"type":"TextField","text":"FieldText","width":300}""",
        "TextView" to """{"type":"TextView","text":"ViewText","width":300,"height":120}""",
        "SelectBox" to """{"type":"SelectBox","items":["BoxText","Other"],"selectedIndex":0,"width":300}""",
        "Switch" to """{"type":"Switch","label":"SwitchText"}""",
        "CheckBox" to """{"type":"CheckBox","label":"CheckText"}""",
        "Radio" to """{"type":"Radio","text":"RadioText","group":"g"}""",
        "IconLabel" to """{"type":"IconLabel","text":"IconText"}""",
        "Segment" to """{"type":"Segment","items":["SegText","Two"],"selectedIndex":0}""",
    )

    private fun layout(node: SemanticsNodeInteraction): TextLayoutResult? {
        val results = mutableListOf<TextLayoutResult>()
        val get = node.fetchSemanticsNode().config.getOrNull(SemanticsActions.GetTextLayoutResult) ?: return null
        get.action?.invoke(results)
        return results.firstOrNull()
    }

    /** The drawn text's size (sp) and style for each case. */
    private var current by mutableStateOf("""{"type":"View"}""")

    private fun drawn(): Map<String, Pair<Float, FontStyle?>> {
        rule.setContent { Box { key(current) { DynamicView(json = JsonParser.parseString(current).asJsonObject) } } }
        return cases.associate { (name, json) -> name to drawnOne(name, json) }
    }

    private fun drawnOne(name: String, json: String): Pair<Float, FontStyle?> {
        val text = Regex("\"(\\w+Text)\"").find(json)!!.groupValues[1]
        current = json
        rule.waitForIdle()
        val nodes = rule.onAllNodesWithText(text, substring = true, useUnmergedTree = true)
        val l = (0 until nodes.fetchSemanticsNodes().size).firstNotNullOfOrNull { layout(nodes[it]) }
        assertTrue("$name drew no text layout for $text", l != null)
        return l!!.layoutInput.style.fontSize.value to l.layoutInput.style.fontStyle
    }

    @Test
    fun a_provider_reaches_every_text_that_names_no_font() {
        Configuration.Font.fontProvider = { spec ->
            asked.add(spec)
            ResolvedFont(size = 40.sp, style = FontStyle.Italic)
        }
        val got = drawn()
        for ((name, pair) in got) {
            assertEquals("$name size", 40f, pair.first, 0.01f)
            assertEquals("$name style", FontStyle.Italic, pair.second)
        }
        assertTrue("the provider was asked", asked.isNotEmpty())
    }

    /**
     * A provider that answers a font file by the weight it is asked with (as client's and bar's do: no weight is
     * Regular) keeps the weight a text draws when it declares none: kjui's no-font Button (labelLarge, Medium) and a
     * Segment's tab (Medium) are asked with Medium and draw the Medium file. Asked with no weight they drew the
     * Regular one — a glyph change on every Button that names no font (found reading client's provider, 2026-11-14).
     * The Button below is kjui's emit for `{"type":"Button","text":"GenText"}`, as generated.
     */
    @Test
    fun a_provider_asked_by_weight_keeps_the_medium_of_a_button_and_a_tab_that_name_no_weight() {
        val medium = FontFamily.Serif
        val other = FontFamily.Monospace
        Configuration.Font.fontProvider = { spec ->
            asked.add(spec)
            ResolvedFont(family = if (spec.weight == FontWeight.Medium) medium else other, weight = spec.weight, size = spec.size)
        }
        var which by mutableStateOf(0)
        rule.setContent {
            Box {
                key(which) {
                    if (which == 0) GeneratedPlainButton()
                    else DynamicView(json = JsonParser.parseString("""{"type":"Segment","items":["SegText","Two"],"selectedIndex":0}""").asJsonObject)
                }
            }
        }
        rule.waitForIdle()
        fun familyOf(text: String): FontFamily? {
            val nodes = rule.onAllNodesWithText(text, substring = true, useUnmergedTree = true)
            return (0 until nodes.fetchSemanticsNodes().size).firstNotNullOfOrNull { layout(nodes[it]) }?.layoutInput?.style?.fontFamily
        }
        assertEquals("the generated no-font Button draws the provider's Medium file", medium, familyOf("GenText"))
        which = 1
        rule.waitForIdle()
        assertEquals("a Segment tab draws the provider's Medium file", medium, familyOf("SegText"))
    }

    /** kjui's emit for a Button that names no font attribute (jsonui-cli feat/font-provider-android). */
    @Composable
    private fun GeneratedPlainButton() {
        Button(
            onClick = { },
            shape = RoundedCornerShape(Configuration.Button.defaultCornerRadius.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Configuration.Button.defaultBackgroundColor,
                contentColor = Configuration.Button.defaultTextColor,
            )
        ) {
            val resolved_button1 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = LocalTextStyle.current.fontWeight,
                size = null,
                italic = false
            ))
            Text(
                text = "GenText",
                fontFamily = resolved_button1.family,
                fontWeight = resolved_button1.weight,
                fontSize = jsonUIFontSize(resolved_button1.size ?: TextUnit.Unspecified),
                letterSpacing = jsonUILetterSpacing(),
                fontStyle = resolved_button1.style ?: FontStyle.Normal,
            )
        }
    }

    /**
     * The line follows the size the text is drawn at, the provider's when it gives one (ruling 2026-11-14): an IconLabel
     * whose provider answers 40sp draws a line of at least 40sp, as its text does. Its line was taken from the declared
     * size (16) and drew 40sp text on a line for 16.
     */
    @Test
    fun an_icon_labels_line_follows_the_providers_size() {
        Configuration.Font.fontProvider = { spec -> ResolvedFont(size = 40.sp) }
        rule.setContent { Box { DynamicView(json = JsonParser.parseString("""{"type":"IconLabel","text":"IconText","fontSize":16}""").asJsonObject) } }
        rule.waitForIdle()
        val nodes = rule.onAllNodesWithText("IconText", substring = true, useUnmergedTree = true)
        val l = (0 until nodes.fetchSemanticsNodes().size).firstNotNullOfOrNull { layout(nodes[it]) }
        assertTrue("IconLabel drew no text layout", l != null)
        val style = l!!.layoutInput.style
        assertEquals("size", 40f, style.fontSize.value, 0.01f)
        assertTrue("line ${style.lineHeight} under the 40sp text", style.lineHeight.value >= 40f)
    }

    @Test
    fun without_a_provider_each_draws_its_own_size_control() {
        Configuration.Font.fontProvider = null
        for ((name, pair) in drawn()) {
            assertNotEquals("$name control", 40f, pair.first, 0.01f)
            assertNotEquals("$name control style", FontStyle.Italic, pair.second)
        }
    }
}
