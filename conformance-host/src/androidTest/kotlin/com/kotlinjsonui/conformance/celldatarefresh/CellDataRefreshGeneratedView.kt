// Pasted from what `kjui build` (kjui_tools of jsonui-cli train/1.9.11 = 5447c616) emits for three Collections
// of conformance_cell cells — cellIdProperty "cellId", "key", and "cellId" with autoChangeTrackingId — and the
// cell view and view model it scaffolds, for CellDataRefreshProbeTest (jsonui-cli ticket
// ios-cell-ignores-data-change-when-cellid-is-fixed-android-updates). Changed from the output: the packages
// (com.kotlinjsonui.probe.* → com.kotlinjsonui.conformance.celldatarefresh) and the cell's two colorResource(R.color.…)
// (the probe project's resources) → the literal colors conformance_cell.json declares.
package com.kotlinjsonui.conformance.celldatarefresh

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotlinjsonui.components.CollectionStack
import com.kotlinjsonui.components.CollectionStackAxis
import com.kotlinjsonui.components.CollectionStackMode
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun CellDataRefreshGeneratedView(
    data: CellDataRefreshData,
    viewModel: CellDataRefreshViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from cell_data_refresh.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
        // Check if Dynamic Mode is active
        if (DynamicModeManager.isActive()) {
            // Dynamic Mode - use SafeDynamicView for real-time updates
            SafeDynamicView(
                layoutName = "cell_data_refresh",
                modifier = modifier,
                data = data.toMap(),
                fallback = {
                    // Show error or loading state when dynamic view is not available
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Dynamic view not available",
                            color = Color.Gray
                        )
                    }
                },
                onError = { error ->
                    // Log error or show error UI
                    android.util.Log.e("DynamicView", "Error loading cell_data_refresh: \$error")
                },
                onLoading = {
                    // Show loading indicator
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            ) { jsonContent ->
                // Parse and render the dynamic JSON content
                // This will be handled by the DynamicView implementation
            }
        } else {
            // Static Mode - use generated code
        Section0(data, viewModel, modifier)    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("cell_data_refresh")
    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: CellDataRefreshData,
    viewModel: CellDataRefreshViewModel,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .testTag("cdr_root")
            .semantics { testTagsAsResourceId = true }
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box(
            modifier = Modifier
                .testTag("cdr_cellid_box")
                .semantics { testTagsAsResourceId = true }
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            val section0 = data.rows_cellid.sections.getOrNull(0)
            val cellData0 = section0?.cells
            CollectionStack(
                mode = CollectionStackMode.LAZY,
                axis = CollectionStackAxis.VERTICAL,
                modifier = Modifier
                                    .testTag("cdr_cellid")
                                    .semantics { testTagsAsResourceId = true }
                                    .fillMaxWidth()
                                    .requiredHeight(150.dp),
                lazyContent = {
                    // Section 1: conformance_cell
                    if (section0 != null) {
                        if (cellData0 != null) {
                            val lazyKeys0 = HashSet<String>().let { seen -> cellData0.data.mapIndexed { i, cell -> ((cell["cellId"] as? String) ?: (cell["cellId"] as? String) ?: i.toString()).let { k -> if (seen.add(k)) k else generateSequence(2) { it + 1 }.map { "$k#$it" }.first { seen.add(it) } } } }
                            items(cellData0.data.size, key = { idx -> lazyKeys0[idx] }) { cellIndex ->
                                val currentCellData = cellData0.data[cellIndex]
                                val cellId = lazyKeys0[cellIndex]
                                val cellViewModel: ConformanceCellViewModel = viewModel(key = "conformance_cell_cell_0_${cellId}_${viewModel.hashCode()}")
                                remember(cellViewModel, currentCellData) { cellViewModel.updateData(currentCellData); currentCellData }
                                LaunchedEffect(currentCellData) { cellViewModel.updateData(currentCellData) }
                                ConformanceCellView(
                                    viewModel = cellViewModel,
                                    modifier = Modifier.testTag("cdr_cellid_item_$cellIndex")
                                )
                            }
                        }
                    }
                },
                eagerContent = {
                    // Section 1: conformance_cell
                    if (section0 != null) {
                        if (cellData0 != null) {
                            val eagerKeys0 = HashSet<String>().let { seen -> cellData0.data.mapIndexed { i, cell -> ((cell["cellId"] as? String) ?: (cell["cellId"] as? String) ?: i.toString()).let { k -> if (seen.add(k)) k else generateSequence(2) { it + 1 }.map { "$k#$it" }.first { seen.add(it) } } } }
                            cellData0.data.forEachIndexed { cellIndex, _ ->
                                val currentCellData = cellData0.data[cellIndex]
                                val cellId = eagerKeys0[cellIndex]
                                val cellViewModel: ConformanceCellViewModel = viewModel(key = "conformance_cell_cell_0_${cellId}_${viewModel.hashCode()}")
                                remember(cellViewModel, currentCellData) { cellViewModel.updateData(currentCellData); currentCellData }
                                LaunchedEffect(currentCellData) { cellViewModel.updateData(currentCellData) }
                                ConformanceCellView(
                                    viewModel = cellViewModel,
                                    modifier = Modifier.testTag("cdr_cellid_item_$cellIndex")
                                )
                            }
                        }
                    }
                }
            )
        }
        Box(
            modifier = Modifier
                .testTag("cdr_key_box")
                .semantics { testTagsAsResourceId = true }
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            val section0 = data.rows_key.sections.getOrNull(0)
            val cellData0 = section0?.cells
            CollectionStack(
                mode = CollectionStackMode.LAZY,
                axis = CollectionStackAxis.VERTICAL,
                modifier = Modifier
                                    .testTag("cdr_key")
                                    .semantics { testTagsAsResourceId = true }
                                    .fillMaxWidth()
                                    .requiredHeight(150.dp),
                lazyContent = {
                    // Section 1: conformance_cell
                    if (section0 != null) {
                        if (cellData0 != null) {
                            val lazyKeys0 = HashSet<String>().let { seen -> cellData0.data.mapIndexed { i, cell -> ((cell["cellId"] as? String) ?: (cell["key"] as? String) ?: i.toString()).let { k -> if (seen.add(k)) k else generateSequence(2) { it + 1 }.map { "$k#$it" }.first { seen.add(it) } } } }
                            items(cellData0.data.size, key = { idx -> lazyKeys0[idx] }) { cellIndex ->
                                val currentCellData = cellData0.data[cellIndex]
                                val cellId = lazyKeys0[cellIndex]
                                val cellViewModel: ConformanceCellViewModel = viewModel(key = "conformance_cell_cell_0_${cellId}_${viewModel.hashCode()}")
                                remember(cellViewModel, currentCellData) { cellViewModel.updateData(currentCellData); currentCellData }
                                LaunchedEffect(currentCellData) { cellViewModel.updateData(currentCellData) }
                                ConformanceCellView(
                                    viewModel = cellViewModel,
                                    modifier = Modifier.testTag("cdr_key_item_$cellIndex")
                                )
                            }
                        }
                    }
                },
                eagerContent = {
                    // Section 1: conformance_cell
                    if (section0 != null) {
                        if (cellData0 != null) {
                            val eagerKeys0 = HashSet<String>().let { seen -> cellData0.data.mapIndexed { i, cell -> ((cell["cellId"] as? String) ?: (cell["key"] as? String) ?: i.toString()).let { k -> if (seen.add(k)) k else generateSequence(2) { it + 1 }.map { "$k#$it" }.first { seen.add(it) } } } }
                            cellData0.data.forEachIndexed { cellIndex, _ ->
                                val currentCellData = cellData0.data[cellIndex]
                                val cellId = eagerKeys0[cellIndex]
                                val cellViewModel: ConformanceCellViewModel = viewModel(key = "conformance_cell_cell_0_${cellId}_${viewModel.hashCode()}")
                                remember(cellViewModel, currentCellData) { cellViewModel.updateData(currentCellData); currentCellData }
                                LaunchedEffect(currentCellData) { cellViewModel.updateData(currentCellData) }
                                ConformanceCellView(
                                    viewModel = cellViewModel,
                                    modifier = Modifier.testTag("cdr_key_item_$cellIndex")
                                )
                            }
                        }
                    }
                }
            )
        }
        Box(
            modifier = Modifier
                .testTag("cdr_tracked_box")
                .semantics { testTagsAsResourceId = true }
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            val section0 = data.rows_tracked.sections.getOrNull(0)
            val cellData0 = section0?.cells
            val enrichedData0 = if (cellData0 != null) remember(cellData0.data) { com.kotlinjsonui.utils.CellIdGenerator.enrichCellIds(cellData0.data, "cellId") } else null
            CollectionStack(
                mode = CollectionStackMode.LAZY,
                axis = CollectionStackAxis.VERTICAL,
                modifier = Modifier
                                    .testTag("cdr_tracked")
                                    .semantics { testTagsAsResourceId = true }
                                    .fillMaxWidth()
                                    .requiredHeight(150.dp),
                lazyContent = {
                    // Section 1: conformance_cell
                    if (section0 != null) {
                        if (enrichedData0 != null) {
                            items(enrichedData0.size, key = { idx -> (enrichedData0[idx]["cellId"] as? String) ?: (enrichedData0[idx]["cellId"] as? String) ?: idx.toString() }) { cellIndex ->
                                val currentCellData = enrichedData0[cellIndex]
                                val cellId = (currentCellData["cellId"] as? String) ?: (currentCellData["cellId"] as? String) ?: "$cellIndex"
                                val cellViewModel: ConformanceCellViewModel = viewModel(key = "conformance_cell_cell_0_${cellId}_${viewModel.hashCode()}")
                                remember(cellViewModel, currentCellData) { cellViewModel.updateData(currentCellData); currentCellData }
                                LaunchedEffect(currentCellData) { cellViewModel.updateData(currentCellData) }
                                ConformanceCellView(
                                    viewModel = cellViewModel,
                                    modifier = Modifier.testTag("cdr_tracked_item_$cellIndex")
                                )
                            }
                        }
                    }
                },
                eagerContent = {
                    // Section 1: conformance_cell
                    if (section0 != null) {
                        if (enrichedData0 != null) {
                            enrichedData0.forEachIndexed { cellIndex, _ ->
                                val currentCellData = enrichedData0[cellIndex]
                                val cellId = (currentCellData["cellId"] as? String) ?: (currentCellData["cellId"] as? String) ?: "$cellIndex"
                                val cellViewModel: ConformanceCellViewModel = viewModel(key = "conformance_cell_cell_0_${cellId}_${viewModel.hashCode()}")
                                remember(cellViewModel, currentCellData) { cellViewModel.updateData(currentCellData); currentCellData }
                                LaunchedEffect(currentCellData) { cellViewModel.updateData(currentCellData) }
                                ConformanceCellView(
                                    viewModel = cellViewModel,
                                    modifier = Modifier.testTag("cdr_tracked_item_$cellIndex")
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}
// >>> RESPONSIVE_HELPERS_END