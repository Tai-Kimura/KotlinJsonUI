package com.kotlinjsonui.dynamic

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.layout.LayoutInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.google.gson.JsonParser

/**
 * Measures which of the standard modifier stages (ModifierBuilder.standardOrder)
 * a component DynamicView dispatches applies — shared by the opt-in
 * CommonStageFamilyProbe, which prints the rows the family table is built
 * from, and CommonStagesOnEveryComponentTest, which asserts them. One
 * measurement, so the two cannot drift apart.
 *
 * Two instruments, because neither sees every stage:
 * - **elements**: the modifier elements of every layout node under a fixed
 *   root (LayoutInfo.getModifierInfo, the layout tree walked), bare vs with
 *   one stage's attributes. It misses a stage a component takes as a
 *   parameter of its own (a TextField's paddings are its content padding),
 *   and counts one applied to another node (a TabView's id on its tab
 *   items) or in the wrong place (margins inside the size).
 * - **effects**: pixels, the root's size, a tagged node, the handler calls
 *   from a click. It sees nothing on a node that draws nothing.
 *
 * [judge] combines them per stage, as the family table does.
 */
internal class StageMeasurer(private val rule: ComposeContentTestRule) {

    private var json by mutableStateOf("{\"type\": \"View\"}")
    private var taps = 0
    private val data = mapOf<String, Any>("t" to "", "onTap" to { taps++ }, "onOther" to {})

    fun start() {
        rule.setContent {
            Box(Modifier.testTag("root")) {
                DynamicView(json = JsonParser.parseString(json).asJsonObject, data = data)
            }
        }
    }

    /** Called with a line for each measurement that threw. */
    var onError: (String) -> Unit = {}

    private fun <T> show(j: String, read: () -> T): T? = try {
        json = j
        rule.waitForIdle()
        read()
    } catch (e: Throwable) {
        onError("$j ${e.javaClass.simpleName}: ${e.message?.take(120)}")
        null
    }

    private fun children(node: Any): List<Any> {
        val m = node.javaClass.methods.firstOrNull { it.name.startsWith("getChildren") && it.parameterCount == 0 }
            ?: return emptyList()
        @Suppress("UNCHECKED_CAST")
        return (m.invoke(node) as? List<Any>) ?: emptyList()
    }

    private fun collect(node: Any, out: MutableMap<String, Int>) {
        (node as? LayoutInfo)?.getModifierInfo()?.forEach {
            val name = it.modifier.javaClass.simpleName
            out[name] = (out[name] ?: 0) + 1
        }
        children(node).forEach { collect(it, out) }
    }

    /** Modifier element kinds under the root, with their counts. */
    fun elements(j: String): Map<String, Int>? = show(j) {
        val out = mutableMapOf<String, Int>()
        collect(rule.onNodeWithTag("root").fetchSemanticsNode().layoutInfo, out)
        out
    }

    fun capture(j: String): ImageBitmap? = show(j) { rule.onNodeWithTag("root").captureToImage() }

    /** Pixels within 0.03 of the colour under the root. */
    fun colored(j: String, r: Int, g: Int, b: Int): Int? = capture(j)?.toPixelMap()?.let { px ->
        var n = 0
        for (x in 0 until px.width) for (y in 0 until px.height) {
            val c = px[x, y]
            if (kotlin.math.abs(c.red - r / 255f) < 0.03f && kotlin.math.abs(c.green - g / 255f) < 0.03f &&
                kotlin.math.abs(c.blue - b / 255f) < 0.03f) n++
        }
        n
    }

    /** The handler calls from a click at the root's centre. */
    fun clicks(j: String): Int? = show(j) {
        val before = taps
        rule.onNodeWithTag("root").performClick()
        rule.waitForIdle()
        taps - before
    }

