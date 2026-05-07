package com.example.kotlinjsonui.sample.views.custom_component_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.CustomComponentTestData
import com.example.kotlinjsonui.sample.viewmodels.CustomComponentTestViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.blur
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.kotlinjsonui.sample.R
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.border
import com.example.kotlinjsonui.sample.extensions.SampleCard
import com.example.kotlinjsonui.sample.extensions.StatusBadge
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.colorResource
import com.kotlinjsonui.dynamic.LocalSafeAreaConfig
import com.kotlinjsonui.dynamic.SafeAreaConfig
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.text.TextStyle
import com.kotlinjsonui.core.Configuration

@Composable
fun CustomComponentTestGeneratedView(
    data: CustomComponentTestData,
    viewModel: CustomComponentTestViewModel
) {
    // Generated Compose code from custom_component_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "custom_component_test",
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
        modifier = Modifier
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
                Text(
                    text = stringResource(R.string.custom_component_test_custom_component_test),
                    fontSize = 28.sp,
                    color = colorResource(R.color.black),
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(lineHeight = 28.sp),
                    modifier = Modifier.padding(top = 0.dp, end = 0.dp, bottom = 20.dp, start = 0.dp)
                )
                Text(
                    text = stringResource(R.string.custom_component_test_testing_samplecard_static_value),
                    fontSize = 18.sp,
                    color = colorResource(R.color.dark_gray),
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(lineHeight = 18.sp),
                    modifier = Modifier.padding(top = 0.dp, end = 0.dp, bottom = 8.dp, start = 0.dp)
                )
// TODO: Implement component type: SampleCard
                Text(
                    text = stringResource(R.string.custom_component_test_testing_samplecard_dynamic_valu),
                    fontSize = 18.sp,
                    color = colorResource(R.color.dark_gray),
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(lineHeight = 18.sp),
                    modifier = Modifier.padding(top = 20.dp, end = 0.dp, bottom = 8.dp, start = 0.dp)
                )
// TODO: Implement component type: SampleCard
                Text(
                    text = stringResource(R.string.custom_component_test_testing_multiple_cards),
                    fontSize = 18.sp,
                    color = colorResource(R.color.dark_gray),
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(lineHeight = 18.sp),
                    modifier = Modifier.padding(top = 20.dp, end = 0.dp, bottom = 8.dp, start = 0.dp)
                )
                Column(
                ) {
// TODO: Implement component type: SampleCard
// TODO: Implement component type: SampleCard
// TODO: Implement component type: SampleCard
                }
                Text(
                    text = stringResource(R.string.custom_component_test_testing_statusbadge_noncontaine),
                    fontSize = 18.sp,
                    color = colorResource(R.color.dark_gray),
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(lineHeight = 18.sp),
                    modifier = Modifier.padding(top = 20.dp, end = 0.dp, bottom = 8.dp, start = 0.dp)
                )
                Column(
                ) {
// TODO: Implement component type: StatusBadge
// TODO: Implement component type: StatusBadge
// TODO: Implement component type: StatusBadge
                }
                Button(
                    onClick = { data.toggleDynamicMode?.invoke() },
                    modifier = Modifier.wrapContentWidth(),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_blue_3),
                                            contentColor = colorResource(R.color.white)
                                        )
                ) {
                    Text(stringResource(R.string.custom_component_test_toggle_dynamic_mode))
                }
                Text(
                    text = "${data.dynamicModeStatus}",
                    fontSize = 14.sp,
                    color = colorResource(R.color.medium_gray_4),
                    style = TextStyle(lineHeight = 14.sp),
                    modifier = Modifier,
                    textAlign = TextAlign.Center
                )
            }
            }
        }
    }    }
    // >>> GENERATED_CODE_END
}