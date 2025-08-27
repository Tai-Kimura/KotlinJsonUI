package com.example.kotlinjsonui.sample.views.feature_cell

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kotlinjsonui.sample.data.FeatureCellData
import com.example.kotlinjsonui.sample.viewmodels.FeatureCellViewModel

@Composable
fun FeatureCellView(
    data: FeatureCellData,
    viewModel: FeatureCellViewModel,
    modifier: Modifier = Modifier
) {
    // This is a cell view for use in Collection components
    // The data parameter contains an 'item' property with the cell's data
    
    FeatureCellGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}
