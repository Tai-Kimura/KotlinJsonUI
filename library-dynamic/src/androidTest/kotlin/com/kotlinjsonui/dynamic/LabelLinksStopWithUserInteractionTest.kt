package com.kotlinjsonui.dynamic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import com.kotlinjsonui.components.PartialAttribute
import com.kotlinjsonui.components.PartialAttributesText
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `userInteractionEnabled: false` on a Label, or on a view around it, stops
 * its links — a tappable range and a link `linkable` detects — for touch and
 * for TalkBack; a binding gates them (the tap rule, jsonui-cli
 * shared/core/tap_accessibility.rb; 4f ruling, jsonui-cli 1.9.0).
 *
 * Before: the pointer blocker stopped a touch, and each link's own semantics
 * node kept its OnClick action, which TalkBack's double tap called — the
 * range's handler ran and the URL opened (measured, API 35 emulator,
 * LinkSpanUnderOuterClickableProbe). Each case here counts the calls from a
 * touch on the link and from every link node's action, and holds a control
 * where the links are operable, so a count of 0 is not a link that never
 * drew.
 */
@RunWith(AndroidJUnit4::class)
class LabelLinksStopWithUserInteractionTest {
    @get:Rule
    val rule = createComposeRule()

    private val widthDp = 600
    private val style = TextStyle(fontSize = 24.sp)
    private val partialText = "Terms and the Privacy policy text"
    private val urlText = "https://example.com/terms and some plain words"

    private var calls = 0
    private val opened = mutableListOf<String>()
    private var content: @Composable () -> Unit by mutableStateOf({})
    private var gen by mutableStateOf(0)
    private var layout: TextLayoutResult? = null

    private fun start() {
        rule.setContent {
            CompositionLocalProvider(LocalUriHandler provides object : UriHandler {
                override fun openUri(uri: String) { opened += uri }
            }) {
                Box { key(gen) { content() } }
            }
        }
    }

    private fun show(text: String, c: @Composable () -> Unit) {
        calls = 0; opened.clear(); layout = null
        content = {
            val measurer = rememberTextMeasurer()
            val px = with(LocalDensity.current) { widthDp.dp.roundToPx() }
            layout = measurer.measure(AnnotatedString(text), style, constraints = Constraints.fixedWidth(px))
            c()
        }
        gen++
        rule.waitForIdle()
    }

    private fun center(text: String, piece: String): Offset {
        val l = requireNotNull(layout)
        val s = text.indexOf(piece)
        val a = l.getBoundingBox(s)
        val b = l.getBoundingBox(s + piece.length - 1)
        return Rect(a.left, a.top, b.right, b.bottom).center
    }

