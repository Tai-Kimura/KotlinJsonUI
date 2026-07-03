package com.example.kotlinjsonui.sample.views.textfield_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SecureTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.TextfieldTestData
import com.example.kotlinjsonui.sample.viewmodels.TextfieldTestViewModel
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun TextfieldTestGeneratedView(
    data: TextfieldTestData,
    viewModel: TextfieldTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from textfield_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "textfield_test",
            modifier = modifier,
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
        modifier = modifier
            .background(colorResource(R.color.white))
            .padding(20.dp)
    ) {
        Section0(data, viewModel)
        Section1(data, viewModel)
        Section2(data, viewModel)
        Section3(data, viewModel)
        Section4(data, viewModel)
        Section5(data, viewModel)
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: TextfieldTestData,
    viewModel: TextfieldTestViewModel
) {
    val resolved_text404 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 20.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.test_menu_textfield_test),
        color = colorResource(R.color.black),
        fontFamily = resolved_text404.family,
        fontWeight = resolved_text404.weight,
        fontSize = resolved_text404.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text404.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 26.0.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section1(
    data: TextfieldTestData,
    viewModel: TextfieldTestViewModel
) {
    val textFieldState_email_field = rememberTextFieldState(initialText = data.email)
    LaunchedEffect(data.email) { if (textFieldState_email_field.text.toString() != data.email) textFieldState_email_field.edit { replace(0, length, data.email) } }
    LaunchedEffect(textFieldState_email_field.text) { val newValue = textFieldState_email_field.text.toString(); if (newValue != data.email) viewModel.updateData(mapOf("email" to newValue)) }
    val resolved_textfield15 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextField(
        state = textFieldState_email_field,
        modifier = Modifier
            .testTag("email_field")
            .semantics { testTagsAsResourceId = true },
        placeholder = { Text(
                        text = stringResource(R.string.textfield_events_test_enter_email),
                        color = Configuration.TextField.defaultPlaceholderColor
                    ) },
        isOutlined = true,
        textStyle = TextStyle(fontFamily = resolved_textfield15.family, fontWeight = resolved_textfield15.weight, fontSize = (resolved_textfield15.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield15.style ?: FontStyle.Normal), color = Color(android.graphics.Color.parseColor("#000000"))),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Default)
    )
    val textFieldState_password_field = rememberTextFieldState(initialText = data.password)
    LaunchedEffect(data.password) { if (textFieldState_password_field.text.toString() != data.password) textFieldState_password_field.edit { replace(0, length, data.password) } }
    LaunchedEffect(textFieldState_password_field.text) { val newValue = textFieldState_password_field.text.toString(); if (newValue != data.password) viewModel.updateData(mapOf("password" to newValue)) }
    val resolved_textfield16 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextField(
        state = textFieldState_password_field,
        modifier = Modifier
            .testTag("password_field")
            .semantics { testTagsAsResourceId = true },
        placeholder = { Text(
                        text = stringResource(R.string.secure_field_test_enter_password),
                        color = Configuration.TextField.defaultPlaceholderColor
                    ) },
        isOutlined = true,
        isSecure = true,
        textStyle = TextStyle(fontFamily = resolved_textfield16.family, fontWeight = resolved_textfield16.weight, fontSize = (resolved_textfield16.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield16.style ?: FontStyle.Normal), color = Color(android.graphics.Color.parseColor("#000000"))),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
    )
    val textFieldState_phone_field = rememberTextFieldState(initialText = data.phone)
    LaunchedEffect(data.phone) { if (textFieldState_phone_field.text.toString() != data.phone) textFieldState_phone_field.edit { replace(0, length, data.phone) } }
    LaunchedEffect(textFieldState_phone_field.text) { val newValue = textFieldState_phone_field.text.toString(); if (newValue != data.phone) viewModel.updateData(mapOf("phone" to newValue)) }
    val resolved_textfield17 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextField(
        state = textFieldState_phone_field,
        modifier = Modifier
            .testTag("phone_field")
            .semantics { testTagsAsResourceId = true },
        placeholder = { Text(
                        text = stringResource(R.string.textfield_test_phone_number),
                        color = Configuration.TextField.defaultPlaceholderColor
                    ) },
        isOutlined = true,
        textStyle = TextStyle(fontFamily = resolved_textfield17.family, fontWeight = resolved_textfield17.weight, fontSize = (resolved_textfield17.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield17.style ?: FontStyle.Normal), color = Color(android.graphics.Color.parseColor("#000000"))),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Default)
    )
    val textFieldState_number_field = rememberTextFieldState(initialText = data.number)
    LaunchedEffect(data.number) { if (textFieldState_number_field.text.toString() != data.number) textFieldState_number_field.edit { replace(0, length, data.number) } }
    LaunchedEffect(textFieldState_number_field.text) { val newValue = textFieldState_number_field.text.toString(); if (newValue != data.number) viewModel.updateData(mapOf("number" to newValue)) }
    val resolved_textfield18 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextField(
        state = textFieldState_number_field,
        modifier = Modifier
            .testTag("number_field")
            .semantics { testTagsAsResourceId = true },
        placeholder = { Text(
                        text = stringResource(R.string.textfield_test_enter_number),
                        color = Configuration.TextField.defaultPlaceholderColor
                    ) },
        backgroundColor = colorResource(R.color.white_17),
        textStyle = TextStyle(fontFamily = resolved_textfield18.family, fontWeight = resolved_textfield18.weight, fontSize = (resolved_textfield18.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield18.style ?: FontStyle.Normal), color = Color(android.graphics.Color.parseColor("#000000"))),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Default)
    )
    val textFieldState_search_field = rememberTextFieldState(initialText = data.search)
    LaunchedEffect(data.search) { if (textFieldState_search_field.text.toString() != data.search) textFieldState_search_field.edit { replace(0, length, data.search) } }
    LaunchedEffect(textFieldState_search_field.text) { val newValue = textFieldState_search_field.text.toString(); if (newValue != data.search) viewModel.updateData(mapOf("search" to newValue)) }
    val resolved_textfield19 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextField(
        state = textFieldState_search_field,
        modifier = Modifier
            .testTag("search_field")
            .semantics { testTagsAsResourceId = true },
        placeholder = { Text(
                        text = stringResource(R.string.textfield_test_search),
                        color = Configuration.TextField.defaultPlaceholderColor
                    ) },
        isOutlined = true,
        textStyle = TextStyle(fontFamily = resolved_textfield19.family, fontWeight = resolved_textfield19.weight, fontSize = (resolved_textfield19.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield19.style ?: FontStyle.Normal), color = Color(android.graphics.Color.parseColor("#000000"))),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
    )
    val textFieldState_url_field = rememberTextFieldState(initialText = data.url)
    LaunchedEffect(data.url) { if (textFieldState_url_field.text.toString() != data.url) textFieldState_url_field.edit { replace(0, length, data.url) } }
    LaunchedEffect(textFieldState_url_field.text) { val newValue = textFieldState_url_field.text.toString(); if (newValue != data.url) viewModel.updateData(mapOf("url" to newValue)) }
    val resolved_textfield20 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextField(
        state = textFieldState_url_field,
        modifier = Modifier
            .testTag("url_field")
            .semantics { testTagsAsResourceId = true },
        placeholder = { Text(
                        text = stringResource(R.string.textfield_test_website_url),
                        color = Configuration.TextField.defaultPlaceholderColor
                    ) },
        isOutlined = true,
        textStyle = TextStyle(fontFamily = resolved_textfield20.family, fontWeight = resolved_textfield20.weight, fontSize = (resolved_textfield20.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield20.style ?: FontStyle.Normal), color = Color(android.graphics.Color.parseColor("#000000"))),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Default)
    )
}

@Composable
private fun Section2(
    data: TextfieldTestData,
    viewModel: TextfieldTestViewModel
) {
    val resolved_text405 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.textfield_test_entered_values),
        color = colorResource(R.color.black),
        fontFamily = resolved_text405.family,
        fontWeight = resolved_text405.weight,
        fontSize = resolved_text405.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text405.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section3(
    data: TextfieldTestData,
    viewModel: TextfieldTestViewModel
) {
    val resolved_text406 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.email}",
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text406.family,
        fontWeight = resolved_text406.weight,
        fontSize = resolved_text406.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text406.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section4(
    data: TextfieldTestData,
    viewModel: TextfieldTestViewModel
) {
    val resolved_text407 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.password}",
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text407.family,
        fontWeight = resolved_text407.weight,
        fontSize = resolved_text407.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text407.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section5(
    data: TextfieldTestData,
    viewModel: TextfieldTestViewModel
) {
    val resolved_text408 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.phone}",
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text408.family,
        fontWeight = resolved_text408.weight,
        fontSize = resolved_text408.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text408.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
    )
}
// >>> RESPONSIVE_HELPERS_END