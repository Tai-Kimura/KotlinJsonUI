package com.example.kotlinjsonui.sample.views.scroll_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.ScrollTestData
import com.example.kotlinjsonui.sample.viewmodels.ScrollTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun ScrollTestGeneratedView(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from scroll_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "scroll_test",
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
                android.util.Log.e("DynamicView", "Error loading scroll_test: \$error")
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
        Column(
        modifier = modifier.background(colorResource(R.color.white))
    ) {
        Section0(data, viewModel)
        Section1(data, viewModel)
        Section2(data, viewModel)
        Section3(data, viewModel)
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text72 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 20.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_scrollview_test),
        color = colorResource(R.color.black),
        fontFamily = resolved_text72.family,
        fontWeight = resolved_text72.weight,
        fontSize = resolved_text72.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text72.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 26.0.sp),
        modifier = Modifier.padding(10.dp)
    )
}

@Composable
private fun Section1_0(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text73 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_no_vertical_indicator),
        color = colorResource(R.color.black),
        fontFamily = resolved_text73.family,
        fontWeight = resolved_text73.weight,
        fontSize = resolved_text73.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text73.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section1_1(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text74 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_item_1),
        color = colorResource(R.color.black),
        fontFamily = resolved_text74.family,
        fontWeight = resolved_text74.weight,
        fontSize = resolved_text74.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text74.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .background(colorResource(R.color.white_5))
            .padding(10.dp)
    )
}

@Composable
private fun Section1_2(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text75 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_item_2),
        color = colorResource(R.color.black),
        fontFamily = resolved_text75.family,
        fontWeight = resolved_text75.weight,
        fontSize = resolved_text75.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text75.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .background(colorResource(R.color.white_6))
            .padding(10.dp)
    )
}

@Composable
private fun Section1_3(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text76 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_item_3),
        color = colorResource(R.color.black),
        fontFamily = resolved_text76.family,
        fontWeight = resolved_text76.weight,
        fontSize = resolved_text76.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text76.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .background(colorResource(R.color.white_7))
            .padding(10.dp)
    )
}

@Composable
private fun Section1_4(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text77 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_item_4),
        color = colorResource(R.color.black),
        fontFamily = resolved_text77.family,
        fontWeight = resolved_text77.weight,
        fontSize = resolved_text77.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text77.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .background(colorResource(R.color.white_8))
            .padding(10.dp)
    )
}

@Composable
private fun Section1_5(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text78 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_item_5),
        color = colorResource(R.color.black),
        fontFamily = resolved_text78.family,
        fontWeight = resolved_text78.weight,
        fontSize = resolved_text78.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text78.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .background(colorResource(R.color.white_9))
            .padding(10.dp)
    )
}

@Composable
private fun Section1(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    LazyColumn(
        modifier = Modifier
            .height(200.dp)
            .background(colorResource(R.color.white_17))
            .imePadding()
    ) {
        item {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Section1_0(data, viewModel)
            Section1_1(data, viewModel)
            Section1_2(data, viewModel)
            Section1_3(data, viewModel)
            Section1_4(data, viewModel)
            Section1_5(data, viewModel)
        }
        }
    }
}

@Composable
private fun Section2(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    LazyColumn(
        modifier = Modifier
            .height(150.dp)
            .background(colorResource(R.color.white_17))
            .imePadding(),
        userScrollEnabled = false
    ) {
        item {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            val resolved_text79 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = FontWeight.Bold,
                size = 16.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.scroll_test_scroll_disabled),
                color = colorResource(R.color.dark_red),
                fontFamily = resolved_text79.family,
                fontWeight = resolved_text79.weight,
                fontSize = resolved_text79.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text79.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 20.8.sp),
                modifier = Modifier
            )
            val resolved_text80 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.scroll_test_this_scrollview_cannot_be_scrol),
                color = colorResource(R.color.black),
                fontFamily = resolved_text80.family,
                fontWeight = resolved_text80.weight,
                fontSize = resolved_text80.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text80.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 18.2.sp),
                modifier = Modifier
            )
            val resolved_text81 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.scroll_test_item_1),
                color = colorResource(R.color.black),
                fontFamily = resolved_text81.family,
                fontWeight = resolved_text81.weight,
                fontSize = resolved_text81.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text81.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 18.2.sp),
                modifier = Modifier
            )
            val resolved_text82 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.scroll_test_item_2),
                color = colorResource(R.color.black),
                fontFamily = resolved_text82.family,
                fontWeight = resolved_text82.weight,
                fontSize = resolved_text82.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text82.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 18.2.sp),
                modifier = Modifier
            )
            val resolved_text83 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.scroll_test_item_3_hidden_below),
                color = colorResource(R.color.black),
                fontFamily = resolved_text83.family,
                fontWeight = resolved_text83.weight,
                fontSize = resolved_text83.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text83.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 18.2.sp),
                modifier = Modifier
            )
        }
        }
    }
}

