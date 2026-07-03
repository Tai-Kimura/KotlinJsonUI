package com.example.kotlinjsonui.sample.views.featured_header

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.kotlinjsonui.sample.viewmodels.FeaturedHeaderViewModel

@Composable
fun FeaturedHeaderView(
    viewModel: FeaturedHeaderViewModel,
    modifier: Modifier = Modifier
) {
    // This is a cell view for use in Collection components
    // Data is observed from viewModel using collectAsState
    val data by viewModel.data.collectAsState()

    FeaturedHeaderGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}
