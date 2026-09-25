package com.kotlinjsonui.conformance

import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
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
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.DynamicView
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.assertEquals
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * What `canTap` stops on device — NOT part of the conformance suite, and
 * skipped unless requested:
 *
 *   adb shell am instrument -w \
 *     -e class com.kotlinjsonui.conformance.CanTapGateProbeTest \
 *     -e canTapGateProbe 1 \
 *     com.kotlinjsonui.conformance.test/androidx.test.runner.AndroidJUnitRunner
 *
 * The line (attribute_definitions.json common.canTap): false, or a binding
 * resolving false, turns the onClick / onclick handler off, and nothing else
 * — a control's own operation (a check, a switch, a selection) is
 * `enabled`'s.
 *
 * Codegen: kjui_tools' Button under no canTap, `canTap: false`, and a binding
 * resolving false and true, pasted unchanged (ComposeBuilder), and a View
 * with onClick under the same four; and the same
 * Button as jsonui-cli 6ab928bf emitted it under `false` and the shut binding
 * (cg_btn_old_*), which called the handler. The two Rows around them are the
 * probe's, to keep every row on screen.
 *
 * Dynamic: Button, IconLabel, Radio, CheckBox, Switch, Toggle and Segment
 * with onClick under the same four gates. Each is tapped once; the handler
 * calls are counted, and the control's own operation is read back from the
 * accessibility tree (checked / selected) or from `updateData`. And every
 * node reads as enabled under every gate: `canTap` is not `enabled`.
 */
@OptIn(ExperimentalComposeUiApi::class)
@RunWith(AndroidJUnit4::class)
class CanTapGateProbeTest {

    @Before
    fun skipUnlessRequested() {
        val enabled = InstrumentationRegistry.getArguments().getString("canTapGateProbe") == "1"
        Assume.assumeTrue("set -e canTapGateProbe 1", enabled)
    }

    private val counts = ConcurrentHashMap<String, AtomicInteger>()
    private fun hit(k: String) { counts.getOrPut(k) { AtomicInteger() }.incrementAndGet() }
    private fun calls(k: String) = counts[k]?.get() ?: 0
    private val updates = ConcurrentHashMap<String, Any>()

    /** The codegen rows' data surface. */
    private inner class CodegenData {
        val gateClosed: Boolean? = false
        val gateOpen: Boolean? = true
        val onCgBtnNone: (() -> Unit)? = { hit("cg_btn_none") }
        val onCgBtnFalse: (() -> Unit)? = { hit("cg_btn_false") }
        val onCgBtnBClosed: (() -> Unit)? = { hit("cg_btn_bclosed") }
        val onCgBtnBOpen: (() -> Unit)? = { hit("cg_btn_bopen") }
        val onCgBtnOldFalse: (() -> Unit)? = { hit("cg_btn_old_false") }
        val onCgBtnOldBClosed: (() -> Unit)? = { hit("cg_btn_old_bclosed") }
        val onCgViewNone: (() -> Unit)? = { hit("cg_view_none") }
        val onCgViewFalse: (() -> Unit)? = { hit("cg_view_false") }
        val onCgViewBClosed: (() -> Unit)? = { hit("cg_view_bclosed") }
        val onCgViewBOpen: (() -> Unit)? = { hit("cg_view_bopen") }
        val onCgViewOldFalse: (() -> Unit)? = { hit("cg_view_old_false") }
        val onCgViewOldBClosed: (() -> Unit)? = { hit("cg_view_old_bclosed") }
    }

