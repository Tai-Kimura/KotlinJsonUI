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
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.SwitchAttributes
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.rememberTypedAttrs

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
        /** Switch-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "isOn", "value", "checked", "bind", "enabled",
            "onValueChange", "onToggle",
            "onTintColor", "tint", "tintColor", "thumbTintColor",
            "labelAttributes"
        )

        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap(),
            parentType: String? = null
        ) {
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                SwitchAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "Switch", json,
                declared = SwitchAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = LocalContext.current
            )
            val hasLabel = a.labelAttributes != null
            if (hasLabel) {
                createWithLabel(json, a, data, parentType)
            } else {
                createSwitchOnly(json, a, data, parentType)
            }
        }

        @Composable
        private fun createSwitchOnly(
            json: JsonObject,
            a: SwitchAttributes,
            data: Map<String, Any>,
            parentType: String?
        ) {
            val context = LocalContext.current

            // Parse binding variable (priority: isOn > value > checked > bind)
            val bindingVariable = resolveBindingVariable(a)

            // Get checked state from binding or direct value
            val checked = resolveCheckedState(a, data, bindingVariable)

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

            // Enabled state (supports @{binding}; Switch declares its own row)
            val isEnabled = TypedAttrs.boolean(a.enabled, data)
                ?: TypedAttrs.boolean(a.common.enabled, data) ?: true

            // Build onCheckedChange handler
            val onCheckedChange = buildOnCheckedChange(a, data, bindingVariable) { newValue ->
                checkedState = newValue
            }

            // Build modifier: testTag, margins, alpha, padding
            val modifier = ModifierBuilder.buildModifier(json, data, parentType, context)

            // Colors: onTintColor/tint/tintColor -> checkedTrackColor, thumbTintColor -> checkedThumbColor
            val checkedTrackColor = ColorParser.parseColorStringWithBinding(a.onTintColor, data, context)
                ?: ColorParser.parseColorStringWithBinding(TypedAttrs.rawString(a.tint), data, context)
                ?: ColorParser.parseColorStringWithBinding(a.tintColor, data, context)
            val checkedThumbColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.thumbTintColor), data, context
            )

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
            a: SwitchAttributes,
            data: Map<String, Any>,
            parentType: String?
        ) {
            val context = LocalContext.current
            val labelAttrs = a.labelAttributes

            // Parse binding variable (priority: isOn > value > checked > bind)
            val bindingVariable = resolveBindingVariable(a)

            // Get checked state from binding or direct value
            val checked = resolveCheckedState(a, data, bindingVariable)

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
            val onCheckedChange = buildOnCheckedChange(a, data, bindingVariable) { newValue ->
                checkedState = newValue
            }

            // Build modifier for Row container: testTag, margins, alpha, padding
            val rowModifier = ModifierBuilder.buildModifier(json, data, parentType, context)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = rowModifier
            ) {
                // Label Text with weight(1f)
                val labelText = labelAttrs?.get("text") as? String ?: ""
                val fontSize = (labelAttrs?.get("fontSize") as? Number)?.toFloat()
                val fontColor = (labelAttrs?.get("fontColor") as? String)?.let {
                    ColorParser.parseColorStringWithBinding(it, data, context)
                }
                val fontWeightValue = when ((labelAttrs?.get("font") as? String)?.lowercase()) {
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
                val checkedTrackColor = ColorParser.parseColorStringWithBinding(a.onTintColor, data, context)
                    ?: ColorParser.parseColorStringWithBinding(TypedAttrs.rawString(a.tint), data, context)
                    ?: ColorParser.parseColorStringWithBinding(a.tintColor, data, context)
                val checkedThumbColor = ColorParser.parseColorStringWithBinding(
                    TypedAttrs.rawString(a.thumbTintColor), data, context
                )

                val colors = if (checkedTrackColor != null || checkedThumbColor != null) {
                    SwitchDefaults.colors(
                        checkedTrackColor = checkedTrackColor ?: SwitchDefaults.colors().checkedTrackColor,
                        checkedThumbColor = checkedThumbColor ?: SwitchDefaults.colors().checkedThumbColor
                    )
                } else {
                    SwitchDefaults.colors()
                }

                // Enabled state (supports @{binding}; Switch declares its own row)
                val isEnabled = TypedAttrs.boolean(a.enabled, data)
                    ?: TypedAttrs.boolean(a.common.enabled, data) ?: true

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
         * Priority: isOn > value > checked > bind (all standalone declared
         * rows — not an SSoT alias group, so the priority order is kept).
         */
        private fun resolveBindingVariable(a: SwitchAttributes): String? {
            // Check isOn, value, checked in priority order
            val stateAttr = a.isOn ?: a.value ?: a.checked
            TypedAttrs.binding(stateAttr)?.let { return it }

            // Fall back to bind
            (TypedAttrs.raw(a.bind) as? String)?.let { bind ->
                ModifierBuilder.extractBindingProperty(bind)?.let { return it }
            }

            return null
        }

        /**
         * Resolve the current checked state from JSON and data.
         */
        private fun resolveCheckedState(
            a: SwitchAttributes,
            data: Map<String, Any>,
            bindingVariable: String?
        ): Boolean {
            if (bindingVariable != null) {
                return (data[bindingVariable] as? Boolean) ?: false
            }

            // Direct value from isOn/value/checked
            val stateAttr = a.isOn ?: a.value ?: a.checked
            return TypedAttrs.static(stateAttr) ?: false
        }

        /**
         * Build the onCheckedChange callback.
         * Updates bound variable via data["updateData"] and calls onValueChange/onToggle handler.
         */
        private fun buildOnCheckedChange(
            a: SwitchAttributes,
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
            val handler = TypedAttrs.raw(a.onValueChange) as? String
                ?: TypedAttrs.raw(a.onToggle) as? String
            if (handler != null && ModifierBuilder.isBinding(handler)) {
                val viewId = a.common.id ?: "switch"
                ModifierBuilder.resolveEventHandler(handler, data, viewId, newValue)
            }
        }
    }
}
