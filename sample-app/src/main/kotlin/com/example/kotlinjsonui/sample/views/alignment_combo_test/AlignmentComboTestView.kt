package com.example.kotlinjsonui.sample.views.alignment_combo_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.AlignmentComboTestViewModel

@Composable
fun AlignmentComboTestView(
    viewModel: AlignmentComboTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    AlignmentComboTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
