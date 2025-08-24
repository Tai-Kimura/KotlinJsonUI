package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import kotlin.math.roundToInt

/**
 * Dynamic Slider Component Converter
 * Converts JSON to Slider composable at runtime
 * 
 * Supported JSON attributes (matching Ruby implementation):
 * - value: Float or @{variable} for current value
 * - bind: @{variable} for two-way binding
 * - minimumValue/min: Float minimum value (default 0)
 * - maximumValue/max: Float maximum value (default 100)
 * - step: Float step size for discrete slider
 * - onValueChange: String method name for change handler
 * - enabled: Boolean to enable/disable
 * - minimumTrackTintColor: String hex color for active track
 * - maximumTrackTintColor: String hex color for inactive track
 * - thumbTintColor: String hex color for thumb
 * - width/height: Number dimensions
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 */
class DynamicSliderComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Parse binding variable
            val bindingVariable = when {
                json.get("value")?.isJsonPrimitive == true && 
                json.get("value").asString.contains("@{") -> {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(json.get("value").asString)?.groupValues?.get(1)
                }
                json.get("bind")?.asString?.contains("@{") == true -> {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(json.get("bind").asString)?.groupValues?.get(1)
                }
                else -> null
            }
            
            // Parse min/max values
            val minValue = (json.get("minimumValue") ?: json.get("min"))?.asFloat ?: 0f
            val maxValue = (json.get("maximumValue") ?: json.get("max"))?.asFloat ?: 100f
            
            // Get initial value
            val initialValue = when {
                bindingVariable != null -> {
                    when (val boundValue = data[bindingVariable]) {
                        is Number -> boundValue.toFloat()
                        is String -> boundValue.toFloatOrNull() ?: minValue
                        else -> minValue
                    }
                }
                json.get("value")?.isJsonPrimitive == true -> {
                    val valueElement = json.get("value")
                    when {
                        valueElement.asJsonPrimitive.isNumber -> valueElement.asFloat
                        valueElement.asJsonPrimitive.isString && 
                        !valueElement.asString.contains("@{") -> 
                            valueElement.asString.toFloatOrNull() ?: minValue
                        else -> minValue
                    }
                }
                else -> minValue
            }
            
            // State for the slider value
            var sliderValue by remember(initialValue, bindingVariable, data) { 
                mutableStateOf(
                    if (bindingVariable != null) {
                        when (val boundValue = data[bindingVariable]) {
                            is Number -> boundValue.toFloat().coerceIn(minValue, maxValue)
                            is String -> boundValue.toFloatOrNull()?.coerceIn(minValue, maxValue) ?: minValue
                            else -> minValue
                        }
                    } else {
                        initialValue.coerceIn(minValue, maxValue)
                    }
                )
            }
            
            // Update value when data changes
            LaunchedEffect(data, bindingVariable) {
                if (bindingVariable != null) {
                    sliderValue = when (val boundValue = data[bindingVariable]) {
                        is Number -> boundValue.toFloat().coerceIn(minValue, maxValue)
                        is String -> boundValue.toFloatOrNull()?.coerceIn(minValue, maxValue) ?: minValue
                        else -> minValue
                    }
                }
            }
            
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
            
            // Calculate steps if specified
            val steps = json.get("step")?.asFloat?.let { step ->
                if (step > 0) {
                    ((maxValue - minValue) / step).roundToInt() - 1
                } else 0
            } ?: 0
            
            // Handle value change
            val onValueChange: (Float) -> Unit = { newValue ->
                sliderValue = newValue
                
                // Call custom handler if specified
                json.get("onValueChange")?.asString?.let { methodName ->
                    val handler = data[methodName]
                    if (handler is Function<*>) {
                        try {
                            @Suppress("UNCHECKED_CAST")
                            (handler as (Float) -> Unit)(newValue)
                        } catch (e: Exception) {
                            // Try with Double
                            try {
                                @Suppress("UNCHECKED_CAST")
                                (handler as (Double) -> Unit)(newValue.toDouble())
                            } catch (e2: Exception) {
                                // Try without parameter
                                try {
                                    @Suppress("UNCHECKED_CAST")
                                    (handler as () -> Unit)()
                                } catch (e3: Exception) {
                                    // Handler doesn't match expected signature
                                }
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
                                    mapOf(bindingVariable to newValue.toDouble())
                                )
                            } catch (e: Exception) {
                                // Update function doesn't match expected signature
                            }
                        }
                    }
                }
            }
            
            // Parse colors
            val thumbColor = json.get("thumbTintColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { null }
            }
            
            val activeTrackColor = json.get("minimumTrackTintColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { null }
            }
            
            val inactiveTrackColor = json.get("maximumTrackTintColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { null }
            }
            
            val colors = if (thumbColor != null || activeTrackColor != null || inactiveTrackColor != null) {
                SliderDefaults.colors(
                    thumbColor = thumbColor ?: SliderDefaults.colors().thumbColor,
                    activeTrackColor = activeTrackColor ?: SliderDefaults.colors().activeTrackColor,
                    inactiveTrackColor = inactiveTrackColor ?: SliderDefaults.colors().inactiveTrackColor
                )
            } else {
                SliderDefaults.colors()
            }
            
            // Build modifier
            val modifier = buildModifier(json)
            
            // Create the Slider
            Slider(
                value = sliderValue,
                onValueChange = onValueChange,
                valueRange = minValue..maxValue,
                steps = steps,
                modifier = modifier,
                enabled = isEnabled,
                colors = colors
            )
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