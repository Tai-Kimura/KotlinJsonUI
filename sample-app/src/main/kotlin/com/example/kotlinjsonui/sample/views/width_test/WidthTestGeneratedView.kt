package com.example.kotlinjsonui.sample.views.width_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.WidthTestData
import com.example.kotlinjsonui.sample.viewmodels.WidthTestViewModel
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
import com.kotlinjsonui.core.SafeDynamicView
import androidx.compose.ui.res.stringResource
import com.example.kotlinjsonui.sample.R
import androidx.compose.ui.res.colorResource

@Composable
fun WidthTestGeneratedView(
    data: WidthTestData,
    viewModel: WidthTestViewModel
) {
    // Generated Compose code from width_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "width_test",
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
                android.util.Log.e("DynamicView", "Error loading width_test: \$error")
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
        Box(
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
            text = stringResource(R.string.test_menu_width_test),
            fontSize = 24.sp,
            color = colorResource(R.color.black),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 20.dp)
        )
        Text(
            text = stringResource(R.string.width_test_matchparent_width),
            color = colorResource(R.color.white),
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .height(50.dp)
                .padding(top = 20.dp)
                .background(colorResource(R.color.light_red)),
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.width_test_fixed_width_200),
            color = colorResource(R.color.white),
            modifier = Modifier
                .align(Alignment.TopStart)
                .width(200.dp)
                .height(50.dp)
                .padding(top = 10.dp)
                .background(colorResource(R.color.light_lime)),
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.width_test_wrapcontent_width),
            color = colorResource(R.color.white),
            modifier = Modifier
                .align(Alignment.TopStart)
                .wrapContentWidth()
                .height(50.dp)
                .padding(top = 10.dp)
                .background(colorResource(R.color.light_cyan)),
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(top = 20.dp)
                .background(colorResource(R.color.pale_gray))
        ) {
            Text(
                text = stringResource(R.string.width_test_weight_1),
                color = colorResource(R.color.white),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(colorResource(R.color.light_yellow)),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.width_test_weight_2_wrap),
                color = colorResource(R.color.black),
                modifier = Modifier
                    .weight(2f)
                    .wrapContentHeight()
                    .background(colorResource(R.color.pale_pink_2)),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.width_test_weight_1),
                color = colorResource(R.color.black),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(colorResource(R.color.pale_gray_6)),
                textAlign = TextAlign.Center
            )
        }
    }    }
    // >>> GENERATED_CODE_END
}