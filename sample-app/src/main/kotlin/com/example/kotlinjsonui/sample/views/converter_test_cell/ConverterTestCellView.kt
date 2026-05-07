package com.example.kotlinjsonui.sample.views.converter_test_cell

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kotlinjsonui.sample.data.ConverterTestCellData
import com.example.kotlinjsonui.sample.viewmodels.ConverterTestCellViewModel

@Composable
fun ConverterTestCellView(
    data: ConverterTestCellData,
    viewModel: ConverterTestCellViewModel,
    modifier: Modifier = Modifier
) {
    // This is a cell view for use in Collection components
    // The data parameter contains an 'item' property with the cell's data
    
    ConverterTestCellGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}
