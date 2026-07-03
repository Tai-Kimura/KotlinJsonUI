package com.example.kotlinjsonui.sample.views.text_styling_test

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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.TextStylingTestData
import com.example.kotlinjsonui.sample.viewmodels.TextStylingTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun TextStylingTestGeneratedView(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from text_styling_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "text_styling_test",
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
                android.util.Log.e("DynamicView", "Error loading text_styling_test: \$error")
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
            Section16(data, viewModel)
            Section17(data, viewModel)
            Section18(data, viewModel)
            Section19(data, viewModel)
            Section20(data, viewModel)
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
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
        val resolved_button6 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button6.family,
            fontWeight = resolved_button6.weight,
            fontSize = resolved_button6.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button6.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section1(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    val resolved_text43 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = "${data.title}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text43.family,
        fontWeight = resolved_text43.weight,
        fontSize = resolved_text43.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text43.style ?: FontStyle.Normal,
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
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    val resolved_text44 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_styling_test_font_sizes),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text44.family,
        fontWeight = resolved_text44.weight,
        fontSize = resolved_text44.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text44.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section3(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    val resolved_text45 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 12.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_styling_test_fontsize_12),
        color = colorResource(R.color.black),
        fontFamily = resolved_text45.family,
        fontWeight = resolved_text45.weight,
        fontSize = resolved_text45.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text45.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 15.6.sp),
        modifier = Modifier.padding(top = 10.dp)
    )
}

@Composable
private fun Section4(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    val resolved_text46 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_styling_test_fontsize_16),
        color = colorResource(R.color.black),
        fontFamily = resolved_text46.family,
        fontWeight = resolved_text46.weight,
        fontSize = resolved_text46.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text46.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier.padding(top = 5.dp)
    )
}

@Composable
private fun Section5(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    val resolved_text47 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 20.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_styling_test_fontsize_20),
        color = colorResource(R.color.black),
        fontFamily = resolved_text47.family,
        fontWeight = resolved_text47.weight,
        fontSize = resolved_text47.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text47.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 26.0.sp),
        modifier = Modifier.padding(top = 5.dp)
    )
}

@Composable
private fun Section6(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    val resolved_text48 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_styling_test_fontsize_24),
        color = colorResource(R.color.black),
        fontFamily = resolved_text48.family,
        fontWeight = resolved_text48.weight,
        fontSize = resolved_text48.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text48.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 31.2.sp),
        modifier = Modifier.padding(top = 5.dp)
    )
}

@Composable
private fun Section7(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    val resolved_text49 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_styling_test_font_styles),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text49.family,
        fontWeight = resolved_text49.weight,
        fontSize = resolved_text49.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text49.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section8(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    val resolved_text50 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_styling_test_bold_text),
        color = colorResource(R.color.black),
        fontFamily = resolved_text50.family,
        fontWeight = resolved_text50.weight,
        fontSize = resolved_text50.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text50.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier.padding(top = 10.dp)
    )
}

@Composable
private fun Section9(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    val resolved_text51 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_styling_test_underlined_text),
        color = colorResource(R.color.black),
        fontFamily = resolved_text51.family,
        fontWeight = resolved_text51.weight,
        fontSize = resolved_text51.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text51.style ?: FontStyle.Normal,
        textDecoration = TextDecoration.Underline,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier.padding(top = 10.dp)
    )
}

@Composable
private fun Section10(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    val resolved_text52 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_styling_test_text_alignment),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text52.family,
        fontWeight = resolved_text52.weight,
        fontSize = resolved_text52.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text52.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section11(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    val resolved_text53 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_styling_test_left_aligned_default),
        color = colorResource(R.color.black),
        fontFamily = resolved_text53.family,
        fontWeight = resolved_text53.weight,
        fontSize = resolved_text53.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text53.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .background(colorResource(R.color.pale_gray))
            .padding(5.dp),
        textAlign = TextAlign.Start
    )
}

@Composable
private fun Section12(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    val resolved_text54 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_styling_test_center_aligned),
        color = colorResource(R.color.black),
        fontFamily = resolved_text54.family,
        fontWeight = resolved_text54.weight,
        fontSize = resolved_text54.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text54.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .background(colorResource(R.color.pale_gray))
            .padding(5.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section13(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    val resolved_text55 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_styling_test_right_aligned),
        color = colorResource(R.color.black),
        fontFamily = resolved_text55.family,
        fontWeight = resolved_text55.weight,
        fontSize = resolved_text55.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text55.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .background(colorResource(R.color.pale_gray))
            .padding(5.dp),
        textAlign = TextAlign.End
    )
}

@Composable
private fun Section14(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    val resolved_text56 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_styling_test_line_spacing_test),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text56.family,
        fontWeight = resolved_text56.weight,
        fontSize = resolved_text56.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text56.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section15(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    val resolved_text57 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_styling_test_this_is_a_multiline_text_with_l),
        color = colorResource(R.color.black),
        fontFamily = resolved_text57.family,
        fontWeight = resolved_text57.weight,
        fontSize = resolved_text57.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text57.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 19.0.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .background(colorResource(R.color.white))
            .padding(10.dp)
    )
}

@Composable
private fun Section16(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    val resolved_text58 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_styling_test_this_is_another_multiline_text_),
        color = colorResource(R.color.black),
        fontFamily = resolved_text58.family,
        fontWeight = resolved_text58.weight,
        fontSize = resolved_text58.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text58.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 24.0.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .background(colorResource(R.color.white))
            .padding(10.dp)
    )
}

@Composable
private fun Section17(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    val resolved_text59 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_styling_test_font_colors),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text59.family,
        fontWeight = resolved_text59.weight,
        fontSize = resolved_text59.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text59.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section18(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    val resolved_text60 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_styling_test_red_text),
        color = colorResource(R.color.dark_red),
        fontFamily = resolved_text60.family,
        fontWeight = resolved_text60.weight,
        fontSize = resolved_text60.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text60.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier.padding(top = 10.dp)
    )
}

@Composable
private fun Section19(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    val resolved_text61 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_styling_test_green_text),
        color = colorResource(R.color.dark_green_2),
        fontFamily = resolved_text61.family,
        fontWeight = resolved_text61.weight,
        fontSize = resolved_text61.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text61.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier.padding(top = 5.dp)
    )
}

@Composable
private fun Section20(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    val resolved_text62 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_styling_test_blue_text),
        color = colorResource(R.color.dark_blue),
        fontFamily = resolved_text62.family,
        fontWeight = resolved_text62.weight,
        fontSize = resolved_text62.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text62.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier.padding(top = 5.dp)
    )
}
// >>> RESPONSIVE_HELPERS_END