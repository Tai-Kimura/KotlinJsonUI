package com.kotlinjsonui.conformance

import android.app.Application
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.google.gson.JsonParser
import com.kotlinjsonui.conformance.staticvalued.BoundSelectsGeneratedView
import com.kotlinjsonui.conformance.staticvalued.BoundSelectsViewModel
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.dynamic.DynamicView
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A SelectBox bound to the data — by selectedItem, selectedValue, selectedDate
 * or selectedIndex, every one declared two-way — shows the model's value, and
 * the user's pick reaches the model (ticket
 * selectbox-selected-item-binding-is-read-once). NOT part of the conformance
 * suite, and skipped unless requested (`-e boundSelectProbe 1`).
 *
 * Two paths: what `kjui build` emits (staticvalued/BoundSelects*, pasted — the
 * model is its generated ViewModel, moved through updateData) and DynamicView
 * (the model is the data map; `updateData` writes into it). Each box is read by
 * its node's text: at the declared values, after the model moves every value
 * and back, and after the user picks qq / the 3rd — then the model's own value.
 */
@OptIn(ExperimentalComposeUiApi::class)
@RunWith(AndroidJUnit4::class)
class BoundSelectBoxProbeTest {

    @Before
    fun skipUnlessRequested() {
        val enabled = InstrumentationRegistry.getArguments().getString("boundSelectProbe") == "1"
        Assume.assumeTrue("set -e boundSelectProbe 1", enabled)
    }

    private val layout = """
        {"type": "View", "orientation": "vertical", "spacing": 8, "width": "matchParent",
         "data": [
           {"name": "sbiSel", "class": "String", "defaultValue": "pp"},
           {"name": "sbvSel", "class": "String", "defaultValue": "pp"},
           {"name": "sbdDate", "class": "String", "defaultValue": "2026-01-02"},
           {"name": "sbIdx", "class": "Int", "defaultValue": 0}
         ],
         "child": [
          {"type": "SelectBox", "id": "sbi", "height": 40, "items": ["pp", "qq"], "selectedItem": "@{sbiSel}"},
          {"type": "SelectBox", "id": "sbv", "height": 40, "items": ["pp", "qq"], "selectedValue": "@{sbvSel}"},
          {"type": "SelectBox", "id": "sbd", "height": 40, "selectItemType": "Date", "datePickerMode": "date", "dateStringFormat": "yyyy-MM-dd", "selectedDate": "@{sbdDate}"},
          {"type": "SelectBox", "id": "sb", "height": 40, "items": ["pp", "qq"], "selectedIndex": "@{sbIdx}"}
         ]}
    """.trimIndent()

    private val ids = listOf("sbi", "sbv", "sbd", "sb")
    private val declared = mapOf("sbiSel" to "pp", "sbvSel" to "pp", "sbdDate" to "2026-01-02", "sbIdx" to 0)
    private val moved = mapOf("sbiSel" to "qq", "sbvSel" to "qq", "sbdDate" to "2026-01-03", "sbIdx" to 1)
    private val shownDeclared = mapOf("sbi" to "pp", "sbv" to "pp", "sbd" to "2026-01-02", "sb" to "pp")
    private val shownMoved = mapOf("sbi" to "qq", "sbv" to "qq", "sbd" to "2026-01-03", "sb" to "qq")

    private val device get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private fun settle() {
        device.waitForIdle()
        Thread.sleep(600)
        device.waitForIdle()
    }

    private fun shown(): Map<String, String> = ids.associateWith { id ->
        device.findObject(By.res(id))?.let { o ->
            (listOfNotNull(o.text) + o.findObjects(By.textStartsWith("")).mapNotNull { it.text }).distinct().joinToString("/")
        } ?: "no-node"
    }

    private fun pick() {
        for (id in listOf("sbi", "sbv", "sb")) {
            device.findObject(By.res(id))?.click()
            device.wait(Until.findObjects(By.text("qq")), 5_000)?.maxByOrNull { it.visibleBounds.top }?.click()
            settle()
        }
        device.findObject(By.res("sbd"))?.click()
        settle()
        device.findObjects(By.textContains(" 3, 20")).maxByOrNull { it.visibleBounds.top }?.click()
        settle()
        device.findObject(By.res("kjui_x7q_done"))?.click()
        settle()
    }

    private class Model(val move: (Map<String, Any>) -> Unit, val read: () -> Map<String, Any?>)

    private fun launch(path: String): Pair<ActivityScenario<FixtureHostActivity>, Model> {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = BoundSelectsViewModel(app)
        val state = mutableStateOf<Map<String, Any>>(declared)
        val scenario = ActivityScenario.launch(FixtureHostActivity::class.java)
        scenario.onActivity { activity ->
            DynamicModeManager.setDynamicModeEnabled(activity, false)
            val json = JsonParser.parseString(layout).asJsonObject
            activity.setContent {
                Column(Modifier.padding(top = 40.dp).semantics { testTagsAsResourceId = true }) {
                    if (path == "codegen") {
                        val data by vm.data.collectAsState()
                        BoundSelectsGeneratedView(data = data, viewModel = vm)
                    } else {
                        val write = remember { { m: Map<String, Any> -> state.value = state.value + m } }
                        DynamicView(json = json, data = state.value + ("updateData" to write))
                    }
                }
            }
        }
        device.wait(Until.hasObject(By.res("sb")), 10_000)
        settle()
        val model = if (path == "codegen") {
            Model({ vm.updateData(it) }, { vm.data.value.let { d -> mapOf("sbiSel" to d.sbiSel, "sbvSel" to d.sbvSel, "sbdDate" to d.sbdDate, "sbIdx" to d.sbIdx) } })
        } else {
            Model({ m -> state.value = state.value + m }, { state.value.filterKeys { it in declared } })
        }
        return scenario to model
    }

    private fun run(path: String): List<String> {
        val wrong = mutableListOf<String>()
        val (scenario, model) = launch(path)
        fun check(stage: String, want: Map<String, String>) {
            settle()
            val got = shown()
            println("BOUNDSELECT $path $stage shown=$got model=${model.read()}")
            for (id in ids) if (got[id] != want[id]) wrong += "$path $stage $id: ${got[id]} (want ${want[id]})"
        }
        check("declared", shownDeclared)
        InstrumentationRegistry.getInstrumentation().runOnMainSync { model.move(moved) }
        check("the model moved", shownMoved)
        InstrumentationRegistry.getInstrumentation().runOnMainSync { model.move(declared) }
        check("the model moved back", shownDeclared)
        pick()
        check("the user picked", shownMoved)
        val after = model.read()
        println("BOUNDSELECT $path the model after the pick: $after")
        for ((key, want) in moved) if (after[key] != want) wrong += "$path the pick did not reach the model: $key=${after[key]} (want $want)"
        scenario.close()
        return wrong
    }

    @Test
    fun a_bound_selectbox_shows_the_model_and_writes_the_pick() {
        val wrong = run("dynamic") + run("codegen")
        assertTrue("bound SelectBoxes: $wrong", wrong.isEmpty())
    }
}
