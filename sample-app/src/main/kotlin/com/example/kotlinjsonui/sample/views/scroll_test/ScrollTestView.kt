package com.example.kotlinjsonui.sample.views.scroll_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.ScrollTestViewModel

@Composable
fun ScrollTestView(
    viewModel: ScrollTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    ScrollTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
