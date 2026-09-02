package com.example.kotlinjsonui.sample.views.visibility_test

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
import androidx.compose.ui.draw.alpha
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
import com.example.kotlinjsonui.sample.data.VisibilityTestData
import com.example.kotlinjsonui.sample.viewmodels.VisibilityTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.components.VisibilityWrapper
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun VisibilityTestGeneratedView(
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from visibility_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
        // Check if Dynamic Mode is active
        if (DynamicModeManager.isActive()) {
            // Dynamic Mode - use SafeDynamicView for real-time updates
            SafeDynamicView(
                layoutName = "visibility_test",
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
                    android.util.Log.e("DynamicView", "Error loading visibility_test: \$error")
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
            }
            }
        }    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("visibility_test")
    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
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
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
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
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
) {
    VisibilityWrapper(
        visibility = "visible",
    ) {
    VisibilityWrapper(
        visibility = "visible",
    ) {
    val resolved_text2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.visibility_test_visibility_visible_default),
        color = colorResource(R.color.black),
        fontFamily = resolved_text2.family,
        fontWeight = resolved_text2.weight,
        fontSize = resolved_text2.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text2.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.pale_yellow))
    )
    }
    }
}

@Composable
private fun Section3(
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
) {
    VisibilityWrapper(
        visibility = "invisible",
    ) {
    VisibilityWrapper(
        visibility = "invisible",
    ) {
    val resolved_text3 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.visibility_test_this_label_is_invisible_takes_s),
        color = colorResource(R.color.black),
        fontFamily = resolved_text3.family,
        fontWeight = resolved_text3.weight,
        fontSize = resolved_text3.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text3.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.pale_pink))
    )
    }
    }
}

@Composable
private fun Section4(
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
) {
    val resolved_text4 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.visibility_test_after_invisible_label),
        color = colorResource(R.color.black),
        fontFamily = resolved_text4.family,
        fontWeight = resolved_text4.weight,
        fontSize = resolved_text4.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text4.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.pale_cyan))
    )
}

@Composable
private fun Section5(
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
) {
    val resolved_text5 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.visibility_test_after_gone_label_no_gap),
        color = colorResource(R.color.black),
        fontFamily = resolved_text5.family,
        fontWeight = resolved_text5.weight,
        fontSize = resolved_text5.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text5.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.white_3))
    )
}

@Composable
private fun Section6(
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
) {
    val resolved_text6 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.visibility_test_opacity_tests),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text6.family,
        fontWeight = resolved_text6.weight,
        fontSize = resolved_text6.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text6.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section7(
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
) {
    val resolved_text7 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.visibility_test_opacity_10_fully_visible),
        color = colorResource(R.color.black),
        fontFamily = resolved_text7.family,
        fontWeight = resolved_text7.weight,
        fontSize = resolved_text7.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text7.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier
            .alpha(1.0f)
            .padding(top = 10.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.white_5))
    )
}

@Composable
private fun Section8(
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
) {
    val resolved_text8 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.visibility_test_opacity_07),
        color = colorResource(R.color.black),
        fontFamily = resolved_text8.family,
        fontWeight = resolved_text8.weight,
        fontSize = resolved_text8.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text8.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier
            .alpha(0.7f)
            .padding(top = 10.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.white_6))
    )
}

@Composable
private fun Section9(
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
) {
    val resolved_text9 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.visibility_test_opacity_05),
        color = colorResource(R.color.black),
        fontFamily = resolved_text9.family,
        fontWeight = resolved_text9.weight,
        fontSize = resolved_text9.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text9.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier
            .alpha(0.5f)
            .padding(top = 10.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.white_7))
    )
}

@Composable
private fun Section10(
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
) {
    val resolved_text10 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.visibility_test_opacity_03),
        color = colorResource(R.color.black),
        fontFamily = resolved_text10.family,
        fontWeight = resolved_text10.weight,
        fontSize = resolved_text10.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text10.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier
            .alpha(0.3f)
            .padding(top = 10.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.white_8))
    )
}

@Composable
private fun Section11(
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
) {
    val resolved_text11 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.visibility_test_opacity_01),
        color = colorResource(R.color.black),
        fontFamily = resolved_text11.family,
        fontWeight = resolved_text11.weight,
        fontSize = resolved_text11.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text11.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier
            .alpha(0.1f)
            .padding(top = 10.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.white_3))
    )
}

@Composable
private fun Section12(
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
) {
    val resolved_text12 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.visibility_test_alpha_test_same_as_opacity),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text12.family,
        fontWeight = resolved_text12.weight,
        fontSize = resolved_text12.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text12.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section13(
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
) {
    val resolved_text13 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.visibility_test_alpha_06),
        color = colorResource(R.color.black),
        fontFamily = resolved_text13.family,
        fontWeight = resolved_text13.weight,
        fontSize = resolved_text13.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text13.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier
            .alpha(0.6f)
            .padding(top = 10.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.white_4))
    )
}

@Composable
private fun Section14(
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
) {
    val resolved_text14 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.visibility_test_dynamic_visibility_tests),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text14.family,
        fontWeight = resolved_text14.weight,
        fontSize = resolved_text14.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text14.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section15(
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
) {
    Button(
        onClick = { data.toggleVisibility?.invoke() },
        modifier = Modifier.padding(top = 10.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.medium_blue),
                            disabledContainerColor = colorResource(R.color.medium_blue).copy(alpha = 0.5f),
                            contentColor = colorResource(R.color.white),
                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                        )
    ) {
        Text(stringResource(R.string.visibility_test_toggle_visibility))
    }
}

@Composable
private fun Section16(
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
) {
    VisibilityWrapper(
        visibility = data.textVisibility,
    ) {
    VisibilityWrapper(
        visibility = data.textVisibility,
    ) {
    val resolved_text15 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.visibility_test_this_text_uses_dynamic_visibili),
        color = colorResource(R.color.black),
        fontFamily = resolved_text15.family,
        fontWeight = resolved_text15.weight,
        fontSize = resolved_text15.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text15.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.white_5))
            .padding(10.dp)
    )
    }
    }
}

@Composable
private fun Section17(
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
) {
    Button(
        onClick = { data.toggleHidden?.invoke() },
        modifier = Modifier.padding(top = 20.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.medium_green),
                            disabledContainerColor = colorResource(R.color.medium_green).copy(alpha = 0.5f),
                            contentColor = colorResource(R.color.white),
                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                        )
    ) {
        Text(stringResource(R.string.visibility_test_toggle_hidden))
    }
}

@Composable
private fun Section18(
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
) {
    VisibilityWrapper(
        hidden = data.isHidden,
    ) {
    VisibilityWrapper(
        hidden = data.isHidden,
    ) {
    val resolved_text16 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.visibility_test_this_text_uses_dynamic_hidden_a),
        color = colorResource(R.color.black),
        fontFamily = resolved_text16.family,
        fontWeight = resolved_text16.weight,
        fontSize = resolved_text16.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text16.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.white_6))
            .padding(10.dp)
    )
    }
    }
}
// >>> RESPONSIVE_HELPERS_END