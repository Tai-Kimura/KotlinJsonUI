package com.kotlinjsonui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kotlinjsonui.core.Configuration

/**
 * The decoration of a JsonUI TextField / TextView (CustomTextField): placeholder, padding, the filled container or
 * the outline, drawn around the field's own text.
 *
 * Why a wrapper: when Compose changes how a field's decoration is written, only this body changes — the field hands
 * over the values JsonUI knows (ticket kjui-slider-deprecated-in-material3-1-5: material3 1.5 deprecated the
 * value + VisualTransformation DecorationBox the field called; this is its state-based decorator).
 *
 * The text is shown as typed (no output transformation), as the VisualTransformation.None the old form took.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun JsonUITextFieldDecoration(
    state: TextFieldState,
    innerTextField: @Composable () -> Unit,
    outlined: Boolean,
    enabled: Boolean,
    singleLine: Boolean,
    interactionSource: MutableInteractionSource,
    isError: Boolean,
    placeholder: @Composable (() -> Unit)?,
    contentPadding: PaddingValues,
    shape: Shape,
    textColor: Color,
    isFocused: Boolean,
    focusedBackground: Color,
    unfocusedBackground: Color,
    borderColor: Color?,
    borderWidth: Dp?
) {
    val lineLimits = if (singleLine) TextFieldLineLimits.SingleLine else TextFieldLineLimits.MultiLine()
    if (outlined) {
        // A 0 stroke is a hairline in Compose, not no line.
        val outlineColor = if (borderWidth != null && borderWidth <= 0.dp) {
            Color.Transparent
        } else {
            borderColor ?: Configuration.TextField.defaultBorderColor
        }
        OutlinedTextFieldDefaults.decorator(
            state = state,
            enabled = enabled,
            lineLimits = lineLimits,
            outputTransformation = null,
            interactionSource = interactionSource,
            isError = isError,
            placeholder = placeholder,
            contentPadding = contentPadding,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = outlineColor,
                unfocusedBorderColor = outlineColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                focusedContainerColor = unfocusedBackground,
                unfocusedContainerColor = unfocusedBackground
            ),
            container = {
                OutlinedTextFieldDefaults.Container(
                    enabled = enabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = outlineColor,
                        unfocusedBorderColor = outlineColor,
                        focusedContainerColor = unfocusedBackground,
                        unfocusedContainerColor = unfocusedBackground
                    ),
                    shape = shape,
                    focusedBorderThickness = when {
                        borderWidth == null -> OutlinedTextFieldDefaults.FocusedBorderThickness
                        borderWidth <= 0.dp -> 0.dp
                        else -> maxOf(borderWidth, OutlinedTextFieldDefaults.FocusedBorderThickness)
                    },
                    unfocusedBorderThickness = borderWidth ?: OutlinedTextFieldDefaults.UnfocusedBorderThickness
                )
            }
        ).Decoration(innerTextField)
    } else {
        TextFieldDefaults.decorator(
            state = state,
            enabled = enabled,
            lineLimits = lineLimits,
            outputTransformation = null,
            interactionSource = interactionSource,
            isError = isError,
            placeholder = placeholder,
            contentPadding = contentPadding,
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
                    TextFieldDefaults.Container(
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
                        shape = shape
                    )
                }
            }
        ).Decoration(innerTextField)
    }
}
