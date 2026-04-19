package com.example.kotlinjsonui.sample.views.category_header

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kotlinjsonui.sample.data.CategoryHeaderData
import com.example.kotlinjsonui.sample.viewmodels.CategoryHeaderViewModel

@Composable
fun CategoryHeaderView(
    data: CategoryHeaderData,
    viewModel: CategoryHeaderViewModel,
    modifier: Modifier = Modifier
) {
    // This is a cell view for use in Collection components
    // The data parameter contains an 'item' property with the cell's data
    
    CategoryHeaderGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}
