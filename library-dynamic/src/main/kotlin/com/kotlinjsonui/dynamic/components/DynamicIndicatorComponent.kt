package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver

/**
 * Dynamic Indicator Component Converter
 * Converts JSON to activity/loading indicator composable at runtime.
 * Reference: indicator_component.rb in kjui_tools.
 *
 * Supported JSON attributes:
 * - style: "linear" | "small" | "medium" | "large" for indicator style
 * - animating: Boolean or @{variable} to control visibility (wraps in if-condition)
 * - size: Number for custom size (when style is not large/small)
 * - color: String hex color for indicator
 * - trackColor: String hex color for track (linear only)
 * - strokeWidth: Float width for circular stroke
 * - Modifiers: testTag, size-by-style, margins, alpha, clickable, padding, alignment, weight
 *
 * Note: Always shows indeterminate progress indicator.
 */
class DynamicIndicatorComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap(),
            parentType: String? = null
        ) {
            val context = LocalContext.current

            // Parse animating state (supports @{binding})
            val isAnimating = ResourceResolver.resolveBoolean(json, "animating", data, default = true)

            // Only show indicator if animating is true
            if (!isAnimating) return

            // Parse style
            val style = json.get("style")?.asString ?: "medium"

            // Build base modifier with testTag first
            var modifier: Modifier = Modifier
            modifier = ModifierBuilder.applyTestTag(modifier, json)

            // Apply size based on style (before other modifiers, matching Ruby order)
            when (style) {
                "large" -> modifier = modifier.size(48.dp)
                "small" -> modifier = modifier.size(16.dp)
                else -> {
                    // Check for custom size attribute
                    ResourceResolver.resolveFloat(json, "size", data)?.let { customSize ->
                        modifier = modifier.size(customSize.dp)
                    }
                }
            }

            // Continue modifier chain: margins -> alpha -> clickable -> padding -> alignment -> weight
            modifier = ModifierBuilder.applyMargins(modifier, json, data)
            modifier = ModifierBuilder.applyAlpha(modifier, json, data)
            modifier = ModifierBuilder.applyClickable(modifier, json, data)
            modifier = ModifierBuilder.applyPadding(modifier, json)
            modifier = ModifierBuilder.applyAlignment(modifier, json, parentType)

            // Apply weight if in Row or Column
            val weight = ModifierBuilder.getWeight(json)
            // Weight is applied via parentType-aware buildModifier; handled by caller or RowScope/ColumnScope

            // Parse colors (supports @{binding})
            val color = ColorParser.parseColorWithBinding(json, "color", data, context)
            val trackColor = ColorParser.parseColorWithBinding(json, "trackColor", data, context)

            // Parse stroke width for circular indicator
            val strokeWidth = ResourceResolver.resolveFloat(json, "strokeWidth", data)?.dp

            // Lifecycle effects
            ModifierBuilder.ApplyLifecycleEffects(json, data)

            // Create the appropriate indicator
            if (style == "linear") {
                LinearProgressIndicator(
                    modifier = modifier,
                    color = color ?: MaterialTheme.colorScheme.primary,
                    trackColor = trackColor ?: MaterialTheme.colorScheme.surfaceVariant
                )
            } else {
                // Circular indicator for all other styles (small, medium, large, default)
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
