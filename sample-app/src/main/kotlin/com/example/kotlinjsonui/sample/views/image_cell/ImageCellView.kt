package com.example.kotlinjsonui.sample.views.image_cell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.kotlinjsonui.sample.viewmodels.ImageCellViewModel

@Composable
fun ImageCellView(
    viewModel: ImageCellViewModel,
    modifier: Modifier = Modifier
) {
    // This is a cell view for use in Collection components
    // Data is observed from viewModel using collectAsState
    val data by viewModel.data.collectAsState()

    ImageCellGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}
