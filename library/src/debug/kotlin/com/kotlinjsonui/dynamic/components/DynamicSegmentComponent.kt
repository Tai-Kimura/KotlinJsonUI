package com.kotlinjsonui.dynamic.components

import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.google.gson.JsonObject
import com.kotlinjsonui.components.Segment
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ColorParser

/**
 * Dynamic Segment Component Converter
 * Converts JSON to Segment (TabRow) composable at runtime
 * 
 * Supported JSON attributes (matching Ruby implementation):
 * - selectedIndex/bind: Integer or @{variable} for selected tab index
 * - items/segments: Array of segment titles or @{variable} for dynamic segments
 * - enabled: Boolean or @{variable} to enable/disable
 * - backgroundColor: String hex color for container
 * - normalColor: String hex color for unselected text
 * - selectedColor/tintColor/selectedSegmentTintColor: String hex color for selected text
 * - indicatorColor: String hex color for indicator
 * - onValueChange: String method name for selection change handler
 * - width/height: Number dimensions
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 */
class DynamicSegmentComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Parse binding variable for selected index
            val bindingVariable = when {
                json.get("selectedIndex")?.asString?.contains("@{") == true -> {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(json.get("selectedIndex").asString)?.groupValues?.get(1)
                }
                json.get("bind")?.asString?.contains("@{") == true -> {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(json.get("bind").asString)?.groupValues?.get(1)
                }
                else -> null
            }
            
            // Get selected index
            val initialIndex = when {
                bindingVariable != null -> {
                    when (val boundValue = data[bindingVariable]) {
                        is Number -> boundValue.toInt()
                        is String -> boundValue.toIntOrNull() ?: 0
                        else -> 0
                    }
                }
                json.get("selectedIndex")?.isJsonPrimitive == true -> {
                    val indexElement = json.get("selectedIndex")
                    when {
                        indexElement.asJsonPrimitive.isNumber -> indexElement.asInt
                        indexElement.asJsonPrimitive.isString && 
                        !indexElement.asString.contains("@{") -> 
                            indexElement.asString.toIntOrNull() ?: 0
                        else -> 0
                    }
                }
                else -> 0
            }
            
            // State for selected index
            var selectedIndex by remember(initialIndex, bindingVariable, data) { 
                mutableStateOf(
                    if (bindingVariable != null) {
                        when (val boundValue = data[bindingVariable]) {
                            is Number -> boundValue.toInt()
                            is String -> boundValue.toIntOrNull() ?: 0
                            else -> 0
                        }
                    } else {
                        initialIndex
                    }
                )
            }
            
            // Update value when data changes
            LaunchedEffect(data, bindingVariable) {
                if (bindingVariable != null) {
                    selectedIndex = when (val boundValue = data[bindingVariable]) {
                        is Number -> boundValue.toInt()
                        is String -> boundValue.toIntOrNull() ?: 0
                        else -> 0
                    }
                }
            }
            
            // Parse segments
            val segments = parseSegments(json, data)
            
            // Parse enabled state
            val isEnabled = when {
                json.get("enabled")?.isJsonPrimitive == true -> {
                    val enabledValue = json.get("enabled")
                    when {
                        enabledValue.asJsonPrimitive.isBoolean -> enabledValue.asBoolean
                        enabledValue.asJsonPrimitive.isString && 
                        enabledValue.asString.contains("@{") -> {
                            val pattern = "@\\{([^}]+)\\}".toRegex()
                            val variable = pattern.find(enabledValue.asString)?.groupValues?.get(1)
                            (data[variable] as? Boolean) ?: true
                        }
                        else -> true
                    }
                }
                else -> true
            }
            
            // Parse colors
            val backgroundColor = ColorParser.parseColor(json, "backgroundColor")
            val normalColor = ColorParser.parseColor(json, "normalColor")
            val selectedColor = ColorParser.parseColor(json, "selectedColor")
                ?: ColorParser.parseColor(json, "tintColor")
                ?: ColorParser.parseColor(json, "selectedSegmentTintColor")
            val indicatorColor = ColorParser.parseColor(json, "indicatorColor")
            
            // Build modifier
            val modifier = ModifierBuilder.buildModifier(json)
            
            // Create the Segment using the existing component
            Segment(
                selectedTabIndex = selectedIndex,
                modifier = modifier,
                enabled = isEnabled,
                containerColor = backgroundColor,
                contentColor = normalColor,
                selectedContentColor = selectedColor,
                indicatorColor = indicatorColor
            ) {
                segments.forEachIndexed { index, segment ->
                    Tab(
                        selected = selectedIndex == index,
                        enabled = isEnabled,
                        onClick = {
                            selectedIndex = index
                            
                            // Call custom handler if specified
                            json.get("onValueChange")?.asString?.let { methodName ->
                                val handler = data[methodName]
                                if (handler is Function<*>) {
                                    try {
                                        @Suppress("UNCHECKED_CAST")
                                        (handler as (Int) -> Unit)(index)
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
                            } ?: run {
                                // Update bound variable if no custom handler
                                if (bindingVariable != null) {
                                    val updateData = data["updateData"]
                                    if (updateData is Function<*>) {
                                        try {
                                            @Suppress("UNCHECKED_CAST")
                                            (updateData as (Map<String, Any>) -> Unit)(
                                                mapOf(bindingVariable to index)
                                            )
                                        } catch (e: Exception) {
                                            // Update function doesn't match expected signature
                                        }
                                    }
                                }
                            }
                        },
                        text = {
                            Text(
                                text = segment,
                                color = if (selectedIndex == index) {
                                    selectedColor ?: Color.Unspecified
                                } else {
                                    normalColor ?: Color.Unspecified
                                }
                            )
                        }
                    )
                }
            }
        }
        
        private fun parseSegments(json: JsonObject, data: Map<String, Any>): List<String> {
            val segmentsElement = json.get("items") ?: json.get("segments")
            
            return when {
                segmentsElement == null -> emptyList()
                segmentsElement.isJsonArray -> {
                    segmentsElement.asJsonArray.mapNotNull { element ->
                        when {
                            element.isJsonPrimitive -> element.asString
                            else -> null
                        }
                    }
                }
                segmentsElement.isJsonPrimitive && segmentsElement.asString.contains("@{") -> {
                    // Dynamic segments from data binding
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    val variable = pattern.find(segmentsElement.asString)?.groupValues?.get(1)
                    if (variable != null) {
                        when (val segments = data[variable]) {
                            is List<*> -> segments.mapNotNull { it?.toString() }
                            is Array<*> -> segments.mapNotNull { it?.toString() }
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