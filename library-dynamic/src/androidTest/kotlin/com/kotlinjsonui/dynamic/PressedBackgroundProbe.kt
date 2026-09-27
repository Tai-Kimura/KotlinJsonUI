package com.kotlinjsonui.dynamic

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.JsonParser
import com.kotlinjsonui.components.keyboardAvoidance
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.FontSpec
import org.junit.Assert.assertEquals
import org.junit.Assume
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * tapBackground is the background while pressed, on every node with a tap
 * (4f ruling (a), jsonui-cli 1.9.0) — the Android twin of SwiftJsonUI's
 * ConformanceHost PressedBackgroundProbe (0b6df28). NOT part of any suite:
 * opt in with the instrumentation argument `pressedBackgroundProbe=1`
 * (`-Pandroid.testInstrumentationRunnerArguments.pressedBackgroundProbe=1`).
 *
 * Two paths over one layout:
 * - dynamic: DynamicView over [PB_LAYOUT] (this branch's ModifierBuilder:
 *   the click's own press draws tapBackground over the background);
 * - codegen: what kjui_tools of jsonui-cli 6f37b5c2 emits for it —
 *   ComposeBuilder#generate_component on the layout (TapAccessibility
 *   annotated, depth 2), pasted unchanged in [PbCodegen]; the data surface is
 *   [PbCodegenData].
 *
 * The layout (the iOS probe's, plus a View whose tap is gated by a bound
 * canTap):
 *   {"type":"View","id":"pbRoot","orientation":"vertical","spacing":12,"width":"matchParent","data":[{"name":"onView","class":"(() -> Unit)?"},{"name":"onLabel","class":"(() -> Unit)?"},{"name":"onEmpty","class":"(() -> Unit)?"},{"name":"onGated","class":"(() -> Unit)?"},{"name":"onScrollItem","class":"(() -> Unit)?"},{"name":"gateOpen","class":"Boolean"}],"child":[
 *   {"type":"View","id":"pbView","width":160,"height":60,"paddings":[14,14,14,14],"background":"#0000FF","tapBackground":"#FF0000","onClick":"@{onView}","child":[{"type":"Label","text":"view","fontColor":"#FFFFFF"}]},
 *   {"type":"Label","id":"pbLabel","width":160,"height":60,"text":"label","fontColor":"#FFFFFF","background":"#0000FF","tapBackground":"#FF0000","onClick":"@{onLabel}"},
 *   {"type":"View","id":"pbEmpty","width":160,"height":60,"background":"#0000FF","tapBackground":"#FF0000","onClick":"@{onEmpty}"},
 *   {"type":"View","id":"pbGated","width":160,"height":60,"background":"#0000FF","tapBackground":"#FF0000","onClick":"@{onGated}","canTap":"@{gateOpen}","child":[{"type":"Label","text":"gated","fontColor":"#FFFFFF"}]},
 *   {"type":"View","id":"pbNoTap","width":160,"height":60,"background":"#0000FF","tapBackground":"#FF0000","child":[{"type":"Label","text":"no tap","fontColor":"#FFFFFF"}]},
 *   {"type":"ScrollView","id":"pbScroll","width":160,"height":120,"child":[{"type":"View","orientation":"vertical","child":[{"type":"View","id":"pbScrollItem","width":160,"height":80,"background":"#0000FF","tapBackground":"#FF0000","onClick":"@{onScrollItem}","child":[{"type":"Label","text":"item","fontColor":"#FFFFFF"}]},{"type":"View","width":160,"height":400,"background":"#CCCCCC"}]}]}
 *   ]}
 *
 * A UI test sees no colour while a finger is down unless it reads the
 * screen then, so the probe does: it puts a pointer down 8dp in from the
 * bottom-right corner of the node (clear of the child labels), advances the
 * clock 500 ms, reads its own window's pixel at the pointer, lifts it,
 * advances 400 ms, and reads the pixel again. The ScrollView's item is held,
 * then dragged up 40dp while still down — the list scrolls, which must end
 * the press — and read again before and after the lift, at the item's own
 * spot: where the item is now (its position in the root, which the
 * scroll moved), not where it was. A tap (performClick) on each node counts
 * what that tap called.
 *
 * Expected, per the ruling (asserted after every reading is printed):
 * - pbView, pbLabel, pbEmpty, pbScrollItem: red while held, blue after;
 * - pbGated: as those with the gate open; blue both times with it shut;
 * - pbNoTap (no tap): blue both times;
 * - the scroll item after the drag: blue while the finger is still down,
 *   the list moved, and the drag called nothing;
 * - each tap calls its handler once; the shut gate's, never.
 * Colours are read as "red" / "blue" by their dominant channel: a pressed
 * node also draws the click's indication over its background.
 */
