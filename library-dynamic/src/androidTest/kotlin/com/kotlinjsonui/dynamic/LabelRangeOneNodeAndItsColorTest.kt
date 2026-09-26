package com.kotlinjsonui.dynamic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kotlinjsonui.components.PartialAttribute
import com.kotlinjsonui.components.PartialAttributesText
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A tappable range of a Label is one node to TalkBack, and draws the colour
 * its declaration gives it (4f rulings 3 and 4, jsonui-cli 1.9.0).
 *
 * Before (measured on an API 35 emulator, LinkSpanUnderOuterClickableProbe):
 * - a range with a handler was two TalkBack nodes — its LinkAnnotation's and
 *   the library's hit-target — each with an OnClick action;
 * - Material 3's Text drew the LinkAnnotation in the theme's primary colour
 *   over the range's own style: a range declaring fontColor drew the primary.
 * The hit-target stays — it is what jsonui-test's Android driver taps
 * (`By.desc(rangeText).clickable(true)`) — and a range no longer adds a
 * LinkAnnotation, so its own SpanStyle is what draws. A link `linkable`
 * detects keeps its LinkAnnotation; the library's own style for it
 * (Configuration.Colors.linkColor, underlined) is added after the link and
 * already drew over Material's (measured #0000EE before and after).
 */
@RunWith(AndroidJUnit4::class)
class LabelRangeOneNodeAndItsColorTest {
    @get:Rule
    val rule = createComposeRule()

    private val widthDp = 600
    private val style = TextStyle(fontSize = 24.sp, color = Color(0xFF94A3B8))
    private var content: @Composable () -> Unit by mutableStateOf({})
    private var gen by mutableStateOf(0)
    private var layout: TextLayoutResult? = null
    private var calls = 0

    /** A theme whose primary is not any colour a range declares (a face's own, measured there: #D4A438). */
    private val theme = lightColorScheme(primary = Color(0xFFD4A438))

    private fun start() {
        rule.setContent { MaterialTheme(colorScheme = theme) { Box(Modifier.background(Color.White)) { key(gen) { content() } } } }
    }

    private fun show(text: String, c: @Composable () -> Unit) {
        layout = null; calls = 0
        content = {
            val px = with(LocalDensity.current) { widthDp.dp.roundToPx() }
            layout = rememberTextMeasurer().measure(AnnotatedString(text), style, constraints = Constraints.fixedWidth(px))
            c()
        }
        gen++
        rule.waitForIdle()
    }

    private fun bounds(text: String, piece: String): Rect {
        val l = requireNotNull(layout)
        val s = text.indexOf(piece)
        val a = l.getBoundingBox(s)
        val b = l.getBoundingBox(s + piece.length - 1)
        return Rect(a.left, a.top, b.right, b.bottom)
    }

    /** The most frequent colour in [r] that is not the white background, as #RRGGBB. */
    private fun ink(r: Rect): String {
        val px = rule.onNodeWithTag("t").captureToImage().toPixelMap()
        val counts = HashMap<Int, Int>()
        for (x in r.left.toInt().coerceAtLeast(0) until r.right.toInt().coerceAtMost(px.width)) {
            for (y in r.top.toInt().coerceAtLeast(0) until r.bottom.toInt().coerceAtMost(px.height)) {
                val c = px[x, y]
                val argb = (Math.round(c.red * 255) shl 16) or (Math.round(c.green * 255) shl 8) or Math.round(c.blue * 255)
                if (argb != 0xFFFFFF) counts[argb] = (counts[argb] ?: 0) + 1
            }
        }
        val top = counts.maxByOrNull { it.value }?.key ?: return "none"
        return "#%06X".format(top)
    }

    /** Untagged nodes with an OnClick action: what TalkBack can activate besides the Label. */
    private fun actionNodes(): Int =
        rule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.OnClick), useUnmergedTree = true)
            .fetchSemanticsNodes()
            .count { it.config.getOrNull(SemanticsProperties.TestTag) == null }

    /** The node jsonui-test's driver taps: clickable, with the range text as its description. */
    private fun driverNodes(piece: String): Int =
        rule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.OnClick), useUnmergedTree = true)
            .fetchSemanticsNodes()
            .count { it.config.getOrNull(SemanticsProperties.ContentDescription)?.contains(piece) == true }

    private val ranged = "Terms of Service and the rest"
    private val linked = "https://example.com/terms and more"

    private fun range(onClick: (() -> Unit)?) =
        listOfNotNull(PartialAttribute.fromJsonRange("Terms of Service", ranged, fontColor = "#FFC364", fontWeight = "bold", onClick = onClick))

    @Test
    fun aRangeIsOneNodeAndDrawsItsDeclaredColour() {
        start()
        val got = linkedMapOf<String, String>()

        show(ranged) { PartialAttributesText(ranged, range { calls++ }, style = style, modifier = Modifier.testTag("t").width(widthDp.dp)) }
        got["range with a handler: action nodes"] = actionNodes().toString()
        got["range with a handler: driver nodes"] = driverNodes("Terms of Service").toString()
        got["range with a handler: ink"] = ink(bounds(ranged, "Terms of Service"))

        show(ranged) { PartialAttributesText(ranged, range(null), style = style, modifier = Modifier.testTag("t").width(widthDp.dp)) }
        got["range with no handler (control): ink"] = ink(bounds(ranged, "Terms of Service"))

        show(linked) { PartialAttributesText(linked, linkable = true, style = style, modifier = Modifier.testTag("t").width(widthDp.dp)) }
        got["detected URL: action nodes"] = actionNodes().toString()
        got["detected URL: ink"] = ink(bounds(linked, "example.com"))

        // A range inside linkable text: one node for the range, one for the URL.
        val both = "Terms of Service at https://example.com/terms"
        show(both) {
            PartialAttributesText(both, listOfNotNull(PartialAttribute.fromJsonRange("Terms of Service", both,
                fontColor = "#FFC364", fontWeight = "bold") { calls++ }), linkable = true, style = style,
                modifier = Modifier.testTag("t").width(widthDp.dp))
        }
        got["range in linkable text: action nodes"] = actionNodes().toString()
        got["range in linkable text: ink"] = ink(bounds(both, "Terms of Service"))

        val link = com.kotlinjsonui.core.Configuration.Colors.linkColor
        val linkHex = "#%06X".format(
            (Math.round(link.red * 255) shl 16) or (Math.round(link.green * 255) shl 8) or Math.round(link.blue * 255)
        )
        assertEquals(
            linkedMapOf(
                "range with a handler: action nodes" to "1",
                "range with a handler: driver nodes" to "1",
                "range with a handler: ink" to "#FFC364",
                "range with no handler (control): ink" to "#FFC364",
                "detected URL: action nodes" to "1",
                "detected URL: ink" to linkHex,
                "range in linkable text: action nodes" to "2",
                "range in linkable text: ink" to "#FFC364",
            ),
            got
        )
    }
}
