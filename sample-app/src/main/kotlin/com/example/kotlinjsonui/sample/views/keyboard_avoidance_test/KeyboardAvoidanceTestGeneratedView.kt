package com.example.kotlinjsonui.sample.views.keyboard_avoidance_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.KeyboardAvoidanceTestData
import com.example.kotlinjsonui.sample.viewmodels.KeyboardAvoidanceTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.shape.CircleShape
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

@Composable
fun KeyboardAvoidanceTestGeneratedView(
    data: KeyboardAvoidanceTestData,
    viewModel: KeyboardAvoidanceTestViewModel
) {
    // Generated Compose code from keyboard_avoidance_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "keyboard_avoidance_test",
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
                android.util.Log.e("DynamicView", "Error loading keyboard_avoidance_test: \$error")
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
                .padding(16.dp)
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
                modifier = Modifier.padding(bottom = 20.dp)
            )
            Text(
                text = stringResource(R.string.keyboard_avoidance_test_textfield_1),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            CustomTextFieldWithMargins(
                value = "${data.textField1}",
                onValueChange = { newValue -> viewModel.updateData(mapOf("textField1" to newValue)) },
                boxModifier = Modifier
                    .padding(bottom = 16.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                placeholder = { Text(stringResource(R.string.keyboard_avoidance_test_enter_text_here)) },
                shape = RoundedCornerShape(8.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray_4),
                isOutlined = true,
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black))
            )
            Text(
                text = stringResource(R.string.keyboard_avoidance_test_textfield_2),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            CustomTextFieldWithMargins(
                value = "${data.textField2}",
                onValueChange = { newValue -> viewModel.updateData(mapOf("textField2" to newValue)) },
                boxModifier = Modifier
                    .padding(bottom = 16.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                placeholder = { Text(stringResource(R.string.keyboard_avoidance_test_another_text_field)) },
                shape = RoundedCornerShape(8.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray_4),
                isOutlined = true,
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black))
            )
            Text(
                text = stringResource(R.string.keyboard_avoidance_test_textfield_3),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            CustomTextFieldWithMargins(
                value = "${data.textField3}",
                onValueChange = { newValue -> viewModel.updateData(mapOf("textField3" to newValue)) },
                boxModifier = Modifier
                    .padding(bottom = 16.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                placeholder = { Text(stringResource(R.string.keyboard_avoidance_test_keep_typing)) },
                shape = RoundedCornerShape(8.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray_4),
                isOutlined = true,
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black))
            )
            Text(
                text = stringResource(R.string.keyboard_avoidance_test_textfield_4),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            CustomTextFieldWithMargins(
                value = "${data.textField4}",
                onValueChange = { newValue -> viewModel.updateData(mapOf("textField4" to newValue)) },
                boxModifier = Modifier
                    .padding(bottom = 16.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                placeholder = { Text(stringResource(R.string.keyboard_avoidance_test_this_should_scroll_up)) },
                shape = RoundedCornerShape(8.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray_4),
                isOutlined = true,
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black))
            )
            Text(
                text = stringResource(R.string.keyboard_avoidance_test_textfield_5_at_bottom),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            CustomTextFieldWithMargins(
                value = "${data.textField5}",
                onValueChange = { newValue -> viewModel.updateData(mapOf("textField5" to newValue)) },
                boxModifier = Modifier
                    .padding(bottom = 16.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                placeholder = { Text(stringResource(R.string.keyboard_avoidance_test_this_is_near_the_bottom)) },
                shape = RoundedCornerShape(8.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray_4),
                isOutlined = true,
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black))
            )
            Text(
                text = stringResource(R.string.keyboard_avoidance_test_textview_multiline),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            CustomTextFieldWithMargins(
                value = data.textView,
                onValueChange = { newValue -> viewModel.updateData(mapOf("textView" to newValue)) },
                boxModifier = Modifier
                    .padding(bottom = 16.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Multi-line text input\nType here...") },
                shape = RoundedCornerShape(8.dp),
                backgroundColor = colorResource(R.color.white),
                isOutlined = true,
                maxLines = Int.MAX_VALUE,
                singleLine = false,
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.dark_gray))
            )
            Button(
                onClick = { viewModel.submitForm() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue)
                                )
            ) {
                Text(
                    text = stringResource(R.string.keyboard_avoidance_test_submit),
                    fontSize = 18.sp,
                    color = colorResource(R.color.white),
                )
            }
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}