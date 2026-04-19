package com.kotlinjsonui.dynamic

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import java.io.InputStreamReader

/**
 * Loads and manages styles for dynamic components from JSON files
 * Styles are loaded from HotLoader cache first, then assets/Styles/ directory
 */
object DynamicStyleLoader {
    private const val TAG = "DynamicStyleLoader"
    private const val STYLES_DIR = "Styles"

    private val styleCache = mutableMapOf<String, JsonObject>()
    private val gson = Gson()

    /**
     * Load a style by name from the Styles directory
     * First checks HotLoader cache, then falls back to assets
     * @param context Android context for asset access
     * @param styleName Name of the style file (without .json extension)
     * @return JsonObject containing the style properties, or null if not found
     */
    fun loadStyle(context: Context, styleName: String): JsonObject? {
        // Check memory cache first
        styleCache[styleName]?.let { return it }

        // Try to load from HotLoader cache first (for hot reload support)
        val cachedStyle = loadFromHotLoaderCache(context, styleName)
        if (cachedStyle != null) {
            styleCache[styleName] = cachedStyle
            Log.d(TAG, "Loaded style from HotLoader cache: $styleName")
            return cachedStyle
        }

        // Fall back to loading from assets
        return try {
            val fileName = if (styleName.endsWith(".json")) {
                "$STYLES_DIR/$styleName"
            } else {
                "$STYLES_DIR/$styleName.json"
            }

            context.assets.open(fileName).use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val jsonElement = JsonParser.parseReader(reader)
                    if (jsonElement.isJsonObject) {
                        val styleJson = jsonElement.asJsonObject
                        // Cache the loaded style
                        styleCache[styleName] = styleJson
                        Log.d(TAG, "Loaded style: $styleName")
                        styleJson
                    } else {
                        Log.e(TAG, "Style file is not a JSON object: $styleName")
                        null
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Style not found or error loading: $styleName - ${e.message}")
            null
        }
    }

    /**
     * Load style from HotLoader cache directory
     */
    private fun loadFromHotLoaderCache(context: Context, styleName: String): JsonObject? {
        return try {
            val cacheDir = File(context.cacheDir, "hotloader_styles")
            val fileName = if (styleName.endsWith(".json")) styleName else "$styleName.json"
            val cacheFile = File(cacheDir, fileName)

            if (cacheFile.exists()) {
                val content = cacheFile.readText()
                val jsonElement = JsonParser.parseString(content)
                if (jsonElement.isJsonObject) {
                    jsonElement.asJsonObject
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.d(TAG, "Failed to load style from HotLoader cache: $styleName - ${e.message}")
            null
        }
    }
    
    /**
     * Apply a style to a JSON object by merging style properties
     * Style properties are applied first, then overridden by inline properties
     * 
     * @param context Android context for asset access
     * @param json The JSON object to apply styles to
     * @return A new JsonObject with styles applied
     */
    fun applyStyle(context: Context, json: JsonObject): JsonObject {
        val styleName = json.get("style")?.asString ?: return json
        
        val styleJson = loadStyle(context, styleName) ?: return json
        
        // Create a new JsonObject with merged properties
        val mergedJson = JsonObject()
        
        // First, add all style properties
        styleJson.entrySet().forEach { entry ->
            // Skip the 'style' property itself to avoid recursion
            if (entry.key != "style") {
                mergedJson.add(entry.key, entry.value)
            }
        }
        
        // Then, override with inline properties from the original json
        json.entrySet().forEach { entry ->
            // Always include inline properties (they override style properties)
            mergedJson.add(entry.key, entry.value)
        }
        
        Log.d(TAG, "Applied style '$styleName' to component")
        return mergedJson
    }
    
    /**
     * Clear the style cache
     */
    fun clearCache() {
        styleCache.clear()
        Log.d(TAG, "Style cache cleared")
    }
    
    /**
     * Preload multiple styles into cache
     * @param context Android context for asset access
     * @param styleNames List of style names to preload
     */
    fun preloadStyles(context: Context, vararg styleNames: String) {
        styleNames.forEach { styleName ->
            loadStyle(context, styleName)
        }
    }
}