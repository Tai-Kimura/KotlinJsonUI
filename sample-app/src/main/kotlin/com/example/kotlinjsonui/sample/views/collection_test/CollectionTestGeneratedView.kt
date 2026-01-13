package com.example.kotlinjsonui.sample.views.collection_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.CollectionTestData
import com.example.kotlinjsonui.sample.viewmodels.CollectionTestViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.border
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.views.product_cell.ProductCellView
import com.example.kotlinjsonui.sample.data.ProductCellData
import com.example.kotlinjsonui.sample.viewmodels.ProductCellViewModel
import com.example.kotlinjsonui.sample.views.simple_cell.SimpleCellView
import com.example.kotlinjsonui.sample.data.SimpleCellData
import com.example.kotlinjsonui.sample.viewmodels.SimpleCellViewModel
import com.example.kotlinjsonui.sample.views.basic_cell.BasicCellView
import com.example.kotlinjsonui.sample.data.BasicCellData
import com.example.kotlinjsonui.sample.viewmodels.BasicCellViewModel
import com.example.kotlinjsonui.sample.views.image_cell.ImageCellView
import com.example.kotlinjsonui.sample.data.ImageCellData
import com.example.kotlinjsonui.sample.viewmodels.ImageCellViewModel
import com.example.kotlinjsonui.sample.views.feature_cell.FeatureCellView
import com.example.kotlinjsonui.sample.data.FeatureCellData
import com.example.kotlinjsonui.sample.viewmodels.FeatureCellViewModel
import com.example.kotlinjsonui.sample.views.horizontal_card.HorizontalCardView
import com.example.kotlinjsonui.sample.data.HorizontalCardData
import com.example.kotlinjsonui.sample.viewmodels.HorizontalCardViewModel
import com.example.kotlinjsonui.sample.views.section_header.SectionHeaderView
import com.example.kotlinjsonui.sample.data.SectionHeaderData
import com.example.kotlinjsonui.sample.viewmodels.SectionHeaderViewModel
import com.example.kotlinjsonui.sample.views.section_footer.SectionFooterView
import com.example.kotlinjsonui.sample.data.SectionFooterData
import com.example.kotlinjsonui.sample.viewmodels.SectionFooterViewModel
import com.example.kotlinjsonui.sample.views.grid_header.GridHeaderView
import com.example.kotlinjsonui.sample.data.GridHeaderData
import com.example.kotlinjsonui.sample.viewmodels.GridHeaderViewModel
import com.example.kotlinjsonui.sample.views.grid_footer.GridFooterView
import com.example.kotlinjsonui.sample.data.GridFooterData
import com.example.kotlinjsonui.sample.viewmodels.GridFooterViewModel
import com.example.kotlinjsonui.sample.views.horizontal_header.HorizontalHeaderView
import com.example.kotlinjsonui.sample.data.HorizontalHeaderData
import com.example.kotlinjsonui.sample.viewmodels.HorizontalHeaderViewModel
import com.example.kotlinjsonui.sample.views.category_header.CategoryHeaderView
import com.example.kotlinjsonui.sample.data.CategoryHeaderData
import com.example.kotlinjsonui.sample.viewmodels.CategoryHeaderViewModel
import com.example.kotlinjsonui.sample.views.category_footer.CategoryFooterView
import com.example.kotlinjsonui.sample.data.CategoryFooterData
import com.example.kotlinjsonui.sample.viewmodels.CategoryFooterViewModel
import com.example.kotlinjsonui.sample.views.featured_header.FeaturedHeaderView
import com.example.kotlinjsonui.sample.data.FeaturedHeaderData
import com.example.kotlinjsonui.sample.viewmodels.FeaturedHeaderViewModel
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.ui.res.stringResource
import com.example.kotlinjsonui.sample.R
import androidx.compose.ui.res.colorResource
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.text.TextStyle
import com.kotlinjsonui.core.Configuration

