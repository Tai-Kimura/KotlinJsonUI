package com.example.kotlinjsonui.sample.views.text_styling_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.TextStylingTestViewModel

@Composable
fun TextStylingTestView(
    viewModel: TextStylingTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    TextStylingTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
