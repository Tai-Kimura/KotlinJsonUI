package com.example.kotlinjsonui.sample.views.disabled_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.DisabledTestData
import com.example.kotlinjsonui.sample.viewmodels.DisabledTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.TextStyle
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
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
import androidx.compose.foundation.layout.imePadding
import com.kotlinjsonui.core.Configuration
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription

@Composable
fun DisabledTestGeneratedView(
    data: DisabledTestData,
    viewModel: DisabledTestViewModel
) {
    // Generated Compose code from disabled_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "disabled_test",
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
                android.util.Log.e("DynamicView", "Error loading disabled_test: \$error")
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
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 20.dp)
            )
            Text(
                text = stringResource(R.string.disabled_test_enabled_button),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_gray),
                style = TextStyle(lineHeight = 16.sp),
                modifier = Modifier.padding(top = 20.dp)
            )
            Button(
                onClick = { data.onEnabledButtonTap?.invoke() },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue),
                                    contentColor = colorResource(R.color.white)
                                ),
                enabled = true
            ) {
                Text(stringResource(R.string.disabled_test_enabled_button))
            }
            Text(
                text = stringResource(R.string.disabled_test_disabled_button),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_gray),
                style = TextStyle(lineHeight = 16.sp),
                modifier = Modifier.padding(top = 20.dp)
            )
            Button(
                onClick = { data.onDisabledButtonTap?.invoke() },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue),
                                    disabledContainerColor = colorResource(R.color.pale_gray_4),
                                    contentColor = colorResource(R.color.white),
                                    disabledContentColor = colorResource(R.color.light_gray_8)
                                ),
                enabled = false
            ) {
                Text(stringResource(R.string.disabled_test_disabled_button))
            }
            Text(
                text = stringResource(R.string.disabled_test_touchdisabledstate_button),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_gray),
                style = TextStyle(lineHeight = 16.sp),
                modifier = Modifier.padding(top = 20.dp)
            )
            Button(
                onClick = { data.onTouchDisabledTap?.invoke() },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_red_3),
                                    contentColor = colorResource(R.color.white)
                                )
            ) {
                Text(stringResource(R.string.disabled_test_touch_disabled))
            }
            Text(
                text = stringResource(R.string.disabled_test_enabled_textfield),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_gray),
                style = TextStyle(lineHeight = 16.sp),
                modifier = Modifier.padding(top = 20.dp)
            )
            CustomTextFieldWithMargins(
                value = data.textFieldValue,
                onValueChange = { newValue -> viewModel.updateData(mapOf("textFieldValue" to newValue)) },
                boxModifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                placeholder = { Text(
                                    text = stringResource(R.string.disabled_test_enabled_can_type_here),
                                    color = Configuration.TextField.defaultPlaceholderColor
                                ) },
                shape = RoundedCornerShape(8.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray_4),
                isOutlined = true,
                textStyle = TextStyle(color = colorResource(R.color.black))
            )
            Text(
                text = stringResource(R.string.disabled_test_disabled_textfield),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_gray),
                style = TextStyle(lineHeight = 16.sp),
                modifier = Modifier.padding(top = 20.dp)
            )
            CustomTextFieldWithMargins(
                value = stringResource(R.string.disabled_test_disabled_text_field),
                onValueChange = { },
                boxModifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                placeholder = { Text(
                                    text = stringResource(R.string.disabled_test_disabled_cannot_type),
                                    color = Configuration.TextField.defaultPlaceholderColor
                                ) },
                shape = RoundedCornerShape(8.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray_4),
                isOutlined = true,
                textStyle = TextStyle(color = colorResource(R.color.medium_gray_4))
            )
            Text(
                text = stringResource(R.string.disabled_test_dynamic_enabledisable_test),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                style = TextStyle(lineHeight = 16.sp),
                modifier = Modifier.padding(top = 30.dp)
            )
            Button(
                onClick = { data.toggleEnableState?.invoke() },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_green),
                                    contentColor = colorResource(R.color.white)
                                )
            ) {
                Text(stringResource(R.string.disabled_test_toggle_enable_state))
            }
            Button(
                onClick = { data.onDynamicButtonTap?.invoke() },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue_3),
                                    disabledContainerColor = colorResource(R.color.pale_gray_3),
                                    contentColor = colorResource(R.color.white),
                                    disabledContentColor = colorResource(R.color.medium_gray_2)
                                ),
                enabled = data.isEnabled
            ) {
                Text(stringResource(R.string.disabled_test_dynamic_button))
            }
            Text(
                text = "${data.isEnabled}",
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                style = TextStyle(lineHeight = 14.sp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp),
                textAlign = TextAlign.Center
            )
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}