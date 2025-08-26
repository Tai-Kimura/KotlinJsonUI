package com.example.kotlinjsonui.sample.views.simple_cell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.SimpleCellViewModel

@Composable
fun SimpleCellView(
    viewModel: SimpleCellViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    SimpleCellGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
