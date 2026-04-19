package com.example.kotlinjsonui.sample.views.product_cell

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kotlinjsonui.sample.data.ProductCellData
import com.example.kotlinjsonui.sample.viewmodels.ProductCellViewModel

@Composable
fun ProductCellView(
    data: ProductCellData,
    viewModel: ProductCellViewModel,
    modifier: Modifier = Modifier
) {
    // This is a cell view for use in Collection components
    // The data parameter contains an 'item' property with the cell's data
    
    ProductCellGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}
