package com.example.kotlinjsonui.sample.views.disabled_test

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
import androidx.compose.ui.semantics.disabled
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
import com.example.kotlinjsonui.sample.data.DisabledTestData
import com.example.kotlinjsonui.sample.viewmodels.DisabledTestViewModel
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
fun DisabledTestGeneratedView(
    data: DisabledTestData,
    viewModel: DisabledTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from disabled_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
        // Check if Dynamic Mode is active
        if (DynamicModeManager.isActive()) {
            // Dynamic Mode - use SafeDynamicView for real-time updates
            SafeDynamicView(
                layoutName = "disabled_test",
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
                    android.util.Log.e("DynamicView", "Error loading disabled_test: \$error")
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
            ) {
                Section0(data, viewModel)
                Section1(data, viewModel)
                Section2(data, viewModel)
                Section3(data, viewModel)
                Section4(data, viewModel)
                Section5(data, viewModel)
                Section6(data, viewModel)
                Section7(data, viewModel)
                Section8(data, viewModel)
                Section9(data, viewModel)
                Section10(data, viewModel)
                Section11(data, viewModel)
                Section12(data, viewModel)
                Section13(data, viewModel)
                Section14(data, viewModel)
                val resolved_text8 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = "${data.isEnabled}",
                    color = colorResource(R.color.medium_gray_4),
                    fontFamily = resolved_text8.family,
                    fontWeight = resolved_text8.weight,
                    fontSize = resolved_text8.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text8.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp),
                    textAlign = TextAlign.Center
                )
            }
            }
        }    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("disabled_test")
    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: DisabledTestData,
    viewModel: DisabledTestViewModel
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
    data: DisabledTestData,
    viewModel: DisabledTestViewModel
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
        modifier = Modifier
            .testTag("title_label")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 20.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section2(
    data: DisabledTestData,
    viewModel: DisabledTestViewModel
) {
    val resolved_text2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.disabled_test_enabled_button),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text2.family,
        fontWeight = resolved_text2.weight,
        fontSize = resolved_text2.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text2.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section3(
    data: DisabledTestData,
    viewModel: DisabledTestViewModel
) {
    Button(
        onClick = { data.onEnabledButtonTap?.invoke() },
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .requiredHeight(44.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.medium_blue),
                            disabledContainerColor = colorResource(R.color.medium_blue).copy(alpha = 0.5f),
                            contentColor = colorResource(R.color.white),
                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                        ),
        enabled = true
    ) {
        Text(stringResource(R.string.disabled_test_enabled_button))
    }
}

@Composable
private fun Section4(
    data: DisabledTestData,
    viewModel: DisabledTestViewModel
) {
    val resolved_text3 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.disabled_test_disabled_button),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text3.family,
        fontWeight = resolved_text3.weight,
        fontSize = resolved_text3.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text3.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section5(
    data: DisabledTestData,
    viewModel: DisabledTestViewModel
) {
    Button(
        onClick = { data.onDisabledButtonTap?.invoke() },
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .requiredHeight(44.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.medium_blue),
                            disabledContainerColor = colorResource(R.color.pale_gray_4),
                            contentColor = colorResource(R.color.white),
                            disabledContentColor = Color(android.graphics.Color.parseColor("#999999"))
                        ),
        enabled = false
    ) {
        Text(stringResource(R.string.disabled_test_disabled_button))
    }
}

@Composable
private fun Section6(
    data: DisabledTestData,
    viewModel: DisabledTestViewModel
) {
    val resolved_text4 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.disabled_test_touchdisabledstate_button),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text4.family,
        fontWeight = resolved_text4.weight,
        fontSize = resolved_text4.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text4.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section7(
    data: DisabledTestData,
    viewModel: DisabledTestViewModel
) {
    Button(
        onClick = { data.onTouchDisabledTap?.invoke() },
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .requiredHeight(44.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.medium_red_3),
                            disabledContainerColor = colorResource(R.color.medium_red_3).copy(alpha = 0.5f),
                            contentColor = colorResource(R.color.white),
                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                        )
    ) {
        Text(stringResource(R.string.disabled_test_touch_disabled))
    }
}

