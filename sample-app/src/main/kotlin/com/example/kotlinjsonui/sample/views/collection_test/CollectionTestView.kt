package com.example.kotlinjsonui.sample.views.collection_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.CollectionTestViewModel

@Composable
fun CollectionTestView(
    viewModel: CollectionTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    CollectionTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
