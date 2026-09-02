package com.example.kotlinjsonui.sample.views.segment_test

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
import com.example.kotlinjsonui.sample.data.SegmentTestData
import com.example.kotlinjsonui.sample.viewmodels.SegmentTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.components.Segment
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.dynamic.LocalSafeAreaConfig
import com.kotlinjsonui.dynamic.SafeAreaConfig
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun SegmentTestGeneratedView(
    data: SegmentTestData,
    viewModel: SegmentTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from segment_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
        // Check if Dynamic Mode is active
        if (DynamicModeManager.isActive()) {
            // Dynamic Mode - use SafeDynamicView for real-time updates
            SafeDynamicView(
                layoutName = "segment_test",
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
                    android.util.Log.e("DynamicView", "Error loading segment_test: \$error")
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
        Section20(data, viewModel, modifier)    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("segment_test")
    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section1(
    data: SegmentTestData,
    viewModel: SegmentTestViewModel
) {
    val resolved_text2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.segment_test_basic_segment),
        fontFamily = resolved_text2.family,
        fontWeight = resolved_text2.weight,
        fontSize = resolved_text2.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text2.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
    )
}

@Composable
private fun Section2(
    data: SegmentTestData,
    viewModel: SegmentTestViewModel
) {
    Segment(
        selectedTabIndex = data.selectedBasic,
        containerColor = Color.Transparent,
        modifier = Modifier
            .testTag("basicSegment")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
    ) {
        Tab(
            selected = (data.selectedBasic == 0),
            onClick = {
                viewModel.updateData(mapOf("selectedBasic" to 0))
            },
            text = { Text(stringResource(R.string.segment_test_option_1)) }
        )
        Tab(
            selected = (data.selectedBasic == 1),
            onClick = {
                viewModel.updateData(mapOf("selectedBasic" to 1))
            },
            text = { Text(stringResource(R.string.segment_test_option_2)) }
        )
        Tab(
            selected = (data.selectedBasic == 2),
            onClick = {
                viewModel.updateData(mapOf("selectedBasic" to 2))
            },
            text = { Text(stringResource(R.string.segment_test_option_3)) }
        )
    }
}

@Composable
private fun Section3(
    data: SegmentTestData,
    viewModel: SegmentTestViewModel
) {
    val resolved_text3 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.segment_test_segment_with_custom_colors),
        fontFamily = resolved_text3.family,
        fontWeight = resolved_text3.weight,
        fontSize = resolved_text3.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text3.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .padding(start = 20.dp)
    )
}

@Composable
private fun Section4(
    data: SegmentTestData,
    viewModel: SegmentTestViewModel
) {
    Segment(
        selectedTabIndex = data.selectedColor,
        containerColor = Color.Transparent,
        modifier = Modifier
            .testTag("colorSegment")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
    ) {
        Tab(
            selected = (data.selectedColor == 0),
            onClick = {
                viewModel.updateData(mapOf("selectedColor" to 0))
            },
            text = { Text(stringResource(R.string.segment_test_red)) }
        )
        Tab(
            selected = (data.selectedColor == 1),
            onClick = {
                viewModel.updateData(mapOf("selectedColor" to 1))
            },
            text = { Text(stringResource(R.string.segment_test_green)) }
        )
        Tab(
            selected = (data.selectedColor == 2),
            onClick = {
                viewModel.updateData(mapOf("selectedColor" to 2))
            },
            text = { Text(stringResource(R.string.segment_test_blue)) }
        )
    }
}

@Composable
private fun Section5(
    data: SegmentTestData,
    viewModel: SegmentTestViewModel
) {
    val resolved_text4 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.segment_test_segment_with_onchange_event),
        fontFamily = resolved_text4.family,
        fontWeight = resolved_text4.weight,
        fontSize = resolved_text4.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text4.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .padding(start = 20.dp)
    )
}

