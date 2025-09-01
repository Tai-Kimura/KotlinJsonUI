package com.kotlinjsonui.dynamic.components

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.google.gson.JsonObject
import com.kotlinjsonui.components.SelectBox
import com.kotlinjsonui.components.DateSelectBox
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ColorParser

/**
 * Dynamic SelectBox Component Converter
 * Converts JSON to dropdown/date picker composable at runtime
 * 
 * Supported JSON attributes (matching Ruby implementation):
 * - selectedItem/bind: String with @{variable} for selected value binding
 * - items/options: Array or @{variable} for dropdown options
 * - selectItemType: "Date" for date picker mode
 * - datePickerMode: "date" | "time" | "dateAndTime" 
 * - datePickerStyle: "wheels" | "inline" | "compact" | "graphical"
 * - dateFormat/dateStringFormat: Date format pattern
 * - minuteInterval: Integer interval for time picker
 * - minimumDate/maximumDate: Date range constraints
 * - hint/placeholder: String placeholder text
 * - enabled/disabled: Boolean state
 * - background: String hex color
 * - borderColor: String hex color
 * - fontColor: String text color
 * - hintColor: String placeholder color
 * - cornerRadius: Float corner radius
 * - cancelButtonBackgroundColor: String color for cancel button
 * - cancelButtonTextColor: String color for cancel button text
 * - width/height: Number dimensions
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 */
class DynamicSelectBoxComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Check if this is a date picker
            val isDatePicker = json.get("selectItemType")?.asString == "Date"
            
            if (isDatePicker) {
                createDatePicker(json, data)
            } else {
                createDropdown(json, data)
            }
        }
        
        @Composable
        private fun createDropdown(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Parse binding variable
            val bindingVariable = when {
                json.get("selectedItem")?.asString?.contains("@{") == true -> {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(json.get("selectedItem").asString)?.groupValues?.get(1)
                }
                json.get("bind")?.asString?.contains("@{") == true -> {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(json.get("bind").asString)?.groupValues?.get(1)
                }
                else -> null
            }
            
            // Get initial selected value
            val initialValue = when {
                bindingVariable != null -> {
                    data[bindingVariable]?.toString() ?: ""
                }
                else -> ""
            }
            
            // State for the selected value
            var selectedValue by remember(initialValue, bindingVariable, data) { 
                mutableStateOf(
                    if (bindingVariable != null) {
                        data[bindingVariable]?.toString() ?: ""
                    } else {
                        initialValue
                    }
                )
            }
            
            // Update value when data changes
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
                json.get("enabled")?.asBoolean == false -> false
                else -> true
            }
            
            // Parse placeholder
            val placeholder = json.get("hint")?.asString 
                ?: json.get("placeholder")?.asString
            
            // Parse colors using helper
            val colors = ColorParser.parseTextColors(json)
            val backgroundColor = colors.backgroundColor ?: Color.White
            val borderColor = colors.borderColor ?: Color(0xFFCCCCCC)
            val textColor = colors.textColor ?: Color.Black
            val hintColor = colors.hintColor ?: Color(0xFF999999)
            
            val cornerRadius = json.get("cornerRadius")?.asInt ?: 8
            
            val cancelButtonBackgroundColor = ColorParser.parseColor(json, "cancelButtonBackgroundColor")
            val cancelButtonTextColor = ColorParser.parseColor(json, "cancelButtonTextColor")
            
            // Build modifier using helper (defaulting to fill width)
            val modifier = ModifierBuilder.buildModifier(json, defaultFillMaxWidth = true)
            
            // Create the SelectBox using the existing component
            SelectBox(
                value = selectedValue,
                onValueChange = { newValue ->
                    selectedValue = newValue
                    
                    // Update bound variable
                    if (bindingVariable != null) {
                        val updateData = data["updateData"]
                        if (updateData is Function<*>) {
                            try {
                                @Suppress("UNCHECKED_CAST")
                                (updateData as (Map<String, Any>) -> Unit)(
                                    mapOf(bindingVariable to newValue)
                                )
                            } catch (e: Exception) {
                                // Update function doesn't match expected signature
                            }
                        }
                    }
                },
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
        
        @Composable
        private fun createDatePicker(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Parse binding variable
            val bindingVariable = when {
                json.get("selectedItem")?.asString?.contains("@{") == true -> {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(json.get("selectedItem").asString)?.groupValues?.get(1)
                }
                json.get("bind")?.asString?.contains("@{") == true -> {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(json.get("bind").asString)?.groupValues?.get(1)
                }
                else -> null
            }
            
            // Get initial value
            val initialValue = when {
                bindingVariable != null -> {
                    data[bindingVariable]?.toString() ?: ""
                }
                else -> ""
            }
            
            // State for the selected date
            var selectedDate by remember(initialValue, bindingVariable, data) { 
                mutableStateOf(
                    if (bindingVariable != null) {
                        data[bindingVariable]?.toString() ?: ""
                    } else {
                        initialValue
                    }
                )
            }
            
            // Update value when data changes
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
                json.get("enabled")?.asBoolean == false -> false
                else -> true
            }
            
            // Parse placeholder
            val placeholder = json.get("hint")?.asString 
                ?: json.get("placeholder")?.asString
            
            // Parse colors using helper
            val colors = ColorParser.parseTextColors(json)
            val backgroundColor = colors.backgroundColor ?: Color.White
            val borderColor = colors.borderColor ?: Color(0xFFCCCCCC)
            val textColor = colors.textColor ?: Color.Black
            val hintColor = colors.hintColor ?: Color(0xFF999999)
            
            val cornerRadius = json.get("cornerRadius")?.asInt ?: 8
            
            // Build modifier using helper (defaulting to fill width)
            val modifier = ModifierBuilder.buildModifier(json, defaultFillMaxWidth = true)
            
            // Create the DateSelectBox using the existing component
            DateSelectBox(
                value = selectedDate,
                onValueChange = { newValue ->
                    selectedDate = newValue
                    
                    // Update bound variable
                    if (bindingVariable != null) {
                        val updateData = data["updateData"]
                        if (updateData is Function<*>) {
                            try {
                                @Suppress("UNCHECKED_CAST")
                                (updateData as (Map<String, Any>) -> Unit)(
                                    mapOf(bindingVariable to newValue)
                                )
                            } catch (e: Exception) {
                                // Update function doesn't match expected signature
                            }
                        }
                    }
                },
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
                optionsElement.isJsonPrimitive && optionsElement.asString.contains("@{") -> {
                    // Dynamic options from data binding
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    val variable = pattern.find(optionsElement.asString)?.groupValues?.get(1)
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
    }
}