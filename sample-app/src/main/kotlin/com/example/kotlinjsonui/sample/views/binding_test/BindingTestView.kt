package com.example.kotlinjsonui.sample.views.binding_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.BindingTestViewModel

@Composable
fun BindingTestView(
    viewModel: BindingTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    BindingTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
