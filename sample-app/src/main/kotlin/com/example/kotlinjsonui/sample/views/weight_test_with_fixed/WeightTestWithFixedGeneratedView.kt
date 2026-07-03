package com.example.kotlinjsonui.sample.views.weight_test_with_fixed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.WeightTestWithFixedData
import com.example.kotlinjsonui.sample.viewmodels.WeightTestWithFixedViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun WeightTestWithFixedGeneratedView(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from weight_test_with_fixed.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "weight_test_with_fixed",
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
                android.util.Log.e("DynamicView", "Error loading weight_test_with_fixed: \$error")
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
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                val resolved_text112 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_fixed_80),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text112.family,
                    fontWeight = resolved_text112.weight,
                    fontSize = resolved_text112.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text112.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .width(80.dp)
                        .fillMaxHeight()
                        .background(colorResource(R.color.pale_pink)),
                    textAlign = TextAlign.Center
                )
                val resolved_text113 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.weight_test_weight_1),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text113.family,
                    fontWeight = resolved_text113.weight,
                    fontSize = resolved_text113.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text113.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(colorResource(R.color.pale_yellow)),
                    textAlign = TextAlign.Center
                )
                val resolved_text114 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.weight_test_weight_2),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text114.family,
                    fontWeight = resolved_text114.weight,
                    fontSize = resolved_text114.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text114.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                        .background(colorResource(R.color.pale_cyan)),
                    textAlign = TextAlign.Center
                )
                val resolved_text115 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_fixed_60),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text115.family,
                    fontWeight = resolved_text115.weight,
                    fontSize = resolved_text115.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text115.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .width(60.dp)
                        .fillMaxHeight()
                        .background(colorResource(R.color.white_2)),
                    textAlign = TextAlign.Center
                )
            }
            Section4(data, viewModel)
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    val resolved_text117 = Configuration.Font.resolve(FontSpec(
                        family = null,
                        weight = null,
                        size = 12.sp,
                        italic = false
                    ))
                    Text(
                        text = "1-1",
                        color = colorResource(R.color.black),
                        fontFamily = resolved_text117.family,
                        fontWeight = resolved_text117.weight,
                        fontSize = resolved_text117.size ?: TextUnit.Unspecified,
                        fontStyle = resolved_text117.style ?: FontStyle.Normal,
                        style = TextStyle(lineHeight = 15.6.sp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .height(0.dp)
                            .background(colorResource(R.color.white_5)),
                        textAlign = TextAlign.Center
                    )
                    val resolved_text118 = Configuration.Font.resolve(FontSpec(
                        family = null,
                        weight = null,
                        size = 12.sp,
                        italic = false
                    ))
                    Text(
                        text = "1-2",
                        color = colorResource(R.color.black),
                        fontFamily = resolved_text118.family,
                        fontWeight = resolved_text118.weight,
                        fontSize = resolved_text118.size ?: TextUnit.Unspecified,
                        fontStyle = resolved_text118.style ?: FontStyle.Normal,
                        style = TextStyle(lineHeight = 15.6.sp),
                        modifier = Modifier
                            .weight(2f)
                            .fillMaxWidth()
                            .height(0.dp)
                            .background(colorResource(R.color.pale_red_3)),
                        textAlign = TextAlign.Center
                    )
                }
                Section5_1(data, viewModel)
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                ) {
                    val resolved_text120 = Configuration.Font.resolve(FontSpec(
                        family = null,
                        weight = null,
                        size = 12.sp,
                        italic = false
                    ))
                    Text(
                        text = "Fixed 30",
                        color = colorResource(R.color.black),
                        fontFamily = resolved_text120.family,
                        fontWeight = resolved_text120.weight,
                        fontSize = resolved_text120.size ?: TextUnit.Unspecified,
                        fontStyle = resolved_text120.style ?: FontStyle.Normal,
                        style = TextStyle(lineHeight = 15.6.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .background(colorResource(R.color.white_6)),
                        textAlign = TextAlign.Center
                    )
                    val resolved_text121 = Configuration.Font.resolve(FontSpec(
                        family = null,
                        weight = null,
                        size = 12.sp,
                        italic = false
                    ))
                    Text(
                        text = stringResource(R.string.width_test_weight_1),
                        color = colorResource(R.color.black),
                        fontFamily = resolved_text121.family,
                        fontWeight = resolved_text121.weight,
                        fontSize = resolved_text121.size ?: TextUnit.Unspecified,
                        fontStyle = resolved_text121.style ?: FontStyle.Normal,
                        style = TextStyle(lineHeight = 15.6.sp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .height(0.dp)
                            .background(colorResource(R.color.pale_green_3)),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Section6(data, viewModel)
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Section7_0(data, viewModel)
                val resolved_text124 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_w1),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text124.family,
                    fontWeight = resolved_text124.weight,
                    fontSize = resolved_text124.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text124.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(colorResource(R.color.pale_yellow)),
                    textAlign = TextAlign.Center
                )
                Section7_2(data, viewModel)
                val resolved_text126 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_w1),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text126.family,
                    fontWeight = resolved_text126.weight,
                    fontSize = resolved_text126.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text126.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(colorResource(R.color.pale_yellow)),
                    textAlign = TextAlign.Center
                )
                Section7_4(data, viewModel)
            }
            Section8(data, viewModel)
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                val resolved_text129 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_w0),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text129.family,
                    fontWeight = resolved_text129.weight,
                    fontSize = resolved_text129.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text129.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(colorResource(R.color.light_red_6)),
                    textAlign = TextAlign.Center
                )
                val resolved_text130 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_w1),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text130.family,
                    fontWeight = resolved_text130.weight,
                    fontSize = resolved_text130.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text130.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(colorResource(R.color.light_green_2)),
                    textAlign = TextAlign.Center
                )
                val resolved_text131 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_fixed100),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text131.family,
                    fontWeight = resolved_text131.weight,
                    fontSize = resolved_text131.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text131.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .width(100.dp)
                        .fillMaxHeight()
                        .background(colorResource(R.color.light_blue_3)),
                    textAlign = TextAlign.Center
                )
                val resolved_text132 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_w3),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text132.family,
                    fontWeight = resolved_text132.weight,
                    fontSize = resolved_text132.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text132.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxHeight()
                        .background(colorResource(R.color.pale)),
                    textAlign = TextAlign.Center
                )
            }
            Section10(data, viewModel)
            Column(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Section11_0(data, viewModel)
                val resolved_text135 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.weight_test_weight_1),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text135.family,
                    fontWeight = resolved_text135.weight,
                    fontSize = resolved_text135.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text135.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .height(0.dp)
                        .background(colorResource(R.color.white_6)),
                    textAlign = TextAlign.Center
                )
                Section11_2(data, viewModel)
                val resolved_text137 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.weight_test_weight_2),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text137.family,
                    fontWeight = resolved_text137.weight,
                    fontSize = resolved_text137.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text137.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxWidth()
                        .height(0.dp)
                        .background(colorResource(R.color.white_8)),
                    textAlign = TextAlign.Center
                )
                Section11_4(data, viewModel)
            }
            Section12(data, viewModel)
            Column(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Section13_0(data, viewModel)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .height(0.dp)
                ) {
                    val resolved_text141 = Configuration.Font.resolve(FontSpec(
                        family = null,
                        weight = null,
                        size = 12.sp,
                        italic = false
                    ))
                    Text(
                        text = stringResource(R.string.weight_test_with_fixed_col_1),
                        color = colorResource(R.color.black),
                        fontFamily = resolved_text141.family,
                        fontWeight = resolved_text141.weight,
                        fontSize = resolved_text141.size ?: TextUnit.Unspecified,
                        fontStyle = resolved_text141.style ?: FontStyle.Normal,
                        style = TextStyle(lineHeight = 15.6.sp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(colorResource(R.color.pale_pink)),
                        textAlign = TextAlign.Center
                    )
                    val resolved_text142 = Configuration.Font.resolve(FontSpec(
                        family = null,
                        weight = null,
                        size = 12.sp,
                        italic = false
                    ))
                    Text(
                        text = stringResource(R.string.weight_test_with_fixed_fix60),
                        color = colorResource(R.color.black),
                        fontFamily = resolved_text142.family,
                        fontWeight = resolved_text142.weight,
                        fontSize = resolved_text142.size ?: TextUnit.Unspecified,
                        fontStyle = resolved_text142.style ?: FontStyle.Normal,
                        style = TextStyle(lineHeight = 15.6.sp),
                        modifier = Modifier
                            .width(60.dp)
                            .fillMaxHeight()
                            .background(colorResource(R.color.pale_gray_3)),
                        textAlign = TextAlign.Center
                    )
                    val resolved_text143 = Configuration.Font.resolve(FontSpec(
                        family = null,
                        weight = null,
                        size = 12.sp,
                        italic = false
                    ))
                    Text(
                        text = stringResource(R.string.weight_test_with_fixed_col_2),
                        color = colorResource(R.color.black),
                        fontFamily = resolved_text143.family,
                        fontWeight = resolved_text143.weight,
                        fontSize = resolved_text143.size ?: TextUnit.Unspecified,
                        fontStyle = resolved_text143.style ?: FontStyle.Normal,
                        style = TextStyle(lineHeight = 15.6.sp),
                        modifier = Modifier
                            .weight(2f)
                            .fillMaxHeight()
                            .background(colorResource(R.color.pale_yellow)),
                        textAlign = TextAlign.Center
                    )
                }
                Section13_2(data, viewModel)
            }
            Section14(data, viewModel)
            Column(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                val resolved_text146 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_w1),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text146.family,
                    fontWeight = resolved_text146.weight,
                    fontSize = resolved_text146.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text146.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .height(0.dp)
                        .background(colorResource(R.color.pale_red)),
                    textAlign = TextAlign.Center
                )
                val resolved_text147 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_w1),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text147.family,
                    fontWeight = resolved_text147.weight,
                    fontSize = resolved_text147.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text147.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .height(0.dp)
                        .background(colorResource(R.color.pale_green)),
                    textAlign = TextAlign.Center
                )
                val resolved_text148 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_w2),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text148.family,
                    fontWeight = resolved_text148.weight,
                    fontSize = resolved_text148.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text148.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxWidth()
                        .height(0.dp)
                        .background(colorResource(R.color.pale_blue_3)),
                    textAlign = TextAlign.Center
                )
                val resolved_text149 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_w1),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text149.family,
                    fontWeight = resolved_text149.weight,
                    fontSize = resolved_text149.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text149.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .height(0.dp)
                        .background(colorResource(R.color.white_18)),
                    textAlign = TextAlign.Center
                )
            }
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
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
        val resolved_button11 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button11.family,
            fontWeight = resolved_button11.weight,
            fontSize = resolved_button11.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button11.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section1(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text110 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = "${data.title}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text110.family,
        fontWeight = resolved_text110.weight,
        fontSize = resolved_text110.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text110.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 31.2.sp),
        modifier = Modifier
            .testTag("title_label")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 20.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section2(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text111 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_fixed80_weight1_weight2_fixed60),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text111.family,
        fontWeight = resolved_text111.weight,
        fontSize = resolved_text111.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text111.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section4(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text116 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_complex_nested_weights_with_fix),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text116.family,
        fontWeight = resolved_text116.weight,
        fontSize = resolved_text116.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text116.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section5_1(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text119 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 12.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_fix_50),
        color = colorResource(R.color.black),
        fontFamily = resolved_text119.family,
        fontWeight = resolved_text119.weight,
        fontSize = resolved_text119.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text119.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 15.6.sp),
        modifier = Modifier
            .width(50.dp)
            .fillMaxHeight()
            .background(colorResource(R.color.pale_gray)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section6(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text122 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_multiple_fixed_sizes_with_weigh),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text122.family,
        fontWeight = resolved_text122.weight,
        fontSize = resolved_text122.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text122.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section7_0(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text123 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "40",
        color = colorResource(R.color.black),
        fontFamily = resolved_text123.family,
        fontWeight = resolved_text123.weight,
        fontSize = resolved_text123.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text123.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .width(40.dp)
            .fillMaxHeight()
            .background(colorResource(R.color.pale_pink)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section7_2(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text125 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "60",
        color = colorResource(R.color.black),
        fontFamily = resolved_text125.family,
        fontWeight = resolved_text125.weight,
        fontSize = resolved_text125.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text125.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .width(60.dp)
            .fillMaxHeight()
            .background(colorResource(R.color.pale_pink)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section7_4(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text127 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "80",
        color = colorResource(R.color.black),
        fontFamily = resolved_text127.family,
        fontWeight = resolved_text127.weight,
        fontSize = resolved_text127.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text127.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .width(80.dp)
            .fillMaxHeight()
            .background(colorResource(R.color.pale_pink)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section8(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text128 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_zero_weights_test),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text128.family,
        fontWeight = resolved_text128.weight,
        fontSize = resolved_text128.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text128.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section10(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text133 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_vertical_fixed_weight_combinati),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text133.family,
        fontWeight = resolved_text133.weight,
        fontSize = resolved_text133.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text133.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section11_0(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text134 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_fixed_30),
        color = colorResource(R.color.black),
        fontFamily = resolved_text134.family,
        fontWeight = resolved_text134.weight,
        fontSize = resolved_text134.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text134.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(colorResource(R.color.white_5)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section11_2(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text136 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_fixed_40),
        color = colorResource(R.color.black),
        fontFamily = resolved_text136.family,
        fontWeight = resolved_text136.weight,
        fontSize = resolved_text136.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text136.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(colorResource(R.color.white_7)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section11_4(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text138 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_fixed_50),
        color = colorResource(R.color.black),
        fontFamily = resolved_text138.family,
        fontWeight = resolved_text138.weight,
        fontSize = resolved_text138.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text138.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(colorResource(R.color.white_9)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section12(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text139 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_vertical_nested_horizontal_weig),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text139.family,
        fontWeight = resolved_text139.weight,
        fontSize = resolved_text139.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text139.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section13_0(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text140 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 12.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_header_fixed_25),
        color = colorResource(R.color.white),
        fontFamily = resolved_text140.family,
        fontWeight = resolved_text140.weight,
        fontSize = resolved_text140.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text140.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 15.6.sp),
        modifier = Modifier
            .fillMaxWidth()
            .height(25.dp)
            .background(colorResource(R.color.medium_gray_4)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section13_2(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text144 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 12.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_footer_fixed_25),
        color = colorResource(R.color.white),
        fontFamily = resolved_text144.family,
        fontWeight = resolved_text144.weight,
        fontSize = resolved_text144.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text144.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 15.6.sp),
        modifier = Modifier
            .fillMaxWidth()
            .height(25.dp)
            .background(colorResource(R.color.medium_gray_4)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section14(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text145 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_vertical_multiple_weights_only),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text145.family,
        fontWeight = resolved_text145.weight,
        fontSize = resolved_text145.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text145.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}
// >>> RESPONSIVE_HELPERS_END