package com.example.kotlinjsonui.sample.views.button_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.ButtonTestViewModel

@Composable
fun ButtonTestView(
    viewModel: ButtonTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    ButtonTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
