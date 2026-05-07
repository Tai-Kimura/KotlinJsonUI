package com.example.kotlinjsonui.sample.views.grid_footer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kotlinjsonui.sample.data.GridFooterData
import com.example.kotlinjsonui.sample.viewmodels.GridFooterViewModel

@Composable
fun GridFooterView(
    data: GridFooterData,
    viewModel: GridFooterViewModel,
    modifier: Modifier = Modifier
) {
    // This is a cell view for use in Collection components
    // The data parameter contains an 'item' property with the cell's data
    
    GridFooterGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}
