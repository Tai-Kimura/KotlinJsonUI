package com.example.kotlinjsonui.sample.views.collection_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.BasicCellData
import com.example.kotlinjsonui.sample.data.CategoryFooterData
import com.example.kotlinjsonui.sample.data.CategoryHeaderData
import com.example.kotlinjsonui.sample.data.CollectionTestData
import com.example.kotlinjsonui.sample.data.FeatureCellData
import com.example.kotlinjsonui.sample.data.FeaturedHeaderData
import com.example.kotlinjsonui.sample.data.GridFooterData
import com.example.kotlinjsonui.sample.data.GridHeaderData
import com.example.kotlinjsonui.sample.data.HorizontalCardData
import com.example.kotlinjsonui.sample.data.HorizontalHeaderData
import com.example.kotlinjsonui.sample.data.ImageCellData
import com.example.kotlinjsonui.sample.data.ProductCellData
import com.example.kotlinjsonui.sample.data.SectionFooterData
import com.example.kotlinjsonui.sample.data.SectionHeaderData
import com.example.kotlinjsonui.sample.viewmodels.BasicCellViewModel
import com.example.kotlinjsonui.sample.viewmodels.CategoryFooterViewModel
import com.example.kotlinjsonui.sample.viewmodels.CategoryHeaderViewModel
import com.example.kotlinjsonui.sample.viewmodels.CollectionTestViewModel
import com.example.kotlinjsonui.sample.viewmodels.FeatureCellViewModel
import com.example.kotlinjsonui.sample.viewmodels.FeaturedHeaderViewModel
import com.example.kotlinjsonui.sample.viewmodels.GridFooterViewModel
import com.example.kotlinjsonui.sample.viewmodels.GridHeaderViewModel
import com.example.kotlinjsonui.sample.viewmodels.HorizontalCardViewModel
import com.example.kotlinjsonui.sample.viewmodels.HorizontalHeaderViewModel
import com.example.kotlinjsonui.sample.viewmodels.ImageCellViewModel
import com.example.kotlinjsonui.sample.viewmodels.ProductCellViewModel
import com.example.kotlinjsonui.sample.viewmodels.SectionFooterViewModel
import com.example.kotlinjsonui.sample.viewmodels.SectionHeaderViewModel
import com.example.kotlinjsonui.sample.views.basic_cell.BasicCellView
import com.example.kotlinjsonui.sample.views.category_footer.CategoryFooterView
import com.example.kotlinjsonui.sample.views.category_header.CategoryHeaderView
import com.example.kotlinjsonui.sample.views.feature_cell.FeatureCellView
import com.example.kotlinjsonui.sample.views.featured_header.FeaturedHeaderView
import com.example.kotlinjsonui.sample.views.grid_footer.GridFooterView
import com.example.kotlinjsonui.sample.views.grid_header.GridHeaderView
import com.example.kotlinjsonui.sample.views.horizontal_card.HorizontalCardView
import com.example.kotlinjsonui.sample.views.horizontal_header.HorizontalHeaderView
import com.example.kotlinjsonui.sample.views.image_cell.ImageCellView
import com.example.kotlinjsonui.sample.views.product_cell.ProductCellView
import com.example.kotlinjsonui.sample.views.section_footer.SectionFooterView
import com.example.kotlinjsonui.sample.views.section_header.SectionHeaderView
import com.kotlinjsonui.components.CollectionStack
import com.kotlinjsonui.components.CollectionStackAxis
import com.kotlinjsonui.components.CollectionStackMode
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun CollectionTestGeneratedView(
    data: CollectionTestData,
    viewModel: CollectionTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from collection_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
        // Check if Dynamic Mode is active
        if (DynamicModeManager.isActive()) {
            // Dynamic Mode - use SafeDynamicView for real-time updates
            SafeDynamicView(
                layoutName = "collection_test",
                modifier = modifier,
                data = data.toMap(),
                fallback = {
                    // Show error or loading state when dynamic view is not available
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Dynamic view not available",
                            color = Color.Gray
                        )
                    }
                },
                onError = { error ->
                    // Log error or show error UI
                    android.util.Log.e("DynamicView", "Error loading collection_test: \$error")
                },
                onLoading = {
                    // Show loading indicator
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            ) { jsonContent ->
                // Parse and render the dynamic JSON content
                // This will be handled by the DynamicView implementation
            }
        } else {
            // Static Mode - use generated code
        Section4(data, viewModel, modifier)    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("collection_test")
    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: CollectionTestData,
    viewModel: CollectionTestViewModel
) {
    val resolved_text1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 28.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.collection_test_collectionview_test),
        color = colorResource(R.color.black),
        fontFamily = resolved_text1.family,
        fontWeight = resolved_text1.weight,
        fontSize = resolved_text1.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text1.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 36.4.sp),
        modifier = Modifier.padding(bottom = 20.dp)
    )
}

