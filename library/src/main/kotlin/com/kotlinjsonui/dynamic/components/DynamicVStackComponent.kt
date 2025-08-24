package com.kotlinjsonui.dynamic.components

import androidx.compose.runtime.Composable
import com.google.gson.JsonObject

/**
 * Dynamic VStack Component Converter
 * Converts JSON to Column (vertical stack) composable at runtime
 * 
 * This is a convenience wrapper around DynamicContainerComponent
 * that forces vertical orientation.
 * 
 * Supported JSON attributes (matching Ruby implementation):
 * - All attributes from DynamicContainerComponent
 * - orientation is always set to "vertical"
 */
class DynamicVStackComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Create a copy of the JSON with orientation set to vertical
            val modifiedJson = json.deepCopy()
            modifiedJson.addProperty("orientation", "vertical")
            
            // Delegate to DynamicContainerComponent
            DynamicContainerComponent.create(modifiedJson, data)
        }
    }
}