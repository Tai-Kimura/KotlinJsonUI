package com.kotlinjsonui.conformance

import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.DynamicView
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The family table of kjui-dynamic-stateful-components-reset-on-unrelated-data
 * (it began as kjui-dynamic-tabview-loses-its-selection, withdrawn: that
 * observation was a stale accessibility read) — NOT part of the conformance
 * suite, and skipped unless requested:
 *
 *   adb shell am instrument -w \
 *     -e class com.kotlinjsonui.conformance.TabViewSelectionProbeTest \
 *     -e tabViewSelectionProbe 1 \
 *     com.kotlinjsonui.conformance.test/androidx.test.runner.AndroidJUnitRunner
 *
 * Dynamic TabViews side by side, each in a View(240 x 150 — five fit the
 * 1280 dp of the conf_ci tablet; a TabView's Scaffold fills its parent), its
 * second tab tapped; its `isSelected` read
 * back from the accessibility tree at each stage. Printed, not asserted
 * (`TABPROBE <scenario> <form> <stage>=<selected>`): the axes are the form of
 * the TabView, the order the tabs are tapped in, a tap on the content first,
 * and what recomposes the screen afterwards — nothing, the parent with the
 * same data, a new map equal to the old, a map with an unrelated key changed,
 * and an `updateData` that writes into the data.
 */
@OptIn(ExperimentalComposeUiApi::class)
@RunWith(AndroidJUnit4::class)
class TabViewSelectionProbeTest {

    @Before
    fun skipUnlessRequested() {
        val enabled = InstrumentationRegistry.getArguments().getString("tabViewSelectionProbe") == "1"
        Assume.assumeTrue("set -e tabViewSelectionProbe 1", enabled)
    }

    private val forms = linkedMapOf(
        "plain" to "",
        "onclick" to ", \"onClick\": \"@{on_tap}\"",
        "cantapfalse" to ", \"onClick\": \"@{on_tap}\", \"canTap\": false",
        "cantapclosed" to ", \"onClick\": \"@{on_tap}\", \"canTap\": \"@{gate_closed}\"",
        "cantapopen" to ", \"onClick\": \"@{on_tap}\", \"canTap\": \"@{gate_open}\"",
        "staticsel" to ", \"selectedIndex\": 0",
        "boundsel" to ", \"selectedIndex\": \"@{sel_boundsel}\"",
    )

    private fun layout(names: List<String>): String {
        val cells = names.joinToString(",") { n ->
            "{\"type\": \"View\", \"width\": 240, \"height\": 150, \"child\": [" +
                "{\"type\": \"TabView\", \"id\": \"tv_$n\", \"tabs\": [{\"title\": \"a_$n\"}, {\"title\": \"b_$n\"}]${forms[n]}}]}"
        }
        return "{\"type\": \"View\", \"orientation\": \"horizontal\", \"child\": [$cells]}"
    }

    private class Screen(val data: MutableState<Map<String, Any>>, val tick: MutableState<Int>)

    private fun launch(names: List<String>, updateDataWrites: Boolean): Pair<ActivityScenario<FixtureHostActivity>, Screen> {
        val base = mutableMapOf<String, Any>(
            "gate_closed" to false, "gate_open" to true, "on_tap" to { }, "sel_boundsel" to 0,
        )
        val screen = Screen(mutableStateOf(base), mutableStateOf(0))
        base["updateData"] = { m: Map<String, Any> ->
            if (updateDataWrites) screen.data.value = screen.data.value + m
        }
        val json = JsonParser.parseString(layout(names)).asJsonObject
        val scenario = ActivityScenario.launch(FixtureHostActivity::class.java)
        scenario.onActivity { activity ->
            activity.setContent {
                // Read here, so a tick recomposes this Column and calls
                // DynamicView again with the same data.
                val t = screen.tick.value
                Column(Modifier.padding(top = 40.dp).semantics { testTagsAsResourceId = true }) {
                    Text("tick $t")
                    DynamicView(json = json, data = screen.data.value)
                }
            }
        }
        return scenario to screen
    }

    private val device get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private fun selected(n: String): Boolean? = device.findObject(By.res("tv_${n}_tab_1"))?.isSelected

    private fun settle() {
        device.waitForIdle()
        Thread.sleep(500)
        device.waitForIdle()
    }

    /** Which of a TabView's two items carries the indicator, read from a screenshot:
     *  the pixel behind each item's icon, the selected one differs. */
    private fun pixelSelected(n: String, shot: android.graphics.Bitmap): String {
        val density = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.density
        return (0..1).joinToString("/") { i ->
            val r = device.findObject(By.res("tv_${n}_tab_$i"))?.visibleBounds ?: return@joinToString "?"
            Integer.toHexString(shot.getPixel(r.centerX().coerceIn(0, shot.width - 1),
                (r.top + (16 * density).toInt()).coerceIn(0, shot.height - 1)))
        }
    }

    private fun report(scenario: String, names: List<String>, stage: String) {
        val shot = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        for (n in names) println("TABPROBE $scenario $n $stage=${selected(n)} pixels_tab0/tab1=${pixelSelected(n, shot)}")
    }

    private fun tapAll(names: List<String>, order: List<String>, contentFirst: Boolean, scenario: String) {
        val density = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.density
        for (n in order) {
            if (contentFirst) {
                val t0 = device.findObject(By.res("tv_${n}_tab_0")) ?: continue
                val b = t0.visibleBounds
                device.click(b.centerX(), b.top - (10 * density).toInt())
                device.waitForIdle()
            }
            device.findObject(By.res("tv_${n}_tab_1"))?.click()
            device.waitForIdle()
            println("TABPROBE $scenario $n right_after_its_tap=${selected(n)}")
        }
    }

    private fun run(scenario: String, names: List<String>, order: List<String>, contentFirst: Boolean,
                    updateDataWrites: Boolean = false, triggers: Boolean = false) {
        val (activity, screen) = launch(names, updateDataWrites)
        device.wait(Until.hasObject(By.res("tv_${names.last()}_tab_1")), 10_000)
        tapAll(names, order, contentFirst, scenario)
        settle()
        report(scenario, names, "after_500ms")
        if (triggers) {
            screen.tick.value = screen.tick.value + 1
            settle()
            report(scenario, names, "after_parent_recomposed_same_data")
            screen.data.value = HashMap(screen.data.value)
            settle()
            report(scenario, names, "after_a_new_map_equal_to_the_old")
            screen.data.value = screen.data.value + ("unrelated" to System.nanoTime())
            settle()
            report(scenario, names, "after_an_unrelated_key_changed")
        }
        activity.close()
    }

    private val canTapFour = listOf("onclick", "cantapfalse", "cantapclosed", "cantapopen")

    /** 40's shape: onClick under four gates, the content tapped first, left to right. */
    @Test fun s1_fortys_shape() = run("s1", canTapFour, canTapFour, contentFirst = true)

    /** The same, tapped right to left: position against canTap. */
    @Test fun s2_reversed() = run("s2", canTapFour, canTapFour.reversed(), contentFirst = true)

    /** The same, the content not tapped. */
    @Test fun s3_no_content_tap() = run("s3", canTapFour, canTapFour, contentFirst = false)

    /** Every form, then what recomposes the screen. */
    @Test fun s4_triggers() {
        val names = listOf("plain", "onclick", "cantapfalse", "staticsel", "boundsel")
        run("s4", names, names, contentFirst = false, triggers = true)
    }

    /** An updateData that writes into the data (a view model would). */
    @Test fun s5_update_data_writes() {
        val names = listOf("plain", "staticsel", "boundsel")
        run("s5", names, names, contentFirst = false, updateDataWrites = true, triggers = true)
    }

    // ---- s6: 40's screen (CanTapGateProbeTest's dynamic rows), read both ways ----

    private val gates = listOf("none", "false", "bclosed", "bopen")

    private fun gate(g: String) = when (g) {
        "false" -> ", \"canTap\": false"
        "bclosed" -> ", \"canTap\": \"@{gate_closed}\""
        "bopen" -> ", \"canTap\": \"@{gate_open}\""
        else -> ""
    }

    private fun fortysItem(kind: String, g: String): String {
        val id = "dyn_${kind}_$g"
        val tap = "\"onClick\": \"@{on_$id}\"" + gate(g)
        return when (kind) {
            "btn" -> "{\"type\": \"Button\", \"id\": \"$id\", \"text\": \"B$g\", $tap}"
            "icl" -> "{\"type\": \"IconLabel\", \"id\": \"$id\", \"text\": \"I$g\", $tap}"
            "radio" -> "{\"type\": \"Radio\", \"id\": \"$id\", \"group\": \"grp_$g\", \"text\": \"R$g\", $tap}"
            "check" -> "{\"type\": \"CheckBox\", \"id\": \"$id\", $tap}"
            "switch" -> "{\"type\": \"Switch\", \"id\": \"$id\", $tap}"
            "toggle" -> "{\"type\": \"Toggle\", \"id\": \"$id\", $tap}"
            "seg" -> "{\"type\": \"Segment\", \"id\": \"$id\", \"width\": 280, \"items\": [\"x_$g\", \"y_$g\"], $tap}"
            else -> "{\"type\": \"View\", \"width\": 280, \"height\": 150, \"child\": [" +
                "{\"type\": \"TabView\", \"id\": \"$id\", \"tabs\": [{\"title\": \"ta_$g\"}, {\"title\": \"tb_$g\"}], $tap}]}"
        }
    }

    private fun collect(node: AccessibilityNodeInfo?, out: MutableMap<String, MutableList<AccessibilityNodeInfo>>) {
        if (node == null) return
        node.viewIdResourceName?.let { out.getOrPut(it.substringAfterLast('/')) { mutableListOf() }.add(node) }
        for (i in 0 until node.childCount) collect(node.getChild(i), out)
    }

    private fun fortysScreen(scenario: String, kinds: List<String>, tapOthers: Boolean) {
        val rows = kinds.map { k -> "{\"type\": \"View\", \"orientation\": \"horizontal\", \"child\": [" +
            gates.joinToString(",") { fortysItem(k, it) } + "]}" }
        val layout = "{\"type\": \"View\", \"orientation\": \"vertical\", \"child\": [${rows.joinToString(",")}]}"
        val data = mutableMapOf<String, Any>("gate_closed" to false, "gate_open" to true)
        for (k in kinds) for (g in gates) data["on_dyn_${k}_$g"] = { }
        data["updateData"] = { _: Map<String, Any> -> }
        val json = JsonParser.parseString(layout).asJsonObject
        val scenarioHandle = ActivityScenario.launch(FixtureHostActivity::class.java)
        scenarioHandle.onActivity { activity ->
            activity.setContent {
                Column(Modifier.padding(top = 40.dp).semantics { testTagsAsResourceId = true }) {
                    DynamicView(json = json, data = data)
                }
            }
        }
        device.wait(Until.hasObject(By.res("dyn_tab_bopen_tab_1")), 10_000)
        if (tapOthers) {
            for (k in kinds.filter { it in listOf("btn", "icl", "check", "switch", "toggle") }) for (g in gates) {
                device.findObject(By.res("dyn_${k}_$g"))?.click(); device.waitForIdle()
            }
            if ("seg" in kinds) for (g in gates) { device.findObject(By.text("y_$g"))?.click(); device.waitForIdle() }
        }
        for (g in gates) {
            device.findObject(By.res("dyn_tab_${g}_tab_1"))?.click()
            device.waitForIdle()
            println("TABPROBE $scenario $g right_after_its_tap=${device.findObject(By.res("dyn_tab_${g}_tab_1"))?.isSelected}")
        }
        Thread.sleep(500)
        val nodes = mutableMapOf<String, MutableList<AccessibilityNodeInfo>>()
        collect(InstrumentationRegistry.getInstrumentation().uiAutomation.rootInActiveWindow, nodes)
        for (g in gates) {
            val all = nodes["dyn_tab_${g}_tab_1"].orEmpty()
            val b = device.findObject(By.res("dyn_tab_${g}_tab_1"))
            val cached = all.map { it.isSelected }
            val refreshed = all.map { it.refresh(); it.isSelected }
            println("TABPROBE $scenario $g by_findObject=${b?.isSelected} tree_nodes=${all.size} " +
                "tree_as_collected=$cached tree_after_refresh=$refreshed bounds=${b?.visibleBounds}")
        }
        // The pixels: each TabView's two items, the selected one carries
        // Material3's indicator pill behind its icon. The screenshot is the
        // third reading, one the accessibility tree cannot cache.
        val shot = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        for (g in gates) {
            for (i in 0..1) {
                val r = device.findObject(By.res("dyn_tab_${g}_tab_$i"))?.visibleBounds ?: continue
                // The indicator sits behind the icon: 16 dp below the item's top, centred.
                val density = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.density
                val x = r.centerX(); val y = r.top + (16 * density).toInt()
                val c = shot.getPixel(x.coerceIn(0, shot.width - 1), y.coerceIn(0, shot.height - 1))
                println("TABPROBE $scenario $g pixel_tab_$i=${Integer.toHexString(c)}")
            }
        }
        scenarioHandle.close()
    }

    /** 40's whole screen, 40's taps. */
    @Test fun s6_fortys_screen() = fortysScreen("s6", listOf("btn", "icl", "radio", "check", "switch", "toggle", "seg", "tab"), tapOthers = true)

    /** 40's whole screen, only the tabs tapped. */
    @Test fun s7_fortys_screen_tabs_only() = fortysScreen("s7", listOf("btn", "icl", "radio", "check", "switch", "toggle", "seg", "tab"), tapOthers = false)

    /** The tab row alone, in 40's cell size. */
    @Test fun s8_tab_row_alone() = fortysScreen("s8", listOf("tab"), tapOthers = false)
}
