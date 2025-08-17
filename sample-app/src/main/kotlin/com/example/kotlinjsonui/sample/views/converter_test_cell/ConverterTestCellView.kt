package com.example.kotlinjsonui.sample.views.converter_test_cell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.ConverterTestCellViewModel

@Composable
fun ConverterTestCellView(
    viewModel: ConverterTestCellViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    ConverterTestCellGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
