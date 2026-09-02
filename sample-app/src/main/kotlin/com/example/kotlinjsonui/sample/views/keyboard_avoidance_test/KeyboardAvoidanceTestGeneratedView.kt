package com.example.kotlinjsonui.sample.views.keyboard_avoidance_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.KeyboardAvoidanceTestData
import com.example.kotlinjsonui.sample.viewmodels.KeyboardAvoidanceTestViewModel
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
fun KeyboardAvoidanceTestGeneratedView(
    data: KeyboardAvoidanceTestData,
    viewModel: KeyboardAvoidanceTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from keyboard_avoidance_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
        // Check if Dynamic Mode is active
        if (DynamicModeManager.isActive()) {
            // Dynamic Mode - use SafeDynamicView for real-time updates
            SafeDynamicView(
                layoutName = "keyboard_avoidance_test",
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
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(colorResource(R.color.white))
                .imePadding()
        ) {
            item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp)
            ) {
                Section0(data, viewModel)
                Section1(data, viewModel)
                Section2(data, viewModel)
                Section3(data, viewModel)
                Section4(data, viewModel)
            }
            }
        }    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("keyboard_avoidance_test")
    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: KeyboardAvoidanceTestData,
    viewModel: KeyboardAvoidanceTestViewModel
) {
    Button(
        onClick = { data.toggleDynamicMode?.invoke() },
        modifier = Modifier
            .wrapContentWidth()
            .requiredHeight(44.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(android.graphics.Color.parseColor("#5856D6")),
                            disabledContainerColor = Color(android.graphics.Color.parseColor("#5856D6")).copy(alpha = 0.5f),
                            contentColor = colorResource(R.color.white),
                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                        )
    ) {
        val resolved_button1 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button1.family,
            fontWeight = resolved_button1.weight,
            fontSize = resolved_button1.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button1.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section1(
    data: KeyboardAvoidanceTestData,
    viewModel: KeyboardAvoidanceTestViewModel
) {
    val resolved_text1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = "${data.title}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text1.family,
        fontWeight = resolved_text1.weight,
        fontSize = resolved_text1.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text1.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 31.2.sp),
        modifier = Modifier.padding(bottom = 20.dp)
    )
}

