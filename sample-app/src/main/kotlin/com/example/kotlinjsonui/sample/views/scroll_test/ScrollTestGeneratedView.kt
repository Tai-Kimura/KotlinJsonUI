package com.example.kotlinjsonui.sample.views.scroll_test

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
import com.example.kotlinjsonui.sample.data.ScrollTestData
import com.example.kotlinjsonui.sample.viewmodels.ScrollTestViewModel
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
fun ScrollTestGeneratedView(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    // Generated Compose code from scroll_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "scroll_test",
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
                android.util.Log.e("DynamicView", "Error loading scroll_test: \$error")
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
        Column(
        modifier = Modifier.background(colorResource(R.color.white))
    ) {
        Text(
            text = stringResource(R.string.scroll_test_scrollview_test),
            fontSize = 20.sp,
            color = colorResource(R.color.black),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(10.dp)
        )
        LazyColumn(
            modifier = Modifier
                .height(200.dp)
                .background(colorResource(R.color.white_17))
        ) {
            item {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.scroll_test_no_vertical_indicator),
                    fontSize = 16.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                )
                Text(
                    text = stringResource(R.string.scroll_test_item_1),
                    fontSize = 16.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .background(colorResource(R.color.white_5))
                        .padding(10.dp)
                )
                Text(
                    text = stringResource(R.string.scroll_test_item_2),
                    fontSize = 16.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .background(colorResource(R.color.white_6))
                        .padding(10.dp)
                )
                Text(
                    text = stringResource(R.string.scroll_test_item_3),
                    fontSize = 16.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .background(colorResource(R.color.white_7))
                        .padding(10.dp)
                )
                Text(
                    text = stringResource(R.string.scroll_test_item_4),
                    fontSize = 16.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .background(colorResource(R.color.white_8))
                        .padding(10.dp)
                )
                Text(
                    text = stringResource(R.string.scroll_test_item_5),
                    fontSize = 16.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .background(colorResource(R.color.white_9))
                        .padding(10.dp)
                )
            }
            }
        }
        LazyColumn(
            modifier = Modifier
                .height(150.dp)
                .background(colorResource(R.color.white_17))
        ) {
            item {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.scroll_test_scroll_disabled),
                    fontSize = 16.sp,
                    color = colorResource(R.color.dark_red),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                )
                Text(
                    text = stringResource(R.string.scroll_test_this_scrollview_cannot_be_scrol),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                )
                Text(
                    text = stringResource(R.string.scroll_test_item_1),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                )
                Text(
                    text = stringResource(R.string.scroll_test_item_2),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                )
                Text(
                    text = stringResource(R.string.scroll_test_item_3_hidden_below),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                )
            }
            }
        }
        LazyRow(
            modifier = Modifier
                .height(100.dp)
                .background(colorResource(R.color.white_17))
        ) {
            item {
            Row(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.scroll_test_horizontal_scroll_item_1),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .width(150.dp)
                        .background(colorResource(R.color.white_5))
                        .padding(10.dp)
                )
                Text(
                    text = stringResource(R.string.scroll_test_horizontal_scroll_item_2),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .width(150.dp)
                        .background(colorResource(R.color.white_6))
                        .padding(10.dp)
                )
                Text(
                    text = stringResource(R.string.scroll_test_horizontal_scroll_item_3),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .width(150.dp)
                        .background(colorResource(R.color.white_7))
                        .padding(10.dp)
                )
                Text(
                    text = stringResource(R.string.scroll_test_horizontal_scroll_item_4),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .width(150.dp)
                        .background(colorResource(R.color.white_8))
                        .padding(10.dp)
                )
                Text(
                    text = stringResource(R.string.scroll_test_horizontal_scroll_item_5),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .width(150.dp)
                        .background(colorResource(R.color.white_9))
                        .padding(10.dp)
                )
                Text(
                    text = stringResource(R.string.scroll_test_horizontal_scroll_item_6),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .width(150.dp)
                        .background(colorResource(R.color.white_22))
                        .padding(10.dp)
                )
                Text(
                    text = stringResource(R.string.scroll_test_horizontal_scroll_item_7),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .width(150.dp)
                        .background(colorResource(R.color.white_25))
                        .padding(10.dp)
                )
                Text(
                    text = stringResource(R.string.scroll_test_horizontal_scroll_item_8_end),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .width(150.dp)
                        .background(colorResource(R.color.white_26))
                        .padding(10.dp)
                )
            }
            }
        }
    }    }
    // >>> GENERATED_CODE_END
}