package com.example.kotlinjsonui.sample.views.width_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.WidthTestViewModel

@Composable
fun WidthTestView(
    viewModel: WidthTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    WidthTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
