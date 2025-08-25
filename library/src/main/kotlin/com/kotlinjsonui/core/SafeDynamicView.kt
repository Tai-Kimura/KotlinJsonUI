package com.kotlinjsonui.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.util.Log
import com.google.gson.JsonObject

/**
 * A safe wrapper around DynamicView that provides fallback behavior
 * when DynamicView is not available (e.g., in release builds)
 */
@Composable
fun SafeDynamicView(
    layoutName: String,
    data: Map<String, Any> = emptyMap(),
    fallback: @Composable () -> Unit = { DefaultFallback() },
    onError: ((String) -> Unit)? = null,
    onLoading: @Composable () -> Unit = { DefaultLoading() },
    content: @Composable (String) -> Unit = {}
) {
    // Check if dynamic mode is active
    if (!DynamicModeManager.isActive()) {
        fallback()
        return
    }
    
    // Try to render using DynamicViewProvider (will work in debug, fallback in release)
    val rendered = DynamicViewProvider.renderDynamicView(
        layoutName = layoutName,
        data = data,
        modifier = Modifier,
        onError = onError,
        onLoading = onLoading,
        content = content,
        fallback = fallback
    )
    
    if (!rendered) {
        // If rendering failed, show fallback
        fallback()
    }
}

/**
 * Alternative overload for JSON object
 */
@Composable
fun SafeDynamicView(
    json: JsonObject,
    data: Map<String, Any> = emptyMap(),
    fallback: @Composable () -> Unit = { DefaultFallback() }
) {
    if (!DynamicModeManager.isActive()) {
        fallback()
        return
    }
    
    // For JSON objects, we need to use reflection to call DynamicView
    // This is a limitation - we can't directly use JSON-based DynamicView
    // Users should use layout names instead
    Log.w("SafeDynamicView", "JSON-based SafeDynamicView is not fully supported. Use layout names instead.")
    fallback()
}

@Composable
private fun DefaultFallback() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Dynamic view not available")
    }
}

@Composable
private fun DefaultLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}