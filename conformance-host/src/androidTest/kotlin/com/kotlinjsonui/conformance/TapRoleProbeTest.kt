package com.kotlinjsonui.conformance

import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
import com.kotlinjsonui.dynamic.DynamicView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * What the accessibility tree says of a tappable — NOT part of the
 * conformance suite, and skipped unless requested:
 *
 *   adb shell am instrument -w \
 *     -e class com.kotlinjsonui.conformance.TapRoleProbeTest \
 *     -e tapRoleProbe 1 \
 *     com.kotlinjsonui.conformance.test/androidx.test.runner.AndroidJUnitRunner
 *
 * Prints one TAPROLE line per node (className, roleDescription, clickable,
 * text / content-desc, and how many nodes By.res finds under that id), then
 * taps the Switch inside a tappable row and prints whether it toggled. The
 * DynamicView rows go through the library as built; the gen_* rows are the
 * kjui codegen shape written by hand, with and without `role = Role.Button`.
 * Measured before and after the tap-button change (jsonui-cli
 * shared/core/tap_accessibility.rb), API 35 emulator, 2026-09-25:
 *
 *   node             before                      after
 *   dyn_image        android.widget.ImageView    android.widget.Button
 *   dyn_label        android.widget.TextView     android.widget.Button
 *   dyn_row          android.view.View           android.view.View
 *   gen_row_role     android.view.View           android.view.View   (role written explicitly)
 *   row_title / sub  By.res 1 each               By.res 1 each
 *   dyn_sw           its own node, toggles       its own node, toggles
 *
 * `role = Role.Button` reaches the node's class only on a node with no
 * children: a tappable container with children keeps android.view.View —
 * the hand-written codegen shape with the role spelled out does the same, so
 * it is Compose, not the rule. The children stay separate nodes under their
 * resource-ids (a test still finds them), and a Switch inside a tappable row
 * stays its own operable node. roleDescription was null on every node. What
 * TalkBack speaks was not measured.
 *
 * The table is asserted, not only printed. The two containers (dyn_row,
 * gen_row_role) are pinned to android.view.View AS MEASURED — Compose's
 * behaviour, not the rule's intent: the rule still hands them
 * `role = Role.Button` (4f, 2026-09-25: kept — correct API, harmless, one
 * rule). If a Compose update starts putting the class on a node with
 * children, these rows go red: then Android containers ARE announced, and
 * the docs that say they are not must change with this table.
 *
 * canTap (common.canTap, the Compose tap gate): dyn_gate (onClick, canTap:
 * false) was enabled — the gate ignored — on 2cc81f3, and is disabled
 * after; dyn_image_nogate (onClick, no canTap) taps before and after.
 *
 * The content starts 80dp down: at y=0 the first row sat under the status
 * bar, its title was not visible to the user, and UiAutomator's By.res did
 * not find it (the node was in the tree: visibleToUser=false, bounds
 * [0,0][170,33]) — the probe's layout, not a generator's testTag.
 */
@OptIn(ExperimentalComposeUiApi::class)
@RunWith(AndroidJUnit4::class)
class TapRoleProbeTest {

    private val layout = """
    {"type": "View", "orientation": "vertical", "child": [
      {"type": "Image", "id": "dyn_image", "srcName": "conformance_sample", "width": 40, "height": 40, "canTap": true, "onClick": "@{onProbe}"},
      {"type": "Label", "id": "dyn_label", "text": "Label tap", "onClick": "@{onProbe}"},
      {"type": "View", "id": "dyn_row", "orientation": "vertical", "onClick": "@{onProbe}", "child": [
        {"type": "Label", "id": "row_title", "text": "Row title"},
        {"type": "Label", "id": "row_sub", "text": "Row sub"}
      ]},
      {"type": "Image", "id": "dyn_image_nogate", "srcName": "conformance_sample", "width": 40, "height": 40, "onClick": "@{onProbe}"},
      {"type": "View", "id": "dyn_gate", "canTap": false, "onClick": "@{onProbe}", "child": [
        {"type": "Label", "id": "gate_title", "text": "Gated shut"}
      ]},
      {"type": "View", "id": "dyn_switch_row", "orientation": "horizontal", "onClick": "@{onProbe}", "child": [
        {"type": "Label", "id": "sw_title", "text": "Switch row"},
        {"type": "Switch", "id": "dyn_sw"}
      ]}
    ]}
    """

