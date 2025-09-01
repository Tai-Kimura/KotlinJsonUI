package com.example.kotlinjsonui.sample.views.weight_test_with_fixed

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.WeightTestWithFixedData
import com.example.kotlinjsonui.sample.viewmodels.WeightTestWithFixedViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextAlign
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
fun WeightTestWithFixedGeneratedView(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    // Generated Compose code from weight_test_with_fixed.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "weight_test_with_fixed",
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
                android.util.Log.e("DynamicView", "Error loading weight_test_with_fixed: \$error")
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
                text = stringResource(R.string.weight_test_with_fixed_fixed80_weight1_weight2_fixed60),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_gray),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 20.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_fixed_80),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .width(80.dp)
                        .fillMaxHeight()
                        .background(colorResource(R.color.pale_pink)),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.weight_test_weight_1),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(colorResource(R.color.pale_yellow)),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.weight_test_weight_2),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                        .background(colorResource(R.color.pale_cyan)),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_fixed_60),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .width(60.dp)
                        .fillMaxHeight()
                        .background(colorResource(R.color.white_2)),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = stringResource(R.string.weight_test_with_fixed_complex_nested_weights_with_fix),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_gray),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 30.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Text(
                        text = "1-1",
                        fontSize = 12.sp,
                        color = colorResource(R.color.black),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .height(0.dp)
                            .background(colorResource(R.color.white_5)),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "1-2",
                        fontSize = 12.sp,
                        color = colorResource(R.color.black),
                        modifier = Modifier
                            .weight(2f)
                            .fillMaxWidth()
                            .height(0.dp)
                            .background(colorResource(R.color.pale_red_3)),
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_fix_50),
                    fontSize = 12.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .width(50.dp)
                        .fillMaxHeight()
                        .background(colorResource(R.color.pale_gray)),
                    textAlign = TextAlign.Center
                )
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                ) {
                    Text(
                        text = "Fixed 30",
                        fontSize = 12.sp,
                        color = colorResource(R.color.black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .background(colorResource(R.color.white_6)),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.width_test_weight_1),
                        fontSize = 12.sp,
                        color = colorResource(R.color.black),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .height(0.dp)
                            .background(colorResource(R.color.pale_green_3)),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Text(
                text = stringResource(R.string.weight_test_with_fixed_multiple_fixed_sizes_with_weigh),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_gray),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 30.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = "40",
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .width(40.dp)
                        .fillMaxHeight()
                        .background(colorResource(R.color.pale_pink)),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_w1),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(colorResource(R.color.pale_yellow)),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "60",
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .width(60.dp)
                        .fillMaxHeight()
                        .background(colorResource(R.color.pale_pink)),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_w1),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(colorResource(R.color.pale_yellow)),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "80",
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .width(80.dp)
                        .fillMaxHeight()
                        .background(colorResource(R.color.pale_pink)),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = stringResource(R.string.weight_test_with_fixed_zero_weights_test),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_gray),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 30.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_w0),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(colorResource(R.color.light_red_6)),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_w1),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(colorResource(R.color.light_green_2)),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_fixed100),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .width(100.dp)
                        .fillMaxHeight()
                        .background(colorResource(R.color.light_blue_3)),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_w3),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxHeight()
                        .background(colorResource(R.color.pale)),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = stringResource(R.string.weight_test_with_fixed_vertical_fixed_weight_combinati),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_gray),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 30.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_fixed_30),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .background(colorResource(R.color.white_5)),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.weight_test_weight_1),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .height(0.dp)
                        .background(colorResource(R.color.white_6)),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_fixed_40),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(colorResource(R.color.white_7)),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.weight_test_weight_2),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxWidth()
                        .height(0.dp)
                        .background(colorResource(R.color.white_8)),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_fixed_50),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(colorResource(R.color.white_9)),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = stringResource(R.string.weight_test_with_fixed_vertical_nested_horizontal_weig),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_gray),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 30.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_header_fixed_25),
                    fontSize = 12.sp,
                    color = colorResource(R.color.white),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(25.dp)
                        .background(colorResource(R.color.medium_gray_4)),
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .height(0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.weight_test_with_fixed_col_1),
                        fontSize = 12.sp,
                        color = colorResource(R.color.black),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(colorResource(R.color.pale_pink)),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.weight_test_with_fixed_fix60),
                        fontSize = 12.sp,
                        color = colorResource(R.color.black),
                        modifier = Modifier
                            .width(60.dp)
                            .fillMaxHeight()
                            .background(colorResource(R.color.pale_gray_3)),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.weight_test_with_fixed_col_2),
                        fontSize = 12.sp,
                        color = colorResource(R.color.black),
                        modifier = Modifier
                            .weight(2f)
                            .fillMaxHeight()
                            .background(colorResource(R.color.pale_yellow)),
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_footer_fixed_25),
                    fontSize = 12.sp,
                    color = colorResource(R.color.white),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(25.dp)
                        .background(colorResource(R.color.medium_gray_4)),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = stringResource(R.string.weight_test_with_fixed_vertical_multiple_weights_only),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_gray),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 30.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_w1),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .height(0.dp)
                        .background(colorResource(R.color.pale_red)),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_w1),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .height(0.dp)
                        .background(colorResource(R.color.pale_green)),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_w2),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxWidth()
                        .height(0.dp)
                        .background(colorResource(R.color.pale_blue_3)),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.weight_test_with_fixed_w1),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .height(0.dp)
                        .background(colorResource(R.color.white_18)),
                    textAlign = TextAlign.Center
                )
            }
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}