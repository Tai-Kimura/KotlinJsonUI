package com.example.kotlinjsonui.sample.views.weight_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.WeightTestData
import com.example.kotlinjsonui.sample.viewmodels.WeightTestViewModel
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
fun WeightTestGeneratedView(
    data: WeightTestData,
    viewModel: WeightTestViewModel
) {
    // Generated Compose code from weight_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "weight_test",
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
                android.util.Log.e("DynamicView", "Error loading weight_test: \$error")
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
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(R.color.white))
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
            text = stringResource(R.string.weight_test_horizontal_weight_distribution_),
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
                text = stringResource(R.string.weight_test_weight_1),
                fontSize = 14.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
                    .background(colorResource(R.color.pale_pink)),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.weight_test_weight_2),
                fontSize = 14.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .background(colorResource(R.color.pale_yellow)),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.weight_test_weight_1),
                fontSize = 14.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(colorResource(R.color.pale_cyan)),
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = stringResource(R.string.weight_test_vertical_weight_distribution_13),
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
                .height(300.dp)
                .padding(top = 10.dp)
        ) {
            Text(
                text = stringResource(R.string.weight_test_weight_1),
                fontSize = 14.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .height(0.dp)
                    .background(colorResource(R.color.white_2)),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.weight_test_weight_3),
                fontSize = 14.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
                    .height(0.dp)
                    .background(colorResource(R.color.white_3)),
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
                    .background(colorResource(R.color.white_4)),
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = stringResource(R.string.weight_test_widthweight_test),
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
                text = stringResource(R.string.weight_test_widthweight_1),
                fontSize = 14.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .width(0.dp)
                    .fillMaxHeight()
                    .background(colorResource(R.color.white_5)),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.weight_test_widthweight_1),
                fontSize = 14.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .width(0.dp)
                    .fillMaxHeight()
                    .background(colorResource(R.color.white_6)),
                textAlign = TextAlign.Center
            )
        }
    }    }
    // >>> GENERATED_CODE_END
}