@Composable
private fun Section3_0(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text84 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_horizontal_scroll_item_1),
        color = colorResource(R.color.black),
        fontFamily = resolved_text84.family,
        fontWeight = resolved_text84.weight,
        fontSize = resolved_text84.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text84.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .width(150.dp)
            .background(colorResource(R.color.white_5))
            .padding(10.dp)
    )
}

@Composable
private fun Section3_1(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text85 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_horizontal_scroll_item_2),
        color = colorResource(R.color.black),
        fontFamily = resolved_text85.family,
        fontWeight = resolved_text85.weight,
        fontSize = resolved_text85.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text85.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .width(150.dp)
            .background(colorResource(R.color.white_6))
            .padding(10.dp)
    )
}

@Composable
private fun Section3_2(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text86 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_horizontal_scroll_item_3),
        color = colorResource(R.color.black),
        fontFamily = resolved_text86.family,
        fontWeight = resolved_text86.weight,
        fontSize = resolved_text86.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text86.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .width(150.dp)
            .background(colorResource(R.color.white_7))
            .padding(10.dp)
    )
}

@Composable
private fun Section3_3(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text87 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_horizontal_scroll_item_4),
        color = colorResource(R.color.black),
        fontFamily = resolved_text87.family,
        fontWeight = resolved_text87.weight,
        fontSize = resolved_text87.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text87.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .width(150.dp)
            .background(colorResource(R.color.white_8))
            .padding(10.dp)
    )
}

@Composable
private fun Section3_4(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text88 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_horizontal_scroll_item_5),
        color = colorResource(R.color.black),
        fontFamily = resolved_text88.family,
        fontWeight = resolved_text88.weight,
        fontSize = resolved_text88.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text88.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .width(150.dp)
            .background(colorResource(R.color.white_9))
            .padding(10.dp)
    )
}

@Composable
private fun Section3_5(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text89 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_horizontal_scroll_item_6),
        color = colorResource(R.color.black),
        fontFamily = resolved_text89.family,
        fontWeight = resolved_text89.weight,
        fontSize = resolved_text89.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text89.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .width(150.dp)
            .background(colorResource(R.color.white_22))
            .padding(10.dp)
    )
}

@Composable
private fun Section3_6(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text90 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_horizontal_scroll_item_7),
        color = colorResource(R.color.black),
        fontFamily = resolved_text90.family,
        fontWeight = resolved_text90.weight,
        fontSize = resolved_text90.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text90.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .width(150.dp)
            .background(colorResource(R.color.white_25))
            .padding(10.dp)
    )
}

@Composable
private fun Section3_7(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text91 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_horizontal_scroll_item_8_end),
        color = colorResource(R.color.black),
        fontFamily = resolved_text91.family,
        fontWeight = resolved_text91.weight,
        fontSize = resolved_text91.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text91.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .width(150.dp)
            .background(colorResource(R.color.white_26))
            .padding(10.dp)
    )
}

@Composable
private fun Section3(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    LazyRow(
        modifier = Modifier
            .height(100.dp)
            .background(colorResource(R.color.white_17))
            .imePadding()
    ) {
        item {
        Row(
            modifier = Modifier.padding(10.dp)
        ) {
            Section3_0(data, viewModel)
            Section3_1(data, viewModel)
            Section3_2(data, viewModel)
            Section3_3(data, viewModel)
            Section3_4(data, viewModel)
            Section3_5(data, viewModel)
            Section3_6(data, viewModel)
            Section3_7(data, viewModel)
        }
        }
    }
}
// >>> RESPONSIVE_HELPERS_END