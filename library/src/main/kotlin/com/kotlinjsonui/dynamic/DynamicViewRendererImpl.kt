package com.kotlinjsonui.dynamic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kotlinjsonui.core.DynamicViewProvider

/**
 * Implementation of DynamicViewRenderer
 * This class is only compiled in DEBUG builds
 */
class DynamicViewRendererImpl : DynamicViewProvider.DynamicViewRenderer {
    
    @Composable
    override fun render(
        layoutName: String,
        data: Map<String, Any>,
        modifier: Modifier,
        onError: ((String) -> Unit)?,
        onLoading: @Composable () -> Unit,
        content: @Composable (String) -> Unit
    ) {
        // For now, just render an empty box since we don't have a DynamicView that takes layoutName
        // This is a placeholder implementation
        // TODO: Implement proper dynamic loading from layoutName
    }
}

/**
 * Auto-registration of the renderer
 * This will be called when the class is loaded
 */
private val autoRegister = run {
    DynamicViewProvider.setRenderer(DynamicViewRendererImpl())
}