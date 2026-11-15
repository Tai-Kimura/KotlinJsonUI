package com.kotlinjsonui.dynamic

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import com.kotlinjsonui.components.Segment
import com.kotlinjsonui.components.SegmentTab
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.jsonUIFontSize
import com.kotlinjsonui.core.jsonUILetterSpacing
import com.kotlinjsonui.core.jsonUILineHeight
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A Segment's labels and corners as iOS draws them (jsonui-cli ticket
 * kjui-segment-label-font-larger-than-ios; the rulings of 2026-11-16).
 *
 * Labels: UISegmentedControl's own font, measured on iOS 27.2 — 13 pt, Medium on the selected
 * segment and Regular on the others, 13 at every Dynamic Type size. Here, for the static call kjui
 * generates and for DynamicView, at font scale 1 and 2: each label drawn 13 dp, Medium selected
 * and Normal otherwise, its line the font's own at every scale, and not cut ("アプリ風" and "本棚風" in a Segment 156 wide and 32 high,
 * which iOS fits; Material's 16 sp drew "ア…" at 2).
 *
 * Corners: a Segment does not read cornerRadius — the track and the selected fill are capsules
 * whatever the layout declares (0, 4, 100 or none), read where a capsule and a 4 dp corner differ.
 */
@RunWith(AndroidJUnit4::class)
class SegmentLabelAndCornerTest {
    @get:Rule val rule = createComposeRule()
    private var content by mutableStateOf<(@Composable () -> Unit)?>(null)
    private var composed = false
    private val labels = listOf("アプリ風", "本棚風")

    private fun show(draw: @Composable () -> Unit) {
        if (!composed) {
            // Under MaterialTheme, as an app draws it (its text style is bodyLarge, 16 sp).
            rule.setContent { MaterialTheme { content?.let { c -> key(c) { Box(Modifier.background(Color.White).padding(8.dp)) { c() } } } } }
            composed = true
        }
        rule.runOnIdle { content = null }
        rule.waitForIdle()
        rule.runOnIdle { content = draw }
        rule.waitForIdle()
        Thread.sleep(300)
        rule.waitForIdle()
    }

    @Composable
    private fun generated() {
        // What kjui generates for {"type": "Segment", "id": "seg", "width": 156, "height": 32,
        // "items": ["アプリ風", "本棚風"], "selectedIndex": 0} (jsonui-cli 2.5.44).
        Segment(selectedTabIndex = 0, modifier = Modifier.width(156.dp).height(32.dp).testTag("seg")) {
            labels.forEachIndexed { i, label ->
                SegmentTab(selected = i == 0, onClick = {}, text = {
                    val segmentFont = Configuration.Font.resolve(FontSpec(family = null, weight = LocalTextStyle.current.fontWeight, size = null, italic = false))
                    Text(label, fontFamily = segmentFont.family, fontWeight = segmentFont.weight, fontSize = jsonUIFontSize(segmentFont.size ?: TextUnit.Unspecified), letterSpacing = jsonUILetterSpacing(), lineHeight = jsonUILineHeight(jsonUIFontSize(segmentFont.size ?: TextUnit.Unspecified)), fontStyle = segmentFont.style ?: FontStyle.Normal, maxLines = 1, overflow = TextOverflow.Ellipsis)
                })
            }
        }
    }

    private fun dynamic(extra: String = ""): @Composable () -> Unit = {
        DynamicView(
            json = JsonParser.parseString(
                """{"type": "Segment", "id": "seg", "width": 156, "height": 32, "items": ["アプリ風", "本棚風"], "selectedIndex": 0$extra}"""
            ).asJsonObject,
            data = emptyMap(),
        )
    }

