package com.example.kotlinjsonui.sample.views.section_footer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kotlinjsonui.sample.data.SectionFooterData
import com.example.kotlinjsonui.sample.viewmodels.SectionFooterViewModel

@Composable
fun SectionFooterView(
    data: SectionFooterData,
    viewModel: SectionFooterViewModel,
    modifier: Modifier = Modifier
) {
    // This is a cell view for use in Collection components
    // The data parameter contains an 'item' property with the cell's data
    
    SectionFooterGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}
