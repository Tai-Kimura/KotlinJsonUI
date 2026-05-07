package com.example.kotlinjsonui.sample.views.text_view_hint_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.TextViewHintTestViewModel

@Composable
fun TextViewHintTestView(
    viewModel: TextViewHintTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    TextViewHintTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
