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
import com.kotlinjsonui.conformance.valuechange.SegmentValueChangeGeneratedView
import com.kotlinjsonui.conformance.valuechange.SegmentValueChangeViewModel
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.dynamic.DynamicView
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Collections

/**
 * A Segment's `valueChange` — the selector spelling, its own attribute in the
 * definitions with no platform named — is the report where no onValueChange is
 * declared, and stands down beside one: called from the user's choice, after
 * the write and before onClick; not for the view model's writes (4f's ruling
 * on control-onclick-is-called-differently-on-every-path). NOT part of the
 * conformance suite, and skipped unless requested
 * (`-e segmentValueChangeProbe 1`).
 *
 * Two paths: what `kjui build` emits (valuechange/SegmentValueChange*, pasted —
 * the model is its generated ViewModel) and DynamicView (the model is the data
 * map; `updateData` writes into it). Four Segments: segv… carry `valueChange`
 * alone, segw… an onValueChange beside a `valueChange` (…X, never to be
 * called); …u over a value of their own, …b bound. Every handler logs its name
 * in order; the test picks each Segment's second tab, reads each one's own
 * order, then moves the model's bound values and expects no call.
 */
@OptIn(ExperimentalComposeUiApi::class)
@RunWith(AndroidJUnit4::class)
class SegmentValueChangeProbeTest {

    @Before
    fun skipUnlessRequested() {
        val enabled = InstrumentationRegistry.getArguments().getString("segmentValueChangeProbe") == "1"
        Assume.assumeTrue("set -e segmentValueChangeProbe 1", enabled)
    }

    private val layout = """
        {"type":"View","orientation":"vertical","spacing":8,"width":"matchParent",
         "data":[{"name":"segvbIdx","class":"Int","defaultValue":0},{"name":"segwbIdx","class":"Int","defaultValue":0}],
         "child":[
          {"type":"Segment","items":["p1","p2"],"selectedIndex":0,"id":"segvu","onClick":"@{onSegvuC}","valueChange":"onSegvuV"},
          {"type":"Segment","items":["q1","q2"],"selectedIndex":"@{segvbIdx}","id":"segvb","onClick":"@{onSegvbC}","valueChange":"onSegvbV"},
          {"type":"Segment","items":["r1","r2"],"selectedIndex":0,"id":"segwu","onClick":"@{onSegwuC}","onValueChange":"@{onSegwuV}","valueChange":"onSegwuX"},
          {"type":"Segment","items":["s1","s2"],"selectedIndex":"@{segwbIdx}","id":"segwb","onClick":"@{onSegwbC}","onValueChange":"@{onSegwbV}","valueChange":"onSegwbX"}
         ]}
    """.trimIndent()

    private val controls = listOf("segvu", "segvb", "segwu", "segwb")
    private val handlers = controls.flatMap { c ->
        val base = c.replaceFirstChar { it.uppercase() }
        listOf("on${base}V", "on${base}C", "on${base}X")
    }

    private val device get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private fun settle() {
        device.waitForIdle()
        Thread.sleep(600)
        device.waitForIdle()
    }

    private class Model(val move: (Map<String, Any>) -> Unit)

    private fun launch(path: String, log: MutableList<String>): Pair<ActivityScenario<FixtureHostActivity>, Model> {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = SegmentValueChangeViewModel(app)
        val calls: Map<String, Any> = handlers.associateWith { name -> { log.add(name); Unit } }
        vm.updateData(calls)
        val state = mutableStateOf<Map<String, Any>>(calls + mapOf("segvbIdx" to 0, "segwbIdx" to 0))
        val scenario = ActivityScenario.launch(FixtureHostActivity::class.java)
        scenario.onActivity { activity ->
            DynamicModeManager.setDynamicModeEnabled(activity, false)
            val json = JsonParser.parseString(layout).asJsonObject
            activity.setContent {
                Column(Modifier.padding(top = 40.dp).semantics { testTagsAsResourceId = true }) {
                    if (path == "codegen") {
                        val data by vm.data.collectAsState()
                        SegmentValueChangeGeneratedView(data = data, viewModel = vm)
                    } else {
                        val write = remember { { m: Map<String, Any> -> state.value = state.value + m } }
                        DynamicView(json = json, data = state.value + ("updateData" to write))
                    }
                }
            }
        }
        device.wait(Until.hasObject(By.text("s2")), 10_000)
        settle()
        val model = if (path == "codegen") Model { vm.updateData(it) } else Model { m -> state.value = state.value + m }
        return scenario to model
    }

    private fun run(path: String): List<String> {
        val wrong = mutableListOf<String>()
        val log = Collections.synchronizedList(mutableListOf<String>())
        val (scenario, model) = launch(path, log)
        for (tab in listOf("p2", "q2", "r2", "s2")) {
            device.findObject(By.text(tab))?.click()
            settle()
        }
        val operated = log.toList()
        println("SEGVC $path operated seq=$operated")
        for (c in controls) {
            val base = c.replaceFirstChar { it.uppercase() }
            val own = operated.filter { it.startsWith("on$base") }.joinToString("") {
                when (it.last()) { 'V' -> "v"; 'C' -> "c"; else -> "x" }
            }
            println("SEGVC $path $c order=$own")
            if (own != "vc") wrong += "$path $c: the write's report, then onClick — got $own"
        }
        InstrumentationRegistry.getInstrumentation().runOnMainSync { model.move(mapOf("segvbIdx" to 0, "segwbIdx" to 0)) }
        settle()
        val fromModel = log.toList().drop(operated.size)
        println("SEGVC $path model seq=$fromModel")
        if (fromModel.isNotEmpty()) wrong += "$path: the view model's change called $fromModel"
        scenario.close()
        return wrong
    }

    @Test
    fun a_segments_value_change_reports_the_users_choice_where_no_on_value_change_is_declared() {
        val wrong = run("dynamic") + run("codegen")
        assertTrue("Segment valueChange: $wrong", wrong.isEmpty())
    }
}