@Composable
fun CollectionTestGeneratedView(
    data: CollectionTestData,
    viewModel: CollectionTestViewModel
) {
    // Generated Compose code from collection_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "collection_test",
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
        Box(
        modifier = Modifier.background(colorResource(R.color.white_12))
    ) {
        LazyColumn(
            modifier = Modifier.imePadding()
        ) {
            item {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.collection_test_collectionview_test),
                    fontSize = 28.sp,
                    color = colorResource(R.color.black),
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(lineHeight = 28.sp),
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Text(
                    text = stringResource(R.string.collection_test_basic_collection_with_headers_f),
                    fontSize = 20.sp,
                    color = colorResource(R.color.black),
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(lineHeight = 20.sp),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .height(300.dp)
                        .padding(bottom = 20.dp)
                ) {
                    // Section 1: BasicCell (2 columns)
                    data.items1?.sections?.getOrNull(0)?.let { section ->
                        // Section 1 Header: SectionHeader
                        section.header?.let { headerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val headerViewModel: SectionHeaderViewModel = viewModel(key = "SectionHeader_header_0")
                                headerViewModel.updateData(headerData.data)
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
                                    val cellViewModel: BasicCellViewModel = viewModel(key = "BasicCell_cell_$cellIndex")
                                    cellViewModel.updateData(cellData.data[cellIndex])
                                    BasicCellView(
                                        viewModel = cellViewModel,
                                        modifier = Modifier
                                    )
                                }
                            }
                        }
                        // Section 1 Footer: SectionFooter
                        section.footer?.let { footerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val footerViewModel: SectionFooterViewModel = viewModel(key = "SectionFooter_footer_0")
                                footerViewModel.updateData(footerData.data)
                                SectionFooterView(
                                    viewModel = footerViewModel,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.collection_test_grid_collection_with_multiple_c),
                    fontSize = 20.sp,
                    color = colorResource(R.color.black),
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(lineHeight = 20.sp),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .height(400.dp)
                        .padding(bottom = 20.dp)
                ) {
                    // Section 1: ImageCell (3 columns)
                    data.mixedItems?.sections?.getOrNull(0)?.let { section ->
                        // Section 1 Header: GridHeader
                        section.header?.let { headerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val headerViewModel: GridHeaderViewModel = viewModel(key = "GridHeader_header_0")
                                headerViewModel.updateData(headerData.data)
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
                                    val cellViewModel: ImageCellViewModel = viewModel(key = "ImageCell_cell_$cellIndex")
                                    cellViewModel.updateData(cellData.data[cellIndex])
                                    ImageCellView(
                                        viewModel = cellViewModel,
                                        modifier = Modifier
                                    )
                                }
                            }
                        }
                        // Section 1 Footer: GridFooter
                        section.footer?.let { footerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val footerViewModel: GridFooterViewModel = viewModel(key = "GridFooter_footer_0")
                                footerViewModel.updateData(footerData.data)
                                GridFooterView(
                                    viewModel = footerViewModel,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.collection_test_horizontal_collection),
                    fontSize = 20.sp,
                    color = colorResource(R.color.black),
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(lineHeight = 20.sp),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(1),
                    modifier = Modifier
                        .height(150.dp)
                        .padding(bottom = 20.dp)
                ) {
                    // Section 1: HorizontalCard (1 columns)
                    data.horizontalItems?.sections?.getOrNull(0)?.let { section ->
                        // Section 1 Header: HorizontalHeader
                        section.header?.let { headerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val headerViewModel: HorizontalHeaderViewModel = viewModel(key = "HorizontalHeader_header_0")
                                headerViewModel.updateData(headerData.data)
                                HorizontalHeaderView(
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
                                    val cellViewModel: HorizontalCardViewModel = viewModel(key = "HorizontalCard_cell_$cellIndex")
                                    cellViewModel.updateData(cellData.data[cellIndex])
                                    HorizontalCardView(
                                        viewModel = cellViewModel,
                                        modifier = Modifier
                                    )
                                }
                            }
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.collection_test_sectioned_collection),
                    fontSize = 20.sp,
                    color = colorResource(R.color.black),
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(lineHeight = 20.sp),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .height(400.dp)
                        .padding(bottom = 20.dp)
                ) {
                    // Section 1: ProductCell (2 columns)
                    data.sectionedItems?.sections?.getOrNull(0)?.let { section ->
                        // Section 1 Header: CategoryHeader
                        section.header?.let { headerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val headerViewModel: CategoryHeaderViewModel = viewModel(key = "CategoryHeader_header_0")
                                headerViewModel.updateData(headerData.data)
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
                                    val cellViewModel: ProductCellViewModel = viewModel(key = "ProductCell_cell_$cellIndex")
                                    cellViewModel.updateData(cellData.data[cellIndex])
                                    ProductCellView(
                                        viewModel = cellViewModel,
                                        modifier = Modifier
                                    )
                                }
                            }
                        }
                        // Section 1 Footer: CategoryFooter
                        section.footer?.let { footerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val footerViewModel: CategoryFooterViewModel = viewModel(key = "CategoryFooter_footer_0")
                                footerViewModel.updateData(footerData.data)
                                CategoryFooterView(
                                    viewModel = footerViewModel,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.collection_test_multisection_collection),
                    fontSize = 20.sp,
                    color = colorResource(R.color.black),
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(lineHeight = 20.sp),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(12),
                    modifier = Modifier.height(600.dp)
                ) {
                    // Section 1: ProductCell (3 columns)
                    data.multiSectionItems?.sections?.getOrNull(0)?.let { section ->
                        // Section 1 Header: CategoryHeader
                        section.header?.let { headerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val headerViewModel: CategoryHeaderViewModel = viewModel(key = "CategoryHeader_header_0")
                                headerViewModel.updateData(headerData.data)
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
                                    val cellViewModel: ProductCellViewModel = viewModel(key = "ProductCell_cell_$cellIndex")
                                    cellViewModel.updateData(cellData.data[cellIndex])
                                    ProductCellView(
                                        viewModel = cellViewModel,
                                        modifier = Modifier
                                    )
                                }
                            }
                        }
                        // Section 1 Footer: CategoryFooter
                        section.footer?.let { footerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val footerViewModel: CategoryFooterViewModel = viewModel(key = "CategoryFooter_footer_0")
                                footerViewModel.updateData(footerData.data)
                                CategoryFooterView(
                                    viewModel = footerViewModel,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    // Section 2: FeatureCell (2 columns)
                    data.multiSectionItems?.sections?.getOrNull(1)?.let { section ->
                        // Section 2 Header: FeaturedHeader
                        section.header?.let { headerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val headerViewModel: FeaturedHeaderViewModel = viewModel(key = "FeaturedHeader_header_1")
                                headerViewModel.updateData(headerData.data)
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
                                    val cellViewModel: FeatureCellViewModel = viewModel(key = "FeatureCell_cell_$cellIndex")
                                    cellViewModel.updateData(cellData.data[cellIndex])
                                    FeatureCellView(
                                        viewModel = cellViewModel,
                                        modifier = Modifier
                                    )
                                }
                            }
                        }
                    }
                    // Section 3: ImageCell (4 columns)
                    data.multiSectionItems?.sections?.getOrNull(2)?.let { section ->
                        // Section 3 Header: GridHeader
                        section.header?.let { headerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val headerViewModel: GridHeaderViewModel = viewModel(key = "GridHeader_header_2")
                                headerViewModel.updateData(headerData.data)
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
                                    val cellViewModel: ImageCellViewModel = viewModel(key = "ImageCell_cell_$cellIndex")
                                    cellViewModel.updateData(cellData.data[cellIndex])
                                    ImageCellView(
                                        viewModel = cellViewModel,
                                        modifier = Modifier
                                    )
                                }
                            }
                        }
                        // Section 3 Footer: GridFooter
                        section.footer?.let { footerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val footerViewModel: GridFooterViewModel = viewModel(key = "GridFooter_footer_2")
                                footerViewModel.updateData(footerData.data)
                                GridFooterView(
                                    viewModel = footerViewModel,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
                Button(
                    onClick = { data.toggleDynamicMode?.invoke() },
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .wrapContentWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_blue_3),
                                            contentColor = colorResource(R.color.white)
                                        )
                ) {
                    Text(
                        text = "${data.dynamicModeStatus}",
                        fontSize = 14.sp
                    )
                }
            }
            }
        }
    }    }
    // >>> GENERATED_CODE_END
}