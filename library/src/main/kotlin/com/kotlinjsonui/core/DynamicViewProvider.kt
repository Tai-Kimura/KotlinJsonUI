package com.kotlinjsonui.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.content.Context
import android.util.Log
import com.google.gson.JsonObject
import com.kotlinjsonui.BuildConfig

/**
 * Provider for DynamicView components
 * This allows safe access to DynamicView without direct imports
 */
object DynamicViewProvider {
    private const val TAG = "DynamicViewProvider"
    
    /**
     * Interface for DynamicView rendering
     * Implementation will be provided by debug builds
     */
    interface DynamicViewRenderer {
        @Composable
        fun render(
            layoutName: String,
            data: Map<String, Any> = emptyMap(),
            modifier: Modifier,
            onError: ((String) -> Unit)?,
            onLoading: @Composable () -> Unit,
            content: @Composable (String) -> Unit
        )
    }
    
    private var renderer: DynamicViewRenderer? = null
    
    /**
     * Initialize the provider with a renderer
     * This should be called from debug-only code
     */
    fun setRenderer(renderer: DynamicViewRenderer) {
        if (BuildConfig.DEBUG) {
            this.renderer = renderer
            Log.d(TAG, "DynamicView renderer registered")
        }
    }
    
    /**
     * Render a DynamicView if available
     * Returns true if rendered, false if not available
     */
    @Composable
    fun renderDynamicView(
        layoutName: String,
        data: Map<String, Any> = emptyMap(),
        modifier: Modifier = Modifier,
        onError: ((String) -> Unit)? = null,
        onLoading: @Composable () -> Unit = {},
        content: @Composable (String) -> Unit = {},
        fallback: @Composable () -> Unit = {}
    ): Boolean {
        if (!BuildConfig.DEBUG || !DynamicModeManager.isActive()) {
            fallback()
            return false
        }
        
        val currentRenderer = renderer
        if (currentRenderer != null) {
            currentRenderer.render(
                layoutName = layoutName,
                data = data,
                modifier = modifier,
                onError = onError,
                onLoading = onLoading,
                content = content
            )
            return true
        } else {
            Log.w(TAG, "DynamicView renderer not available")
            fallback()
            return false
        }
    }
    
    /**
     * Create a DynamicView instance
     * Returns the instance or null if not available
     */
    fun createDynamicView(layoutName: String, context: Context): Any? {
        if (!BuildConfig.DEBUG) {
            return null
        }
        
        return try {
            val clazz = Class.forName("com.kotlinjsonui.dynamic.DynamicView")
            val constructor = clazz.getConstructor(String::class.java, Context::class.java)
            constructor.newInstance(layoutName, context)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create DynamicView", e)
            null
        }
    }
}