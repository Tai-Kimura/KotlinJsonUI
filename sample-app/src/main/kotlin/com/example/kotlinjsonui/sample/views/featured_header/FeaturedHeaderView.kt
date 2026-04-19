package com.example.kotlinjsonui.sample.views.featured_header

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kotlinjsonui.sample.data.FeaturedHeaderData
import com.example.kotlinjsonui.sample.viewmodels.FeaturedHeaderViewModel

@Composable
fun FeaturedHeaderView(
    data: FeaturedHeaderData,
    viewModel: FeaturedHeaderViewModel,
    modifier: Modifier = Modifier
) {
    // This is a cell view for use in Collection components
    // The data parameter contains an 'item' property with the cell's data
    
    FeaturedHeaderGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}
