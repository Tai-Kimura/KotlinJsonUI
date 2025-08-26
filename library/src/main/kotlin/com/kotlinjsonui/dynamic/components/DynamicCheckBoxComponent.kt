package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.processDataBinding
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder

/**
 * Dynamic CheckBox Component Converter
 * Converts JSON to CheckBox composable at runtime
 *
 * Supported JSON attributes (matching Ruby implementation):
 * - bind: @{variable} for two-way binding
 * - label/text: String label text to display next to checkbox
 * - onValueChange: String method name for change handler
 * - enabled: Boolean to enable/disable
 * - checkColor: String hex color for checked state
 * - uncheckedColor: String hex color for unchecked state
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 */
class DynamicCheckBoxComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Parse binding variable
            val bindingVariable = json.get("bind")?.asString?.let { bind ->
                if (bind.contains("@{")) {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(bind)?.groupValues?.get(1)
                } else null
            }

            // Get initial checked state
            val initialChecked = if (bindingVariable != null) {
                (data[bindingVariable] as? Boolean) ?: false
            } else {
                false
            }

            // State for the checkbox
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
                json.get("onValueChange")?.asString?.let { methodName ->
                    val handler = data[methodName]
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

            // Parse colors
            val checkedColor = json.get("checkColor")?.asString?.let {
                try {
                    Color(android.graphics.Color.parseColor(it))
                } catch (e: Exception) {
                    null
                }
            }

            val uncheckedColor = json.get("uncheckedColor")?.asString?.let {
                try {
                    Color(android.graphics.Color.parseColor(it))
                } catch (e: Exception) {
                    null
                }
            }

            val colors = CheckboxDefaults.colors()

            // Check if we have a label
            val labelText = json.get("label")?.asString ?: json.get("text")?.asString

            if (labelText != null) {
                // Checkbox with label - create a Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = buildModifier(json)
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = onCheckedChange,
                        enabled = isEnabled,
                        colors = colors
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = processDataBinding(labelText, data))
                }
            } else {
                // Checkbox without label
                Checkbox(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    modifier = buildModifier(json),
                    enabled = isEnabled,
                    colors = colors
                )
            }
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