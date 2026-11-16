package com.example.kotlinjsonui.sample.views.components_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.ComponentsTestViewModel

@Composable
fun ComponentsTestView(
    viewModel: ComponentsTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    ComponentsTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
