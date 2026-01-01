package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.processDataBinding
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ColorParser
import androidx.compose.ui.platform.LocalContext

/**
 * Dynamic TextView Component Converter
 * Converts JSON to TextView (multiline text) composable at runtime
 * 
 * Supported JSON attributes:
 * - text: String value or @{binding} for data binding
 * - placeholder/hint: String placeholder text
 * - fontSize: Int text size in sp
 * - fontColor: String hex color for text
 * - background: String hex color for background
 * - highlightBackground: String hex color for focused background
 * - borderColor: String hex color for border
 * - cornerRadius: Float corner radius
 * - maxLines: Int maximum number of lines
 * - height: Number or "wrapContent"/"matchParent" (default: 120dp)
 * - width: Number or "wrapContent"/"matchParent" (default: fillMaxWidth)
 * - enabled: Boolean whether the field is enabled
 * - returnKeyType: String keyboard action (Done, Next, Default)
 * - onTextChange: String method name for text change callback
 * - padding/margins: Spacing properties
 */
class DynamicTextViewComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current

            // Parse text value with data binding
            val textBinding = json.get("text")?.asString ?: ""
            val initialText = processDataBinding(textBinding, data)
            var currentText by remember(initialText) { mutableStateOf(initialText) }
            
            // Parse placeholder
            val placeholder = json.get("placeholder")?.asString 
                ?: json.get("hint")?.asString 
                ?: ""
            
            // Parse enabled state
            val isEnabled = json.get("enabled")?.let { element ->
                when {
                    element.isJsonPrimitive && element.asJsonPrimitive.isBoolean -> element.asBoolean
                    element.isJsonPrimitive && element.asJsonPrimitive.isString -> {
                        val binding = element.asString
                        if (binding.startsWith("@{") && binding.endsWith("}")) {
                            val key = binding.substring(2, binding.length - 1)
                            data[key] as? Boolean ?: true
                        } else {
                            true
                        }
                    }
                    else -> true
                }
            } ?: true
            
            // Parse text style (supports @{binding})
            val fontSize = json.get("fontSize")?.asInt ?: Configuration.TextField.defaultFontSize
            val textColor = ColorParser.parseColorWithBinding(json, "fontColor", data, context)
                ?: Configuration.TextField.defaultTextColor

            // Parse background colors (supports @{binding})
            val backgroundColor = ColorParser.parseColorWithBinding(json, "background", data, context)
                ?: Configuration.TextField.defaultBackgroundColor

            val highlightBackgroundColor = ColorParser.parseColorWithBinding(json, "highlightBackground", data, context)
                ?: Configuration.TextField.defaultHighlightBackgroundColor

            // Parse border color (supports @{binding})
            val borderColor = ColorParser.parseColorWithBinding(json, "borderColor", data, context)
                ?: Configuration.TextField.defaultBorderColor
            
            // Parse shape
            val cornerRadius = json.get("cornerRadius")?.asFloat ?: Configuration.TextField.defaultCornerRadius.toFloat()
            val shape = RoundedCornerShape(cornerRadius.dp)
            
            // Parse isOutlined - automatically use outlined style if borderColor or borderWidth is specified
            val isOutlined = json.get("outlined")?.asBoolean == true || 
                             json.get("borderColor") != null || 
                             json.get("borderWidth") != null
            
            // Parse max lines (default to unlimited for TextView)
            val maxLines = json.get("maxLines")?.asInt ?: Int.MAX_VALUE
            
            // Parse keyboard options
            val imeAction = when (json.get("returnKeyType")?.asString) {
                "Done" -> ImeAction.Done
                "Next" -> ImeAction.Next
                "Default" -> ImeAction.Default
                else -> ImeAction.Default
            }
            val keyboardOptions = KeyboardOptions(imeAction = imeAction)
            
            // Value change handler
            val onValueChange: (String) -> Unit = { newValue ->
                currentText = newValue
                
                // Call onTextChange method if specified
                json.get("onTextChange")?.asString?.let { methodName ->
                    val handler = data[methodName]
                    if (handler is Function1<*, *>) {
                        try {
                            @Suppress("UNCHECKED_CAST")
                            (handler as (String) -> Unit)(newValue)
                        } catch (e: Exception) {
                            // Handler doesn't match expected signature
                        }
                    }
                }
            }
            
            // Check if we need margins wrapper
            val hasMargins = json.has("margins") || json.has("topMargin") || 
                            json.has("bottomMargin") || json.has("leftMargin") || 
                            json.has("rightMargin") || json.has("marginTop") ||
                            json.has("marginBottom") || json.has("marginLeft") ||
                            json.has("marginRight") || json.has("marginStart") ||
                            json.has("marginEnd")
            
            if (hasMargins) {
                // Use CustomTextFieldWithMargins for margin support
                val boxModifier = ModifierBuilder.applyMargins(Modifier, json)
                
                // Build text field modifier with size and padding
                var textFieldModifier: Modifier = Modifier
                
                // Apply width (default to fillMaxWidth for TextView)
                val width = json.get("width")?.asString
                textFieldModifier = when (width) {
                    "matchParent", null -> textFieldModifier.fillMaxWidth()
                    "wrapContent" -> textFieldModifier
                    else -> {
                        try {
                            val widthValue = width?.toFloatOrNull()
                            if (widthValue != null) {
                                textFieldModifier.fillMaxWidth()
                            } else {
                                textFieldModifier.fillMaxWidth()
                            }
                        } catch (e: Exception) {
                            textFieldModifier.fillMaxWidth()
                        }
                    }
                }
                
                // Apply height (default to 120dp for TextView)
                val heightValue = json.get("height")?.asFloat ?: 120f
                textFieldModifier = textFieldModifier.height(heightValue.dp)
                
                // Apply padding
                textFieldModifier = ModifierBuilder.applyPadding(textFieldModifier, json)
                
                // Apply opacity
                textFieldModifier = ModifierBuilder.applyOpacity(textFieldModifier, json)
                
                CustomTextFieldWithMargins(
                    value = currentText,
                    onValueChange = onValueChange,
                    boxModifier = boxModifier,
                    textFieldModifier = textFieldModifier,
                    placeholder = if (placeholder.isNotEmpty()) {
                        { Text(placeholder) }
                    } else null,
                    shape = shape,
                    backgroundColor = backgroundColor,
                    highlightBackgroundColor = highlightBackgroundColor,
                    borderColor = borderColor,
                    isOutlined = isOutlined,
                    maxLines = maxLines,
                    singleLine = false,
                    textStyle = TextStyle(
                        fontSize = fontSize.sp,
                        color = textColor
                    ),
                    keyboardOptions = keyboardOptions
                )
            } else {
                // Use regular CustomTextField
                var modifier: Modifier = Modifier
                
                // Apply width (default to fillMaxWidth for TextView)
                val width = json.get("width")?.asString
                modifier = when (width) {
                    "matchParent", null -> modifier.fillMaxWidth()
                    "wrapContent" -> modifier
                    else -> {
                        try {
                            val widthValue = width?.toFloatOrNull()
                            if (widthValue != null) {
                                modifier.fillMaxWidth()
                            } else {
                                modifier.fillMaxWidth()
                            }
                        } catch (e: Exception) {
                            modifier.fillMaxWidth()
                        }
                    }
                }
                
                // Apply height (default to 120dp for TextView)
                val heightValue = json.get("height")?.asFloat ?: 120f
                modifier = modifier.height(heightValue.dp)
                
                // Apply padding
                modifier = ModifierBuilder.applyPadding(modifier, json)
                
                CustomTextField(
                    value = currentText,
                    onValueChange = onValueChange,
                    modifier = modifier,
                    placeholder = if (placeholder.isNotEmpty()) {
                        { Text(placeholder) }
                    } else null,
                    shape = shape,
                    backgroundColor = backgroundColor,
                    highlightBackgroundColor = highlightBackgroundColor,
                    borderColor = borderColor,
                    isOutlined = isOutlined,
                    maxLines = maxLines,
                    singleLine = false,
                    textStyle = TextStyle(
                        fontSize = fontSize.sp,
                        color = textColor
                    ),
                    keyboardOptions = keyboardOptions
                )
            }
        }
    }
}