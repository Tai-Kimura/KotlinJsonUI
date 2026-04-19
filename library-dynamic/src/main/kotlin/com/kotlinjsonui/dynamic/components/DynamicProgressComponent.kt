package com.kotlinjsonui.dynamic.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver

/**
 * Dynamic Progress Component Converter
 * Converts JSON to ProgressBar/CircularProgressIndicator composable at runtime.
 * Reference: progress_component.rb in kjui_tools.
 *
 * Supported JSON attributes:
 * - value/bind: Float or @{variable} for progress value (0.0 to 1.0)
 * - style: "linear" | "circular" | "large" for indeterminate style
 * - progressTintColor: String hex color for progress color
 * - trackTintColor: String hex color for background track
 * - Modifiers: testTag, margins, size, alpha, clickable, padding, weight
 *
 * Note: If value/bind is present, always renders LinearProgressIndicator (determinate).
 *       If not present, style determines the indicator type (indeterminate).
 */
class DynamicProgressComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap(),
            parentType: String? = null
        ) {
            val context = LocalContext.current

            // Determine if this is determinate (has value/bind) or indeterminate
            val hasValue = json.has("value") || json.has("bind")

            // Parse binding variable from value or bind
            val bindingVariable = extractBindingVariable(json, "bind")
                ?: extractBindingVariable(json, "value")

            // Resolve progress value (coerced to 0..1)
            val progressValue = when {
                bindingVariable != null -> {
                    when (val boundValue = data[bindingVariable]) {
                        is Number -> boundValue.toFloat()
                        is String -> boundValue.toFloatOrNull() ?: 0f
                        else -> 0f
                    }
                }
                json.has("value") -> {
                    ResourceResolver.resolveFloat(json, "value", data, 0f) ?: 0f
                }
                else -> 0f
            }.coerceIn(0f, 1f)

            // State for the progress value
            var progress by remember(progressValue, bindingVariable, data) {
                mutableStateOf(progressValue)
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

            // Parse style (only used for indeterminate)
            val style = json.get("style")?.asString ?: "linear"

            // Parse colors (supports @{binding})
            val progressColor = ColorParser.parseColorWithBinding(json, "progressTintColor", data, context)
            val trackColor = ColorParser.parseColorWithBinding(json, "trackTintColor", data, context)

            // Build modifier: testTag -> margins -> size -> alpha -> clickable -> padding -> weight
            val modifier = ModifierBuilder.buildModifier(json, data, parentType, context)

            // Lifecycle effects
            ModifierBuilder.ApplyLifecycleEffects(json, data)

            when {
                hasValue -> {
                    // Determinate: always LinearProgressIndicator with progress lambda
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = modifier,
                        color = progressColor ?: MaterialTheme.colorScheme.primary,
                        trackColor = trackColor ?: MaterialTheme.colorScheme.surfaceVariant
                    )
                }
                else -> {
                    // Indeterminate: style determines component type
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

        private fun extractBindingVariable(json: JsonObject, key: String): String? {
            val value = json.get(key)?.asString ?: return null
            return ModifierBuilder.extractBindingProperty(value)
        }
    }
}
