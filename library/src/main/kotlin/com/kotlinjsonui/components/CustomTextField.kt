package com.kotlinjsonui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
 * Custom TextField component that handles default colors from Configuration
 * and supports custom content padding using BasicTextField with DecorationBox
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
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
    onEndEditing: (() -> Unit)? = null
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

    // Use BasicTextField with DecorationBox for custom content padding support
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = focusModifier,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        textStyle = textStyle.copy(color = textStyle.color.takeIf { it != Color.Unspecified } ?: Color.Black),
        singleLine = singleLine,
        maxLines = maxLines,
        cursorBrush = SolidColor(textStyle.color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.primary),
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            if (isOutlined) {
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value,
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = singleLine,
                    visualTransformation = visualTransformation,
                    interactionSource = interactionSource,
                    isError = isError,
                    placeholder = placeholder,
                    contentPadding = effectiveContentPadding,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = borderColor ?: MaterialTheme.colorScheme.outline,
                        unfocusedBorderColor = borderColor ?: MaterialTheme.colorScheme.outline,
                        focusedTextColor = textStyle.color,
                        unfocusedTextColor = textStyle.color,
                        focusedContainerColor = backgroundColor ?: Color.Transparent,
                        unfocusedContainerColor = backgroundColor ?: Color.Transparent
                    ),
                    container = {
                        OutlinedTextFieldDefaults.ContainerBox(
                            enabled = true,
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
                    value = value,
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = singleLine,
                    visualTransformation = visualTransformation,
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
                        TextFieldDefaults.ContainerBox(
                            enabled = true,
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
                )
            }
        }
    )
}

/**
 * Custom TextField with Box wrapper for margins
 */
@Composable
fun CustomTextFieldWithMargins(
    value: String,
    onValueChange: (String) -> Unit,
    boxModifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
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
    onEndEditing: (() -> Unit)? = null
) {
    Box(modifier = boxModifier) {
        CustomTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = textFieldModifier,
            placeholder = placeholder,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
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
            onEndEditing = onEndEditing
        )
    }
}
