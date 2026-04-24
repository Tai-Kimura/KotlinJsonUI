package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver

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
 * Supported JSON attributes:
 * - data: @{variable} for checked state binding (primary)
 * - isOn: Boolean for checked state (fallback)
 * - onclick/onClick: Event handler
 * - tintColor: Hex color for checkedThumbColor + checkedTrackColor (with 0.5f alpha)
 * - backgroundColor: Hex color for uncheckedTrackColor
 * - padding/paddings/margins: Layout modifiers
 * - alpha/opacity: Transparency
 * - weight: Weight in Row/Column
 */
class DynamicToggleComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap(),
            parentType: String? = null
        ) {
            val context = LocalContext.current

            // Parse checked state: 'data' attribute (@{var}) or 'isOn' (boolean)
            val dataAttr = json.get("data")?.asString
            val bindingVariable = if (dataAttr != null) {
                ModifierBuilder.extractBindingProperty("@{$dataAttr}")
                    ?: ModifierBuilder.extractBindingProperty(dataAttr)
                    ?: dataAttr
            } else null

            val checked = when {
                bindingVariable != null -> (data[bindingVariable] as? Boolean) ?: false
                json.has("isOn") -> ResourceResolver.resolveBoolean(json, "isOn", data, default = false)
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

                // Call onclick/onClick handler
                val handler = json.get("onclick")?.asString ?: json.get("onClick")?.asString
                if (handler != null) {
                    val viewId = json.get("id")?.asString ?: "toggle"
                    ModifierBuilder.resolveEventHandler(handler, data, viewId, newValue)
                }
            }

            // Build modifier: testTag, margins, alpha, padding, alignment, weight
            val modifier = ModifierBuilder.buildModifier(json, data, parentType, context)

            // Colors: tintColor -> checkedThumbColor + checkedTrackColor (0.5f alpha)
            //         backgroundColor -> uncheckedTrackColor
            val tintColor = ColorParser.parseColorWithBinding(json, "tintColor", data, context)
            val bgColor = ColorParser.parseColorWithBinding(json, "backgroundColor", data, context)

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

            val labelAttrs = json.get("labelAttributes")?.takeIf { it.isJsonObject }?.asJsonObject
            if (labelAttrs == null) {
                Switch(
                    checked = checkedState,
                    onCheckedChange = onCheckedChange,
                    modifier = modifier,
                    colors = colors
                )
            } else {
                // Leading label by default matches common iOS Toggle usage.
                val labelPosition = json.get("labelPosition")?.asString
                    ?: "leading"

                Row(
                    modifier = modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (labelPosition == "leading") {
                        ToggleLabel(labelAttrs, data, context)
                    }
                    Switch(
                        checked = checkedState,
                        onCheckedChange = onCheckedChange,
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
            labelAttrs: JsonObject,
            data: Map<String, Any>,
            context: android.content.Context
        ) {
            val textValue = labelAttrs.get("text")?.asString?.let { raw ->
                if (ModifierBuilder.isBinding(raw)) {
                    val key = ModifierBuilder.extractBindingProperty(raw)
                    (key?.let { data[it] } ?: raw).toString()
                } else {
                    raw
                }
            } ?: return

            val fontSize = labelAttrs.get("fontSize")?.asFloat
            val color = ColorParser.parseColorWithBinding(labelAttrs, "fontColor", data, context)
            val weight = labelAttrs.get("fontWeight")?.asString?.let { w ->
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
