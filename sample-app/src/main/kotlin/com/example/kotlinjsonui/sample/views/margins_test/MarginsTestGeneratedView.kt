package com.example.kotlinjsonui.sample.views.margins_test

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
import com.example.kotlinjsonui.sample.data.MarginsTestData
import com.example.kotlinjsonui.sample.viewmodels.MarginsTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun MarginsTestGeneratedView(
    data: MarginsTestData,
    viewModel: MarginsTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from margins_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "margins_test",
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
                android.util.Log.e("DynamicView", "Error loading margins_test: \$error")
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
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: MarginsTestData,
    viewModel: MarginsTestViewModel
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
        val resolved_button19 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button19.family,
            fontWeight = resolved_button19.weight,
            fontSize = resolved_button19.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button19.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section1(
    data: MarginsTestData,
    viewModel: MarginsTestViewModel
) {
    val resolved_text260 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = "${data.title}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text260.family,
        fontWeight = resolved_text260.weight,
        fontSize = resolved_text260.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text260.style ?: FontStyle.Normal,
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
    data: MarginsTestData,
    viewModel: MarginsTestViewModel
) {
    val resolved_text261 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.margins_test_all_margins_20_20_20_20),
        color = colorResource(R.color.black),
        fontFamily = resolved_text261.family,
        fontWeight = resolved_text261.weight,
        fontSize = resolved_text261.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text261.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 20.dp, end = 20.dp, bottom = 20.dp, start = 20.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.white_5))
    )
}

@Composable
private fun Section3(
    data: MarginsTestData,
    viewModel: MarginsTestViewModel
) {
    val resolved_text262 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.margins_test_left_margin_40),
        color = colorResource(R.color.black),
        fontFamily = resolved_text262.family,
        fontWeight = resolved_text262.weight,
        fontSize = resolved_text262.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text262.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 40.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.white_6))
    )
}

@Composable
private fun Section4(
    data: MarginsTestData,
    viewModel: MarginsTestViewModel
) {
    val resolved_text263 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.margins_test_right_margin_40),
        color = colorResource(R.color.black),
        fontFamily = resolved_text263.family,
        fontWeight = resolved_text263.weight,
        fontSize = resolved_text263.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text263.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(end = 40.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.white_7))
    )
}

@Composable
private fun Section5(
    data: MarginsTestData,
    viewModel: MarginsTestViewModel
) {
    val resolved_text264 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.margins_test_top_margin_30),
        color = colorResource(R.color.black),
        fontFamily = resolved_text264.family,
        fontWeight = resolved_text264.weight,
        fontSize = resolved_text264.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text264.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.white_8))
    )
}

@Composable
private fun Section6(
    data: MarginsTestData,
    viewModel: MarginsTestViewModel
) {
    val resolved_text265 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.margins_test_bottom_margin_30),
        color = colorResource(R.color.black),
        fontFamily = resolved_text265.family,
        fontWeight = resolved_text265.weight,
        fontSize = resolved_text265.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text265.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(bottom = 30.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.white_9))
    )
}

@Composable
private fun Section7(
    data: MarginsTestData,
    viewModel: MarginsTestViewModel
) {
    val resolved_text266 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.margins_test_label_with_padding_20),
        color = colorResource(R.color.black),
        fontFamily = resolved_text266.family,
        fontWeight = resolved_text266.weight,
        fontSize = resolved_text266.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text266.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.white_22))
            .padding(20.dp)
    )
}

@Composable
private fun Section8(
    data: MarginsTestData,
    viewModel: MarginsTestViewModel
) {
    val resolved_text267 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.margins_test_label_with_leftpadding_30_right),
        color = colorResource(R.color.black),
        fontFamily = resolved_text267.family,
        fontWeight = resolved_text267.weight,
        fontSize = resolved_text267.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text267.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.pale_red))
            .padding(start = 30.dp)
            .padding(end = 30.dp)
    )
}

@Composable
private fun Section9(
    data: MarginsTestData,
    viewModel: MarginsTestViewModel
) {
    Box(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.pale_gray_5))
            .padding(start = 20.dp)
            .padding(end = 20.dp)
    ) {
        val resolved_text268 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.margins_test_parent_has_leftpadding_20_right),
            color = colorResource(R.color.black),
            fontFamily = resolved_text268.family,
            fontWeight = resolved_text268.weight,
            fontSize = resolved_text268.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text268.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 18.2.sp),
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
        )
    }
}

@Composable
private fun Section10(
    data: MarginsTestData,
    viewModel: MarginsTestViewModel
) {
    Box(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .height(100.dp)
            .background(colorResource(R.color.pale_gray_4))
    ) {
        val resolved_text269 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.margins_test_maxwidth_200),
            color = colorResource(R.color.black),
            fontFamily = resolved_text269.family,
            fontWeight = resolved_text269.weight,
            fontSize = resolved_text269.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text269.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 18.2.sp),
            modifier = Modifier
                .widthIn(max = 200.dp)
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.pale_red))
        )
    }
}

@Composable
private fun Section11(
    data: MarginsTestData,
    viewModel: MarginsTestViewModel
) {
    Box(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .height(100.dp)
            .background(colorResource(R.color.light_gray_10))
    ) {
        val resolved_text270 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.margins_test_minwidth_150),
            color = colorResource(R.color.black),
            fontFamily = resolved_text270.family,
            fontWeight = resolved_text270.weight,
            fontSize = resolved_text270.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text270.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 18.2.sp),
            modifier = Modifier
                .widthIn(min = 150.dp)
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.pale_green))
        )
    }
}
// >>> RESPONSIVE_HELPERS_END