package com.kotlinjsonui.dynamic

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.InputStreamReader

/**
 * ResourceCache manages cached resources for dynamic UI rendering.
 * It loads and caches strings.json and colors.json files for efficient resource resolution.
 * 
 * Features:
 * - Lazy loading: Resources are loaded only when first accessed
 * - Singleton pattern: Single instance shared across the app
 * - Flattened string structure: Nested JSON is flattened to file_key format
 * - Fallback support: Returns original value if resource not found
 */
object ResourceCache {
    private const val TAG = "ResourceCache"
    
    // Cached resources
    private var stringsCache: Map<String, String>? = null
    private var colorsCache: Map<String, String>? = null
    
    // Flag to track if loading has been attempted
    private var stringsLoadAttempted = false
    private var colorsLoadAttempted = false
    
    /**
     * Initialize the cache with context.
     * This can be called multiple times safely.
     */
    fun init(context: Context) {
        // Load resources lazily when first accessed
        loadStringsIfNeeded(context)
        loadColorsIfNeeded(context)
    }
    
    /**
     * Get a string resource by key.
     * Returns the localized string if found, otherwise returns the key itself.
     * 
     * @param key The string key to look up
     * @param context Context for loading resources if not yet loaded
     * @return The localized string or the original key
     */
    fun getString(key: String, context: Context): String {
        loadStringsIfNeeded(context)
        return stringsCache?.get(key) ?: key
    }
    
    /**
     * Get a color resource by name.
     * Returns the color hex value if found, otherwise returns null.
     * 
     * @param colorName The color name to look up
     * @param context Context for loading resources if not yet loaded
     * @return The color hex value or null
     */
    fun getColor(colorName: String, context: Context): String? {
        loadColorsIfNeeded(context)
        return colorsCache?.get(colorName)
    }
    
    /**
     * Check if a string key exists in the cache
     */
    fun hasString(key: String, context: Context): Boolean {
        loadStringsIfNeeded(context)
        return stringsCache?.containsKey(key) == true
    }
    
    /**
     * Check if a color name exists in the cache
     */
    fun hasColor(colorName: String, context: Context): Boolean {
        loadColorsIfNeeded(context)
        return colorsCache?.containsKey(colorName) == true
    }
    
    /**
     * Load strings.json if not already loaded
     */
    private fun loadStringsIfNeeded(context: Context) {
        if (stringsLoadAttempted) return
        stringsLoadAttempted = true
        
        try {
            // Try to load from Layouts/Resources/strings.json
            val jsonString = try {
                context.assets.open("Layouts/Resources/strings.json").use { inputStream ->
                    InputStreamReader(inputStream).use { it.readText() }
                }
            } catch (e: Exception) {
                // Try alternate path
                try {
                    context.assets.open("strings.json").use { inputStream ->
                        InputStreamReader(inputStream).use { it.readText() }
                    }
                } catch (e2: Exception) {
                    Log.d(TAG, "strings.json not found, using fallback")
                    return
                }
            }
            
            // Parse and flatten the JSON structure
            val gson = Gson()
            val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)
            val flattenedStrings = mutableMapOf<String, String>()
            
            // Flatten the nested structure
            // Format: { "file1": { "key1": "value1" }, "file2": { "key2": "value2" } }
            // Becomes: { "file1_key1": "value1", "file2_key2": "value2" }
            for ((fileName, fileObject) in jsonObject.entrySet()) {
                if (fileObject.isJsonObject) {
                    val fileJsonObject = fileObject.asJsonObject
                    for ((key, value) in fileJsonObject.entrySet()) {
                        if (value.isJsonPrimitive) {
                            // Create flattened key: filename_key
                            val flatKey = "${fileName}_${key}"
                            flattenedStrings[flatKey] = value.asString
                            
                            // Also store without prefix for backward compatibility
                            if (!flattenedStrings.containsKey(key)) {
                                flattenedStrings[key] = value.asString
                            }
                        }
                    }
                }
            }
            
            stringsCache = flattenedStrings
            Log.d(TAG, "Loaded ${flattenedStrings.size} strings")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load strings.json", e)
        }
    }
    
    /**
     * Load colors.json if not already loaded
     */
    private fun loadColorsIfNeeded(context: Context) {
        if (colorsLoadAttempted) return
        colorsLoadAttempted = true
        
        try {
            // Try to load from Layouts/Resources/colors.json
            val jsonString = try {
                context.assets.open("Layouts/Resources/colors.json").use { inputStream ->
                    InputStreamReader(inputStream).use { it.readText() }
                }
            } catch (e: Exception) {
                // Try alternate path
                try {
                    context.assets.open("colors.json").use { inputStream ->
                        InputStreamReader(inputStream).use { it.readText() }
                    }
                } catch (e2: Exception) {
                    Log.d(TAG, "colors.json not found, using fallback")
                    return
                }
            }
            
            // Parse the JSON
            val gson = Gson()
            val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)
            val colors = mutableMapOf<String, String>()
            
            // Extract color mappings
            // Format: { "color_name": "#HEXVALUE", ... }
            for ((key, value) in jsonObject.entrySet()) {
                if (value.isJsonPrimitive) {
                    colors[key] = value.asString
                }
            }
            
            colorsCache = colors
            Log.d(TAG, "Loaded ${colors.size} colors")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load colors.json", e)
        }
    }
    
    /**
     * Clear the cache (useful for testing or when resources are updated)
     */
    fun clearCache() {
        stringsCache = null
        colorsCache = null
        stringsLoadAttempted = false
        colorsLoadAttempted = false
    }
    
    /**
     * Process a text string that might be a string resource key.
     * If the text matches a key in strings cache, returns the localized value.
     * Otherwise returns the original text.
     * 
     * @param text The text that might be a resource key
     * @param context Context for loading resources if not yet loaded
     * @return The resolved string or original text
     */
    fun resolveString(text: String?, context: Context): String {
        if (text.isNullOrEmpty()) return ""
        
        // Check if it's a data binding expression - don't resolve those
        if (text.contains("@{")) return text
        
        // Try to resolve as a string resource
        loadStringsIfNeeded(context)
        return stringsCache?.get(text) ?: text
    }
    
    /**
     * Process a color string that might be a color resource name.
     * If the color name exists in colors cache, returns the hex value.
     * Otherwise attempts to parse as a hex color.
     * 
     * @param colorName The color name or hex value
     * @param context Context for loading resources if not yet loaded
     * @return The resolved color hex value or null if invalid
     */
    fun resolveColor(colorName: String?, context: Context): String? {
        if (colorName.isNullOrEmpty()) return null
        
        // Check if it's a data binding expression - don't resolve those
        if (colorName.contains("@{")) return colorName
        
        // If it already looks like a hex color, return it
        if (colorName.startsWith("#")) {
            return colorName
        }
        
        // Try to resolve as a color resource
        loadColorsIfNeeded(context)
        colorsCache?.get(colorName)?.let { return it }
        
        // If not found, try to parse as hex without #
        return if (colorName.matches(Regex("[0-9a-fA-F]{6,8}"))) {
            "#$colorName"
        } else {
            null
        }
    }
}