    @Composable
    private fun Codegen(data: CodegenData) {
        Row {
            Button(
                onClick = { data.onCgBtnNone?.invoke() },
                modifier = Modifier
                    .testTag("cg_btn_none")
                    .semantics { testTagsAsResourceId = true },
                shape = RoundedCornerShape(Configuration.Button.defaultCornerRadius.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                        containerColor = Configuration.Button.defaultBackgroundColor,
                        disabledContainerColor = Configuration.Button.defaultBackgroundColor.copy(alpha = 0.5f),
                        contentColor = Configuration.Button.defaultTextColor,
                        disabledContentColor = Configuration.Button.defaultTextColor.copy(alpha = 0.5f)
                    )
            ) {
                Text("cgBNone")
            }
            Button(
                onClick = { },
                modifier = Modifier
                    .testTag("cg_btn_false")
                    .semantics { testTagsAsResourceId = true },
                shape = RoundedCornerShape(Configuration.Button.defaultCornerRadius.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                        containerColor = Configuration.Button.defaultBackgroundColor,
                        disabledContainerColor = Configuration.Button.defaultBackgroundColor.copy(alpha = 0.5f),
                        contentColor = Configuration.Button.defaultTextColor,
                        disabledContentColor = Configuration.Button.defaultTextColor.copy(alpha = 0.5f)
                    )
            ) {
                Text("cgBFalse")
            }
            Button(
                onClick = { if ((data.gateClosed ?: false)) { data.onCgBtnBClosed?.invoke() } },
                modifier = Modifier
                    .testTag("cg_btn_bclosed")
                    .semantics { testTagsAsResourceId = true },
                shape = RoundedCornerShape(Configuration.Button.defaultCornerRadius.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                        containerColor = Configuration.Button.defaultBackgroundColor,
                        disabledContainerColor = Configuration.Button.defaultBackgroundColor.copy(alpha = 0.5f),
                        contentColor = Configuration.Button.defaultTextColor,
                        disabledContentColor = Configuration.Button.defaultTextColor.copy(alpha = 0.5f)
                    )
            ) {
                Text("cgBBClosed")
            }
            Button(
                onClick = { if ((data.gateOpen ?: false)) { data.onCgBtnBOpen?.invoke() } },
                modifier = Modifier
                    .testTag("cg_btn_bopen")
                    .semantics { testTagsAsResourceId = true },
                shape = RoundedCornerShape(Configuration.Button.defaultCornerRadius.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                        containerColor = Configuration.Button.defaultBackgroundColor,
                        disabledContainerColor = Configuration.Button.defaultBackgroundColor.copy(alpha = 0.5f),
                        contentColor = Configuration.Button.defaultTextColor,
                        disabledContentColor = Configuration.Button.defaultTextColor.copy(alpha = 0.5f)
                    )
            ) {
                Text("cgBBOpen")
            }
        }
        Row {
            Button(
                onClick = { data.onCgBtnOldFalse?.invoke() },
                modifier = Modifier
                    .testTag("cg_btn_old_false")
                    .semantics { testTagsAsResourceId = true },
                shape = RoundedCornerShape(Configuration.Button.defaultCornerRadius.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                        containerColor = Configuration.Button.defaultBackgroundColor,
                        disabledContainerColor = Configuration.Button.defaultBackgroundColor.copy(alpha = 0.5f),
                        contentColor = Configuration.Button.defaultTextColor,
                        disabledContentColor = Configuration.Button.defaultTextColor.copy(alpha = 0.5f)
                    )
            ) {
                Text("cgBOldFalse")
            }
            Button(
                onClick = { data.onCgBtnOldBClosed?.invoke() },
                modifier = Modifier
                    .testTag("cg_btn_old_bclosed")
                    .semantics { testTagsAsResourceId = true },
                shape = RoundedCornerShape(Configuration.Button.defaultCornerRadius.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                        containerColor = Configuration.Button.defaultBackgroundColor,
                        disabledContainerColor = Configuration.Button.defaultBackgroundColor.copy(alpha = 0.5f),
                        contentColor = Configuration.Button.defaultTextColor,
                        disabledContentColor = Configuration.Button.defaultTextColor.copy(alpha = 0.5f)
                    )
            ) {
                Text("cgBOldBClosed")
            }
        }
        Row {
            Box(
                modifier = Modifier
                    .testTag("cg_view_none")
                    .semantics { testTagsAsResourceId = true }
                    .requiredWidth(80.dp)
                    .requiredHeight(40.dp)
                    .background(Color(android.graphics.Color.parseColor("#3366CC")))
                    .clickable(role = Role.Button) { data.onCgViewNone?.invoke() }
            ) {
            }
            Box(
                modifier = Modifier
                    .testTag("cg_view_false")
                    .semantics { testTagsAsResourceId = true }
                    .requiredWidth(80.dp)
                    .requiredHeight(40.dp)
                    .background(Color(android.graphics.Color.parseColor("#3366CC")))
            ) {
            }
            Box(
                modifier = Modifier
                    .testTag("cg_view_bclosed")
                    .semantics { testTagsAsResourceId = true }
                    .requiredWidth(80.dp)
                    .requiredHeight(40.dp)
                    .background(Color(android.graphics.Color.parseColor("#3366CC")))
                    .then(if ((data.gateClosed ?: false)) Modifier.clickable(role = Role.Button) { data.onCgViewBClosed?.invoke() } else Modifier)
            ) {
            }
            Box(
                modifier = Modifier
                    .testTag("cg_view_bopen")
                    .semantics { testTagsAsResourceId = true }
                    .requiredWidth(80.dp)
                    .requiredHeight(40.dp)
                    .background(Color(android.graphics.Color.parseColor("#3366CC")))
                    .then(if ((data.gateOpen ?: false)) Modifier.clickable(role = Role.Button) { data.onCgViewBOpen?.invoke() } else Modifier)
            ) {
            }
        }
        Row {
            Box(
                modifier = Modifier
                    .testTag("cg_view_old_false")
                    .semantics { testTagsAsResourceId = true }
                    .requiredWidth(80.dp)
                    .requiredHeight(40.dp)
                    .background(Color(android.graphics.Color.parseColor("#3366CC")))
                    .clickable(enabled = false) { data.onCgViewOldFalse?.invoke() }
            ) {
            }
            Box(
                modifier = Modifier
                    .testTag("cg_view_old_bclosed")
                    .semantics { testTagsAsResourceId = true }
                    .requiredWidth(80.dp)
                    .requiredHeight(40.dp)
                    .background(Color(android.graphics.Color.parseColor("#3366CC")))
                    .clickable(enabled = (data.gateClosed ?: false), role = Role.Button) { data.onCgViewOldBClosed?.invoke() }
            ) {
            }
        }
    }

