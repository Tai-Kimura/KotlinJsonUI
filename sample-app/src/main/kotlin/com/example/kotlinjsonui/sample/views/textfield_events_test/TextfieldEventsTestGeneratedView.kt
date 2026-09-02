package com.example.kotlinjsonui.sample.views.textfield_events_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.imePadding
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
import com.example.kotlinjsonui.sample.data.TextfieldEventsTestData
import com.example.kotlinjsonui.sample.viewmodels.TextfieldEventsTestViewModel
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.dynamic.LocalSafeAreaConfig
import com.kotlinjsonui.dynamic.SafeAreaConfig
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun TextfieldEventsTestGeneratedView(
    data: TextfieldEventsTestData,
    viewModel: TextfieldEventsTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from textfield_events_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
        // Check if Dynamic Mode is active
        if (DynamicModeManager.isActive()) {
            // Dynamic Mode - use SafeDynamicView for real-time updates
            SafeDynamicView(
                layoutName = "textfield_events_test",
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
        Section6(data, viewModel, modifier)    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("textfield_events_test")
    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section1(
    data: TextfieldEventsTestData,
    viewModel: TextfieldEventsTestViewModel
) {
    val resolved_text2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.textfield_events_test_ontextchange_event_test),
        fontFamily = resolved_text2.family,
        fontWeight = resolved_text2.weight,
        fontSize = resolved_text2.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text2.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
    )
}

@Composable
private fun Section2(
    data: TextfieldEventsTestData,
    viewModel: TextfieldEventsTestViewModel
) {
    val textFieldState_emailField = rememberTextFieldState(initialText = data.email)
    LaunchedEffect(data.email) { if (textFieldState_emailField.text.toString() != data.email) textFieldState_emailField.edit { replace(0, length, data.email) } }
    LaunchedEffect(textFieldState_emailField.text) { val newValue = textFieldState_emailField.text.toString(); if (newValue != data.email) { viewModel.updateData(mapOf("email" to newValue)); data.handleEmailChange?.invoke("emailField", newValue) } }
    val focusRequester_emailField = remember { FocusRequester() }
    val keyboardController_emailField = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.emailFieldIsFocused) { if (data.emailFieldIsFocused) { focusRequester_emailField.requestFocus(); keyboardController_emailField?.show() } }
    CustomTextFieldWithMargins(
        state = textFieldState_emailField,
        boxModifier = Modifier
            .testTag("emailField")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp),
        textFieldModifier = Modifier
            .onFocusChanged { if (it.isFocused != data.emailFieldIsFocused) viewModel.updateData(mapOf("emailFieldIsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_emailField),
        placeholder = { Text(
                                text = stringResource(R.string.textfield_events_test_enter_email),
                                color = Configuration.TextField.defaultPlaceholderColor
                            ) },
        isOutlined = true,
        textStyle = TextStyle(color = Configuration.TextField.defaultTextColor, fontSize = Configuration.TextField.defaultFontSize.sp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Default)
    )
    val resolved_text3 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.emailDisplay}",
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text3.family,
        fontWeight = resolved_text3.weight,
        fontSize = resolved_text3.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text3.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier
            .testTag("emailStatus")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
    )
    val resolved_text4 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.textfield_events_test_secure_textfield_test),
        fontFamily = resolved_text4.family,
        fontWeight = resolved_text4.weight,
        fontSize = resolved_text4.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text4.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .padding(start = 20.dp)
    )
    val textFieldState_passwordField = rememberTextFieldState(initialText = data.password)
    LaunchedEffect(data.password) { if (textFieldState_passwordField.text.toString() != data.password) textFieldState_passwordField.edit { replace(0, length, data.password) } }
    LaunchedEffect(textFieldState_passwordField.text) { val newValue = textFieldState_passwordField.text.toString(); if (newValue != data.password) { viewModel.updateData(mapOf("password" to newValue)); data.handlePasswordChange?.invoke("passwordField", newValue) } }
    val focusRequester_passwordField = remember { FocusRequester() }
    val keyboardController_passwordField = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.passwordFieldIsFocused) { if (data.passwordFieldIsFocused) { focusRequester_passwordField.requestFocus(); keyboardController_passwordField?.show() } }
    CustomTextFieldWithMargins(
        state = textFieldState_passwordField,
        boxModifier = Modifier
            .testTag("passwordField")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp),
        textFieldModifier = Modifier
            .onFocusChanged { if (it.isFocused != data.passwordFieldIsFocused) viewModel.updateData(mapOf("passwordFieldIsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_passwordField),
        placeholder = { Text(
                                text = stringResource(R.string.textfield_events_test_enter_password),
                                color = Configuration.TextField.defaultPlaceholderColor
                            ) },
        isOutlined = true,
        isSecure = true,
        textStyle = TextStyle(color = Configuration.TextField.defaultTextColor, fontSize = Configuration.TextField.defaultFontSize.sp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
    )
    val resolved_text5 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.passwordLength}",
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text5.family,
        fontWeight = resolved_text5.weight,
        fontSize = resolved_text5.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text5.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
    )
    val resolved_text6 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.textfield_events_test_input_accessory_test),
        fontFamily = resolved_text6.family,
        fontWeight = resolved_text6.weight,
        fontSize = resolved_text6.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text6.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .padding(start = 20.dp)
    )
    val textFieldState_notesField = rememberTextFieldState(initialText = data.notes)
    LaunchedEffect(data.notes) { if (textFieldState_notesField.text.toString() != data.notes) textFieldState_notesField.edit { replace(0, length, data.notes) } }
    LaunchedEffect(textFieldState_notesField.text) { val newValue = textFieldState_notesField.text.toString(); if (newValue != data.notes) viewModel.updateData(mapOf("notes" to newValue)) }
    val focusRequester_notesField = remember { FocusRequester() }
    val keyboardController_notesField = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.notesFieldIsFocused) { if (data.notesFieldIsFocused) { focusRequester_notesField.requestFocus(); keyboardController_notesField?.show() } }
    CustomTextFieldWithMargins(
        state = textFieldState_notesField,
        boxModifier = Modifier
            .testTag("notesField")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp),
        textFieldModifier = Modifier
            .onFocusChanged { if (it.isFocused != data.notesFieldIsFocused) viewModel.updateData(mapOf("notesFieldIsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_notesField),
        placeholder = { Text(
                                text = stringResource(R.string.textfield_events_test_enter_notes),
                                color = Configuration.TextField.defaultPlaceholderColor
                            ) },
        textStyle = TextStyle(color = Configuration.TextField.defaultTextColor, fontSize = Configuration.TextField.defaultFontSize.sp)
    )
}

@Composable
private fun Section6(
    data: TextfieldEventsTestData,
    viewModel: TextfieldEventsTestViewModel,
    modifier: Modifier
) {
    val safeAreaConfig = LocalSafeAreaConfig.current
    val edges = mutableListOf("all").apply {
        if (safeAreaConfig.ignoreBottom) {
            remove("bottom")
            if (contains("all")) { remove("all"); addAll(listOf("top", "start", "end")) }
        }
        if (safeAreaConfig.ignoreTop) {
            remove("top")
            if (contains("all")) { remove("all"); addAll(listOf("bottom", "start", "end")) }
        }
    }.distinct()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(if (edges.contains("all")) Modifier.systemBarsPadding() else Modifier)
            .then(if (!edges.contains("all") && edges.contains("top")) Modifier.statusBarsPadding() else Modifier)
            .then(if (!edges.contains("all") && edges.contains("bottom")) Modifier.navigationBarsPadding() else Modifier)
            .imePadding()
    ) {
        LazyColumn(
            modifier = Modifier.imePadding()
        ) {
            item {
            Column(
                modifier = Modifier
                    .testTag("container")
                    .semantics { testTagsAsResourceId = true }
                    .background(colorResource(R.color.white_23))
            ) {
                val resolved_text1 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = FontWeight.Bold,
                    size = 24.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.textfield_events_test_textfield_events_test),
                    fontFamily = resolved_text1.family,
                    fontWeight = resolved_text1.weight,
                    fontSize = resolved_text1.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text1.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 31.2.sp),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp)
                        .padding(bottom = 20.dp),
                    textAlign = TextAlign.Center
                )
                Section1(data, viewModel)
                Section2(data, viewModel)
            }
            }
        }
    }
}
// >>> RESPONSIVE_HELPERS_END