package com.example.kotlinjsonui.sample.views.button_enabled_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.ButtonEnabledTestData
import com.example.kotlinjsonui.sample.viewmodels.ButtonEnabledTestViewModel
import androidx.compose.foundation.background
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
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
fun ButtonEnabledTestGeneratedView(
    data: ButtonEnabledTestData,
    viewModel: ButtonEnabledTestViewModel
) {
    // Generated Compose code from button_enabled_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "button_enabled_test",
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
                android.util.Log.e("DynamicView", "Error loading button_enabled_test: \$error")
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
            .padding(20.dp)
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
        )
        Text(
            text = "${data.isButtonEnabled}",
            fontSize = 16.sp,
            color = colorResource(R.color.medium_gray_4),
            modifier = Modifier
        )
        Button(
            onClick = { viewModel.testAction() },
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(10.dp),
            colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.medium_green_2)
                        ),
            enabled = data.isButtonEnabled
        ) {
            Text(
                text = stringResource(R.string.button_enabled_test_test_button_controlled_by_data),
                color = colorResource(R.color.white),
            )
        }
        Button(
            onClick = { viewModel.toggleEnabled() },
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(10.dp),
            colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.medium_blue_2)
                        )
        ) {
            Text(
                text = stringResource(R.string.button_enabled_test_toggle_enabled_state),
                color = colorResource(R.color.white),
            )
        }
        Button(
            onClick = { viewModel.neverCalled() },
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(10.dp),
            colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.medium_red_2)
                        ),
            enabled = false
        ) {
            Text(
                text = stringResource(R.string.button_enabled_test_always_disabled_button),
                color = colorResource(R.color.white),
            )
        }
        Button(
            onClick = { viewModel.alwaysCalled() },
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(10.dp),
            colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.medium_purple)
                        ),
            enabled = true
        ) {
            Text(
                text = stringResource(R.string.button_enabled_test_always_enabled_button),
                color = colorResource(R.color.white),
            )
        }
    }    }
    // >>> GENERATED_CODE_END
}