package com.kotlinjsonui.dynamic.components

import androidx.compose.runtime.Composable
import com.google.gson.JsonObject

/**
 * Dynamic WebView Component Converter
 * Delegates to DynamicWebComponent which handles both "web" and "webview"
 * types (typed attribute access lives there, via the generated
 * WebAttributes module).
 */
class DynamicWebViewComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // WebView parses with the Web module; label the
            // UnappliedAttributes check with the authored type name.
            DynamicWebComponent.create(json, data, componentType = "WebView")
        }
    }
}
