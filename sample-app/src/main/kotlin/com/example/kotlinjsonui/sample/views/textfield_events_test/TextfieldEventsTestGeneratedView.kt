package com.example.kotlinjsonui.sample.views.textfield_events_test

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
import com.example.kotlinjsonui.sample.data.TextfieldEventsTestData
import com.example.kotlinjsonui.sample.viewmodels.TextfieldEventsTestViewModel
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
fun TextfieldEventsTestGeneratedView(
    data: TextfieldEventsTestData,
    viewModel: TextfieldEventsTestViewModel
) {
    // Generated Compose code from textfield_events_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "textfield_events_test",
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
                android.util.Log.e("DynamicView", "Error loading textfield_events_test: \$error")
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
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        LazyColumn(
        ) {
            item {
            Column(
                modifier = Modifier.background(colorResource(R.color.white_23))
            ) {
                Text(
                    text = stringResource(R.string.test_menu_textfield_events_test),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp)
                        .padding(bottom = 20.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.textfield_events_test_ontextchange_event_test),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                )
                CustomTextFieldWithMargins(
                    value = "${data.email}",
                    onValueChange = { newValue -> viewModel.handleEmailChange(newValue) },
                    boxModifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                        .padding(end = 20.dp),
                    placeholder = { Text(stringResource(R.string.textfield_events_test_enter_email)) },
                    textStyle = TextStyle(color = colorResource(R.color.black)),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Default)
                )
                Text(
                    text = "${data.emailDisplay}",
                    fontSize = 14.sp,
                    color = colorResource(R.color.medium_gray_4),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                )
                Text(
                    text = stringResource(R.string.textfield_events_test_secure_textfield_test),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .padding(start = 20.dp)
                )
                CustomTextFieldWithMargins(
                    value = "${data.password}",
                    onValueChange = { newValue -> viewModel.handlePasswordChange(newValue) },
                    boxModifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                        .padding(end = 20.dp),
                    placeholder = { Text(stringResource(R.string.secure_field_test_enter_password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    isSecure = true,
                    textStyle = TextStyle(color = colorResource(R.color.black)),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
                )
                Text(
                    text = "${data.passwordLength}",
                    fontSize = 14.sp,
                    color = colorResource(R.color.medium_gray_4),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                )
                Text(
                    text = stringResource(R.string.textfield_events_test_input_accessory_test),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .padding(start = 20.dp)
                )
                CustomTextFieldWithMargins(
                    value = "${data.notes}",
                    onValueChange = { newValue -> viewModel.updateData(mapOf("notes" to newValue)) },
                    boxModifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                        .padding(end = 20.dp),
                    placeholder = { Text(stringResource(R.string.textfield_events_test_enter_notes)) },
                    textStyle = TextStyle(color = colorResource(R.color.black))
                )
            }
            }
        }
    }    }
    // >>> GENERATED_CODE_END
}