    /**
     * Calls from a touch on [piece], then from TalkBack's action on every link
     * node. A tappable range is two nodes — its LinkAnnotation's and the
     * library's hit-target — so an operable range answers 2 to the second.
     */
    private fun operate(text: String, piece: String): Pair<Int, Int> {
        rule.onNodeWithTag("t", useUnmergedTree = true).performTouchInput { click(center(text, piece)) }
        rule.waitForIdle()
        val touched = calls + opened.size
        calls = 0; opened.clear()
        val links = rule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.OnClick), useUnmergedTree = true)
            .fetchSemanticsNodes()
            .filter { it.config.getOrNull(SemanticsProperties.TestTag) == null }
        links.forEach { n -> rule.runOnIdle { n.config[SemanticsActions.OnClick].action?.invoke() } }
        rule.waitForIdle()
        return touched to (calls + opened.size)
    }

    /** Differing pixels, with their bounds when there are any. */
    private fun diff(a: ImageBitmap, b: ImageBitmap): String {
        if (a.width != b.width || a.height != b.height) return "size ${a.width}x${a.height} vs ${b.width}x${b.height}"
        val pa = a.toPixelMap(); val pb = b.toPixelMap()
        var n = 0; var l = Int.MAX_VALUE; var t = Int.MAX_VALUE; var r = -1; var btm = -1
        for (x in 0 until a.width) for (y in 0 until a.height) if (pa[x, y] != pb[x, y]) {
            n++; l = minOf(l, x); t = minOf(t, y); r = maxOf(r, x); btm = maxOf(btm, y)
        }
        if (n == 0) return "0"
        var sa = 0f; var sb = 0f; var darkA = 0; var darkB = 0
        for (x in l..r) for (y in t..btm) if (pa[x, y] != pb[x, y]) {
            sa += pa[x, y].red + pa[x, y].green + pa[x, y].blue; sb += pb[x, y].red + pb[x, y].green + pb[x, y].blue
            if (pa[x, y].red < 0.5f) darkA++; if (pb[x, y].red < 0.5f) darkB++
        }
        return "$n in ($l,$t)-($r,$btm) of ${a.width}x${a.height}; mean rgb sum ${sa / n} vs ${sb / n}; dark $darkA vs $darkB"
    }

    private val data: Map<String, Any> = mapOf("onTerms" to { calls++ }, "open" to true, "shut" to false)
    private val partial = """{"type": "Label", "id": "t", "width": $widthDp, "fontSize": 24, "text": "$partialText",
        "partialAttributes": [{"range": "Terms", "onClick": "@{onTerms}"}]"""
    private val linked = """{"type": "Label", "id": "t", "width": $widthDp, "fontSize": 24, "text": "$urlText", "linkable": true"""

    private fun dynamic(text: String, json: String) =
        show(text) { DynamicView(json = JsonParser.parseString(json).asJsonObject, data = data) }

    @Test
    fun theDynamicLabelsLinksStopUnderUserInteractionEnabled() {
        start()
        val cases = listOf(
            // name, text, json, piece, expected (touch, TalkBack)
            listOf("range, no flag (control)", partialText, "$partial}", "Terms", 1 to 2),
            listOf("range, own false", partialText, "$partial, \"userInteractionEnabled\": false}", "Terms", 0 to 0),
            listOf("range, own binding false", partialText, "$partial, \"userInteractionEnabled\": \"@{shut}\"}", "Terms", 0 to 0),
            listOf("range, own binding true (control)", partialText, "$partial, \"userInteractionEnabled\": \"@{open}\"}", "Terms", 1 to 2),
            listOf("range, in a View with false", partialText,
                """{"type": "View", "id": "p", "userInteractionEnabled": false, "child": [$partial}]}""", "Terms", 0 to 0),
            listOf("url, no flag (control)", urlText, "$linked}", "example.com", 1 to 1),
            listOf("url, own false", urlText, "$linked, \"userInteractionEnabled\": false}", "example.com", 0 to 0),
            listOf("url, in a View with a binding false", urlText,
                """{"type": "View", "id": "p", "userInteractionEnabled": "@{shut}", "child": [$linked}]}""", "example.com", 0 to 0),
        )
        val got = cases.map { c ->
            dynamic(c[1] as String, c[2] as String)
            c[0] as String to operate(c[1] as String, c[3] as String)
        }
        assertEquals(cases.map { it[0] as String to it[4] }, got)
    }

    /** kjui's codegen passes `linksEnabled` to the library's PartialAttributesText. */
    @Test
    fun linksEnabledFalseStopsTheLinksAndKeepsTheirDrawing() {
        start()
        val ranges = { listOfNotNull(PartialAttribute.fromJsonRange("Terms", partialText, onClick = { calls++ })) }
        val got = mutableListOf<Pair<String, Pair<Int, Int>>>()
        val drawn = mutableListOf<ImageBitmap>()
        for (enabled in listOf(true, false)) {
            show(partialText) {
                PartialAttributesText(partialText, ranges(), style = style, linksEnabled = enabled,
                    modifier = Modifier.testTag("t").width(widthDp.dp))
            }
            drawn += rule.onNodeWithTag("t").captureToImage()
            got += "range linksEnabled=$enabled" to operate(partialText, "Terms")
            show(urlText) {
                PartialAttributesText(urlText, linkable = true, style = style, linksEnabled = enabled,
                    modifier = Modifier.testTag("t").width(widthDp.dp))
            }
            drawn += rule.onNodeWithTag("t").captureToImage()
            got += "url linksEnabled=$enabled" to operate(urlText, "example.com")
        }
        assertEquals(
            listOf(
                "range linksEnabled=true" to (1 to 2), "url linksEnabled=true" to (1 to 1),
                "range linksEnabled=false" to (0 to 0), "url linksEnabled=false" to (0 to 0)
            ),
            got
        )
        // A stopped URL draws as the operable one does (its colour and
        // underline are the library's own span style).
        assertEquals("url: pixels that differ when stopped", "0", diff(drawn[1], drawn[3]))
        // A stopped range draws as its declaration says — as the same range
        // with no handler draws. An operable range does not: Material 3's
        // Text draws a LinkAnnotation in the theme's primary colour over the
        // range's own style (measured here, 2038 px in the range; a range
        // declaring fontColor #FF0000 draws the primary colour while it has a
        // handler — LinkSpanUnderOuterClickableProbe H). That is left for the
        // ticket's ruling; this pins only the stopped side.
        show(partialText) {
            PartialAttributesText(partialText, listOfNotNull(PartialAttribute.fromJsonRange("Terms", partialText)),
                style = style, modifier = Modifier.testTag("t").width(widthDp.dp))
        }
        val declared = rule.onNodeWithTag("t").captureToImage()
        assertEquals("range: stopped draws otherwise than its declaration", "0", diff(declared, drawn[2]))
    }
}
