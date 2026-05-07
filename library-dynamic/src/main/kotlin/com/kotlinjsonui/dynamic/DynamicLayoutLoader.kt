package com.kotlinjsonui.dynamic

import android.content.Context
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.InputStreamReader

/**
 * Loader for dynamic layout files
 * Handles loading JSON layouts from assets
 *
 * Note: This loader automatically expands includes with ID prefix support.
 * Use loadLayoutRaw() to get the raw JSON without include expansion.
 */
object DynamicLayoutLoader {
    private var context: Context? = null
    private val layoutCache = mutableMapOf<String, JsonObject?>()
    private val rawLayoutCache = mutableMapOf<String, JsonObject?>()

    /**
     * Initialize the loader with an application context
     */
    fun init(appContext: Context) {
        context = appContext.applicationContext
        IncludeExpander.init(appContext)
    }

    /**
     * Load a layout from assets/Layouts directory with include expansion.
     * Includes are expanded inline with ID prefix support.
     * Supports both direct paths (e.g., "screens/detail_view") and simple names (e.g., "detail_view")
     * For simple names, it will search in subdirectories if not found at root level
     */
    fun loadLayout(layoutName: String): JsonObject? {
        // Return from cache if available
        layoutCache[layoutName]?.let { return it.deepCopy() }

        // Load raw JSON first
        val rawJson = loadLayoutRaw(layoutName) ?: return null

        // Process includes (expand inline with ID prefixes)
        val expandedJson = IncludeExpander.processIncludes(rawJson.deepCopy())

        // Cache the expanded result
        layoutCache[layoutName] = expandedJson

        return expandedJson.deepCopy()
    }

    /**
     * Load a layout without include expansion (raw JSON)
     * Used internally by IncludeExpander to load included files
     */
    fun loadLayoutRaw(layoutName: String): JsonObject? {
        // Return from cache if available
        rawLayoutCache[layoutName]?.let { return it.deepCopy() }

        val ctx = context ?: return null

        // First, try to load from the exact path
        val directPath = "Layouts/$layoutName.json"
        try {
            ctx.assets.open(directPath).use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val json = JsonParser.parseReader(reader).asJsonObject
                    // Cache the result
                    rawLayoutCache[layoutName] = json
                    return json.deepCopy()
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
                                    rawLayoutCache[layoutName] = json
                                    rawLayoutCache[fullPath] = json
                                    return json.deepCopy()
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
     * Clear all layout caches (both expanded and raw)
     */
    fun clearCache() {
        layoutCache.clear()
        rawLayoutCache.clear()
    }

    /**
     * Remove a specific layout from both caches
     */
    fun clearCache(layoutName: String) {
        layoutCache.remove(layoutName)
        rawLayoutCache.remove(layoutName)
    }
}