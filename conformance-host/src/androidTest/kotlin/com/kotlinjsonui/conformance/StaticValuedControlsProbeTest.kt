package com.kotlinjsonui.conformance

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Rect
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.gson.JsonParser
import com.kotlinjsonui.conformance.staticvalued.StaticControlsGeneratedView
import com.kotlinjsonui.conformance.staticvalued.StaticControlsViewModel
import com.kotlinjsonui.conformance.staticvalued.StaticInputsGeneratedView
import com.kotlinjsonui.conformance.staticvalued.StaticInputsViewModel
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.dynamic.DynamicView
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A control written with a static value takes a user's tap (ticket
 * static-valued-controls-do-not-change-on-a-users-tap: the static value is the
 * initial one — the reading HTML gives `checked`). NOT part of the conformance
 * suite, and skipped unless requested:
 *
 *   adb shell am instrument -w \
 *     -e class com.kotlinjsonui.conformance.StaticValuedControlsProbeTest \
 *     -e staticValuedProbe 1 \
 *     com.kotlinjsonui.conformance.test/androidx.test.runner.AndroidJUnitRunner
 *
 * One layout of static values — a Switch, a Toggle (the normalizer writes it
 * Switch), a CheckBox, a Radio of items, a group of two single Radios (the
 * first checked), a Segment, a TabView, a Slider and two SelectBoxes (by
 * selectedIndex and by selectedItem) — on two paths: what `kjui build` emits
 * for it (staticvalued/, pasted) and DynamicView. Every control is tapped
 * through UiAutomator (a real tap), and read by its pixels and, where the
 * node says, by its checked / selected / text.
 *
 * The dynamic path is also the positive control for the taps: the same tap
 * code must move all ten there. Its data is wired as a screen wires it —
 * `updateData` writes into the data — since a group of single Radios keeps its
 * selection there.
 *
 * A second screen (`inputs`) holds the rest of what attribute_definitions.json
 * declares two-way: a TextField and a TextView (a typed key), a SelectBox by
 * selectedValue and one by date (the compact calendar's day, then its confirm).
 * An input is judged by its text — a caret moves pixels on its own.
 *
 * Measured 2026-09-26 (emulator-5598 conf_ci API 35, rel/2.42.0 329f5d6,
 * kjui_tools of jsonui-cli rel/v1.8.121): dynamic, all fourteen moved — though
 * its date SelectBox does not start at the static date (empty; the calendar opens
 * on today); codegen, the TabView, the TextField and the TextView moved — the rest
 * are emitted with fixed values and `{ }` callbacks, and the Radio group's
 * `updateData` has no branch for its key.
 */
@OptIn(ExperimentalComposeUiApi::class)
@RunWith(AndroidJUnit4::class)
class StaticValuedControlsProbeTest {

    @Before
    fun skipUnlessRequested() {
        val enabled = InstrumentationRegistry.getArguments().getString("staticValuedProbe") == "1"
        Assume.assumeTrue("set -e staticValuedProbe 1", enabled)
    }

    /** The layout `kjui build` was given (after the normalizer). */
    private val layout = """
        {"type": "View", "orientation": "vertical", "spacing": 8, "width": "matchParent", "child": [
          {"type": "Switch", "id": "sw", "isOn": false},
          {"type": "Switch", "id": "tg", "isOn": false},
          {"type": "CheckBox", "id": "cb", "label": "cbl", "isOn": false},
          {"type": "Radio", "id": "rv", "items": ["ra", "rb"], "selectedValue": "ra"},
          {"type": "Radio", "id": "rg1", "group": "grp", "text": "rg1", "checked": true},
          {"type": "Radio", "id": "rg2", "group": "grp", "text": "rg2"},
          {"type": "Segment", "id": "seg", "items": ["sx", "sy"], "selectedIndex": 0},
          {"type": "View", "width": "matchParent", "height": 130, "child": [
            {"type": "TabView", "id": "tab", "tabs": [{"title": "ta"}, {"title": "tb"}], "selectedIndex": 0}]},
          {"type": "Slider", "id": "sl", "minimumValue": 0, "maximumValue": 1, "value": 0.2},
          {"type": "SelectBox", "id": "sb", "height": 40, "items": ["pp", "qq"], "selectedIndex": 0},
          {"type": "SelectBox", "id": "sbi", "height": 40, "items": ["pp", "qq"], "selectedItem": "pp"}
        ]}
    """.trimIndent()

    /** The text inputs and the other two SelectBox spellings — a second screen. */
    private val inputsLayout = """
        {"type": "View", "orientation": "vertical", "spacing": 8, "width": "matchParent", "child": [
          {"type": "TextField", "id": "tf", "height": 40, "text": "t0"},
          {"type": "TextView", "id": "tv", "height": 60, "text": "v0"},
          {"type": "SelectBox", "id": "sbv", "height": 40, "items": ["pp", "qq"], "selectedValue": "pp"},
          {"type": "SelectBox", "id": "sbd", "height": 40, "selectItemType": "Date", "datePickerMode": "date", "dateStringFormat": "yyyy-MM-dd", "selectedDate": "2026-01-02"},
          {"type": "Slider", "id": "sln", "minimumValue": -2, "maximumValue": 1}
        ]}
    """.trimIndent()

    private val groups = linkedMapOf(
        "controls" to listOf("sw", "tg", "cb", "rv", "rg", "seg", "tab", "sl", "sb", "sbi"),
        "inputs" to listOf("tf", "tv", "sbv", "sbd", "sln"),
    )
    private var group = "controls"

    /** What each control shows before the user touches it — the static value — where its node says. */
    private val declaredReading = mapOf(
        "sw" to "checked=false", "tg" to "checked=false", "cb" to "checked=false",
        "seg" to "sy.selected=false", "tab" to "tb.selected=false", "sb" to "texts=pp", "sbi" to "texts=pp",
        "tf" to "texts=t0", "tv" to "texts=v0", "sbv" to "texts=pp", "sbd" to "texts=2026-01-02",
        "sln" to "value=-2.000 of -2..1",
    )
    private val controls get() = groups.getValue(group)

    private val device get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    private val density get() = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.density

    private fun settle() {
        device.waitForIdle()
        Thread.sleep(600)
        device.waitForIdle()
    }

    /** Bumped after the choice: the screen recomposes with something that concerns no control. */
    private val tick = mutableStateOf(0)

    private fun launch(path: String): ActivityScenario<FixtureHostActivity> {
        tick.value = 0
        val scenario = ActivityScenario.launch(FixtureHostActivity::class.java)
        scenario.onActivity { activity ->
            DynamicModeManager.setDynamicModeEnabled(activity, false)
            val json = JsonParser.parseString(if (group == "inputs") inputsLayout else layout).asJsonObject
            activity.setContent {
                Column(Modifier.padding(top = 40.dp).semantics { testTagsAsResourceId = true }) {
                    if (path == "codegen") {
                        val app = ApplicationProvider.getApplicationContext<Application>()
                        // A new modifier makes the generated view run again —
                        // the unrelated change; a test tag moves no pixel.
                        val t = tick.value
                        val unrelated = if (t == 0) Modifier else Modifier.testTag("unrelated$t")
                        if (group == "inputs") {
                            val vm = remember { StaticInputsViewModel(app) }
                            val data by vm.data.collectAsState()
                            StaticInputsGeneratedView(data = data, viewModel = vm, modifier = unrelated)
                        } else {
                            val vm = remember { StaticControlsViewModel(app) }
                            val data by vm.data.collectAsState()
                            StaticControlsGeneratedView(data = data, viewModel = vm, modifier = unrelated)
                        }
                    } else {
                        // As a screen wires it: `updateData` writes into the data
                        // (a group of single Radios keeps its selection there).
                        val state = remember { mutableStateOf<Map<String, Any>>(emptyMap()) }
                        val write = remember { { m: Map<String, Any> -> state.value = state.value + m } }
                        DynamicView(json = json, data = state.value + ("updateData" to write) + ("unrelated" to tick.value))
                    }
                }
            }
        }
        device.wait(Until.hasObject(By.res(if (group == "inputs") "sbd" else "sb")), 10_000)
        settle()
        return scenario
    }

    // ---------------------------------------------------------------- where

    private fun bounds(text: String): Rect? = device.findObject(By.text(text))?.visibleBounds

    /** Each control's rectangle on screen. Rows start at x 0 (no horizontal padding). */
    private fun frames(): Map<String, Rect> {
        val width = device.displayWidth
        fun row(a: String, b: String): Rect? {
            val ra = bounds(a) ?: return null
            val rb = bounds(b) ?: return null
            return Rect(0, minOf(ra.top, rb.top), width, maxOf(ra.bottom, rb.bottom))
        }
        val out = linkedMapOf<String, Rect>()
        if (group == "inputs") {
            for (id in controls) device.findObject(By.res(id))?.visibleBounds?.let { out[id] = it }
            return out
        }
        for (id in listOf("sw", "tg", "cb", "seg", "sl", "sb", "sbi")) {
            device.findObject(By.res(id))?.visibleBounds?.let { out[id] = it }
        }
        row("ra", "rb")?.let { out["rv"] = it }
        row("rg1", "rg2")?.let { out["rg"] = it }
        // The tab bar: its two items' labels, widened to the screen.
        val tabs = device.findObjects(By.text("tb")).map { it.visibleBounds }.maxByOrNull { it.top }
        val tas = device.findObjects(By.text("ta")).map { it.visibleBounds }.maxByOrNull { it.top }
        if (tabs != null && tas != null) {
            out["tab"] = Rect(0, minOf(tabs.top, tas.top) - (30 * density).toInt(), width, maxOf(tabs.bottom, tas.bottom))
        }
        return out
    }

    // ---------------------------------------------------------------- reading

    private class Reading(val pixels: Map<String, IntArray>, val sizes: Map<String, Pair<Int, Int>>, val nodes: Map<String, String>)

    private fun nodeReading(id: String): String {
        fun checkable(res: String): String {
            val o = device.findObject(By.res(res)) ?: return "no-node"
            val c = if (o.isCheckable) o else o.findObject(By.checkable(true))
            return "checked=${c?.isChecked}"
        }
        return when (id) {
            "sw", "tg", "cb" -> checkable(id)
            "seg" -> "sy.selected=${device.findObject(By.text("sy"))?.let { selectedUp(it) }}"
            "tab" -> "tb.selected=${device.findObjects(By.text("tb")).maxByOrNull { it.visibleBounds.top }?.let { selectedUp(it) }}"
            // A slider's value, from the node's range (a fresh node, not a cached one).
            "sln" -> rangeOf(id)
            "sb", "sbi", "sbv", "sbd", "tf", "tv" -> device.findObject(By.res(id))?.let { o ->
                "texts=" + (listOfNotNull(o.text) + o.findObjects(By.textStartsWith("")).mapNotNull { it.text }).distinct().joinToString("/")
            } ?: "no-node"
            else -> "-"
        }
    }

    private fun rangeOf(res: String): String {
        fun find(n: android.view.accessibility.AccessibilityNodeInfo?): android.view.accessibility.AccessibilityNodeInfo? {
            if (n == null) return null
            n.refresh()
            if (n.rangeInfo != null && (n.viewIdResourceName?.endsWith(res) == true || n.parent?.viewIdResourceName?.endsWith(res) == true)) return n
            for (i in 0 until n.childCount) find(n.getChild(i))?.let { return it }
            return null
        }
        val r = find(InstrumentationRegistry.getInstrumentation().uiAutomation.rootInActiveWindow)?.rangeInfo ?: return "no-range"
        return "value=${"%.3f".format(r.current)} of ${"%.0f".format(r.min)}..${"%.0f".format(r.max)}"
    }

    /** A tab's text sits inside the node that carries its selection. */
    private fun selectedUp(o: androidx.test.uiautomator.UiObject2): Boolean =
        o.isSelected || o.parent?.isSelected == true || o.parent?.parent?.isSelected == true

    private fun read(): Reading {
        settle()
        val shot: Bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        val pixels = linkedMapOf<String, IntArray>()
        val sizes = linkedMapOf<String, Pair<Int, Int>>()
        for ((id, r) in frames()) {
            val left = r.left.coerceIn(0, shot.width - 1)
            val top = r.top.coerceIn(0, shot.height - 1)
            val w = (r.right.coerceAtMost(shot.width) - left).coerceAtLeast(1)
            val h = (r.bottom.coerceAtMost(shot.height) - top).coerceAtLeast(1)
            val px = IntArray(w * h)
            shot.getPixels(px, 0, w, left, top, w, h)
            pixels[id] = px
            sizes[id] = w to h
        }
        return Reading(pixels, sizes, controls.associateWith { nodeReading(it) })
    }

    /** Pixels whose colour differs by more than 40 in a channel; -1 when the crops differ in size. */
    private fun diff(a: Reading, b: Reading, id: String): Int {
        val pa = a.pixels[id] ?: return -2
        val pb = b.pixels[id] ?: return -2
        if (a.sizes[id] != b.sizes[id]) return -1
        var n = 0
        for (i in pa.indices) {
            val x = pa[i]; val y = pb[i]
            if (Math.abs(((x shr 16) and 255) - ((y shr 16) and 255)) > 40 ||
                Math.abs(((x shr 8) and 255) - ((y shr 8) and 255)) > 40 ||
                Math.abs((x and 255) - (y and 255)) > 40) n++
        }
        return n
    }

    // ---------------------------------------------------------------- tapping

    private fun tapRadioBefore(text: String) {
        // The RadioButton is the row's first 48 dp; rows start at x 0.
        val r = bounds(text) ?: return
        device.click((24 * density).toInt(), r.centerY())
        settle()
    }

    private fun choose() {
        if (group == "inputs") { chooseInputs(); return }
        for (id in listOf("sw", "tg")) { device.findObject(By.res(id))?.click(); settle() }
        device.findObject(By.res("cb"))?.let { o -> (if (o.isCheckable) o else o.findObject(By.checkable(true)) ?: o).click() }
        settle()
        tapRadioBefore("rb")
        tapRadioBefore("rg2")
        device.findObject(By.text("sy"))?.click(); settle()
        device.findObjects(By.text("tb")).maxByOrNull { it.visibleBounds.top }?.click(); settle()
        device.findObject(By.res("sl"))?.visibleBounds?.let { r ->
            device.click(r.left + (r.width() * 0.8).toInt(), r.centerY())
        }
        settle()
        for (id in listOf("sb", "sbi")) {
            device.findObject(By.res(id))?.click()
            val option = device.wait(Until.findObjects(By.text("qq")), 5_000)
            option?.maxByOrNull { it.visibleBounds.top }?.click()
            settle()
            // A sheet that did not close is closed so the next control is reachable.
            if (device.findObjects(By.text("qq")).size > 0 && device.findObject(By.res(id))?.let { o ->
                    o.findObjects(By.text("qq")).isEmpty() } == true) {
                device.pressBack(); settle()
            }
        }
    }

    /** The sheets first, then the TextView and the TextField (a typed key each); back puts the keyboard away. */
    private fun chooseInputs() {
        // The slider with no value: a tap at 30% of its track.
        device.findObject(By.res("sln"))?.visibleBounds?.let { r -> device.click(r.left + (r.width() * 0.3).toInt(), r.centerY()) }
        settle()
        device.findObject(By.res("sbv"))?.click()
        device.wait(Until.findObjects(By.text("qq")), 5_000)?.maxByOrNull { it.visibleBounds.top }?.click()
        settle()
        device.findObject(By.res("sbd"))?.click()
        settle()
        // The compact style's calendar: a day's tap sets the date and closes the sheet.
        val texts = device.findObjects(By.textStartsWith("")).mapNotNull { it.text }.filter { it.isNotBlank() }
        println("STATICVALUED $group sbd_sheet texts=${texts.take(60)}")
        // A day cell reads "Saturday, January 3, 2026": the 3rd of whatever month it opened
        // on, then the sheet's confirm button (kjui_x7q_done).
        device.findObjects(By.textContains(" 3, 20")).maxByOrNull { it.visibleBounds.top }?.click()
        settle()
        device.findObject(By.res("kjui_x7q_done"))?.click()
        settle()
        if (device.findObject(By.res("tv")) == null || device.hasObject(By.textContains("2026年")) || device.hasObject(By.text("日付を選択"))) {
            println("STATICVALUED $group sbd_sheet still open — closed with back")
            device.pressBack(); settle()
        }
        for (id in listOf("tv", "tf")) {
            device.findObject(By.res(id))?.click()
            settle()
            val focused = device.findObject(By.focused(true))
            println("STATICVALUED $group focus $id focused=${focused?.resourceName} editable=${focused?.className}")
            device.executeShellCommand("input text x")
            settle()
        }
        // Back puts a shown keyboard away; with none shown it would close the screen.
        if (device.executeShellCommand("dumpsys input_method").contains("mInputShown=true")) {
            device.pressBack(); settle()
        }
    }

    private fun run(path: String): Map<String, Boolean> {
        val scenario = launch(path)
        val declared = read()
        choose()
        val chosen = read()
        tick.value = 1
        val afterUnrelated = read()
        val changed = linkedMapOf<String, Boolean>()
        for (id in controls) {
            val d = diff(declared, chosen, id)
            // A text input's caret can move pixels on its own: an input is judged by its text.
            changed[id] = if (group == "inputs") declared.nodes[id] != chosen.nodes[id] && chosen.nodes[id] !in setOf("no-node", "no-range") else d > 16
            // It started at the static value (a dropped seed starts elsewhere).
            declaredReading[id]?.let { want -> if (declared.nodes[id] != want) unseeded += "$path $id: ${declared.nodes[id]}" }
            // After the unrelated change the control still shows the choice.
            val kept = if (group == "inputs") afterUnrelated.nodes[id] == chosen.nodes[id] else diff(chosen, afterUnrelated, id) in 0..16
            if (changed.getValue(id) && !kept) lost += "$path $id"
            println("STATICVALUED $group $path $id tap_pixels=$d node ${declared.nodes[id]} -> ${chosen.nodes[id]}" +
                (if (changed.getValue(id)) "" else " TAP_DID_NOT_CHANGE_IT") +
                " | after_an_unrelated_change=${if (kept) "kept" else "LOST"} node ${afterUnrelated.nodes[id]}")
        }
        scenario.close()
        return changed
    }

    private val lost = mutableListOf<String>()
    private val unseeded = mutableListOf<String>()

    private fun check(group: String) {
        this.group = group
        lost.clear()
        unseeded.clear()
        val dynamic = run("dynamic")
        val codegen = run("codegen")
        // The dynamic path is the positive control for the taps themselves.
        for ((id, moved) in dynamic) assertTrue("$group dynamic $id: the tap reached and moved it (positive control)", moved)
        val stuck = codegen.filterValues { !it }.keys
        assertTrue("$group codegen: a tap moves every static-valued control; did not: $stuck", stuck.isEmpty())
        assertTrue("$group: a choice survives an unrelated change; lost: $lost", lost.isEmpty())
        assertTrue("$group: every control starts at its static value; did not: $unseeded", unseeded.isEmpty())
    }

    @Test
    fun a_static_value_is_where_a_control_starts_and_the_user_changes_it() = check("controls")

    @Test
    fun a_static_text_or_selection_is_where_an_input_starts_and_the_user_changes_it() = check("inputs")
}
