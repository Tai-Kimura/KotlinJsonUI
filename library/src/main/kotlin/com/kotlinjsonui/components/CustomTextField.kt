package com.kotlinjsonui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.kotlinjsonui.core.Configuration

/**
 * Custom TextField component using TextFieldState API (Compose BOM 2026.03.00+).
 * Supports custom content padding, decoration, and Configuration defaults.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    textStyle: TextStyle = LocalTextStyle.current,
    shape: RoundedCornerShape? = null,
    contentPadding: PaddingValues? = null,
    backgroundColor: Color? = null,
    highlightBackgroundColor: Color? = null,
    borderColor: Color? = null,
    isOutlined: Boolean = false,
    isSecure: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    onFocus: (() -> Unit)? = null,
    onBlur: (() -> Unit)? = null,
    onBeginEditing: (() -> Unit)? = null,
    onEndEditing: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    // Determine background colors
    val unfocusedBackground = backgroundColor ?: Configuration.TextField.defaultBackgroundColor
    val focusedBackground = highlightBackgroundColor ?: backgroundColor ?: Configuration.TextField.defaultHighlightBackgroundColor

    // Track focus state
    var isFocused by remember { mutableStateOf(false) }
    var hasStartedEditing by remember { mutableStateOf(false) }

    // Interaction source for decoration box
    val interactionSource = remember { MutableInteractionSource() }

    // Create focus modifier
    val focusModifier = modifier.onFocusChanged { focusState ->
        if (focusState.isFocused && !isFocused) {
            onFocus?.invoke()
            if (!hasStartedEditing) {
                onBeginEditing?.invoke()
                hasStartedEditing = true
            }
        } else if (!focusState.isFocused && isFocused) {
            onBlur?.invoke()
            if (hasStartedEditing) {
                onEndEditing?.invoke()
                hasStartedEditing = false
            }
        }
        isFocused = focusState.isFocused
    }

    // Default content padding if not specified
    val effectiveContentPadding = contentPadding ?: TextFieldDefaults.contentPaddingWithoutLabel()
    val effectiveShape = shape ?: RoundedCornerShape(Configuration.TextField.defaultCornerRadius.dp)
    val effectiveTextStyle = textStyle.copy(color = textStyle.color.takeIf { it != Color.Unspecified } ?: Color.Black)

    // Shared decorator for both normal and secure text fields
    val textFieldDecorator: @Composable (innerTextField: @Composable () -> Unit) -> Unit = { innerTextField ->
            if (isOutlined) {
                OutlinedTextFieldDefaults.DecorationBox(
                    value = state.text.toString(),
                    innerTextField = innerTextField,
                    enabled = enabled,
                    singleLine = singleLine,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    isError = isError,
                    placeholder = placeholder,
                    contentPadding = effectiveContentPadding,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = borderColor ?: MaterialTheme.colorScheme.outline,
                        unfocusedBorderColor = borderColor ?: MaterialTheme.colorScheme.outline,
                        focusedTextColor = effectiveTextStyle.color,
                        unfocusedTextColor = effectiveTextStyle.color,
                        focusedContainerColor = backgroundColor ?: Color.Transparent,
                        unfocusedContainerColor = backgroundColor ?: Color.Transparent
                    ),
                    container = {
                        OutlinedTextFieldDefaults.ContainerBox(
                            enabled = enabled,
                            isError = isError,
                            interactionSource = interactionSource,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = borderColor ?: MaterialTheme.colorScheme.outline,
                                unfocusedBorderColor = borderColor ?: MaterialTheme.colorScheme.outline,
                                focusedContainerColor = backgroundColor ?: Color.Transparent,
                                unfocusedContainerColor = backgroundColor ?: Color.Transparent
                            ),
                            shape = effectiveShape
                        )
                    }
                )
            } else {
                TextFieldDefaults.DecorationBox(
                    value = state.text.toString(),
                    innerTextField = innerTextField,
                    enabled = enabled,
                    singleLine = singleLine,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    isError = isError,
                    placeholder = placeholder,
                    contentPadding = effectiveContentPadding,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = focusedBackground,
                        unfocusedContainerColor = unfocusedBackground,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    container = {
                        val bg = if (isFocused) focusedBackground else unfocusedBackground
                        if (bg != Color.Transparent) {
                            TextFieldDefaults.ContainerBox(
                                enabled = enabled,
                                isError = isError,
                                interactionSource = interactionSource,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = if (isFocused) focusedBackground else unfocusedBackground,
                                    unfocusedContainerColor = unfocusedBackground,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                ),
                                shape = effectiveShape
                            )
                        }
                    }
                )
            }
    }

    // Use BasicSecureTextField for password fields, BasicTextField for normal
    if (isSecure) {
        BasicSecureTextField(
            state = state,
            modifier = focusModifier,
            enabled = enabled,
            textStyle = effectiveTextStyle,
            keyboardOptions = keyboardOptions,
            cursorBrush = SolidColor(textStyle.color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.primary),
            interactionSource = interactionSource,
            decorator = textFieldDecorator
        )
    } else {
        BasicTextField(
            state = state,
            modifier = focusModifier,
            enabled = enabled,
            textStyle = effectiveTextStyle,
            keyboardOptions = keyboardOptions,
            lineLimits = if (singleLine) {
                androidx.compose.foundation.text.input.TextFieldLineLimits.SingleLine
            } else {
                androidx.compose.foundation.text.input.TextFieldLineLimits.MultiLine(maxHeightInLines = maxLines)
            },
            cursorBrush = SolidColor(textStyle.color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.primary),
            interactionSource = interactionSource,
            decorator = textFieldDecorator
        )
    }
}

/**
 * Custom TextField with Box wrapper for margins
 */