@Composable
private fun Section2(
    data: KeyboardAvoidanceTestData,
    viewModel: KeyboardAvoidanceTestViewModel
) {
    val resolved_text2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.keyboard_avoidance_test_textfield_1),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text2.family,
        fontWeight = resolved_text2.weight,
        fontSize = resolved_text2.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text2.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun Section3(
    data: KeyboardAvoidanceTestData,
    viewModel: KeyboardAvoidanceTestViewModel
) {
    val textFieldState_textfield1 = rememberTextFieldState(initialText = data.textField1)
    LaunchedEffect(data.textField1) { if (textFieldState_textfield1.text.toString() != data.textField1) textFieldState_textfield1.edit { replace(0, length, data.textField1) } }
    LaunchedEffect(textFieldState_textfield1.text) { val newValue = textFieldState_textfield1.text.toString(); if (newValue != data.textField1) viewModel.updateData(mapOf("textField1" to newValue)) }
    val resolved_textfield1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    val focusRequester_textfield1 = remember { FocusRequester() }
    val keyboardController_textfield1 = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.textfield1IsFocused) { if (data.textfield1IsFocused) { focusRequester_textfield1.requestFocus(); keyboardController_textfield1?.show() } }
    CustomTextFieldWithMargins(
        state = textFieldState_textfield1,
        boxModifier = Modifier
            .testTag("textfield1")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .requiredHeight(50.dp)
            .onFocusChanged { if (it.isFocused != data.textfield1IsFocused) viewModel.updateData(mapOf("textfield1IsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_textfield1),
        placeholder = { Text(
                            text = stringResource(R.string.keyboard_avoidance_test_enter_text_here),
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray_4),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = (resolved_textfield1.family ?: LocalTextStyle.current.fontFamily), fontWeight = (resolved_textfield1.weight ?: LocalTextStyle.current.fontWeight), fontSize = (resolved_textfield1.size ?: LocalTextStyle.current.fontSize), fontStyle = (resolved_textfield1.style ?: LocalTextStyle.current.fontStyle), color = colorResource(R.color.black))
    )
    val resolved_text3 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.keyboard_avoidance_test_textfield_2),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text3.family,
        fontWeight = resolved_text3.weight,
        fontSize = resolved_text3.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text3.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 8.dp)
    )
    val textFieldState_textfield2 = rememberTextFieldState(initialText = data.textField2)
    LaunchedEffect(data.textField2) { if (textFieldState_textfield2.text.toString() != data.textField2) textFieldState_textfield2.edit { replace(0, length, data.textField2) } }
    LaunchedEffect(textFieldState_textfield2.text) { val newValue = textFieldState_textfield2.text.toString(); if (newValue != data.textField2) viewModel.updateData(mapOf("textField2" to newValue)) }
    val resolved_textfield2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    val focusRequester_textfield2 = remember { FocusRequester() }
    val keyboardController_textfield2 = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.textfield2IsFocused) { if (data.textfield2IsFocused) { focusRequester_textfield2.requestFocus(); keyboardController_textfield2?.show() } }
    CustomTextFieldWithMargins(
        state = textFieldState_textfield2,
        boxModifier = Modifier
            .testTag("textfield2")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .requiredHeight(50.dp)
            .onFocusChanged { if (it.isFocused != data.textfield2IsFocused) viewModel.updateData(mapOf("textfield2IsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_textfield2),
        placeholder = { Text(
                            text = stringResource(R.string.keyboard_avoidance_test_another_text_field),
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray_4),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = (resolved_textfield2.family ?: LocalTextStyle.current.fontFamily), fontWeight = (resolved_textfield2.weight ?: LocalTextStyle.current.fontWeight), fontSize = (resolved_textfield2.size ?: LocalTextStyle.current.fontSize), fontStyle = (resolved_textfield2.style ?: LocalTextStyle.current.fontStyle), color = colorResource(R.color.black))
    )
    val resolved_text4 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.keyboard_avoidance_test_textfield_3),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text4.family,
        fontWeight = resolved_text4.weight,
        fontSize = resolved_text4.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text4.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 8.dp)
    )
    val textFieldState_textfield3 = rememberTextFieldState(initialText = data.textField3)
    LaunchedEffect(data.textField3) { if (textFieldState_textfield3.text.toString() != data.textField3) textFieldState_textfield3.edit { replace(0, length, data.textField3) } }
    LaunchedEffect(textFieldState_textfield3.text) { val newValue = textFieldState_textfield3.text.toString(); if (newValue != data.textField3) viewModel.updateData(mapOf("textField3" to newValue)) }
    val resolved_textfield3 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    val focusRequester_textfield3 = remember { FocusRequester() }
    val keyboardController_textfield3 = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.textfield3IsFocused) { if (data.textfield3IsFocused) { focusRequester_textfield3.requestFocus(); keyboardController_textfield3?.show() } }
    CustomTextFieldWithMargins(
        state = textFieldState_textfield3,
        boxModifier = Modifier
            .testTag("textfield3")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .requiredHeight(50.dp)
            .onFocusChanged { if (it.isFocused != data.textfield3IsFocused) viewModel.updateData(mapOf("textfield3IsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_textfield3),
        placeholder = { Text(
                            text = stringResource(R.string.keyboard_avoidance_test_keep_typing),
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray_4),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = (resolved_textfield3.family ?: LocalTextStyle.current.fontFamily), fontWeight = (resolved_textfield3.weight ?: LocalTextStyle.current.fontWeight), fontSize = (resolved_textfield3.size ?: LocalTextStyle.current.fontSize), fontStyle = (resolved_textfield3.style ?: LocalTextStyle.current.fontStyle), color = colorResource(R.color.black))
    )
    val resolved_text5 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.keyboard_avoidance_test_textfield_4),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text5.family,
        fontWeight = resolved_text5.weight,
        fontSize = resolved_text5.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text5.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 8.dp)
    )
    val textFieldState_textfield4 = rememberTextFieldState(initialText = data.textField4)
    LaunchedEffect(data.textField4) { if (textFieldState_textfield4.text.toString() != data.textField4) textFieldState_textfield4.edit { replace(0, length, data.textField4) } }
    LaunchedEffect(textFieldState_textfield4.text) { val newValue = textFieldState_textfield4.text.toString(); if (newValue != data.textField4) viewModel.updateData(mapOf("textField4" to newValue)) }
    val resolved_textfield4 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    val focusRequester_textfield4 = remember { FocusRequester() }
    val keyboardController_textfield4 = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.textfield4IsFocused) { if (data.textfield4IsFocused) { focusRequester_textfield4.requestFocus(); keyboardController_textfield4?.show() } }
    CustomTextFieldWithMargins(
        state = textFieldState_textfield4,
        boxModifier = Modifier
            .testTag("textfield4")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .requiredHeight(50.dp)
            .onFocusChanged { if (it.isFocused != data.textfield4IsFocused) viewModel.updateData(mapOf("textfield4IsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_textfield4),
        placeholder = { Text(
                            text = stringResource(R.string.keyboard_avoidance_test_this_should_scroll_up),
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray_4),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = (resolved_textfield4.family ?: LocalTextStyle.current.fontFamily), fontWeight = (resolved_textfield4.weight ?: LocalTextStyle.current.fontWeight), fontSize = (resolved_textfield4.size ?: LocalTextStyle.current.fontSize), fontStyle = (resolved_textfield4.style ?: LocalTextStyle.current.fontStyle), color = colorResource(R.color.black))
    )
    val resolved_text6 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.keyboard_avoidance_test_textfield_5_at_bottom),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text6.family,
        fontWeight = resolved_text6.weight,
        fontSize = resolved_text6.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text6.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 8.dp)
    )
    val textFieldState_textfield5 = rememberTextFieldState(initialText = data.textField5)
    LaunchedEffect(data.textField5) { if (textFieldState_textfield5.text.toString() != data.textField5) textFieldState_textfield5.edit { replace(0, length, data.textField5) } }
    LaunchedEffect(textFieldState_textfield5.text) { val newValue = textFieldState_textfield5.text.toString(); if (newValue != data.textField5) viewModel.updateData(mapOf("textField5" to newValue)) }
    val resolved_textfield5 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    val focusRequester_textfield5 = remember { FocusRequester() }
    val keyboardController_textfield5 = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.textfield5IsFocused) { if (data.textfield5IsFocused) { focusRequester_textfield5.requestFocus(); keyboardController_textfield5?.show() } }
    CustomTextFieldWithMargins(
        state = textFieldState_textfield5,
        boxModifier = Modifier
            .testTag("textfield5")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .requiredHeight(50.dp)
            .onFocusChanged { if (it.isFocused != data.textfield5IsFocused) viewModel.updateData(mapOf("textfield5IsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_textfield5),
        placeholder = { Text(
                            text = stringResource(R.string.keyboard_avoidance_test_this_is_near_the_bottom),
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray_4),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = (resolved_textfield5.family ?: LocalTextStyle.current.fontFamily), fontWeight = (resolved_textfield5.weight ?: LocalTextStyle.current.fontWeight), fontSize = (resolved_textfield5.size ?: LocalTextStyle.current.fontSize), fontStyle = (resolved_textfield5.style ?: LocalTextStyle.current.fontStyle), color = colorResource(R.color.black))
    )
    val resolved_text7 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.keyboard_avoidance_test_textview_multiline),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text7.family,
        fontWeight = resolved_text7.weight,
        fontSize = resolved_text7.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text7.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 8.dp)
    )
    val textFieldState_textview = rememberTextFieldState(initialText = data.textView)
    LaunchedEffect(data.textView) { if (textFieldState_textview.text.toString() != data.textView) textFieldState_textview.edit { replace(0, length, data.textView) } }
    LaunchedEffect(textFieldState_textview.text) { val newValue = textFieldState_textview.text.toString(); if (newValue != data.textView) viewModel.updateData(mapOf("textView" to newValue)) }
    val resolved_textview1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    val focusRequester_textview = remember { FocusRequester() }
    val keyboardController_textview = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.textviewIsFocused) { if (data.textviewIsFocused) { focusRequester_textview.requestFocus(); keyboardController_textview?.show() } }
    CustomTextFieldWithMargins(
        state = textFieldState_textview,
        boxModifier = Modifier
            .testTag("textview")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .onFocusChanged { if (it.isFocused != data.textviewIsFocused) viewModel.updateData(mapOf("textviewIsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_textview),
        placeholder = {
            Text(
                text = stringResource(R.string.keyboard_avoidance_test_multiline_text_input_type_here),
                color = colorResource(R.color.light_gray_8)
            )
        },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.white),
        isOutlined = true,
        maxLines = Int.MAX_VALUE,
        singleLine = false,
        textStyle = TextStyle(fontFamily = (resolved_textview1.family ?: LocalTextStyle.current.fontFamily), fontWeight = (resolved_textview1.weight ?: LocalTextStyle.current.fontWeight), fontSize = (resolved_textview1.size ?: LocalTextStyle.current.fontSize), fontStyle = (resolved_textview1.style ?: LocalTextStyle.current.fontStyle), color = colorResource(R.color.dark_gray))
    )
}

@Composable
private fun Section4(
    data: KeyboardAvoidanceTestData,
    viewModel: KeyboardAvoidanceTestViewModel
) {
    Button(
        onClick = { data.submitForm?.invoke() },
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(50.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.medium_blue),
                            disabledContainerColor = colorResource(R.color.medium_blue).copy(alpha = 0.5f),
                            contentColor = colorResource(R.color.white),
                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                        )
    ) {
        val resolved_button2 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 18.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.keyboard_avoidance_test_submit),
            fontFamily = resolved_button2.family,
            fontWeight = resolved_button2.weight,
            fontSize = resolved_button2.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button2.style ?: FontStyle.Normal,
        )
    }
}
// >>> RESPONSIVE_HELPERS_END