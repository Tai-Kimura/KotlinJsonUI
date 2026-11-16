package com.example.kotlinjsonui.sample.views.margins_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.MarginsTestViewModel

@Composable
fun MarginsTestView(
    viewModel: MarginsTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    MarginsTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
