package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
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
import androidx.compose.ui.semantics.Role
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
            // 'src' is the common-attribute spelling of the unchecked icon
            // (ios renders it — 33 cross-effect measured android dropping it).
            val hasCustomIcon = a.icon != null || a.selectedIcon != null ||
                a.src != null

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
            // in its semantics for accessibility / UI tests. The whole labeled
            // row is toggleable (standard labeled-checkbox pattern): a tap
            // anywhere on the row — including the label, which is where the
            // row's center lands for UI-test drivers — toggles the value.
            val rowModifier = ModifierBuilder.buildModifier(json, data, parentType, context)
                .toggleable(
                    value = checkedState,
                    enabled = isEnabled,
                    role = Role.Checkbox,
                    onValueChange = onCheckedChange
                )
                .let { if (!isEnabled) it.semantics { disabled() } else it }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = rowModifier
            ) {
                // Checkbox keeps its own handler (and with it the Material
                // minimumInteractiveComponentSize wrapper, so labeled-checkbox
                // visuals stay identical to static codegen). A tap on the box
                // is consumed here; taps elsewhere on the row hit the row-level
                // toggleable. No double-fire: inner consumption wins.
                Checkbox(
                    checked = checkedState,
                    onCheckedChange = onCheckedChange,
                    enabled = isEnabled,
                    // iconSize scales the default glyph via the size modifier
                    // (mirrors checkbox_component.rb — no separate glyph to
                    // size on a Material Checkbox).
                    modifier = a.iconSize?.let {
                        Modifier.size(it.toInt().dp)
                    } ?: Modifier,
                    // The labeled path dropped the color skin entirely —
                    // uncheckedColor/checkedColor never reached the box
                    // every text-bearing fixture renders (33 cross-effect).
                    colors = buildCheckboxColors(json, a, data, context)
                )

                // Spacer with configurable spacing
                val spacing = TypedAttrs.float(a.spacing, data)?.dp ?: 8.dp
                Spacer(modifier = Modifier.width(spacing))

                // Label text. The raw representation is the LAYOUT spelling,
                // so a bound label drew the characters `@{expr}` on screen
                // (smoke run: CheckBox/text__binding rendered "@{boundText}").
                // Every other component routes the same rows through
                // ResourceResolver.resolveTextValue, which resolves the
                // binding and the string-resource name alike.
                val labelText = (TypedAttrs.rawString(a.label)
                    ?: TypedAttrs.rawString(a.text))
                    ?.let { ResourceResolver.resolveTextValue(it, data, context) } ?: ""
                val fontSize = TypedAttrs.float(a.fontSize, data)
                val fontColor = ColorParser.parseColorStringWithBinding(
                    TypedAttrs.rawString(a.fontColor), data, context
                )
                // `font` is declared `["string","binding"]`. `rawString` hands
                // back `"@{expr}"` verbatim (it exists to feed the
                // binding-AWARE helpers), so matching it against weight names
                // could only ever hit the static face — CheckBox/font__static
                // was active on android while CheckBox/font__binding rendered
                // the default weight. Resolve first, then look the name up in
                // the shared weight table rather than a local one-entry copy.
                val fontWeightValue =
                    ResourceResolver.fontWeightFor(TypedAttrs.string(a.font, data))

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

            // Resolve icon drawable resources — each state falls back to the
            // OTHER supplied asset, not to a Material drawable name that the
            // app does not ship (kjui codegen and iOS share this contract;
            // "check_box_outline_blank" resolved to 0 and drew NOTHING for a
            // selectedIcon-only declaration).
            val srcIcon = a.src
            val iconName = a.icon ?: srcIcon ?: a.selectedIcon ?: "check_box_outline_blank"
            val selectedIconName = a.selectedIcon ?: a.icon ?: srcIcon ?: "check_box"
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
            // Declared spelling first; 'checkColor' is the undeclared
            // legacy runtime extra.
            val checkedColor = ColorParser.parseColorStringWithBinding(
                a.checkedColor, data, context
            ) ?: ColorParser.parseColorStringWithBinding(
                TypedAttrs.undeclared(json, "checkColor")?.asString, data, context
            )
            val uncheckedColor = ColorParser.parseColorStringWithBinding(
                a.uncheckedColor, data, context
            )
            // `iconColor` ("Icon tint color") had NO reference on this path at
            // all, so a declared one rendered the default tick
            // (CheckBox/iconColor__static, redistributed by E2). On a Material
            // Checkbox the "icon" is the tick, not the box — the kjui codegen
            // maps it to `checkmarkColor` and says so in as many words
            // (checkbox_component.rb:227-231); this is the same mapping, not a
            // second opinion about what the icon is.
            val iconColor = ColorParser.parseColorStringWithBinding(
                a.iconColor, data, context
            )

            // `Color.Unspecified` is Material's own "leave this slot alone":
            // every parameter defaults to it, and the copy() underneath resolves
            // each one with `takeOrElse { existing }`. So one call covers all
            // eight combinations, and an undeclared colour keeps the THEME
            // value rather than a value we reconstructed.
            //
            // Reading the defaults back out and passing them in would have been
            // wrong, not merely verbose: `uncheckedColor` lands on
            // `uncheckedBorderColor`, while `uncheckedBoxColor` is pinned to
            // Transparent — so feeding the box colour into that parameter would
            // have erased the unchecked border.
            return CheckboxDefaults.colors(
                checkedColor = checkedColor ?: Color.Unspecified,
                uncheckedColor = uncheckedColor ?: Color.Unspecified,
                checkmarkColor = iconColor ?: Color.Unspecified
            )
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
            (TypedAttrs.raw(a.common.bind) as? String)?.let { bind ->
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
