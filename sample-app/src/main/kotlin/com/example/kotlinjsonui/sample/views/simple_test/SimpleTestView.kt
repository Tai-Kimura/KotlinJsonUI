package com.example.kotlinjsonui.sample.views.simple_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.SimpleTestViewModel

@Composable
fun SimpleTestView(
    viewModel: SimpleTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    SimpleTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
