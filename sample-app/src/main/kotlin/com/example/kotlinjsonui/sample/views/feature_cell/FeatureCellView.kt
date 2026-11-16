package com.example.kotlinjsonui.sample.views.feature_cell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.kotlinjsonui.sample.viewmodels.FeatureCellViewModel

@Composable
fun FeatureCellView(
    viewModel: FeatureCellViewModel,
    modifier: Modifier = Modifier
) {
    // This is a cell view for use in Collection components
    // Data is observed from viewModel using collectAsState
    val data by viewModel.data.collectAsState()

    FeatureCellGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}
