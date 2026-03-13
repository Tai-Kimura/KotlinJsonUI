package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver

/**
 * TextView component → CustomTextField (multiline) / CustomTextFieldWithMargins.
 * Reference: textview_component.rb in kjui_tools.
 *
 * Key differences from TextField:
 * - Default height: 120dp
 * - Default width: fillMaxWidth
 * - Default maxLines: unlimited
 * - singleLine: false
 * - isOutlined: true by default
 */
class DynamicTextViewComponent {
    companion object {
        @Composable
        fun create(json: JsonObject, data: Map<String, Any> = emptyMap()) {
            val context = LocalContext.current

            // Parse text value with data binding
            val rawText = json.get("text")?.asString ?: ""
            val initialText = ResourceResolver.resolveTextValue(rawText, data, context)
            var currentText by remember(initialText) { mutableStateOf(initialText) }

            // Parse placeholder with resource resolution
            val rawPlaceholder = json.get("hint")?.asString
                ?: json.get("placeholder")?.asString ?: ""
            val placeholderText = ResourceResolver.resolveTextValue(rawPlaceholder, data, context)

            // Enabled state (supports @{binding})
            val isEnabled = ResourceResolver.resolveBoolean(json, "enabled", data, default = true)

            // Text style (supports @{binding})
            val fontSize = json.get("fontSize")?.asInt ?: Configuration.TextField.defaultFontSize
            val textColor = ColorParser.parseColorWithBinding(json, "fontColor", data, context)
                ?: Configuration.TextField.defaultTextColor

            // Background colors (supports @{binding})
            val backgroundColor = ColorParser.parseColorWithBinding(json, "background", data, context)
                ?: Configuration.TextField.defaultBackgroundColor
            val highlightBackgroundColor = ColorParser.parseColorWithBinding(json, "highlightBackground", data, context)
                ?: Configuration.TextField.defaultHighlightBackgroundColor

            // Border color (supports @{binding})
            val borderColor = ColorParser.parseColorWithBinding(json, "borderColor", data, context)
                ?: Configuration.TextField.defaultBorderColor

            // Shape
            val cornerRadius = json.get("cornerRadius")?.asFloat
                ?: Configuration.TextField.defaultCornerRadius.toFloat()
            val shape = RoundedCornerShape(cornerRadius.dp)

            // isOutlined (default true for TextView)
            val isOutlined = true

            // Max lines (default unlimited for TextView)
            val maxLines = json.get("maxLines")?.asInt ?: Int.MAX_VALUE

            // Keyboard options
            val keyboardOptions = buildKeyboardOptions(json)

            // Container inset → contentPadding
            val contentPadding = buildContainerInset(json)

            // Value change handler
            val viewId = json.get("id")?.asString ?: "textview"
            val onValueChange: (String) -> Unit = { newValue ->
                currentText = newValue

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
                val onTextChangeHandler = json.get("onTextChange")?.asString
                if (onTextChangeHandler != null) {
                    ModifierBuilder.resolveEventHandler(
                        onTextChangeHandler, data, viewId, newValue
                    )
                }
            }

            // Placeholder
            val placeholder: @Composable (() -> Unit)? = if (placeholderText.isNotEmpty()) {
                buildPlaceholder(json, placeholderText, fontSize)
            } else null

            // Check margins
            val hasMargins = hasMarginAttributes(json)

            if (hasMargins) {
                // Box modifier with margins and weight
                var boxModifier: Modifier = Modifier
                boxModifier = ModifierBuilder.applyTestTag(boxModifier, json)
                boxModifier = ModifierBuilder.applyMargins(boxModifier, json, data)

                // TextField modifier with size (default fillMaxWidth + 120dp height)
                var textFieldModifier = buildTextViewSizeModifier(json)
                textFieldModifier = ModifierBuilder.applyAlpha(textFieldModifier, json, data)
                textFieldModifier = ModifierBuilder.applyPadding(textFieldModifier, json)

                CustomTextFieldWithMargins(
                    value = currentText,
                    onValueChange = onValueChange,
                    boxModifier = boxModifier,
                    textFieldModifier = textFieldModifier,
                    placeholder = placeholder,
                    shape = shape,
                    backgroundColor = backgroundColor,
                    highlightBackgroundColor = highlightBackgroundColor,
                    borderColor = borderColor,
                    isOutlined = isOutlined,
                    maxLines = maxLines,
                    singleLine = false,
                    textStyle = TextStyle(fontSize = fontSize.sp, color = textColor),
                    keyboardOptions = keyboardOptions,
                    contentPadding = contentPadding,
                    enabled = isEnabled
                )
            } else {
                // Regular modifier with size (default fillMaxWidth + 120dp height)
                var modifier = buildTextViewSizeModifier(json)
                modifier = ModifierBuilder.applyTestTag(modifier, json)
                modifier = ModifierBuilder.applyAlpha(modifier, json, data)
                modifier = ModifierBuilder.applyPadding(modifier, json)

                CustomTextField(
                    value = currentText,
                    onValueChange = onValueChange,
                    modifier = modifier,
                    placeholder = placeholder,
                    shape = shape,
                    backgroundColor = backgroundColor,
                    highlightBackgroundColor = highlightBackgroundColor,
                    borderColor = borderColor,
                    isOutlined = isOutlined,
                    maxLines = maxLines,
                    singleLine = false,
                    textStyle = TextStyle(fontSize = fontSize.sp, color = textColor),
                    keyboardOptions = keyboardOptions,
                    contentPadding = contentPadding,
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

        /**
         * Build size modifier for TextView.
         * Default: fillMaxWidth + 120dp height (matching textview_component.rb).
         */
        private fun buildTextViewSizeModifier(json: JsonObject): Modifier {
            var modifier: Modifier = Modifier

            // Width (default fillMaxWidth)
            val widthElement = json.get("width")
            modifier = when {
                widthElement == null -> modifier.fillMaxWidth()
                widthElement.isJsonPrimitive && widthElement.asJsonPrimitive.isString -> {
                    when (widthElement.asString) {
                        "matchParent", "match_parent" -> modifier.fillMaxWidth()
                        "wrapContent", "wrap_content" -> modifier
                        else -> modifier.fillMaxWidth()
                    }
                }
                else -> modifier.fillMaxWidth()
            }

            // Height (default 120dp)
            val heightElement = json.get("height")
            modifier = when {
                heightElement == null -> modifier.height(120.dp)
                heightElement.isJsonPrimitive && heightElement.asJsonPrimitive.isString -> {
                    when (heightElement.asString) {
                        "matchParent", "match_parent" -> modifier.fillMaxHeight()
                        "wrapContent", "wrap_content" -> modifier.wrapContentHeight()
                        else -> {
                            heightElement.asString.toFloatOrNull()?.let {
                                modifier.height(it.dp)
                            } ?: modifier.height(120.dp)
                        }
                    }
                }
                heightElement.isJsonPrimitive && heightElement.asJsonPrimitive.isNumber -> {
                    modifier.height(heightElement.asFloat.dp)
                }
                else -> modifier.height(120.dp)
            }

            return modifier
        }

        @Composable
        private fun buildPlaceholder(
            json: JsonObject,
            text: String,
            baseFontSize: Int
        ): @Composable () -> Unit {
            val hintLineHeightMultiple = json.get("hintLineHeightMultiple")?.asFloat

            return if (hintLineHeightMultiple != null) {
                val hintFontSize = json.get("hintFontSize")?.asFloat ?: baseFontSize.toFloat()
                val lineHeight = hintFontSize * hintLineHeightMultiple
                {
                    Text(
                        text = text,
                        style = TextStyle(lineHeight = lineHeight.sp)
                    )
                }
            } else {
                { Text(text = text) }
            }
        }

        private fun buildKeyboardOptions(json: JsonObject): KeyboardOptions {
            val keyboardType = json.get("keyboardType")?.asString?.let { type ->
                when (type.lowercase()) {
                    "email" -> KeyboardType.Email
                    "number" -> KeyboardType.Number
                    "decimal" -> KeyboardType.Decimal
                    "phone" -> KeyboardType.Phone
                    "url" -> KeyboardType.Uri
                    else -> KeyboardType.Text
                }
            } ?: json.get("input")?.asString?.let { input ->
                when (input.lowercase()) {
                    "email" -> KeyboardType.Email
                    "number" -> KeyboardType.Number
                    "decimal" -> KeyboardType.Decimal
                    "phone" -> KeyboardType.Phone
                    "url" -> KeyboardType.Uri
                    else -> KeyboardType.Text
                }
            } ?: KeyboardType.Text

            val imeAction = when (json.get("returnKeyType")?.asString) {
                "Done" -> ImeAction.Done
                "Next" -> ImeAction.Next
                else -> ImeAction.Default
            }

            return KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction)
        }

        private fun buildContainerInset(json: JsonObject): PaddingValues? {
            val inset = json.get("containerInset") ?: return null
            return when {
                inset.isJsonArray -> {
                    val arr = inset.asJsonArray
                    when (arr.size()) {
                        4 -> PaddingValues(
                            top = arr[0].asFloat.dp,
                            end = arr[1].asFloat.dp,
                            bottom = arr[2].asFloat.dp,
                            start = arr[3].asFloat.dp
                        )
                        2 -> PaddingValues(
                            vertical = arr[0].asFloat.dp,
                            horizontal = arr[1].asFloat.dp
                        )
                        else -> null
                    }
                }
                inset.isJsonPrimitive && inset.asJsonPrimitive.isNumber -> {
                    PaddingValues(inset.asFloat.dp)
                }
                else -> null
            }
        }
    }
}
