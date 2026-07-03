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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
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

@Composable
fun KeyboardAvoidanceTestGeneratedView(
    data: KeyboardAvoidanceTestData,
    viewModel: KeyboardAvoidanceTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from keyboard_avoidance_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
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
            .height(44.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(android.graphics.Color.parseColor("#5856D6")),
                            contentColor = colorResource(R.color.white)
                        )
    ) {
        val resolved_button28 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button28.family,
            fontWeight = resolved_button28.weight,
            fontSize = resolved_button28.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button28.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section1(
    data: KeyboardAvoidanceTestData,
    viewModel: KeyboardAvoidanceTestViewModel
) {
    val resolved_text439 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = "${data.title}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text439.family,
        fontWeight = resolved_text439.weight,
        fontSize = resolved_text439.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text439.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 31.2.sp),
        modifier = Modifier.padding(bottom = 20.dp)
    )
}

@Composable
private fun Section2(
    data: KeyboardAvoidanceTestData,
    viewModel: KeyboardAvoidanceTestViewModel
) {
    val resolved_text440 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.keyboard_avoidance_test_textfield_1),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text440.family,
        fontWeight = resolved_text440.weight,
        fontSize = resolved_text440.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text440.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
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
    val resolved_textfield22 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_textfield1,
        boxModifier = Modifier
            .testTag("textfield1")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        placeholder = { Text(
                            text = stringResource(R.string.keyboard_avoidance_test_enter_text_here),
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray_4),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = resolved_textfield22.family, fontWeight = resolved_textfield22.weight, fontSize = (resolved_textfield22.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield22.style ?: FontStyle.Normal), color = colorResource(R.color.black))
    )
    val resolved_text441 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.keyboard_avoidance_test_textfield_2),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text441.family,
        fontWeight = resolved_text441.weight,
        fontSize = resolved_text441.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text441.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 8.dp)
    )
    val textFieldState_textfield2 = rememberTextFieldState(initialText = data.textField2)
    LaunchedEffect(data.textField2) { if (textFieldState_textfield2.text.toString() != data.textField2) textFieldState_textfield2.edit { replace(0, length, data.textField2) } }
    LaunchedEffect(textFieldState_textfield2.text) { val newValue = textFieldState_textfield2.text.toString(); if (newValue != data.textField2) viewModel.updateData(mapOf("textField2" to newValue)) }
    val resolved_textfield23 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_textfield2,
        boxModifier = Modifier
            .testTag("textfield2")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        placeholder = { Text(
                            text = stringResource(R.string.keyboard_avoidance_test_another_text_field),
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray_4),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = resolved_textfield23.family, fontWeight = resolved_textfield23.weight, fontSize = (resolved_textfield23.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield23.style ?: FontStyle.Normal), color = colorResource(R.color.black))
    )
    val resolved_text442 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.keyboard_avoidance_test_textfield_3),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text442.family,
        fontWeight = resolved_text442.weight,
        fontSize = resolved_text442.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text442.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 8.dp)
    )
    val textFieldState_textfield3 = rememberTextFieldState(initialText = data.textField3)
    LaunchedEffect(data.textField3) { if (textFieldState_textfield3.text.toString() != data.textField3) textFieldState_textfield3.edit { replace(0, length, data.textField3) } }
    LaunchedEffect(textFieldState_textfield3.text) { val newValue = textFieldState_textfield3.text.toString(); if (newValue != data.textField3) viewModel.updateData(mapOf("textField3" to newValue)) }
    val resolved_textfield24 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_textfield3,
        boxModifier = Modifier
            .testTag("textfield3")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        placeholder = { Text(
                            text = stringResource(R.string.keyboard_avoidance_test_keep_typing),
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray_4),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = resolved_textfield24.family, fontWeight = resolved_textfield24.weight, fontSize = (resolved_textfield24.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield24.style ?: FontStyle.Normal), color = colorResource(R.color.black))
    )
    val resolved_text443 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.keyboard_avoidance_test_textfield_4),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text443.family,
        fontWeight = resolved_text443.weight,
        fontSize = resolved_text443.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text443.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 8.dp)
    )
    val textFieldState_textfield4 = rememberTextFieldState(initialText = data.textField4)
    LaunchedEffect(data.textField4) { if (textFieldState_textfield4.text.toString() != data.textField4) textFieldState_textfield4.edit { replace(0, length, data.textField4) } }
    LaunchedEffect(textFieldState_textfield4.text) { val newValue = textFieldState_textfield4.text.toString(); if (newValue != data.textField4) viewModel.updateData(mapOf("textField4" to newValue)) }
    val resolved_textfield25 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_textfield4,
        boxModifier = Modifier
            .testTag("textfield4")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        placeholder = { Text(
                            text = stringResource(R.string.keyboard_avoidance_test_this_should_scroll_up),
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray_4),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = resolved_textfield25.family, fontWeight = resolved_textfield25.weight, fontSize = (resolved_textfield25.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield25.style ?: FontStyle.Normal), color = colorResource(R.color.black))
    )
    val resolved_text444 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.keyboard_avoidance_test_textfield_5_at_bottom),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text444.family,
        fontWeight = resolved_text444.weight,
        fontSize = resolved_text444.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text444.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 8.dp)
    )
    val textFieldState_textfield5 = rememberTextFieldState(initialText = data.textField5)
    LaunchedEffect(data.textField5) { if (textFieldState_textfield5.text.toString() != data.textField5) textFieldState_textfield5.edit { replace(0, length, data.textField5) } }
    LaunchedEffect(textFieldState_textfield5.text) { val newValue = textFieldState_textfield5.text.toString(); if (newValue != data.textField5) viewModel.updateData(mapOf("textField5" to newValue)) }
    val resolved_textfield26 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_textfield5,
        boxModifier = Modifier
            .testTag("textfield5")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        placeholder = { Text(
                            text = stringResource(R.string.keyboard_avoidance_test_this_is_near_the_bottom),
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray_4),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = resolved_textfield26.family, fontWeight = resolved_textfield26.weight, fontSize = (resolved_textfield26.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield26.style ?: FontStyle.Normal), color = colorResource(R.color.black))
    )
    val resolved_text445 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.keyboard_avoidance_test_textview_multiline),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text445.family,
        fontWeight = resolved_text445.weight,
        fontSize = resolved_text445.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text445.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 8.dp)
    )
    val textFieldState_textview = rememberTextFieldState(initialText = data.textView)
    LaunchedEffect(data.textView) { if (textFieldState_textview.text.toString() != data.textView) textFieldState_textview.edit { replace(0, length, data.textView) } }
    LaunchedEffect(textFieldState_textview.text) { val newValue = textFieldState_textview.text.toString(); if (newValue != data.textView) viewModel.updateData(mapOf("textView" to newValue)) }
    val resolved_textview6 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_textview,
        boxModifier = Modifier
            .testTag("textview")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        placeholder = { Text(stringResource(R.string.keyboard_avoidance_test_multiline_text_input_type_here)) },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.white),
        isOutlined = true,
        maxLines = Int.MAX_VALUE,
        singleLine = false,
        textStyle = TextStyle(fontFamily = resolved_textview6.family, fontWeight = resolved_textview6.weight, fontSize = (resolved_textview6.size ?: TextUnit.Unspecified), fontStyle = (resolved_textview6.style ?: FontStyle.Normal), color = colorResource(R.color.dark_gray))
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
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.medium_blue),
                            contentColor = colorResource(R.color.white)
                        )
    ) {
        val resolved_button29 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 18.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.keyboard_avoidance_test_submit),
            fontFamily = resolved_button29.family,
            fontWeight = resolved_button29.weight,
            fontSize = resolved_button29.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button29.style ?: FontStyle.Normal,
        )
    }
}
// >>> RESPONSIVE_HELPERS_END