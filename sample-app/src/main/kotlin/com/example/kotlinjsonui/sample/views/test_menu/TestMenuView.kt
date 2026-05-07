package com.example.kotlinjsonui.sample.views.test_menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.TestMenuViewModel

@Composable
fun TestMenuView(
    viewModel: TestMenuViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    TestMenuGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