@Composable
fun CustomTextFieldWithMargins(
    state: TextFieldState,
    boxModifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    textStyle: TextStyle = LocalTextStyle.current,
    shape: RoundedCornerShape? = null,
    contentPadding: PaddingValues? = null,
    backgroundColor: Color? = null,
    highlightBackgroundColor: Color? = null,
    borderColor: Color? = null,
    isOutlined: Boolean = false,
    isSecure: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    onFocus: (() -> Unit)? = null,
    onBlur: (() -> Unit)? = null,
    onBeginEditing: (() -> Unit)? = null,
    onEndEditing: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    Box(modifier = boxModifier) {
        CustomTextField(
            state = state,
            modifier = textFieldModifier,
            placeholder = placeholder,
            isError = isError,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            textStyle = textStyle,
            shape = shape,
            contentPadding = contentPadding,
            backgroundColor = backgroundColor,
            highlightBackgroundColor = highlightBackgroundColor,
            borderColor = borderColor,
            isOutlined = isOutlined,
            isSecure = isSecure,
            singleLine = singleLine,
            maxLines = maxLines,
            onFocus = onFocus,
            onBlur = onBlur,
            onBeginEditing = onBeginEditing,
            onEndEditing = onEndEditing,
            enabled = enabled
        )
    }
}

/**
 * Helper: Create a TextFieldState that syncs bidirectionally with an external value.
 * Use this when bridging TextFieldState with value/onValueChange patterns (e.g., ViewModel data binding).
 */
@Composable
fun rememberSyncedTextFieldState(
    externalValue: String,
    onValueChange: (String) -> Unit
): TextFieldState {
    val state = rememberTextFieldState(initialText = externalValue)

    // Sync external → state (e.g. ViewModel clears text)
    LaunchedEffect(externalValue) {
        if (state.text.toString() != externalValue) {
            state.edit { replace(0, length, externalValue) }
        }
    }

    // Sync state → external (user typing)
    LaunchedEffect(state.text) {
        val current = state.text.toString()
        if (current != externalValue) {
            onValueChange(current)
        }
    }

    return state
}