@Composable
private fun Section8(
    data: DisabledTestData,
    viewModel: DisabledTestViewModel
) {
    val resolved_text5 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.disabled_test_enabled_textfield),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text5.family,
        fontWeight = resolved_text5.weight,
        fontSize = resolved_text5.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text5.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section9(
    data: DisabledTestData,
    viewModel: DisabledTestViewModel
) {
    val textFieldState_disabled_test_field1 = rememberTextFieldState(initialText = data.textFieldValue)
    LaunchedEffect(data.textFieldValue) { if (textFieldState_disabled_test_field1.text.toString() != data.textFieldValue) textFieldState_disabled_test_field1.edit { replace(0, length, data.textFieldValue) } }
    LaunchedEffect(textFieldState_disabled_test_field1.text) { val newValue = textFieldState_disabled_test_field1.text.toString(); if (newValue != data.textFieldValue) viewModel.updateData(mapOf("textFieldValue" to newValue)) }
    val focusRequester_disabled_test_field1 = remember { FocusRequester() }
    val keyboardController_disabled_test_field1 = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.disabledTestField1IsFocused) { if (data.disabledTestField1IsFocused) { focusRequester_disabled_test_field1.requestFocus(); keyboardController_disabled_test_field1?.show() } }
    CustomTextFieldWithMargins(
        state = textFieldState_disabled_test_field1,
        boxModifier = Modifier
            .testTag("disabled_test_field1")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .requiredHeight(44.dp)
            .onFocusChanged { if (it.isFocused != data.disabledTestField1IsFocused) viewModel.updateData(mapOf("disabledTestField1IsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_disabled_test_field1),
        placeholder = { Text(
                            text = stringResource(R.string.disabled_test_enabled_can_type_here),
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray_4),
        isOutlined = true,
        textStyle = TextStyle(color = Configuration.TextField.defaultTextColor, fontSize = Configuration.TextField.defaultFontSize.sp),
        enabled = true
    )
}

@Composable
private fun Section10(
    data: DisabledTestData,
    viewModel: DisabledTestViewModel
) {
    val resolved_text6 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.disabled_test_disabled_textfield),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text6.family,
        fontWeight = resolved_text6.weight,
        fontSize = resolved_text6.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text6.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section11(
    data: DisabledTestData,
    viewModel: DisabledTestViewModel
) {
    val textFieldState_disabled_test_field2 = rememberTextFieldState(initialText = stringResource(R.string.disabled_test_disabled_text_field))
    val focusRequester_disabled_test_field2 = remember { FocusRequester() }
    val keyboardController_disabled_test_field2 = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.disabledTestField2IsFocused) { if (data.disabledTestField2IsFocused) { focusRequester_disabled_test_field2.requestFocus(); keyboardController_disabled_test_field2?.show() } }
    CustomTextFieldWithMargins(
        state = textFieldState_disabled_test_field2,
        boxModifier = Modifier
            .testTag("disabled_test_field2")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .semantics { disabled() },
        textFieldModifier = Modifier
            .fillMaxWidth()
            .requiredHeight(44.dp)
            .onFocusChanged { if (it.isFocused != data.disabledTestField2IsFocused) viewModel.updateData(mapOf("disabledTestField2IsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_disabled_test_field2),
        placeholder = { Text(
                            text = stringResource(R.string.disabled_test_disabled_cannot_type),
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray_4),
        isOutlined = true,
        textStyle = TextStyle(color = colorResource(R.color.medium_gray_4), fontSize = Configuration.TextField.defaultFontSize.sp),
        enabled = false
    )
}

@Composable
private fun Section12(
    data: DisabledTestData,
    viewModel: DisabledTestViewModel
) {
    val resolved_text7 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.disabled_test_dynamic_enabledisable_test),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text7.family,
        fontWeight = resolved_text7.weight,
        fontSize = resolved_text7.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text7.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier.padding(top = 30.dp)
    )
}

@Composable
private fun Section13(
    data: DisabledTestData,
    viewModel: DisabledTestViewModel
) {
    Button(
        onClick = { data.toggleEnableState?.invoke() },
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .requiredHeight(44.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.medium_green),
                            disabledContainerColor = colorResource(R.color.medium_green).copy(alpha = 0.5f),
                            contentColor = colorResource(R.color.white),
                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                        )
    ) {
        Text(stringResource(R.string.disabled_test_toggle_enable_state))
    }
}

@Composable
private fun Section14(
    data: DisabledTestData,
    viewModel: DisabledTestViewModel
) {
    Button(
        onClick = { data.onDynamicButtonTap?.invoke() },
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .requiredHeight(44.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.medium_blue_3),
                            disabledContainerColor = colorResource(R.color.pale_gray_3),
                            contentColor = colorResource(R.color.white),
                            disabledContentColor = Color(android.graphics.Color.parseColor("#888888"))
                        ),
        enabled = data.isEnabled
    ) {
        Text(stringResource(R.string.disabled_test_dynamic_button))
    }
}
// >>> RESPONSIVE_HELPERS_END