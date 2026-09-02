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
import androidx.compose.material3.LocalTextStyle
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
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun WeightTestWithFixedGeneratedView(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from weight_test_with_fixed.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
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
                Section3(data, viewModel)
                Section4(data, viewModel)
                Section5(data, viewModel)
                Section6(data, viewModel)
                Section7(data, viewModel)
                Section8(data, viewModel)
                Section9(data, viewModel)
                Section10(data, viewModel)
                Section11(data, viewModel)
                Section12(data, viewModel)
                Section13(data, viewModel)
                Section14(data, viewModel)
                Section15(data, viewModel)
            }
            }
        }    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("weight_test_with_fixed")
    }
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
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
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
    val resolved_text2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_fixed80_weight1_weight2_fixed60),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text2.family,
        fontWeight = resolved_text2.weight,
        fontSize = resolved_text2.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text2.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section3(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .requiredHeight(60.dp)
    ) {
        val resolved_text3 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.weight_test_with_fixed_fixed_80),
            color = colorResource(R.color.black),
            fontFamily = resolved_text3.family,
            fontWeight = resolved_text3.weight,
            fontSize = resolved_text3.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text3.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .requiredWidth(80.dp)
                .fillMaxHeight()
                .background(colorResource(R.color.pale_pink)),
            textAlign = TextAlign.Center
        )
        val resolved_text4 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.weight_test_with_fixed_weight_1),
            color = colorResource(R.color.black),
            fontFamily = resolved_text4.family,
            fontWeight = resolved_text4.weight,
            fontSize = resolved_text4.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text4.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(colorResource(R.color.pale_yellow)),
            textAlign = TextAlign.Center
        )
        val resolved_text5 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.weight_test_with_fixed_weight_2),
            color = colorResource(R.color.black),
            fontFamily = resolved_text5.family,
            fontWeight = resolved_text5.weight,
            fontSize = resolved_text5.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text5.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .background(colorResource(R.color.pale_cyan)),
            textAlign = TextAlign.Center
        )
        val resolved_text6 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.weight_test_with_fixed_fixed_60),
            color = colorResource(R.color.black),
            fontFamily = resolved_text6.family,
            fontWeight = resolved_text6.weight,
            fontSize = resolved_text6.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text6.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .requiredWidth(60.dp)
                .fillMaxHeight()
                .background(colorResource(R.color.white_2)),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Section4(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text7 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_complex_nested_weights_with_fix),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text7.family,
        fontWeight = resolved_text7.weight,
        fontSize = resolved_text7.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text7.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
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
    val resolved_text10 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 12.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_fix_50),
        color = colorResource(R.color.black),
        fontFamily = resolved_text10.family,
        fontWeight = resolved_text10.weight,
        fontSize = resolved_text10.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text10.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 15.6.sp),
        modifier = Modifier
            .requiredWidth(50.dp)
            .fillMaxHeight()
            .background(colorResource(R.color.pale_gray)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section5(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .requiredHeight(100.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            val resolved_text8 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 12.sp,
                italic = false
            ))
            Text(
                text = "1-1",
                color = colorResource(R.color.black),
                fontFamily = resolved_text8.family,
                fontWeight = resolved_text8.weight,
                fontSize = resolved_text8.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text8.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 15.6.sp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(colorResource(R.color.white_5)),
                textAlign = TextAlign.Center
            )
            val resolved_text9 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 12.sp,
                italic = false
            ))
            Text(
                text = "1-2",
                color = colorResource(R.color.black),
                fontFamily = resolved_text9.family,
                fontWeight = resolved_text9.weight,
                fontSize = resolved_text9.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text9.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 15.6.sp),
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
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
            val resolved_text11 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 12.sp,
                italic = false
            ))
            Text(
                text = "Fixed 30",
                color = colorResource(R.color.black),
                fontFamily = resolved_text11.family,
                fontWeight = resolved_text11.weight,
                fontSize = resolved_text11.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text11.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 15.6.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(30.dp)
                    .background(colorResource(R.color.white_6)),
                textAlign = TextAlign.Center
            )
            val resolved_text12 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 12.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.weight_test_with_fixed_weight_1_2),
                color = colorResource(R.color.black),
                fontFamily = resolved_text12.family,
                fontWeight = resolved_text12.weight,
                fontSize = resolved_text12.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text12.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 15.6.sp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(colorResource(R.color.pale_green_3)),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun Section6(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text13 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_multiple_fixed_sizes_with_weigh),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text13.family,
        fontWeight = resolved_text13.weight,
        fontSize = resolved_text13.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text13.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
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
    val resolved_text14 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "40",
        color = colorResource(R.color.black),
        fontFamily = resolved_text14.family,
        fontWeight = resolved_text14.weight,
        fontSize = resolved_text14.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text14.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier
            .requiredWidth(40.dp)
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
    val resolved_text16 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "60",
        color = colorResource(R.color.black),
        fontFamily = resolved_text16.family,
        fontWeight = resolved_text16.weight,
        fontSize = resolved_text16.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text16.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier
            .requiredWidth(60.dp)
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
    val resolved_text18 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "80",
        color = colorResource(R.color.black),
        fontFamily = resolved_text18.family,
        fontWeight = resolved_text18.weight,
        fontSize = resolved_text18.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text18.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier
            .requiredWidth(80.dp)
            .fillMaxHeight()
            .background(colorResource(R.color.pale_pink)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section7(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .requiredHeight(60.dp)
    ) {
        Section7_0(data, viewModel)
        val resolved_text15 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.weight_test_with_fixed_w1),
            color = colorResource(R.color.black),
            fontFamily = resolved_text15.family,
            fontWeight = resolved_text15.weight,
            fontSize = resolved_text15.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text15.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(colorResource(R.color.pale_yellow)),
            textAlign = TextAlign.Center
        )
        Section7_2(data, viewModel)
        val resolved_text17 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.weight_test_with_fixed_w1),
            color = colorResource(R.color.black),
            fontFamily = resolved_text17.family,
            fontWeight = resolved_text17.weight,
            fontSize = resolved_text17.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text17.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(colorResource(R.color.pale_yellow)),
            textAlign = TextAlign.Center
        )
        Section7_4(data, viewModel)
    }
}

