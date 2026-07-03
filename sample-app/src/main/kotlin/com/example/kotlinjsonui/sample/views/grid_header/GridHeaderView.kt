package com.example.kotlinjsonui.sample.views.grid_header

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.kotlinjsonui.sample.viewmodels.GridHeaderViewModel

@Composable
fun GridHeaderView(
    viewModel: GridHeaderViewModel,
    modifier: Modifier = Modifier
) {
    // This is a cell view for use in Collection components
    // Data is observed from viewModel using collectAsState
    val data by viewModel.data.collectAsState()

    GridHeaderGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}
