package com.example.kotlinjsonui.sample.views.grid_header

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kotlinjsonui.sample.data.GridHeaderData
import com.example.kotlinjsonui.sample.viewmodels.GridHeaderViewModel

@Composable
fun GridHeaderView(
    data: GridHeaderData,
    viewModel: GridHeaderViewModel,
    modifier: Modifier = Modifier
) {
    // This is a cell view for use in Collection components
    // The data parameter contains an 'item' property with the cell's data
    
    GridHeaderGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}
