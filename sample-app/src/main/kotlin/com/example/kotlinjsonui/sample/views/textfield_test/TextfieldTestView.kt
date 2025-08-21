package com.example.kotlinjsonui.sample.views.textfield_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.TextfieldTestViewModel

@Composable
fun TextfieldTestView(
    viewModel: TextfieldTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    TextfieldTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
