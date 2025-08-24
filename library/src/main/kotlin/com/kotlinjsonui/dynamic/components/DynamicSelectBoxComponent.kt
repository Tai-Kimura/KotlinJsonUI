package com.kotlinjsonui.dynamic.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Dynamic SelectBox Component Converter
 * Converts JSON to dropdown/date picker composable at runtime
 * 
 * Supported JSON attributes (matching Ruby implementation):
 * - selectedItem/bind: String with @{variable} for selected value binding
 * - items/options: Array or @{variable} for dropdown options
 * - selectItemType: "Date" for date picker mode
 * - datePickerMode: "date" | "time" | "dateAndTime" 
 * - datePickerStyle: Style variant for date picker
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
            
            // State for dropdown expansion
            var expanded by remember { mutableStateOf(false) }
            
            // Parse enabled state
            val isEnabled = when {
                json.get("disabled")?.asBoolean == true -> false
                json.get("enabled")?.asBoolean == false -> false
                else -> true
            }
            
            // Parse placeholder
            val placeholder = json.get("hint")?.asString 
                ?: json.get("placeholder")?.asString 
                ?: "Select an option"
            
            // Parse colors
            val backgroundColor = json.get("background")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { null }
            }
            
            val borderColor = json.get("borderColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { null }
            }
            
            val textColor = json.get("fontColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { null }
            }
            
            val hintColor = json.get("hintColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { null }
            }
            
            val cornerRadius = json.get("cornerRadius")?.asFloat ?: 4f
            
            // Build modifier
            var modifier = buildModifier(json)
            
            // Apply background
            backgroundColor?.let {
                modifier = modifier.background(it, RoundedCornerShape(cornerRadius.dp))
            }
            
            // Apply border
            borderColor?.let {
                val borderWidth = json.get("borderWidth")?.asFloat ?: 1f
                modifier = modifier.border(borderWidth.dp, it, RoundedCornerShape(cornerRadius.dp))
            }
            
            // Apply clip for corner radius
            if (cornerRadius > 0) {
                modifier = modifier.clip(RoundedCornerShape(cornerRadius.dp))
            }
            
            // Create the dropdown
            Box(modifier = modifier) {
                // Trigger button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = isEnabled) { expanded = !expanded }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedValue.isEmpty()) placeholder else selectedValue,
                        color = if (selectedValue.isEmpty()) {
                            hintColor ?: MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            textColor ?: MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown arrow",
                        tint = textColor ?: MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Dropdown menu
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    text = option,
                                    color = textColor ?: MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                selectedValue = option
                                expanded = false
                                
                                // Update bound variable
                                if (bindingVariable != null) {
                                    val updateData = data["updateData"]
                                    if (updateData is Function<*>) {
                                        try {
                                            @Suppress("UNCHECKED_CAST")
                                            (updateData as (Map<String, Any>) -> Unit)(
                                                mapOf(bindingVariable to option)
                                            )
                                        } catch (e: Exception) {
                                            // Update function doesn't match expected signature
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
        
        @Composable
        private fun createDatePicker(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current
            val calendar = Calendar.getInstance()
            
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
            
            // Parse date format
            val dateFormatPattern = json.get("dateFormat")?.asString 
                ?: json.get("dateStringFormat")?.asString 
                ?: "yyyy-MM-dd"
            val dateFormat = SimpleDateFormat(dateFormatPattern, Locale.getDefault())
            
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
            
            // Parse date picker mode
            val datePickerMode = json.get("datePickerMode")?.asString ?: "date"
            
            // Parse enabled state
            val isEnabled = when {
                json.get("disabled")?.asBoolean == true -> false
                json.get("enabled")?.asBoolean == false -> false
                else -> true
            }
            
            // Parse placeholder
            val placeholder = json.get("hint")?.asString 
                ?: json.get("placeholder")?.asString 
                ?: "Select date"
            
            // Parse colors
            val backgroundColor = json.get("background")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { null }
            }
            
            val borderColor = json.get("borderColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { null }
            }
            
            val textColor = json.get("fontColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { null }
            }
            
            val hintColor = json.get("hintColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { null }
            }
            
            val cornerRadius = json.get("cornerRadius")?.asFloat ?: 4f
            
            // Build modifier
            var modifier = buildModifier(json)
            
            // Apply background
            backgroundColor?.let {
                modifier = modifier.background(it, RoundedCornerShape(cornerRadius.dp))
            }
            
            // Apply border
            borderColor?.let {
                val borderWidth = json.get("borderWidth")?.asFloat ?: 1f
                modifier = modifier.border(borderWidth.dp, it, RoundedCornerShape(cornerRadius.dp))
            }
            
            // Apply clip for corner radius
            if (cornerRadius > 0) {
                modifier = modifier.clip(RoundedCornerShape(cornerRadius.dp))
            }
            
            // Date picker click handler
            val showDatePicker = {
                when (datePickerMode) {
                    "time" -> {
                        val minuteInterval = json.get("minuteInterval")?.asInt ?: 1
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                calendar.set(Calendar.MINUTE, minute)
                                selectedDate = dateFormat.format(calendar.time)
                                
                                // Update bound variable
                                if (bindingVariable != null) {
                                    val updateData = data["updateData"]
                                    if (updateData is Function<*>) {
                                        try {
                                            @Suppress("UNCHECKED_CAST")
                                            (updateData as (Map<String, Any>) -> Unit)(
                                                mapOf(bindingVariable to selectedDate)
                                            )
                                        } catch (e: Exception) {
                                            // Update function doesn't match expected signature
                                        }
                                    }
                                }
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    }
                    "dateAndTime" -> {
                        // First show date picker, then time picker
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                calendar.set(year, month, dayOfMonth)
                                
                                // Then show time picker
                                TimePickerDialog(
                                    context,
                                    { _, hourOfDay, minute ->
                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                        calendar.set(Calendar.MINUTE, minute)
                                        selectedDate = dateFormat.format(calendar.time)
                                        
                                        // Update bound variable
                                        if (bindingVariable != null) {
                                            val updateData = data["updateData"]
                                            if (updateData is Function<*>) {
                                                try {
                                                    @Suppress("UNCHECKED_CAST")
                                                    (updateData as (Map<String, Any>) -> Unit)(
                                                        mapOf(bindingVariable to selectedDate)
                                                    )
                                                } catch (e: Exception) {
                                                    // Update function doesn't match expected signature
                                                }
                                            }
                                        }
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    true
                                ).show()
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                    else -> { // "date" mode
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                calendar.set(year, month, dayOfMonth)
                                selectedDate = dateFormat.format(calendar.time)
                                
                                // Update bound variable
                                if (bindingVariable != null) {
                                    val updateData = data["updateData"]
                                    if (updateData is Function<*>) {
                                        try {
                                            @Suppress("UNCHECKED_CAST")
                                            (updateData as (Map<String, Any>) -> Unit)(
                                                mapOf(bindingVariable to selectedDate)
                                            )
                                        } catch (e: Exception) {
                                            // Update function doesn't match expected signature
                                        }
                                    }
                                }
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).apply {
                            // Apply minimum date if specified
                            json.get("minimumDate")?.asString?.let { minDateStr ->
                                try {
                                    val minDate = dateFormat.parse(minDateStr)
                                    minDate?.let { datePicker.minDate = it.time }
                                } catch (e: Exception) {
                                    // Invalid date format
                                }
                            }
                            
                            // Apply maximum date if specified
                            json.get("maximumDate")?.asString?.let { maxDateStr ->
                                try {
                                    val maxDate = dateFormat.parse(maxDateStr)
                                    maxDate?.let { datePicker.maxDate = it.time }
                                } catch (e: Exception) {
                                    // Invalid date format
                                }
                            }
                        }.show()
                    }
                }
            }
            
            // Create the date picker trigger
            Box(
                modifier = modifier
                    .clickable(enabled = isEnabled) { showDatePicker() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedDate.isEmpty()) placeholder else selectedDate,
                        color = if (selectedDate.isEmpty()) {
                            hintColor ?: MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            textColor ?: MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Date picker",
                        tint = textColor ?: MaterialTheme.colorScheme.onSurface
                    )
                }
            }
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
            
            // Apply padding
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
    }
}