@Composable
private fun Section8(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text19 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_zero_weights_test),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text19.family,
        fontWeight = resolved_text19.weight,
        fontSize = resolved_text19.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text19.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section9(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .requiredHeight(60.dp)
    ) {
        val resolved_text20 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.weight_test_with_fixed_w0),
            color = colorResource(R.color.black),
            fontFamily = resolved_text20.family,
            fontWeight = resolved_text20.weight,
            fontSize = resolved_text20.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text20.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .fillMaxHeight()
                .background(colorResource(R.color.light_red_6)),
            textAlign = TextAlign.Center
        )
        val resolved_text21 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.weight_test_with_fixed_w1),
            color = colorResource(R.color.black),
            fontFamily = resolved_text21.family,
            fontWeight = resolved_text21.weight,
            fontSize = resolved_text21.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text21.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(colorResource(R.color.light_green_2)),
            textAlign = TextAlign.Center
        )
        val resolved_text22 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.weight_test_with_fixed_fixed100),
            color = colorResource(R.color.black),
            fontFamily = resolved_text22.family,
            fontWeight = resolved_text22.weight,
            fontSize = resolved_text22.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text22.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .requiredWidth(100.dp)
                .fillMaxHeight()
                .background(colorResource(R.color.light_blue_3)),
            textAlign = TextAlign.Center
        )
        val resolved_text23 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.weight_test_with_fixed_w3),
            color = colorResource(R.color.black),
            fontFamily = resolved_text23.family,
            fontWeight = resolved_text23.weight,
            fontSize = resolved_text23.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text23.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .weight(3f)
                .fillMaxHeight()
                .background(colorResource(R.color.pale)),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Section10(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text24 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_vertical_fixed_weight_combinati),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text24.family,
        fontWeight = resolved_text24.weight,
        fontSize = resolved_text24.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text24.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
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
    val resolved_text25 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_fixed_30),
        color = colorResource(R.color.black),
        fontFamily = resolved_text25.family,
        fontWeight = resolved_text25.weight,
        fontSize = resolved_text25.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text25.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(30.dp)
            .background(colorResource(R.color.white_5)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section11_2(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text27 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_fixed_40),
        color = colorResource(R.color.black),
        fontFamily = resolved_text27.family,
        fontWeight = resolved_text27.weight,
        fontSize = resolved_text27.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text27.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(40.dp)
            .background(colorResource(R.color.white_7)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section11_4(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text29 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_fixed_50),
        color = colorResource(R.color.black),
        fontFamily = resolved_text29.family,
        fontWeight = resolved_text29.weight,
        fontSize = resolved_text29.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text29.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(50.dp)
            .background(colorResource(R.color.white_9)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section11(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    Column(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .requiredHeight(200.dp)
    ) {
        Section11_0(data, viewModel)
        val resolved_text26 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.weight_test_with_fixed_weight_1),
            color = colorResource(R.color.black),
            fontFamily = resolved_text26.family,
            fontWeight = resolved_text26.weight,
            fontSize = resolved_text26.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text26.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(colorResource(R.color.white_6)),
            textAlign = TextAlign.Center
        )
        Section11_2(data, viewModel)
        val resolved_text28 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.weight_test_with_fixed_weight_2),
            color = colorResource(R.color.black),
            fontFamily = resolved_text28.family,
            fontWeight = resolved_text28.weight,
            fontSize = resolved_text28.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text28.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth()
                .background(colorResource(R.color.white_8)),
            textAlign = TextAlign.Center
        )
        Section11_4(data, viewModel)
    }
}

