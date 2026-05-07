package com.example.kotlinjsonui.sample.views.keyboard_avoidance_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.KeyboardAvoidanceTestViewModel

@Composable
fun KeyboardAvoidanceTestView(
    viewModel: KeyboardAvoidanceTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    KeyboardAvoidanceTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
