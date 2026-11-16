package com.example.kotlinjsonui.sample.views.included1

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.Included1ViewModel

@Composable
fun Included1View(
    viewModel: Included1ViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    Included1GeneratedView(
        data = data,
        viewModel = viewModel
    )
}