@Composable
private fun Section12(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text30 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_vertical_nested_horizontal_weig),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text30.family,
        fontWeight = resolved_text30.weight,
        fontSize = resolved_text30.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text30.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
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
    val resolved_text31 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 12.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_header_fixed_25),
        color = colorResource(R.color.white),
        fontFamily = resolved_text31.family,
        fontWeight = resolved_text31.weight,
        fontSize = resolved_text31.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text31.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 15.6.sp),
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(25.dp)
            .background(colorResource(R.color.medium_gray_4)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section13_2(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text35 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 12.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_footer_fixed_25),
        color = colorResource(R.color.white),
        fontFamily = resolved_text35.family,
        fontWeight = resolved_text35.weight,
        fontSize = resolved_text35.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text35.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 15.6.sp),
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(25.dp)
            .background(colorResource(R.color.medium_gray_4)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section13(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    Column(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .requiredHeight(150.dp)
    ) {
        Section13_0(data, viewModel)
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val resolved_text32 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 12.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.weight_test_with_fixed_col_1),
                color = colorResource(R.color.black),
                fontFamily = resolved_text32.family,
                fontWeight = resolved_text32.weight,
                fontSize = resolved_text32.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text32.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 15.6.sp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(colorResource(R.color.pale_pink)),
                textAlign = TextAlign.Center
            )
            val resolved_text33 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 12.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.weight_test_with_fixed_fix60),
                color = colorResource(R.color.black),
                fontFamily = resolved_text33.family,
                fontWeight = resolved_text33.weight,
                fontSize = resolved_text33.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text33.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 15.6.sp),
                modifier = Modifier
                    .requiredWidth(60.dp)
                    .fillMaxHeight()
                    .background(colorResource(R.color.pale_gray_3)),
                textAlign = TextAlign.Center
            )
            val resolved_text34 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 12.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.weight_test_with_fixed_col_2),
                color = colorResource(R.color.black),
                fontFamily = resolved_text34.family,
                fontWeight = resolved_text34.weight,
                fontSize = resolved_text34.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text34.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 15.6.sp),
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .background(colorResource(R.color.pale_yellow)),
                textAlign = TextAlign.Center
            )
        }
        Section13_2(data, viewModel)
    }
}

@Composable
private fun Section14(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    val resolved_text36 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_with_fixed_vertical_multiple_weights_only),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text36.family,
        fontWeight = resolved_text36.weight,
        fontSize = resolved_text36.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text36.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section15(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    Column(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .requiredHeight(120.dp)
    ) {
        val resolved_text37 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.weight_test_with_fixed_w1),
            color = colorResource(R.color.black),
            fontFamily = resolved_text37.family,
            fontWeight = resolved_text37.weight,
            fontSize = resolved_text37.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text37.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(colorResource(R.color.pale_red)),
            textAlign = TextAlign.Center
        )
        val resolved_text38 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.weight_test_with_fixed_w1),
            color = colorResource(R.color.black),
            fontFamily = resolved_text38.family,
            fontWeight = resolved_text38.weight,
            fontSize = resolved_text38.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text38.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(colorResource(R.color.pale_green)),
            textAlign = TextAlign.Center
        )
        val resolved_text39 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.weight_test_with_fixed_w2),
            color = colorResource(R.color.black),
            fontFamily = resolved_text39.family,
            fontWeight = resolved_text39.weight,
            fontSize = resolved_text39.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text39.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth()
                .background(colorResource(R.color.pale_blue_3)),
            textAlign = TextAlign.Center
        )
        val resolved_text40 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.weight_test_with_fixed_w1),
            color = colorResource(R.color.black),
            fontFamily = resolved_text40.family,
            fontWeight = resolved_text40.weight,
            fontSize = resolved_text40.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text40.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(colorResource(R.color.white_18)),
            textAlign = TextAlign.Center
        )
    }
}
// >>> RESPONSIVE_HELPERS_END