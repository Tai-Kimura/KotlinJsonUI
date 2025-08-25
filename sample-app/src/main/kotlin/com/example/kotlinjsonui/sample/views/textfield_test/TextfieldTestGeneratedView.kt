package com.example.kotlinjsonui.sample.views.textfield_test

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
import com.example.kotlinjsonui.sample.data.TextfieldTestData
import com.example.kotlinjsonui.sample.viewmodels.TextfieldTestViewModel
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.core.SafeDynamicView

@Composable
fun TextfieldTestGeneratedView(
    data: TextfieldTestData,
    viewModel: TextfieldTestViewModel
) {
    // Generated Compose code from textfield_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "textfield_test",
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
                android.util.Log.e("DynamicView", "Error loading textfield_test: \$error")
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
            .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
            .padding(20.dp)
    ) {
        Text(
            text = "TextField Test",
            fontSize = 20.sp,
            color = Color(android.graphics.Color.parseColor("#000000")),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        CustomTextField(
            value = data.email,
            onValueChange = { newValue -> viewModel.updateData(mapOf("email" to newValue)) },
            placeholder = { Text("Enter email") },
            textStyle = TextStyle(fontSize = 16.sp, color = Color(android.graphics.Color.parseColor("#000000"))),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Default)
        )
        CustomTextField(
            value = data.password,
            onValueChange = { newValue -> viewModel.updateData(mapOf("password" to newValue)) },
            placeholder = { Text("Enter password") },
            visualTransformation = PasswordVisualTransformation(),
            isSecure = true,
            textStyle = TextStyle(fontSize = 16.sp, color = Color(android.graphics.Color.parseColor("#000000"))),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
        )
        CustomTextField(
            value = data.phone,
            onValueChange = { newValue -> viewModel.updateData(mapOf("phone" to newValue)) },
            placeholder = { Text("Phone number") },
            textStyle = TextStyle(fontSize = 16.sp, color = Color(android.graphics.Color.parseColor("#000000"))),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Default)
        )
        CustomTextField(
            value = data.number,
            onValueChange = { newValue -> viewModel.updateData(mapOf("number" to newValue)) },
            modifier = Modifier
                .padding(10.dp),
            placeholder = { Text("Enter number") },
            backgroundColor = Color(android.graphics.Color.parseColor("#F0F0F0")),
            textStyle = TextStyle(fontSize = 16.sp, color = Color(android.graphics.Color.parseColor("#000000"))),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Default)
        )
        CustomTextField(
            value = data.search,
            onValueChange = { newValue -> viewModel.updateData(mapOf("search" to newValue)) },
            placeholder = { Text("Search...") },
            textStyle = TextStyle(fontSize = 16.sp, color = Color(android.graphics.Color.parseColor("#000000"))),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
        )
        CustomTextField(
            value = data.url,
            onValueChange = { newValue -> viewModel.updateData(mapOf("url" to newValue)) },
            placeholder = { Text("Website URL") },
            textStyle = TextStyle(fontSize = 16.sp, color = Color(android.graphics.Color.parseColor("#000000"))),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Default)
        )
        Text(
            text = "Entered Values:",
            fontSize = 16.sp,
            color = Color(android.graphics.Color.parseColor("#000000")),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Text(
            text = "${data.email}",
            fontSize = 14.sp,
            color = Color(android.graphics.Color.parseColor("#666666")),
            modifier = Modifier
        )
        Text(
            text = "${data.password}",
            fontSize = 14.sp,
            color = Color(android.graphics.Color.parseColor("#666666")),
            modifier = Modifier
        )
        Text(
            text = "${data.phone}",
            fontSize = 14.sp,
            color = Color(android.graphics.Color.parseColor("#666666")),
            modifier = Modifier
        )
    }    }
    // >>> GENERATED_CODE_END
}