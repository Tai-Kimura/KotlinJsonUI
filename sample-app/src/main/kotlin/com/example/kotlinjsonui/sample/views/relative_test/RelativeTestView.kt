package com.example.kotlinjsonui.sample.views.relative_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.RelativeTestViewModel

@Composable
fun RelativeTestView(
    viewModel: RelativeTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    RelativeTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
