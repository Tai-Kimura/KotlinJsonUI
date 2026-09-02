package com.example.kotlinjsonui.sample.views.custom_component_test

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
import com.example.kotlinjsonui.sample.data.CustomComponentTestData
import com.example.kotlinjsonui.sample.viewmodels.CustomComponentTestViewModel
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
fun CustomComponentTestGeneratedView(
    data: CustomComponentTestData,
    viewModel: CustomComponentTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from custom_component_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
        // Check if Dynamic Mode is active
        if (DynamicModeManager.isActive()) {
            // Dynamic Mode - use SafeDynamicView for real-time updates
            SafeDynamicView(
                layoutName = "custom_component_test",
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
                    android.util.Log.e("DynamicView", "Error loading custom_component_test: \$error")
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
        Section22(data, viewModel, modifier)    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("custom_component_test")
    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: CustomComponentTestData,
    viewModel: CustomComponentTestViewModel
) {
    val resolved_text1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 28.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.custom_component_test_custom_component_test),
        color = colorResource(R.color.black),
        fontFamily = resolved_text1.family,
        fontWeight = resolved_text1.weight,
        fontSize = resolved_text1.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text1.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 36.4.sp),
        modifier = Modifier.padding(top = 0.dp, end = 0.dp, bottom = 20.dp, start = 0.dp)
    )
}

@Composable
private fun Section1(
    data: CustomComponentTestData,
    viewModel: CustomComponentTestViewModel
) {
    val resolved_text2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.custom_component_test_testing_samplecard_static_value),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text2.family,
        fontWeight = resolved_text2.weight,
        fontSize = resolved_text2.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text2.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 0.dp, end = 0.dp, bottom = 8.dp, start = 0.dp)
    )
}

@Composable
private fun Section3(
    data: CustomComponentTestData,
    viewModel: CustomComponentTestViewModel
) {
    val resolved_text3 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.custom_component_test_testing_samplecard_dynamic_valu),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text3.family,
        fontWeight = resolved_text3.weight,
        fontSize = resolved_text3.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text3.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 20.dp, end = 0.dp, bottom = 8.dp, start = 0.dp)
    )
}

@Composable
private fun Section5(
    data: CustomComponentTestData,
    viewModel: CustomComponentTestViewModel
) {
    val resolved_text4 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.custom_component_test_testing_multiple_cards),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text4.family,
        fontWeight = resolved_text4.weight,
        fontSize = resolved_text4.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text4.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 20.dp, end = 0.dp, bottom = 8.dp, start = 0.dp)
    )
}

@Composable
private fun Section6(
    data: CustomComponentTestData,
    viewModel: CustomComponentTestViewModel
) {
                    Column(
                    ) {
    // TODO: Implement component type: SampleCard
    // TODO: Implement component type: SampleCard
    // TODO: Implement component type: SampleCard
                    }
}

@Composable
private fun Section7(
    data: CustomComponentTestData,
    viewModel: CustomComponentTestViewModel
) {
    val resolved_text5 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.custom_component_test_testing_statusbadge_noncontaine),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text5.family,
        fontWeight = resolved_text5.weight,
        fontSize = resolved_text5.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text5.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 20.dp, end = 0.dp, bottom = 8.dp, start = 0.dp)
    )
}

@Composable
private fun Section8(
    data: CustomComponentTestData,
    viewModel: CustomComponentTestViewModel
) {
                    Column(
                    ) {
    // TODO: Implement component type: StatusBadge
    // TODO: Implement component type: StatusBadge
    // TODO: Implement component type: StatusBadge
                    }
}

@Composable
private fun Section9(
    data: CustomComponentTestData,
    viewModel: CustomComponentTestViewModel
) {
    Button(
        onClick = { data.toggleDynamicMode?.invoke() },
        modifier = Modifier.wrapContentWidth(),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
        colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.medium_blue_3),
                                disabledContainerColor = colorResource(R.color.medium_blue_3).copy(alpha = 0.5f),
                                contentColor = colorResource(R.color.white),
                                disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                            )
    ) {
        Text(stringResource(R.string.custom_component_test_toggle_dynamic_mode))
    }
}

@Composable
private fun Section10(
    data: CustomComponentTestData,
    viewModel: CustomComponentTestViewModel
) {
    val resolved_text6 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.dynamicModeStatus}",
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text6.family,
        fontWeight = resolved_text6.weight,
        fontSize = resolved_text6.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text6.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section22(
    data: CustomComponentTestData,
    viewModel: CustomComponentTestViewModel,
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
                .background(colorResource(R.color.white_12))
                .then(if (edges.contains("all")) Modifier.systemBarsPadding() else Modifier)
                .then(if (!edges.contains("all") && edges.contains("top")) Modifier.statusBarsPadding() else Modifier)
                .then(if (!edges.contains("all") && edges.contains("bottom")) Modifier.navigationBarsPadding() else Modifier)
                .imePadding()
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .imePadding()
            ) {
                item {
                Column(
                ) {
                    Section0(data, viewModel)
                    Section1(data, viewModel)
    // TODO: Implement component type: SampleCard
                    Section3(data, viewModel)
    // TODO: Implement component type: SampleCard
                    Section5(data, viewModel)
                    Section6(data, viewModel)
                    Section7(data, viewModel)
                    Section8(data, viewModel)
                    Section9(data, viewModel)
                    Section10(data, viewModel)
                }
                }
            }
        }
}
// >>> RESPONSIVE_HELPERS_END