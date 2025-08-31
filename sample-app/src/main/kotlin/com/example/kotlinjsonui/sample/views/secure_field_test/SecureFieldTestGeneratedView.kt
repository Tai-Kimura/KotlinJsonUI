package com.example.kotlinjsonui.sample.views.secure_field_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.SecureFieldTestData
import com.example.kotlinjsonui.sample.viewmodels.SecureFieldTestViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.TextStyle
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
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
fun SecureFieldTestGeneratedView(
    data: SecureFieldTestData,
    viewModel: SecureFieldTestViewModel
) {
    // Generated Compose code from secure_field_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "secure_field_test",
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
                android.util.Log.e("DynamicView", "Error loading secure_field_test: \$error")
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
            text = stringResource(R.string.secure_field_test_regular_textfield_not_secure),
            fontSize = 14.sp,
            color = colorResource(R.color.medium_gray_4),
            modifier = Modifier.padding(top = 30.dp)
        )
        CustomTextFieldWithMargins(
            value = "${data.regularText}",
            onValueChange = { newValue -> viewModel.updateData(mapOf("regularText" to newValue)) },
            boxModifier = Modifier
                .padding(top = 10.dp)
                .padding(start = 20.dp)
                .padding(end = 20.dp),
            textFieldModifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            placeholder = { Text(stringResource(R.string.secure_field_test_enter_regular_text)) },
            shape = RoundedCornerShape(8.dp),
            backgroundColor = colorResource(R.color.white),
            borderColor = colorResource(R.color.pale_gray_4),
            isOutlined = true,
            textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black))
        )
        Text(
            text = stringResource(R.string.secure_field_test_secure_textfield_password),
            fontSize = 14.sp,
            color = colorResource(R.color.medium_gray_4),
            modifier = Modifier.padding(top = 20.dp)
        )
        CustomTextFieldWithMargins(
            value = "${data.password}",
            onValueChange = { newValue -> viewModel.updateData(mapOf("password" to newValue)) },
            boxModifier = Modifier
                .padding(top = 10.dp)
                .padding(start = 20.dp)
                .padding(end = 20.dp),
            textFieldModifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            placeholder = { Text(stringResource(R.string.secure_field_test_enter_password)) },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = colorResource(R.color.white),
            borderColor = colorResource(R.color.pale_gray_4),
            isOutlined = true,
            isSecure = true,
            textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black))
        )
        Text(
            text = stringResource(R.string.secure_field_test_confirm_password_also_secure),
            fontSize = 14.sp,
            color = colorResource(R.color.medium_gray_4),
            modifier = Modifier.padding(top = 20.dp)
        )
        CustomTextFieldWithMargins(
            value = "${data.confirmPassword}",
            onValueChange = { newValue -> viewModel.updateData(mapOf("confirmPassword" to newValue)) },
            boxModifier = Modifier
                .padding(top = 10.dp)
                .padding(start = 20.dp)
                .padding(end = 20.dp),
            textFieldModifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            placeholder = { Text(stringResource(R.string.secure_field_test_confirm_password)) },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = colorResource(R.color.white),
            borderColor = colorResource(R.color.pale_gray_4),
            isOutlined = true,
            isSecure = true,
            textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black))
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 30.dp)
                .padding(start = 20.dp)
                .padding(end = 20.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colorResource(R.color.pale_gray))
                .padding(15.dp)
        ) {
            Text(
                text = stringResource(R.string.secure_field_test_values_entered),
                fontSize = 14.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )
            Text(
                text = "${data.regularText}",
                fontSize = 12.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = stringResource(R.string.secure_field_test_password_hidden),
                fontSize = 12.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = stringResource(R.string.secure_field_test_confirm_hidden),
                fontSize = 12.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(top = 5.dp)
            )
        }
    }    }
    // >>> GENERATED_CODE_END
}