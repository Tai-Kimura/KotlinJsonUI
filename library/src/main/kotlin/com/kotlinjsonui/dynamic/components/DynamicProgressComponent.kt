package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject

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
            
            // Build modifier
            val modifier = buildModifier(json)
            
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
        
        private fun buildModifier(json: JsonObject): Modifier {
            var modifier: Modifier = Modifier
            
            // Width
            json.get("width")?.asFloat?.let { width ->
                modifier = if (width < 0) {
                    modifier.fillMaxWidth()
                } else {
                    modifier.width(width.dp)
                }
            }
            
            // Height
            json.get("height")?.asFloat?.let { height ->
                modifier = modifier.height(height.dp)
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