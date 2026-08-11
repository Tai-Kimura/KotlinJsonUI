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

    // Application context captured at initialize() for the context-free
    // string accessor below. Assigned before the initialized guard so a
    // re-initialize still refreshes it (idempotent either way).
    @Volatile
    private var appContext: Context? = null

    /**
     * Initialize the KotlinJsonUI library
     * This should be called once during app startup
     */
    fun initialize(context: Context) {
        appContext = context.applicationContext
        if (initialized) {
            return
        }
        
        // Clear all cached JSON files
        clearAllCaches(context)
        
        // Initialize DynamicModeManager first
        DynamicModeManager.initialize(context)
        
        // Check if the host app is in debug mode using ApplicationInfo flags
        // This is more reliable than BuildConfig.DEBUG which may not exist in AGP 8.0+
        val isHostAppDebug = try {
            val appInfo = context.applicationInfo
            (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
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
     * Locale-aware string lookup for generated code that runs OUTSIDE
     * composition — a generated Data class's `defaultValue` resolves here,
     * where `stringResource` (composable-only) cannot be called. The
     * codegen face emits `KotlinJsonUI.localizedString(R.string.x, "raw")`
     * for a defaultValue whose key the layout's own strings.json section
     * declares, mirroring the generated-Data behavior on the other
     * platforms.
     *
     * Honors the AppCompat per-app language (in-app locale switching):
     * below API 33 only Activity contexts are auto-localized, so the
     * application context is re-configured with the AppCompat locale list
     * when one is set.
     *
     * Returns [fallback] before [initialize] has run (unit tests,
     * previews) or when the resource id does not resolve.
     */
    fun localizedString(resId: Int, fallback: String = ""): String {
        val ctx = appContext ?: return fallback
        return try {
            val locales = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales()
            val resolved = if (locales.isEmpty) {
                ctx
            } else {
                val config = android.content.res.Configuration(ctx.resources.configuration)
                config.setLocales(android.os.LocaleList.forLanguageTags(locales.toLanguageTags()))
                ctx.createConfigurationContext(config)
            }
            resolved.getString(resId)
        } catch (e: android.content.res.Resources.NotFoundException) {
            fallback
        }
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