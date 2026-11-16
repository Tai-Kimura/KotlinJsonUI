package com.example.kotlinjsonui.sample.views.partial_attributes_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.PartialAttributesTestViewModel

@Composable
fun PartialAttributesTestView(
    viewModel: PartialAttributesTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    PartialAttributesTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
