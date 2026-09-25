package com.kotlinjsonui.dynamic

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.JsonParser
import org.junit.Assume
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Which of the standard modifier stages (ModifierBuilder.standardOrder) each
 * component DynamicView dispatches to actually applies — NOT a test of the
 * suite, and skipped unless requested (`-e stageFamilyProbe 1`).
 *
 * Each component is drawn bare (its type and what it needs to draw), then with
 * the attributes of one stage added. The modifier elements of every layout
 * node under a fixed root (LayoutInfo.getModifierInfo, walked through the
 * layout tree) are counted each time; a stage the component applies adds
 * elements, one it drops adds none. Printed as `STAGE <type> <stage> +[…]`.
 */
@RunWith(AndroidJUnit4::class)
class CommonStageFamilyProbe {
    @get:Rule
    val rule = createComposeRule()

    @Before
    fun skipUnlessRequested() {
        val on = InstrumentationRegistry.getArguments().getString("stageFamilyProbe") == "1"
        Assume.assumeTrue("set -e stageFamilyProbe 1", on)
    }

    /** One spelling per component DynamicView dispatches, with what it needs to draw. */
    private val types: List<Pair<String, String>> = listOf(
        "Label" to """, "text": "t"""",
        "TextField" to """, "text": "@{t}"""",
        "Button" to """, "text": "t", "onClick": "@{onOther}"""",
        "Image" to """, "srcName": "ic_star_filled"""",
        "NetworkImage" to """, "url": "https://example.invalid/x.png"""",
        "CircleImage" to """, "srcName": "ic_star_filled"""",
        "Switch" to "",
        "CheckBox" to "",
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

    private val stages: List<Pair<String, String>> = listOf(
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

    private fun children(node: Any): List<Any> {
        val m = node.javaClass.methods.firstOrNull { it.name.startsWith("getChildren") && it.parameterCount == 0 }
            ?: return emptyList()
        @Suppress("UNCHECKED_CAST")
        return (m.invoke(node) as? List<Any>) ?: emptyList()
    }

    private fun elements(node: Any, out: MutableMap<String, Int>) {
        (node as? LayoutInfo)?.getModifierInfo()?.forEach {
            val name = it.modifier.javaClass.simpleName
            out[name] = (out[name] ?: 0) + 1
        }
        children(node).forEach { elements(it, out) }
    }

    @Test
    fun whichStagesEachComponentApplies() {
        var json by mutableStateOf("{\"type\": \"View\"}")
        var taps = 0
        val data = mapOf<String, Any>("t" to "", "onTap" to { taps++ }, "onOther" to {})
        rule.setContent {
            Box(Modifier.testTag("root")) {
                DynamicView(json = JsonParser.parseString(json).asJsonObject, data = data)
            }
        }
        fun measure(j: String): Map<String, Int>? = try {
            json = j
            rule.waitForIdle()
            val out = mutableMapOf<String, Int>()
            elements(rule.onNodeWithTag("root").fetchSemanticsNode().layoutInfo, out)
            out
        } catch (e: Throwable) {
            println("STAGE_ERROR $j ${e.javaClass.simpleName}: ${e.message?.take(120)}")
            null
        }
        println("STAGE_WALK children method: " + (rule.onNodeWithTag("root").fetchSemanticsNode().layoutInfo.javaClass.methods
            .filter { it.name.startsWith("getChildren") }.joinToString { it.name }))
        // Effects, for the stages a component may apply through its own
        // parameters rather than a modifier (a Button's colours): the pixels of
        // the background colour, and a click at the root's centre.
        fun blue(j: String): Int? = try {
            json = j
            rule.waitForIdle()
            val px = rule.onNodeWithTag("root").captureToImage().toPixelMap()
            var n = 0
            for (x in 0 until px.width) for (y in 0 until px.height) {
                val c = px[x, y]
                if (kotlin.math.abs(c.red - 0x33 / 255f) < 0.03f && kotlin.math.abs(c.green - 0x66 / 255f) < 0.03f &&
                    kotlin.math.abs(c.blue - 0xCC / 255f) < 0.03f) n++
            }
            n
        } catch (e: Throwable) { println("STAGE_ERROR blue $j ${e.javaClass.simpleName}"); null }
        fun clicks(j: String): Int? = try {
            json = j
            rule.waitForIdle()
            val before = taps
            rule.onNodeWithTag("root").performClick()
            rule.waitForIdle()
            taps - before
        } catch (e: Throwable) { println("STAGE_ERROR click $j ${e.javaClass.simpleName}"); null }
        for ((type, extra) in types) {
            val base = measure("{\"type\": \"$type\"$extra}") ?: continue
            val b0 = blue("{\"type\": \"$type\"$extra}")
            val b1 = blue("{\"type\": \"$type\"$extra, \"width\": 111, \"height\": 53, \"background\": \"#3366CC\"}")
            val c1 = clicks("{\"type\": \"$type\"$extra, \"width\": 111, \"height\": 53, \"onClick\": \"@{onTap}\"}")
            println("STAGE_EFFECT $type background_px=$b0->$b1 click_calls=$c1")
            println("STAGE_BASE $type ${base.toSortedMap()}")
            for ((stage, attrs) in stages) {
                val with = measure("{\"type\": \"$type\"$extra$attrs}") ?: continue
                val added = with.flatMap { (k, v) -> List(maxOf(0, v - (base[k] ?: 0))) { k } }.sorted()
                val removed = base.flatMap { (k, v) -> List(maxOf(0, v - (with[k] ?: 0))) { k } }.sorted()
                println("STAGE $type $stage +$added -$removed")
            }
        }
    }
}
