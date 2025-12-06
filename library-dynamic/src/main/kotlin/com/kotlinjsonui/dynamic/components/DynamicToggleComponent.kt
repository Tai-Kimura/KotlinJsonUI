package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.processDataBinding
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder

/**
 * Dynamic Toggle Component Converter
 * Converts JSON to Toggle (Switch) composable at runtime
 *
 * Toggle is an alias for Switch. Switch is the primary component name.
 * This is maintained for backward compatibility. New code should use Switch.
 *
 * Supported JSON attributes (matching Ruby implementation):
 * - isOn: Boolean or @{variable} for checked state
 * - bind: @{variable} for two-way binding
 * - onValueChange: String method name for change handler
 * - enabled: Boolean to enable/disable
 * - onTintColor: String hex color for checked track
 * - thumbTintColor: String hex color for checked thumb
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 */
class DynamicToggleComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Delegate to DynamicSwitchComponent for actual implementation
            // Toggle and Switch are the same component, just different names
            DynamicSwitchComponent.create(json, data)
        }
    }
}
