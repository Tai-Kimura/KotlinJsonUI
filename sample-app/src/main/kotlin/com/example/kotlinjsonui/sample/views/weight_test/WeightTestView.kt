package com.example.kotlinjsonui.sample.views.weight_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.WeightTestViewModel

@Composable
fun WeightTestView(
    viewModel: WeightTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    WeightTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
