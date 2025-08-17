package com.example.kotlinjsonui.sample.views.form_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.FormTestViewModel

@Composable
fun FormTestView(
    viewModel: FormTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    FormTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
