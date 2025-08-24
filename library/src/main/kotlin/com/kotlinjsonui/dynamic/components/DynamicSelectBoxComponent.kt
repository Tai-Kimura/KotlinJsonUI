package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.kotlinjsonui.components.SelectBox
import com.kotlinjsonui.components.DateSelectBox

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
            
            // Parse colors
            val backgroundColor = json.get("background")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { Color.White }
            } ?: Color.White
            
            val borderColor = json.get("borderColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { Color(0xFFCCCCCC) }
            } ?: Color(0xFFCCCCCC)
            
            val textColor = json.get("fontColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { Color.Black }
            } ?: Color.Black
            
            val hintColor = json.get("hintColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { Color(0xFF999999) }
            } ?: Color(0xFF999999)
            
            val cornerRadius = json.get("cornerRadius")?.asInt ?: 8
            
            val cancelButtonBackgroundColor = json.get("cancelButtonBackgroundColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { null }
            }
            
            val cancelButtonTextColor = json.get("cancelButtonTextColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { null }
            }
            
            // Build modifier
            val modifier = buildModifier(json)
            
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
            
            // Parse colors
            val backgroundColor = json.get("background")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { Color.White }
            } ?: Color.White
            
            val borderColor = json.get("borderColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { Color(0xFFCCCCCC) }
            } ?: Color(0xFFCCCCCC)
            
            val textColor = json.get("fontColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { Color.Black }
            } ?: Color.Black
            
            val hintColor = json.get("hintColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { Color(0xFF999999) }
            } ?: Color(0xFF999999)
            
            val cornerRadius = json.get("cornerRadius")?.asInt ?: 8
            
            // Build modifier
            val modifier = buildModifier(json)
            
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
        
        private fun buildModifier(json: JsonObject): Modifier {
            var modifier: Modifier = Modifier
            
            // Width
            json.get("width")?.asFloat?.let { width ->
                modifier = if (width < 0) {
                    modifier.fillMaxWidth()
                } else {
                    modifier.width(width.dp)
                }
            } ?: run {
                // Default to fill width if not specified
                modifier = modifier.fillMaxWidth()
            }
            
            // Height
            json.get("height")?.asFloat?.let { height ->
                modifier = if (height < 0) {
                    modifier.fillMaxHeight()
                } else {
                    modifier.height(height.dp)
                }
            }
            
            // Apply margins first
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
            val leftMargin = json.get("leftMargin")?.asFloat 
                ?: json.get("startMargin")?.asFloat ?: 0f
            val rightMargin = json.get("rightMargin")?.asFloat 
                ?: json.get("endMargin")?.asFloat ?: 0f
            
            if (topMargin > 0 || bottomMargin > 0 || leftMargin > 0 || rightMargin > 0) {
                modifier = modifier.padding(
                    top = topMargin.dp,
                    bottom = bottomMargin.dp,
                    start = leftMargin.dp,
                    end = rightMargin.dp
                )
            }
            
            // Note: Padding is handled internally by the SelectBox/DateSelectBox components
            // So we don't apply padding to the modifier here
            
            return modifier
        }
    }
}