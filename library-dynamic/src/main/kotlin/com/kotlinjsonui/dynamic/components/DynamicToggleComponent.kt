package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.ToggleAttributes
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.rememberTypedAttrs

/**
 * Dynamic Toggle Component Converter
 * Converts JSON to Toggle (Switch) composable at runtime.
 *
 * Reference: toggle_component.rb in kjui_tools.
 *
 * Toggle in iOS maps to Switch in Android.
 * This is a distinct component from DynamicSwitchComponent with its own
 * attribute handling (data/isOn for state, onclick/onClick for handler,
 * tintColor for both thumb+track, backgroundColor for unchecked track).
 *
 * Attribute access goes through the generated [ToggleAttributes]
 * extraction (typed, alias-aware, L1-marker-aware) via the [TypedAttrs]
 * bridge; the node itself is only passed wholesale to the shared
 * ModifierBuilder pipeline.
 *
 * Supported JSON attributes:
 * - data: @{variable} for checked state binding (primary)
 * - isOn: Boolean for checked state (fallback)
 * - onclick/onClick: Event handler
 * - enabled: Boolean or @{variable} to enable/disable
 * - tintColor: Hex color for checkedThumbColor + checkedTrackColor (with 0.5f alpha)
 * - backgroundColor: Hex color for uncheckedTrackColor
 * - padding/paddings/margins: Layout modifiers
 * - alpha/opacity: Transparency
 * - weight: Weight in Row/Column
 */
class DynamicToggleComponent {
    companion object {
        /** Toggle-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "isOn", "enabled", "tintColor", "labelAttributes", "labelPosition",
            "onValueChange", "onToggle"
        )

        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap(),
            parentType: String? = null
        ) {
            val context = LocalContext.current
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                ToggleAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "Toggle", json,
                declared = ToggleAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // Parse checked state: 'data' attribute (@{var}) or 'isOn' (boolean).
            // 'data' is a structural key the legacy Toggle reuses as a binding
            // string — kept as a raw node read.
            val dataAttr = json.get("data")?.asString
            val bindingVariable = if (dataAttr != null) {
                ModifierBuilder.extractBindingProperty("@{$dataAttr}")
                    ?: ModifierBuilder.extractBindingProperty(dataAttr)
                    ?: dataAttr
            } else null

            val checked = when {
                bindingVariable != null -> (data[bindingVariable] as? Boolean) ?: false
                a.isOn != null -> TypedAttrs.boolean(a.isOn, data) ?: false
                else -> false
            }

            // State for the toggle
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
            val onCheckedChange: (Boolean) -> Unit = { newValue ->
                checkedState = newValue

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

                // Call onValueChange/onToggle handler (declared change callbacks,
                // like Switch), falling back to legacy onclick/onClick
                val handler = TypedAttrs.raw(a.onValueChange) as? String
                    ?: TypedAttrs.raw(a.onToggle) as? String
                    ?: a.common.onclick as? String
                    ?: TypedAttrs.raw(a.common.onClick) as? String
                if (handler != null) {
                    val viewId = a.common.id ?: "toggle"
                    ModifierBuilder.resolveEventHandler(handler, data, viewId, newValue)
                }
            }

            // Enabled state (supports @{binding}; Toggle declares its own row) —
            // Toggle is a canonical alias of Switch and must honor the same attribute
            val isEnabled = TypedAttrs.boolean(a.enabled, data)
                ?: TypedAttrs.boolean(a.common.enabled, data) ?: true

            // Build modifier: testTag, margins, alpha, padding, alignment, weight
            val modifier = ModifierBuilder.buildModifier(json, data, parentType, context)

            // Colors: tintColor -> checkedThumbColor + checkedTrackColor (0.5f alpha)
            //         backgroundColor -> uncheckedTrackColor
            val tintColor = ColorParser.parseColorStringWithBinding(a.tintColor, data, context)
            // 'backgroundColor' is an undeclared legacy runtime extra
            val bgColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.undeclared(json, "backgroundColor")?.asString, data, context
            )

            val colors = if (tintColor != null || bgColor != null) {
                SwitchDefaults.colors(
                    checkedThumbColor = tintColor ?: SwitchDefaults.colors().checkedThumbColor,
                    checkedTrackColor = tintColor?.copy(alpha = 0.5f)
                        ?: SwitchDefaults.colors().checkedTrackColor,
                    uncheckedTrackColor = bgColor ?: SwitchDefaults.colors().uncheckedTrackColor
                )
            } else {
                SwitchDefaults.colors()
            }

            val labelAttrs = a.labelAttributes
            if (labelAttrs == null) {
                Switch(
                    checked = checkedState,
                    onCheckedChange = onCheckedChange,
                    modifier = modifier,
                    enabled = isEnabled,
                    colors = colors
                )
            } else {
                // Leading label by default matches common iOS Toggle usage.
                val labelPosition = TypedAttrs.enumString(a.labelPosition) { it.json }
                    ?: "leading"

                Row(
                    // The Row carries the component's id, so mirror the disabled
                    // state in its semantics for accessibility / UI tests.
                    modifier = modifier.let {
                        if (!isEnabled) it.semantics { disabled() } else it
                    },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (labelPosition == "leading") {
                        ToggleLabel(labelAttrs, data, context)
                    }
                    Switch(
                        checked = checkedState,
                        onCheckedChange = onCheckedChange,
                        enabled = isEnabled,
                        colors = colors
                    )
                    if (labelPosition == "trailing") {
                        ToggleLabel(labelAttrs, data, context)
                    }
                }
            }
        }

        /**
         * Render the Toggle label text using `labelAttributes` object.
         * Reads: text, fontSize, fontColor, fontWeight.
         * Tool emits this shape from sjui_tools / kjui_tools `labelAttributes`.
         */
        @Composable
        private fun ToggleLabel(
            labelAttrs: Map<String, Any?>,
            data: Map<String, Any>,
            context: android.content.Context
        ) {
            val textValue = (labelAttrs["text"] as? String)?.let { raw ->
                if (ModifierBuilder.isBinding(raw)) {
                    val key = ModifierBuilder.extractBindingProperty(raw)
                    (key?.let { data[it] } ?: raw).toString()
                } else {
                    raw
                }
            } ?: return

            val fontSize = (labelAttrs["fontSize"] as? Number)?.toFloat()
            val color = (labelAttrs["fontColor"] as? String)?.let {
                ColorParser.parseColorStringWithBinding(it, data, context)
            }
            val weight = (labelAttrs["fontWeight"] as? String)?.let { w ->
                when (w.lowercase()) {
                    "bold" -> FontWeight.Bold
                    "semibold", "semi-bold" -> FontWeight.SemiBold
                    "medium" -> FontWeight.Medium
                    "light" -> FontWeight.Light
                    "thin" -> FontWeight.Thin
                    "black" -> FontWeight.Black
                    "normal", "regular" -> FontWeight.Normal
                    else -> w.toIntOrNull()?.let { FontWeight(it) }
                }
            }

            Text(
                text = textValue,
                fontSize = fontSize?.sp ?: androidx.compose.ui.unit.TextUnit.Unspecified,
                color = color ?: androidx.compose.ui.graphics.Color.Unspecified,
                fontWeight = weight
            )
        }
    }
}
