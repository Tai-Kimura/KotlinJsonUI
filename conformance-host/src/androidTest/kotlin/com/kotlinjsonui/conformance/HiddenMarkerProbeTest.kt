package com.kotlinjsonui.conformance

import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.setContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewRootForTest
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.DynamicView
import org.junit.Assert.assertEquals
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * What a test driver can read of each way a view is (not) drawn — NOT part of
 * the conformance suite, and skipped unless requested:
 *
 *   adb shell am instrument -w \
 *     -e class com.kotlinjsonui.conformance.HiddenMarkerProbeTest \
 *     -e hiddenProbe 1 \
 *     com.kotlinjsonui.conformance.test/androidx.test.runner.AndroidJUnitRunner
 *
 * jsonui-test-runner's `hidden` asks "not drawn, wherever it is". UiAutomator
 * cannot answer it: Compose leaves an off-screen node, and every node of a
 * window under a dialog, out of the accessibility tree. The unmerged semantics
 * tree has them all, but without a mark it cannot tell visibility "invisible"
 * (alpha 0) from `opacity: 0` (alpha 0, drawn). VisibilityWrapper sets
 * JsonUIHiddenKey on what it does not draw; this pins, for every shape and in
 * three states (as laid out, scrolled to the end, under a dialog), that a view
 * is hidden exactly when no node has its id or its node or an ancestor carries
 * the key — measured on API 35, 2026-10-15.
 */
@OptIn(ExperimentalComposeUiApi::class)
@RunWith(AndroidJUnit4::class)
class HiddenMarkerProbeTest {
    private val LAYOUT = """{"type": "ScrollView", "id": "hp_scroll", "width": "matchParent", "height": "matchParent", "child": [{"type": "View", "id": "hp_content", "orientation": "vertical", "width": "matchParent", "height": "wrapContent", "child": [{"type": "Label", "id": "hp_plain", "text": "plain", "height": 30}, {"type": "Label", "id": "hp_opacity0", "text": "opacity zero", "height": 30, "opacity": 0}, {"type": "Button", "id": "hp_disabled", "text": "disabled", "height": 40, "enabled": false}, {"type": "Label", "id": "hp_invisible", "text": "invisible", "height": 30, "visibility": "invisible"}, {"type": "Label", "id": "hp_gone", "text": "gone", "height": 30, "visibility": "gone"}, {"type": "Label", "id": "hp_hidden", "text": "hidden", "height": 30, "hidden": true}, {"type": "Label", "id": "hp_bind_invisible", "text": "bound invisible", "height": 30, "visibility": "@{bindVis}"}, {"type": "Label", "id": "hp_bind_hidden", "text": "bound hidden", "height": 30, "hidden": "@{bindHidden}"}, {"type": "View", "id": "hp_parent_invisible", "visibility": "invisible", "height": 40, "child": [{"type": "Label", "id": "hp_child_of_invisible", "text": "child of invisible", "height": 30}]}, {"type": "View", "id": "hp_parent_gone", "visibility": "gone", "height": 40, "child": [{"type": "Label", "id": "hp_child_of_gone", "text": "child of gone", "height": 30}]}, {"type": "View", "id": "hp_spacer", "width": "matchParent", "height": 3000, "background": "#EEEEEE"}, {"type": "Label", "id": "hp_below", "text": "below the fold", "height": 30}]}]}"""
    private val ids = listOf("hp_plain", "hp_opacity0", "hp_disabled", "hp_invisible", "hp_gone", "hp_hidden",
        "hp_bind_invisible", "hp_bind_hidden", "hp_parent_invisible", "hp_child_of_invisible",
        "hp_parent_gone", "hp_child_of_gone", "hp_below", "hp_absent")
    private val dialog = mutableStateOf(false)
    private val expectedHidden = setOf("hp_invisible", "hp_gone", "hp_hidden", "hp_bind_invisible", "hp_bind_hidden",
        "hp_parent_invisible", "hp_child_of_invisible", "hp_parent_gone", "hp_child_of_gone", "hp_absent")

    @Before
    fun skipUnlessRequested() {
        Assume.assumeTrue(InstrumentationRegistry.getArguments().getString("hiddenProbe") == "1")
    }

    private fun roots(v: View, out: MutableList<ViewRootForTest>) {
        if (v is ViewRootForTest) out.add(v)
        if (v is ViewGroup) for (i in 0 until v.childCount) roots(v.getChildAt(i), out)
    }

    private fun collect(n: SemanticsNode, out: MutableMap<String, SemanticsNode>) {
        n.config.getOrNull(SemanticsProperties.TestTag)?.let { out.putIfAbsent(it, n) }
        for (c in n.children) collect(c, out)
    }

    private fun report(scenario: ActivityScenario<FixtureHostActivity>, device: UiDevice, phase: String) {
        device.waitForIdle(); Thread.sleep(1000)
        val tags = mutableMapOf<String, SemanticsNode>()
        val rootCount = intArrayOf(0)
        scenario.onActivity { a ->
            val rs = mutableListOf<ViewRootForTest>()
            roots(a.window.decorView, rs)
            rootCount[0] = rs.size
            for (r in rs) collect(r.semanticsOwner.unmergedRootSemanticsNode, tags)
        }
        for (id in ids) {
            val ui = device.findObject(By.res(id))
            val n = tags[id]
            val marked = generateSequence(n) { it.parent }.any { node -> node.config.any { it.key.name == "JsonUIHidden" && it.value == true } }
            val hidden = n == null || marked
            println("HIDDEN_MARKER phase=$phase id=$id uiautomator=${ui != null} semantics=${n != null} marked=$marked hidden=$hidden roots=${rootCount[0]}")
            assertEquals("$id ($phase): hidden", id in expectedHidden, hidden)
        }
    }

    @Test
    fun hiddenIsExactlyAbsentOrMarked() {
        val scenario = ActivityScenario.launch(FixtureHostActivity::class.java)
        scenario.onActivity { activity ->
            activity.setContent {
                Box(Modifier.semantics { testTagsAsResourceId = true }) {
                    DynamicView(json = JsonParser.parseString(LAYOUT).asJsonObject,
                        data = mapOf("bindVis" to "invisible", "bindHidden" to true))
                    if (dialog.value) {
                        AlertDialog(onDismissRequest = { dialog.value = false },
                            confirmButton = { TextButton(onClick = { dialog.value = false }) { Text("OK") } },
                            text = { Text("dialog") })
                    }
                }
            }
        }
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.wait(Until.hasObject(By.res("hp_plain")), 10_000)
        report(scenario, device, "none")
        device.findObject(By.res("hp_scroll"))?.scroll(androidx.test.uiautomator.Direction.DOWN, 50f)
        report(scenario, device, "scrolled")
        device.findObject(By.res("hp_scroll"))?.scroll(androidx.test.uiautomator.Direction.UP, 50f)
        scenario.onActivity { dialog.value = true }
        device.wait(Until.hasObject(By.text("dialog")), 10_000)
        report(scenario, device, "dialog")
        scenario.close()
    }
}
