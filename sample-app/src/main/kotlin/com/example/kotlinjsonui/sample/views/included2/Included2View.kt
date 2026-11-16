package com.example.kotlinjsonui.sample.views.included2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.Included2ViewModel

@Composable
fun Included2View(
    viewModel: Included2ViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    Included2GeneratedView(
        data = data,
        viewModel = viewModel
    )
}