    fun tagged(j: String): Boolean? = show(j) {
        rule.onAllNodesWithTag("n", useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
    }

    /** The root's size in whole dp. */
    fun rootDp(j: String): Pair<Int, Int>? = show(j) {
        val size = rule.onNodeWithTag("root").fetchSemanticsNode().size
        val d = rule.density.density
        Math.round(size.width / d) to Math.round(size.height / d)
    }

    /** Pixels that differ; -1 when the two differ in size. */
    fun diff(a: ImageBitmap?, b: ImageBitmap?): Int? {
        if (a == null || b == null) return null
        if (a.width != b.width || a.height != b.height) return -1
        val pa = a.toPixelMap()
        val pb = b.toPixelMap()
        var n = 0
        for (x in 0 until a.width) for (y in 0 until a.height) if (pa[x, y] != pb[x, y]) n++
        return n
    }

    /** What one component shows for every stage. */
    class Measured(
        val type: String,
        val base: Map<String, Int>,
        /** Per stage, the elements measured with its attributes. */
        val with: Map<String, Map<String, Int>>,
        val backgroundPx: Pair<Int?, Int?>,
        val radiusPx: Int?,
        val clickCalls: Int?,
        val disabledCalls: Int?,
        val controlDiff: Int?,
        val alphaDiff: Int?,
        val offsetDiff: Int?,
        val paddingDiff: Int?,
        val redPx: Pair<Int?, Int?>,
        val tagged: Boolean?,
        val sizeDp: Pair<Int, Int>?,
        val marginsDp: Pair<Int, Int>?,
        val uieCalls: Int?,
    ) {
        /** Elements the stage adds over [against] (the bare node when null). */
        fun added(stage: String, against: String? = null): List<String> {
            val w = with[stage] ?: return emptyList()
            val ref = against?.let { with[it] } ?: base
            return w.flatMap { (k, v) -> List(maxOf(0, v - (ref[k] ?: 0))) { k } }.sorted()
        }

        fun removed(stage: String): List<String> {
            val w = with[stage] ?: return emptyList()
            return base.flatMap { (k, v) -> List(maxOf(0, v - (w[k] ?: 0))) { k } }.sorted()
        }
    }

    fun measure(type: String, extra: String): Measured? {
        val node = "{\"type\": \"$type\"$extra"
        val base = elements("$node}") ?: return null
        val with = STAGES.mapNotNull { (stage, attrs) -> elements("$node$attrs}")?.let { stage to it } }.toMap()
        val sized = "$node, \"width\": 111, \"height\": 53"
        val b0 = colored("$node}", 0x33, 0x66, 0xCC)
        val b1 = colored("$sized, \"background\": \"#3366CC\"}", 0x33, 0x66, 0xCC)
        val r1 = colored("$sized, \"background\": \"#3366CC\", \"cornerRadius\": 20}", 0x33, 0x66, 0xCC)
        val c1 = clicks("$sized, \"onClick\": \"@{onTap}\"}")
        val d1 = clicks("$sized, \"onClick\": \"@{onTap}\", \"enabled\": false}")
        // On a node of a fixed 200 x 60, the pixels that change when the
        // stage is added. The control is the same node drawn twice, with
        // another node drawn between.
        val fixed = "{\"type\": \"$type\"${EFFECT_EXTRA[type] ?: extra}, \"width\": 200, \"height\": 60"
        val base0 = capture("$fixed}")
        capture("{\"type\": \"View\"}")
        val ctrl = diff(base0, capture("$fixed}"))
        val alphaD = diff(base0, capture("$fixed, \"alpha\": 0.5}"))
        val offsetD = diff(base0, capture("$fixed, \"offsetX\": 30}"))
        val paddingD = diff(base0, capture("$fixed, \"paddings\": [0, 0, 0, 30]}"))
        val red = colored("$fixed, \"borderColor\": \"#FF0000\", \"borderWidth\": 2}", 0xFF, 0x00, 0x00)
        val redBare = colored("$fixed}", 0xFF, 0x00, 0x00)
        return Measured(
            type = type,
            base = base,
            with = with,
            backgroundPx = b0 to b1,
            radiusPx = r1,
            clickCalls = c1,
            disabledCalls = d1,
            controlDiff = ctrl,
            alphaDiff = alphaD,
            offsetDiff = offsetD,
            paddingDiff = paddingD,
            redPx = redBare to red,
            tagged = tagged("$node, \"id\": \"n\"}"),
            sizeDp = rootDp("$sized}"),
            marginsDp = rootDp("$sized, \"margins\": [7, 7, 7, 7]}"),
            uieCalls = clicks("$sized, \"onClick\": \"@{onTap}\", \"userInteractionEnabled\": false}"),
        )
    }

    companion object {
        /**
         * One spelling per component DynamicView dispatches, with what it
         * needs to draw. Check / EditText / Input are declared spellings of
         * CheckBox / TextField, and HStack / Row / … of View: they reach the
         * same components, and are listed so that every declared type is.
         */
        val TYPES: List<Pair<String, String>> = listOf(
            "Label" to """, "text": "t"""",
            "TextField" to """, "text": "@{t}"""",
            "EditText" to """, "text": "@{t}"""",
            "Input" to """, "text": "@{t}"""",
            "Button" to """, "text": "t", "onClick": "@{onOther}"""",
            "Image" to """, "srcName": "ic_star_filled"""",
            "NetworkImage" to """, "url": "https://example.invalid/x.png"""",
            // CircleImage read src / source / url, not srcName (the key it was
            // handed here, so every stage was measured on the fallback Box —
            // StageMeasurerKeysAreReadTest). It reads Image's source now
            // (jsonui-cli 1.9.0); src stays.
            "CircleImage" to """, "src": "ic_star_filled"""",
            "Switch" to "",
            "CheckBox" to "",
            "Check" to "",
            "Radio" to """, "text": "r"""",
            "Slider" to "",
            "Progress" to "",
            "Indicator" to "",
            "SelectBox" to """, "items": ["a", "b"]""",
            "Segment" to """, "items": ["a", "b"]""",
            "Toggle" to "",
            "ScrollView" to """, "child": [{"type": "Label", "text": "c"}]""",
            "HStack" to """, "child": [{"type": "Label", "text": "c"}]""",
            "VStack" to """, "child": [{"type": "Label", "text": "c"}]""",
            "ZStack" to """, "child": [{"type": "Label", "text": "c"}]""",
            "View" to """, "child": [{"type": "Label", "text": "c"}]""",
            "SafeAreaView" to """, "child": [{"type": "Label", "text": "c"}]""",
            "ConstraintLayout" to """, "child": [{"type": "Label", "text": "c"}]""",
            "Collection" to """, "items": []""",
            "Table" to """, "items": []""",
            "WebView" to """, "url": "data:text/html,probe"""",
            "Web" to """, "url": "data:text/html,probe"""",
            "TabView" to """, "tabs": [{"title": "a"}, {"title": "b"}]""",
            "Embed" to """, "screen": "missing"""",
            "GradientView" to """, "gradient": ["#FF0000", "#0000FF"]""",
            "CircleView" to "",
            "Blur" to "",
            "IconLabel" to """, "text": "t"""",
            "TextView" to """, "text": "@{t}"""",
            "Triangle" to "",
        )

        /**
         * What a component needs to show content its paddings can move, where
         * [TYPES] draws none: a TextField / TextView bound to an empty string
         * draws no text, a SelectBox with nothing selected no label.
         */
        val EFFECT_EXTRA: Map<String, String> = mapOf(
            "TextField" to """, "text": "Wg"""",
            "EditText" to """, "text": "Wg"""",
            "Input" to """, "text": "Wg"""",
            "TextView" to """, "text": "Wg"""",
            "SelectBox" to """, "items": ["a", "b"], "hint": "Wg"""",
        )

        val STAGES: List<Pair<String, String>> = listOf(
            "testTag" to """, "id": "n"""",
            "margins" to """, "margins": [7, 7, 7, 7]""",
            "size" to """, "width": 111, "height": 53""",
            "offset" to """, "offsetX": 3, "offsetY": 3""",
            "alpha" to """, "alpha": 0.5""",
            "shadow" to """, "shadow": "#000000|0|2|0.5|4"""",
            "background" to """, "background": "#3366CC"""",
            "cornerRadius" to """, "background": "#3366CC", "cornerRadius": 6""",
            "border" to """, "borderColor": "#FF0000", "borderWidth": 2""",
            "clickable" to """, "onClick": "@{onTap}"""",
            "enabled" to """, "onClick": "@{onTap}", "enabled": false""",
            "userInteraction" to """, "userInteractionEnabled": false""",
            "padding" to """, "paddings": [5, 5, 5, 5]""",
        )

        /** A circle clips a radius away: judged by the element it adds. */
        private val CIRCLES = setOf("CircleImage", "CircleView")

        /** The page draws differently each time: their pixel diffs are not read. */
        private val UNSTEADY = setOf("WebView", "Web")

        /**
         * Whether [stage] applies, and on what evidence. The same rules the
         * family table (jsonui-cli docs/bugs/fixtures/kjui-stage-family/
         * family_table.py) is built with.
         */
        fun judge(m: Measured, stage: String): Pair<Boolean, String> {
            val el = m.added(stage).isNotEmpty()
            val steady = m.controlDiff == 0 && m.type !in UNSTEADY
            fun changed(d: Int?) = steady && d != null && d != 0
            return when (stage) {
                "testTag" -> m.tagged?.let { it to "a node tagged with the id: $it" } ?: (el to "elements")
                "size" -> m.sizeDp?.let { (it == 111 to 53) to "root ${it.first}x${it.second} dp" } ?: (el to "elements")
                "margins" -> {
                    val a = m.sizeDp
                    val b = m.marginsDp
                    if (a != null && b != null) {
                        (b == (a.first + 14 to a.second + 14)) to "root ${a.first}x${a.second} -> ${b.first}x${b.second} dp"
                    } else el to "elements"
                }
                "background" -> (el || (m.backgroundPx.second ?: 0) > 0) to "elements or ${m.backgroundPx.second} px"
                "cornerRadius" -> {
                    val b1 = m.backgroundPx.second
                    val r1 = m.radiusPx
                    if (b1 != null && b1 > 0 && r1 != null && m.type !in CIRCLES) {
                        (r1 < b1) to "background $b1 px, with the radius $r1 px"
                    } else m.added(stage, "background").isNotEmpty() to "elements over background"
                }
                "clickable" -> (el || (m.clickCalls ?: 0) > 0) to "elements or ${m.clickCalls} calls"
                "enabled" -> if ((m.clickCalls ?: 0) > 0) {
                    (m.disabledCalls == 0) to "${m.disabledCalls} calls under enabled: false"
                } else m.added(stage, "clickable").isNotEmpty() to "elements over clickable (no click reaches)"
                "userInteraction" -> if ((m.clickCalls ?: 0) > 0) {
                    (m.uieCalls == 0) to "${m.uieCalls} calls under userInteractionEnabled: false"
                } else el to "elements (no click reaches)"
                "alpha" -> (el || changed(m.alphaDiff)) to "elements or ${m.alphaDiff} px"
                "offset" -> (el || changed(m.offsetDiff)) to "elements or ${m.offsetDiff} px"
                "padding" -> (el || changed(m.paddingDiff)) to "elements or ${m.paddingDiff} px"
                "border" -> {
                    val (r0, r1) = m.redPx
                    (el || (r0 != null && r1 != null && r1 > r0)) to "elements or red ${r0}->${r1} px"
                }
                else -> el to "elements"
            }
        }
    }
}
