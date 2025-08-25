package com.kotlinjsonui.core

import android.content.Context
import android.util.Log

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
        
        // Initialize dynamic view support (only works in debug builds)
        initializeDynamicViewSupport()

        // Initialize DynamicModeManager
        DynamicModeManager.initialize(context)
        
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
            val clazz = Class.forName("com.kotlinjsonui.core.DynamicViewInitializer")
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
}