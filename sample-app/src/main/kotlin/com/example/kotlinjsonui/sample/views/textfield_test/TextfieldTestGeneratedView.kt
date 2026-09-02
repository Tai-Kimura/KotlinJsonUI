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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.SecureTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun TextfieldTestGeneratedView(
    data: TextfieldTestData,
    viewModel: TextfieldTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from textfield_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
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
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("textfield_test")
    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: TextfieldTestData,
    viewModel: TextfieldTestViewModel
) {
    val resolved_text1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 20.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.textfield_test_textfield_test),
        color = colorResource(R.color.black),
        fontFamily = resolved_text1.family,
        fontWeight = resolved_text1.weight,
        fontSize = resolved_text1.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text1.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 26.0.sp),
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
    val resolved_textfield1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    val focusRequester_email_field = remember { FocusRequester() }
    val keyboardController_email_field = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.emailFieldIsFocused) { if (data.emailFieldIsFocused) { focusRequester_email_field.requestFocus(); keyboardController_email_field?.show() } }
    CustomTextField(
        state = textFieldState_email_field,
        modifier = Modifier
            .testTag("email_field")
            .semantics { testTagsAsResourceId = true }
            .onFocusChanged { if (it.isFocused != data.emailFieldIsFocused) viewModel.updateData(mapOf("emailFieldIsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_email_field),
        placeholder = { Text(
                        text = stringResource(R.string.textfield_test_enter_email),
                        color = Configuration.TextField.defaultPlaceholderColor
                    ) },
        isOutlined = true,
        textStyle = TextStyle(fontFamily = (resolved_textfield1.family ?: LocalTextStyle.current.fontFamily), fontWeight = (resolved_textfield1.weight ?: LocalTextStyle.current.fontWeight), fontSize = (resolved_textfield1.size ?: LocalTextStyle.current.fontSize), fontStyle = (resolved_textfield1.style ?: LocalTextStyle.current.fontStyle), color = Configuration.TextField.defaultTextColor),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Default)
    )
    val textFieldState_password_field = rememberTextFieldState(initialText = data.password)
    LaunchedEffect(data.password) { if (textFieldState_password_field.text.toString() != data.password) textFieldState_password_field.edit { replace(0, length, data.password) } }
    LaunchedEffect(textFieldState_password_field.text) { val newValue = textFieldState_password_field.text.toString(); if (newValue != data.password) viewModel.updateData(mapOf("password" to newValue)) }
    val resolved_textfield2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    val focusRequester_password_field = remember { FocusRequester() }
    val keyboardController_password_field = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.passwordFieldIsFocused) { if (data.passwordFieldIsFocused) { focusRequester_password_field.requestFocus(); keyboardController_password_field?.show() } }
    CustomTextField(
        state = textFieldState_password_field,
        modifier = Modifier
            .testTag("password_field")
            .semantics { testTagsAsResourceId = true }
            .onFocusChanged { if (it.isFocused != data.passwordFieldIsFocused) viewModel.updateData(mapOf("passwordFieldIsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_password_field),
        placeholder = { Text(
                        text = stringResource(R.string.textfield_test_enter_password),
                        color = Configuration.TextField.defaultPlaceholderColor
                    ) },
        isOutlined = true,
        isSecure = true,
        textStyle = TextStyle(fontFamily = (resolved_textfield2.family ?: LocalTextStyle.current.fontFamily), fontWeight = (resolved_textfield2.weight ?: LocalTextStyle.current.fontWeight), fontSize = (resolved_textfield2.size ?: LocalTextStyle.current.fontSize), fontStyle = (resolved_textfield2.style ?: LocalTextStyle.current.fontStyle), color = Configuration.TextField.defaultTextColor),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
    )
    val textFieldState_phone_field = rememberTextFieldState(initialText = data.phone)
    LaunchedEffect(data.phone) { if (textFieldState_phone_field.text.toString() != data.phone) textFieldState_phone_field.edit { replace(0, length, data.phone) } }
    LaunchedEffect(textFieldState_phone_field.text) { val newValue = textFieldState_phone_field.text.toString(); if (newValue != data.phone) viewModel.updateData(mapOf("phone" to newValue)) }
    val resolved_textfield3 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    val focusRequester_phone_field = remember { FocusRequester() }
    val keyboardController_phone_field = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.phoneFieldIsFocused) { if (data.phoneFieldIsFocused) { focusRequester_phone_field.requestFocus(); keyboardController_phone_field?.show() } }
    CustomTextField(
        state = textFieldState_phone_field,
        modifier = Modifier
            .testTag("phone_field")
            .semantics { testTagsAsResourceId = true }
            .onFocusChanged { if (it.isFocused != data.phoneFieldIsFocused) viewModel.updateData(mapOf("phoneFieldIsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_phone_field),
        placeholder = { Text(
                        text = stringResource(R.string.textfield_test_phone_number),
                        color = Configuration.TextField.defaultPlaceholderColor
                    ) },
        isOutlined = true,
        textStyle = TextStyle(fontFamily = (resolved_textfield3.family ?: LocalTextStyle.current.fontFamily), fontWeight = (resolved_textfield3.weight ?: LocalTextStyle.current.fontWeight), fontSize = (resolved_textfield3.size ?: LocalTextStyle.current.fontSize), fontStyle = (resolved_textfield3.style ?: LocalTextStyle.current.fontStyle), color = Configuration.TextField.defaultTextColor),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Default)
    )
    val textFieldState_number_field = rememberTextFieldState(initialText = data.number)
    LaunchedEffect(data.number) { if (textFieldState_number_field.text.toString() != data.number) textFieldState_number_field.edit { replace(0, length, data.number) } }
    LaunchedEffect(textFieldState_number_field.text) { val newValue = textFieldState_number_field.text.toString(); if (newValue != data.number) viewModel.updateData(mapOf("number" to newValue)) }
    val resolved_textfield4 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    val focusRequester_number_field = remember { FocusRequester() }
    val keyboardController_number_field = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.numberFieldIsFocused) { if (data.numberFieldIsFocused) { focusRequester_number_field.requestFocus(); keyboardController_number_field?.show() } }
    CustomTextField(
        state = textFieldState_number_field,
        modifier = Modifier
            .testTag("number_field")
            .semantics { testTagsAsResourceId = true }
            .onFocusChanged { if (it.isFocused != data.numberFieldIsFocused) viewModel.updateData(mapOf("numberFieldIsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_number_field),
        placeholder = { Text(
                        text = stringResource(R.string.textfield_test_enter_number),
                        color = Configuration.TextField.defaultPlaceholderColor
                    ) },
        backgroundColor = colorResource(R.color.white_17),
        textStyle = TextStyle(fontFamily = (resolved_textfield4.family ?: LocalTextStyle.current.fontFamily), fontWeight = (resolved_textfield4.weight ?: LocalTextStyle.current.fontWeight), fontSize = (resolved_textfield4.size ?: LocalTextStyle.current.fontSize), fontStyle = (resolved_textfield4.style ?: LocalTextStyle.current.fontStyle), color = Configuration.TextField.defaultTextColor),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Default)
    )
    val textFieldState_search_field = rememberTextFieldState(initialText = data.search)
    LaunchedEffect(data.search) { if (textFieldState_search_field.text.toString() != data.search) textFieldState_search_field.edit { replace(0, length, data.search) } }
    LaunchedEffect(textFieldState_search_field.text) { val newValue = textFieldState_search_field.text.toString(); if (newValue != data.search) viewModel.updateData(mapOf("search" to newValue)) }
    val resolved_textfield5 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    val focusRequester_search_field = remember { FocusRequester() }
    val keyboardController_search_field = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.searchFieldIsFocused) { if (data.searchFieldIsFocused) { focusRequester_search_field.requestFocus(); keyboardController_search_field?.show() } }
    CustomTextField(
        state = textFieldState_search_field,
        modifier = Modifier
            .testTag("search_field")
            .semantics { testTagsAsResourceId = true }
            .onFocusChanged { if (it.isFocused != data.searchFieldIsFocused) viewModel.updateData(mapOf("searchFieldIsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_search_field),
        placeholder = { Text(
                        text = stringResource(R.string.textfield_test_search),
                        color = Configuration.TextField.defaultPlaceholderColor
                    ) },
        isOutlined = true,
        textStyle = TextStyle(fontFamily = (resolved_textfield5.family ?: LocalTextStyle.current.fontFamily), fontWeight = (resolved_textfield5.weight ?: LocalTextStyle.current.fontWeight), fontSize = (resolved_textfield5.size ?: LocalTextStyle.current.fontSize), fontStyle = (resolved_textfield5.style ?: LocalTextStyle.current.fontStyle), color = Configuration.TextField.defaultTextColor),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
    )
    val textFieldState_url_field = rememberTextFieldState(initialText = data.url)
    LaunchedEffect(data.url) { if (textFieldState_url_field.text.toString() != data.url) textFieldState_url_field.edit { replace(0, length, data.url) } }
    LaunchedEffect(textFieldState_url_field.text) { val newValue = textFieldState_url_field.text.toString(); if (newValue != data.url) viewModel.updateData(mapOf("url" to newValue)) }
    val resolved_textfield6 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    val focusRequester_url_field = remember { FocusRequester() }
    val keyboardController_url_field = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.urlFieldIsFocused) { if (data.urlFieldIsFocused) { focusRequester_url_field.requestFocus(); keyboardController_url_field?.show() } }
    CustomTextField(
        state = textFieldState_url_field,
        modifier = Modifier
            .testTag("url_field")
            .semantics { testTagsAsResourceId = true }
            .onFocusChanged { if (it.isFocused != data.urlFieldIsFocused) viewModel.updateData(mapOf("urlFieldIsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_url_field),
        placeholder = { Text(
                        text = stringResource(R.string.textfield_test_website_url),
                        color = Configuration.TextField.defaultPlaceholderColor
                    ) },
        isOutlined = true,
        textStyle = TextStyle(fontFamily = (resolved_textfield6.family ?: LocalTextStyle.current.fontFamily), fontWeight = (resolved_textfield6.weight ?: LocalTextStyle.current.fontWeight), fontSize = (resolved_textfield6.size ?: LocalTextStyle.current.fontSize), fontStyle = (resolved_textfield6.style ?: LocalTextStyle.current.fontStyle), color = Configuration.TextField.defaultTextColor),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Default)
    )
}

@Composable
private fun Section2(
    data: TextfieldTestData,
    viewModel: TextfieldTestViewModel
) {
    val resolved_text2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.textfield_test_entered_values),
        color = colorResource(R.color.black),
        fontFamily = resolved_text2.family,
        fontWeight = resolved_text2.weight,
        fontSize = resolved_text2.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text2.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section3(
    data: TextfieldTestData,
    viewModel: TextfieldTestViewModel
) {
    val resolved_text3 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.email}",
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text3.family,
        fontWeight = resolved_text3.weight,
        fontSize = resolved_text3.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text3.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section4(
    data: TextfieldTestData,
    viewModel: TextfieldTestViewModel
) {
    val resolved_text4 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.password}",
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text4.family,
        fontWeight = resolved_text4.weight,
        fontSize = resolved_text4.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text4.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section5(
    data: TextfieldTestData,
    viewModel: TextfieldTestViewModel
) {
    val resolved_text5 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.phone}",
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text5.family,
        fontWeight = resolved_text5.weight,
        fontSize = resolved_text5.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text5.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier
    )
}
// >>> RESPONSIVE_HELPERS_END