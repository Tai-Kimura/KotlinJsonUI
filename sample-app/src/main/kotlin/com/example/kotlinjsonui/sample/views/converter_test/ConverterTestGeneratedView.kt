package com.example.kotlinjsonui.sample.views.converter_test

import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.ConverterTestCellData
import com.example.kotlinjsonui.sample.data.ConverterTestData
import com.example.kotlinjsonui.sample.viewmodels.ConverterTestCellViewModel
import com.example.kotlinjsonui.sample.viewmodels.ConverterTestViewModel
import com.example.kotlinjsonui.sample.views.converter_test_cell.ConverterTestCellView
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont
import com.kotlinjsonui.dynamic.LocalSafeAreaConfig
import com.kotlinjsonui.dynamic.SafeAreaConfig

@Composable
fun ConverterTestGeneratedView(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from converter_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "converter_test",
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
        modifier = modifier
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
            Section0(data, viewModel)
            Section1(data, viewModel)
            Section2(data, viewModel)
            Section3(data, viewModel)
            Section4(data, viewModel)
            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(colorResource(R.color.medium_green_2))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    val resolved_text431 = Configuration.Font.resolve(FontSpec(
                        family = null,
                        weight = FontWeight.Bold,
                        size = 24.sp,
                        italic = false
                    ))
                    Text(
                        text = stringResource(R.string.converter_test_background_text),
                        color = colorResource(R.color.white),
                        fontFamily = resolved_text431.family,
                        fontWeight = resolved_text431.weight,
                        fontSize = resolved_text431.size ?: TextUnit.Unspecified,
                        fontStyle = resolved_text431.style ?: FontStyle.Normal,
                        style = TextStyle(lineHeight = 31.2.sp),
                        modifier = Modifier.align(Alignment.Center)
                    )
                    val resolved_text432 = Configuration.Font.resolve(FontSpec(
                        family = null,
                        weight = null,
                        size = 16.sp,
                        italic = false
                    ))
                    Text(
                        text = stringResource(R.string.converter_test_this_will_be_blurred),
                        color = colorResource(R.color.light_orange),
                        fontFamily = resolved_text432.family,
                        fontWeight = resolved_text432.weight,
                        fontSize = resolved_text432.size ?: TextUnit.Unspecified,
                        fontStyle = resolved_text432.style ?: FontStyle.Normal,
                        style = TextStyle(lineHeight = 20.8.sp),
                        modifier = Modifier
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
                    val resolved_text433 = Configuration.Font.resolve(FontSpec(
                        family = null,
                        weight = FontWeight.Bold,
                        size = 18.sp,
                        italic = false
                    ))
                    Text(
                        text = stringResource(R.string.converter_test_clear_text_on_blur_layer),
                        color = colorResource(R.color.white),
                        fontFamily = resolved_text433.family,
                        fontWeight = resolved_text433.weight,
                        fontSize = resolved_text433.size ?: TextUnit.Unspecified,
                        fontStyle = resolved_text433.style ?: FontStyle.Normal,
                        style = TextStyle(lineHeight = 23.4.sp),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            Section6(data, viewModel)
            Section7(data, viewModel)
            Section8(data, viewModel)
            // TabView with NavigationBar
            Section10(data, viewModel)
            Section11(data, viewModel)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                // Section 1: converter_test_cell (3 columns)
                data.items?.sections?.getOrNull(0)?.let { section ->
                    section.cells?.let { cellData ->
                        items(cellData.data.size) { cellIndex ->
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.TopStart
                            ) {
                                val currentCellData = cellData.data[cellIndex]
                                val cellViewModel: ConverterTestCellViewModel = viewModel(key = "converter_test_cell_cell_0_${cellIndex}_${viewModel.hashCode()}")
                                LaunchedEffect(currentCellData) {
                                    cellViewModel.updateData(currentCellData)
                                }
                                ConverterTestCellView(
                                    viewModel = cellViewModel,
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                }
            }
            Section13(data, viewModel)
            Section14(data, viewModel)
            Section15(data, viewModel)
            Section16(data, viewModel)
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    Button(
        onClick = { data.toggleDynamicMode?.invoke() },
        modifier = Modifier
            .wrapContentWidth()
            .height(44.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(android.graphics.Color.parseColor("#5856D6")),
                            contentColor = colorResource(R.color.white)
                        )
    ) {
        val resolved_button27 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button27.family,
            fontWeight = resolved_button27.weight,
            fontSize = resolved_button27.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button27.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section1(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    val resolved_text427 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = "${data.title}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text427.family,
        fontWeight = resolved_text427.weight,
        fontSize = resolved_text427.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text427.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 31.2.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(bottom = 20.dp)
    )
}

@Composable
private fun Section2(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    val resolved_text428 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.converter_test_gradientview_test),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text428.family,
        fontWeight = resolved_text428.weight,
        fontSize = resolved_text428.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text428.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 10.dp)
    )
}

@Composable
private fun Section3(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    Box(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .height(50.dp)
            .background(Brush.linearGradient(listOf(Color(android.graphics.Color.parseColor("#FF0000")), Color(android.graphics.Color.parseColor("#00FF00")), Color(android.graphics.Color.parseColor("#0000FF")))))
    ) {
        val resolved_text429 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 16.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.converter_test_diagonal_gradient),
            color = colorResource(R.color.white),
            fontFamily = resolved_text429.family,
            fontWeight = resolved_text429.weight,
            fontSize = resolved_text429.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text429.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 20.8.sp),
            modifier = Modifier
        )
    }
}

@Composable
private fun Section4(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    val resolved_text430 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.converter_test_blurview_test),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text430.family,
        fontWeight = resolved_text430.weight,
        fontSize = resolved_text430.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text430.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section6(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    val resolved_text434 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.converter_test_webview_test),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text434.family,
        fontWeight = resolved_text434.weight,
        fontSize = resolved_text434.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text434.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section7(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()
                loadUrl("https://www.example.com")
            }
        },
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .height(200.dp)
    )
}

@Composable
private fun Section8(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    val resolved_text435 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.converter_test_tabview_test),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text435.family,
        fontWeight = resolved_text435.weight,
        fontSize = resolved_text435.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text435.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section10(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
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
}

@Composable
private fun Section11(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    val resolved_text436 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.converter_test_collection_test_2),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text436.family,
        fontWeight = resolved_text436.weight,
        fontSize = resolved_text436.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text436.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section13(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    val resolved_text437 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.converter_test_image_test),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text437.family,
        fontWeight = resolved_text437.weight,
        fontSize = resolved_text437.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text437.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section14(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    Image(
        painter = painterResource(id = R.drawable.placeholder),
        contentDescription = "",
        modifier = Modifier
            .padding(top = 10.dp)
            .size(100.dp, 100.dp)
    )
}

@Composable
private fun Section15(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    val resolved_text438 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.converter_test_networkimage_test),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text438.family,
        fontWeight = resolved_text438.weight,
        fontSize = resolved_text438.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text438.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section16(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
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
// >>> RESPONSIVE_HELPERS_END