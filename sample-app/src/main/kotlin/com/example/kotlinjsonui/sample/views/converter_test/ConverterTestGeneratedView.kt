package com.example.kotlinjsonui.sample.views.converter_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.ConverterTestData
import com.example.kotlinjsonui.sample.viewmodels.ConverterTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.kotlinjsonui.sample.R
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.blur
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Arrangement
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.views.converter_test_cell.ConverterTestCellView
import com.example.kotlinjsonui.sample.data.ConverterTestCellData
import com.example.kotlinjsonui.sample.viewmodels.ConverterTestCellViewModel
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.colorResource
import androidx.compose.foundation.layout.imePadding
import com.kotlinjsonui.core.Configuration
import androidx.compose.ui.text.TextStyle
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.kotlinjsonui.dynamic.LocalSafeAreaConfig
import com.kotlinjsonui.dynamic.SafeAreaConfig
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun ConverterTestGeneratedView(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    // Generated Compose code from converter_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "converter_test",
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
                android.util.Log.e("DynamicView", "Error loading converter_test: \$error")
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
        LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(R.color.white))
            .imePadding()
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Button(
                onClick = { data.toggleDynamicMode?.invoke() },
                modifier = Modifier
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
            Text(
                text = "${data.title}",
                fontSize = 24.sp,
                color = colorResource(R.color.black),
                style = TextStyle(lineHeight = 24.sp),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(bottom = 20.dp)
            )
            Text(
                text = stringResource(R.string.converter_test_gradientview_test),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                style = TextStyle(lineHeight = 18.sp),
                modifier = Modifier.padding(top = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 10.dp)
                    .background(Brush.linearGradient(listOf(colorResource(R.color.dark_red), colorResource(R.color.dark_green_2), colorResource(R.color.dark_blue))))
            ) {
                Text(
                    text = stringResource(R.string.converter_test_diagonal_gradient),
                    fontSize = 16.sp,
                    color = colorResource(R.color.white),
                    style = TextStyle(lineHeight = 16.sp),
                    modifier = Modifier
                )
            }
            Text(
                text = stringResource(R.string.converter_test_blurview_test),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                style = TextStyle(lineHeight = 18.sp),
                modifier = Modifier.padding(top = 20.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(top = 10.dp)
                    .background(colorResource(R.color.medium_green_2))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Text(
                        text = stringResource(R.string.converter_test_background_text),
                        fontSize = 24.sp,
                        color = colorResource(R.color.white),
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(lineHeight = 24.sp),
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Text(
                        text = stringResource(R.string.converter_test_this_will_be_blurred),
                        fontSize = 16.sp,
                        color = colorResource(R.color.light_orange),
                        style = TextStyle(lineHeight = 16.sp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 50.dp)
                            .padding(start = 20.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .blur(20.dp)
                        .align(Alignment.Center)
                ) {
                    Text(
                        text = stringResource(R.string.converter_test_clear_text_on_blur_layer),
                        fontSize = 18.sp,
                        color = colorResource(R.color.white),
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(lineHeight = 18.sp),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            Text(
                text = stringResource(R.string.converter_test_webview_test),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                style = TextStyle(lineHeight = 18.sp),
                modifier = Modifier.padding(top = 20.dp)
            )
// TODO: Implement component type: WebView
            Text(
                text = stringResource(R.string.converter_test_tabview_test),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                style = TextStyle(lineHeight = 18.sp),
                modifier = Modifier.padding(top = 20.dp)
            )
            // TabView with NavigationBar
            var selectedTab by remember { mutableStateOf(0) }

            Scaffold(
                bottomBar = {
                    NavigationBar(
                    ) {
                        NavigationBarItem(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.Circle,
                                    contentDescription = "Tab 1"
                                )
                            },
                            label = { Text("Tab 1") },
                        )
                        NavigationBarItem(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.Circle,
                                    contentDescription = "Tab 2"
                                )
                            },
                            label = { Text("Tab 2") },
                        )
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                    CompositionLocalProvider(
                        LocalSafeAreaConfig provides SafeAreaConfig(ignoreBottom = true)
                    ) {
                        when (selectedTab) {
                            0 -> {
                                Text("Tab 1 content")
                            }
                            1 -> {
                                Text("Tab 2 content")
                            }
                        }
                    }
                }
            }
            Text(
                text = stringResource(R.string.converter_test_collection_test_2),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                style = TextStyle(lineHeight = 18.sp),
                modifier = Modifier.padding(top = 20.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(top = 10.dp)
            ) {
                // Section 1: ConverterTestCell (3 columns)
                data.items?.sections?.getOrNull(0)?.let { section ->
                    section.cells?.let { cellData ->
                        items(cellData.data.size) { cellIndex ->
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.TopStart
                            ) {
                                val cellViewModel: ConverterTestCellViewModel = viewModel(key = "ConverterTestCell_cell_$cellIndex")
                                cellViewModel.updateData(cellData.data[cellIndex])
                                ConverterTestCellView(
                                    viewModel = cellViewModel,
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                }
            }
            Text(
                text = stringResource(R.string.converter_test_image_test),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                style = TextStyle(lineHeight = 18.sp),
                modifier = Modifier.padding(top = 20.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.placeholder),
                contentDescription = "",
                modifier = Modifier
                    .padding(top = 10.dp)
                    .size(100.dp, 100.dp)
            )
            Text(
                text = stringResource(R.string.converter_test_networkimage_test),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                style = TextStyle(lineHeight = 18.sp),
                modifier = Modifier.padding(top = 20.dp)
            )
            AsyncImage(
                model = "https://picsum.photos/400/300",
                contentDescription = "Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .width(200.dp)
                    .height(150.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}