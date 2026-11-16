package com.example.kotlinjsonui.sample.views.visibility_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.VisibilityTestViewModel

@Composable
fun VisibilityTestView(
    viewModel: VisibilityTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    VisibilityTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
