package com.example.kotlinjsonui.sample.views.alignment_combo_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.AlignmentComboTestData
import com.example.kotlinjsonui.sample.viewmodels.AlignmentComboTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.BiasAlignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.res.stringResource
import com.example.kotlinjsonui.sample.R
import androidx.compose.ui.res.colorResource
import androidx.compose.foundation.layout.imePadding
import com.kotlinjsonui.core.Configuration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription

@Composable
fun AlignmentComboTestGeneratedView(
    data: AlignmentComboTestData,
    viewModel: AlignmentComboTestViewModel
) {
    // Generated Compose code from alignment_combo_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "alignment_combo_test",
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
        modifier = Modifier
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
            Button(
                onClick = { data.toggleDynamicMode?.invoke() },
                modifier = Modifier
                    .wrapContentWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue_3),
                                    contentColor = colorResource(R.color.white)
                                )
            ) {
                Text(
                    text = "${data.dynamicModeStatus}",
                    fontSize = 14.sp
                )
            }
            Text(
                text = "${data.title}",
                fontSize = 24.sp,
                color = colorResource(R.color.black),
                style = TextStyle(lineHeight = 24.sp),
                modifier = Modifier
                    .testTag("title_label")
                    .semantics { contentDescription = "title_label" }
                    .align(Alignment.CenterHorizontally)
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(bottom = 20.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.alignment_combo_test_corner_combinations),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                style = TextStyle(lineHeight = 18.sp),
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(colorResource(R.color.pale_gray))
            ) {
                Text(
                    text = stringResource(R.string.alignment_combo_test_topleft),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    style = TextStyle(lineHeight = 14.sp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(colorResource(R.color.pale_pink))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(colorResource(R.color.pale_gray_2))
            ) {
                Text(
                    text = stringResource(R.string.alignment_combo_test_topright),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    style = TextStyle(lineHeight = 14.sp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(colorResource(R.color.pale_yellow))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(colorResource(R.color.pale_gray_3))
            ) {
                Text(
                    text = stringResource(R.string.alignment_combo_test_bottomleft),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    style = TextStyle(lineHeight = 14.sp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(colorResource(R.color.pale_cyan))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(colorResource(R.color.light_gray))
            ) {
                Text(
                    text = stringResource(R.string.alignment_combo_test_bottomright),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    style = TextStyle(lineHeight = 14.sp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(colorResource(R.color.white_2))
                        .padding(8.dp)
                )
            }
            Text(
                text = stringResource(R.string.alignment_combo_test_edge_center_combinations),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                style = TextStyle(lineHeight = 18.sp),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(colorResource(R.color.light_gray_2))
            ) {
                Text(
                    text = stringResource(R.string.alignment_combo_test_topcenter),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    style = TextStyle(lineHeight = 14.sp),
                    modifier = Modifier
                        .align(BiasAlignment(0f, -1f))
                        .background(colorResource(R.color.white_3))
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(colorResource(R.color.light_gray_3))
            ) {
                Text(
                    text = stringResource(R.string.alignment_combo_test_bottomcenter),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    style = TextStyle(lineHeight = 14.sp),
                    modifier = Modifier
                        .align(BiasAlignment(0f, 1f))
                        .background(colorResource(R.color.white_4))
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(colorResource(R.color.light_gray_4))
            ) {
                Text(
                    text = stringResource(R.string.alignment_combo_test_leftcenter),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    style = TextStyle(lineHeight = 14.sp),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .background(colorResource(R.color.pale_red))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(colorResource(R.color.light_gray_5))
            ) {
                Text(
                    text = stringResource(R.string.alignment_combo_test_rightcenter),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    style = TextStyle(lineHeight = 14.sp),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .background(colorResource(R.color.pale_green))
                        .padding(8.dp)
                )
            }
            Text(
                text = stringResource(R.string.alignment_combo_test_multiple_elements_test),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                style = TextStyle(lineHeight = 18.sp),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(bottom = 10.dp)
                    .background(colorResource(R.color.light_gray_6))
            ) {
                Text(
                    text = "TL",
                    fontSize = 12.sp,
                    color = colorResource(R.color.black),
                    style = TextStyle(lineHeight = 12.sp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(colorResource(R.color.white_5))
                        .padding(5.dp)
                )
                Text(
                    text = "TR",
                    fontSize = 12.sp,
                    color = colorResource(R.color.black),
                    style = TextStyle(lineHeight = 12.sp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(colorResource(R.color.white_6))
                        .padding(5.dp)
                )
                Text(
                    text = "BL",
                    fontSize = 12.sp,
                    color = colorResource(R.color.black),
                    style = TextStyle(lineHeight = 12.sp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(colorResource(R.color.white_7))
                        .padding(5.dp)
                )
                Text(
                    text = "BR",
                    fontSize = 12.sp,
                    color = colorResource(R.color.black),
                    style = TextStyle(lineHeight = 12.sp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(colorResource(R.color.white_8))
                        .padding(5.dp)
                )
                Text(
                    text = stringResource(R.string.alignment_combo_test_center),
                    fontSize = 12.sp,
                    color = colorResource(R.color.black),
                    style = TextStyle(lineHeight = 12.sp),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(colorResource(R.color.white_9))
                        .padding(5.dp)
                )
            }
            Text(
                text = stringResource(R.string.alignment_combo_test_hstack_mixed_alignment),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                style = TextStyle(lineHeight = 18.sp),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(bottom = 10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 10.dp)
                    .background(colorResource(R.color.light_gray_7))
            ) {
                Text(
                    text = stringResource(R.string.alignment_combo_test_lefttop),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .align(Alignment.Top)
                        .background(colorResource(R.color.pale_red_2))
                        .padding(8.dp)
                )
                Text(
                    text = stringResource(R.string.alignment_combo_test_center),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .background(colorResource(R.color.pale_green_2))
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.alignment_combo_test_rightbottom),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .background(colorResource(R.color.pale_blue))
                        .padding(8.dp)
                )
            }
            Text(
                text = stringResource(R.string.alignment_combo_test_vstack_mixed_alignment),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                style = TextStyle(lineHeight = 18.sp),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(bottom = 10.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(colorResource(R.color.medium_gray))
            ) {
                Text(
                    text = stringResource(R.string.alignment_combo_test_topleft),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .background(colorResource(R.color.pale_red_3))
                        .padding(8.dp)
                )
                Text(
                    text = stringResource(R.string.alignment_combo_test_center),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(colorResource(R.color.pale_green_3))
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.alignment_combo_test_bottomright),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .align(Alignment.End)
                        .background(colorResource(R.color.pale_blue_2))
                        .padding(8.dp)
                )
            }
            Text(
                text = stringResource(R.string.alignment_combo_test_edge_cases),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                style = TextStyle(lineHeight = 18.sp),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(colorResource(R.color.medium_gray_2))
            ) {
                Text(
                    text = stringResource(R.string.alignment_combo_test_only_horizontal_center),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    style = TextStyle(lineHeight = 14.sp),
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
                Text(
                    text = stringResource(R.string.alignment_combo_test_only_vertical_center),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    style = TextStyle(lineHeight = 14.sp),
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