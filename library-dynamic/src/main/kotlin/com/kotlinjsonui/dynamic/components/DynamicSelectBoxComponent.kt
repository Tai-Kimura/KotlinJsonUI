package com.kotlinjsonui.dynamic.components

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.google.gson.JsonObject
import com.kotlinjsonui.components.SelectBox
import com.kotlinjsonui.components.DateSelectBox
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import androidx.compose.ui.platform.LocalContext

/**
 * Dynamic SelectBox Component Converter
 * Converts JSON to SelectBox/DateSelectBox composable at runtime.
 * Reference: selectbox_component.rb in kjui_tools.
 *
 * Supported JSON attributes:
 * - selectedItem/selectedDate/bind: @{variable} for selected value binding
 * - selectItemType: "Date" for date picker mode
 * - items/options: Array or @{variable} for dropdown options
 * - datePickerMode: "date" | "time" | "dateAndTime"
 * - datePickerStyle: "wheels" | "inline" | "compact" | "graphical"
 * - dateFormat/dateStringFormat: Date format pattern
 * - minuteInterval: Integer interval for time picker
 * - minimumDate/maximumDate: Date range constraints
 * - hint/placeholder: Placeholder text
 * - enabled/disabled: Boolean state
 * - background/borderColor/fontColor/hintColor: Colors
 * - cornerRadius: Float corner radius
 * - fontSize: Float font size
 * - font: Font weight string (bold, semibold, medium, light, thin)
 * - cancelButtonBackgroundColor/cancelButtonTextColor: Cancel button colors
 * - onValueChange: @{handler} for change callback
 * - Modifiers: testTag, margins, size, alpha, clickable, padding, alignment, weight
 */
class DynamicSelectBoxComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val isDatePicker = json.get("selectItemType")?.asString == "Date"

            if (isDatePicker) {
                createDatePicker(json, data)
            } else {
                createDropdown(json, data)
            }
        }

        // ── Dropdown SelectBox ──

        @Composable
        private fun createDropdown(json: JsonObject, data: Map<String, Any>) {
            val context = LocalContext.current

            // Parse binding variable: selectedItem > bind
            val bindingVariable = extractBindingVariable(json, "selectedItem")
                ?: extractBindingVariable(json, "bind")

            // Get current selected value
            val currentValue = if (bindingVariable != null) {
                data[bindingVariable]?.toString() ?: ""
            } else ""

            var selectedValue by remember(currentValue, bindingVariable, data) {
                mutableStateOf(currentValue)
            }

            LaunchedEffect(data, bindingVariable) {
                if (bindingVariable != null) {
                    selectedValue = data[bindingVariable]?.toString() ?: ""
                }
            }

            // Parse options
            val options = parseOptions(json, data)

            // Parse enabled state
            val isEnabled = when {
                json.get("disabled")?.asBoolean == true -> false
                json.has("enabled") -> ResourceResolver.resolveBoolean(json, "enabled", data, true)
                else -> true
            }

            // Parse placeholder
            val placeholder = json.get("hint")?.asString
                ?: json.get("placeholder")?.asString

            // Parse colors
            val backgroundColor = ColorParser.parseColorWithBinding(json, "background", data, context)
                ?: Color.White
            val borderColor = ColorParser.parseColorWithBinding(json, "borderColor", data, context)
                ?: Color(0xFFCCCCCC)
            val textColor = ColorParser.parseColorWithBinding(json, "fontColor", data, context)
                ?: Color.Black
            val hintColor = ColorParser.parseColorWithBinding(json, "hintColor", data, context)
                ?: Color(0xFF999999)

            val cornerRadius = json.get("cornerRadius")?.asInt ?: 8

            // Font styling
            val fontSize = json.get("fontSize")?.asFloat
            val fontWeight = parseFontWeight(json.get("font")?.asString)

            val cancelButtonBackgroundColor = ColorParser.parseColorWithBinding(
                json, "cancelButtonBackgroundColor", data, context
            )
            val cancelButtonTextColor = ColorParser.parseColorWithBinding(
                json, "cancelButtonTextColor", data, context
            )

            // Handle value change
            val viewId = json.get("id")?.asString ?: "selectbox"
            val onValueChange: (String) -> Unit = { newValue ->
                selectedValue = newValue

                // Update bound variable
                if (bindingVariable != null) {
                    @Suppress("UNCHECKED_CAST")
                    (data["updateData"] as? (Map<String, Any>) -> Unit)
                        ?.invoke(mapOf(bindingVariable to newValue))
                }

                // Call onValueChange handler if specified
                val handler = json.get("onValueChange")?.asString
                if (handler != null && ModifierBuilder.isBinding(handler)) {
                    ModifierBuilder.resolveEventHandler(handler, data, viewId, newValue)
                }
            }

            // Build modifier (default fill width)
            val modifier = ModifierBuilder.buildModifier(
                json, data, context = context, defaultFillMaxWidth = true
            )

            SelectBox(
                value = selectedValue,
                onValueChange = onValueChange,
                options = options,
                modifier = modifier,
                placeholder = placeholder,
                enabled = isEnabled,
                backgroundColor = backgroundColor,
                borderColor = borderColor,
                textColor = textColor,
                hintColor = hintColor,
                cornerRadius = cornerRadius,
                cancelButtonBackgroundColor = cancelButtonBackgroundColor ?: backgroundColor,
                cancelButtonTextColor = cancelButtonTextColor ?: textColor
            )
        }

        // ── Date Picker SelectBox ──

        @Composable
        private fun createDatePicker(json: JsonObject, data: Map<String, Any>) {
            val context = LocalContext.current

            // Parse binding variable: selectedDate > selectedItem > bind
            val bindingVariable = extractBindingVariable(json, "selectedDate")
                ?: extractBindingVariable(json, "selectedItem")
                ?: extractBindingVariable(json, "bind")

            // Get current value
            val currentValue = if (bindingVariable != null) {
                data[bindingVariable]?.toString() ?: ""
            } else ""

            var selectedDate by remember(currentValue, bindingVariable, data) {
                mutableStateOf(currentValue)
            }

            LaunchedEffect(data, bindingVariable) {
                if (bindingVariable != null) {
                    selectedDate = data[bindingVariable]?.toString() ?: ""
                }
            }

            // Parse date picker attributes
            val datePickerMode = json.get("datePickerMode")?.asString ?: "date"
            val datePickerStyle = json.get("datePickerStyle")?.asString ?: "compact"
            val dateFormat = json.get("dateFormat")?.asString
                ?: json.get("dateStringFormat")?.asString
                ?: "yyyy-MM-dd"
            val minuteInterval = json.get("minuteInterval")?.asInt ?: 1
            val minimumDate = json.get("minimumDate")?.asString
            val maximumDate = json.get("maximumDate")?.asString

            // Parse enabled state
            val isEnabled = when {
                json.get("disabled")?.asBoolean == true -> false
                json.has("enabled") -> ResourceResolver.resolveBoolean(json, "enabled", data, true)
                else -> true
            }

            // Parse placeholder
            val placeholder = json.get("hint")?.asString
                ?: json.get("placeholder")?.asString

            // Parse colors
            val backgroundColor = ColorParser.parseColorWithBinding(json, "background", data, context)
                ?: Color.White
            val borderColor = ColorParser.parseColorWithBinding(json, "borderColor", data, context)
                ?: Color(0xFFCCCCCC)
            val textColor = ColorParser.parseColorWithBinding(json, "fontColor", data, context)
                ?: Color.Black
            val hintColor = ColorParser.parseColorWithBinding(json, "hintColor", data, context)
                ?: Color(0xFF999999)

            val cornerRadius = json.get("cornerRadius")?.asInt ?: 8

            // Handle value change
            val viewId = json.get("id")?.asString ?: "selectbox"
            val onValueChange: (String) -> Unit = { newValue ->
                selectedDate = newValue

                // Update bound variable
                if (bindingVariable != null) {
                    @Suppress("UNCHECKED_CAST")
                    (data["updateData"] as? (Map<String, Any>) -> Unit)
                        ?.invoke(mapOf(bindingVariable to newValue))
                }

                // Call onValueChange handler if specified
                val handler = json.get("onValueChange")?.asString
                if (handler != null && ModifierBuilder.isBinding(handler)) {
                    ModifierBuilder.resolveEventHandler(handler, data, viewId, newValue)
                }
            }

            // Build modifier (default fill width for date pickers)
            val modifier = ModifierBuilder.buildModifier(
                json, data, context = context, defaultFillMaxWidth = true
            )

            DateSelectBox(
                value = selectedDate,
                onValueChange = onValueChange,
                datePickerMode = datePickerMode,
                datePickerStyle = datePickerStyle,
                dateFormat = dateFormat,
                minuteInterval = minuteInterval,
                minimumDate = minimumDate,
                maximumDate = maximumDate,
                modifier = modifier,
                placeholder = placeholder,
                enabled = isEnabled,
                backgroundColor = backgroundColor,
                borderColor = borderColor,
                textColor = textColor,
                hintColor = hintColor,
                cornerRadius = cornerRadius
            )
        }

        // ── Helpers ──

        private fun parseOptions(json: JsonObject, data: Map<String, Any>): List<String> {
            val optionsElement = json.get("items") ?: json.get("options")

            return when {
                optionsElement == null -> emptyList()
                optionsElement.isJsonArray -> {
                    optionsElement.asJsonArray.mapNotNull { element ->
                        when {
                            element.isJsonPrimitive -> element.asString
                            element.isJsonObject -> {
                                val obj = element.asJsonObject
                                obj.get("label")?.asString ?: obj.get("value")?.asString
                            }
                            else -> null
                        }
                    }
                }
                optionsElement.isJsonPrimitive && ModifierBuilder.isBinding(optionsElement.asString) -> {
                    val variable = ModifierBuilder.extractBindingProperty(optionsElement.asString)
                    if (variable != null) {
                        when (val options = data[variable]) {
                            is List<*> -> options.mapNotNull { it?.toString() }
                            is Array<*> -> options.mapNotNull { it?.toString() }
                            else -> emptyList()
                        }
                    } else {
                        emptyList()
                    }
                }
                else -> emptyList()
            }
        }

        private fun parseFontWeight(font: String?): FontWeight {
            return when (font?.lowercase()) {
                "bold" -> FontWeight.Bold
                "semibold" -> FontWeight.SemiBold
                "medium" -> FontWeight.Medium
                "light" -> FontWeight.Light
                "thin" -> FontWeight.Thin
                else -> FontWeight.Normal
            }
        }

        private fun extractBindingVariable(json: JsonObject, key: String): String? {
            val value = json.get(key)?.asString ?: return null
            return ModifierBuilder.extractBindingProperty(value)
        }
    }
}
