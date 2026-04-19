package com.example.kotlinjsonui.sample.views.implemented_attributes_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.ImplementedAttributesTestViewModel

@Composable
fun ImplementedAttributesTestView(
    viewModel: ImplementedAttributesTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    ImplementedAttributesTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
