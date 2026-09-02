package com.example.kotlinjsonui.sample.views.scroll_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
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
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun ScrollTestGeneratedView(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from scroll_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
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
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("scroll_test")
    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 20.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_scrollview_test),
        color = colorResource(R.color.black),
        fontFamily = resolved_text1.family,
        fontWeight = resolved_text1.weight,
        fontSize = resolved_text1.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text1.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 26.0.sp),
        modifier = Modifier.padding(10.dp)
    )
}

@Composable
private fun Section1_0(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_no_vertical_indicator),
        color = colorResource(R.color.black),
        fontFamily = resolved_text2.family,
        fontWeight = resolved_text2.weight,
        fontSize = resolved_text2.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text2.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section1_1(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text3 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_item_1),
        color = colorResource(R.color.black),
        fontFamily = resolved_text3.family,
        fontWeight = resolved_text3.weight,
        fontSize = resolved_text3.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text3.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
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
    val resolved_text4 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_item_2),
        color = colorResource(R.color.black),
        fontFamily = resolved_text4.family,
        fontWeight = resolved_text4.weight,
        fontSize = resolved_text4.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text4.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
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
    val resolved_text5 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_item_3),
        color = colorResource(R.color.black),
        fontFamily = resolved_text5.family,
        fontWeight = resolved_text5.weight,
        fontSize = resolved_text5.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text5.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
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
    val resolved_text6 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_item_4),
        color = colorResource(R.color.black),
        fontFamily = resolved_text6.family,
        fontWeight = resolved_text6.weight,
        fontSize = resolved_text6.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text6.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
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
    val resolved_text7 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_item_5),
        color = colorResource(R.color.black),
        fontFamily = resolved_text7.family,
        fontWeight = resolved_text7.weight,
        fontSize = resolved_text7.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text7.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
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
            .requiredHeight(200.dp)
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
            .requiredHeight(150.dp)
            .background(colorResource(R.color.white_17))
            .imePadding(),
        userScrollEnabled = false
    ) {
        item {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            val resolved_text8 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = FontWeight.Bold,
                size = 16.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.scroll_test_scroll_disabled),
                color = colorResource(R.color.dark_red),
                fontFamily = resolved_text8.family,
                fontWeight = resolved_text8.weight,
                fontSize = resolved_text8.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text8.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
                modifier = Modifier
            )
            val resolved_text9 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.scroll_test_this_scrollview_cannot_be_scrol),
                color = colorResource(R.color.black),
                fontFamily = resolved_text9.family,
                fontWeight = resolved_text9.weight,
                fontSize = resolved_text9.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text9.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
                modifier = Modifier
            )
            val resolved_text10 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.scroll_test_item_1),
                color = colorResource(R.color.black),
                fontFamily = resolved_text10.family,
                fontWeight = resolved_text10.weight,
                fontSize = resolved_text10.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text10.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
                modifier = Modifier
            )
            val resolved_text11 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.scroll_test_item_2),
                color = colorResource(R.color.black),
                fontFamily = resolved_text11.family,
                fontWeight = resolved_text11.weight,
                fontSize = resolved_text11.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text11.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
                modifier = Modifier
            )
            val resolved_text12 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.scroll_test_item_3_hidden_below),
                color = colorResource(R.color.black),
                fontFamily = resolved_text12.family,
                fontWeight = resolved_text12.weight,
                fontSize = resolved_text12.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text12.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
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
    val resolved_text13 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_horizontal_scroll_item_1),
        color = colorResource(R.color.black),
        fontFamily = resolved_text13.family,
        fontWeight = resolved_text13.weight,
        fontSize = resolved_text13.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text13.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier
            .requiredWidth(150.dp)
            .background(colorResource(R.color.white_5))
            .padding(10.dp)
    )
}

@Composable
private fun Section3_1(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text14 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_horizontal_scroll_item_2),
        color = colorResource(R.color.black),
        fontFamily = resolved_text14.family,
        fontWeight = resolved_text14.weight,
        fontSize = resolved_text14.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text14.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier
            .requiredWidth(150.dp)
            .background(colorResource(R.color.white_6))
            .padding(10.dp)
    )
}

@Composable
private fun Section3_2(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text15 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_horizontal_scroll_item_3),
        color = colorResource(R.color.black),
        fontFamily = resolved_text15.family,
        fontWeight = resolved_text15.weight,
        fontSize = resolved_text15.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text15.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier
            .requiredWidth(150.dp)
            .background(colorResource(R.color.white_7))
            .padding(10.dp)
    )
}

@Composable
private fun Section3_3(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text16 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_horizontal_scroll_item_4),
        color = colorResource(R.color.black),
        fontFamily = resolved_text16.family,
        fontWeight = resolved_text16.weight,
        fontSize = resolved_text16.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text16.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier
            .requiredWidth(150.dp)
            .background(colorResource(R.color.white_8))
            .padding(10.dp)
    )
}

@Composable
private fun Section3_4(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text17 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_horizontal_scroll_item_5),
        color = colorResource(R.color.black),
        fontFamily = resolved_text17.family,
        fontWeight = resolved_text17.weight,
        fontSize = resolved_text17.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text17.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier
            .requiredWidth(150.dp)
            .background(colorResource(R.color.white_9))
            .padding(10.dp)
    )
}

@Composable
private fun Section3_5(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text18 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_horizontal_scroll_item_6),
        color = colorResource(R.color.black),
        fontFamily = resolved_text18.family,
        fontWeight = resolved_text18.weight,
        fontSize = resolved_text18.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text18.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier
            .requiredWidth(150.dp)
            .background(colorResource(R.color.white_22))
            .padding(10.dp)
    )
}

@Composable
private fun Section3_6(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text19 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_horizontal_scroll_item_7),
        color = colorResource(R.color.black),
        fontFamily = resolved_text19.family,
        fontWeight = resolved_text19.weight,
        fontSize = resolved_text19.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text19.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier
            .requiredWidth(150.dp)
            .background(colorResource(R.color.white_25))
            .padding(10.dp)
    )
}

@Composable
private fun Section3_7(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    val resolved_text20 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.scroll_test_horizontal_scroll_item_8_end),
        color = colorResource(R.color.black),
        fontFamily = resolved_text20.family,
        fontWeight = resolved_text20.weight,
        fontSize = resolved_text20.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text20.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier
            .requiredWidth(150.dp)
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
            .requiredHeight(100.dp)
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