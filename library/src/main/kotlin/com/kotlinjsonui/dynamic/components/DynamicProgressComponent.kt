package com.kotlinjsonui.dynamic.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder

/**
 * Dynamic Progress Component Converter
 * Converts JSON to ProgressBar/CircularProgressIndicator composable at runtime
 * 
 * Supported JSON attributes (matching Ruby implementation):
 * - value/bind: Float or @{variable} for progress value (0.0 to 1.0)
 * - style: "linear" | "circular" | "large" for progress indicator style
 * - progressTintColor: String hex color for progress track
 * - trackTintColor: String hex color for background track
 * - width/height: Number dimensions
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 * 
 * Note: If value/bind is not provided, shows indeterminate progress
 */
class DynamicProgressComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Check if this is determinate or indeterminate progress
            val hasValue = json.has("value") || json.has("bind")
            
            // Parse binding variable for progress value
            val bindingVariable = when {
                json.get("bind")?.asString?.contains("@{") == true -> {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(json.get("bind").asString)?.groupValues?.get(1)
                }
                json.get("value")?.asString?.contains("@{") == true -> {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(json.get("value").asString)?.groupValues?.get(1)
                }
                else -> null
            }
            
            // Get progress value
            val progressValue = when {
                bindingVariable != null -> {
                    when (val boundValue = data[bindingVariable]) {
                        is Number -> boundValue.toFloat()
                        is String -> boundValue.toFloatOrNull() ?: 0f
                        else -> 0f
                    }
                }
                json.get("value")?.isJsonPrimitive == true -> {
                    val valueElement = json.get("value")
                    when {
                        valueElement.asJsonPrimitive.isNumber -> valueElement.asFloat
                        valueElement.asJsonPrimitive.isString && 
                        !valueElement.asString.contains("@{") -> 
                            valueElement.asString.toFloatOrNull() ?: 0f
                        else -> 0f
                    }
                }
                else -> 0f
            }
            
            // State for the progress value
            var progress by remember(progressValue, bindingVariable, data) { 
                mutableStateOf(
                    if (bindingVariable != null) {
                        when (val boundValue = data[bindingVariable]) {
                            is Number -> boundValue.toFloat().coerceIn(0f, 1f)
                            is String -> boundValue.toFloatOrNull()?.coerceIn(0f, 1f) ?: 0f
                            else -> 0f
                        }
                    } else if (hasValue) {
                        progressValue.coerceIn(0f, 1f)
                    } else {
                        0f
                    }
                )
            }
            
            // Update value when data changes
            LaunchedEffect(data, bindingVariable) {
                if (bindingVariable != null) {
                    progress = when (val boundValue = data[bindingVariable]) {
                        is Number -> boundValue.toFloat().coerceIn(0f, 1f)
                        is String -> boundValue.toFloatOrNull()?.coerceIn(0f, 1f) ?: 0f
                        else -> 0f
                    }
                }
            }
            
            // Parse style
            val style = json.get("style")?.asString ?: "linear"
            
            // Parse colors
            val progressColor = json.get("progressTintColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { null }
            }
            
            val trackColor = json.get("trackTintColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { null }
            }
            
            // Build modifier using helper
            val modifier = ModifierBuilder.buildModifier(json)
            
            // Create the appropriate progress indicator
            when {
                hasValue -> {
                    // Determinate progress
                    if (style == "circular" || style == "large") {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = modifier,
                            color = progressColor ?: MaterialTheme.colorScheme.primary,
                            trackColor = trackColor ?: MaterialTheme.colorScheme.surfaceVariant
                        )
                    } else {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = modifier,
                            color = progressColor ?: MaterialTheme.colorScheme.primary,
                            trackColor = trackColor ?: MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
                else -> {
                    // Indeterminate progress
                    if (style == "circular" || style == "large") {
                        CircularProgressIndicator(
                            modifier = modifier,
                            color = progressColor ?: MaterialTheme.colorScheme.primary,
                            trackColor = trackColor ?: MaterialTheme.colorScheme.surfaceVariant
                        )
                    } else {
                        LinearProgressIndicator(
                            modifier = modifier,
                            color = progressColor ?: MaterialTheme.colorScheme.primary,
                            trackColor = trackColor ?: MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }
    }
}