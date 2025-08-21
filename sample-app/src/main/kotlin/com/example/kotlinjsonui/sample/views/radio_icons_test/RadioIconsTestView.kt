package com.example.kotlinjsonui.sample.views.radio_icons_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.RadioIconsTestViewModel

@Composable
fun RadioIconsTestView(
    viewModel: RadioIconsTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    RadioIconsTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
