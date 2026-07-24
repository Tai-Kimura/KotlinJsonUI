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
 * Expression evaluation delegates to the canonical resolver
 * ([DataBindingContext.processBindings]): flat-key-first path lookup, dot
 * paths ("profile.name"), bracket array access ("items[0].title") and
 * `?? default` all behave the same as every other binding site.
 *
 * Example:
 * - Input: "Hello @{name}", data: {"name": "World"}
 * - Output: "Hello World"
 *
 * Supports default values with ?? syntax:
 * - Input: "@{name ?? 'Guest'}", data: {}
 * - Output: "Guest"
 *
 * @param text The text to process
 * @param data The data map for binding
 * @param context Optional context for resource resolution
 */
fun processDataBinding(text: String, data: Map<String, Any>, context: Context? = null): String {
    // First handle data binding via the canonical resolver
    val boundText = if (text.contains("@{")) {
        DataBindingContext.processBindings(text, data)
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
