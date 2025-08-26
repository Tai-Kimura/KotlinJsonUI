package com.kotlinjsonui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.DynamicViewProvider
import com.kotlinjsonui.dynamic.DynamicLayoutLoader

/**
 * A safe wrapper for DynamicView that can be used in any build variant
 * In release builds, this will simply not render anything
 * In debug builds, it will render the DynamicView if Dynamic Mode is active
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
    
    // Initialize DynamicLayoutLoader with context
    DynamicLayoutLoader.init(context)
    
    // Use the provider to render DynamicView
    DynamicViewProvider.renderDynamicView(
        layoutName = layoutName,
        data = data,
        modifier = modifier,
        onError = onError,
        onLoading = onLoading,
        content = content,
        fallback = fallback
    )
}