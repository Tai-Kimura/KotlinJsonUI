package com.example.kotlinjsonui.sample.views.textfield_events_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.TextfieldEventsTestViewModel

@Composable
fun TextfieldEventsTestView(
    viewModel: TextfieldEventsTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    TextfieldEventsTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
