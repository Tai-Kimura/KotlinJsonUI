package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.processDataBinding
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder

/**
 * Dynamic Radio Component Converter
 * Converts JSON to RadioButton/RadioGroup composable at runtime
 *
 * Supported JSON attributes (matching Ruby implementation):
 * - bind: @{variable} for selected value binding
 * - options: Array of options or @{variable} for dynamic options
 * - items: Array of items for radio group
 * - selectedValue: @{variable} for selected value in group
 * - onValueChange: String method name for change handler
 * - selectedColor: String hex color for selected state
 * - unselectedColor: String hex color for unselected state
 * - icon/selectedIcon: String icon names for custom icons
 * - group: String group identifier
 * - id: String unique identifier
 * - text: String label text
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
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
                json.has("group") || json.has("text") -> createRadioItem(json, data)
                // Handle radio group with options
                else -> createRadioGroup(json, data)
            }
        }

        @Composable
        private fun createRadioGroup(json: JsonObject, data: Map<String, Any>) {
            // Parse binding variable
            val bindingVariable = json.get("bind")?.asString?.let { bind ->
                if (bind.contains("@{")) {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(bind)?.groupValues?.get(1)
                } else null
            }

            // Get initial selected value
            val initialSelected = if (bindingVariable != null) {
                (data[bindingVariable] as? String) ?: ""
            } else {
                ""
            }

            // State for the selected value
            var selectedValue by remember(initialSelected, bindingVariable, data) {
                mutableStateOf(
                    if (bindingVariable != null) {
                        (data[bindingVariable] as? String) ?: ""
                    } else {
                        initialSelected
                    }
                )
            }

            // Update selected state when data changes
            LaunchedEffect(data, bindingVariable) {
                if (bindingVariable != null) {
                    selectedValue = (data[bindingVariable] as? String) ?: ""
                }
            }

            // Parse options
            val options = when {
                json.get("options")?.isJsonArray == true -> {
                    json.get("options").asJsonArray.map { element ->
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

                json.get("options")?.asString?.contains("@{") == true -> {
                    // Dynamic options from data binding
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    val variable = pattern.find(json.get("options").asString)?.groupValues?.get(1)

                    @Suppress("UNCHECKED_CAST")
                    val dynamicOptions = data[variable] as? List<String> ?: emptyList()
                    dynamicOptions.map { Pair(it, it) }
                }

                else -> emptyList()
            }

            // Handle value change
            val onValueChange: (String) -> Unit = { newValue ->
                selectedValue = newValue

                // Call custom handler if specified
                json.get("onValueChange")?.asString?.let { methodName ->
                    val handler = data[methodName]
                    if (handler is Function<*>) {
                        try {
                            @Suppress("UNCHECKED_CAST")
                            (handler as (String) -> Unit)(newValue)
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
                } ?: run {
                    // Update bound variable if no custom handler
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
                }
            }

            // Parse colors
            val selectedColor = json.get("selectedColor")?.asString?.let {
                try {
                    Color(android.graphics.Color.parseColor(it))
                } catch (e: Exception) {
                    null
                }
            }

            val unselectedColor = json.get("unselectedColor")?.asString?.let {
                try {
                    Color(android.graphics.Color.parseColor(it))
                } catch (e: Exception) {
                    null
                }
            }

            val colors = RadioButtonDefaults.colors()

            Column(
                modifier = buildModifier(json)
            ) {
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

        @Composable
        private fun createRadioItem(json: JsonObject, data: Map<String, Any>) {
            val group = json.get("group")?.asString ?: "default"
            val id = json.get("id")?.asString ?: "radio_${System.currentTimeMillis()}"
            val text = json.get("text")?.asString ?: ""

            // Variable name for selected state
            val selectedVar = if (group.lowercase() != "default") {
                "selected${group.replaceFirstChar { it.uppercase() }}"
            } else {
                "selectedRadiogroup"
            }

            // Get current selected value
            val isSelected = (data[selectedVar] as? String) == id

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = buildModifier(json)
            ) {
                // Check for custom icons
                val icon = json.get("icon")?.asString
                val selectedIcon = json.get("selectedIcon")?.asString

                when {
                    // Standard radio button (circle icons or no icons)
                    (icon == "circle" || icon == null) &&
                            (selectedIcon == "checkmark.circle.fill" || selectedIcon == null) -> {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                val updateData = data["updateData"]
                                if (updateData is Function<*>) {
                                    try {
                                        @Suppress("UNCHECKED_CAST")
                                        (updateData as (Map<String, Any>) -> Unit)(
                                            mapOf(selectedVar to id)
                                        )
                                    } catch (e: Exception) {
                                        // Update function doesn't match expected signature
                                    }
                                }
                            }
                        )
                    }
                    // Square checkbox appearance
                    icon == "square" &&
                            (selectedIcon == "checkmark.square.fill" || selectedIcon == null) -> {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = {
                                val updateData = data["updateData"]
                                if (updateData is Function<*>) {
                                    try {
                                        @Suppress("UNCHECKED_CAST")
                                        (updateData as (Map<String, Any>) -> Unit)(
                                            mapOf(selectedVar to id)
                                        )
                                    } catch (e: Exception) {
                                        // Update function doesn't match expected signature
                                    }
                                }
                            }
                        )
                    }
                    // Custom icons
                    icon != null || selectedIcon != null -> {
                        val iconVector = mapIconName(icon ?: "star")
                        val selectedIconVector = mapIconName(selectedIcon ?: "star.fill")

                        IconButton(
                            onClick = {
                                val updateData = data["updateData"]
                                if (updateData is Function<*>) {
                                    try {
                                        @Suppress("UNCHECKED_CAST")
                                        (updateData as (Map<String, Any>) -> Unit)(
                                            mapOf(selectedVar to id)
                                        )
                                    } catch (e: Exception) {
                                        // Update function doesn't match expected signature
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) selectedIconVector else iconVector,
                                contentDescription = text,
                                tint = if (isSelected) {
                                    json.get("selectedColor")?.asString?.let {
                                        try {
                                            Color(android.graphics.Color.parseColor(it))
                                        } catch (e: Exception) {
                                            MaterialTheme.colorScheme.primary
                                        }
                                    } ?: MaterialTheme.colorScheme.primary
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
                            onClick = {
                                val updateData = data["updateData"]
                                if (updateData is Function<*>) {
                                    try {
                                        @Suppress("UNCHECKED_CAST")
                                        (updateData as (Map<String, Any>) -> Unit)(
                                            mapOf(selectedVar to id)
                                        )
                                    } catch (e: Exception) {
                                        // Update function doesn't match expected signature
                                    }
                                }
                            }
                        )
                    }
                }

                // Add label text
                if (text.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    val textColor =
                        (json.get("fontColor") ?: json.get("textColor"))?.asString?.let {
                            try {
                                Color(android.graphics.Color.parseColor(it))
                            } catch (e: Exception) {
                                Color.Black
                            }
                        } ?: Color.Black
                    Text(text = text, color = textColor)
                }
            }
        }

        @Composable
        private fun createRadioGroupWithItems(json: JsonObject, data: Map<String, Any>) {
            val items = json.get("items")?.asJsonArray?.map { it.asString } ?: emptyList()

            // Parse selected value binding
            val selectedValueStr = json.get("selectedValue")?.asString
            val bindingVariable = if (selectedValueStr?.contains("@{") == true) {
                val pattern = "@\\{([^}]+)\\}".toRegex()
                pattern.find(selectedValueStr)?.groupValues?.get(1)
            } else null

            // Get initial selected value
            val initialSelected = if (bindingVariable != null) {
                (data[bindingVariable] as? String) ?: ""
            } else {
                ""
            }

            // State for the selected value
            var selectedValue by remember(initialSelected, bindingVariable, data) {
                mutableStateOf(
                    if (bindingVariable != null) {
                        (data[bindingVariable] as? String) ?: ""
                    } else {
                        initialSelected
                    }
                )
            }

            // Update selected state when data changes
            LaunchedEffect(data, bindingVariable) {
                if (bindingVariable != null) {
                    selectedValue = (data[bindingVariable] as? String) ?: ""
                }
            }

            val onValueChange: (String) -> Unit = { newValue ->
                selectedValue = newValue
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
            }

            Column(
                modifier = buildModifier(json)
            ) {
                // Add label if present
                json.get("text")?.asString?.let { label ->
                    val textColor =
                        (json.get("fontColor") ?: json.get("textColor"))?.asString?.let {
                            try {
                                Color(android.graphics.Color.parseColor(it))
                            } catch (e: Exception) {
                                Color.Black
                            }
                        } ?: Color.Black
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
                        val textColor =
                            (json.get("fontColor") ?: json.get("textColor"))?.asString?.let {
                                try {
                                    Color(android.graphics.Color.parseColor(it))
                                } catch (e: Exception) {
                                    Color.Black
                                }
                            } ?: Color.Black
                        Text(text = item, color = textColor)
                    }
                }
            }
        }

        private fun mapIconName(iconName: String): ImageVector {
            return when (iconName) {
                "circle" -> Icons.Outlined.PanoramaFishEye
                "checkmark.circle.fill" -> Icons.Filled.CheckCircle
                "star" -> Icons.Outlined.Star
                "star.fill" -> Icons.Filled.Star
                "heart" -> Icons.Outlined.FavoriteBorder
                "heart.fill" -> Icons.Filled.Favorite
                "square" -> Icons.Outlined.CheckBoxOutlineBlank
                "checkmark.square.fill" -> Icons.Default.CheckBox
                else -> Icons.Outlined.Star
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