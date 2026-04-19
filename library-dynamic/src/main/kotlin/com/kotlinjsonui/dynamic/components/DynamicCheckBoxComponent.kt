package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver

/**
 * Dynamic CheckBox Component Converter
 * Converts JSON to CheckBox composable at runtime.
 *
 * Reference: checkbox_component.rb in kjui_tools.
 *
 * CheckBox is the primary component name. Check is supported as an alias
 * for backward compatibility.
 *
 * State binding priority: isOn > checked > bind
 *
 * Supported JSON attributes:
 * - isOn/checked: Boolean or @{variable} for checked state
 * - bind: @{variable} for two-way binding (lowest priority)
 * - onValueChange: @{functionName} for change handler (binding format only)
 * - enabled: Boolean or @{variable} to enable/disable
 * - label/text: String label text to display next to checkbox
 * - icon/selectedIcon: String drawable names for IconToggleButton variant
 * - checkColor: Hex color for checked state
 * - uncheckedColor: Hex color for unchecked state
 * - fontColor: Hex color for icon tint (icon variant)
 * - fontSize/font: Label text styling
 * - spacing: Number dp between checkbox and label (default 8)
 * - padding/paddings/margins: Layout modifiers
 */
class DynamicCheckBoxComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap(),
            parentType: String? = null
        ) {
            val hasLabel = json.has("label") || json.has("text")
            val hasCustomIcon = json.has("icon") || json.has("selectedIcon")

            when {
                hasCustomIcon -> createIconCheckbox(json, data, parentType)
                hasLabel -> createWithLabel(json, data, parentType)
                else -> createCheckboxOnly(json, data, parentType)
            }
        }

        // ── Checkbox without label ──

        @Composable
        private fun createCheckboxOnly(
            json: JsonObject,
            data: Map<String, Any>,
            parentType: String?
        ) {
            val context = LocalContext.current

            // Parse binding variable (priority: isOn > checked > bind)
            val bindingVariable = resolveBindingVariable(json)

            // Get checked state
            val checked = resolveCheckedState(json, data, bindingVariable)

            // State for the checkbox
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

            // Build modifier: testTag, margins, alpha, padding, alignment, weight
            val modifier = ModifierBuilder.buildModifier(json, data, parentType, context)

            // Colors: checkColor -> checkedColor, uncheckedColor -> uncheckedColor
            val colors = buildCheckboxColors(json, data, context)

            Checkbox(
                checked = checkedState,
                onCheckedChange = onCheckedChange,
                modifier = modifier,
                enabled = isEnabled,
                colors = colors
            )
        }

        // ── Checkbox with label (Row layout) ──

        @Composable
        private fun createWithLabel(
            json: JsonObject,
            data: Map<String, Any>,
            parentType: String?
        ) {
            val context = LocalContext.current

            // Parse binding variable (priority: isOn > checked > bind)
            val bindingVariable = resolveBindingVariable(json)

            // Get checked state
            val checked = resolveCheckedState(json, data, bindingVariable)

            // State for the checkbox
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
                // Checkbox
                Checkbox(
                    checked = checkedState,
                    onCheckedChange = onCheckedChange
                )

                // Spacer with configurable spacing
                val spacing = json.get("spacing")?.asFloat?.dp ?: 8.dp
                Spacer(modifier = Modifier.width(spacing))

                // Label text with font attributes
                val labelText = json.get("label")?.asString ?: json.get("text")?.asString ?: ""
                val fontSize = json.get("fontSize")?.asFloat
                val fontColor = json.get("fontColor")?.asString?.let {
                    ColorParser.parseColorStringWithBinding(it, data, context)
                }
                val fontWeightValue = when (json.get("font")?.asString?.lowercase()) {
                    "bold" -> FontWeight.Bold
                    else -> null
                }

                if (fontSize != null || fontColor != null || fontWeightValue != null) {
                    Text(
                        text = labelText,
                        fontSize = fontSize?.sp ?: 16.sp,
                        color = fontColor ?: Color.Unspecified,
                        fontWeight = fontWeightValue ?: FontWeight.Normal
                    )
                } else {
                    Text(text = labelText)
                }
            }
        }

        // ── Checkbox with custom icon/selectedIcon (IconToggleButton) ──

        @Composable
        private fun createIconCheckbox(
            json: JsonObject,
            data: Map<String, Any>,
            parentType: String?
        ) {
            val context = LocalContext.current

            // Parse binding variable (priority: isOn > checked > bind)
            val bindingVariable = resolveBindingVariable(json)

            // Get checked state
            val checked = resolveCheckedState(json, data, bindingVariable)

            // State for the checkbox
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

            // Resolve icon drawable resources
            val iconName = json.get("icon")?.asString ?: "check_box_outline_blank"
            val selectedIconName = json.get("selectedIcon")?.asString ?: "check_box"
            val iconRes = ResourceResolver.resolveDrawable(iconName, data, context)
            val selectedIconRes = ResourceResolver.resolveDrawable(selectedIconName, data, context)

            // Build modifier: testTag, margins, alpha, padding, alignment
            val modifier = ModifierBuilder.buildModifier(json, data, parentType, context)

            IconToggleButton(
                checked = checkedState,
                onCheckedChange = onCheckedChange,
                modifier = modifier
            ) {
                // Icon tint color
                val tintColor = json.get("fontColor")?.asString?.let {
                    ColorParser.parseColorStringWithBinding(it, data, context)
                }

                val activeRes = if (checkedState) selectedIconRes else iconRes
                if (activeRes != 0) {
                    Icon(
                        painter = painterResource(id = activeRes),
                        contentDescription = null,
                        tint = tintColor ?: Color.Unspecified
                    )
                }
            }
        }

        // ── Helper Functions ──

        /**
         * Build checkbox colors from JSON attributes.
         * checkColor -> checkedColor, uncheckedColor -> uncheckedColor
         */
        @Composable
        private fun buildCheckboxColors(
            json: JsonObject,
            data: Map<String, Any>,
            context: android.content.Context
        ): androidx.compose.material3.CheckboxColors {
            val checkedColor = ColorParser.parseColorWithBinding(json, "checkColor", data, context)
            val uncheckedColor = ColorParser.parseColorWithBinding(json, "uncheckedColor", data, context)

            return when {
                checkedColor != null && uncheckedColor != null ->
                    CheckboxDefaults.colors(
                        checkedColor = checkedColor,
                        uncheckedColor = uncheckedColor
                    )
                checkedColor != null ->
                    CheckboxDefaults.colors(checkedColor = checkedColor)
                uncheckedColor != null ->
                    CheckboxDefaults.colors(uncheckedColor = uncheckedColor)
                else -> CheckboxDefaults.colors()
            }
        }

        /**
         * Resolve the binding variable name from JSON attributes.
         * Priority: isOn > checked > bind
         */
        private fun resolveBindingVariable(json: JsonObject): String? {
            // Check isOn, checked in priority order
            val stateAttr = json.get("isOn") ?: json.get("checked")
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

            // Direct value from isOn/checked
            val stateAttr = json.get("isOn") ?: json.get("checked")
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
         * Updates bound variable via data["updateData"] and calls onValueChange handler.
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

            // Call onValueChange handler (binding format only)
            val handler = json.get("onValueChange")?.asString
            if (handler != null && ModifierBuilder.isBinding(handler)) {
                val viewId = json.get("id")?.asString ?: "checkbox"
                ModifierBuilder.resolveEventHandler(handler, data, viewId, newValue)
            }
        }
    }
}
