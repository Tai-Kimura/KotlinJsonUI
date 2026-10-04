package com.kotlinjsonui.conformance

import android.app.Application
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.kotlinjsonui.conformance.celldatarefresh.CellDataRefreshGeneratedView
import com.kotlinjsonui.conformance.celldatarefresh.CellDataRefreshViewModel
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.data.CollectionDataSection
import com.kotlinjsonui.data.CollectionDataSource
import com.kotlinjsonui.dynamic.DynamicView
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A cell whose key stays fixed while its data changes shows the new data —
 * the Android rows of jsonui-cli ticket
 * ios-cell-ignores-data-change-when-cellid-is-fixed-android-updates, whose
 * iOS arm is SwiftJsonUI's CellDataRefreshProbeUITests (the same three
 * Collections). NOT part of the conformance suite, and skipped unless
 * requested (`-e cellDataRefreshProbe 1`); the Dynamic path needs the
 * fixtures synced (conformance_cell, a Collection fixture's cell, under
 * assets/Layouts).
 *
 * Three Collections of three conformance_cell cells (each in a View of its
 * own: three sibling Collections declare section0 / cellData0 three times in
 * one scope in what kjui build emits, which does not compile), none of whose keys
 * change: (A) cellIdProperty "cellId", (B) "key" with no "cellId" in the
 * data, (C) "cellId" with autoChangeTrackingId. Each step rewrites every
 * cell's title ("A0 v1", …) from the model; the test reads whether every cell
 * shows its new title. Two paths: what `kjui build` emits (celldatarefresh/,
 * pasted — the model is its generated ViewModel, the cells the cell view and
 * view model it scaffolds) and DynamicView (the model is the data map).
 */
@OptIn(ExperimentalComposeUiApi::class)
@RunWith(AndroidJUnit4::class)
class CellDataRefreshProbeTest {

    @Before
    fun skipUnlessRequested() {
        val enabled = InstrumentationRegistry.getArguments().getString("cellDataRefreshProbe") == "1"
        Assume.assumeTrue("set -e cellDataRefreshProbe 1", enabled)
    }

    private val layout = """{"type": "View", "id": "cdr_root", "orientation": "vertical", "width": "matchParent", "height": "wrapContent", "data": [{"name": "rows_cellid", "class": "CollectionDataSource", "defaultValue": {"sections": [{"cells": []}]}}, {"name": "rows_key", "class": "CollectionDataSource", "defaultValue": {"sections": [{"cells": []}]}}, {"name": "rows_tracked", "class": "CollectionDataSource", "defaultValue": {"sections": [{"cells": []}]}}], "child": [{"type": "View", "id": "cdr_cellid_box", "width": "matchParent", "height": "wrapContent", "child": [{"type": "Collection", "id": "cdr_cellid", "layout": "vertical", "width": "matchParent", "height": 150, "items": "@{rows_cellid}", "cellIdProperty": "cellId", "sections": [{"cell": "conformance_cell"}]}]}, {"type": "View", "id": "cdr_key_box", "width": "matchParent", "height": "wrapContent", "child": [{"type": "Collection", "id": "cdr_key", "layout": "vertical", "width": "matchParent", "height": 150, "items": "@{rows_key}", "cellIdProperty": "key", "sections": [{"cell": "conformance_cell"}]}]}, {"type": "View", "id": "cdr_tracked_box", "width": "matchParent", "height": "wrapContent", "child": [{"type": "Collection", "id": "cdr_tracked", "layout": "vertical", "width": "matchParent", "height": 150, "items": "@{rows_tracked}", "cellIdProperty": "cellId", "sections": [{"cell": "conformance_cell"}], "autoChangeTrackingId": true}]}]}"""

    private val lists = listOf("A" to ("rows_cellid" to "cellId"), "K" to ("rows_key" to "key"), "T" to ("rows_tracked" to "cellId"))

    private fun rows(prefix: String, keyName: String, version: Int): CollectionDataSource {
        val cells: List<Map<String, Any>> = (0 until 3).map { i ->
            mapOf(keyName to "${prefix.lowercase()}$i", "title" to "$prefix$i v$version")
        }
        return CollectionDataSource(sections = listOf(CollectionDataSection(cells = CollectionDataSection.CellData(viewName = "conformance_cell", data = cells))))
    }

    private fun model(version: Int): Map<String, Any> =
        lists.associate { (prefix, list) -> list.first to rows(prefix, list.second, version) }

    private val device get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private fun settle() {
        device.waitForIdle()
        Thread.sleep(600)
        device.waitForIdle()
    }

    private fun run(path: String): List<String> {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = CellDataRefreshViewModel(app)
        vm.updateData(model(0))
        val state = mutableStateOf(model(0))
        val scenario = ActivityScenario.launch(FixtureHostActivity::class.java)
        scenario.onActivity { activity ->
            DynamicModeManager.setDynamicModeEnabled(activity, false)
            val json = JsonParser.parseString(layout).asJsonObject
            activity.setContent {
                Column(Modifier.padding(top = 40.dp).semantics { testTagsAsResourceId = true }) {
                    if (path == "codegen") {
                        val data by vm.data.collectAsState()
                        CellDataRefreshGeneratedView(data = data, viewModel = vm)
                    } else {
                        DynamicView(json = json, data = state.value)
                    }
                }
            }
        }
        device.wait(Until.hasObject(By.text("A0 v0")), 10_000)
        settle()
        val stale = mutableListOf<String>()
        for (version in 0..2) {
            if (version > 0) {
                InstrumentationRegistry.getInstrumentation().runOnMainSync {
                    if (path == "codegen") vm.updateData(model(version)) else state.value = model(version)
                }
                settle()
            }
            for ((prefix, list) in lists) {
                for (i in 0 until 3) {
                    val want = "$prefix$i v$version"
                    val shown = device.wait(Until.hasObject(By.text(want)), if (version == 0) 10_000L else 3_000L) == true
                    println("CELLREFRESH $path version $version cellIdProperty ${list.second}${if (prefix == "T") " + autoChangeTrackingId" else ""} cell $i: $want ${if (shown) "shown" else "NOT SHOWN"}")
                    if (!shown) stale += "$path $want"
                }
            }
        }
        scenario.close()
        return stale
    }

    @Test
    fun cells_show_new_data_under_a_fixed_key_on_both_paths() {
        val stale = run("dynamic") + run("codegen")
        assertTrue("cells kept their old data after a data change with a fixed key: $stale", stale.isEmpty())
    }
}
