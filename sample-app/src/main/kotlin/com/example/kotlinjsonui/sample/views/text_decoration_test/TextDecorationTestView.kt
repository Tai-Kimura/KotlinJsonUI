package com.example.kotlinjsonui.sample.views.text_decoration_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.TextDecorationTestViewModel

@Composable
fun TextDecorationTestView(
    viewModel: TextDecorationTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    TextDecorationTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
