package com.example.kotlinjsonui.sample.views.grid_footer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.kotlinjsonui.sample.viewmodels.GridFooterViewModel

@Composable
fun GridFooterView(
    viewModel: GridFooterViewModel,
    modifier: Modifier = Modifier
) {
    // This is a cell view for use in Collection components
    // Data is observed from viewModel using collectAsState
    val data by viewModel.data.collectAsState()

    GridFooterGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}
