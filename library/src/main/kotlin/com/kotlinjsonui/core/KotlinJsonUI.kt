package com.kotlinjsonui.core

import android.content.Context
import android.util.Log
import java.io.File

/**
 * Main entry point for KotlinJsonUI library initialization
 */
object KotlinJsonUI {
    private const val TAG = "KotlinJsonUI"
    private var initialized = false
    
    /**
     * Initialize the KotlinJsonUI library
     * This should be called once during app startup
     */
    fun initialize(context: Context) {
        if (initialized) {
            return
        }
        
        // Clear all cached JSON files
        clearAllCaches(context)
        
        // Initialize DynamicModeManager first
        DynamicModeManager.initialize(context)
        
        // Check if the host app is in debug mode
        val isHostAppDebug = try {
            val buildConfigClass = Class.forName("${context.packageName}.BuildConfig")
            val debugField = buildConfigClass.getField("DEBUG")
            debugField.getBoolean(null)
        } catch (e: Exception) {
            false
        }
        
        // Automatically enable dynamic mode for debug builds
        if (isHostAppDebug) {
            DynamicModeManager.setDynamicModeEnabled(context, true)
            Log.d(TAG, "Dynamic mode enabled for debug build")
        }
        
        // Initialize dynamic view support (only works when dynamic module is available)
        initializeDynamicViewSupport()
        
        initialized = true
        Log.d(TAG, "KotlinJsonUI initialized")
    }
    
    /**
     * Internal method to initialize dynamic view support
     * This will call the appropriate initializer based on build type
     */
    private fun initializeDynamicViewSupport() {
        try {
            // Use reflection to call DynamicViewInitializer if it exists
            // This avoids NoClassDefFoundError when the class doesn't exist
            val clazz = Class.forName("com.kotlinjsonui.dynamic.DynamicViewInitializer")
            val initMethod = clazz.getMethod("initialize")
            val instance = clazz.getField("INSTANCE").get(null)
            initMethod.invoke(instance)
            Log.d(TAG, "Dynamic view support initialized")
        } catch (e: ClassNotFoundException) {
            // Expected in release builds or when dynamic view is not available
            Log.d(TAG, "Dynamic view support not available")
        } catch (e: Exception) {
            // Other errors during initialization
            Log.e(TAG, "Failed to initialize dynamic view support", e)
        }
    }
    
    /**
     * Clear all cached JSON files
     * This includes HotLoader caches, .kjui_cache, and other temporary JSON storage
     */
    private fun clearAllCaches(context: Context) {
        try {
            // Clear HotLoader caches
            clearHotLoaderCache(context)
            
            // Clear .kjui_cache directory
            clearKjuiCache(context)
            
            // Clear any other JSON caches
            clearGeneralJsonCache(context)
            
            Log.d(TAG, "All JSON caches cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing caches", e)
        }
    }
    
    /**
     * Clear HotLoader cached layouts and styles
     */
    private fun clearHotLoaderCache(context: Context) {
        try {
            val hotloaderLayouts = File(context.cacheDir, "hotloader_layouts")
            if (hotloaderLayouts.exists()) {
                hotloaderLayouts.deleteRecursively()
                Log.d(TAG, "Cleared HotLoader layouts cache")
            }
            
            val hotloaderStyles = File(context.cacheDir, "hotloader_styles")
            if (hotloaderStyles.exists()) {
                hotloaderStyles.deleteRecursively()
                Log.d(TAG, "Cleared HotLoader styles cache")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing HotLoader cache", e)
        }
    }
    
    /**
     * Clear .kjui_cache directory
     */
    private fun clearKjuiCache(context: Context) {
        try {
            // Check in app's files directory
            val filesKjuiCache = File(context.filesDir.parentFile, ".kjui_cache")
            if (filesKjuiCache.exists()) {
                filesKjuiCache.deleteRecursively()
                Log.d(TAG, "Cleared .kjui_cache from files directory")
            }
            
            // Check in cache directory
            val cacheKjuiCache = File(context.cacheDir, ".kjui_cache")
            if (cacheKjuiCache.exists()) {
                cacheKjuiCache.deleteRecursively()
                Log.d(TAG, "Cleared .kjui_cache from cache directory")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing .kjui_cache", e)
        }
    }
    
    /**
     * Clear any other JSON cache files
     */
    private fun clearGeneralJsonCache(context: Context) {
        try {
            // Clear any JSON files in cache directory
            context.cacheDir.listFiles()?.filter { 
                it.extension == "json" || it.name.contains("json", ignoreCase = true)
            }?.forEach { file ->
                if (file.delete()) {
                    Log.d(TAG, "Deleted cache file: ${file.name}")
                }
            }
            
            // Clear dynamic_layouts cache if exists
            val dynamicLayouts = File(context.cacheDir, "dynamic_layouts")
            if (dynamicLayouts.exists()) {
                dynamicLayouts.deleteRecursively()
                Log.d(TAG, "Cleared dynamic_layouts cache")
            }
            
            // Clear dynamic_styles cache if exists
            val dynamicStyles = File(context.cacheDir, "dynamic_styles")
            if (dynamicStyles.exists()) {
                dynamicStyles.deleteRecursively()
                Log.d(TAG, "Cleared dynamic_styles cache")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing general JSON cache", e)
        }
    }
}