@Composable
private fun Section1(
    data: CollectionTestData,
    viewModel: CollectionTestViewModel
) {
    val resolved_text2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 20.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.collection_test_basic_collection_with_headers_f),
        color = colorResource(R.color.black),
        fontFamily = resolved_text2.family,
        fontWeight = resolved_text2.weight,
        fontSize = resolved_text2.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text2.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 26.0.sp),
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun Section2(
    data: CollectionTestData,
    viewModel: CollectionTestViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .padding(bottom = 20.dp)
            .requiredHeight(300.dp)
    ) {
        // Section 1: basic_cell (2 columns)
        data.items1.sections.getOrNull(0)?.let { section ->
            // Section 1 Header: section_header
            section.header?.let { headerData ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    val headerViewModel: SectionHeaderViewModel = viewModel(key = "section_header_header_0_${viewModel.hashCode()}")
                    LaunchedEffect(headerData.data) {
                        headerViewModel.updateData(headerData.data)
                    }
                    SectionHeaderView(
                        viewModel = headerViewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            section.cells?.let { cellData ->
                items(cellData.data.size) { cellIndex ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopStart
                    ) {
                        val currentCellData = cellData.data[cellIndex]
                        val cellViewModel: BasicCellViewModel = viewModel(key = "basic_cell_cell_0_${cellIndex}_${viewModel.hashCode()}")
                        LaunchedEffect(currentCellData) {
                            cellViewModel.updateData(currentCellData)
                        }
                        BasicCellView(
                            viewModel = cellViewModel,
                            modifier = Modifier
                        )
                    }
                }
            }
            // Section 1 Footer: section_footer
            section.footer?.let { footerData ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    val footerViewModel: SectionFooterViewModel = viewModel(key = "section_footer_footer_0_${viewModel.hashCode()}")
                    LaunchedEffect(footerData.data) {
                        footerViewModel.updateData(footerData.data)
                    }
                    SectionFooterView(
                        viewModel = footerViewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
    val resolved_text3 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 20.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.collection_test_grid_collection_with_multiple_c),
        color = colorResource(R.color.black),
        fontFamily = resolved_text3.family,
        fontWeight = resolved_text3.weight,
        fontSize = resolved_text3.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text3.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 26.0.sp),
        modifier = Modifier.padding(bottom = 12.dp)
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .padding(bottom = 20.dp)
            .requiredHeight(400.dp)
    ) {
        // Section 1: image_cell (3 columns)
        data.mixedItems.sections.getOrNull(0)?.let { section ->
            // Section 1 Header: grid_header
            section.header?.let { headerData ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    val headerViewModel: GridHeaderViewModel = viewModel(key = "grid_header_header_0_${viewModel.hashCode()}")
                    LaunchedEffect(headerData.data) {
                        headerViewModel.updateData(headerData.data)
                    }
                    GridHeaderView(
                        viewModel = headerViewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            section.cells?.let { cellData ->
                items(cellData.data.size) { cellIndex ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopStart
                    ) {
                        val currentCellData = cellData.data[cellIndex]
                        val cellViewModel: ImageCellViewModel = viewModel(key = "image_cell_cell_0_${cellIndex}_${viewModel.hashCode()}")
                        LaunchedEffect(currentCellData) {
                            cellViewModel.updateData(currentCellData)
                        }
                        ImageCellView(
                            viewModel = cellViewModel,
                            modifier = Modifier
                        )
                    }
                }
            }
            // Section 1 Footer: grid_footer
            section.footer?.let { footerData ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    val footerViewModel: GridFooterViewModel = viewModel(key = "grid_footer_footer_0_${viewModel.hashCode()}")
                    LaunchedEffect(footerData.data) {
                        footerViewModel.updateData(footerData.data)
                    }
                    GridFooterView(
                        viewModel = footerViewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
    val resolved_text4 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 20.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.collection_test_horizontal_collection),
        color = colorResource(R.color.black),
        fontFamily = resolved_text4.family,
        fontWeight = resolved_text4.weight,
        fontSize = resolved_text4.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text4.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 26.0.sp),
        modifier = Modifier.padding(bottom = 12.dp)
    )
    val section0 = data.horizontalItems.sections.getOrNull(0)
    val cellData0 = section0?.cells
    CollectionStack(
        mode = CollectionStackMode.LAZY,
        axis = CollectionStackAxis.HORIZONTAL,
        modifier = Modifier
                                .padding(bottom = 20.dp)
                                .requiredHeight(150.dp),
        lazyContent = {
            // Section 1: horizontal_card
            if (section0 != null) {
                // Section 1 Header: horizontal_header
                section0.header?.let { headerData ->
                    item {
                        val headerViewModel: HorizontalHeaderViewModel = viewModel(key = "horizontal_header_header_0_${viewModel.hashCode()}")
                        LaunchedEffect(headerData.data) { headerViewModel.updateData(headerData.data) }
                        HorizontalHeaderView(viewModel = headerViewModel, modifier = Modifier.fillMaxWidth())
                    }
                }
                if (cellData0 != null) {
                    items(cellData0.data.size) { cellIndex ->
                        val currentCellData = cellData0.data[cellIndex]
                        val cellViewModel: HorizontalCardViewModel = viewModel(key = "horizontal_card_cell_0_${cellIndex}_${viewModel.hashCode()}")
                        LaunchedEffect(currentCellData) { cellViewModel.updateData(currentCellData) }
                        HorizontalCardView(
                            viewModel = cellViewModel,
                            modifier = Modifier
                        )
                    }
                }
            }
        },
        eagerContent = {
            // Section 1: horizontal_card
            if (section0 != null) {
                section0.header?.let { headerData ->
                    val headerViewModel: HorizontalHeaderViewModel = viewModel(key = "horizontal_header_header_0_${viewModel.hashCode()}")
                    LaunchedEffect(headerData.data) { headerViewModel.updateData(headerData.data) }
                    HorizontalHeaderView(viewModel = headerViewModel, modifier = Modifier.fillMaxWidth())
                }
                if (cellData0 != null) {
                    cellData0.data.forEachIndexed { cellIndex, _ ->
                        val currentCellData = cellData0.data[cellIndex]
                        val cellViewModel: HorizontalCardViewModel = viewModel(key = "horizontal_card_cell_0_${cellIndex}_${viewModel.hashCode()}")
                        LaunchedEffect(currentCellData) { cellViewModel.updateData(currentCellData) }
                        HorizontalCardView(
                            viewModel = cellViewModel,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    )
    val resolved_text5 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 20.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.collection_test_sectioned_collection),
        color = colorResource(R.color.black),
        fontFamily = resolved_text5.family,
        fontWeight = resolved_text5.weight,
        fontSize = resolved_text5.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text5.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 26.0.sp),
        modifier = Modifier.padding(bottom = 12.dp)
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .padding(bottom = 20.dp)
            .requiredHeight(400.dp)
    ) {
        // Section 1: product_cell (2 columns)
        data.sectionedItems.sections.getOrNull(0)?.let { section ->
            // Section 1 Header: category_header
            section.header?.let { headerData ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    val headerViewModel: CategoryHeaderViewModel = viewModel(key = "category_header_header_0_${viewModel.hashCode()}")
                    LaunchedEffect(headerData.data) {
                        headerViewModel.updateData(headerData.data)
                    }
                    CategoryHeaderView(
                        viewModel = headerViewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            section.cells?.let { cellData ->
                items(cellData.data.size) { cellIndex ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopStart
                    ) {
                        val currentCellData = cellData.data[cellIndex]
                        val cellViewModel: ProductCellViewModel = viewModel(key = "product_cell_cell_0_${cellIndex}_${viewModel.hashCode()}")
                        LaunchedEffect(currentCellData) {
                            cellViewModel.updateData(currentCellData)
                        }
                        ProductCellView(
                            viewModel = cellViewModel,
                            modifier = Modifier
                        )
                    }
                }
            }
            // Section 1 Footer: category_footer
            section.footer?.let { footerData ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    val footerViewModel: CategoryFooterViewModel = viewModel(key = "category_footer_footer_0_${viewModel.hashCode()}")
                    LaunchedEffect(footerData.data) {
                        footerViewModel.updateData(footerData.data)
                    }
                    CategoryFooterView(
                        viewModel = footerViewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
    val resolved_text6 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 20.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.collection_test_multisection_collection),
        color = colorResource(R.color.black),
        fontFamily = resolved_text6.family,
        fontWeight = resolved_text6.weight,
        fontSize = resolved_text6.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text6.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 26.0.sp),
        modifier = Modifier.padding(bottom = 12.dp)
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(12),
        modifier = Modifier.requiredHeight(600.dp)
    ) {
        // Section 1: product_cell (3 columns)
        data.multiSectionItems.sections.getOrNull(0)?.let { section ->
            // Section 1 Header: category_header
            section.header?.let { headerData ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    val headerViewModel: CategoryHeaderViewModel = viewModel(key = "category_header_header_0_${viewModel.hashCode()}")
                    LaunchedEffect(headerData.data) {
                        headerViewModel.updateData(headerData.data)
                    }
                    CategoryHeaderView(
                        viewModel = headerViewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            section.cells?.let { cellData ->
                items(cellData.data.size, span = { GridItemSpan(4) }) { cellIndex ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopStart
                    ) {
                        val currentCellData = cellData.data[cellIndex]
                        val cellViewModel: ProductCellViewModel = viewModel(key = "product_cell_cell_0_${cellIndex}_${viewModel.hashCode()}")
                        LaunchedEffect(currentCellData) {
                            cellViewModel.updateData(currentCellData)
                        }
                        ProductCellView(
                            viewModel = cellViewModel,
                            modifier = Modifier
                        )
                    }
                }
            }
            // Section 1 Footer: category_footer
            section.footer?.let { footerData ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    val footerViewModel: CategoryFooterViewModel = viewModel(key = "category_footer_footer_0_${viewModel.hashCode()}")
                    LaunchedEffect(footerData.data) {
                        footerViewModel.updateData(footerData.data)
                    }
                    CategoryFooterView(
                        viewModel = footerViewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        // Section 2: feature_cell (2 columns)
        data.multiSectionItems.sections.getOrNull(1)?.let { section ->
            // Section 2 Header: featured_header
            section.header?.let { headerData ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    val headerViewModel: FeaturedHeaderViewModel = viewModel(key = "featured_header_header_1_${viewModel.hashCode()}")
                    LaunchedEffect(headerData.data) {
                        headerViewModel.updateData(headerData.data)
                    }
                    FeaturedHeaderView(
                        viewModel = headerViewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            section.cells?.let { cellData ->
                items(cellData.data.size, span = { GridItemSpan(6) }) { cellIndex ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopStart
                    ) {
                        val currentCellData = cellData.data[cellIndex]
                        val cellViewModel: FeatureCellViewModel = viewModel(key = "feature_cell_cell_1_${cellIndex}_${viewModel.hashCode()}")
                        LaunchedEffect(currentCellData) {
                            cellViewModel.updateData(currentCellData)
                        }
                        FeatureCellView(
                            viewModel = cellViewModel,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
        // Section 3: image_cell (4 columns)
        data.multiSectionItems.sections.getOrNull(2)?.let { section ->
            // Section 3 Header: grid_header
            section.header?.let { headerData ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    val headerViewModel: GridHeaderViewModel = viewModel(key = "grid_header_header_2_${viewModel.hashCode()}")
                    LaunchedEffect(headerData.data) {
                        headerViewModel.updateData(headerData.data)
                    }
                    GridHeaderView(
                        viewModel = headerViewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            section.cells?.let { cellData ->
                items(cellData.data.size, span = { GridItemSpan(3) }) { cellIndex ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopStart
                    ) {
                        val currentCellData = cellData.data[cellIndex]
                        val cellViewModel: ImageCellViewModel = viewModel(key = "image_cell_cell_2_${cellIndex}_${viewModel.hashCode()}")
                        LaunchedEffect(currentCellData) {
                            cellViewModel.updateData(currentCellData)
                        }
                        ImageCellView(
                            viewModel = cellViewModel,
                            modifier = Modifier
                        )
                    }
                }
            }
            // Section 3 Footer: grid_footer
            section.footer?.let { footerData ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    val footerViewModel: GridFooterViewModel = viewModel(key = "grid_footer_footer_2_${viewModel.hashCode()}")
                    LaunchedEffect(footerData.data) {
                        footerViewModel.updateData(footerData.data)
                    }
                    GridFooterView(
                        viewModel = footerViewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun Section3(
    data: CollectionTestData,
    viewModel: CollectionTestViewModel
) {
    Button(
        onClick = { data.toggleDynamicMode?.invoke() },
        modifier = Modifier
            .padding(top = 20.dp)
            .wrapContentWidth()
            .requiredHeight(44.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
        colors = ButtonDefaults.buttonColors(
                                containerColor = Color(android.graphics.Color.parseColor("#5856D6")),
                                disabledContainerColor = Color(android.graphics.Color.parseColor("#5856D6")).copy(alpha = 0.5f),
                                contentColor = colorResource(R.color.white),
                                disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                            )
    ) {
        val resolved_button1 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button1.family,
            fontWeight = resolved_button1.weight,
            fontSize = resolved_button1.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button1.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section4(
    data: CollectionTestData,
    viewModel: CollectionTestViewModel,
    modifier: Modifier
) {
    Box(
        modifier = modifier.background(colorResource(R.color.white_12))
    ) {
        LazyColumn(
            modifier = Modifier.imePadding()
        ) {
            item {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Section0(data, viewModel)
                Section1(data, viewModel)
                Section2(data, viewModel)
                Section3(data, viewModel)
            }
            }
        }
    }
}
// >>> RESPONSIVE_HELPERS_END