package com.example.kotlinjsonui.sample.views.margins_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.MarginsTestData
import com.example.kotlinjsonui.sample.viewmodels.MarginsTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.ui.res.stringResource
import com.example.kotlinjsonui.sample.R
import androidx.compose.ui.res.colorResource

@Composable
fun MarginsTestGeneratedView(
    data: MarginsTestData,
    viewModel: MarginsTestViewModel
) {
    // Generated Compose code from margins_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "margins_test",
            data = data.toMap(viewModel),
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
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(R.color.white))
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Button(
                onClick = { viewModel.toggleDynamicMode() },
                modifier = Modifier
                    .wrapContentWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue_3)
                                )
            ) {
                Text(
                    text = "${data.dynamicModeStatus}",
                    fontSize = 14.sp,
                    color = colorResource(R.color.white),
                )
            }
            Text(
                text = "${data.title}",
                fontSize = 24.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 20.dp)
            )
            Text(
                text = stringResource(R.string.margins_test_all_margins_20_20_20_20),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 20.dp, end = 20.dp, bottom = 20.dp, start = 20.dp)
                    .background(colorResource(R.color.white_5))
            )
            Text(
                text = stringResource(R.string.margins_test_left_margin_40),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 10.dp)
                    .padding(start = 40.dp)
                    .background(colorResource(R.color.white_6))
            )
            Text(
                text = stringResource(R.string.margins_test_right_margin_40),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 10.dp)
                    .padding(end = 40.dp)
                    .background(colorResource(R.color.white_7))
            )
            Text(
                text = stringResource(R.string.margins_test_top_margin_30),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 30.dp)
                    .background(colorResource(R.color.white_8))
            )
            Text(
                text = stringResource(R.string.margins_test_bottom_margin_30),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(bottom = 30.dp)
                    .background(colorResource(R.color.white_9))
            )
            Text(
                text = stringResource(R.string.margins_test_label_with_padding_20),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 10.dp)
                    .background(colorResource(R.color.white_22))
                    .padding(20.dp)
            )
            Text(
                text = stringResource(R.string.margins_test_label_with_leftpadding_30_right),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 10.dp)
                    .background(colorResource(R.color.pale_red))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 20.dp)
                    .background(colorResource(R.color.pale_gray_5))
            ) {
                Text(
                    text = stringResource(R.string.margins_test_parent_has_leftpadding_20_right),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 20.dp)
                    .background(colorResource(R.color.pale_gray_4))
            ) {
                Text(
                    text = stringResource(R.string.margins_test_maxwidth_200),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .widthIn(max = 200.dp)
                        .background(colorResource(R.color.pale_red))
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 20.dp)
                    .background(colorResource(R.color.light_gray_10))
            ) {
                Text(
                    text = stringResource(R.string.margins_test_minwidth_150),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .widthIn(min = 150.dp)
                        .background(colorResource(R.color.pale_green))
                )
            }
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}