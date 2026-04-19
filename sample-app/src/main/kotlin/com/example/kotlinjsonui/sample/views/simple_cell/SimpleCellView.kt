package com.example.kotlinjsonui.sample.views.simple_cell

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kotlinjsonui.sample.data.SimpleCellData
import com.example.kotlinjsonui.sample.viewmodels.SimpleCellViewModel

@Composable
fun SimpleCellView(
    data: SimpleCellData,
    viewModel: SimpleCellViewModel,
    modifier: Modifier = Modifier
) {
    // This is a cell view for use in Collection components
    // The data parameter contains an 'item' property with the cell's data
    
    SimpleCellGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}
