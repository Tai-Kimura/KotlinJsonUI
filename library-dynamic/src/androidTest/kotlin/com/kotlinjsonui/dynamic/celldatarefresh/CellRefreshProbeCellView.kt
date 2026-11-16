// Pasted from what `kjui build` (kjui_tools of jsonui-cli 5447c616 + the sibling-locals fix, c38b15be) emits for three
// Collections of cell_refresh_probe_cell cells — cellIdProperty "cellId", "key", and "cellId" with
// autoChangeTrackingId, each in a View of its own — and the cell view and view model it scaffolds, for
// DynamicCellDataRefreshProbeTest (jsonui-cli ticket ios-cell-ignores-data-change-when-cellid-is-fixed-android-updates).
// Changed from the output: the packages (com.kotlinjsonui.probe.* → com.kotlinjsonui.dynamic.celldatarefresh).
package com.kotlinjsonui.dynamic.celldatarefresh

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CellRefreshProbeCellView(
    viewModel: CellRefreshProbeCellViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val data by viewModel.data.collectAsState()

    CellRefreshProbeCellGeneratedView(data = data, viewModel = viewModel, modifier = modifier)
}
