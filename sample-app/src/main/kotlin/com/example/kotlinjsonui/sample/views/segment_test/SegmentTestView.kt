package com.example.kotlinjsonui.sample.views.segment_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.SegmentTestViewModel

@Composable
fun SegmentTestView(
    viewModel: SegmentTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    SegmentTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