@RunWith(AndroidJUnit4::class)
class PressedBackgroundProbe {
    @get:Rule
    val rule = createComposeRule()

    @Before
    fun skipUnlessRequested() {
        val on = InstrumentationRegistry.getArguments().getString("pressedBackgroundProbe") == "1"
        Assume.assumeTrue("opt-in: -Pandroid.testInstrumentationRunnerArguments.pressedBackgroundProbe=1", on)
    }

    private val counts = ConcurrentHashMap<String, AtomicInteger>()
    private fun hit(k: String) { counts.getOrPut(k) { AtomicInteger() }.incrementAndGet() }
    private fun calls(k: String) = counts[k]?.get() ?: 0

    /** The codegen screen's data surface (what `kjui build` writes for the layout's `data`). */
    class PbCodegenData(
        val onView: (() -> Unit)? = null,
        val onLabel: (() -> Unit)? = null,
        val onEmpty: (() -> Unit)? = null,
        val onGated: (() -> Unit)? = null,
        val onScrollItem: (() -> Unit)? = null,
        val gateOpen: Boolean? = null
    )

    /** jsonui-cli 6f37b5c2, kjui ComposeBuilder#generate_component on [PB_LAYOUT] — pasted unchanged. */
    @Composable
    private fun PbCodegen(data: PbCodegenData) {
        Column(
            modifier = Modifier
                .testTag("pbRoot")
                .semantics { testTagsAsResourceId = true }
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .testTag("pbView")
                    .semantics { testTagsAsResourceId = true }
                    .requiredWidth(160.dp)
                    .requiredHeight(60.dp)
                    .then(run { var isPressed by remember { mutableStateOf(false) }; Modifier.pointerInput(Unit) { awaitEachGesture { awaitFirstDown(requireUnconsumed = false); isPressed = true; waitForUpOrCancellation(); isPressed = false } }.background(if (isPressed) Color(android.graphics.Color.parseColor("#FF0000")) else Color(android.graphics.Color.parseColor("#0000FF"))) })
                    .clickable(role = Role.Button) { data.onView?.invoke() }
                    .padding(top = 14.dp, end = 14.dp, bottom = 14.dp, start = 14.dp)
            ) {
                val resolved_text1 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = "view",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    fontFamily = resolved_text1.family,
                    fontWeight = resolved_text1.weight,
                    fontSize = resolved_text1.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text1.style ?: FontStyle.Normal,
                    modifier = Modifier
                )
            }
            val resolved_text2 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = null,
                italic = false
            ))
            Text(
                text = "label",
                color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                fontFamily = resolved_text2.family,
                fontWeight = resolved_text2.weight,
                fontSize = resolved_text2.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text2.style ?: FontStyle.Normal,
                modifier = Modifier
                    .testTag("pbLabel")
                    .semantics { testTagsAsResourceId = true }
                    .requiredWidth(160.dp)
                    .requiredHeight(60.dp)
                    .then(run { var isPressed by remember { mutableStateOf(false) }; Modifier.pointerInput(Unit) { awaitEachGesture { awaitFirstDown(requireUnconsumed = false); isPressed = true; waitForUpOrCancellation(); isPressed = false } }.background(if (isPressed) Color(android.graphics.Color.parseColor("#FF0000")) else Color(android.graphics.Color.parseColor("#0000FF"))) })
                    .clickable(role = Role.Button) { data.onLabel?.invoke() }
            )
            Box(
                modifier = Modifier
                    .testTag("pbEmpty")
                    .semantics { testTagsAsResourceId = true }
                    .requiredWidth(160.dp)
                    .requiredHeight(60.dp)
                    .then(run { var isPressed by remember { mutableStateOf(false) }; Modifier.pointerInput(Unit) { awaitEachGesture { awaitFirstDown(requireUnconsumed = false); isPressed = true; waitForUpOrCancellation(); isPressed = false } }.background(if (isPressed) Color(android.graphics.Color.parseColor("#FF0000")) else Color(android.graphics.Color.parseColor("#0000FF"))) })
                    .clickable(role = Role.Button) { data.onEmpty?.invoke() }
            ) {
            }
            Box(
                modifier = Modifier
                    .testTag("pbGated")
                    .semantics { testTagsAsResourceId = true }
                    .requiredWidth(160.dp)
                    .requiredHeight(60.dp)
                    .then(run { var isPressed by remember { mutableStateOf(false) }; Modifier.pointerInput(Unit) { awaitEachGesture { awaitFirstDown(requireUnconsumed = false); isPressed = true; waitForUpOrCancellation(); isPressed = false } }.background(if (isPressed && (data.gateOpen ?: false)) Color(android.graphics.Color.parseColor("#FF0000")) else Color(android.graphics.Color.parseColor("#0000FF"))) })
                    .then(if ((data.gateOpen ?: false)) Modifier.clickable(role = Role.Button) { data.onGated?.invoke() } else Modifier)
            ) {
                val resolved_text3 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = "gated",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    fontFamily = resolved_text3.family,
                    fontWeight = resolved_text3.weight,
                    fontSize = resolved_text3.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text3.style ?: FontStyle.Normal,
                    modifier = Modifier
                )
            }
            Box(
                modifier = Modifier
                    .testTag("pbNoTap")
                    .semantics { testTagsAsResourceId = true }
                    .requiredWidth(160.dp)
                    .requiredHeight(60.dp)
                    .background(Color(android.graphics.Color.parseColor("#0000FF")))
            ) {
                val resolved_text4 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = "no tap",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    fontFamily = resolved_text4.family,
                    fontWeight = resolved_text4.weight,
                    fontSize = resolved_text4.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text4.style ?: FontStyle.Normal,
                    modifier = Modifier
                )
            }
            val scrollPagingStatepbScroll = rememberLazyListState()
            LazyColumn(
                state = scrollPagingStatepbScroll,
                modifier = Modifier
                    .testTag("pbScroll")
                    .semantics { testTagsAsResourceId = true }
                    .requiredWidth(160.dp)
                    .requiredHeight(120.dp)
                    .keyboardAvoidance(scrollPagingStatepbScroll, 20)
            ) {
                item {
                Column(
                ) {
                    Box(
                        modifier = Modifier
                            .testTag("pbScrollItem")
                            .semantics { testTagsAsResourceId = true }
                            .requiredWidth(160.dp)
                            .requiredHeight(80.dp)
                            .then(run { var isPressed by remember { mutableStateOf(false) }; Modifier.pointerInput(Unit) { awaitEachGesture { awaitFirstDown(requireUnconsumed = false); isPressed = true; waitForUpOrCancellation(); isPressed = false } }.background(if (isPressed) Color(android.graphics.Color.parseColor("#FF0000")) else Color(android.graphics.Color.parseColor("#0000FF"))) })
                            .clickable(role = Role.Button) { data.onScrollItem?.invoke() }
                    ) {
                        val resolved_text5 = Configuration.Font.resolve(FontSpec(
                            family = null,
                            weight = null,
                            size = null,
                            italic = false
                        ))
                        Text(
                            text = "item",
                            color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                            fontFamily = resolved_text5.family,
                            fontWeight = resolved_text5.weight,
                            fontSize = resolved_text5.size ?: TextUnit.Unspecified,
                            fontStyle = resolved_text5.style ?: FontStyle.Normal,
                            modifier = Modifier
                        )
                    }
                    Box(
                        modifier = Modifier
                            .requiredWidth(160.dp)
                            .requiredHeight(400.dp)
                            .background(Color(android.graphics.Color.parseColor("#CCCCCC")))
                    ) {
                    }
                }
                }
            }
        }
    }

    private fun show(path: String, gateOpen: Boolean) {
        val handlers = listOf("onView", "onLabel", "onEmpty", "onGated", "onScrollItem")
        if (path == "codegen") {
            val data = PbCodegenData(
                onView = { hit("onView") }, onLabel = { hit("onLabel") }, onEmpty = { hit("onEmpty") },
                onGated = { hit("onGated") }, onScrollItem = { hit("onScrollItem") }, gateOpen = gateOpen
            )
            rule.setContent { PbCodegen(data) }
        } else {
            val data = mutableMapOf<String, Any>("gateOpen" to gateOpen)
            for (h in handlers) data[h] = { hit(h) }
            val json = JsonParser.parseString(PB_LAYOUT).asJsonObject
            rule.setContent { DynamicView(json = json, data = data) }
        }
        rule.waitForIdle()
    }

    /** "red" / "blue" by the dominant channel, else the value. */
    private fun colourAt(root: Offset): String {
        val bitmap = rule.onRoot().captureToImage().asAndroidBitmap()
        val px = bitmap.getPixel(root.x.toInt().coerceIn(0, bitmap.width - 1), root.y.toInt().coerceIn(0, bitmap.height - 1))
        val r = (px shr 16) and 0xFF; val g = (px shr 8) and 0xFF; val b = px and 0xFF
        return when {
            r >= 120 && g <= 90 && b <= 90 -> "red"
            b >= 120 && r <= 90 && g <= 90 -> "blue"
            else -> "other(%02X%02X%02X)".format(r, g, b)
        }
    }

    /**
     * The colour at [local] in the node [tag] where the node is now (its
     * position in the root, fetched afresh); "not shown" when that spot is
     * outside what the node shows (its bounds, cut at the viewport).
     */
    private fun colourOnNode(tag: String, local: Offset): String {
        val node = rule.onNodeWithTag(tag, useUnmergedTree = true).fetchSemanticsNode()
        val spot = node.positionInRoot + local
        return if (node.boundsInRoot.contains(spot)) colourAt(spot) else "not shown (${spot.x}, ${spot.y})"
    }

    private fun inset(size: Size): Offset {
        val d = with(rule.density) { 8.dp.toPx() }
        return Offset(size.width - d, size.height - d)
    }

    private fun settle(ms: Long) {
        rule.mainClock.advanceTimeBy(ms)
        rule.waitForIdle()
    }

    /** Held 500 ms, then 400 ms after the lift: the colour at the pointer each time. */
    private fun hold(tag: String): Pair<String, String> {
        val node = rule.onNodeWithTag(tag, useUnmergedTree = true)
        val bounds = node.fetchSemanticsNode().boundsInRoot
        val local = inset(bounds.size)
        val root = bounds.topLeft + local
        node.performTouchInput { down(local) }
        settle(500)
        val held = colourAt(root)
        node.performTouchInput { up() }
        settle(400)
        return held to colourAt(root)
    }

    private data class Reading(val path: String, val gate: Boolean, val tag: String, val what: String, val got: Any, val want: Any)

    private fun measure(path: String, gateOpen: Boolean) {
        show(path, gateOpen)
        val readings = mutableListOf<Reading>()
        fun read(tag: String, what: String, got: Any, want: Any) { readings += Reading(path, gateOpen, tag, what, got, want) }

        for (tag in listOf("pbView", "pbLabel", "pbEmpty")) {
            val (held, after) = hold(tag)
            read(tag, "held", held, "red"); read(tag, "after", after, "blue")
        }
        hold("pbGated").let { (held, after) ->
            read("pbGated", "held", held, if (gateOpen) "red" else "blue"); read("pbGated", "after", after, "blue")
        }
        hold("pbNoTap").let { (held, after) ->
            read("pbNoTap", "held", held, "blue"); read("pbNoTap", "after", after, "blue")
        }

        // The scroll view's item: held, then dragged while still down. Where
        // the item is, is its position in the root: boundsInRoot is cut at the
        // list's viewport, so once the item's top scrolled above it the bounds
        // kept the old top, "list moved" read false, and the colour was read
        // at the old spot — the grey spacer that had scrolled under it. On an
        // API 35 emulator: position 720 -> 656 px, the list's scroll 0 -> 64
        // px, boundsInRoot.top 720 -> 720 (and blue at the item's own spot
        // while still down).
        val item = rule.onNodeWithTag("pbScrollItem", useUnmergedTree = true)
        // Values, taken now: a SemanticsNode reads its layout when asked, so
        // the node's own positionInRoot after the drag is the moved one.
        val start = item.fetchSemanticsNode()
        val startY = start.positionInRoot.y
        val local = inset(start.boundsInRoot.size)
        item.performTouchInput { down(local) }
        settle(500)
        read("pbScrollItem", "held", colourOnNode("pbScrollItem", local), "red")
        val step = with(rule.density) { 10.dp.toPx() }
        repeat(4) { item.performTouchInput { moveBy(Offset(0f, -step)) }; settle(16) }
        settle(400)
        read("pbScrollItem", "list moved", item.fetchSemanticsNode().positionInRoot.y < startY, true)
        read("pbScrollItem", "dragged, still down", colourOnNode("pbScrollItem", local), "blue")
        item.performTouchInput { up() }
        settle(400)
        read("pbScrollItem", "after the drag", colourOnNode("pbScrollItem", local), "blue")
        read("pbScrollItem", "calls from the drag", calls("onScrollItem"), 0)

        // Taps: what each tap called — its handler's count after it less
        // before it. The hold above is a press and a release on the node,
        // which the click answers as well (Compose's clickable calls on the
        // release however long the press); the ruling says nothing of a hold,
        // so its calls are printed, not judged. SwiftJsonUI's twin reads a
        // tap the same way (after - before).
        val tapped = listOf("pbView" to "onView", "pbLabel" to "onLabel", "pbEmpty" to "onEmpty", "pbGated" to "onGated")
        for ((tag, handler) in tapped) {
            val before = calls(handler)
            Log.i(TAG, "PB path=$path gate=${if (gateOpen) "open" else "shut"} $tag calls from the hold: $before (not judged)")
            rule.onNodeWithTag(tag, useUnmergedTree = true).performClick()
            settle(400)
            read(tag, "tap calls", calls(handler) - before, if (tag == "pbGated" && !gateOpen) 0 else 1)
        }

        for (r in readings) {
            Log.i(TAG, "PB path=${r.path} gate=${if (r.gate) "open" else "shut"} ${r.tag} ${r.what}: ${r.got} (want ${r.want})")
        }
        val wrong = readings.filter { it.got != it.want }
            .map { "${it.path}/${if (it.gate) "open" else "shut"} ${it.tag} ${it.what}: ${it.got}, want ${it.want}" }
        assertEquals("readings off the ruling", emptyList<String>(), wrong)
    }

    @Test fun dynamicGateOpen() = measure("dynamic", gateOpen = true)
    @Test fun dynamicGateShut() = measure("dynamic", gateOpen = false)
    @Test fun codegenGateOpen() = measure("codegen", gateOpen = true)
    @Test fun codegenGateShut() = measure("codegen", gateOpen = false)

    private companion object {
        const val TAG = "PressedBackgroundProbe"
        const val PB_LAYOUT = """{"type":"View","id":"pbRoot","orientation":"vertical","spacing":12,"width":"matchParent","data":[{"name":"onView","class":"(() -> Unit)?"},{"name":"onLabel","class":"(() -> Unit)?"},{"name":"onEmpty","class":"(() -> Unit)?"},{"name":"onGated","class":"(() -> Unit)?"},{"name":"onScrollItem","class":"(() -> Unit)?"},{"name":"gateOpen","class":"Boolean"}],"child":[{"type":"View","id":"pbView","width":160,"height":60,"paddings":[14,14,14,14],"background":"#0000FF","tapBackground":"#FF0000","onClick":"@{onView}","child":[{"type":"Label","text":"view","fontColor":"#FFFFFF"}]},{"type":"Label","id":"pbLabel","width":160,"height":60,"text":"label","fontColor":"#FFFFFF","background":"#0000FF","tapBackground":"#FF0000","onClick":"@{onLabel}"},{"type":"View","id":"pbEmpty","width":160,"height":60,"background":"#0000FF","tapBackground":"#FF0000","onClick":"@{onEmpty}"},{"type":"View","id":"pbGated","width":160,"height":60,"background":"#0000FF","tapBackground":"#FF0000","onClick":"@{onGated}","canTap":"@{gateOpen}","child":[{"type":"Label","text":"gated","fontColor":"#FFFFFF"}]},{"type":"View","id":"pbNoTap","width":160,"height":60,"background":"#0000FF","tapBackground":"#FF0000","child":[{"type":"Label","text":"no tap","fontColor":"#FFFFFF"}]},{"type":"ScrollView","id":"pbScroll","width":160,"height":120,"child":[{"type":"View","orientation":"vertical","child":[{"type":"View","id":"pbScrollItem","width":160,"height":80,"background":"#0000FF","tapBackground":"#FF0000","onClick":"@{onScrollItem}","child":[{"type":"Label","text":"item","fontColor":"#FFFFFF"}]},{"type":"View","width":160,"height":400,"background":"#CCCCCC"}]}]}]}"""
    }
}
