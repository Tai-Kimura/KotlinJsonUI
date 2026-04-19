package com.example.kotlinjsonui.sample.views.visibility_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.VisibilityTestData
import com.example.kotlinjsonui.sample.viewmodels.VisibilityTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.ui.draw.alpha
import com.kotlinjsonui.components.VisibilityWrapper
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
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
fun VisibilityTestGeneratedView(
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
) {
    // Generated Compose code from visibility_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "visibility_test",
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
                android.util.Log.e("DynamicView", "Error loading visibility_test: \$error")
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
            VisibilityWrapper(
                visibility = "visible",
            ) {
            Text(
                text = stringResource(R.string.visibility_test_visibility_visible_default),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 20.dp)
                    .background(colorResource(R.color.pale_yellow))
            )
            }
            VisibilityWrapper(
                visibility = "invisible",
            ) {
            Text(
                text = stringResource(R.string.visibility_test_this_label_is_invisible_takes_s),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 10.dp)
                    .background(colorResource(R.color.pale_pink))
            )
            }
            Text(
                text = stringResource(R.string.visibility_test_after_invisible_label),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 10.dp)
                    .background(colorResource(R.color.pale_cyan))
            )
            Text(
                text = stringResource(R.string.visibility_test_after_gone_label_no_gap),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 10.dp)
                    .background(colorResource(R.color.white_3))
            )
            Text(
                text = stringResource(R.string.visibility_test_opacity_tests),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 30.dp)
            )
            Text(
                text = stringResource(R.string.visibility_test_opacity_10_fully_visible),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .alpha(1.0f)
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 10.dp)
                    .background(colorResource(R.color.white_5))
            )
            Text(
                text = stringResource(R.string.visibility_test_opacity_07),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .alpha(0.7f)
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 10.dp)
                    .background(colorResource(R.color.white_6))
            )
            Text(
                text = stringResource(R.string.visibility_test_opacity_05),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .alpha(0.5f)
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 10.dp)
                    .background(colorResource(R.color.white_7))
            )
            Text(
                text = stringResource(R.string.visibility_test_opacity_03),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .alpha(0.3f)
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 10.dp)
                    .background(colorResource(R.color.white_8))
            )
            Text(
                text = stringResource(R.string.visibility_test_opacity_01),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .alpha(0.1f)
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 10.dp)
                    .background(colorResource(R.color.white_3))
            )
            Text(
                text = stringResource(R.string.visibility_test_alpha_test_same_as_opacity),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 30.dp)
            )
            Text(
                text = stringResource(R.string.visibility_test_alpha_06),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .alpha(0.6f)
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 10.dp)
                    .background(colorResource(R.color.white_4))
            )
            Text(
                text = stringResource(R.string.visibility_test_dynamic_visibility_tests),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 30.dp)
            )
            Button(
                onClick = { },
                modifier = Modifier.padding(top = 10.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue)
                                )
            ) {
                Text(
                    text = stringResource(R.string.visibility_test_toggle_visibility),
                    color = colorResource(R.color.white),
                )
            }
            VisibilityWrapper(
                visibility = data.textVisibility,
            ) {
            Text(
                text = stringResource(R.string.visibility_test_this_text_uses_dynamic_visibili),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 10.dp)
                    .background(colorResource(R.color.white_5))
                    .padding(10.dp)
            )
            }
            Button(
                onClick = { },
                modifier = Modifier.padding(top = 20.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_green)
                                )
            ) {
                Text(
                    text = stringResource(R.string.visibility_test_toggle_hidden),
                    color = colorResource(R.color.white),
                )
            }
            VisibilityWrapper(
                hidden = data.isHidden,
            ) {
            Text(
                text = stringResource(R.string.visibility_test_this_text_uses_dynamic_hidden_a),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 10.dp)
                    .background(colorResource(R.color.white_6))
                    .padding(10.dp)
            )
            }
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}