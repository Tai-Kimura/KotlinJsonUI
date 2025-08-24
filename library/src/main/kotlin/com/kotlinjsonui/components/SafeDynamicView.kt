package com.kotlinjsonui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.DynamicViewProvider

/**
 * A safe wrapper for DynamicView that can be used in any build variant
 * In release builds, this will simply not render anything
 * In debug builds, it will render the DynamicView if Dynamic Mode is active
 */
@Composable
fun SafeDynamicView(
    layoutName: String,
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
    
    // Use the provider to render DynamicView
    DynamicViewProvider.renderDynamicView(
        layoutName = layoutName,
        modifier = modifier,
        onError = onError,
        onLoading = onLoading,
        content = content,
        fallback = fallback
    )
}