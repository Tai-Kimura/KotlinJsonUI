package com.kotlinjsonui.dynamic.components

import androidx.compose.runtime.Composable
import com.google.gson.JsonObject

/**
 * Dynamic HStack Component Converter
 * Converts JSON to Row (horizontal stack) composable at runtime
 * 
 * This is a convenience wrapper around DynamicContainerComponent
 * that forces horizontal orientation.
 * 
 * Supported JSON attributes (matching Ruby implementation):
 * - All attributes from DynamicContainerComponent
 * - orientation is always set to "horizontal"
 */
class DynamicHStackComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Create a copy of the JSON with orientation set to horizontal
            val modifiedJson = json.deepCopy()
            modifiedJson.addProperty("orientation", "horizontal")
            
            // Delegate to DynamicContainerComponent
            DynamicContainerComponent.create(modifiedJson, data)
        }
    }
}