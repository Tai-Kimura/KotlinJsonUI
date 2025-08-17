package com.example.kotlinjsonui.sample.views.alignment_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.AlignmentTestViewModel

@Composable
fun AlignmentTestView(
    viewModel: AlignmentTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    AlignmentTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
