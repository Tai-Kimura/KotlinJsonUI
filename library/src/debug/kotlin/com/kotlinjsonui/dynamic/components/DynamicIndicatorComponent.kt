package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ColorParser

/**
 * Dynamic Indicator Component Converter
 * Converts JSON to Indicator (activity/loading indicator) composable at runtime
 * 
 * Supported JSON attributes (matching Ruby implementation):
 * - style: "linear" | "small" | "medium" | "large" for indicator style
 * - animating: Boolean or @{variable} to control visibility
 * - size: Number for custom size
 * - color: String hex color for indicator
 * - trackColor: String hex color for track (linear only)
 * - strokeWidth: Float width for circular stroke
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 * 
 * Note: Always shows indeterminate progress indicator
 */
class DynamicIndicatorComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Parse animating state
            val animatingElement = json.get("animating")
            val isAnimating = when {
                animatingElement?.isJsonPrimitive == true -> {
                    val primitive = animatingElement.asJsonPrimitive
                    when {
                        primitive.isBoolean -> primitive.asBoolean
                        primitive.isString && primitive.asString.contains("@{") -> {
                            // Extract variable name from @{variable}
                            val pattern = "@\\{([^}]+)\\}".toRegex()
                            val variable = pattern.find(primitive.asString)?.groupValues?.get(1)
                            (data[variable] as? Boolean) ?: true
                        }
                        else -> true
                    }
                }
                else -> true // Default to showing if not specified
            }
            
            // Only show indicator if animating is true
            if (!isAnimating) return
            
            // Parse style
            val style = json.get("style")?.asString ?: "medium"
            
            // Parse colors
            val color = ColorParser.parseColor(json, "color")
            val trackColor = ColorParser.parseColor(json, "trackColor")
            
            // Build base modifier
            var modifier = ModifierBuilder.buildModifier(json)
            
            // Apply size based on style
            when (style) {
                "large" -> modifier = modifier.size(48.dp)
                "small" -> modifier = modifier.size(16.dp)
                else -> {
                    // Check for custom size
                    json.get("size")?.asFloat?.let { size ->
                        modifier = modifier.size(size.dp)
                    }
                }
            }
            
            // Parse stroke width for circular indicator
            val strokeWidth = json.get("strokeWidth")?.asFloat?.dp
            
            // Create the appropriate indicator
            if (style == "linear") {
                LinearProgressIndicator(
                    modifier = modifier,
                    color = color ?: MaterialTheme.colorScheme.primary,
                    trackColor = trackColor ?: MaterialTheme.colorScheme.surfaceVariant
                )
            } else {
                // Circular indicator for all other styles
                if (strokeWidth != null) {
                    CircularProgressIndicator(
                        modifier = modifier,
                        color = color ?: MaterialTheme.colorScheme.primary,
                        trackColor = trackColor ?: MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = strokeWidth
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = modifier,
                        color = color ?: MaterialTheme.colorScheme.primary,
                        trackColor = trackColor ?: MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}