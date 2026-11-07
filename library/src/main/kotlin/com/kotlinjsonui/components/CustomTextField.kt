package com.kotlinjsonui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.jsonUITintOrNull

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
    enabled: Boolean = true,
    // `tintColor` in JsonUI vocabulary: the caret (cursor) accent. Falls
    // back to the tint a container handed down (LocalJsonUITint), then to
    // the text colour / theme primary, which was the previous hard-wired
    // behaviour.
    cursorColor: Color? = null,
    // The outline's width. Null is Material's: 1dp, 2dp under focus. A width
    // thinner than Material's focus width keeps that focus cue; 0 is no line.
    borderWidth: Dp? = null
) {
    // Determine background colors
    val unfocusedBackground = backgroundColor ?: Configuration.TextField.defaultBackgroundColor
    val focusedBackground = highlightBackgroundColor ?: backgroundColor ?: Configuration.TextField.defaultHighlightBackgroundColor

    // Track focus state
    var isFocused by remember { mutableStateOf(false) }
    var hasStartedEditing by remember { mutableStateOf(false) }

    // Interaction source for decoration box
    val interactionSource = remember { MutableInteractionSource() }

    // Create focus modifier. `keyboardAvoidanceField()` makes this field —
    // after the caller's size and margins, so its own box — what a
    // `keyboardAvoidance` list keeps above the IME. Without it the list keeps
    // the caret clear and lets the rest of the field run past the edge
    // (KeyboardAvoidance.kt).
    val focusModifier = modifier.keyboardAvoidanceField().onFocusChanged { focusState ->
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

    // Shared decoration logic extracted to a composable
    @Composable
    fun DecorationContent(innerTextField: @Composable () -> Unit) {
        if (isOutlined) {
            // A 0 stroke is a hairline in Compose, not no line.
            val outlineColor = if (borderWidth != null && borderWidth <= 0.dp) {
                Color.Transparent
            } else {
                borderColor ?: Configuration.TextField.defaultBorderColor
            }
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
                    focusedBorderColor = outlineColor,
                    unfocusedBorderColor = outlineColor,
                    focusedTextColor = effectiveTextStyle.color,
                    unfocusedTextColor = effectiveTextStyle.color,
                    focusedContainerColor = backgroundColor ?: Configuration.TextField.defaultBackgroundColor,
                    unfocusedContainerColor = backgroundColor ?: Configuration.TextField.defaultBackgroundColor
                ),
                container = {
                    OutlinedTextFieldDefaults.Container(
                        enabled = enabled,
                        isError = isError,
                        interactionSource = interactionSource,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = outlineColor,
                            unfocusedBorderColor = outlineColor,
                            focusedContainerColor = backgroundColor ?: Configuration.TextField.defaultBackgroundColor,
                            unfocusedContainerColor = backgroundColor ?: Configuration.TextField.defaultBackgroundColor
                        ),
                        shape = effectiveShape,
                        focusedBorderThickness = when {
                            borderWidth == null -> OutlinedTextFieldDefaults.FocusedBorderThickness
                            borderWidth <= 0.dp -> 0.dp
                            else -> maxOf(borderWidth, OutlinedTextFieldDefaults.FocusedBorderThickness)
                        },
                        unfocusedBorderThickness = borderWidth ?: OutlinedTextFieldDefaults.UnfocusedBorderThickness
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
                            shape = effectiveShape
                        )
                    }
                }
            )
        }
    }

    // The caret: the field's own tintColor, else the tint a container
    // handed down (LocalJsonUITint), else the text colour / theme primary.
    val caretColor = cursorColor ?: jsonUITintOrNull()
        ?: textStyle.color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.primary

    if (isSecure) {
        BasicSecureTextField(
            state = state,
            modifier = focusModifier,
            enabled = enabled,
            textStyle = effectiveTextStyle,
            keyboardOptions = submitOptions(keyboardOptions, keyboardActions, singleLine = true),
            onKeyboardAction = keyboardActionHandler(keyboardActions, keyboardOptions.imeAction, singleLine = true),
            cursorBrush = SolidColor(caretColor),
            interactionSource = interactionSource,
            decorator = { innerTextField -> DecorationContent(innerTextField) }
        )
    } else {
        BasicTextField(
            state = state,
            modifier = focusModifier,
            enabled = enabled,
            textStyle = effectiveTextStyle,
            keyboardOptions = submitOptions(keyboardOptions, keyboardActions, singleLine),
            onKeyboardAction = keyboardActionHandler(keyboardActions, keyboardOptions.imeAction, singleLine),
            lineLimits = if (singleLine) {
                androidx.compose.foundation.text.input.TextFieldLineLimits.SingleLine
            } else {
                androidx.compose.foundation.text.input.TextFieldLineLimits.MultiLine(maxHeightInLines = maxLines)
            },
            cursorBrush = SolidColor(caretColor),
            interactionSource = interactionSource,
            decorator = { innerTextField -> DecorationContent(innerTextField) }
        )
    }
}

