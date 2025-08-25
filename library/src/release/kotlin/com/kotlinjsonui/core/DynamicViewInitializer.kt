package com.kotlinjsonui.core

import android.util.Log

/**
 * Release version of DynamicViewInitializer
 * This version does nothing since DynamicView is not available in release builds
 */
object DynamicViewInitializer {
    private const val TAG = "DynamicViewInitializer"
    
    /**
     * No-op in release builds
     */
    fun initialize() {
        // Dynamic views are not available in release builds
        Log.d(TAG, "DynamicView not available in release build")
    }
}