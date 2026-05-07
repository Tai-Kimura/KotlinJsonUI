package com.example.kotlinjsonui.sample.views.weight_test_with_fixed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.WeightTestWithFixedViewModel

@Composable
fun WeightTestWithFixedView(
    viewModel: WeightTestWithFixedViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    WeightTestWithFixedGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
