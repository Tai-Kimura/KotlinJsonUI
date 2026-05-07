package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.res.painterResource
import com.kotlinjsonui.dynamic.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import androidx.compose.ui.platform.LocalContext

/**
 * Dynamic Radio Component Converter
 * Converts JSON to RadioButton/RadioGroup composable at runtime.
 * Reference: radio_component.rb in kjui_tools.
 *
 * Three modes:
 * 1. Radio group with items array (highest priority)
 * 2. Individual radio item (has group or text without items/options)
 * 3. Radio group with options (static array or @{binding})
 *
 * Supported JSON attributes:
 * - bind: @{variable} for selected value binding
 * - options: Array of options or @{variable} for dynamic options
 * - items: Array of items for radio group
 * - selectedValue: @{variable} for selected value in items mode
 * - onValueChange: @{handler} for change callback
 * - selectedColor/unselectedColor: Colors for RadioButtonDefaults.colors
 * - icon/selectedIcon: Custom icon names
 * - group: Group identifier for individual radio item
 * - text: Label text
 * - fontColor/textColor: Label text color
 * - Modifiers: testTag, margins, alpha, padding, weight
 */
class DynamicRadioComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            when {
                // Handle radio group with items (highest priority)
                json.has("items") -> createRadioGroupWithItems(json, data)
                // Handle individual radio item
                json.has("group") || (json.has("text") && !json.has("options")) -> createRadioItem(json, data)
                // Handle radio group with options
                else -> createRadioGroup(json, data)
            }
        }

        // ── Radio group with options (static array or @{binding}) ──

        @Composable
        private fun createRadioGroup(json: JsonObject, data: Map<String, Any>) {
            val context = LocalContext.current

            // Parse binding variable
            val bindingVariable = extractBindingVariable(json, "bind")

            // Get selected value from data
            val currentSelected = if (bindingVariable != null) {
                (data[bindingVariable] as? String) ?: ""
            } else ""

            var selectedValue by remember(currentSelected, bindingVariable, data) {
                mutableStateOf(currentSelected)
            }

            LaunchedEffect(data, bindingVariable) {
                if (bindingVariable != null) {
                    selectedValue = (data[bindingVariable] as? String) ?: ""
                }
            }

            // Parse options: static array or @{binding}
            val options = parseOptions(json, data)

            // Parse colors (supports @{binding})
            val selectedColor = ColorParser.parseColorWithBinding(json, "selectedColor", data, context)
            val unselectedColor = ColorParser.parseColorWithBinding(json, "unselectedColor", data, context)

            val colors = if (selectedColor != null || unselectedColor != null) {
                RadioButtonDefaults.colors(
                    selectedColor = selectedColor ?: RadioButtonDefaults.colors().selectedColor,
                    unselectedColor = unselectedColor ?: RadioButtonDefaults.colors().unselectedColor
                )
            } else {
                RadioButtonDefaults.colors()
            }

            // Handle value change
            val viewId = json.get("id")?.asString ?: "radio"
            val onValueChange: (String) -> Unit = { newValue ->
                selectedValue = newValue

                // Update bound variable
                if (bindingVariable != null) {
                    @Suppress("UNCHECKED_CAST")
                    (data["updateData"] as? (Map<String, Any>) -> Unit)
                        ?.invoke(mapOf(bindingVariable to newValue))
                }

                // Call onValueChange handler if specified
                val handler = json.get("onValueChange")?.asString
                if (handler != null && ModifierBuilder.isBinding(handler)) {
                    ModifierBuilder.resolveEventHandler(handler, data, viewId, newValue)
                }
            }

            // Build modifier
            val modifier = ModifierBuilder.buildModifier(json, data, context = context)

            Column(modifier = modifier) {
                options.forEach { (value, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onValueChange(value) }
                    ) {
                        RadioButton(
                            selected = selectedValue == value,
                            onClick = { onValueChange(value) },
                            colors = colors
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label)
                    }
                }
            }
        }

        // ── Individual radio item (group/text mode) ──

        @Composable
        private fun createRadioItem(json: JsonObject, data: Map<String, Any>) {
            val context = LocalContext.current
            val group = json.get("group")?.asString ?: "default"
            val id = json.get("id")?.asString ?: "radio_${System.currentTimeMillis()}"
            val text = json.get("text")?.asString ?: ""

            // Variable name for selected state based on group
            val selectedVar = if (group.lowercase() != "default") {
                "selected${group.replaceFirstChar { it.uppercase() }}"
            } else {
                "selectedRadiogroup"
            }

            // Get current selected value
            val isSelected = (data[selectedVar] as? String) == id

            // Build modifier
            val modifier = ModifierBuilder.buildModifier(json, data, context = context)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
            ) {
                val icon = json.get("icon")?.asString
                val selectedIcon = json.get("selectedIcon")?.asString

                // Update function for selection
                val onSelect: () -> Unit = {
                    @Suppress("UNCHECKED_CAST")
                    (data["updateData"] as? (Map<String, Any>) -> Unit)
                        ?.invoke(mapOf(selectedVar to id))
                }

                when {
                    // Standard radio button (circle icons or no icons)
                    (icon == "circle" || icon == null) &&
                            (selectedIcon == "checkmark.circle.fill" || selectedIcon == null) -> {
                        RadioButton(
                            selected = isSelected,
                            onClick = onSelect
                        )
                    }
                    // Square checkbox appearance
                    icon == "square" &&
                            (selectedIcon == "checkmark.square.fill" || selectedIcon == null) -> {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onSelect() }
                        )
                    }
                    // Custom icons
                    icon != null || selectedIcon != null -> {
                        val iconResId = mapIconResId(icon ?: "star")
                        val selectedIconResId = mapIconResId(selectedIcon ?: "star.fill")

                        IconButton(onClick = onSelect) {
                            Icon(
                                painter = painterResource(if (isSelected) selectedIconResId else iconResId),
                                contentDescription = text,
                                tint = if (isSelected) {
                                    ColorParser.parseColorWithBinding(json, "selectedColor", data, context)
                                        ?: ColorParser.parseColorWithBinding(json, "tintColor", data, context)
                                        ?: MaterialTheme.colorScheme.primary
                                } else {
                                    Color.Gray
                                }
                            )
                        }
                    }
                    // Default radio button
                    else -> {
                        RadioButton(
                            selected = isSelected,
                            onClick = onSelect
                        )
                    }
                }

                // Add label text
                if (text.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    val textColor =
                        ColorParser.parseColorWithBinding(json, "fontColor", data, context)
                            ?: ColorParser.parseColorWithBinding(json, "textColor", data, context)
                            ?: Color.Black
                    Text(text = text, color = textColor)
                }
            }
        }

        // ── Radio group with items array ──

        @Composable
        private fun createRadioGroupWithItems(json: JsonObject, data: Map<String, Any>) {
            val context = LocalContext.current
            val items = json.get("items")?.asJsonArray?.map { it.asString } ?: emptyList()

            // Parse selected value binding from selectedValue attribute
            val bindingVariable = extractBindingVariable(json, "selectedValue")

            val currentSelected = if (bindingVariable != null) {
                (data[bindingVariable] as? String) ?: ""
            } else ""

            var selectedValue by remember(currentSelected, bindingVariable, data) {
                mutableStateOf(currentSelected)
            }

            LaunchedEffect(data, bindingVariable) {
                if (bindingVariable != null) {
                    selectedValue = (data[bindingVariable] as? String) ?: ""
                }
            }

            // Handle value change
            val onValueChange: (String) -> Unit = { newValue ->
                selectedValue = newValue
                if (bindingVariable != null) {
                    @Suppress("UNCHECKED_CAST")
                    (data["updateData"] as? (Map<String, Any>) -> Unit)
                        ?.invoke(mapOf(bindingVariable to newValue))
                }
            }

            // Build modifier
            val modifier = ModifierBuilder.buildModifier(json, data, context = context)

            // Parse text color
            val textColor = ColorParser.parseColorWithBinding(json, "fontColor", data, context)
                ?: ColorParser.parseColorWithBinding(json, "textColor", data, context)
                ?: Color.Black

            Column(modifier = modifier) {
                // Add label if present
                json.get("text")?.asString?.let { label ->
                    Text(text = label, color = textColor)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Generate radio items
                items.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onValueChange(item) }
                    ) {
                        RadioButton(
                            selected = selectedValue == item,
                            onClick = { onValueChange(item) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = item, color = textColor)
                    }
                }
            }
        }

        // ── Helpers ──

        private fun parseOptions(json: JsonObject, data: Map<String, Any>): List<Pair<String, String>> {
            val optionsElement = json.get("options") ?: return emptyList()

            return when {
                optionsElement.isJsonArray -> {
                    optionsElement.asJsonArray.map { element ->
                        when {
                            element.isJsonObject -> {
                                val obj = element.asJsonObject
                                Pair(
                                    obj.get("value")?.asString ?: "",
                                    obj.get("label")?.asString ?: ""
                                )
                            }
                            element.isJsonPrimitive -> {
                                val value = element.asString
                                Pair(value, value)
                            }
                            else -> Pair("", "")
                        }
                    }
                }
                optionsElement.isJsonPrimitive && ModifierBuilder.isBinding(optionsElement.asString) -> {
                    val variable = ModifierBuilder.extractBindingProperty(optionsElement.asString)
                    @Suppress("UNCHECKED_CAST")
                    val dynamicOptions = variable?.let { data[it] as? List<String> } ?: emptyList()
                    dynamicOptions.map { Pair(it, it) }
                }
                else -> emptyList()
            }
        }

        private fun extractBindingVariable(json: JsonObject, key: String): String? {
            val value = json.get(key)?.asString ?: return null
            return ModifierBuilder.extractBindingProperty(value)
        }

        private fun mapIconResId(iconName: String): Int {
            return when (iconName) {
                "circle" -> R.drawable.ic_panorama_fish_eye
                "checkmark.circle.fill" -> R.drawable.ic_check_circle_filled
                "star" -> R.drawable.ic_star_outlined
                "star.fill" -> R.drawable.ic_star_filled
                "heart" -> R.drawable.ic_favorite_border
                "heart.fill" -> R.drawable.ic_favorite_filled
                "square" -> R.drawable.ic_check_box_outline_blank
                "checkmark.square.fill" -> R.drawable.ic_check_box_filled
                else -> R.drawable.ic_star_outlined
            }
        }
    }
}
