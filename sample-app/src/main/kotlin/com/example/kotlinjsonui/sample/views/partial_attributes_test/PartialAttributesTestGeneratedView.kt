package com.example.kotlinjsonui.sample.views.partial_attributes_test

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
import com.example.kotlinjsonui.sample.data.PartialAttributesTestData
import com.example.kotlinjsonui.sample.viewmodels.PartialAttributesTestViewModel
import com.kotlinjsonui.components.PartialAttribute
import com.kotlinjsonui.components.PartialAttributesText
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont
import com.kotlinjsonui.dynamic.LocalSafeAreaConfig
import com.kotlinjsonui.dynamic.SafeAreaConfig

@Composable
fun PartialAttributesTestGeneratedView(
    data: PartialAttributesTestData,
    viewModel: PartialAttributesTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from partial_attributes_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
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
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: PartialAttributesTestData,
    viewModel: PartialAttributesTestViewModel
) {
    val resolved_text325 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.partial_attributes_test_partialattributes_test),
        fontFamily = resolved_text325.family,
        fontWeight = resolved_text325.weight,
        fontSize = resolved_text325.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text325.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 31.2.sp),
        modifier = Modifier.padding(bottom = 20.dp)
    )
}

@Composable
private fun Section1(
    data: PartialAttributesTestData,
    viewModel: PartialAttributesTestViewModel
) {
    val resolved_text326 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.partial_attributes_test_this_is_a_normal_label_without_),
        fontFamily = resolved_text326.family,
        fontWeight = resolved_text326.weight,
        fontSize = resolved_text326.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text326.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier.padding(bottom = 20.dp)
    )
}

@Composable
private fun Section2(
    data: PartialAttributesTestData,
    viewModel: PartialAttributesTestViewModel
) {
    val resolved_text327 = Configuration.Font.resolve(FontSpec(
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
        style = TextStyle(fontFamily = resolved_text327.family, fontWeight = resolved_text327.weight, fontSize = (resolved_text327.size ?: TextUnit.Unspecified), fontStyle = (resolved_text327.style ?: FontStyle.Normal))
    )
}

@Composable
private fun Section3(
    data: PartialAttributesTestData,
    viewModel: PartialAttributesTestViewModel
) {
    val resolved_text328 = Configuration.Font.resolve(FontSpec(
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
        style = TextStyle(fontFamily = resolved_text328.family, fontWeight = resolved_text328.weight, fontSize = (resolved_text328.size ?: TextUnit.Unspecified), fontStyle = (resolved_text328.style ?: FontStyle.Normal))
    )
}

@Composable
private fun Section4(
    data: PartialAttributesTestData,
    viewModel: PartialAttributesTestViewModel
) {
    val resolved_text329 = Configuration.Font.resolve(FontSpec(
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
        style = TextStyle(fontFamily = resolved_text329.family, fontWeight = resolved_text329.weight, fontSize = (resolved_text329.size ?: TextUnit.Unspecified), fontStyle = (resolved_text329.style ?: FontStyle.Normal))
    )
}

@Composable
private fun Section5(
    data: PartialAttributesTestData,
    viewModel: PartialAttributesTestViewModel
) {
    val resolved_text330 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    PartialAttributesText(
        text = "今日は天気がいいですね。明日も晴れるといいです。",
        partialAttributes = listOf(
            PartialAttribute.fromJsonRange(
                range = "天気",
                text = "今日は天気がいいですね。明日も晴れるといいです。",
                fontColor = "dark_red",
                fontSize = 20,
                fontWeight = "bold",
                onClick = null
            )!!,
            PartialAttribute.fromJsonRange(
                range = stringResource(R.string.partial_attributes_test_),
                text = "今日は天気がいいですね。明日も晴れるといいです。",
                fontColor = "dark_blue",
                underline = true,
                onClick = null
            )!!
        ),
        modifier = Modifier.padding(bottom = 20.dp),
        style = TextStyle(fontFamily = resolved_text330.family, fontWeight = resolved_text330.weight, fontSize = (resolved_text330.size ?: TextUnit.Unspecified), fontStyle = (resolved_text330.style ?: FontStyle.Normal))
    )
}
// >>> RESPONSIVE_HELPERS_END