package com.kotlinjsonui.dynamic

import androidx.compose.runtime.Composable
import com.google.gson.JsonObject

/**
 * Base interface for all dynamic component converters.
 * Each component converter should implement this interface to provide
 * a consistent way to create Compose UI from JSON.
 */
interface DynamicComponent {
    /**
     * Creates a Composable UI element from JSON configuration.
     * 
     * @param json The JSON object containing component configuration
     * @param data A map of data for binding. Keys in the JSON using @{key} syntax
     *             will be replaced with corresponding values from this map.
     *             This map can also contain functions that will be called for events.
     *             For example, if JSON has "onclick": "handleClick", the data map
     *             should contain "handleClick" -> { /* function */ }
     */
    @Composable
    fun create(
        json: JsonObject,
        data: Map<String, Any> = emptyMap()
    )
}

/**
 * Utility function to process data binding in text.
 * Replaces @{key} patterns with values from the data map.
 * 
 * Example:
 * - Input: "Hello @{name}", data: {"name": "World"}
 * - Output: "Hello World"
 * 
 * Supports default values with ?? syntax:
 * - Input: "@{name ?? Guest}", data: {} 
 * - Output: "Guest"
 */
fun processDataBinding(text: String, data: Map<String, Any>): String {
    if (!text.contains("@{")) return text
    
    var result = text
    val pattern = "@\\{([^}]+)\\}".toRegex()
    pattern.findAll(text).forEach { match ->
        val variable = match.groupValues[1]
        val value = if (variable.contains(" ?? ")) {
            // Handle default value syntax
            val parts = variable.split(" ?? ")
            val varName = parts[0].trim()
            data[varName]?.toString() ?: parts[1].trim().removeSurrounding("\"")
        } else {
            data[variable]?.toString() ?: ""
        }
        result = result.replace(match.value, value)
    }
    return result
}