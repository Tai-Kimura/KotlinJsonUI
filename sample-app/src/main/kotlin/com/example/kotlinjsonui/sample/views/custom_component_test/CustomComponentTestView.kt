package com.example.kotlinjsonui.sample.views.custom_component_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.CustomComponentTestViewModel

@Composable
fun CustomComponentTestView(
    viewModel: CustomComponentTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    CustomComponentTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
