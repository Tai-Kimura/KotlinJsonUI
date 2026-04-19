package com.example.kotlinjsonui.sample.views.button_enabled_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.ButtonEnabledTestViewModel

@Composable
fun ButtonEnabledTestView(
    viewModel: ButtonEnabledTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    ButtonEnabledTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
