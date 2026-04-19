package com.example.kotlinjsonui.sample.views.category_footer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kotlinjsonui.sample.data.CategoryFooterData
import com.example.kotlinjsonui.sample.viewmodels.CategoryFooterViewModel

@Composable
fun CategoryFooterView(
    data: CategoryFooterData,
    viewModel: CategoryFooterViewModel,
    modifier: Modifier = Modifier
) {
    // This is a cell view for use in Collection components
    // The data parameter contains an 'item' property with the cell's data
    
    CategoryFooterGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}
