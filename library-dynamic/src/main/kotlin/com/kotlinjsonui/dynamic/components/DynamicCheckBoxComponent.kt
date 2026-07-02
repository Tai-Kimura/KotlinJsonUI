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
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.CheckBoxAttributes
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import com.kotlinjsonui.dynamic.rememberTypedAttrs

/**
 * Dynamic CheckBox Component Converter
 * Converts JSON to CheckBox composable at runtime.
 *
 * Reference: checkbox_component.rb in kjui_tools.
 *
 * CheckBox is the primary component name. Check is supported as an alias
 * for backward compatibility (both spellings parse with the generated
 * [CheckBoxAttributes] — the Check section is a stub alias).
 *
 * State binding priority: isOn > checked > bind
 *
 * Attribute access goes through the generated [CheckBoxAttributes]
 * extraction (typed, alias-aware, L1-marker-aware) via the [TypedAttrs]
 * bridge; the node itself is only passed wholesale to the shared
 * ModifierBuilder pipeline.
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
        /** CheckBox-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "isOn", "checked", "bind", "enabled", "onValueChange",
            "label", "text", "icon", "selectedIcon",
            "spacing", "fontSize", "fontColor", "font", "uncheckedColor"
        )

        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap(),
            parentType: String? = null
        ) {
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                CheckBoxAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "CheckBox", json,
                declared = CheckBoxAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = LocalContext.current
            )

            val hasLabel = a.label != null || a.text != null
            val hasCustomIcon = a.icon != null || a.selectedIcon != null

            when {
                hasCustomIcon -> createIconCheckbox(json, a, data, parentType)
                hasLabel -> createWithLabel(json, a, data, parentType)
                else -> createCheckboxOnly(json, a, data, parentType)
            }
        }

        // ── Checkbox without label ──

        @Composable
        private fun createCheckboxOnly(
            json: JsonObject,
            a: CheckBoxAttributes,
            data: Map<String, Any>,
            parentType: String?
        ) {
            val context = LocalContext.current

            // Parse binding variable (priority: isOn > checked > bind)
            val bindingVariable = resolveBindingVariable(a)

            // Get checked state
            val checked = resolveCheckedState(a, data, bindingVariable)

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

            // Enabled state (supports @{binding}; CheckBox declares its own row)
            val isEnabled = TypedAttrs.boolean(a.enabled, data)
                ?: TypedAttrs.boolean(a.common.enabled, data) ?: true

            // Build onCheckedChange handler
            val onCheckedChange = buildOnCheckedChange(a, data, bindingVariable) { newValue ->
                checkedState = newValue
            }

            // Build modifier: testTag, margins, alpha, padding, alignment, weight
            val modifier = ModifierBuilder.buildModifier(json, data, parentType, context)

            // Colors: checkColor -> checkedColor, uncheckedColor -> uncheckedColor
            val colors = buildCheckboxColors(json, a, data, context)

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
            a: CheckBoxAttributes,
            data: Map<String, Any>,
            parentType: String?
        ) {
            val context = LocalContext.current

            // Parse binding variable (priority: isOn > checked > bind)
            val bindingVariable = resolveBindingVariable(a)

            // Get checked state
            val checked = resolveCheckedState(a, data, bindingVariable)

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

            // Enabled state (supports @{binding}; CheckBox declares its own row)
            val isEnabled = TypedAttrs.boolean(a.enabled, data)
                ?: TypedAttrs.boolean(a.common.enabled, data) ?: true

            // Build onCheckedChange handler
            val onCheckedChange = buildOnCheckedChange(a, data, bindingVariable) { newValue ->
                checkedState = newValue
            }

            // Build modifier for Row container: testTag, margins, alpha, padding.
            // The Row carries the component's id, so mirror the disabled state
            // in its semantics for accessibility / UI tests.
            val rowModifier = ModifierBuilder.buildModifier(json, data, parentType, context)
                .let { if (!isEnabled) it.semantics { disabled() } else it }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = rowModifier
            ) {
                // Checkbox
                Checkbox(
                    checked = checkedState,
                    onCheckedChange = onCheckedChange,
                    enabled = isEnabled
                )

                // Spacer with configurable spacing
                val spacing = TypedAttrs.float(a.spacing, data)?.dp ?: 8.dp
                Spacer(modifier = Modifier.width(spacing))

                // Label text with font attributes (raw representation —
                // the legacy reader did not resolve bindings here)
                val labelText = TypedAttrs.rawString(a.label)
                    ?: TypedAttrs.rawString(a.text) ?: ""
                val fontSize = TypedAttrs.float(a.fontSize, data)
                val fontColor = ColorParser.parseColorStringWithBinding(
                    TypedAttrs.rawString(a.fontColor), data, context
                )
                val fontWeightValue = when (TypedAttrs.rawString(a.font)?.lowercase()) {
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
            a: CheckBoxAttributes,
            data: Map<String, Any>,
            parentType: String?
        ) {
            val context = LocalContext.current

            // Parse binding variable (priority: isOn > checked > bind)
            val bindingVariable = resolveBindingVariable(a)

            // Get checked state
            val checked = resolveCheckedState(a, data, bindingVariable)

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

            // Enabled state (supports @{binding}; CheckBox declares its own row)
            val isEnabled = TypedAttrs.boolean(a.enabled, data)
                ?: TypedAttrs.boolean(a.common.enabled, data) ?: true

            // Build onCheckedChange handler
            val onCheckedChange = buildOnCheckedChange(a, data, bindingVariable) { newValue ->
                checkedState = newValue
            }

            // Resolve icon drawable resources
            val iconName = a.icon ?: "check_box_outline_blank"
            val selectedIconName = a.selectedIcon ?: "check_box"
            val iconRes = ResourceResolver.resolveDrawable(iconName, data, context)
            val selectedIconRes = ResourceResolver.resolveDrawable(selectedIconName, data, context)

            // Build modifier: testTag, margins, alpha, padding, alignment
            val modifier = ModifierBuilder.buildModifier(json, data, parentType, context)

            IconToggleButton(
                checked = checkedState,
                onCheckedChange = onCheckedChange,
                modifier = modifier,
                enabled = isEnabled
            ) {
                // Icon tint color
                val tintColor = ColorParser.parseColorStringWithBinding(
                    TypedAttrs.rawString(a.fontColor), data, context
                )

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
            a: CheckBoxAttributes,
            data: Map<String, Any>,
            context: android.content.Context
        ): androidx.compose.material3.CheckboxColors {
            // 'checkColor' is an undeclared legacy runtime extra
            // (the declared spelling is 'checkedColor')
            val checkedColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.undeclared(json, "checkColor")?.asString, data, context
            )
            val uncheckedColor = ColorParser.parseColorStringWithBinding(
                a.uncheckedColor, data, context
            )

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
        private fun resolveBindingVariable(a: CheckBoxAttributes): String? {
            // Check isOn, checked in priority order
            val stateAttr = a.isOn ?: a.checked
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
            a: CheckBoxAttributes,
            data: Map<String, Any>,
            bindingVariable: String?
        ): Boolean {
            if (bindingVariable != null) {
                return (data[bindingVariable] as? Boolean) ?: false
            }

            // Direct value from isOn/checked
            val stateAttr = a.isOn ?: a.checked
            return TypedAttrs.static(stateAttr) ?: false
        }

        /**
         * Build the onCheckedChange callback.
         * Updates bound variable via data["updateData"] and calls onValueChange handler.
         */
        private fun buildOnCheckedChange(
            a: CheckBoxAttributes,
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
            val handler = TypedAttrs.raw(a.onValueChange) as? String
            if (handler != null && ModifierBuilder.isBinding(handler)) {
                val viewId = a.common.id ?: "checkbox"
                ModifierBuilder.resolveEventHandler(handler, data, viewId, newValue)
            }
        }
    }
}
