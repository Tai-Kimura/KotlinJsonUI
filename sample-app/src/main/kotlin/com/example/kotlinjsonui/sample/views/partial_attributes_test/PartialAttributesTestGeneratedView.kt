package com.example.kotlinjsonui.sample.views.partial_attributes_test

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
import com.example.kotlinjsonui.sample.data.PartialAttributesTestData
import com.example.kotlinjsonui.sample.viewmodels.PartialAttributesTestViewModel
import com.kotlinjsonui.components.PartialAttribute
import com.kotlinjsonui.components.PartialAttributesText
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
fun PartialAttributesTestGeneratedView(
    data: PartialAttributesTestData,
    viewModel: PartialAttributesTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from partial_attributes_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
        // Check if Dynamic Mode is active
        if (DynamicModeManager.isActive()) {
            // Dynamic Mode - use SafeDynamicView for real-time updates
            SafeDynamicView(
                layoutName = "partial_attributes_test",
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
                    android.util.Log.e("DynamicView", "Error loading partial_attributes_test: \$error")
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
        Section6(data, viewModel, modifier)    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("partial_attributes_test")
    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: PartialAttributesTestData,
    viewModel: PartialAttributesTestViewModel
) {
    val resolved_text1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.partial_attributes_test_partialattributes_test),
        fontFamily = resolved_text1.family,
        fontWeight = resolved_text1.weight,
        fontSize = resolved_text1.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text1.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 31.2.sp),
        modifier = Modifier.padding(bottom = 20.dp)
    )
}

@Composable
private fun Section1(
    data: PartialAttributesTestData,
    viewModel: PartialAttributesTestViewModel
) {
    val resolved_text2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.partial_attributes_test_this_is_a_normal_label_without_),
        fontFamily = resolved_text2.family,
        fontWeight = resolved_text2.weight,
        fontSize = resolved_text2.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text2.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier.padding(bottom = 20.dp)
    )
}

@Composable
private fun Section2(
    data: PartialAttributesTestData,
    viewModel: PartialAttributesTestViewModel
) {
    val resolved_text3 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    PartialAttributesText(
        text = stringResource(R.string.partial_attributes_test_this_text_has_partial_styling_a),
        partialAttributes = listOf(
            PartialAttribute.fromJsonRange(
                range = "partial",
                text = stringResource(R.string.partial_attributes_test_this_text_has_partial_styling_a),
                fontColor = "dark_red",
                fontWeight = "bold",
                onClick = null
            )!!,
            PartialAttribute.fromJsonRange(
                range = "styling",
                text = stringResource(R.string.partial_attributes_test_this_text_has_partial_styling_a),
                fontColor = "dark_green_2",
                underline = true,
                onClick = null
            )!!,
            PartialAttribute.fromJsonRange(
                range = " part",
                text = stringResource(R.string.partial_attributes_test_this_text_has_partial_styling_a),
                fontColor = "dark_blue",
                fontSize = 20,
                background = "light_2",
                onClick = null
            )!!
        ),
        modifier = Modifier.padding(bottom = 20.dp),
        style = LocalTextStyle.current.copy(fontFamily = (resolved_text3.family ?: LocalTextStyle.current.fontFamily), fontWeight = (resolved_text3.weight ?: LocalTextStyle.current.fontWeight), fontSize = (resolved_text3.size ?: LocalTextStyle.current.fontSize), fontStyle = (resolved_text3.style ?: LocalTextStyle.current.fontStyle))
    )
}

@Composable
private fun Section3(
    data: PartialAttributesTestData,
    viewModel: PartialAttributesTestViewModel
) {
    val resolved_text4 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    PartialAttributesText(
        text = stringResource(R.string.partial_attributes_test_click_here_to_navigate_or_here_),
        partialAttributes = listOf(
            PartialAttribute.fromJsonRange(
                range = "here",
                text = stringResource(R.string.partial_attributes_test_click_here_to_navigate_or_here_),
                fontColor = "dark_blue",
                underline = true,
                onClick = { data.navigateToPage1?.invoke() }
            )!!,
            PartialAttribute.fromJsonRange(
                range = "ere ",
                text = stringResource(R.string.partial_attributes_test_click_here_to_navigate_or_here_),
                fontColor = "dark_blue",
                underline = true,
                onClick = { data.navigateToPage2?.invoke() }
            )!!
        ),
        modifier = Modifier.padding(bottom = 20.dp),
        style = LocalTextStyle.current.copy(fontFamily = (resolved_text4.family ?: LocalTextStyle.current.fontFamily), fontWeight = (resolved_text4.weight ?: LocalTextStyle.current.fontWeight), fontSize = (resolved_text4.size ?: LocalTextStyle.current.fontSize), fontStyle = (resolved_text4.style ?: LocalTextStyle.current.fontStyle))
    )
}

