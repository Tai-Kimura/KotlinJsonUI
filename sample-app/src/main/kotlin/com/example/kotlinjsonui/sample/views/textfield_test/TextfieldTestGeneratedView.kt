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
import androidx.compose.ui.res.stringResource
import com.example.kotlinjsonui.sample.R
import androidx.compose.ui.res.colorResource

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
            .background(colorResource(R.color.white))
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(R.string.test_menu_textfield_test),
            fontSize = 20.sp,
            color = colorResource(R.color.black),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        CustomTextField(
            value = "${data.email}",
            onValueChange = { newValue -> viewModel.updateData(mapOf("email" to newValue)) },
            placeholder = { Text(stringResource(R.string.textfield_events_test_enter_email)) },
            textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black)),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Default)
        )
        CustomTextField(
            value = "${data.password}",
            onValueChange = { newValue -> viewModel.updateData(mapOf("password" to newValue)) },
            placeholder = { Text(stringResource(R.string.secure_field_test_enter_password)) },
            visualTransformation = PasswordVisualTransformation(),
            isSecure = true,
            textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black)),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
        )
        CustomTextField(
            value = "${data.phone}",
            onValueChange = { newValue -> viewModel.updateData(mapOf("phone" to newValue)) },
            placeholder = { Text(stringResource(R.string.textfield_test_phone_number)) },
            textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black)),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Default)
        )
        CustomTextField(
            value = "${data.number}",
            onValueChange = { newValue -> viewModel.updateData(mapOf("number" to newValue)) },
            modifier = Modifier
                .padding(10.dp),
            placeholder = { Text(stringResource(R.string.textfield_test_enter_number)) },
            backgroundColor = colorResource(R.color.white_17),
            textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black)),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Default)
        )
        CustomTextField(
            value = "${data.search}",
            onValueChange = { newValue -> viewModel.updateData(mapOf("search" to newValue)) },
            placeholder = { Text(stringResource(R.string.textfield_test_search)) },
            textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black)),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
        )
        CustomTextField(
            value = "${data.url}",
            onValueChange = { newValue -> viewModel.updateData(mapOf("url" to newValue)) },
            placeholder = { Text(stringResource(R.string.textfield_test_website_url)) },
            textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black)),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Default)
        )
        Text(
            text = stringResource(R.string.textfield_test_entered_values),
            fontSize = 16.sp,
            color = colorResource(R.color.black),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Text(
            text = "${data.email}",
            fontSize = 14.sp,
            color = colorResource(R.color.medium_gray_4),
            modifier = Modifier
        )
        Text(
            text = "${data.password}",
            fontSize = 14.sp,
            color = colorResource(R.color.medium_gray_4),
            modifier = Modifier
        )
        Text(
            text = "${data.phone}",
            fontSize = 14.sp,
            color = colorResource(R.color.medium_gray_4),
            modifier = Modifier
        )
    }    }
    // >>> GENERATED_CODE_END
}