package com.kotlinjsonui.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.content.Context
import android.util.Log

/**
 * Provider for DynamicView components
 * This allows safe access to DynamicView without direct imports
 */
object DynamicViewProvider {
    private const val TAG = "DynamicViewProvider"
    
    private var rendererWrapper: Any? = null
    
    /**
     * Set the renderer wrapper
     * This should be called from the dynamic module initialization
     */
    fun setRendererWrapper(wrapper: Any) {
        this.rendererWrapper = wrapper
        Log.d(TAG, "DynamicView renderer wrapper registered")
    }
    
    /**
     * Set a renderer (legacy method for compatibility)
     */
    fun setRenderer(renderer: Any) {
        // This is kept for compatibility but not used
        Log.d(TAG, "setRenderer called - ignored in favor of wrapper")
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
        if (!DynamicModeManager.isActive()) {
            fallback()
            return false
        }
        
        val wrapper = rendererWrapper
        if (wrapper != null) {
            // Check if the wrapper has the expected class (non-composable check)
            val isValidWrapper = try {
                wrapper.javaClass.name == "com.kotlinjsonui.dynamic.DynamicViewWrapper"
            } catch (e: Exception) {
                Log.e(TAG, "Failed to check wrapper type", e)
                false
            }
            
            if (isValidWrapper) {
                // We'll use a compile-time approach instead
                // The dynamic module will provide the actual implementation
                renderDynamicInternal(layoutName, data, modifier, onError, onLoading, content, wrapper)
                return true
            }
        }
        
        Log.d(TAG, "DynamicView renderer not available - using fallback")
        fallback()
        return false
    }
    
    /**
     * Internal render method that will be called by the dynamic module
     */
    @Composable
    private fun renderDynamicInternal(
        layoutName: String,
        data: Map<String, Any>,
        modifier: Modifier,
        onError: ((String) -> Unit)?,
        onLoading: @Composable () -> Unit,
        content: @Composable (String) -> Unit,
        wrapper: Any
    ) {
        // This will be overridden by the dynamic module through a generated extension
        // For now, we can't directly call the composable from here
        Log.w(TAG, "renderDynamicInternal called but no implementation available")
    }
    
    /**
     * Create a DynamicView instance using reflection
     * Returns the instance or null if not available
     */
    fun createDynamicView(layoutName: String, context: Context): Any? {
        return try {
            val clazz = Class.forName("com.kotlinjsonui.dynamic.DynamicView")
            val constructor = clazz.getConstructor(String::class.java, Context::class.java)
            constructor.newInstance(layoutName, context)
        } catch (e: Exception) {
            Log.d(TAG, "DynamicView class not available")
            null
        }
    }
}