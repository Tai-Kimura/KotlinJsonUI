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
import com.example.kotlinjsonui.sample.data.TextfieldEventsTestData
import com.example.kotlinjsonui.sample.viewmodels.TextfieldEventsTestViewModel
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont
import com.kotlinjsonui.dynamic.LocalSafeAreaConfig
import com.kotlinjsonui.dynamic.SafeAreaConfig

@Composable
fun TextfieldEventsTestGeneratedView(
    data: TextfieldEventsTestData,
    viewModel: TextfieldEventsTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from textfield_events_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
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
                val resolved_text286 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = FontWeight.Bold,
                    size = 24.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.test_menu_textfield_events_test),
                    fontFamily = resolved_text286.family,
                    fontWeight = resolved_text286.weight,
                    fontSize = resolved_text286.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text286.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 31.2.sp),
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
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section1(
    data: TextfieldEventsTestData,
    viewModel: TextfieldEventsTestViewModel
) {
    val resolved_text287 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.textfield_events_test_ontextchange_event_test),
        fontFamily = resolved_text287.family,
        fontWeight = resolved_text287.weight,
        fontSize = resolved_text287.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text287.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
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
    CustomTextFieldWithMargins(
        state = textFieldState_emailField,
        boxModifier = Modifier
            .testTag("emailField")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp),
        placeholder = { Text(
                                text = stringResource(R.string.textfield_events_test_enter_email),
                                color = Configuration.TextField.defaultPlaceholderColor
                            ) },
        isOutlined = true,
        textStyle = TextStyle(color = Color(android.graphics.Color.parseColor("#000000"))),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Default)
    )
    val resolved_text288 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.emailDisplay}",
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text288.family,
        fontWeight = resolved_text288.weight,
        fontSize = resolved_text288.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text288.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .testTag("emailStatus")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
    )
    val resolved_text289 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.textfield_events_test_secure_textfield_test),
        fontFamily = resolved_text289.family,
        fontWeight = resolved_text289.weight,
        fontSize = resolved_text289.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text289.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .padding(start = 20.dp)
    )
    val textFieldState_passwordField = rememberTextFieldState(initialText = data.password)
    LaunchedEffect(data.password) { if (textFieldState_passwordField.text.toString() != data.password) textFieldState_passwordField.edit { replace(0, length, data.password) } }
    LaunchedEffect(textFieldState_passwordField.text) { val newValue = textFieldState_passwordField.text.toString(); if (newValue != data.password) { viewModel.updateData(mapOf("password" to newValue)); data.handlePasswordChange?.invoke("passwordField", newValue) } }
    CustomTextFieldWithMargins(
        state = textFieldState_passwordField,
        boxModifier = Modifier
            .testTag("passwordField")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp),
        placeholder = { Text(
                                text = stringResource(R.string.secure_field_test_enter_password),
                                color = Configuration.TextField.defaultPlaceholderColor
                            ) },
        isOutlined = true,
        isSecure = true,
        textStyle = TextStyle(color = Color(android.graphics.Color.parseColor("#000000"))),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
    )
    val resolved_text290 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.passwordLength}",
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text290.family,
        fontWeight = resolved_text290.weight,
        fontSize = resolved_text290.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text290.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
    )
    val resolved_text291 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.textfield_events_test_input_accessory_test),
        fontFamily = resolved_text291.family,
        fontWeight = resolved_text291.weight,
        fontSize = resolved_text291.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text291.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .padding(start = 20.dp)
    )
    val textFieldState_notesField = rememberTextFieldState(initialText = data.notes)
    LaunchedEffect(data.notes) { if (textFieldState_notesField.text.toString() != data.notes) textFieldState_notesField.edit { replace(0, length, data.notes) } }
    LaunchedEffect(textFieldState_notesField.text) { val newValue = textFieldState_notesField.text.toString(); if (newValue != data.notes) viewModel.updateData(mapOf("notes" to newValue)) }
    CustomTextFieldWithMargins(
        state = textFieldState_notesField,
        boxModifier = Modifier
            .testTag("notesField")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp),
        placeholder = { Text(
                                text = stringResource(R.string.textfield_events_test_enter_notes),
                                color = Configuration.TextField.defaultPlaceholderColor
                            ) },
        textStyle = TextStyle(color = Color(android.graphics.Color.parseColor("#000000")))
    )
}
// >>> RESPONSIVE_HELPERS_END