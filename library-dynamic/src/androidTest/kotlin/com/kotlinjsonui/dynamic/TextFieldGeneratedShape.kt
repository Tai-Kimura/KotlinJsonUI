@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.kotlinjsonui.dynamic

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.core.Configuration

// What kjui_tools emits for a bound TextField and TextView, written by the
// generator (jsonui-cli kjui_tools, TextFieldComponent / TextViewComponent)
// and pasted here: this module cannot run the generator at build time.

@Composable
fun GeneratedTextFieldShape(viewModel: FieldVm, data: FieldData) {
    val textFieldState_tf = rememberTextFieldState(initialText = data.age)
    LaunchedEffect(data.age) { if (textFieldState_tf.text.toString() != data.age) textFieldState_tf.edit { replace(0, length, data.age) } }
    val textFieldState_tf_held = rememberUpdatedState(data.age)
    LaunchedEffect(textFieldState_tf.text) { val newValue = textFieldState_tf.text.toString(); if (newValue != data.age) { viewModel.updateData(mapOf("age" to newValue)); data.onAgeChange?.invoke(newValue); withFrameNanos { }; withFrameNanos { }; if (textFieldState_tf.text.toString() == newValue && textFieldState_tf_held.value != newValue) textFieldState_tf.edit { replace(0, length, textFieldState_tf_held.value) } } }
    val focusRequester_tf = remember { FocusRequester() }
    val keyboardController_tf = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.tfIsFocused) { if (data.tfIsFocused) { focusRequester_tf.requestFocus(); keyboardController_tf?.show() } }
    CustomTextField(
        state = textFieldState_tf,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .testTag("tf")
            .semantics { testTagsAsResourceId = true }
            .onFocusChanged { if (it.isFocused != data.tfIsFocused) viewModel.updateData(mapOf("tfIsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_tf),
        textStyle = TextStyle(color = Configuration.TextField.defaultTextColor, fontSize = Configuration.TextField.defaultFontSize.sp)
    )
}

@Composable
fun GeneratedTextViewShape(viewModel: FieldVm, data: FieldData) {
    val textFieldState_tv = rememberTextFieldState(initialText = data.age)
    LaunchedEffect(data.age) { if (textFieldState_tv.text.toString() != data.age) textFieldState_tv.edit { replace(0, length, data.age) } }
    val textFieldState_tv_held = rememberUpdatedState(data.age)
    LaunchedEffect(textFieldState_tv.text) { val newValue = textFieldState_tv.text.toString(); if (newValue != data.age) { viewModel.updateData(mapOf("age" to newValue)); data.onAgeChange?.invoke(newValue); withFrameNanos { }; withFrameNanos { }; if (textFieldState_tv.text.toString() == newValue && textFieldState_tv_held.value != newValue) textFieldState_tv.edit { replace(0, length, textFieldState_tv_held.value) } } }
    val focusRequester_tv = remember { FocusRequester() }
    val keyboardController_tv = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.tvIsFocused) { if (data.tvIsFocused) { focusRequester_tv.requestFocus(); keyboardController_tv?.show() } }
    CustomTextField(
        state = textFieldState_tv,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .testTag("tv")
            .semantics { testTagsAsResourceId = true }
            .onFocusChanged { if (it.isFocused != data.tvIsFocused) viewModel.updateData(mapOf("tvIsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_tv),
        isOutlined = true,
        maxLines = Int.MAX_VALUE,
        singleLine = false
    )
}
