package com.example.kotlinjsonui.sample.views.basic_cell

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kotlinjsonui.sample.data.BasicCellData
import com.example.kotlinjsonui.sample.viewmodels.BasicCellViewModel

@Composable
fun BasicCellView(
    data: BasicCellData,
    viewModel: BasicCellViewModel,
    modifier: Modifier = Modifier
) {
    // This is a cell view for use in Collection components
    // The data parameter contains an 'item' property with the cell's data
    
    BasicCellGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}
