package com.kotlinjsonui.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.util.Log
import com.kotlinjsonui.dynamic.DynamicView

/**
 * Debug version of DynamicViewInitializer
 * This version actually initializes the DynamicView renderer
 */
object DynamicViewInitializer {
    private const val TAG = "DynamicViewInitializer"
    
    /**
     * Initialize the DynamicViewProvider with the actual DynamicView implementation
     */
    fun initialize() {
        try {
            DynamicViewProvider.setRenderer(DynamicViewRendererImpl())
            Log.d(TAG, "DynamicView renderer initialized for debug build")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize DynamicView renderer", e)
        }
    }
}

/**
 * Implementation of DynamicViewRenderer that uses the actual DynamicView
 */
private class DynamicViewRendererImpl : DynamicViewProvider.DynamicViewRenderer {
    @Composable
    override fun render(
        layoutName: String,
        data: Map<String, Any>,
        modifier: Modifier,
        onError: ((String) -> Unit)?,
        onLoading: @Composable () -> Unit,
        content: @Composable (String) -> Unit
    ) {
        DynamicView(
            layoutName = layoutName,
            data = data,
            onError = { e ->
                onError?.invoke(e.message ?: "Unknown error")
            }
        )
    }
}