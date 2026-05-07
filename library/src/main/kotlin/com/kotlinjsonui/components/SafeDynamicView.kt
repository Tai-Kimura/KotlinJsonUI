package com.kotlinjsonui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kotlinjsonui.core.DynamicModeManager
import android.util.Log

/**
 * A safe wrapper for DynamicView that can be used in any build variant
 * In release builds, this will simply not render anything
 * In debug builds, it will render the DynamicView if Dynamic Mode is active
 *
 * Note: The parent Activity/Composable should observe DynamicModeManager.isDynamicModeEnabled
 * with collectAsState() and use key() to trigger recomposition when the mode changes.
 *
 * @param layoutName Layout name to load from assets
 * @param data Map of data for binding. @{key} in JSON will be replaced with data[key].
 *             Functions in the data map can be referenced by name for event handlers.
 * @param modifier Optional modifier for the view
 * @param fallback Composable to show if dynamic view is not available
 * @param onError Optional error handler
 * @param onLoading Optional loading state composable
 * @param content Optional content composable
 */
@Composable
fun SafeDynamicView(
    layoutName: String,
    data: Map<String, Any> = emptyMap(),
    modifier: Modifier = Modifier,
    fallback: @Composable () -> Unit = {},
    onError: ((String) -> Unit)? = null,
    onLoading: @Composable () -> Unit = {},
    content: @Composable (String) -> Unit = {}
) {
    val context = LocalContext.current

    // Initialize DynamicModeManager if needed
    if (!DynamicModeManager.isInitialized) {
        DynamicModeManager.initialize(context)
    }

    // Check if dynamic mode is active
    // Note: Recomposition is triggered by the parent observing isDynamicModeEnabled
    if (!DynamicModeManager.isActive()) {
        Log.d("SafeDynamicView", "Dynamic mode not active")
        fallback()
        return
    }
    
    Log.d("SafeDynamicView", "Dynamic mode is active, checking for renderer...")
    
    // Check if dynamic module is available (non-composable check)
    val isDynamicAvailable = try {
        Class.forName("com.kotlinjsonui.dynamic.DynamicRendererKt")
        Log.d("SafeDynamicView", "Dynamic module class found")
        true
    } catch (e: ClassNotFoundException) {
        Log.d("SafeDynamicView", "Dynamic module not available: ${e.message}")
        false
    }
    
    if (!isDynamicAvailable) {
        Log.d("SafeDynamicView", "Dynamic module check failed")
        fallback()
        return
    }
    
    // Check if we have a registered renderer
    Log.d("SafeDynamicView", "Checking for registered renderer... hasDynamicRenderer=${hasDynamicRenderer()}")
    if (hasDynamicRenderer()) {
        Log.d("SafeDynamicView", "Invoking dynamic renderer for layout: $layoutName")
        // This will be handled by the generated code in the dynamic module
        invokeDynamicRenderer(layoutName, data, modifier, onError, onLoading, content)
    } else {
        Log.d("SafeDynamicView", "Dynamic renderer not registered, using fallback")
        fallback()
    }
}

// These will be provided by the dynamic module when available
private var dynamicRendererAvailable = false
private var dynamicRendererFunction: (@Composable (String, Map<String, Any>, Modifier, ((String) -> Unit)?, @Composable () -> Unit, @Composable (String) -> Unit) -> Unit)? = null

/**
 * Register a dynamic renderer function
 * This will be called by the dynamic module during initialization
 */
fun registerDynamicRenderer(renderer: @Composable (String, Map<String, Any>, Modifier, ((String) -> Unit)?, @Composable () -> Unit, @Composable (String) -> Unit) -> Unit) {
    dynamicRendererFunction = renderer
    dynamicRendererAvailable = true
    Log.d("SafeDynamicView", "Dynamic renderer registered")
}

private fun hasDynamicRenderer(): Boolean = dynamicRendererAvailable

@Composable
private fun invokeDynamicRenderer(
    layoutName: String,
    data: Map<String, Any>,
    modifier: Modifier,
    onError: ((String) -> Unit)?,
    onLoading: @Composable () -> Unit,
    content: @Composable (String) -> Unit
) {
    dynamicRendererFunction?.invoke(layoutName, data, modifier, onError, onLoading, content)
}