@Composable
private fun Section6(
    data: SegmentTestData,
    viewModel: SegmentTestViewModel
) {
    Segment(
        selectedTabIndex = data.selectedEvent,
        containerColor = Color.Transparent,
        modifier = Modifier
            .testTag("eventSegment")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
    ) {
        Tab(
            selected = (data.selectedEvent == 0),
            onClick = {
                viewModel.updateData(mapOf("selectedEvent" to 0))
                data.handleSegmentChange?.invoke("eventSegment", 0)
            },
            text = { Text(stringResource(R.string.segment_test_small)) }
        )
        Tab(
            selected = (data.selectedEvent == 1),
            onClick = {
                viewModel.updateData(mapOf("selectedEvent" to 1))
                data.handleSegmentChange?.invoke("eventSegment", 1)
            },
            text = { Text(stringResource(R.string.segment_test_medium)) }
        )
        Tab(
            selected = (data.selectedEvent == 2),
            onClick = {
                viewModel.updateData(mapOf("selectedEvent" to 2))
                data.handleSegmentChange?.invoke("eventSegment", 2)
            },
            text = { Text(stringResource(R.string.segment_test_large)) }
        )
        Tab(
            selected = (data.selectedEvent == 3),
            onClick = {
                viewModel.updateData(mapOf("selectedEvent" to 3))
                data.handleSegmentChange?.invoke("eventSegment", 3)
            },
            text = { Text(stringResource(R.string.segment_test_extra_large)) }
        )
    }
}

@Composable
private fun Section7(
    data: SegmentTestData,
    viewModel: SegmentTestViewModel
) {
    val resolved_text5 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.selectedSize}",
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text5.family,
        fontWeight = resolved_text5.weight,
        fontSize = resolved_text5.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text5.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier
            .testTag("segmentStatus")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
    )
}

@Composable
private fun Section8(
    data: SegmentTestData,
    viewModel: SegmentTestViewModel
) {
    val resolved_text6 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.segment_test_disabled_segment),
        fontFamily = resolved_text6.family,
        fontWeight = resolved_text6.weight,
        fontSize = resolved_text6.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text6.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .padding(start = 20.dp)
    )
}

@Composable
private fun Section9(
    data: SegmentTestData,
    viewModel: SegmentTestViewModel
) {
    Segment(
        selectedTabIndex = data.selectedDisabled,
        enabled = false,
        containerColor = Color.Transparent,
        modifier = Modifier
            .testTag("disabledSegment")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
    ) {
        Tab(
            selected = (data.selectedDisabled == 0),
            enabled = false,
            onClick = {
                viewModel.updateData(mapOf("selectedDisabled" to 0))
            },
            text = { Text(stringResource(R.string.segment_test_disabled_1)) }
        )
        Tab(
            selected = (data.selectedDisabled == 1),
            enabled = false,
            onClick = {
                viewModel.updateData(mapOf("selectedDisabled" to 1))
            },
            text = { Text(stringResource(R.string.segment_test_disabled_2)) }
        )
        Tab(
            selected = (data.selectedDisabled == 2),
            enabled = false,
            onClick = {
                viewModel.updateData(mapOf("selectedDisabled" to 2))
            },
            text = { Text(stringResource(R.string.segment_test_disabled_3)) }
        )
    }
}

@Composable
private fun Section20(
    data: SegmentTestData,
    viewModel: SegmentTestViewModel,
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
            .then(if (edges.contains("all")) Modifier.systemBarsPadding() else Modifier)
            .then(if (!edges.contains("all") && edges.contains("top")) Modifier.statusBarsPadding() else Modifier)
            .then(if (!edges.contains("all") && edges.contains("bottom")) Modifier.navigationBarsPadding() else Modifier)
            .imePadding()
    ) {
        LazyColumn(
            modifier = Modifier.imePadding()
        ) {
            item {
            Column(
                modifier = Modifier
                    .testTag("container")
                    .semantics { testTagsAsResourceId = true }
                    .background(colorResource(R.color.white_23))
            ) {
                val resolved_text1 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = FontWeight.Bold,
                    size = 24.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.segment_test_segment_control_test),
                    fontFamily = resolved_text1.family,
                    fontWeight = resolved_text1.weight,
                    fontSize = resolved_text1.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text1.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 31.2.sp),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp)
                        .padding(bottom = 20.dp),
                    textAlign = TextAlign.Center
                )
                Section1(data, viewModel)
                Section2(data, viewModel)
                Section3(data, viewModel)
                Section4(data, viewModel)
                Section5(data, viewModel)
                Section6(data, viewModel)
                Section7(data, viewModel)
                Section8(data, viewModel)
                Section9(data, viewModel)
            }
            }
        }
    }
}
// >>> RESPONSIVE_HELPERS_END