    private val gates = listOf("none", "false", "bclosed", "bopen")
    private val kinds = listOf("btn", "icl", "radio", "check", "switch", "toggle", "seg", "tab")

    private fun gate(g: String) = when (g) {
        "false" -> ", \"canTap\": false"
        "bclosed" -> ", \"canTap\": \"@{gate_closed}\""
        "bopen" -> ", \"canTap\": \"@{gate_open}\""
        else -> ""
    }

    private fun item(kind: String, g: String): String {
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
            // A TabView fills its parent (a Scaffold): the probe's View sizes it.
            else -> "{\"type\": \"View\", \"width\": 280, \"height\": 150, \"child\": [" +
                "{\"type\": \"TabView\", \"id\": \"$id\", \"tabs\": [{\"title\": \"ta_$g\"}, {\"title\": \"tb_$g\"}], $tap}]}"
        }
    }

    private fun collect(node: AccessibilityNodeInfo?, out: MutableMap<String, AccessibilityNodeInfo>) {
        if (node == null) return
        node.viewIdResourceName?.let { out.putIfAbsent(it.substringAfterLast('/'), node) }
        for (i in 0 until node.childCount) collect(node.getChild(i), out)
    }

    /** A checked descendant (or the node itself) — the control a tag sits on may wrap it. */
    private fun checked(node: AccessibilityNodeInfo?): Boolean? {
        if (node == null) return null
        if (node.isCheckable) return node.isChecked
        for (i in 0 until node.childCount) checked(node.getChild(i))?.let { return it }
        return null
    }

    @Test
    fun canTapStopsTheHandlerAndNothingElse() {
        val rows = kinds.map { kind -> "{\"type\": \"View\", \"orientation\": \"horizontal\", \"child\": [" +
            gates.joinToString(",") { item(kind, it) } + "]}" }
        val layout = "{\"type\": \"View\", \"orientation\": \"vertical\", \"child\": [${rows.joinToString(",")}]}"
        val data = mutableMapOf<String, Any>("gate_closed" to false, "gate_open" to true)
        for (kind in kinds) for (g in gates) {
            val id = "dyn_${kind}_$g"
            data["on_$id"] = { hit(id) }
        }
        data["updateData"] = { m: Map<String, Any> -> updates.putAll(m) }

        val scenario = ActivityScenario.launch(FixtureHostActivity::class.java)
        scenario.onActivity { activity ->
            activity.setContent {
                Column(Modifier.padding(top = 40.dp).semantics { testTagsAsResourceId = true }) {
                    Codegen(CodegenData())
                    DynamicView(json = JsonParser.parseString(layout).asJsonObject, data = data)
                }
            }
        }
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.wait(Until.hasObject(By.res("dyn_seg_bopen")), 10_000)

        val codegenIds = listOf("cg_btn_none", "cg_btn_false", "cg_btn_bclosed", "cg_btn_bopen", "cg_btn_old_false", "cg_btn_old_bclosed",
            "cg_view_none", "cg_view_false", "cg_view_bclosed", "cg_view_bopen", "cg_view_old_false", "cg_view_old_bclosed")
        val dynIds = listOf("btn", "icl", "check", "switch", "toggle").flatMap { k -> gates.map { "dyn_${k}_$it" } }
        for (id in codegenIds + dynIds) {
            val o = device.findObject(By.res(id))
            if (o == null) { println("CANTAP no node $id"); continue }
            o.click()
            device.waitForIdle()
        }
        // A Radio row is its glyph — its own selection (RadioButton onClick)
        // — and its text, where the row's clickable carries the layout's
        // onClick: each is tapped once, the glyph 24 dp in from the left and
        // the text 8 px in from the right.
        val density = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.density
        for (g in gates) {
            val o = device.findObject(By.res("dyn_radio_$g"))
            if (o == null) { println("CANTAP no node dyn_radio_$g"); continue }
            val b = o.visibleBounds
            device.click(b.left + (24 * density).toInt(), b.centerY())
            device.waitForIdle()
            device.click(b.right - 8, b.centerY())
            device.waitForIdle()
        }
        for (g in gates) {
            val o = device.findObject(By.text("y_$g"))
            if (o == null) { println("CANTAP no segment y_$g"); continue }
            o.click()
            device.waitForIdle()
        }
        // A TabView: its content (10 dp above the bar — the content is
        // some 70 dp tall here, and 40 dp above landed on the Segment row),
        // where the layout's onClick would be; then its second tab, its own
        // operation, read right after the tap: three of the four lost the
        // selection again before the end of the run, the parent (4edb7a1)
        // alike — recorded at the end, not asserted.
        val tabSwitched = mutableMapOf<String, Boolean?>()
        for (g in gates) {
            val t0 = device.findObject(By.res("dyn_tab_${g}_tab_0"))
            if (t0 == null) { println("CANTAP no tab $g"); continue }
            val b = t0.visibleBounds
            device.click(b.centerX(), b.top - (10 * density).toInt())
            device.waitForIdle()
            val t1 = device.findObject(By.res("dyn_tab_${g}_tab_1"))
            if (t1 == null) { println("CANTAP no tab_1 $g"); continue }
            t1.click()
            device.waitForIdle()
            tabSwitched[g] = device.findObject(By.res("dyn_tab_${g}_tab_1"))?.isSelected
        }
        Thread.sleep(500)

        val nodes = mutableMapOf<String, AccessibilityNodeInfo>()
        collect(InstrumentationRegistry.getInstrumentation().uiAutomation.rootInActiveWindow, nodes)
        for (id in codegenIds + kinds.flatMap { k -> gates.map { "dyn_${k}_$it" } }) {
            val n = nodes[id]
            println("CANTAP $id calls=${calls(id)} node=${n != null} enabled=${n?.isEnabled} checked=${checked(n)} clickable=${n?.isClickable}")
        }
        // A Segment's selection sits on the Tab, the text's parent.
        val segSelected = gates.associateWith { device.findObject(By.text("y_$it"))?.parent?.isSelected }
        for (g in gates) println("CANTAP seg_$g y_selected=${segSelected[g]}")
        for (g in gates) {
            val t = nodes["dyn_tab_${g}_tab_1"]
            println("CANTAP tab_$g switched=${tabSwitched[g]} still_selected_at_end=${t?.isSelected} enabled=${t?.isEnabled} calls=${calls("dyn_tab_$g")}")
        }
        println("CANTAP updates=${updates.keys.sorted().joinToString(",") { "$it=${updates[it]}" }}")
        scenario.close()

        val want = mapOf(
            "cg_btn_none" to 1, "cg_btn_false" to 0, "cg_btn_bclosed" to 0, "cg_btn_bopen" to 1,
            // the parent's shape (6ab928bf): canTap was not read
            "cg_btn_old_false" to 1, "cg_btn_old_bclosed" to 1,
            "cg_view_none" to 1, "cg_view_false" to 0, "cg_view_bclosed" to 0, "cg_view_bopen" to 1,
            "cg_view_old_false" to 0, "cg_view_old_bclosed" to 0,
        ) + listOf("btn", "icl", "radio", "toggle").flatMap { k ->
            listOf("dyn_${k}_none" to 1, "dyn_${k}_false" to 0, "dyn_${k}_bclosed" to 0, "dyn_${k}_bopen" to 1)
        }
        for ((id, n) in want.toSortedMap()) {
            println("CANTAP_WANT $id calls=${calls(id)} want=$n")
        }
        for ((id, n) in want) assertEquals("$id handler calls", n, calls(id))
        // A CheckBox, a Switch and a Segment take the tap for their own
        // operation, and the handler did not run with no gate set either:
        // their counts are recorded above, not asserted. Their own operation
        // happens whatever the gate says.
        for (g in gates) {
            assertEquals("radio $g is selected", "dyn_radio_$g", updates["selectedGrp_$g"])
            assertEquals("segment $g selects y", true, segSelected[g])
            assertEquals("check $g is checked", true, checked(nodes["dyn_check_$g"]))
            assertEquals("switch $g is on", true, checked(nodes["dyn_switch_$g"]))
            assertEquals("toggle $g is on", true, checked(nodes["dyn_toggle_$g"]))
            assertEquals("tab $g switched", true, tabSwitched[g])
        }

        // `canTap` is not `enabled`: under every gate the node reads as
        // enabled, as XCUITest reads the same nodes on iOS
        // (SwiftJsonUI ConformanceHost CanTapGateProbeUITests). Compose's
        // clickable marks a node disabled while it is not enabled, and it
        // was `clickable(enabled = enabled && canTap)` — the shape the
        // cg_view_old rows keep, read as disabled.
        val enabledIds = codegenIds.filter { !it.startsWith("cg_view_old_") } +
            listOf("btn", "icl", "radio", "check", "switch", "toggle", "seg").flatMap { k -> gates.map { "dyn_${k}_$it" } } +
            gates.map { "dyn_tab_${it}_tab_1" }
        for (id in enabledIds) assertEquals("$id reads as enabled", true, nodes[id]?.isEnabled)
        assertEquals("the parent's shape under false", false, nodes["cg_view_old_false"]?.isEnabled)
        assertEquals("the parent's shape under the shut binding", false, nodes["cg_view_old_bclosed"]?.isEnabled)
    }
}
