package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.FocusManager
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.TextFieldAttributes
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.rememberTypedAttrs
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver

/**
 * TextField component → CustomTextField / CustomTextFieldWithMargins.
 * Reference: textfield_component.rb in kjui_tools.
 *
 * Features:
 * - Data binding for text value (@{property})
 * - Secure/password input
 * - Focus chain (fieldId → nextFocusId)
 * - Keyboard type, IME action, auto-capitalization
 * - Content padding, border style, outlined mode
 * - Hidden TextField (fontColor: "transparent" for 2FA auto-fill)
 */
class DynamicTextFieldComponent {
    companion object {
        @Composable
        fun create(json: JsonObject, data: Map<String, Any> = emptyMap()) {
            val context = LocalContext.current
            // EditText / Input type spellings are canonical aliases of
            // TextField at runtime (see DynamicView), so this component
            // always parses with TextFieldAttributes.
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                TextFieldAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "TextField", json,
                declared = TextFieldAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // Parse text value with data binding
            val rawText = TypedAttrs.rawString(a.text) ?: ""
            val initialText = ResourceResolver.resolveTextValue(rawText, data, context)

            // Parse placeholder with resource resolution ('placeholder' is a
            // standalone declared row alongside 'hint')
            val rawPlaceholder = a.hint ?: a.placeholder ?: ""
            val placeholderText = ResourceResolver.resolveTextValue(rawPlaceholder, data, context)

            // Detect hidden TextField (fontColor: "transparent")
            val isHidden = TypedAttrs.static(a.fontColor)?.lowercase() == "transparent"

            // Parse secure field
            val isSecure = TypedAttrs.static(a.secure) == true ||
                    TypedAttrs.enumString(a.input) { it.json }?.lowercase() == "password" ||
                    TypedAttrs.static(a.contentType)?.lowercase()?.let {
                        it == "password" || it == "newpassword"
                    } ?: false

            // Parse enabled state
            val isEnabled = TypedAttrs.boolean(a.common.enabled, data) ?: true

            // Parse max lines ('maxLines' is an undeclared legacy runtime extra)
            val maxLines = TypedAttrs.undeclared(json, "maxLines")?.asInt ?: 1
            val singleLine = maxLines == 1

            // TextFieldState with data binding sync
            val viewId = a.common.id ?: "textfield"
            val textFieldState = rememberTextFieldState(initialText = initialText)

            // Sync external → state (e.g. ViewModel clears text)
            LaunchedEffect(initialText) {
                if (textFieldState.text.toString() != initialText) {
                    textFieldState.edit { replace(0, length, initialText) }
                }
            }

            // Sync state → external (user typing)
            LaunchedEffect(textFieldState.text) {
                val newValue = textFieldState.text.toString()
                if (newValue != initialText) {
                    // Update data binding
                    if (rawText.contains("@{")) {
                        val variable = extractBindingVariable(rawText)
                        variable?.let { varName ->
                            val updateData = data["updateData"]
                            if (updateData is Function<*>) {
                                try {
                                    @Suppress("UNCHECKED_CAST")
                                    (updateData as (Map<String, Any>) -> Unit)(mapOf(varName to newValue))
                                } catch (_: Exception) {}
                            }
                        }
                    }
                    // Call onTextChange handler
                    val onTextChangeHandler = a.onTextChange
                    if (onTextChangeHandler != null) {
                        ModifierBuilder.resolveEventHandler(onTextChangeHandler, data, viewId, newValue)
                    }
                }
            }

            // Focus management ('fieldId'/'nextFocusId' are undeclared
            // legacy runtime extras; 'nextFocus' is the declared spelling)
            val fieldId = TypedAttrs.undeclared(json, "fieldId")?.asString
            val nextFocusId = TypedAttrs.undeclared(json, "nextFocusId")?.asString
            val onSubmitHandler = a.onSubmit

            val focusRequester = remember { FocusRequester() }
            val composeFocusManager = LocalFocusManager.current
            var hasFocus by remember { mutableStateOf(false) }

            if (fieldId != null) {
                LaunchedEffect(fieldId) {
                    FocusManager.focusRequestFlow.collect { requestedId ->
                        if (requestedId == fieldId) {
                            focusRequester.requestFocus()
                        } else if (hasFocus && requestedId.isEmpty()) {
                            composeFocusManager.clearFocus()
                        }
                    }
                }
            }

            // Colors (supports @{binding})
            val textColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.fontColor), data, context
            ) ?: Configuration.TextField.defaultTextColor
            val placeholderColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.hintColor), data, context
            ) ?: Configuration.TextField.defaultPlaceholderColor
            val backgroundColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.common.background), data, context
            ) ?: Configuration.TextField.defaultBackgroundColor
            val highlightBackgroundColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.common.highlightBackground), data, context
            ) ?: Configuration.TextField.defaultHighlightBackgroundColor
            val borderColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.common.borderColor), data, context
            ) ?: Configuration.TextField.defaultBorderColor

            // Font size
            val fontSize = TypedAttrs.int(a.fontSize, data) ?: Configuration.TextField.defaultFontSize
            val hintFontSize = a.hintFontSize?.toInt()

            // Shape
            val cornerRadius = TypedAttrs.float(a.common.cornerRadius, data)
                ?: Configuration.TextField.defaultCornerRadius.toFloat()
            val shape = RoundedCornerShape(cornerRadius.dp)

            // Outlined mode
            val isOutlined = resolveIsOutlined(json, a)

            // Content padding
            val contentPadding = buildContentPadding(json, a)

            // Text style
            val textStyle = buildTextStyle(a, textColor, fontSize)

            // Keyboard options
            val keyboardOptions = buildKeyboardOptions(a, nextFocusId)

            // Keyboard actions
            val keyboardActions = buildKeyboardActions(data, nextFocusId, onSubmitHandler, viewId)

            // Placeholder composable
            val placeholder: @Composable (() -> Unit)? = if (placeholderText.isNotEmpty()) {
                {
                    Text(
                        text = placeholderText,
                        color = placeholderColor,
                        fontSize = (hintFontSize ?: fontSize).sp,
                        fontWeight = if (a.hintFont == "bold") {
                            androidx.compose.ui.text.font.FontWeight.Bold
                        } else null
                    )
                }
            } else null

            // Focus event handlers
            val onFocusHandler = a.onFocus ?: a.onBeginEditing
            val onBlurHandler = a.onBlur ?: a.onEndEditing

            // Build common modifier
            var modifier: Modifier = Modifier
            modifier = ModifierBuilder.applyTestTag(modifier, json)
            modifier = ModifierBuilder.applyMargins(modifier, json, data)
            modifier = ModifierBuilder.applySize(modifier, json)
            if (isHidden) {
                modifier = modifier.alpha(0f)
            } else {
                modifier = ModifierBuilder.applyAlpha(modifier, json, data)
            }
            if (fieldId != null) {
                modifier = modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        val wasFocused = hasFocus
                        hasFocus = focusState.isFocused
                        if (focusState.isFocused && !wasFocused) {
                            onFocusHandler?.let { ModifierBuilder.resolveEventHandler(it, data) }
                        }
                        if (!focusState.isFocused && wasFocused) {
                            onBlurHandler?.let { ModifierBuilder.resolveEventHandler(it, data) }
                        }
                    }
            }

            if (hasMarginAttributes(json)) {
                var boxModifier: Modifier = Modifier
                boxModifier = ModifierBuilder.applyTestTag(boxModifier, json)
                boxModifier = ModifierBuilder.applyMargins(boxModifier, json, data)
                if (isHidden) {
                    boxModifier = boxModifier.alpha(0f)
                } else {
                    boxModifier = ModifierBuilder.applyAlpha(boxModifier, json, data)
                }

                var textFieldModifier: Modifier = Modifier
                textFieldModifier = ModifierBuilder.applySize(textFieldModifier, json)
                if (fieldId != null) {
                    textFieldModifier = textFieldModifier
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            val wasFocused = hasFocus
                            hasFocus = focusState.isFocused
                            if (focusState.isFocused && !wasFocused) {
                                onFocusHandler?.let { ModifierBuilder.resolveEventHandler(it, data) }
                            }
                            if (!focusState.isFocused && wasFocused) {
                                onBlurHandler?.let { ModifierBuilder.resolveEventHandler(it, data) }
                            }
                        }
                }

                CustomTextFieldWithMargins(
                    state = textFieldState,
                    boxModifier = boxModifier,
                    textFieldModifier = textFieldModifier,
                    placeholder = placeholder,
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
                    enabled = isEnabled
                )
            } else {
                CustomTextField(
                    state = textFieldState,
                    modifier = modifier,
                    placeholder = placeholder,
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
                    enabled = isEnabled
                )
            }
        }

        // ── Helpers ──

        /**
         * Two-way write-back target: canonically a single FLAT identifier
         * (no dots, brackets, `??` or `!`) — kept flat by design so the
         * updateData key matches the ViewModel property. A stray `??`
         * default is stripped defensively (whitespace-insensitive).
         */
        private fun extractBindingVariable(text: String): String? {
            val pattern = "@\\{([^}]+)\\}".toRegex()
            val match = pattern.find(text) ?: return null
            return match.groupValues[1].split("??")[0].trim()
        }

        private fun hasMarginAttributes(json: JsonObject): Boolean {
            return TypedAttrs.rawKey(json, "margins") != null || TypedAttrs.rawKey(json, "topMargin") != null || TypedAttrs.rawKey(json, "bottomMargin") != null ||
                    TypedAttrs.rawKey(json, "leftMargin") != null || TypedAttrs.rawKey(json, "rightMargin") != null ||
                    TypedAttrs.rawKey(json, "startMargin") != null || TypedAttrs.rawKey(json, "endMargin") != null ||
                    TypedAttrs.rawKey(json, "marginTop") != null || TypedAttrs.rawKey(json, "marginBottom") != null ||
                    TypedAttrs.rawKey(json, "marginLeft") != null || TypedAttrs.rawKey(json, "marginRight") != null ||
                    TypedAttrs.rawKey(json, "marginStart") != null || TypedAttrs.rawKey(json, "marginEnd") != null
        }

        private fun resolveIsOutlined(json: JsonObject, a: TextFieldAttributes): Boolean {
            // borderStyle: none → not outlined (TextField declares its own
            // enum; undeclared spellings pass through like the legacy reader)
            val borderStyle = TypedAttrs.enumString(a.borderStyle) { it.json }?.lowercase()
            if (borderStyle == "none") return false
            if (borderStyle in listOf("line", "bezel", "roundedrect")) return true

            // 'outlined' is an undeclared legacy runtime extra
            return TypedAttrs.undeclared(json, "outlined")?.asBoolean == true ||
                    a.common.borderColor != null ||
                    a.common.borderWidth != null
        }

        private fun buildTextStyle(
            a: TextFieldAttributes,
            textColor: androidx.compose.ui.graphics.Color,
            fontSize: Int
        ): TextStyle {
            var style = TextStyle(
                fontSize = fontSize.sp,
                color = textColor
            )

            // Text alignment
            TypedAttrs.enumString(a.textAlign) { it.json }?.let { align ->
                style = when (align.lowercase()) {
                    "center" -> style.copy(textAlign = TextAlign.Center)
                    "right" -> style.copy(textAlign = TextAlign.End)
                    "left" -> style.copy(textAlign = TextAlign.Start)
                    else -> style
                }
            }

            return style
        }

        private fun buildKeyboardOptions(a: TextFieldAttributes, nextFocusId: String?): KeyboardOptions {
            // Keyboard type from contentType (priority) or input
            val keyboardType = resolveKeyboardType(a)

            // IME action
            val returnKey = TypedAttrs.enumString(a.returnKeyType) { it.json }
            val imeAction = when {
                returnKey != null -> when (returnKey) {
                    "Done" -> ImeAction.Done
                    "Next" -> ImeAction.Next
                    "Search" -> ImeAction.Search
                    "Send" -> ImeAction.Send
                    "Go" -> ImeAction.Go
                    else -> ImeAction.Default
                }
                nextFocusId != null -> ImeAction.Next
                else -> ImeAction.Default
            }

            // Auto-capitalization
            val capitalization = a.autocapitalizationType?.let { type ->
                when (type.lowercase()) {
                    "none" -> KeyboardCapitalization.None
                    "words" -> KeyboardCapitalization.Words
                    "sentences" -> KeyboardCapitalization.Sentences
                    "allcharacters", "characters" -> KeyboardCapitalization.Characters
                    else -> KeyboardCapitalization.None
                }
            } ?: KeyboardCapitalization.None

            // Auto-correction
            val autoCorrect = a.autocorrectionType?.let { type ->
                when (type.lowercase()) {
                    "no", "false", "off" -> false
                    else -> true
                }
            } ?: true

            return KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction,
                capitalization = capitalization,
                autoCorrectEnabled = autoCorrect
            )
        }

        private fun resolveKeyboardType(a: TextFieldAttributes): KeyboardType {
            // contentType takes priority
            TypedAttrs.static(a.contentType)?.let { type ->
                return when (type.lowercase()) {
                    "emailaddress", "email" -> KeyboardType.Email
                    "password", "newpassword" -> KeyboardType.Password
                    "telephonenumber", "phone" -> KeyboardType.Phone
                    "url" -> KeyboardType.Uri
                    "creditcardnumber" -> KeyboardType.Number
                    else -> KeyboardType.Text
                }
            }
            // Fallback to input
            TypedAttrs.enumString(a.input) { it.json }?.let { input ->
                return when (input.lowercase()) {
                    "email" -> KeyboardType.Email
                    "password" -> KeyboardType.Password
                    "number" -> KeyboardType.Number
                    "decimal" -> KeyboardType.Decimal
                    "phone" -> KeyboardType.Phone
                    else -> KeyboardType.Text
                }
            }
            return KeyboardType.Text
        }

        private fun buildKeyboardActions(
            data: Map<String, Any>,
            nextFocusId: String?,
            onSubmitHandler: String?,
            viewId: String
        ): KeyboardActions {
            return KeyboardActions(
                onDone = {
                    if (nextFocusId != null) {
                        FocusManager.requestFocus(nextFocusId)
                    }
                    onSubmitHandler?.let {
                        ModifierBuilder.resolveEventHandler(it, data, viewId)
                    }
                },
                onNext = {
                    nextFocusId?.let { FocusManager.requestFocus(it) }
                },
                onGo = {
                    onSubmitHandler?.let {
                        ModifierBuilder.resolveEventHandler(it, data, viewId)
                    }
                },
                onSearch = {
                    onSubmitHandler?.let {
                        ModifierBuilder.resolveEventHandler(it, data, viewId)
                    }
                },
                onSend = {
                    onSubmitHandler?.let {
                        ModifierBuilder.resolveEventHandler(it, data, viewId)
                    }
                }
            )
        }

        /** TextField-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "text", "hint", "placeholder", "fontColor", "hintColor",
            "hintFont", "hintFontSize", "secure", "input", "contentType",
            "enabled", "onTextChange", "onSubmit", "onFocus",
            "onBeginEditing", "onBlur", "onEndEditing", "nextFocus",
            "fontSize", "textAlign", "borderStyle", "returnKeyType",
            "autocapitalizationType", "autocorrectionType",
            "highlightBackground", "fieldPadding", "textPaddingLeft"
        )

        private fun buildContentPadding(json: JsonObject, a: TextFieldAttributes): PaddingValues? {
            // paddings (array or single number)
            TypedAttrs.rawKey(json, "paddings")?.let { element ->
                if (element.isJsonPrimitive && element.asJsonPrimitive.isNumber) {
                    return PaddingValues(element.asFloat.dp)
                }
                if (element.isJsonArray) {
                    val arr = element.asJsonArray
                    return when (arr.size()) {
                        1 -> PaddingValues(arr[0].asFloat.dp)
                        2 -> PaddingValues(
                            vertical = arr[0].asFloat.dp,
                            horizontal = arr[1].asFloat.dp
                        )
                        4 -> PaddingValues(
                            start = arr[3].asFloat.dp,
                            top = arr[0].asFloat.dp,
                            end = arr[1].asFloat.dp,
                            bottom = arr[2].asFloat.dp
                        )
                        else -> null
                    }
                }
            }

            // fieldPadding (legacy)
            a.fieldPadding?.let {
                return PaddingValues(it.toFloat().dp)
            }

            // textPaddingLeft
            a.textPaddingLeft?.let { startPadding ->
                return PaddingValues(start = startPadding.toFloat().dp)
            }

            return null
        }

    }
}
