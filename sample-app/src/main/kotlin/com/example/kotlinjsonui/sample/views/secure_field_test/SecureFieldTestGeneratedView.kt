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
            .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
    ) {
        Button(
            onClick = { viewModel.toggleDynamicMode() },
            modifier = Modifier
                .wrapContentWidth()
                .height(32.dp),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
            colors = ButtonDefaults.buttonColors(
                            containerColor = Color(android.graphics.Color.parseColor("#5856D6"))
                        )
        ) {
            Text(
                text = "Dynamic: ${data.dynamicModeStatus}",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#FFFFFF")),
            )
        }
        Text(
            text = "${data.title}",
            fontSize = 24.sp,
            color = Color(android.graphics.Color.parseColor("#000000")),
            modifier = Modifier
                .padding(top = 20.dp)
                .wrapContentWidth()
                .wrapContentHeight()
        )
        Text(
            text = "Regular TextField (not secure)",
            fontSize = 14.sp,
            color = Color(android.graphics.Color.parseColor("#666666")),
            modifier = Modifier.padding(top = 30.dp)
        )
        CustomTextFieldWithMargins(
            value = data.regularText,
            onValueChange = { newValue -> viewModel.updateData(mapOf("regularText" to newValue)) },
            boxModifier = Modifier
                .padding(top = 10.dp)
                .padding(start = 20.dp)
                .padding(end = 20.dp),
            textFieldModifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            placeholder = { Text("Enter regular text") },
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color(android.graphics.Color.parseColor("#FFFFFF")),
            borderColor = Color(android.graphics.Color.parseColor("#CCCCCC")),
            isOutlined = true,
            textStyle = TextStyle(fontSize = 16.sp, color = Color(android.graphics.Color.parseColor("#000000")))
        )
        Text(
            text = "Secure TextField (password)",
            fontSize = 14.sp,
            color = Color(android.graphics.Color.parseColor("#666666")),
            modifier = Modifier.padding(top = 20.dp)
        )
        CustomTextFieldWithMargins(
            value = data.password,
            onValueChange = { newValue -> viewModel.updateData(mapOf("password" to newValue)) },
            boxModifier = Modifier
                .padding(top = 10.dp)
                .padding(start = 20.dp)
                .padding(end = 20.dp),
            textFieldModifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            placeholder = { Text("Enter password") },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color(android.graphics.Color.parseColor("#FFFFFF")),
            borderColor = Color(android.graphics.Color.parseColor("#CCCCCC")),
            isOutlined = true,
            isSecure = true,
            textStyle = TextStyle(fontSize = 16.sp, color = Color(android.graphics.Color.parseColor("#000000")))
        )
        Text(
            text = "Confirm Password (also secure)",
            fontSize = 14.sp,
            color = Color(android.graphics.Color.parseColor("#666666")),
            modifier = Modifier.padding(top = 20.dp)
        )
        CustomTextFieldWithMargins(
            value = data.confirmPassword,
            onValueChange = { newValue -> viewModel.updateData(mapOf("confirmPassword" to newValue)) },
            boxModifier = Modifier
                .padding(top = 10.dp)
                .padding(start = 20.dp)
                .padding(end = 20.dp),
            textFieldModifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            placeholder = { Text("Confirm password") },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color(android.graphics.Color.parseColor("#FFFFFF")),
            borderColor = Color(android.graphics.Color.parseColor("#CCCCCC")),
            isOutlined = true,
            isSecure = true,
            textStyle = TextStyle(fontSize = 16.sp, color = Color(android.graphics.Color.parseColor("#000000")))
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(15.dp)
                .padding(top = 30.dp)
                .padding(start = 20.dp)
                .padding(end = 20.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
        ) {
            Text(
                text = "Values entered:",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )
            Text(
                text = "${data.regularText}",
                fontSize = 12.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = "Password: [hidden]",
                fontSize = 12.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = "Confirm: [hidden]",
                fontSize = 12.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(top = 5.dp)
            )
        }
    }    }
    // >>> GENERATED_CODE_END
}