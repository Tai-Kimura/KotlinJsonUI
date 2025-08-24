package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import com.kotlinjsonui.dynamic.processDataBinding

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
            // Parse text value with data binding
            val rawText = json.get("text")?.asString ?: ""
            val initialText = processDataBinding(rawText, data)
            
            // State for the text field value
            var textValue by remember(initialText) { mutableStateOf(initialText) }
            
            // Parse placeholder
            val placeholderText = json.get("hint")?.asString 
                ?: json.get("placeholder")?.asString 
                ?: ""
            
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
            
            // Parse colors
            val textColor = json.get("fontColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { null }
            }
            
            val placeholderColor = json.get("hintColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { null }
            }
            
            val backgroundColor = json.get("background")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { null }
            }
            
            // Parse font size
            val fontSize = json.get("fontSize")?.asInt
            val hintFontSize = json.get("hintFontSize")?.asInt
            
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
                        color = placeholderColor ?: LocalTextStyle.current.color.copy(alpha = 0.6f),
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
                    backgroundColor = backgroundColor
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
                    backgroundColor = backgroundColor
                )
            }
        }
        
        private fun buildModifier(json: JsonObject, includeMargins: Boolean): Modifier {
            var modifier: Modifier = Modifier
            
            // Width and height
            json.get("width")?.asFloat?.let { width ->
                modifier = if (width < 0) {
                    modifier.fillMaxWidth()
                } else {
                    modifier.width(width.dp)
                }
            }
            
            json.get("height")?.asFloat?.let { height ->
                modifier = modifier.height(height.dp)
            }
            
            // Padding
            json.get("paddings")?.asJsonArray?.let { paddings ->
                modifier = when (paddings.size()) {
                    1 -> modifier.padding(paddings[0].asFloat.dp)
                    2 -> modifier.padding(
                        vertical = paddings[0].asFloat.dp,
                        horizontal = paddings[1].asFloat.dp
                    )
                    4 -> modifier.padding(
                        top = paddings[0].asFloat.dp,
                        end = paddings[1].asFloat.dp,
                        bottom = paddings[2].asFloat.dp,
                        start = paddings[3].asFloat.dp
                    )
                    else -> modifier
                }
            } ?: json.get("padding")?.asFloat?.let { padding ->
                modifier = modifier.padding(padding.dp)
            }
            
            return modifier
        }
        
        private fun buildMarginModifier(json: JsonObject): Modifier {
            var modifier: Modifier = Modifier
            
            // Handle margins array
            json.get("margins")?.asJsonArray?.let { margins ->
                modifier = when (margins.size()) {
                    1 -> modifier.padding(margins[0].asFloat.dp)
                    2 -> modifier.padding(
                        vertical = margins[0].asFloat.dp,
                        horizontal = margins[1].asFloat.dp
                    )
                    4 -> modifier.padding(
                        top = margins[0].asFloat.dp,
                        end = margins[1].asFloat.dp,
                        bottom = margins[2].asFloat.dp,
                        start = margins[3].asFloat.dp
                    )
                    else -> modifier
                }
            }
            
            // Handle individual margin properties
            val topMargin = json.get("topMargin")?.asFloat ?: 0f
            val bottomMargin = json.get("bottomMargin")?.asFloat ?: 0f
            val leftMargin = json.get("leftMargin")?.asFloat ?: json.get("startMargin")?.asFloat ?: 0f
            val rightMargin = json.get("rightMargin")?.asFloat ?: json.get("endMargin")?.asFloat ?: 0f
            
            if (topMargin > 0 || bottomMargin > 0 || leftMargin > 0 || rightMargin > 0) {
                modifier = modifier.padding(
                    top = topMargin.dp,
                    bottom = bottomMargin.dp,
                    start = leftMargin.dp,
                    end = rightMargin.dp
                )
            }
            
            return modifier
        }
    }
}