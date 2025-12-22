package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.processDataBinding
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder

/**
 * Dynamic TextField Component Converter
 * Converts JSON to TextField/TextInput composable at runtime
 *
 * Supported JSON attributes (matching Ruby implementation):
 * - text: String value (supports @{variable} binding)
 * - hint/placeholder: String placeholder text
 * - hintColor: String hex color for placeholder
 * - hintFontSize: Int size for placeholder text
 * - secure: Boolean for password field
 * - onTextChange: String method name for text change handler
 * - keyboardType: String type (text, number, email, phone, password)
 * - maxLines: Int maximum lines (default 1)
 * - disabled: Boolean to disable input
 * - fontSize: Int text size
 * - fontColor: String hex color for text
 * - background: String hex color for background
 * - cornerRadius: Float corner radius
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 * - width/height: Number dimensions
 */
class DynamicTextFieldComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current

            // Parse text value with data binding
            val rawText = json.get("text")?.asString ?: ""
            val initialText = processDataBinding(rawText, data, context)

            // State for the text field value
            var textValue by remember(initialText) { mutableStateOf(initialText) }

            // Parse placeholder with resource resolution
            val rawPlaceholder = json.get("hint")?.asString
                ?: json.get("placeholder")?.asString
                ?: ""
            val placeholderText = processDataBinding(rawPlaceholder, data, context)

            // Parse secure field
            val isSecure = json.get("secure")?.asBoolean ?: false

            // Parse disabled state
            val isDisabled = json.get("disabled")?.asBoolean ?: false

            // Parse max lines
            val maxLines = json.get("maxLines")?.asInt ?: 1
            val singleLine = maxLines == 1

            // Parse keyboard type
            val keyboardType = when (json.get("keyboardType")?.asString?.lowercase()) {
                "number", "numeric" -> KeyboardType.Number
                "phone" -> KeyboardType.Phone
                "email" -> KeyboardType.Email
                "password" -> KeyboardType.Password
                "decimal" -> KeyboardType.Decimal
                "uri", "url" -> KeyboardType.Uri
                else -> KeyboardType.Text
            }

            // Parse IME action
            val imeAction = when (json.get("imeAction")?.asString?.lowercase()) {
                "done" -> ImeAction.Done
                "go" -> ImeAction.Go
                "next" -> ImeAction.Next
                "previous" -> ImeAction.Previous
                "search" -> ImeAction.Search
                "send" -> ImeAction.Send
                else -> ImeAction.Default
            }

            // Parse colors with Configuration defaults
            val textColor = json.get("fontColor")?.asString?.let {
                ColorParser.parseColorString(it)
            } ?: Configuration.TextField.defaultTextColor

            val placeholderColor = json.get("hintColor")?.asString?.let {
                ColorParser.parseColorString(it)
            } ?: Configuration.TextField.defaultPlaceholderColor

            val backgroundColor = json.get("background")?.asString?.let {
                ColorParser.parseColorString(it)
            } ?: Configuration.TextField.defaultBackgroundColor
            
            val highlightBackgroundColor = json.get("highlightBackground")?.asString?.let {
                ColorParser.parseColorString(it)
            } ?: Configuration.TextField.defaultHighlightBackgroundColor
            
            val borderColor = json.get("borderColor")?.asString?.let {
                ColorParser.parseColorString(it)
            } ?: Configuration.TextField.defaultBorderColor

            // Parse font size with Configuration defaults
            val fontSize = json.get("fontSize")?.asInt ?: Configuration.TextField.defaultFontSize
            val hintFontSize = json.get("hintFontSize")?.asInt ?: Configuration.TextField.defaultFontSize
            
            // Parse shape
            val cornerRadius = json.get("cornerRadius")?.asFloat ?: Configuration.TextField.defaultCornerRadius.toFloat()
            val shape = RoundedCornerShape(cornerRadius.dp)
            
            // Parse isOutlined - automatically use outlined style if borderColor or borderWidth is specified
            val isOutlined = json.get("outlined")?.asBoolean == true ||
                             json.get("borderColor") != null ||
                             json.get("borderWidth") != null

            // Parse contentPadding from paddings or fieldPadding
            val contentPadding = buildContentPadding(json)

            // Handle onTextChange event
            val onValueChange: (String) -> Unit = { newValue ->
                textValue = newValue

                // Call the event handler if specified
                json.get("onTextChange")?.asString?.let { methodName ->
                    // Look for the function in the data map
                    val handler = data[methodName]
                    if (handler is Function<*>) {
                        try {
                            @Suppress("UNCHECKED_CAST")
                            (handler as (String) -> Unit)(newValue)
                        } catch (e: Exception) {
                            // Try without parameter
                            try {
                                @Suppress("UNCHECKED_CAST")
                                (handler as () -> Unit)()
                            } catch (e2: Exception) {
                                // Handler doesn't match expected signature
                            }
                        }
                    }
                }

                // Update data binding if text is bound
                if (rawText.contains("@{")) {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(rawText)?.let { match ->
                        val variable = match.groupValues[1].split(" ?? ")[0].trim()
                        // Find update function in data map
                        val updateData = data["updateData"]
                        if (updateData is Function<*>) {
                            try {
                                @Suppress("UNCHECKED_CAST")
                                (updateData as (Map<String, Any>) -> Unit)(mapOf(variable to newValue))
                            } catch (e: Exception) {
                                // Update function doesn't match expected signature
                            }
                        }
                    }
                }
            }

            // Create placeholder composable
            val placeholder: @Composable (() -> Unit)? = if (placeholderText.isNotEmpty()) {
                {
                    Text(
                        text = placeholderText,
                        color = placeholderColor ?: Configuration.TextField.defaultPlaceholderColor,
                        fontSize = (hintFontSize ?: fontSize ?: 14).sp
                    )
                }
            } else null

            // Create text style
            val textStyle = LocalTextStyle.current.copy(
                fontSize = (fontSize ?: 14).sp,
                color = textColor ?: LocalTextStyle.current.color
            )

            // Visual transformation for password
            val visualTransformation = if (isSecure) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            }

            // Keyboard options
            val keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            )

            // Check if we need CustomTextField for margins
            val hasMargins = json.get("margins") != null ||
                    json.get("topMargin") != null ||
                    json.get("bottomMargin") != null ||
                    json.get("leftMargin") != null ||
                    json.get("rightMargin") != null ||
                    json.get("startMargin") != null ||
                    json.get("endMargin") != null

            // Build modifier
            val modifier = buildModifier(json, !hasMargins)

            if (hasMargins) {
                // Use CustomTextFieldWithMargins for margin support
                val boxModifier = buildMarginModifier(json)
                CustomTextFieldWithMargins(
                    value = textValue,
                    onValueChange = onValueChange,
                    boxModifier = boxModifier,
                    textFieldModifier = modifier,
                    placeholder = placeholder,
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
                    maxLines = maxLines
                )
            } else {
                // Use CustomTextField
                CustomTextField(
                    value = textValue,
                    onValueChange = onValueChange,
                    modifier = modifier,
                    placeholder = placeholder,
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
                    maxLines = maxLines
                )
            }
        }

        private fun buildModifier(json: JsonObject, includeMargins: Boolean): Modifier {
            // Use ModifierBuilder for size
            var modifier = ModifierBuilder.buildSizeModifier(json)

            // NOTE: Don't apply padding to modifier for TextField
            // Padding is handled separately as contentPadding

            // Apply margins if included
            if (includeMargins) {
                modifier = ModifierBuilder.applyMargins(modifier, json)
            }

            // Apply opacity
            modifier = ModifierBuilder.applyOpacity(modifier, json)

            return modifier
        }

        private fun buildMarginModifier(json: JsonObject): Modifier {
            // Use ModifierBuilder for margins
            return ModifierBuilder.applyMargins(Modifier, json)
        }

        /**
         * Build contentPadding from paddings, padding, or fieldPadding JSON attribute
         */
        private fun buildContentPadding(json: JsonObject): PaddingValues? {
            // Check for paddings array first
            json.get("paddings")?.let { paddingsElement ->
                if (paddingsElement.isJsonArray) {
                    val paddings = paddingsElement.asJsonArray
                    return when (paddings.size()) {
                        1 -> PaddingValues(paddings[0].asFloat.dp)
                        2 -> PaddingValues(
                            vertical = paddings[0].asFloat.dp,
                            horizontal = paddings[1].asFloat.dp
                        )
                        4 -> PaddingValues(
                            start = paddings[3].asFloat.dp,
                            top = paddings[0].asFloat.dp,
                            end = paddings[1].asFloat.dp,
                            bottom = paddings[2].asFloat.dp
                        )
                        else -> null
                    }
                }
            }

            // Check for padding (single value or array)
            json.get("padding")?.let { paddingElement ->
                if (paddingElement.isJsonPrimitive && paddingElement.asJsonPrimitive.isNumber) {
                    return PaddingValues(paddingElement.asFloat.dp)
                } else if (paddingElement.isJsonArray) {
                    val paddings = paddingElement.asJsonArray
                    return when (paddings.size()) {
                        1 -> PaddingValues(paddings[0].asFloat.dp)
                        2 -> PaddingValues(
                            vertical = paddings[0].asFloat.dp,
                            horizontal = paddings[1].asFloat.dp
                        )
                        4 -> PaddingValues(
                            start = paddings[3].asFloat.dp,
                            top = paddings[0].asFloat.dp,
                            end = paddings[1].asFloat.dp,
                            bottom = paddings[2].asFloat.dp
                        )
                        else -> null
                    }
                }
            }

            // Check for fieldPadding (legacy single value)
            json.get("fieldPadding")?.asFloat?.let { padding ->
                return PaddingValues(padding.dp)
            }

            // Return null to use default contentPadding
            return null
        }
    }
}