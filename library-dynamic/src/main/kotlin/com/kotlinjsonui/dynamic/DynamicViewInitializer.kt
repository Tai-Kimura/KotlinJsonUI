package com.kotlinjsonui.dynamic

import android.util.Log

/**
 * Initializer for DynamicView support
 * This class is responsible for registering the DynamicViewRenderer
 * It should only be compiled in debug builds
 */
object DynamicViewInitializer {
    private const val TAG = "DynamicViewInitializer"
    
    /**
     * Initialize the dynamic view support
     * This will register the DynamicViewRenderer implementation
     */
    fun initialize() {
        try {
            // Register the renderer function with SafeDynamicView
            com.kotlinjsonui.components.registerDynamicRenderer(::RenderDynamicView)
            Log.d(TAG, "DynamicViewRenderer registered successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize DynamicViewRenderer", e)
        }
    }
}