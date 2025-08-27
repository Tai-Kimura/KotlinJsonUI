package com.example.kotlinjsonui.sample.views.section_header

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kotlinjsonui.sample.data.SectionHeaderData
import com.example.kotlinjsonui.sample.viewmodels.SectionHeaderViewModel

@Composable
fun SectionHeaderView(
    data: SectionHeaderData,
    viewModel: SectionHeaderViewModel,
    modifier: Modifier = Modifier
) {
    // This is a cell view for use in Collection components
    // The data parameter contains an 'item' property with the cell's data
    
    SectionHeaderGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}
