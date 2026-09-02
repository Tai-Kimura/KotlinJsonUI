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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
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
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.dynamic.LocalSafeAreaConfig
import com.kotlinjsonui.dynamic.SafeAreaConfig
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun ConverterTestGeneratedView(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from converter_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
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
                Section5(data, viewModel)
                Section6(data, viewModel)
                Section7(data, viewModel)
                Section8(data, viewModel)
                // TabView with NavigationBar
                Section10(data, viewModel)
                Section11(data, viewModel)
                Section12(data, viewModel)
                Section13(data, viewModel)
                Section14(data, viewModel)
                Section15(data, viewModel)
                Section16(data, viewModel)
            }
            }
        }    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("converter_test")
    }
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
private fun Section1(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    val resolved_text1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = "${data.title}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text1.family,
        fontWeight = resolved_text1.weight,
        fontSize = resolved_text1.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text1.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 31.2.sp),
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
    val resolved_text2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.converter_test_gradientview_test),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text2.family,
        fontWeight = resolved_text2.weight,
        fontSize = resolved_text2.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text2.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
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
            .requiredHeight(50.dp)
            .background(Brush.linearGradient(listOf(Color(android.graphics.Color.parseColor("#FF0000")), Color(android.graphics.Color.parseColor("#00FF00")), Color(android.graphics.Color.parseColor("#0000FF")))))
    ) {
        val resolved_text3 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 16.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.converter_test_diagonal_gradient),
            color = colorResource(R.color.white),
            fontFamily = resolved_text3.family,
            fontWeight = resolved_text3.weight,
            fontSize = resolved_text3.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text3.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
            modifier = Modifier
        )
    }
}

@Composable
private fun Section4(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    val resolved_text4 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.converter_test_blurview_test),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text4.family,
        fontWeight = resolved_text4.weight,
        fontSize = resolved_text4.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text4.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section5(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    Box(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .requiredHeight(150.dp)
            .background(colorResource(R.color.medium_green_2))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            val resolved_text5 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = FontWeight.Bold,
                size = 24.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.converter_test_background_text),
                color = colorResource(R.color.white),
                fontFamily = resolved_text5.family,
                fontWeight = resolved_text5.weight,
                fontSize = resolved_text5.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text5.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 31.2.sp),
                modifier = Modifier.align(Alignment.Center)
            )
            val resolved_text6 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 16.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.converter_test_this_will_be_blurred),
                color = colorResource(R.color.light_orange),
                fontFamily = resolved_text6.family,
                fontWeight = resolved_text6.weight,
                fontSize = resolved_text6.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text6.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
                modifier = Modifier
                    .padding(top = 50.dp)
                    .padding(start = 20.dp)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(60.dp)
                .background(colorResource(R.color.black_2))
                .blur(20.dp)
                .align(Alignment.Center)
        ) {
            val resolved_text7 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = FontWeight.Bold,
                size = 18.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.converter_test_clear_text_on_blur_layer),
                color = colorResource(R.color.white),
                fontFamily = resolved_text7.family,
                fontWeight = resolved_text7.weight,
                fontSize = resolved_text7.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text7.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun Section6(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    val resolved_text8 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.converter_test_webview_test),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text8.family,
        fontWeight = resolved_text8.weight,
        fontSize = resolved_text8.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text8.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
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
            .requiredHeight(200.dp)
    )
}

@Composable
private fun Section8(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    val resolved_text9 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.converter_test_tabview_test),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text9.family,
        fontWeight = resolved_text9.weight,
        fontSize = resolved_text9.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text9.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
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
                            imageVector = if (selectedTab == 0) Icons.Filled.Circle else Icons.Outlined.Circle,
                            contentDescription = "Tab 1"
                        )
                    },
                    label = { Text("Tab 1") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Filled.Circle else Icons.Outlined.Circle,
                            contentDescription = "Tab 2"
                        )
                    },
                    label = { Text("Tab 2") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
    val resolved_text10 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.converter_test_collection_test_2),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text10.family,
        fontWeight = resolved_text10.weight,
        fontSize = resolved_text10.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text10.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section12_1_0(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel,
    cellData: com.kotlinjsonui.data.CollectionDataSection.CellData,
    cellIndex: Int
) {
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

@Composable
private fun Section12(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .requiredHeight(300.dp)
    ) {
        // Section 1: converter_test_cell (3 columns)
        data.items.sections.getOrNull(0)?.let { section ->
            section.cells?.let { cellData ->
                items(cellData.data.size) { cellIndex ->
                    Section12_1_0(data, viewModel, cellData, cellIndex)
                }
            }
        }
    }
}

@Composable
private fun Section13(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    val resolved_text11 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.converter_test_image_test),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text11.family,
        fontWeight = resolved_text11.weight,
        fontSize = resolved_text11.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text11.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
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
            .size(100.dp, 100.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun Section15(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    val resolved_text12 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.converter_test_networkimage_test),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text12.family,
        fontWeight = resolved_text12.weight,
        fontSize = resolved_text12.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text12.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
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
            .requiredWidth(200.dp)
            .requiredHeight(150.dp)
            .clip(RoundedCornerShape(10.dp)),
        error = painterResource(R.drawable.photo),
        fallback = painterResource(R.drawable.photo)
    )
}
// >>> RESPONSIVE_HELPERS_END