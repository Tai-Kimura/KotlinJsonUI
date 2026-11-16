package com.kotlinjsonui.conformance

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Rect
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
import com.kotlinjsonui.conformance.disabledcolours.DisabledColoursGeneratedView
import com.kotlinjsonui.conformance.disabledcolours.DisabledColoursViewModel
import com.kotlinjsonui.core.DynamicModeManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The disabled colours follow a bound `enabled` on the generated face (jsonui-cli ticket
 * 2026-10-23-bound-enabled-disabled-colors-three-faces): a View's disabledBackground had no
 * Compose path at all, and a Label's disabledFontColor showed only for a literal
 * `enabled: false`. NOT part of the conformance suite, and skipped unless requested:
 *
 *   adb shell am instrument -w \
 *     -e class com.kotlinjsonui.conformance.DisabledColoursProbeTest \
 *     -e disabledColoursProbe 1 \
 *     com.kotlinjsonui.conformance.test/androidx.test.runner.AndroidJUnitRunner
 *
 * The layout `kjui build` was given (disabledcolours/, pasted): a View with a bound background,
 * one with a fixed background, both with disabledBackground #999999, and a Label with a bound
 * fontColor and disabledFontColor #555555, all `enabled: @{on}`. Read from the screen: with `on`
 * true the Views paint #0000FF and the text #123456; set false through the view model, #999999
 * and #555555.
 */
@OptIn(ExperimentalComposeUiApi::class)
@RunWith(AndroidJUnit4::class)
class DisabledColoursProbeTest {

    @Before
    fun skipUnlessRequested() {
        val enabled = InstrumentationRegistry.getArguments().getString("disabledColoursProbe") == "1"
        Assume.assumeTrue("set -e disabledColoursProbe 1", enabled)
    }

    private val device get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private fun settle() {
        device.waitForIdle()
        Thread.sleep(600)
        device.waitForIdle()
    }

    private fun rect(id: String): Rect = device.findObject(By.res(id))?.visibleBounds ?: error("$id is not on the screen")

    private fun hex(c: Int) = String.format("#%02X%02X%02X", (c shr 16) and 255, (c shr 8) and 255, c and 255)

    /** How many pixels inside `r` are within 24 of `want` on each channel. */
    private fun count(shot: Bitmap, r: Rect, want: Int): Int {
        val w = r.width()
        val h = r.height()
        val px = IntArray(w * h)
        shot.getPixels(px, 0, w, r.left, r.top, w, h)
        return px.count { c ->
            listOf(16, 8, 0).all { s -> kotlin.math.abs(((c shr s) and 255) - ((want shr s) and 255)) <= 24 }
        }
    }

    private fun read(): Map<String, Any> {
        val shot: Bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        val view = rect("kg_dc_view")
        val fixed = rect("kg_dc_view_fixed")
        val label = rect("kg_dc_label")
        return mapOf(
            "view" to hex(shot.getPixel(view.centerX(), view.centerY())),
            "viewFixed" to hex(shot.getPixel(fixed.centerX(), fixed.centerY())),
            "labelBase" to count(shot, label, 0x123456),
            "labelDisabled" to count(shot, label, 0x555555),
        )
    }

    @Test
    fun aBoundEnabledTurnedFalseShowsTheDisabledColoursGenerated() {
        var vm: DisabledColoursViewModel? = null
        val scenario = ActivityScenario.launch(FixtureHostActivity::class.java)
        scenario.onActivity { activity ->
            DynamicModeManager.setDynamicModeEnabled(activity, false)
            val model = DisabledColoursViewModel(ApplicationProvider.getApplicationContext<Application>())
            vm = model
            activity.setContent {
                Column(Modifier.padding(top = 40.dp).semantics { testTagsAsResourceId = true }) {
                    val data by model.data.collectAsState()
                    DisabledColoursGeneratedView(data = data, viewModel = model)
                }
            }
        }
        device.wait(Until.hasObject(By.res("kg_dc_label")), 10_000)
        settle()
        val on = read()
        scenario.onActivity { vm!!.updateData(mapOf("on" to false)) }
        settle()
        val off = read()
        println("DCP on $on")
        println("DCP off $off")
        assertEquals("enabled: the bound background", "#0000FF", on["view"])
        assertEquals("enabled: the fixed background", "#0000FF", on["viewFixed"])
        assertTrue("enabled: the label in its base colour ($on)", (on["labelBase"] as Int) > 40)
        assertEquals("enabled: no disabled colour on the label", 0, on["labelDisabled"])
        assertEquals("disabled: the bound-background view's disabledBackground", "#999999", off["view"])
        assertEquals("disabled: the fixed-background view's disabledBackground", "#999999", off["viewFixed"])
        assertTrue("disabled: the label's disabledFontColor ($off)", (off["labelDisabled"] as Int) > 40)
        assertEquals("disabled: no base colour left on the label", 0, off["labelBase"])
        scenario.close()
    }
}
