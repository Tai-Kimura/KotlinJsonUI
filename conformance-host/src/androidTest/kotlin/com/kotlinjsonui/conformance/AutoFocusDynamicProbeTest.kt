package com.kotlinjsonui.conformance

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.DynamicView
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `autoFocus` on the Dynamic runtime (jsonui-cli ticket
 * 2026-10-23-dynamic-renderers-do-not-implement-autofocus) — NOT part of the
 * conformance suite, and skipped unless requested:
 *
 *   adb shell am instrument -w \
 *     -e class com.kotlinjsonui.conformance.AutoFocusDynamicProbeTest \
 *     -e autoFocusDynamicProbe 1 \
 *     com.kotlinjsonui.conformance.test/androidx.test.runner.AndroidJUnitRunner
 *
 * The same five layouts as AutoFocusCodegenProbeTest, drawn by DynamicView:
 * a TextField and a TextView with autoFocus, with an id and without one, and
 * a TextField without it (the control). Opened, the field holds the focus
 * (an EditText node focused). The soft keyboard is printed, not asserted, for
 * the reason the codegen probe gives.
 */
@RunWith(AndroidJUnit4::class)
class AutoFocusDynamicProbeTest {
    @Before
    fun skipUnlessRequested() {
        Assume.assumeTrue("set -e autoFocusDynamicProbe 1",
            InstrumentationRegistry.getArguments().getString("autoFocusDynamicProbe") == "1")
    }

    private val layouts = linkedMapOf(
        "TfNoId" to """{"type":"TextField","width":"matchParent","height":48,"autoFocus":true}""",
        "TfId" to """{"type":"TextField","id":"email_field","width":"matchParent","height":48,"autoFocus":true}""",
        "TvNoId" to """{"type":"TextView","width":"matchParent","height":120,"autoFocus":true}""",
        "TvId" to """{"type":"TextView","id":"note_view","width":"matchParent","height":120,"autoFocus":true}""",
    )
    private val control = """{"type":"TextField","id":"plain_field","width":"matchParent","height":48}"""

    private fun opened(layout: String): Pair<Boolean, Boolean> {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val scenario = ActivityScenario.launch(FixtureHostActivity::class.java)
        scenario.onActivity {
            it.setContent { Column { DynamicView(json = JsonParser.parseString(layout).asJsonObject, data = emptyMap()) } }
        }
        val focused = device.wait(Until.findObject(By.focused(true).clazz("android.widget.EditText")), 5000) != null
        Thread.sleep(500)
        val ime = device.executeShellCommand("dumpsys input_method").contains("mInputShown=true")
        scenario.close()
        Thread.sleep(500)
        return focused to ime
    }

    @Test
    fun theFieldIsFocusedWhenTheScreenAppears() {
        for ((name, layout) in layouts) {
            val (focused, ime) = opened(layout)
            println("AF_DYN_PROBE $name focused=$focused ime=$ime")
            assertTrue("$name: an EditText holds the focus on appear", focused)
        }
    }

    @Test
    fun theControlTakesNoFocus() {
        val (focused, ime) = opened(control)
        println("AF_DYN_PROBE TfNone focused=$focused ime=$ime")
        assertFalse("a field without autoFocus is not focused on appear", focused)
    }
}