    @Before
    fun skipUnlessRequested() {
        val enabled = InstrumentationRegistry.getArguments().getString("tapRoleProbe") == "1"
        Assume.assumeTrue("set -e tapRoleProbe 1 to run the tap-role probe", enabled)
    }

    private fun collect(node: AccessibilityNodeInfo?, out: MutableMap<String, AccessibilityNodeInfo>) {
        if (node == null) return
        node.viewIdResourceName?.let { out.putIfAbsent(it.substringAfterLast('/'), node) }
        for (i in 0 until node.childCount) collect(node.getChild(i), out)
    }

    @Test
    fun whatTheTreeSaysOfEachTap() {
        val scenario = ActivityScenario.launch(FixtureHostActivity::class.java)
        scenario.onActivity { activity ->
            activity.setContent {
                Column(Modifier.padding(top = 80.dp).semantics { testTagsAsResourceId = true }) {
                    Column(Modifier.testTag("gen_row_norole").clickable { }) {
                        Text("Gen plain title", Modifier.testTag("gen_plain_title"))
                        Text("Gen plain sub", Modifier.testTag("gen_plain_sub"))
                    }
                    Column(Modifier.testTag("gen_row_role").clickable(role = Role.Button) { }) {
                        Text("Gen role title", Modifier.testTag("gen_role_title"))
                        Text("Gen role sub", Modifier.testTag("gen_role_sub"))
                    }
                    DynamicView(
                        json = JsonParser.parseString(layout).asJsonObject,
                        data = mapOf("onProbe" to { })
                    )
                }
            }
        }
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.wait(Until.hasObject(By.res("dyn_row")), 10_000)
        val view = "android.view.View"
        val text = "android.widget.TextView"
        val button = "android.widget.Button"
        val expected = linkedMapOf(
            "gen_row_norole" to view, "gen_plain_title" to text, "gen_plain_sub" to text,
            "gen_row_role" to view,            // role written, children: Compose keeps View
            "gen_role_title" to text, "gen_role_sub" to text,
            "dyn_image" to button, "dyn_label" to button,
            "dyn_row" to view,                 // the rule's `combine`: Compose keeps View
            "row_title" to text, "row_sub" to text,
            "dyn_image_nogate" to button,      // no canTap: no gate, the handler alone taps
            "dyn_gate" to view, "gate_title" to text,   // canTap: false: gated shut, no role
            "dyn_switch_row" to view, "sw_title" to text, "dyn_sw" to view,
        )
        val ids = expected.keys.toList()
        val nodes = mutableMapOf<String, AccessibilityNodeInfo>()
        collect(InstrumentationRegistry.getInstrumentation().uiAutomation.rootInActiveWindow, nodes)
        for (id in ids) {
            val n = nodes[id]
            val role = n?.extras?.getCharSequence("AccessibilityNodeInfo.roleDescription")
            println("TAPROLE id=$id enabled=${n?.isEnabled} byRes=${device.findObjects(By.res(id)).size} node=${n != null} " +
                "class=${n?.className} role=$role clickable=${n?.isClickable} " +
                "checkable=${n?.isCheckable} checked=${n?.isChecked} text='${n?.text}' desc='${n?.contentDescription}' " +
                "visibleToUser=${n?.isVisibleToUser} bounds=${n?.let { val r = android.graphics.Rect(); it.getBoundsInScreen(r); r.toShortString() }}")
        }
        val sw = device.findObject(By.res("dyn_sw"))
        val before = sw?.isChecked
        sw?.click()
        device.waitForIdle()
        val after = device.findObject(By.res("dyn_sw"))?.isChecked
        println("TAPROLE switch_toggle found=${sw != null} before=$before after=$after")
        for (id in ids) {
            assertEquals("$id: found by By.res", 1, device.findObjects(By.res(id)).size)
            assertEquals("$id: class", expected[id], nodes[id]?.className?.toString())
        }
        assertTrue("dyn_sw is checkable", nodes["dyn_sw"]?.isCheckable == true)
        // common.canTap: false is the Compose tap gate (kjui emits
        // clickable(enabled = false)); the Dynamic runtime reads it too.
        assertEquals("dyn_gate is gated shut", false, nodes["dyn_gate"]?.isEnabled)
        assertEquals("dyn_image_nogate taps", true, nodes["dyn_image_nogate"]?.isEnabled)
        assertEquals("the Switch inside a tappable row toggles on its own", false to true, before to after)
        scenario.close()
    }
}
