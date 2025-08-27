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
import com.kotlinjsonui.core.SafeDynamicView
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
            data = data.toMap(viewModel),
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
        modifier = Modifier.background(Color(android.graphics.Color.parseColor("#F5F5F7")))
    ) {
        LazyColumn(
        ) {
            item {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "CollectionView Test",
                    fontSize = 28.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Text(
                    text = "Basic Collection with Headers & Footers",
                    fontSize = 20.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .height(300.dp)
                        .padding(bottom = 20.dp)
                ) {
                    // Section 1: BasicCell (2 columns)
                    data.items1.sections.getOrNull(0)?.let { section ->
                        // Section 1 Header: SectionHeader
                        section.header?.let { headerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val data = SectionHeaderData.fromMap(headerData.data)
                                SectionHeaderView(
                                    data = data,
                                    viewModel = viewModel(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        section.cells?.let { cellData ->
                            items(cellData.data.size) { cellIndex ->
                                val item = cellData.data[cellIndex]
                                when (item) {
                                    is Map<*, *> -> {
                                        val data = BasicCellData.fromMap(item as Map<String, Any>)
                                        BasicCellView(
                                            data = data,
                                            viewModel = viewModel(),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        )
                                    }
                                    else -> {
                                        // Unsupported item type
                                    }
                                }
                            }
                        }
                        // Section 1 Footer: SectionFooter
                        section.footer?.let { footerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val data = SectionFooterData.fromMap(footerData.data)
                                SectionFooterView(
                                    data = data,
                                    viewModel = viewModel(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
                Text(
                    text = "Grid Collection with Multiple Cell Types",
                    fontSize = 20.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .height(400.dp)
                        .padding(bottom = 20.dp)
                ) {
                    // Section 1: ImageCell (3 columns)
                    data.mixedItems.sections.getOrNull(0)?.let { section ->
                        // Section 1 Header: GridHeader
                        section.header?.let { headerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val data = GridHeaderData.fromMap(headerData.data)
                                GridHeaderView(
                                    data = data,
                                    viewModel = viewModel(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        section.cells?.let { cellData ->
                            items(cellData.data.size) { cellIndex ->
                                val item = cellData.data[cellIndex]
                                when (item) {
                                    is Map<*, *> -> {
                                        val data = ImageCellData.fromMap(item as Map<String, Any>)
                                        ImageCellView(
                                            data = data,
                                            viewModel = viewModel(),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        )
                                    }
                                    else -> {
                                        // Unsupported item type
                                    }
                                }
                            }
                        }
                        // Section 1 Footer: GridFooter
                        section.footer?.let { footerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val data = GridFooterData.fromMap(footerData.data)
                                GridFooterView(
                                    data = data,
                                    viewModel = viewModel(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
                Text(
                    text = "Horizontal Collection",
                    fontSize = 20.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(1),
                    modifier = Modifier
                        .height(150.dp)
                        .padding(bottom = 20.dp)
                ) {
                    // Section 1: HorizontalCard (1 columns)
                    data.horizontalItems.sections.getOrNull(0)?.let { section ->
                        // Section 1 Header: HorizontalHeader
                        section.header?.let { headerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val data = HorizontalHeaderData.fromMap(headerData.data)
                                HorizontalHeaderView(
                                    data = data,
                                    viewModel = viewModel(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        section.cells?.let { cellData ->
                            items(cellData.data.size) { cellIndex ->
                                val item = cellData.data[cellIndex]
                                when (item) {
                                    is Map<*, *> -> {
                                        val data = HorizontalCardData.fromMap(item as Map<String, Any>)
                                        HorizontalCardView(
                                            data = data,
                                            viewModel = viewModel(),
                                            modifier = Modifier
                                        )
                                    }
                                    else -> {
                                        // Unsupported item type
                                    }
                                }
                            }
                        }
                    }
                }
                Text(
                    text = "Sectioned Collection",
                    fontSize = 20.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .height(400.dp)
                        .padding(bottom = 20.dp)
                ) {
                    // Section 1: ProductCell (2 columns)
                    data.sectionedItems.sections.getOrNull(0)?.let { section ->
                        // Section 1 Header: CategoryHeader
                        section.header?.let { headerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val data = CategoryHeaderData.fromMap(headerData.data)
                                CategoryHeaderView(
                                    data = data,
                                    viewModel = viewModel(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        section.cells?.let { cellData ->
                            items(cellData.data.size) { cellIndex ->
                                val item = cellData.data[cellIndex]
                                when (item) {
                                    is Map<*, *> -> {
                                        val data = ProductCellData.fromMap(item as Map<String, Any>)
                                        ProductCellView(
                                            data = data,
                                            viewModel = viewModel(),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        )
                                    }
                                    else -> {
                                        // Unsupported item type
                                    }
                                }
                            }
                        }
                        // Section 1 Footer: CategoryFooter
                        section.footer?.let { footerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val data = CategoryFooterData.fromMap(footerData.data)
                                CategoryFooterView(
                                    data = data,
                                    viewModel = viewModel(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
                Text(
                    text = "Multi-Section Collection",
                    fontSize = 20.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(12),
                    modifier = Modifier.height(600.dp)
                ) {
                    // Section 1: ProductCell (3 columns)
                    data.multiSectionItems.sections.getOrNull(0)?.let { section ->
                        // Section 1 Header: CategoryHeader
                        section.header?.let { headerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val data = CategoryHeaderData.fromMap(headerData.data)
                                CategoryHeaderView(
                                    data = data,
                                    viewModel = viewModel(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        section.cells?.let { cellData ->
                            items(cellData.data.size, span = { GridItemSpan(4) }) { cellIndex ->
                                val item = cellData.data[cellIndex]
                                when (item) {
                                    is Map<*, *> -> {
                                        val data = ProductCellData.fromMap(item as Map<String, Any>)
                                        ProductCellView(
                                            data = data,
                                            viewModel = viewModel(),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        )
                                    }
                                    else -> {
                                        // Unsupported item type
                                    }
                                }
                            }
                        }
                        // Section 1 Footer: CategoryFooter
                        section.footer?.let { footerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val data = CategoryFooterData.fromMap(footerData.data)
                                CategoryFooterView(
                                    data = data,
                                    viewModel = viewModel(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    // Section 2: FeatureCell (2 columns)
                    data.multiSectionItems.sections.getOrNull(1)?.let { section ->
                        // Section 2 Header: FeaturedHeader
                        section.header?.let { headerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val data = FeaturedHeaderData.fromMap(headerData.data)
                                FeaturedHeaderView(
                                    data = data,
                                    viewModel = viewModel(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        section.cells?.let { cellData ->
                            items(cellData.data.size, span = { GridItemSpan(6) }) { cellIndex ->
                                val item = cellData.data[cellIndex]
                                when (item) {
                                    is Map<*, *> -> {
                                        val data = FeatureCellData.fromMap(item as Map<String, Any>)
                                        FeatureCellView(
                                            data = data,
                                            viewModel = viewModel(),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        )
                                    }
                                    else -> {
                                        // Unsupported item type
                                    }
                                }
                            }
                        }
                    }
                    // Section 3: ImageCell (4 columns)
                    data.multiSectionItems.sections.getOrNull(2)?.let { section ->
                        // Section 3 Header: GridHeader
                        section.header?.let { headerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val data = GridHeaderData.fromMap(headerData.data)
                                GridHeaderView(
                                    data = data,
                                    viewModel = viewModel(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        section.cells?.let { cellData ->
                            items(cellData.data.size, span = { GridItemSpan(3) }) { cellIndex ->
                                val item = cellData.data[cellIndex]
                                when (item) {
                                    is Map<*, *> -> {
                                        val data = ImageCellData.fromMap(item as Map<String, Any>)
                                        ImageCellView(
                                            data = data,
                                            viewModel = viewModel(),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        )
                                    }
                                    else -> {
                                        // Unsupported item type
                                    }
                                }
                            }
                        }
                        // Section 3 Footer: GridFooter
                        section.footer?.let { footerData ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val data = GridFooterData.fromMap(footerData.data)
                                GridFooterView(
                                    data = data,
                                    viewModel = viewModel(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
                Button(
                    onClick = { viewModel.toggleDynamicMode() },
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .wrapContentWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(android.graphics.Color.parseColor("#5856D6"))
                                        )
                ) {
                    Text(
                        text = "Dynamic: ${data.dynamicModeStatus}",
                        fontSize = 14.sp,
                        color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    )
                }
            }
            }
        }
    }    }
    // >>> GENERATED_CODE_END
}