    private fun scaled(scale: Float, draw: @Composable () -> Unit): @Composable () -> Unit = {
        val d = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(d.density, scale)) { draw() }
    }

    private val lines = mutableListOf<Float>()

    private fun label(text: String): String {
        val results = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText(text, useUnmergedTree = true).fetchSemanticsNode()
            .config[SemanticsActions.GetTextLayoutResult].action?.invoke(results)
        val r = results.single()
        val fontSize = r.layoutInput.style.fontSize
        val size = if (!fontSize.isSp) "unspecified" else with(Density(rule.density.density, r.layoutInput.density.fontScale)) {
            "%.1f dp".format(fontSize.toDp().value)
        }
        val weight = when (r.layoutInput.style.fontWeight ?: FontWeight.Normal) {
            FontWeight.Medium -> "Medium"; FontWeight.Normal -> "Normal"; else -> r.layoutInput.style.fontWeight.toString()
        }
        // The line: the font's own, the same at every font scale (Material's 24 sp line doubled
        // at 2 and pushed the label out of the 27 dp segment).
        val line = (r.getLineBottom(0) - r.getLineTop(0)) / rule.density.density
        lines += line
        return "$text $size $weight cut=${r.isLineEllipsized(0)}"
    }

    @Test
    fun labelsAre13dpMediumSelectedNormalOtherwise_atEveryFontScale_andNotCut() {
        val rows = mutableListOf<String>()
        for (scale in listOf(1f, 2f)) {
            for ((name, draw) in listOf<Pair<String, @Composable () -> Unit>>("static" to { generated() }, "dynamic" to dynamic())) {
                show(scaled(scale, draw))
                rows += "$name x$scale :: " + labels.joinToString(", ") { label(it) }
            }
        }
        val table = rows.joinToString("\n")
        Log.i("KjuiSegmentLabel", "\n$table")
        val expected = listOf(1f, 2f).flatMap { s ->
            listOf("static", "dynamic").map { "$it x$s :: アプリ風 13.0 dp Medium cut=false, 本棚風 13.0 dp Normal cut=false" }
        }.joinToString("\n")
        assertEquals(expected, table)
        Log.i("KjuiSegmentLabel", "lines dp $lines")
        assertEquals("one line height at every font scale: $lines", 1, lines.map { kotlin.math.round(it) }.distinct().size)
        assertEquals("a line inside the 27 dp segment: $lines", true, lines.all { it < 27f })
    }

    @Test
    fun theTrackAndTheFillAreCapsulesWhateverCornerRadiusSays() {
        val rows = mutableListOf<String>()
        for (corner in listOf("none", "0", "4", "100")) {
            show(dynamic(if (corner == "none") "" else """, "cornerRadius": $corner"""))
            val map = rule.onNodeWithTag("seg", useUnmergedTree = true).captureToImage().toPixelMap()
            fun white(x: Int, y: Int) = map[x, y].let { it.red > 0.97f && it.green > 0.97f && it.blue > 0.97f }
            val dp = rule.density.density
            // The track just inside its top-left corner: outside a capsule of radius 16 (the
            // background, white), inside a square or a 4 dp corner (the track's grey). The fill (white, 2 dp in, half
            // the width) at 1 dp in from its top-right corner: outside its capsule (the track),
            // inside a square fill or the old "declared − 2 dp" one (white).
            // (1.9 dp floored to a pixel: short of the fill, which starts 2 dp in.)
            val p = (1.9f * dp).toInt()
            val trackClear = white(p, p)
            val fillRight = (map.width - 4 * dp) / 2 + 2 * dp
            val fillClear = !white((fillRight - 1 * dp).toInt(), (3 * dp).toInt())
            rows += "cornerRadius $corner :: track corner ${if (trackClear) "clear" else "filled"}, fill corner ${if (fillClear) "clear" else "filled"}"
        }
        // A declared border, and a background, follow the capsule too (the ruling 2026-11-16: the
        // border, the shadow and the clip are the capsule's, the declared cornerRadius unused).
        for (corner in listOf("none", "4")) {
            val radius = if (corner == "none") "" else """, "cornerRadius": $corner"""
            show(dynamic("""$radius, "background": "#FFCC00", "borderWidth": 2, "borderColor": "#FF0000""""))
            val map = rule.onNodeWithTag("seg", useUnmergedTree = true).captureToImage().toPixelMap()
            val dp = rule.density.density
            val p = (1.6f * dp).toInt()
            fun at(x: Int, y: Int) = map[x, y].let {
                when {
                    it.red > 0.97f && it.green > 0.97f && it.blue > 0.97f -> "clear"
                    it.red > 0.8f && it.green < 0.3f && it.blue < 0.3f -> "border"
                    else -> "(%d,%d,%d)".format((it.red * 255).toInt(), (it.green * 255).toInt(), (it.blue * 255).toInt())
                }
            }
            // 1.6 dp in from the corner: outside the capsule (clear), on a square or 4 dp corner's
            // border; and the left end's middle, on the capsule's border.
            rows += "border, cornerRadius $corner :: corner ${at(p, p)}, left end ${at(p, map.height / 2)}"
        }
        val table = rows.joinToString("\n")
        Log.i("KjuiSegmentCorner", "\n$table")
        val expected = (listOf("none", "0", "4", "100").map { "cornerRadius $it :: track corner clear, fill corner clear" } +
            listOf("none", "4").map { "border, cornerRadius $it :: corner clear, left end border" }).joinToString("\n")
        assertEquals(expected, table)
    }
}
