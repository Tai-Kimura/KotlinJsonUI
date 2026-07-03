package com.example.kotlinjsonui.sample.views.alignment_combo_test

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
import androidx.compose.ui.BiasAlignment
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
import com.example.kotlinjsonui.sample.data.AlignmentComboTestData
import com.example.kotlinjsonui.sample.viewmodels.AlignmentComboTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun AlignmentComboTestGeneratedView(
    data: AlignmentComboTestData,
    viewModel: AlignmentComboTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from alignment_combo_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "alignment_combo_test",
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
                android.util.Log.e("DynamicView", "Error loading alignment_combo_test: \$error")
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
                .padding(20.dp)
        ) {
            Section0(data, viewModel)
            val resolved_text375 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 24.sp,
                italic = false
            ))
            Text(
                text = "${data.title}",
                color = colorResource(R.color.black),
                fontFamily = resolved_text375.family,
                fontWeight = resolved_text375.weight,
                fontSize = resolved_text375.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text375.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 31.2.sp),
                modifier = Modifier
                    .testTag("title_label")
                    .semantics { testTagsAsResourceId = true }
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 20.dp)
                    .wrapContentWidth()
                    .wrapContentHeight(),
                textAlign = TextAlign.Center
            )
            Section2(data, viewModel)
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorResource(R.color.pale_gray))
            ) {
                val resolved_text377 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_combo_test_topleft),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text377.family,
                    fontWeight = resolved_text377.weight,
                    fontSize = resolved_text377.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text377.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(colorResource(R.color.pale_pink))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorResource(R.color.pale_gray_2))
            ) {
                val resolved_text378 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_combo_test_topright),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text378.family,
                    fontWeight = resolved_text378.weight,
                    fontSize = resolved_text378.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text378.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(colorResource(R.color.pale_yellow))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorResource(R.color.pale_gray_3))
            ) {
                val resolved_text379 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_combo_test_bottomleft),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text379.family,
                    fontWeight = resolved_text379.weight,
                    fontSize = resolved_text379.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text379.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(colorResource(R.color.pale_cyan))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorResource(R.color.light_gray))
            ) {
                val resolved_text380 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_combo_test_bottomright),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text380.family,
                    fontWeight = resolved_text380.weight,
                    fontSize = resolved_text380.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text380.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(colorResource(R.color.white_2))
                        .padding(8.dp)
                )
            }
            Section7(data, viewModel)
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorResource(R.color.light_gray_2))
            ) {
                val resolved_text382 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_combo_test_topcenter),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text382.family,
                    fontWeight = resolved_text382.weight,
                    fontSize = resolved_text382.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text382.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .align(BiasAlignment(0f, -1f))
                        .background(colorResource(R.color.white_3))
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorResource(R.color.light_gray_3))
            ) {
                val resolved_text383 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_combo_test_bottomcenter),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text383.family,
                    fontWeight = resolved_text383.weight,
                    fontSize = resolved_text383.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text383.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .align(BiasAlignment(0f, 1f))
                        .background(colorResource(R.color.white_4))
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorResource(R.color.light_gray_4))
            ) {
                val resolved_text384 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_combo_test_leftcenter),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text384.family,
                    fontWeight = resolved_text384.weight,
                    fontSize = resolved_text384.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text384.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .background(colorResource(R.color.pale_red))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorResource(R.color.light_gray_5))
            ) {
                val resolved_text385 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_combo_test_rightcenter),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text385.family,
                    fontWeight = resolved_text385.weight,
                    fontSize = resolved_text385.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text385.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .background(colorResource(R.color.pale_green))
                        .padding(8.dp)
                )
            }
            Section12(data, viewModel)
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(colorResource(R.color.light_gray_6))
            ) {
                val resolved_text387 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 12.sp,
                    italic = false
                ))
                Text(
                    text = "TL",
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text387.family,
                    fontWeight = resolved_text387.weight,
                    fontSize = resolved_text387.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text387.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 15.6.sp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(colorResource(R.color.white_5))
                        .padding(5.dp)
                )
                val resolved_text388 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 12.sp,
                    italic = false
                ))
                Text(
                    text = "TR",
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text388.family,
                    fontWeight = resolved_text388.weight,
                    fontSize = resolved_text388.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text388.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 15.6.sp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(colorResource(R.color.white_6))
                        .padding(5.dp)
                )
                val resolved_text389 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 12.sp,
                    italic = false
                ))
                Text(
                    text = "BL",
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text389.family,
                    fontWeight = resolved_text389.weight,
                    fontSize = resolved_text389.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text389.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 15.6.sp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(colorResource(R.color.white_7))
                        .padding(5.dp)
                )
                val resolved_text390 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 12.sp,
                    italic = false
                ))
                Text(
                    text = "BR",
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text390.family,
                    fontWeight = resolved_text390.weight,
                    fontSize = resolved_text390.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text390.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 15.6.sp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(colorResource(R.color.white_8))
                        .padding(5.dp)
                )
                val resolved_text391 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 12.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_combo_test_center),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text391.family,
                    fontWeight = resolved_text391.weight,
                    fontSize = resolved_text391.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text391.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 15.6.sp),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(colorResource(R.color.white_9))
                        .padding(5.dp)
                )
            }
            Section14(data, viewModel)
            Row(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(colorResource(R.color.light_gray_7))
            ) {
                val resolved_text393 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_combo_test_lefttop),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text393.family,
                    fontWeight = resolved_text393.weight,
                    fontSize = resolved_text393.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text393.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .align(Alignment.Top)
                        .background(colorResource(R.color.pale_red_2))
                        .padding(8.dp)
                )
                val resolved_text394 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_combo_test_center),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text394.family,
                    fontWeight = resolved_text394.weight,
                    fontSize = resolved_text394.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text394.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .background(colorResource(R.color.pale_green_2))
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
                val resolved_text395 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_combo_test_rightbottom),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text395.family,
                    fontWeight = resolved_text395.weight,
                    fontSize = resolved_text395.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text395.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .background(colorResource(R.color.pale_blue))
                        .padding(8.dp)
                )
            }
            Section16(data, viewModel)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(colorResource(R.color.medium_gray))
            ) {
                val resolved_text397 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_combo_test_topleft),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text397.family,
                    fontWeight = resolved_text397.weight,
                    fontSize = resolved_text397.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text397.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .background(colorResource(R.color.pale_red_3))
                        .padding(8.dp)
                )
                val resolved_text398 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_combo_test_center),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text398.family,
                    fontWeight = resolved_text398.weight,
                    fontSize = resolved_text398.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text398.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(colorResource(R.color.pale_green_3))
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
                val resolved_text399 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_combo_test_bottomright),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text399.family,
                    fontWeight = resolved_text399.weight,
                    fontSize = resolved_text399.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text399.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .align(Alignment.End)
                        .background(colorResource(R.color.pale_blue_2))
                        .padding(8.dp)
                )
            }
            Section18(data, viewModel)
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorResource(R.color.medium_gray_2))
            ) {
                val resolved_text401 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_combo_test_only_horizontal_center),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text401.family,
                    fontWeight = resolved_text401.weight,
                    fontSize = resolved_text401.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text401.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .align(BiasAlignment(0f, -1f))
                        .background(colorResource(R.color.white_10))
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorResource(R.color.medium_gray_3))
            ) {
                val resolved_text402 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_combo_test_only_vertical_center),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text402.family,
                    fontWeight = resolved_text402.weight,
                    fontSize = resolved_text402.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text402.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .align(BiasAlignment(-1f, 0f))
                        .background(colorResource(R.color.white_11))
                        .padding(8.dp)
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
    data: AlignmentComboTestData,
    viewModel: AlignmentComboTestViewModel
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
        val resolved_button25 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button25.family,
            fontWeight = resolved_button25.weight,
            fontSize = resolved_button25.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button25.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section2(
    data: AlignmentComboTestData,
    viewModel: AlignmentComboTestViewModel
) {
    val resolved_text376 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.alignment_combo_test_corner_combinations),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text376.family,
        fontWeight = resolved_text376.weight,
        fontSize = resolved_text376.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text376.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun Section7(
    data: AlignmentComboTestData,
    viewModel: AlignmentComboTestViewModel
) {
    val resolved_text381 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.alignment_combo_test_edge_center_combinations),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text381.family,
        fontWeight = resolved_text381.weight,
        fontSize = resolved_text381.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text381.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun Section12(
    data: AlignmentComboTestData,
    viewModel: AlignmentComboTestViewModel
) {
    val resolved_text386 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.alignment_combo_test_multiple_elements_test),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text386.family,
        fontWeight = resolved_text386.weight,
        fontSize = resolved_text386.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text386.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun Section14(
    data: AlignmentComboTestData,
    viewModel: AlignmentComboTestViewModel
) {
    val resolved_text392 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.alignment_combo_test_hstack_mixed_alignment),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text392.family,
        fontWeight = resolved_text392.weight,
        fontSize = resolved_text392.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text392.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun Section16(
    data: AlignmentComboTestData,
    viewModel: AlignmentComboTestViewModel
) {
    val resolved_text396 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.alignment_combo_test_vstack_mixed_alignment),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text396.family,
        fontWeight = resolved_text396.weight,
        fontSize = resolved_text396.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text396.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun Section18(
    data: AlignmentComboTestData,
    viewModel: AlignmentComboTestViewModel
) {
    val resolved_text400 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.alignment_combo_test_edge_cases),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text400.family,
        fontWeight = resolved_text400.weight,
        fontSize = resolved_text400.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text400.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(bottom = 10.dp)
    )
}
// >>> RESPONSIVE_HELPERS_END