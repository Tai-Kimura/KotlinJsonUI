package com.kotlinjsonui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.kotlinjsonui.core.Configuration

/**
 * Custom TextField component that handles default colors from Configuration
 */
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    textStyle: TextStyle = LocalTextStyle.current,
    shape: RoundedCornerShape? = null,
    backgroundColor: Color? = null,
    highlightBackgroundColor: Color? = null,
    borderColor: Color? = null,
    isOutlined: Boolean = false,
    isSecure: Boolean = false
) {
    // Determine background colors
    val unfocusedBackground = backgroundColor ?: Configuration.TextField.defaultBackgroundColor
    val focusedBackground = highlightBackgroundColor ?: backgroundColor ?: Configuration.TextField.defaultHighlightBackgroundColor
    
    if (isOutlined || isSecure) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            placeholder = placeholder,
            isError = isError,
            visualTransformation = visualTransformation,
            textStyle = textStyle,
            shape = shape ?: RoundedCornerShape(Configuration.TextField.defaultCornerRadius.dp),
            colors = if (borderColor != null) {
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = borderColor,
                    unfocusedBorderColor = borderColor
                )
            } else {
                OutlinedTextFieldDefaults.colors()
            }
        )
    } else {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            placeholder = placeholder,
            isError = isError,
            visualTransformation = visualTransformation,
            textStyle = textStyle,
            shape = shape ?: RoundedCornerShape(Configuration.TextField.defaultCornerRadius.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = focusedBackground,
                unfocusedContainerColor = unfocusedBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
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
    textStyle: TextStyle = LocalTextStyle.current,
    shape: RoundedCornerShape? = null,
    backgroundColor: Color? = null,
    highlightBackgroundColor: Color? = null,
    borderColor: Color? = null,
    isOutlined: Boolean = false,
    isSecure: Boolean = false
) {
    Box(modifier = boxModifier) {
        CustomTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = textFieldModifier,
            placeholder = placeholder,
            isError = isError,
            visualTransformation = visualTransformation,
            textStyle = textStyle,
            shape = shape,
            backgroundColor = backgroundColor,
            highlightBackgroundColor = highlightBackgroundColor,
            borderColor = borderColor,
            isOutlined = isOutlined,
            isSecure = isSecure
        )
    }
}