package com.example.kotlinjsonui.sample.views.switch_events_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.SwitchEventsTestViewModel

@Composable
fun SwitchEventsTestView(
    viewModel: SwitchEventsTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    SwitchEventsTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