@Composable
private fun Section4(
    data: PartialAttributesTestData,
    viewModel: PartialAttributesTestViewModel
) {
    val resolved_text5 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    PartialAttributesText(
        text = stringResource(R.string.partial_attributes_test_mixed_styles_bold_italic_underl),
        partialAttributes = listOf(
            PartialAttribute.fromJsonRange(
                range = "bold",
                text = stringResource(R.string.partial_attributes_test_mixed_styles_bold_italic_underl),
                fontWeight = "bold",
                onClick = null
            )!!,
            PartialAttribute.fromJsonRange(
                range = "italic",
                text = stringResource(R.string.partial_attributes_test_mixed_styles_bold_italic_underl),
                fontColor = "light",
                onClick = null
            )!!,
            PartialAttribute.fromJsonRange(
                range = "underline",
                text = stringResource(R.string.partial_attributes_test_mixed_styles_bold_italic_underl),
                underline = true,
                onClick = null
            )!!,
            PartialAttribute.fromJsonRange(
                range = "strikethrough",
                text = stringResource(R.string.partial_attributes_test_mixed_styles_bold_italic_underl),
                fontColor = "light_gray_8",
                strikethrough = true,
                onClick = null
            )!!
        ),
        modifier = Modifier,
        style = LocalTextStyle.current.copy(fontFamily = (resolved_text5.family ?: LocalTextStyle.current.fontFamily), fontWeight = (resolved_text5.weight ?: LocalTextStyle.current.fontWeight), fontSize = (resolved_text5.size ?: LocalTextStyle.current.fontSize), fontStyle = (resolved_text5.style ?: LocalTextStyle.current.fontStyle))
    )
}

@Composable
private fun Section5(
    data: PartialAttributesTestData,
    viewModel: PartialAttributesTestViewModel
) {
    val resolved_text6 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    PartialAttributesText(
        text = stringResource(R.string.partial_attributes_test_今日は天気がいいですね_明日も晴れるといいです),
        partialAttributes = listOf(
            PartialAttribute.fromJsonRange(
                range = "天気",
                text = stringResource(R.string.partial_attributes_test_今日は天気がいいですね_明日も晴れるといいです),
                fontColor = "dark_red",
                fontSize = 20,
                fontWeight = "bold",
                onClick = null
            )!!,
            PartialAttribute.fromJsonRange(
                range = stringResource(R.string.partial_attributes_test_晴れる),
                text = stringResource(R.string.partial_attributes_test_今日は天気がいいですね_明日も晴れるといいです),
                fontColor = "dark_blue",
                underline = true,
                onClick = null
            )!!
        ),
        modifier = Modifier.padding(bottom = 20.dp),
        style = LocalTextStyle.current.copy(fontFamily = (resolved_text6.family ?: LocalTextStyle.current.fontFamily), fontWeight = (resolved_text6.weight ?: LocalTextStyle.current.fontWeight), fontSize = (resolved_text6.size ?: LocalTextStyle.current.fontSize), fontStyle = (resolved_text6.style ?: LocalTextStyle.current.fontStyle))
    )
}

@Composable
private fun Section6(
    data: PartialAttributesTestData,
    viewModel: PartialAttributesTestViewModel,
    modifier: Modifier
) {
    val safeAreaConfig = LocalSafeAreaConfig.current
    val edges = mutableListOf("all").apply {
        if (safeAreaConfig.ignoreBottom) {
            remove("bottom")
            if (contains("all")) { remove("all"); addAll(listOf("top", "start", "end")) }
        }
        if (safeAreaConfig.ignoreTop) {
            remove("top")
            if (contains("all")) { remove("all"); addAll(listOf("bottom", "start", "end")) }
        }
    }.distinct()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(R.color.white))
            .then(if (edges.contains("all")) Modifier.systemBarsPadding() else Modifier)
            .then(if (!edges.contains("all") && edges.contains("top")) Modifier.statusBarsPadding() else Modifier)
            .then(if (!edges.contains("all") && edges.contains("bottom")) Modifier.navigationBarsPadding() else Modifier)
            .imePadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .imePadding()
        ) {
            item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp)
            ) {
                Section0(data, viewModel)
                Section1(data, viewModel)
                Section2(data, viewModel)
                Section3(data, viewModel)
                Section4(data, viewModel)
                Section5(data, viewModel)
            }
            }
        }
    }
}
// >>> RESPONSIVE_HELPERS_END