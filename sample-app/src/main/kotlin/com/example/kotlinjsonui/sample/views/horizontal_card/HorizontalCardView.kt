package com.example.kotlinjsonui.sample.views.horizontal_card

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kotlinjsonui.sample.data.HorizontalCardData
import com.example.kotlinjsonui.sample.viewmodels.HorizontalCardViewModel

@Composable
fun HorizontalCardView(
    data: HorizontalCardData,
    viewModel: HorizontalCardViewModel,
    modifier: Modifier = Modifier
) {
    // This is a cell view for use in Collection components
    // The data parameter contains an 'item' property with the cell's data
    
    HorizontalCardGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}
