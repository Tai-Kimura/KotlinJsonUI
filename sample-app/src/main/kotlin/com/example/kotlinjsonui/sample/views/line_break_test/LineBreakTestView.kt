package com.example.kotlinjsonui.sample.views.line_break_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.LineBreakTestViewModel

@Composable
fun LineBreakTestView(
    viewModel: LineBreakTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    LineBreakTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
