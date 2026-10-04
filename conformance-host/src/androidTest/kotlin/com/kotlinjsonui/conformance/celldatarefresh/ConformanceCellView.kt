// Pasted from what `kjui build` (kjui_tools of jsonui-cli train/1.9.11 = 5447c616) emits for three Collections
// of conformance_cell cells — cellIdProperty "cellId", "key", and "cellId" with autoChangeTrackingId — and the
// cell view and view model it scaffolds, for CellDataRefreshProbeTest (jsonui-cli ticket
// ios-cell-ignores-data-change-when-cellid-is-fixed-android-updates). Changed from the output: the packages
// (com.kotlinjsonui.probe.* → com.kotlinjsonui.conformance.celldatarefresh) and the cell's two colorResource(R.color.…)
// (the probe project's resources) → the literal colors conformance_cell.json declares.
package com.kotlinjsonui.conformance.celldatarefresh

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ConformanceCellView(
    viewModel: ConformanceCellViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val data by viewModel.data.collectAsState()

    ConformanceCellGeneratedView(data = data, viewModel = viewModel, modifier = modifier)
}
