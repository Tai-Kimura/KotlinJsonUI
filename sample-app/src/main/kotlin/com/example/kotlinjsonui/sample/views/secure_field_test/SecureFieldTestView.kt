package com.example.kotlinjsonui.sample.views.secure_field_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.SecureFieldTestViewModel

@Composable
fun SecureFieldTestView(
    viewModel: SecureFieldTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    SecureFieldTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
