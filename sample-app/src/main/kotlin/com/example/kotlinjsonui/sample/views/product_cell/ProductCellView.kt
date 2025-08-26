package com.example.kotlinjsonui.sample.views.product_cell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.ProductCellViewModel

@Composable
fun ProductCellView(
    viewModel: ProductCellViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    ProductCellGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
