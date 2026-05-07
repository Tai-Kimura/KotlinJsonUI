package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver

/**
 * Dynamic Switch Component Converter
 * Converts JSON to Switch composable at runtime.
 *
 * Reference: switch_component.rb in kjui_tools.
 *
 * Switch is the primary component name. Toggle is supported as an alias
 * for backward compatibility.
 *
 * State binding priority: isOn > value > checked > bind
 *
 * Supported JSON attributes:
 * - isOn/value/checked: Boolean or @{variable} for checked state
 * - bind: @{variable} for two-way binding (lowest priority)
 * - onValueChange/onToggle: @{functionName} for change handler (binding format only)
 * - enabled: Boolean or @{variable} to enable/disable
 * - onTintColor/tint/tintColor: String hex color for checked track
 * - thumbTintColor: String hex color for checked thumb
 * - labelAttributes: Object with text/fontSize/fontColor/font for labeled switch
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 */
class DynamicSwitchComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap(),
            parentType: String? = null
        ) {
            val hasLabel = json.has("labelAttributes")
            if (hasLabel) {
                createWithLabel(json, data, parentType)
            } else {
                createSwitchOnly(json, data, parentType)
            }
        }

        @Composable
        private fun createSwitchOnly(
            json: JsonObject,
            data: Map<String, Any>,
            parentType: String?
        ) {
            val context = LocalContext.current

            // Parse binding variable (priority: isOn > value > checked > bind)
            val bindingVariable = resolveBindingVariable(json)

            // Get checked state from binding or direct value
            val checked = resolveCheckedState(json, data, bindingVariable)

            // State for the switch
            var checkedState by remember(checked, bindingVariable, data) {
                mutableStateOf(checked)
            }

            // Update checked state when data changes
            LaunchedEffect(data, bindingVariable) {
                if (bindingVariable != null) {
                    checkedState = (data[bindingVariable] as? Boolean) ?: false
                }
            }

            // Enabled state (supports @{binding})
            val isEnabled = ResourceResolver.resolveBoolean(json, "enabled", data, default = true)

            // Build onCheckedChange handler
            val onCheckedChange = buildOnCheckedChange(json, data, bindingVariable) { newValue ->
                checkedState = newValue
            }

            // Build modifier: testTag, margins, alpha, padding
            var modifier = ModifierBuilder.buildModifier(json, data, parentType, context)

            // Apply weight if in Row or Column
            val weight = ModifierBuilder.getWeight(json)

            // Colors: onTintColor/tint/tintColor -> checkedTrackColor, thumbTintColor -> checkedThumbColor
            val checkedTrackColor = ColorParser.parseColorWithBinding(json, "onTintColor", data, context)
                ?: ColorParser.parseColorWithBinding(json, "tint", data, context)
                ?: ColorParser.parseColorWithBinding(json, "tintColor", data, context)
            val checkedThumbColor = ColorParser.parseColorWithBinding(json, "thumbTintColor", data, context)

            val colors = if (checkedTrackColor != null || checkedThumbColor != null) {
                SwitchDefaults.colors(
                    checkedTrackColor = checkedTrackColor ?: SwitchDefaults.colors().checkedTrackColor,
                    checkedThumbColor = checkedThumbColor ?: SwitchDefaults.colors().checkedThumbColor
                )
            } else {
                SwitchDefaults.colors()
            }

            Switch(
                checked = checkedState,
                onCheckedChange = onCheckedChange,
                modifier = modifier,
                enabled = isEnabled,
                colors = colors
            )
        }

        @Composable
        private fun createWithLabel(
            json: JsonObject,
            data: Map<String, Any>,
            parentType: String?
        ) {
            val context = LocalContext.current
            val labelAttrs = json.getAsJsonObject("labelAttributes")

            // Parse binding variable (priority: isOn > value > checked > bind)
            val bindingVariable = resolveBindingVariable(json)

            // Get checked state from binding or direct value
            val checked = resolveCheckedState(json, data, bindingVariable)

            // State for the switch
            var checkedState by remember(checked, bindingVariable, data) {
                mutableStateOf(checked)
            }

            // Update checked state when data changes
            LaunchedEffect(data, bindingVariable) {
                if (bindingVariable != null) {
                    checkedState = (data[bindingVariable] as? Boolean) ?: false
                }
            }

            // Build onCheckedChange handler
            val onCheckedChange = buildOnCheckedChange(json, data, bindingVariable) { newValue ->
                checkedState = newValue
            }

            // Build modifier for Row container: testTag, margins, alpha, padding
            val rowModifier = ModifierBuilder.buildModifier(json, data, parentType, context)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = rowModifier
            ) {
                // Label Text with weight(1f)
                val labelText = labelAttrs?.get("text")?.asString ?: ""
                val fontSize = labelAttrs?.get("fontSize")?.asFloat
                val fontColor = labelAttrs?.get("fontColor")?.asString?.let {
                    ColorParser.parseColorStringWithBinding(it, data, context)
                }
                val fontWeightValue = when (labelAttrs?.get("font")?.asString?.lowercase()) {
                    "bold" -> FontWeight.Bold
                    else -> FontWeight.Normal
                }

                Text(
                    text = labelText,
                    modifier = Modifier.weight(1f),
                    fontSize = fontSize?.sp ?: 16.sp,
                    color = fontColor ?: androidx.compose.ui.graphics.Color.Unspecified,
                    fontWeight = fontWeightValue
                )

                // Colors for switch
                val checkedTrackColor = ColorParser.parseColorWithBinding(json, "onTintColor", data, context)
                    ?: ColorParser.parseColorWithBinding(json, "tint", data, context)
                    ?: ColorParser.parseColorWithBinding(json, "tintColor", data, context)
                val checkedThumbColor = ColorParser.parseColorWithBinding(json, "thumbTintColor", data, context)

                val colors = if (checkedTrackColor != null || checkedThumbColor != null) {
                    SwitchDefaults.colors(
                        checkedTrackColor = checkedTrackColor ?: SwitchDefaults.colors().checkedTrackColor,
                        checkedThumbColor = checkedThumbColor ?: SwitchDefaults.colors().checkedThumbColor
                    )
                } else {
                    SwitchDefaults.colors()
                }

                // Enabled state (supports @{binding})
                val isEnabled = ResourceResolver.resolveBoolean(json, "enabled", data, default = true)

                Switch(
                    checked = checkedState,
                    onCheckedChange = onCheckedChange,
                    enabled = isEnabled,
                    colors = colors
                )
            }
        }

        // ── Helper Functions ──

        /**
         * Resolve the binding variable name from JSON attributes.
         * Priority: isOn > value > checked > bind
         */
        private fun resolveBindingVariable(json: JsonObject): String? {
            // Check isOn, value, checked in priority order
            val stateAttr = json.get("isOn") ?: json.get("value") ?: json.get("checked")
            if (stateAttr != null && stateAttr.isJsonPrimitive && stateAttr.asJsonPrimitive.isString) {
                ModifierBuilder.extractBindingProperty(stateAttr.asString)?.let { return it }
            }

            // Fall back to bind
            json.get("bind")?.asString?.let { bind ->
                ModifierBuilder.extractBindingProperty(bind)?.let { return it }
            }

            return null
        }

        /**
         * Resolve the current checked state from JSON and data.
         */
        private fun resolveCheckedState(
            json: JsonObject,
            data: Map<String, Any>,
            bindingVariable: String?
        ): Boolean {
            if (bindingVariable != null) {
                return (data[bindingVariable] as? Boolean) ?: false
            }

            // Direct value from isOn/value/checked
            val stateAttr = json.get("isOn") ?: json.get("value") ?: json.get("checked")
            if (stateAttr != null && stateAttr.isJsonPrimitive) {
                val p = stateAttr.asJsonPrimitive
                if (p.isBoolean) return p.asBoolean
                if (p.isString && !p.asString.contains("@{")) {
                    return p.asString.toBoolean()
                }
            }

            return false
        }

        /**
         * Build the onCheckedChange callback.
         * Updates bound variable via data["updateData"] and calls onValueChange/onToggle handler.
         */
        private fun buildOnCheckedChange(
            json: JsonObject,
            data: Map<String, Any>,
            bindingVariable: String?,
            updateState: (Boolean) -> Unit
        ): (Boolean) -> Unit = { newValue ->
            updateState(newValue)

            // Update bound variable via data["updateData"]
            if (bindingVariable != null) {
                val updateData = data["updateData"]
                if (updateData is Function<*>) {
                    try {
                        @Suppress("UNCHECKED_CAST")
                        (updateData as (Map<String, Any>) -> Unit)(
                            mapOf(bindingVariable to newValue)
                        )
                    } catch (_: Exception) {
                        // Update function doesn't match expected signature
                    }
                }
            }

            // Call onValueChange/onToggle handler (binding format only)
            val handler = json.get("onValueChange")?.asString
                ?: json.get("onToggle")?.asString
            if (handler != null && ModifierBuilder.isBinding(handler)) {
                val viewId = json.get("id")?.asString ?: "switch"
                ModifierBuilder.resolveEventHandler(handler, data, viewId, newValue)
            }
        }
    }
}
