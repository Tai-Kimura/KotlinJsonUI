package com.example.kotlinjsonui.sample.views.include_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.IncludeTestViewModel

@Composable
fun IncludeTestView(
    viewModel: IncludeTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    IncludeTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
