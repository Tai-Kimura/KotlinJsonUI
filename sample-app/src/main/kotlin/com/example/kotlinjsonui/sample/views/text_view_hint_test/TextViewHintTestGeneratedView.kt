package com.example.kotlinjsonui.sample.views.text_view_hint_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import com.example.kotlinjsonui.sample.data.TextViewHintTestData
import com.example.kotlinjsonui.sample.viewmodels.TextViewHintTestViewModel
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
fun TextViewHintTestGeneratedView(
    data: TextViewHintTestData,
    viewModel: TextViewHintTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from text_view_hint_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
        // Check if Dynamic Mode is active
        if (DynamicModeManager.isActive()) {
            // Dynamic Mode - use SafeDynamicView for real-time updates
            SafeDynamicView(
                layoutName = "text_view_hint_test",
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
                    android.util.Log.e("DynamicView", "Error loading text_view_hint_test: \$error")
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
                .fillMaxWidth()
                .fillMaxHeight()
                .background(colorResource(R.color.white))
                .padding(20.dp)
        ) {
            Section0(data, viewModel)
            Section1(data, viewModel)
            Section2(data, viewModel)
            Section3(data, viewModel)
        }    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("text_view_hint_test")
    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: TextViewHintTestData,
    viewModel: TextViewHintTestViewModel
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
    data: TextViewHintTestData,
    viewModel: TextViewHintTestViewModel
) {
    val resolved_text1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_view_hint_test_textview_hint_test),
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
    data: TextViewHintTestData,
    viewModel: TextViewHintTestViewModel
) {
    val resolved_text2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_view_hint_test_simple_textview_with_hint),
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
    data: TextViewHintTestData,
    viewModel: TextViewHintTestViewModel
) {
    val textFieldState_simpleTextView = rememberTextFieldState(initialText = data.simpleText)
    LaunchedEffect(data.simpleText) { if (textFieldState_simpleTextView.text.toString() != data.simpleText) textFieldState_simpleTextView.edit { replace(0, length, data.simpleText) } }
    LaunchedEffect(textFieldState_simpleTextView.text) { val newValue = textFieldState_simpleTextView.text.toString(); if (newValue != data.simpleText) viewModel.updateData(mapOf("simpleText" to newValue)) }
    val resolved_textview1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    val focusRequester_simpleTextView = remember { FocusRequester() }
    val keyboardController_simpleTextView = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.simpleTextViewIsFocused) { if (data.simpleTextViewIsFocused) { focusRequester_simpleTextView.requestFocus(); keyboardController_simpleTextView?.show() } }
    CustomTextFieldWithMargins(
        state = textFieldState_simpleTextView,
        boxModifier = Modifier
            .testTag("simpleTextView")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 20.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .onFocusChanged { if (it.isFocused != data.simpleTextViewIsFocused) viewModel.updateData(mapOf("simpleTextViewIsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_simpleTextView),
        placeholder = {
            Text(
                text = stringResource(R.string.text_view_hint_test_this_is_a_simple_hint),
                color = colorResource(R.color.dark_red)
            )
        },
        contentPadding = PaddingValues(12.dp),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.white),
        isOutlined = true,
        maxLines = Int.MAX_VALUE,
        singleLine = false,
        textStyle = TextStyle(fontFamily = (resolved_textview1.family ?: LocalTextStyle.current.fontFamily), fontWeight = (resolved_textview1.weight ?: LocalTextStyle.current.fontWeight), fontSize = (resolved_textview1.size ?: LocalTextStyle.current.fontSize), fontStyle = (resolved_textview1.style ?: LocalTextStyle.current.fontStyle), color = colorResource(R.color.black))
    )
    val resolved_text3 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.text_view_hint_test_flexible_textview_with_multilin),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text3.family,
        fontWeight = resolved_text3.weight,
        fontSize = resolved_text3.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text3.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 8.dp)
    )
    val textFieldState_flexibleTextView = rememberTextFieldState(initialText = data.flexibleText)
    LaunchedEffect(data.flexibleText) { if (textFieldState_flexibleTextView.text.toString() != data.flexibleText) textFieldState_flexibleTextView.edit { replace(0, length, data.flexibleText) } }
    LaunchedEffect(textFieldState_flexibleTextView.text) { val newValue = textFieldState_flexibleTextView.text.toString(); if (newValue != data.flexibleText) viewModel.updateData(mapOf("flexibleText" to newValue)) }
    val resolved_textview2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    val focusRequester_flexibleTextView = remember { FocusRequester() }
    val keyboardController_flexibleTextView = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.flexibleTextViewIsFocused) { if (data.flexibleTextViewIsFocused) { focusRequester_flexibleTextView.requestFocus(); keyboardController_flexibleTextView?.show() } }
    CustomTextFieldWithMargins(
        state = textFieldState_flexibleTextView,
        boxModifier = Modifier
            .testTag("flexibleTextView")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 20.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp, max = 200.dp)
            .onFocusChanged { if (it.isFocused != data.flexibleTextViewIsFocused) viewModel.updateData(mapOf("flexibleTextViewIsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_flexibleTextView),
        placeholder = {
            Text(
                text = stringResource(R.string.text_view_hint_test_multiline_hint_line_2_of_hint_l),
                color = colorResource(R.color.dark_blue)
            )
        },
        contentPadding = PaddingValues(12.dp),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.white),
        isOutlined = true,
        maxLines = Int.MAX_VALUE,
        singleLine = false,
        textStyle = TextStyle(fontFamily = (resolved_textview2.family ?: LocalTextStyle.current.fontFamily), fontWeight = (resolved_textview2.weight ?: LocalTextStyle.current.fontWeight), fontSize = (resolved_textview2.size ?: LocalTextStyle.current.fontSize), fontStyle = (resolved_textview2.style ?: LocalTextStyle.current.fontStyle), color = colorResource(R.color.black))
    )
}
// >>> RESPONSIVE_HELPERS_END