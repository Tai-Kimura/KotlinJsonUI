package com.kotlinjsonui.conformance

import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Every id inside a tap is found once — the Android half of the iOS
 * combined-tap check (SwiftJsonUI TapIdentifierOnceUITests). NOT part of the
 * conformance suite, and skipped unless requested:
 *
 *   adb shell am instrument -w \
 *     -e class com.kotlinjsonui.conformance.TapIdOnceProbeTest \
 *     -e tapIdOnceProbe 1 \
 *     com.kotlinjsonui.conformance.test/androidx.test.runner.AndroidJUnitRunner
 *
 * On iOS an id-less tap combined into one button took its child's id (found
 * twice). Android does not combine a container (its role is set, the node
 * keeps its children), so each shape here — through DynamicView (d_*) and
 * in the kjui codegen shape written by hand (g_*) — is asked the same: every
 * id By.res finds once, one clickable node per shape. `tio_dup` (one id on
 * two Texts) is the instrument's positive control and must count 2.
 */
@OptIn(ExperimentalComposeUiApi::class)
@RunWith(AndroidJUnit4::class)
class TapIdOnceProbeTest {

    @Before
    fun skipUnlessRequested() {
        val enabled = InstrumentationRegistry.getArguments().getString("tapIdOnceProbe") == "1"
        Assume.assumeTrue("set -e tapIdOnceProbe 1", enabled)
    }

    private fun label(id: String?, text: String) =
        """{"type": "Label", ${if (id != null) "\"id\": \"$id\", " else ""}"text": "$text"}"""

    private fun image(id: String) =
        """{"type": "Image", "id": "$id", "srcName": "conformance_sample", "width": 40, "height": 40}"""

    private fun tap(ownId: String?, children: List<String>) =
        """{"type": "View", ${if (ownId != null) "\"id\": \"$ownId\", " else ""}"orientation": "vertical", "onClick": "@{onTap}", "child": [${children.joinToString(",")}]}"""

    /** (wrapper, tap) — the same eight shapes as the iOS probe. */
    private val shapes = listOf(
        "one_label" to tap(null, listOf(label("d_one_label", "One label"))),
        "one_image" to tap(null, listOf(image("d_one_image"))),
        "one_plain" to tap(null, listOf(label(null, "One plain"))),
        "two" to tap(null, listOf(label("d_two_a", "Two a"), label("d_two_b", "Two b"))),
        "own_one" to tap("d_own_one", listOf(label("d_own_one_label", "Own one"))),
        "own_image" to tap("d_own_image", listOf(image("d_own_image_child"))),
        "own_plain" to tap("d_own_plain", listOf(label(null, "Own plain"))),
        "own_two" to tap("d_own_two", listOf(label("d_own_two_a", "Own two a"), label("d_own_two_b", "Own two b"))),
    )

    private val dynamicIds = listOf(
        "d_one_label", "d_one_image", "d_two_a", "d_two_b", "d_own_one", "d_own_one_label",
        "d_own_image", "d_own_image_child", "d_own_plain", "d_own_two", "d_own_two_a", "d_own_two_b",
    )
    private val codegenIds = listOf("g_one_label", "g_two_a", "g_two_b", "g_own_one", "g_own_one_label", "g_own_two", "g_own_two_a", "g_own_two_b")

    private fun collect(node: AccessibilityNodeInfo?, out: MutableList<AccessibilityNodeInfo>) {
        if (node == null) return
        out.add(node)
        for (i in 0 until node.childCount) collect(node.getChild(i), out)
    }

    private fun clickableUnder(root: AccessibilityNodeInfo?): Int {
        val all = mutableListOf<AccessibilityNodeInfo>()
        collect(root, all)
        return all.count { it.isClickable }
    }

    @Test
    fun everyIdInATapIsFoundOnce() {
        val layout = """{"type": "View", "orientation": "vertical", "child": [${
            shapes.joinToString(",") { (wrapper, tap) -> """{"type": "View", "id": "wrap_d_$wrapper", "child": [$tap]}""" }
        }]}"""
        val scenario = ActivityScenario.launch(FixtureHostActivity::class.java)
        scenario.onActivity { activity ->
            activity.setContent {
                Column(Modifier.padding(top = 80.dp).verticalScroll(rememberScrollState()).semantics { testTagsAsResourceId = true }) {
                    // The kjui codegen shapes: `.clickable(role = Role.Button)` on the
                    // container, `testTag` only where the layout has an id.
                    Column(Modifier.testTag("wrap_g_one_label")) {
                        Column(Modifier.clickable(role = Role.Button) { }) { Text("G one label", Modifier.testTag("g_one_label")) }
                    }
                    Column(Modifier.testTag("wrap_g_two")) {
                        Column(Modifier.clickable(role = Role.Button) { }) {
                            Text("G two a", Modifier.testTag("g_two_a")); Text("G two b", Modifier.testTag("g_two_b"))
                        }
                    }
                    Column(Modifier.testTag("wrap_g_own_one")) {
                        Column(Modifier.testTag("g_own_one").clickable(role = Role.Button) { }) { Text("G own one", Modifier.testTag("g_own_one_label")) }
                    }
                    Column(Modifier.testTag("wrap_g_own_two")) {
                        Column(Modifier.testTag("g_own_two").clickable(role = Role.Button) { }) {
                            Text("G own two a", Modifier.testTag("g_own_two_a")); Text("G own two b", Modifier.testTag("g_own_two_b"))
                        }
                    }
                    Text("dup one", Modifier.testTag("tio_dup"))
                    Text("dup two", Modifier.testTag("tio_dup"))
                    DynamicView(json = JsonParser.parseString(layout).asJsonObject, data = mapOf("onTap" to { }))
                }
            }
        }
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.wait(Until.hasObject(By.res("wrap_d_own_two")), 10_000)
        val counts = (dynamicIds + codegenIds).associateWith { device.findObjects(By.res(it)).size }
        for ((id, n) in counts) println("TAPIDONCE id=$id byRes=$n")
        val root = InstrumentationRegistry.getInstrumentation().uiAutomation.rootInActiveWindow
        val all = mutableListOf<AccessibilityNodeInfo>()
        collect(root, all)
        val wrappers = shapes.map { "wrap_d_${it.first}" } + listOf("wrap_g_one_label", "wrap_g_two", "wrap_g_own_one", "wrap_g_own_two")
        val clickable = wrappers.associateWith { w -> clickableUnder(all.firstOrNull { it.viewIdResourceName?.substringAfterLast('/') == w }) }
        for ((w, n) in clickable) println("TAPIDONCE wrapper=$w clickable=$n")
        val dup = device.findObjects(By.res("tio_dup")).size
        println("TAPIDONCE control tio_dup byRes=$dup")
        scenario.close()

        assertEquals("the positive control: one id on two Texts", 2, dup)
        for ((id, n) in counts) assertEquals("$id: found by By.res", 1, n)
        for ((w, n) in clickable) assertEquals("$w: clickable nodes", 1, n)
    }
}
