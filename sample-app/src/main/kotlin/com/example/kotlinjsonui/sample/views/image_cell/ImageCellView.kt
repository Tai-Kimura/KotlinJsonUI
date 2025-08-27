package com.example.kotlinjsonui.sample.views.image_cell

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kotlinjsonui.sample.data.ImageCellData
import com.example.kotlinjsonui.sample.viewmodels.ImageCellViewModel

@Composable
fun ImageCellView(
    data: ImageCellData,
    viewModel: ImageCellViewModel,
    modifier: Modifier = Modifier
) {
    // This is a cell view for use in Collection components
    // The data parameter contains an 'item' property with the cell's data
    
    ImageCellGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}
