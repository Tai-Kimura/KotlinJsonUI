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
     */
    fun loadLayout(layoutName: String): JsonObject? {
        // Return from cache if available
        layoutCache[layoutName]?.let { return it }
        
        val ctx = context ?: return null
        
        return try {
            val path = "Layouts/$layoutName.json"
            ctx.assets.open(path).use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val json = JsonParser.parseReader(reader).asJsonObject
                    // Cache the result
                    layoutCache[layoutName] = json
                    json
                }
            }
        } catch (e: Exception) {
            null
        }
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