package com.kotlinjsonui.dynamic.components

import androidx.compose.runtime.Composable
import com.google.gson.JsonObject

/**
 * Dynamic WebView Component Converter
 * Delegates to DynamicWebComponent which handles both "web" and "webview" types.
 */
class DynamicWebViewComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            DynamicWebComponent.create(json, data)
        }
    }
}