/**
 * The keyboard action the field's IME (or a hardware Enter on a single-line
 * field) performs, as the state-based fields take it: `onKeyboardAction`. The
 * fields took `keyboardActions` and passed it on to nothing — kjui codegen's
 * onSubmit (KeyboardActions onDone / onGo / onSearch / onSend) and Dynamic's
 * (onSubmit, and the move to the next field) were never called (jsonui-cli
 * ticket kjui-textfield-keyboard-actions-never-reach-the-field; measured on a
 * device: 0 calls in every form, `returnKeyType: Done` included).
 *
 * The action is the one [imeAction] names; `Default` (no returnKeyType) on a
 * single-line field is Done, as the platform shows it. Null when the
 * declared actions have nothing for it, so the field keeps its default.
 */
internal fun keyboardActionHandler(
    actions: KeyboardActions,
    imeAction: ImeAction,
    singleLine: Boolean
): KeyboardActionHandler? {
    val run: (KeyboardActionScope.() -> Unit)? = when (imeAction) {
        ImeAction.Done -> actions.onDone
        ImeAction.Go -> actions.onGo
        ImeAction.Next -> actions.onNext
        ImeAction.Previous -> actions.onPrevious
        ImeAction.Search -> actions.onSearch
        ImeAction.Send -> actions.onSend
        else -> if (singleLine) actions.onDone else null
    }
    if (run == null) return null
    return KeyboardActionHandler { performDefaultAction ->
        val scope = object : KeyboardActionScope {
            override fun defaultKeyboardAction(imeAction: ImeAction) = performDefaultAction()
        }
        scope.run()
    }
}

/**
 * A single-line field with a Done action and no declared returnKeyType gets
 * the Done action key: the field's Enter is then the action (a hardware Enter
 * on a field whose action is Default or Unspecified performs none — measured
 * on a device: 0 calls for Enter, 1 with Done), as the return key submits on
 * iOS. `KeyboardOptions.Default` — what kjui codegen passes — says
 * Unspecified, Dynamic's options Default. Any declared action is kept.
 */
internal fun submitOptions(options: KeyboardOptions, actions: KeyboardActions, singleLine: Boolean): KeyboardOptions =
    if (singleLine && (options.imeAction == ImeAction.Default || options.imeAction == ImeAction.Unspecified) &&
        actions.onDone != null
    ) {
        options.copy(imeAction = ImeAction.Done)
    } else {
        options
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
    enabled: Boolean = true,
    cursorColor: Color? = null,
    borderWidth: Dp? = null
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
            enabled = enabled,
            cursorColor = cursorColor,
            borderWidth = borderWidth
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
    val held = rememberUpdatedState(externalValue)
    LaunchedEffect(state.text) {
        val current = state.text.toString()
        if (current != externalValue) {
            onValueChange(current)
            // A value taken back to what it was changes nothing the key above
            // can see ("12" -> "12a" -> "12"): read it again once composition
            // has caught up. Read at once it is still the old value and every
            // key is taken back; one frame was enough where measured, the second
            // is margin. A later edit cancels this; an edit that was taken leaves
            // the cursor and the IME alone.
            withFrameNanos { }
            withFrameNanos { }
            val value = held.value
            if (state.text.toString() == current && value != current) {
                state.edit { replace(0, length, value) }
            }
        }
    }

    return state
}
