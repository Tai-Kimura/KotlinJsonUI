package com.kotlinjsonui.dynamic.components

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver

/**
 * Dynamic Web Component Converter
 * Converts JSON to WebView composable at runtime.
 * Merges web_component.rb + webview_component.rb into a single component.
 *
 * Supported JSON attributes:
 * - url: String URL or @{binding} for web page
 * - javaScriptEnabled: Boolean (default: true)
 * - userAgent: String custom user agent
 * - allowZoom: Boolean to enable builtInZoomControls
 * - cornerRadius: Float corner radius
 * - width/height: dimensions (defaults to fillMaxSize if neither specified)
 * - Standard modifier attributes (padding, margins, alpha, onClick, etc.)
 *
 * Modifier order: testTag → margins → size (fillMaxSize default) → alpha → clickable → padding → cornerRadius clip
 */
class DynamicWebComponent {
    companion object {
        @SuppressLint("SetJavaScriptEnabled")
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Apply lifecycle effects first
            ModifierBuilder.ApplyLifecycleEffects(json, data)

            val context = LocalContext.current

            // Parse URL with binding support
            val url = ResourceResolver.resolveText(json, "url", data, context)

            // Parse WebView settings
            val javaScriptEnabled = ResourceResolver.resolveBoolean(json, "javaScriptEnabled", data, true)
            val userAgent = ResourceResolver.resolveString(json, "userAgent", data)
            val allowZoom = ResourceResolver.resolveBoolean(json, "allowZoom", data, false)

            // Build modifier: testTag → margins → size → alpha → clickable → padding
            val defaultFillMax = !json.has("width") && !json.has("height")
            var modifier = ModifierBuilder.buildModifier(json, data, context = context)

            // Default to fillMaxSize if no width/height specified
            if (defaultFillMax) {
                modifier = modifier.fillMaxSize()
            }

            // Apply corner radius clip
            json.get("cornerRadius")?.asFloat?.let { radius ->
                modifier = modifier.clip(RoundedCornerShape(radius.dp))
            }

            // Track URL for updates via recomposition
            var currentUrl by remember(url) { mutableStateOf(url) }

            LaunchedEffect(url) {
                currentUrl = url
            }

            // Create WebView
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.apply {
                            this.javaScriptEnabled = javaScriptEnabled
                            userAgent?.let { this.userAgentString = it }

                            if (allowZoom) {
                                builtInZoomControls = true
                                displayZoomControls = false
                            }
                        }

                        webViewClient = WebViewClient()

                        if (javaScriptEnabled) {
                            webChromeClient = WebChromeClient()
                        }

                        if (currentUrl.isNotEmpty()) {
                            loadUrl(currentUrl)
                        }
                    }
                },
                update = { webView ->
                    // Reload URL if binding value changed
                    if (currentUrl.isNotEmpty() && webView.url != currentUrl) {
                        webView.loadUrl(currentUrl)
                    }
                },
                modifier = modifier
            )
        }
    }
}
