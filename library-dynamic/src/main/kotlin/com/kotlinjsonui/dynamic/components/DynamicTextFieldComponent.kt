package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.FocusManager
import com.kotlinjsonui.dynamic.helpers.ColorParser
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

            // Parse text value with data binding
            val rawText = json.get("text")?.asString ?: ""
            val initialText = ResourceResolver.resolveTextValue(rawText, data, context)
            var textValue by remember(initialText) { mutableStateOf(initialText) }

            // Parse placeholder with resource resolution
            val rawPlaceholder = json.get("hint")?.asString
                ?: json.get("placeholder")?.asString ?: ""
            val placeholderText = ResourceResolver.resolveTextValue(rawPlaceholder, data, context)

            // Detect hidden TextField (fontColor: "transparent")
            val isHidden = json.get("fontColor")?.asString?.lowercase() == "transparent"

            // Parse secure field
            val isSecure = json.get("secure")?.asBoolean == true ||
                    json.get("input")?.asString?.lowercase() == "password" ||
                    json.get("contentType")?.asString?.lowercase()?.let {
                        it == "password" || it == "newpassword"
                    } ?: false

            // Parse enabled state
            val isEnabled = ResourceResolver.resolveBoolean(json, "enabled", data, default = true)

            // Parse max lines
            val maxLines = json.get("maxLines")?.asInt ?: 1
            val singleLine = maxLines == 1

            // onValueChange handler with data binding update
            val viewId = json.get("id")?.asString ?: "textfield"
            val onValueChange: (String) -> Unit = { newValue ->
                textValue = newValue

                // Update data binding if text is bound
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

                // Call onTextChange handler if specified
                val onTextChangeHandler = json.get("onTextChange")?.asString
                if (onTextChangeHandler != null) {
                    ModifierBuilder.resolveEventHandler(
                        onTextChangeHandler, data, viewId, newValue
                    )
                }
            }

            // Focus management
            val fieldId = json.get("fieldId")?.asString
            val nextFocusId = json.get("nextFocusId")?.asString
            val onSubmitHandler = json.get("onSubmit")?.asString

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
            val textColor = ColorParser.parseColorWithBinding(json, "fontColor", data, context)
                ?: Configuration.TextField.defaultTextColor
            val placeholderColor = ColorParser.parseColorWithBinding(json, "hintColor", data, context)
                ?: Configuration.TextField.defaultPlaceholderColor
            val backgroundColor = ColorParser.parseColorWithBinding(json, "background", data, context)
                ?: Configuration.TextField.defaultBackgroundColor
            val highlightBackgroundColor = ColorParser.parseColorWithBinding(json, "highlightBackground", data, context)
                ?: Configuration.TextField.defaultHighlightBackgroundColor
            val borderColor = ColorParser.parseColorWithBinding(json, "borderColor", data, context)
                ?: Configuration.TextField.defaultBorderColor

            // Font size
            val fontSize = json.get("fontSize")?.asInt ?: Configuration.TextField.defaultFontSize
            val hintFontSize = json.get("hintFontSize")?.asInt

            // Shape
            val cornerRadius = json.get("cornerRadius")?.asFloat
                ?: Configuration.TextField.defaultCornerRadius.toFloat()
            val shape = RoundedCornerShape(cornerRadius.dp)

            // Outlined mode
            val isOutlined = resolveIsOutlined(json)

            // Content padding
            val contentPadding = buildContentPadding(json)

            // Text style
            val textStyleParts = mutableListOf<String>()
            val textStyle = buildTextStyle(json, textColor, fontSize, data)

            // Visual transformation
            val visualTransformation = if (isSecure) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            }

            // Keyboard options
            val keyboardOptions = buildKeyboardOptions(json, nextFocusId)

            // Keyboard actions
            val keyboardActions = buildKeyboardActions(json, data, nextFocusId, onSubmitHandler, viewId)

            // Placeholder composable
            val placeholder: @Composable (() -> Unit)? = if (placeholderText.isNotEmpty()) {
                {
                    Text(
                        text = placeholderText,
                        color = placeholderColor,
                        fontSize = (hintFontSize ?: fontSize).sp,
                        fontWeight = if (json.get("hintFont")?.asString == "bold") {
                            androidx.compose.ui.text.font.FontWeight.Bold
                        } else null
                    )
                }
            } else null

            // Focus event handlers
            val onFocusHandler = json.get("onFocus")?.asString
                ?: json.get("onBeginEditing")?.asString
            val onBlurHandler = json.get("onBlur")?.asString
                ?: json.get("onEndEditing")?.asString

            // Check if we need margins wrapper
            val hasMargins = hasMarginAttributes(json)

            if (hasMargins) {
                // Box modifier with margins
                var boxModifier: Modifier = Modifier
                boxModifier = ModifierBuilder.applyTestTag(boxModifier, json)
                boxModifier = ModifierBuilder.applyMargins(boxModifier, json, data)
                if (isHidden) {
                    boxModifier = boxModifier.alpha(0f)
                } else {
                    boxModifier = ModifierBuilder.applyAlpha(boxModifier, json, data)
                }

                // TextField modifier (size only)
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
                    value = textValue,
                    onValueChange = onValueChange,
                    boxModifier = boxModifier,
                    textFieldModifier = textFieldModifier,
                    placeholder = placeholder,
                    visualTransformation = visualTransformation,
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
                // Regular modifier
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

                CustomTextField(
                    value = textValue,
                    onValueChange = onValueChange,
                    modifier = modifier,
                    placeholder = placeholder,
                    visualTransformation = visualTransformation,
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

        private fun extractBindingVariable(text: String): String? {
            val pattern = "@\\{([^}]+)\\}".toRegex()
            val match = pattern.find(text) ?: return null
            return match.groupValues[1].split(" ?? ")[0].trim()
        }

        private fun hasMarginAttributes(json: JsonObject): Boolean {
            return json.has("margins") || json.has("topMargin") || json.has("bottomMargin") ||
                    json.has("leftMargin") || json.has("rightMargin") ||
                    json.has("startMargin") || json.has("endMargin") ||
                    json.has("marginTop") || json.has("marginBottom") ||
                    json.has("marginLeft") || json.has("marginRight") ||
                    json.has("marginStart") || json.has("marginEnd")
        }

        private fun resolveIsOutlined(json: JsonObject): Boolean {
            // borderStyle: none → not outlined
            val borderStyle = json.get("borderStyle")?.asString?.lowercase()
            if (borderStyle == "none") return false
            if (borderStyle in listOf("line", "bezel", "roundedrect")) return true

            return json.get("outlined")?.asBoolean == true ||
                    json.get("borderColor") != null ||
                    json.get("borderWidth") != null
        }

        private fun buildTextStyle(
            json: JsonObject,
            textColor: androidx.compose.ui.graphics.Color,
            fontSize: Int,
            data: Map<String, Any>
        ): TextStyle {
            var style = TextStyle(
                fontSize = fontSize.sp,
                color = textColor
            )

            // Text alignment
            json.get("textAlign")?.asString?.let { align ->
                style = when (align.lowercase()) {
                    "center" -> style.copy(textAlign = TextAlign.Center)
                    "right" -> style.copy(textAlign = TextAlign.End)
                    "left" -> style.copy(textAlign = TextAlign.Start)
                    else -> style
                }
            }

            return style
        }

        private fun buildKeyboardOptions(json: JsonObject, nextFocusId: String?): KeyboardOptions {
            // Keyboard type from contentType (priority) or input
            val keyboardType = resolveKeyboardType(json)

            // IME action
            val imeAction = when {
                json.get("returnKeyType") != null -> when (json.get("returnKeyType").asString) {
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
            val capitalization = json.get("autocapitalizationType")?.asString?.let { type ->
                when (type.lowercase()) {
                    "none" -> KeyboardCapitalization.None
                    "words" -> KeyboardCapitalization.Words
                    "sentences" -> KeyboardCapitalization.Sentences
                    "allcharacters", "characters" -> KeyboardCapitalization.Characters
                    else -> KeyboardCapitalization.None
                }
            } ?: KeyboardCapitalization.None

            // Auto-correction
            val autoCorrect = json.get("autocorrectionType")?.asString?.let { type ->
                when (type.lowercase()) {
                    "no", "false", "off" -> false
                    else -> true
                }
            } ?: true

            return KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction,
                capitalization = capitalization,
                autoCorrect = autoCorrect
            )
        }

        private fun resolveKeyboardType(json: JsonObject): KeyboardType {
            // contentType takes priority
            json.get("contentType")?.asString?.let { type ->
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
            json.get("input")?.asString?.let { input ->
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
            json: JsonObject,
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

        private fun buildContentPadding(json: JsonObject): PaddingValues? {
            // paddings (array or single number)
            json.get("paddings")?.let { element ->
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
            json.get("fieldPadding")?.asFloat?.let {
                return PaddingValues(it.dp)
            }

            // textPaddingLeft
            json.get("textPaddingLeft")?.asFloat?.let { startPadding ->
                return PaddingValues(start = startPadding.dp)
            }

            return null
        }

    }
}
