package com.kotlinjsonui.core

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
            // Use reflection to load and instantiate DynamicViewRendererImpl
            // This ensures the class is loaded and its static initializer runs
            val rendererClass = Class.forName("com.kotlinjsonui.dynamic.DynamicViewRendererImpl")
            val rendererInstance = rendererClass.getDeclaredConstructor().newInstance()
            
            // Register the renderer with the provider
            if (rendererInstance is DynamicViewProvider.DynamicViewRenderer) {
                DynamicViewProvider.setRenderer(rendererInstance)
                Log.d(TAG, "DynamicViewRenderer registered successfully")
            } else {
                Log.e(TAG, "DynamicViewRendererImpl does not implement DynamicViewRenderer interface")
            }
        } catch (e: ClassNotFoundException) {
            Log.d(TAG, "DynamicViewRendererImpl not found - expected in release builds")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize DynamicViewRenderer", e)
        }
    }
}