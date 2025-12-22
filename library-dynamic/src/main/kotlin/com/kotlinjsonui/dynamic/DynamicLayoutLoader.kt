package com.kotlinjsonui.dynamic

import android.content.Context
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.InputStreamReader

/**
 * Loader for dynamic layout files
 * Handles loading JSON layouts from assets
 */
object DynamicLayoutLoader {
    private var context: Context? = null
    private val layoutCache = mutableMapOf<String, JsonObject?>()
    
    /**
     * Initialize the loader with an application context
     */
    fun init(appContext: Context) {
        context = appContext.applicationContext
    }
    
    /**
     * Load a layout from assets/Layouts directory
     * Supports both direct paths (e.g., "screens/detail_view") and simple names (e.g., "detail_view")
     * For simple names, it will search in subdirectories if not found at root level
     */
    fun loadLayout(layoutName: String): JsonObject? {
        // Return from cache if available
        layoutCache[layoutName]?.let { return it }

        val ctx = context ?: return null

        // First, try to load from the exact path
        val directPath = "Layouts/$layoutName.json"
        try {
            ctx.assets.open(directPath).use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val json = JsonParser.parseReader(reader).asJsonObject
                    // Cache the result
                    layoutCache[layoutName] = json
                    return json
                }
            }
        } catch (e: Exception) {
            // Not found at direct path, try searching in subdirectories
        }

        // If the layoutName doesn't contain a path separator, search in subdirectories
        if (!layoutName.contains("/")) {
            return searchInSubdirectories(ctx, layoutName)
        }

        return null
    }

    /**
     * Search for a layout file in subdirectories of Layouts/
     */
    private fun searchInSubdirectories(ctx: Context, layoutName: String): JsonObject? {
        try {
            // List all items in Layouts directory
            val layoutsDir = ctx.assets.list("Layouts") ?: return null

            for (item in layoutsDir) {
                // Skip non-directories (files like styles.json, Resources folder, etc.)
                try {
                    val subItems = ctx.assets.list("Layouts/$item")
                    if (subItems != null && subItems.isNotEmpty()) {
                        // This is a directory, try to find the layout here
                        val subPath = "Layouts/$item/$layoutName.json"
                        try {
                            ctx.assets.open(subPath).use { inputStream ->
                                InputStreamReader(inputStream).use { reader ->
                                    val json = JsonParser.parseReader(reader).asJsonObject
                                    // Cache with the full path for future lookups
                                    val fullPath = "$item/$layoutName"
                                    layoutCache[layoutName] = json
                                    layoutCache[fullPath] = json
                                    return json
                                }
                            }
                        } catch (e: Exception) {
                            // Not found in this subdirectory, continue searching
                        }
                    }
                } catch (e: Exception) {
                    // Not a directory or error accessing, skip
                }
            }
        } catch (e: Exception) {
            // Error listing directories
        }

        return null
    }
    
    /**
     * Clear the layout cache
     */
    fun clearCache() {
        layoutCache.clear()
    }
    
    /**
     * Remove a specific layout from cache
     */
    fun clearCache(layoutName: String) {
        layoutCache.remove(layoutName)
    }
}