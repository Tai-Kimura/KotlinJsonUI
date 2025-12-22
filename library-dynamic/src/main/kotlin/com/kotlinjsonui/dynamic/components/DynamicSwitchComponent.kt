package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.processDataBinding
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder

/**
 * Dynamic Switch Component Converter
 * Converts JSON to Switch composable at runtime
 *
 * Switch is the primary component name. Toggle is supported as an alias for backward compatibility.
 *
 * Supported JSON attributes (matching Ruby implementation):
 * - isOn: Boolean or @{variable} for checked state
 * - bind: @{variable} for two-way binding
 * - onValueChange: String method name for change handler
 * - enabled: Boolean to enable/disable
 * - onTintColor: String hex color for checked track
 * - thumbTintColor: String hex color for checked thumb
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 */
class DynamicSwitchComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Parse checked state with data binding
            val bindingVariable = when {
                json.get("isOn")?.isJsonPrimitive == true &&
                        json.get("isOn").asString.contains("@{") -> {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(json.get("isOn").asString)?.groupValues?.get(1)
                }

                json.get("bind")?.asString?.contains("@{") == true -> {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(json.get("bind").asString)?.groupValues?.get(1)
                }

                else -> null
            }

            // Get initial checked state
            val initialChecked = when {
                bindingVariable != null -> {
                    (data[bindingVariable] as? Boolean) ?: false
                }

                json.get("isOn")?.isJsonPrimitive == true -> {
                    when {
                        json.get("isOn").asJsonPrimitive.isBoolean ->
                            json.get("isOn").asBoolean

                        json.get("isOn").asJsonPrimitive.isString &&
                                !json.get("isOn").asString.contains("@{") ->
                            json.get("isOn").asString.toBoolean()

                        else -> false
                    }
                }

                else -> false
            }

            // State for the switch
            var checked by remember(initialChecked, bindingVariable, data) {
                mutableStateOf(
                    if (bindingVariable != null) {
                        (data[bindingVariable] as? Boolean) ?: false
                    } else {
                        initialChecked
                    }
                )
            }

            // Update checked state when data changes
            LaunchedEffect(data, bindingVariable) {
                if (bindingVariable != null) {
                    checked = (data[bindingVariable] as? Boolean) ?: false
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

            // Handle value change
            val onCheckedChange: (Boolean) -> Unit = { newValue ->
                checked = newValue

                // Update bound variable if data binding is used
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

                // Also call custom handler if specified
                // onValueChange (camelCase) -> binding format only (@{functionName})
                json.get("onValueChange")?.asString?.let { onValueChangeValue ->
                    // Must be binding format
                    if (onValueChangeValue.contains("@{")) {
                        val pattern = "@\\{([^}]+)\\}".toRegex()
                        val methodName = pattern.find(onValueChangeValue)?.groupValues?.get(1)
                        methodName?.let { name ->
                            val handler = data[name]
                            if (handler is Function<*>) {
                                try {
                                    @Suppress("UNCHECKED_CAST")
                                    (handler as (Boolean) -> Unit)(newValue)
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
                        }
                    }
                }
            }

            // Parse colors
            val checkedTrackColor = json.get("onTintColor")?.asString?.let {
                ColorParser.parseColorString(it)
            }

            val checkedThumbColor = json.get("thumbTintColor")?.asString?.let {
                ColorParser.parseColorString(it)
            }

            val colors = if (checkedTrackColor != null || checkedThumbColor != null) {
                SwitchDefaults.colors(
                    checkedTrackColor = checkedTrackColor
                        ?: SwitchDefaults.colors().checkedTrackColor,
                    checkedThumbColor = checkedThumbColor
                        ?: SwitchDefaults.colors().checkedThumbColor
                )
            } else {
                SwitchDefaults.colors()
            }

            // Build modifier
            val modifier = buildModifier(json)

            // Create the Switch
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = modifier,
                enabled = isEnabled,
                colors = colors
            )
        }

        private fun buildModifier(json: JsonObject): Modifier {
            var modifier: Modifier = Modifier

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
            json.get("paddings")?.let { paddingsElement ->
                if (paddingsElement.isJsonPrimitive && paddingsElement.asJsonPrimitive.isNumber) {
                    modifier = modifier.padding(paddingsElement.asFloat.dp)
                } else if (paddingsElement.isJsonArray) {
                    val paddings = paddingsElement.asJsonArray
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
                }
            } ?: json.get("padding")?.asFloat?.let { padding ->
                modifier = modifier.padding(padding.dp)
            }

            return modifier
        }
    }
}
