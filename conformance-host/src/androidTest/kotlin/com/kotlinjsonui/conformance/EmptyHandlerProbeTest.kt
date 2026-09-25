package com.kotlinjsonui.conformance

import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.assertEquals
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * An empty or blank tap handler on device — NOT part of the conformance suite,
 * and skipped unless requested:
 *
 *   adb shell am instrument -w \
 *     -e class com.kotlinjsonui.conformance.EmptyHandlerProbeTest \
 *     -e emptyHandlerProbe 1 \
 *     com.kotlinjsonui.conformance.test/androidx.test.runner.AndroidJUnitRunner
 *
 * Each row is a parent View with a real onClick holding one Label whose
 * handler is the variant; the Label is tapped. A handler names a method
 * (TapAccessibility.clickHandlers; jsonui-cli shared/core/tap_accessibility.rb
 * `handler?`), so an empty or blank one is no click and the tap reaches the
 * row. Measured on an API 35 emulator, 2026-09-25, before (b35dc8a) and after:
 *
 *   child handler             before                              after
 *   "" / "   " / "@{}"         child clickable, row got 0 taps     not clickable, row 1
 *   onclick [""]              child clickable, row got 0 taps     not clickable, row 1
 *   onclick []                crash: Array must have size 1       not clickable, row 1
 *   onclick ["", "x"]         crash: Array must have size 1       x called once
 *   onclick ["x", "y"]        crash: Array must have size 1       x and y called
 *   onclick ["x"] (control)   x called                            x called
 *
 * The crash was ModifierBuilder.applyClickable reading onclick with
 * `asString`, which Gson refuses on an array unless it has one element — the
 * array spelling the SSoT declares took the whole screen down. Run one method
 * per `am instrument` when checking a runtime that may still crash: a crash
 * ends the instrumentation and the methods after it do not run.
 */
@OptIn(ExperimentalComposeUiApi::class)
@RunWith(AndroidJUnit4::class)
class EmptyHandlerProbeTest {

    @Before
    fun skipUnlessRequested() {
        val enabled = InstrumentationRegistry.getArguments().getString("emptyHandlerProbe") == "1"
        Assume.assumeTrue("set -e emptyHandlerProbe 1", enabled)
    }

    private val counts = ConcurrentHashMap<String, AtomicInteger>()
    private fun hit(k: String) { counts.getOrPut(k) { AtomicInteger() }.incrementAndGet() }
    private fun summary() = counts.keys.sorted().joinToString(",") { "$it=${counts[it]!!.get()}" }.ifEmpty { "none" }

    private fun row(key: String, handler: String) = """
      {"type": "View", "id": "par_$key", "orientation": "vertical", "onClick": "@{onTapRow_$key}", "child": [
        {"type": "Label", "id": "ch_$key", "text": "$key child text", "width": 240, "height": 40${if (handler.isEmpty()) "" else ", $handler"}}
      ]}"""

    private fun collect(node: AccessibilityNodeInfo?, out: MutableMap<String, AccessibilityNodeInfo>) {
        if (node == null) return
        node.viewIdResourceName?.let { out.putIfAbsent(it.substringAfterLast('/'), node) }
        for (i in 0 until node.childCount) collect(node.getChild(i), out)
    }

    private class Seen(val clickable: Map<String, Boolean?>, val taps: Map<String, Int>)

    private fun run(rows: List<Pair<String, String>>): Seen {
        val layout = """{"type": "View", "orientation": "vertical", "child": [${rows.joinToString(",") { row(it.first, it.second) }}]}"""
        val data = mutableMapOf<String, Any>()
        for ((k, _) in rows) {
            data["onTapRow_$k"] = { hit("${k}_par") }
        }
        data["onTapChild"] = { hit("child") }
        data["onTapOther"] = { hit("other") }
        val scenario = ActivityScenario.launch(FixtureHostActivity::class.java)
        scenario.onActivity { activity ->
            activity.setContent {
                Column(Modifier.padding(top = 80.dp).semantics { testTagsAsResourceId = true }) {
                    DynamicView(json = JsonParser.parseString(layout).asJsonObject, data = data)
                }
            }
        }
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.wait(Until.hasObject(By.res("par_${rows.first().first}")), 10_000)
        val nodes = mutableMapOf<String, AccessibilityNodeInfo>()
        collect(InstrumentationRegistry.getInstrumentation().uiAutomation.rootInActiveWindow, nodes)
        val clickable = mutableMapOf<String, Boolean?>()
        for ((k, h) in rows) {
            val p = nodes["par_$k"]; val c = nodes["ch_$k"]
            clickable[k] = c?.isClickable
            println("EMPTY $k [$h]: par node=${p != null} class=${p?.className} clickable=${p?.isClickable} | ch node=${c != null} class=${c?.className} clickable=${c?.isClickable} byRes=${device.findObjects(By.res("ch_$k")).size}")
        }
        for ((k, _) in rows) {
            val ch = device.findObject(By.res("ch_$k"))
            if (ch == null) { println("EMPTY tap $k: no child"); continue }
            ch.click()
            device.waitForIdle()
            Thread.sleep(300)
            println("EMPTY tap $k -> ${summary()}")
        }
        scenario.close()
        return Seen(clickable, counts.mapValues { it.value.get() })
    }

    /** The child took no click: it is not clickable, and its tap reached the row. */
    private fun assertNoClick(seen: Seen, key: String) {
        assertEquals("$key: child clickable", false, seen.clickable[key])
        assertEquals("$key: the row got the tap", 1, seen.taps["${key}_par"])
    }

    @Test
    fun scalars() {
        val seen = run(listOf(
            "c0" to "",
            "c1" to "\"onClick\": \"@{onTapChild}\"",
            "e1" to "\"onClick\": \"\"",
            "e2" to "\"onclick\": \"\"",
            "e5" to "\"onClick\": \"   \"",
            "e6" to "\"onclick\": \"   \"",
            "e7" to "\"onClick\": \"@{}\"",
            "e9" to "\"onClick\": \"\u3000\"",
        ))
        // The controls: no handler lets the tap through; a real one takes it.
        assertNoClick(seen, "c0")
        assertEquals("c1: child clickable", true, seen.clickable["c1"])
        assertEquals("c1: the child's handler ran", 1, seen.taps["child"])
        assertEquals("c1: the row did not", null, seen.taps["c1_par"])
        for (key in listOf("e1", "e2", "e5", "e6", "e7", "e9")) assertNoClick(seen, key)
    }

    @Test fun emptyArray() = assertNoClick(run(listOf("e3" to "\"onclick\": []")), "e3")

    @Test fun blankElement() = assertNoClick(run(listOf("e4" to "\"onclick\": [\"\"]")), "e4")

    @Test
    fun blankBesideReal() {
        val seen = run(listOf("e8" to "\"onclick\": [\"\", \"onTapChild\"]"))
        assertEquals(mapOf("child" to 1), seen.taps)
    }

    @Test
    fun twoHandlers() {
        val seen = run(listOf("a2" to "\"onclick\": [\"onTapChild\", \"onTapOther\"]"))
        assertEquals(mapOf("child" to 1, "other" to 1), seen.taps)
    }

    @Test
    fun oneElementArray() {
        val seen = run(listOf("a1" to "\"onclick\": [\"onTapChild\"]"))
        assertEquals(mapOf("child" to 1), seen.taps)
    }
}
