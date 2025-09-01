package com.kotlinjsonui.dynamic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Simple renderer that can be called directly without interfaces
 */
@Composable
fun RenderDynamicView(
    layoutName: String,
    data: Map<String, Any> = emptyMap(),
    modifier: Modifier = Modifier,
    onError: ((String) -> Unit)? = null,
    onLoading: @Composable () -> Unit = {},
    content: @Composable (String) -> Unit = {}
) {
    val renderer = DynamicViewRendererImpl()
    renderer.render(
        layoutName = layoutName,
        data = data,
        modifier = modifier,
        onError = onError,
        onLoading = onLoading,
        content = content
    )
}