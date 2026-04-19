package com.example.kotlinjsonui.sample.views.date_picker_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.DatePickerTestViewModel

@Composable
fun DatePickerTestView(
    viewModel: DatePickerTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    DatePickerTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
