package com.example.kotlinjsonui.sample.views.converter_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.ConverterTestViewModel

@Composable
fun ConverterTestView(
    viewModel: ConverterTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    ConverterTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
