package com.kotlinjsonui.dynamic

import android.app.Application
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.ComposeTimeoutException
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.JsonParser
import com.kotlinjsonui.data.CollectionDataSection
import com.kotlinjsonui.data.CollectionDataSource
import com.kotlinjsonui.dynamic.celldatarefresh.CellDataRefreshGeneratedView
import com.kotlinjsonui.dynamic.celldatarefresh.CellDataRefreshViewModel
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A cell whose key stays fixed while its data changes shows the new data —
 * the Android rows of jsonui-cli ticket
 * ios-cell-ignores-data-change-when-cellid-is-fixed-android-updates, whose
 * iOS arm is SwiftJsonUI's CellDataRefreshProbeUITests (the same three
 * Collections). Opt-in (`-e cellDataRefreshProbe 1`), so conformance-mobile's
 * android-library-tests raises it with the other probes.
 *
 * Three Collections of three cell_refresh_probe_cell cells (androidTest
 * assets/Layouts), none of whose keys change: (A) cellIdProperty "cellId",
 * (B) "key" with no "cellId" in the data, (C) "cellId" with
 * autoChangeTrackingId. Each step rewrites every cell's title ("A0 v1", …)
 * from the model; every title is awaited for up to 3 s. Two paths: what
 * `kjui build` emits (celldatarefresh/, pasted — the model is its generated
 * ViewModel, the cells the cell view and view model it scaffolds) and
 * DynamicView (the model is the data map).
 *
 * Measured first in KotlinJsonUI's conformance-host (2026-10-04, conf_ci):
 * 6 of 6 on every Collection on both paths. Negative control: with the
 * cell's LaunchedEffect(currentCellData) and data-keyed remember removed from
 * the pasted view, (A) and (B) read 0 of 6 on the generated path. A first
 * reading that did not wait reported stale cells on both paths.
 */
@RunWith(AndroidJUnit4::class)
class DynamicCellDataRefreshProbeTest {

    @get:Rule
    val rule = createComposeRule()

    @Before
    fun skipUnlessRequested() {
        val on = InstrumentationRegistry.getArguments().getString("cellDataRefreshProbe") == "1"
        Assume.assumeTrue("set -e cellDataRefreshProbe 1", on)
    }

    private val layout = """{"type": "View", "id": "cdr_root", "orientation": "vertical", "width": "matchParent", "height": "wrapContent", "data": [{"name": "rows_cellid", "class": "CollectionDataSource", "defaultValue": {"sections": [{"cells": []}]}}, {"name": "rows_key", "class": "CollectionDataSource", "defaultValue": {"sections": [{"cells": []}]}}, {"name": "rows_tracked", "class": "CollectionDataSource", "defaultValue": {"sections": [{"cells": []}]}}], "child": [{"type": "View", "id": "cdr_cellid_box", "width": "matchParent", "height": "wrapContent", "child": [{"type": "Collection", "id": "cdr_cellid", "layout": "vertical", "width": "matchParent", "height": 150, "items": "@{rows_cellid}", "cellIdProperty": "cellId", "sections": [{"cell": "cell_refresh_probe_cell"}]}]}, {"type": "View", "id": "cdr_key_box", "width": "matchParent", "height": "wrapContent", "child": [{"type": "Collection", "id": "cdr_key", "layout": "vertical", "width": "matchParent", "height": 150, "items": "@{rows_key}", "cellIdProperty": "key", "sections": [{"cell": "cell_refresh_probe_cell"}]}]}, {"type": "View", "id": "cdr_tracked_box", "width": "matchParent", "height": "wrapContent", "child": [{"type": "Collection", "id": "cdr_tracked", "layout": "vertical", "width": "matchParent", "height": 150, "items": "@{rows_tracked}", "cellIdProperty": "cellId", "sections": [{"cell": "cell_refresh_probe_cell"}], "autoChangeTrackingId": true}]}]}"""

    private val lists = listOf("A" to ("rows_cellid" to "cellId"), "K" to ("rows_key" to "key"), "T" to ("rows_tracked" to "cellId"))

    private fun rows(prefix: String, keyName: String, version: Int): CollectionDataSource {
        val cells: List<Map<String, Any>> = (0 until 3).map { i ->
            mapOf(keyName to "${prefix.lowercase()}$i", "title" to "$prefix$i v$version")
        }
        return CollectionDataSource(
            sections = listOf(CollectionDataSection(cells = CollectionDataSection.CellData(viewName = "cell_refresh_probe_cell", data = cells)))
        )
    }

    private fun model(version: Int): Map<String, Any> =
        lists.associate { (prefix, list) -> list.first to rows(prefix, list.second, version) }

    private fun shown(text: String): Boolean = try {
        rule.waitUntil(3_000) { rule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() }
        true
    } catch (_: ComposeTimeoutException) {
        false
    }

    private fun run(path: String): List<String> {
        val vm = CellDataRefreshViewModel(ApplicationProvider.getApplicationContext<Application>())
        vm.updateData(model(0))
        val state = mutableStateOf(model(0))
        val json = JsonParser.parseString(layout).asJsonObject
        rule.setContent {
            if (path == "codegen") {
                val data by vm.data.collectAsState()
                CellDataRefreshGeneratedView(data = data, viewModel = vm)
            } else {
                DynamicView(json = json, data = state.value)
            }
        }
        val stale = mutableListOf<String>()
        for (version in 0..2) {
            if (version > 0) {
                rule.runOnIdle { if (path == "codegen") vm.updateData(model(version)) else state.value = model(version) }
            }
            for ((prefix, list) in lists) {
                for (i in 0 until 3) {
                    val want = "$prefix$i v$version"
                    val ok = shown(want)
                    println("CELLREFRESH $path version $version cellIdProperty ${list.second}${if (prefix == "T") " + autoChangeTrackingId" else ""} cell $i: $want ${if (ok) "shown" else "NOT SHOWN"}")
                    if (!ok) stale += "$path $want"
                }
            }
        }
        return stale
    }

    @Test
    fun dynamicCellsShowNewDataUnderAFixedKey() {
        val stale = run("dynamic")
        assertTrue("cells kept their old data after a data change with a fixed key: $stale", stale.isEmpty())
    }

    @Test
    fun generatedCellsShowNewDataUnderAFixedKey() {
        val stale = run("codegen")
        assertTrue("cells kept their old data after a data change with a fixed key: $stale", stale.isEmpty())
    }
}
