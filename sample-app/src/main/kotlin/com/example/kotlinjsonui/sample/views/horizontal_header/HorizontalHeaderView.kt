package com.example.kotlinjsonui.sample.views.horizontal_header

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kotlinjsonui.sample.data.HorizontalHeaderData
import com.example.kotlinjsonui.sample.viewmodels.HorizontalHeaderViewModel

@Composable
fun HorizontalHeaderView(
    data: HorizontalHeaderData,
    viewModel: HorizontalHeaderViewModel,
    modifier: Modifier = Modifier
) {
    // This is a cell view for use in Collection components
    // The data parameter contains an 'item' property with the cell's data
    
    HorizontalHeaderGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}
