package com.kotlinjsonui.dynamic

import android.content.Context
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
 * Also resolves string resources using ResourceCache.
 * 
 * Example:
 * - Input: "Hello @{name}", data: {"name": "World"}
 * - Output: "Hello World"
 * 
 * Supports default values with ?? syntax:
 * - Input: "@{name ?? Guest}", data: {} 
 * - Output: "Guest"
 * 
 * @param text The text to process
 * @param data The data map for binding
 * @param context Optional context for resource resolution
 */
fun processDataBinding(text: String, data: Map<String, Any>, context: Context? = null): String {
    // First handle data binding
    val boundText = if (text.contains("@{")) {
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
        result
    } else {
        text
    }
    
    // Then resolve string resources if context is available
    return context?.let { ctx ->
        ResourceCache.resolveString(boundText, ctx)
    } ?: boundText
}

/**
 * Overload for backward compatibility without context
 */
fun processDataBinding(text: String, data: Map<String, Any>): String {
    return processDataBinding(text, data, null)
}