package com.kotlinjsonui.dynamic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.click
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.printToString
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.JsonParser
import com.kotlinjsonui.components.PartialAttribute
import com.kotlinjsonui.components.PartialAttributesText
import org.junit.Assume
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * MEASUREMENT PROBE (opt-in: `-e linkSpanProbe 1`), not an assertion of a
 * rule: how Compose resolves a tap on a link span of a Label against an
 * outer `clickable` on the same node, and what a pointer blocker
 * (`userInteractionEnabled: false`, the codegen's Initial-pass consumer) and
 * TalkBack's action leave of each. Prints one `LINKPROBE` line per
 * measurement (logcat and the instrumentation output).
 *
 * Shapes: the library's PartialAttributesText as kjui's codegen and the
 * dynamic runtime call it, plain Compose (Text + LinkAnnotation, and the
 * deprecated ClickableText), and DynamicView over Label JSON.
 */
@RunWith(AndroidJUnit4::class)
class LinkSpanUnderOuterClickableProbe {
    @get:Rule
    val rule = createComposeRule()

    private val style = TextStyle(fontSize = 24.sp)
    private val widthDp = 600
    private val partialText = "Terms and the Privacy policy text"
    private val urlText = "https://example.com/terms and some plain words"

    private var outer = 0
    private var span = 0
    private val opened = mutableListOf<String>()
    private var content: @Composable () -> Unit by mutableStateOf({})
    private var gen by mutableStateOf(0)
    private var layout: TextLayoutResult? = null
    private val lines = mutableListOf<String>()

    private fun log(line: String) {
        lines += line
        android.util.Log.i("LINKPROBE", line)
        println("LINKPROBE $line")
    }

    private val blocker = Modifier.pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
            }
        }
    }

    private fun start() {
        rule.setContent {
            CompositionLocalProvider(LocalUriHandler provides object : UriHandler {
                override fun openUri(uri: String) { opened += uri }
            }) {
                Box { key(gen) { content() } }
            }
        }
    }

    private fun show(c: @Composable () -> Unit) {
        outer = 0; span = 0; opened.clear(); layout = null
        content = c
        gen++
        rule.waitForIdle()
    }

    /** The first rect of [start, end) in the node's coordinates, via a measurer holding the same text, style and width. */
    @Composable
    private fun Measure(text: String) {
        val measurer = rememberTextMeasurer()
        val px = with(LocalDensity.current) { widthDp.dp.roundToPx() }
        layout = measurer.measure(AnnotatedString(text), style, constraints = Constraints.fixedWidth(px))
    }

    private fun rangeCenter(text: String, piece: String): Offset {
        val l = requireNotNull(layout) { "no layout" }
        val s = text.indexOf(piece)
        val box: Rect = l.getBoundingBox(s).let { first ->
            val last = l.getBoundingBox(s + piece.length - 1)
            Rect(first.left, first.top, last.right, last.bottom)
        }
        return box.center
    }

    private fun tap(tag: String, at: Offset) {
        rule.onNodeWithTag(tag, useUnmergedTree = true).performTouchInput { click(at) }
        rule.waitForIdle()
    }

    private fun counts() = "outer=$outer span=$span opened=${opened.size}"

    private fun semantics(tag: String): String {
        val n = rule.onNodeWithTag(tag, useUnmergedTree = true).fetchSemanticsNode()
        val role = n.config.getOrNull(SemanticsProperties.Role)
        val click = n.config.getOrNull(SemanticsActions.OnClick) != null
        return "role=$role onClick=$click"
    }

    /** TalkBack's double tap on each LinkAnnotation node (its own semantics node: OnClick, no tag, no content description). */
    private fun actOnLinkNodes(): Int {
        val nodes = rule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.OnClick), useUnmergedTree = true)
            .fetchSemanticsNodes()
            .filter { it.config.getOrNull(SemanticsProperties.TestTag) == null && it.config.getOrNull(SemanticsProperties.ContentDescription) == null }
        nodes.forEach { n -> rule.runOnIdle { n.config[SemanticsActions.OnClick].action?.invoke() } }
        rule.waitForIdle()
        return nodes.size
    }

    /** TalkBack's double tap on every untagged node with an OnClick action (link nodes and range hit-targets). */
    private fun actOnUntagged(): Int {
        val nodes = rule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.OnClick), useUnmergedTree = true)
            .fetchSemanticsNodes()
            .filter { it.config.getOrNull(SemanticsProperties.TestTag) == null }
        nodes.forEach { n -> rule.runOnIdle { n.config[SemanticsActions.OnClick].action?.invoke() } }
        rule.waitForIdle()
        return nodes.size
    }

    private fun tree(tag: String) {
        rule.onRoot(useUnmergedTree = true).printToString(maxDepth = 12).lines()
            .filter { it.isNotBlank() }
            .forEach { log("  tree[$tag] $it") }
    }

    @Test
    fun probe() {
        Assume.assumeTrue(
            "set -e linkSpanProbe 1",
            InstrumentationRegistry.getArguments().getString("linkSpanProbe") == "1"
        )
        start()

        // ── A: PartialAttributesText, a range with its own tap, and an outer clickable
        // with Role.Button on the same modifier (kjui codegen's partialAttributes +
        // onClick emit).
        fun partials() = listOfNotNull(PartialAttribute.fromJsonRange("Terms", partialText, onClick = { span++ }))
        show {
            Measure(partialText)
            PartialAttributesText(
                text = partialText, partialAttributes = partials(), style = style,
                modifier = Modifier.testTag("t").width(widthDp.dp).clickable(role = Role.Button) { outer++ }
            )
        }
        tap("t", rangeCenter(partialText, "Terms")); log("A partial+outer tap-on-range: ${counts()}")
        show {
            Measure(partialText)
            PartialAttributesText(
                text = partialText, partialAttributes = partials(), style = style,
                modifier = Modifier.testTag("t").width(widthDp.dp).clickable(role = Role.Button) { outer++ }
            )
        }
        tap("t", rangeCenter(partialText, "policy")); log("A partial+outer tap-off-range: ${counts()}")
        log("A semantics outer: ${semantics("t")}"); tree("A")
        rule.onNode(hasContentDescription("Terms"), useUnmergedTree = true).performSemanticsAction(SemanticsActions.OnClick)
        rule.waitForIdle(); log("A TalkBack action on range node: ${counts()}")
        val nA = actOnLinkNodes(); log("A TalkBack action on $nA LinkAnnotation node(s) as well: ${counts()}")

        // ── B: plain Compose — Text with LinkAnnotation.Clickable, outer clickable.
        fun linked() = buildAnnotatedString {
            withLink(LinkAnnotation.Clickable("terms") { span++ }) { append("Terms") }
            append(partialText.removePrefix("Terms"))
        }
        show {
            Measure(partialText)
            Text(linked(), style = style, modifier = Modifier.testTag("t").width(widthDp.dp).clickable(role = Role.Button) { outer++ })
        }
        tap("t", rangeCenter(partialText, "Terms")); log("B Text+LinkAnnotation+outer tap-on-link: ${counts()}")
        show {
            Measure(partialText)
            Text(linked(), style = style, modifier = Modifier.testTag("t").width(widthDp.dp).clickable(role = Role.Button) { outer++ })
        }
        tap("t", rangeCenter(partialText, "policy")); log("B Text+LinkAnnotation+outer tap-off-link: ${counts()}")
        log("B semantics outer: ${semantics("t")}"); tree("B")

        // ── C: plain Compose — deprecated ClickableText, outer clickable.
        show {
            Measure(partialText)
            @Suppress("DEPRECATION")
            ClickableText(
                AnnotatedString(partialText), style = style,
                modifier = Modifier.testTag("t").width(widthDp.dp).clickable(role = Role.Button) { outer++ },
                onClick = { offset -> if (offset < "Terms".length) span++ }
            )
        }
        tap("t", rangeCenter(partialText, "Terms")); log("C ClickableText+outer tap-on-range: ${counts()}")
        show {
            Measure(partialText)
            @Suppress("DEPRECATION")
            ClickableText(
                AnnotatedString(partialText), style = style,
                modifier = Modifier.testTag("t").width(widthDp.dp).clickable(role = Role.Button) { outer++ },
                onClick = { offset -> if (offset < "Terms".length) span++ }
            )
        }
        tap("t", rangeCenter(partialText, "policy")); log("C ClickableText+outer tap-off-range: ${counts()}")

        // ── D: linkable PartialAttributesText (LinkAnnotation.Url) with an outer clickable.
        show {
            Measure(urlText)
            PartialAttributesText(
                text = urlText, linkable = true, style = style,
                modifier = Modifier.testTag("t").width(widthDp.dp).clickable(role = Role.Button) { outer++ }
            )
        }
        tap("t", rangeCenter(urlText, "example.com")); log("D linkable+outer tap-on-url: ${counts()}")
        show {
            Measure(urlText)
            PartialAttributesText(
                text = urlText, linkable = true, style = style,
                modifier = Modifier.testTag("t").width(widthDp.dp).clickable(role = Role.Button) { outer++ }
            )
        }
        tap("t", rangeCenter(urlText, "plain")); log("D linkable+outer tap-off-url: ${counts()}")
        log("D semantics outer: ${semantics("t")}"); tree("D")

        // ── E: the pointer blocker on the node (userInteractionEnabled: false on the
        // Label, kjui's emit): range and URL taps, and TalkBack's action on the range.
        show {
            Measure(partialText)
            PartialAttributesText(
                text = partialText, partialAttributes = partials(), style = style,
                modifier = Modifier.testTag("t").width(widthDp.dp).then(blocker)
            )
        }
        tap("t", rangeCenter(partialText, "Terms")); log("E partial, blocker on node, tap-on-range: ${counts()}")
        rule.onNode(hasContentDescription("Terms"), useUnmergedTree = true).performSemanticsAction(SemanticsActions.OnClick)
        rule.waitForIdle(); log("E partial, blocker on node, TalkBack action on range: ${counts()}")
        show {
            Measure(urlText)
            PartialAttributesText(
                text = urlText, linkable = true, style = style,
                modifier = Modifier.testTag("t").width(widthDp.dp).then(blocker)
            )
        }
        tap("t", rangeCenter(urlText, "example.com")); log("E linkable, blocker on node, tap-on-url: ${counts()}")
        tree("E-linkable")
        val nE = actOnLinkNodes(); log("E linkable, blocker on node, TalkBack action on $nE link node(s): ${counts()}")

        // ── F: the blocker on a parent (a View with userInteractionEnabled: false).
        show {
            Measure(partialText)
            Box(Modifier.then(blocker)) {
                PartialAttributesText(
                    text = partialText, partialAttributes = partials(), style = style,
                    modifier = Modifier.testTag("t").width(widthDp.dp)
                )
            }
        }
        tap("t", rangeCenter(partialText, "Terms")); log("F partial, blocker on parent, tap-on-range: ${counts()}")
        show {
            Measure(urlText)
            Box(Modifier.then(blocker)) {
                PartialAttributesText(
                    text = urlText, linkable = true, style = style,
                    modifier = Modifier.testTag("t").width(widthDp.dp)
                )
            }
        }
        tap("t", rangeCenter(urlText, "example.com")); log("F linkable, blocker on parent, tap-on-url: ${counts()}")
        val nF = actOnLinkNodes(); log("F linkable, blocker on parent, TalkBack action on $nF link node(s): ${counts()}")

        // ── G: DynamicView over Label JSON (the dynamic runtime's own shapes).
        val data = mapOf<String, Any>("onTap" to { outer++ }, "onTerms" to { span++ })
        fun dyn(json: String) {
            show {
                Measure(if (json.contains("linkable")) urlText else partialText)
                DynamicView(json = JsonParser.parseString(json).asJsonObject, data = data)
            }
        }
        val partialJson = """{"type": "Label", "id": "t", "width": $widthDp, "fontSize": 24, "text": "$partialText",
            "partialAttributes": [{"range": "Terms", "onClick": "@{onTerms}"}]"""
        val linkJson = """{"type": "Label", "id": "t", "width": $widthDp, "fontSize": 24, "text": "$urlText", "linkable": true"""
        dyn("$partialJson, \"onClick\": \"@{onTap}\"}")
        tap("t", rangeCenter(partialText, "Terms")); log("G dynamic partial+onClick tap-on-range: ${counts()}")
        dyn("$partialJson, \"onClick\": \"@{onTap}\"}")
        tap("t", rangeCenter(partialText, "policy")); log("G dynamic partial+onClick tap-off-range: ${counts()}")
        log("G dynamic partial+onClick semantics: ${semantics("t")}")
        dyn("$linkJson, \"onClick\": \"@{onTap}\"}")
        tap("t", rangeCenter(urlText, "example.com")); log("G dynamic linkable+onClick tap-on-url: ${counts()}")
        dyn("$linkJson, \"onClick\": \"@{onTap}\"}")
        tap("t", rangeCenter(urlText, "plain")); log("G dynamic linkable+onClick tap-off-url: ${counts()}")
        log("G dynamic linkable+onClick semantics: ${semantics("t")}")
        dyn("$partialJson, \"userInteractionEnabled\": false}")
        tap("t", rangeCenter(partialText, "Terms")); log("G dynamic partial, uie false, tap-on-range: ${counts()}")
        val nG1 = actOnUntagged(); log("G dynamic partial, uie false, TalkBack action on $nG1 range node(s): ${counts()}")
        dyn("$linkJson, \"userInteractionEnabled\": false}")
        tap("t", rangeCenter(urlText, "example.com")); log("G dynamic linkable, uie false, tap-on-url: ${counts()}")
        val nG = actOnLinkNodes(); log("G dynamic linkable, uie false, TalkBack action on $nG link node(s): ${counts()}")
        dyn("""{"type": "View", "id": "p", "userInteractionEnabled": false, "child": [$partialJson}]}""")
        tap("t", rangeCenter(partialText, "Terms")); log("G dynamic partial in View uie false, tap-on-range: ${counts()}")
        val nG2 = actOnUntagged(); log("G dynamic partial in View uie false, TalkBack action on $nG2 range node(s): ${counts()}")

        // ── H: does a LinkAnnotation draw its range differently from the same text without one?
        fun px(c: @Composable () -> Unit): androidx.compose.ui.graphics.ImageBitmap {
            show(c); return rule.onNodeWithTag("t").captureToImage()
        }
        fun d(a: androidx.compose.ui.graphics.ImageBitmap, b: androidx.compose.ui.graphics.ImageBitmap): String {
            if (a.width != b.width || a.height != b.height) return "size differs"
            val pa = a.toPixelMap(); val pb = b.toPixelMap(); var n = 0
            for (x in 0 until a.width) for (y in 0 until a.height) if (pa[x, y] != pb[x, y]) n++
            return "$n px"
        }
        val plain = px { Text(AnnotatedString(partialText), style = style, modifier = Modifier.testTag("t").width(widthDp.dp)) }
        val withLink = px { Text(linked(), style = style, modifier = Modifier.testTag("t").width(widthDp.dp)) }
        val plain2 = px { Text(AnnotatedString(partialText), style = style, modifier = Modifier.testTag("t").width(widthDp.dp)) }
        log("H Text plain vs plain again: ${d(plain, plain2)}")
        log("H Text plain vs with LinkAnnotation.Clickable(styles=null): ${d(plain, withLink)}")
        fun darkest(img: androidx.compose.ui.graphics.ImageBitmap, x0: Int, x1: Int): String {
            val p = img.toPixelMap(); var best = p[x0, 0]; val hist = mutableMapOf<String, Int>()
            for (x in x0 until x1) for (y in 0 until img.height) {
                val c = p[x, y]
                if (c.red + c.green + c.blue < best.red + best.green + best.blue) best = c
                val k = "%.2f,%.2f,%.2f".format(c.red, c.green, c.blue); hist[k] = (hist[k] ?: 0) + 1
            }
            return "darkest=(%.3f,%.3f,%.3f,a%.2f) top=%s".format(best.red, best.green, best.blue, best.alpha,
                hist.entries.sortedByDescending { it.value }.take(4).joinToString(" ") { "${it.key}x${it.value}" })
        }
        log("H range pixels plain:    ${darkest(plain, 0, 135)}")
        log("H range pixels withLink: ${darkest(withLink, 0, 135)}")
        log("H outside range plain:    ${darkest(plain, 300, 600)}")
        log("H outside range withLink: ${darkest(withLink, 300, 600)}")
        val redOff = px { PartialAttributesText(text = partialText, style = style, modifier = Modifier.testTag("t").width(widthDp.dp),
            partialAttributes = listOfNotNull(PartialAttribute.fromJsonRange("Terms", partialText, fontColor = "#FF0000"))) }
        val redOn = px { PartialAttributesText(text = partialText, style = style, modifier = Modifier.testTag("t").width(widthDp.dp),
            partialAttributes = listOfNotNull(PartialAttribute.fromJsonRange("Terms", partialText, fontColor = "#FF0000", onClick = { span++ }))) }
        log("H range fontColor #FF0000, no handler:   ${darkest(redOff, 0, 135)}")
        log("H range fontColor #FF0000, with handler: ${darkest(redOn, 0, 135)}")
        val urlOn = px { PartialAttributesText(text = urlText, linkable = true, style = style, modifier = Modifier.testTag("t").width(widthDp.dp)) }
        log("H linkable url range: ${darkest(urlOn, 0, 580)}")
        fun partialsOff() = listOfNotNull(PartialAttribute.fromJsonRange("Terms", partialText))
        val lib = px { PartialAttributesText(text = partialText, partialAttributes = partialsOff(), style = style, modifier = Modifier.testTag("t").width(widthDp.dp)) }
        log("H PartialAttributesText range without handler vs plain Text: ${d(plain, lib)}")

        log("DONE ${lines.size} lines")
    }
}
