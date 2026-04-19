package com.example.kotlinjsonui.sample.views.disabled_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.DisabledTestViewModel

@Composable
fun DisabledTestView(
    viewModel: DisabledTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    DisabledTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
