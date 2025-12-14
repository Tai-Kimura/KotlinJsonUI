package com.kotlinjsonui.dynamic.components

import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.dashedBorder
import com.kotlinjsonui.dynamic.helpers.dottedBorder

/**
 * Dynamic Web Component Converter
 * Converts JSON to WebView composable at runtime
 * Simplified version of WebView component
 * 
 * Supported JSON attributes (matching Ruby implementation):
 * - url: String URL or @{variable} for web page
 * - javaScriptEnabled: Boolean to enable JavaScript (default: true)
 * - userAgent: String custom user agent
 * - allowZoom: Boolean to enable zoom controls
 * - borderWidth: Number for border width
 * - borderColor: String hex color for border
 * - width/height: Number dimensions (defaults to fillMaxSize)
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 */
class DynamicWebComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Parse URL with data binding support
            val urlString = json.get("url")?.asString ?: ""
            val url = when {
                urlString.contains("@{") -> {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    val variable = pattern.find(urlString)?.groupValues?.get(1)
                    if (variable != null) {
                        data[variable]?.toString() ?: ""
                    } else {
                        urlString
                    }
                }
                else -> urlString
            }
            
            // Parse WebView settings
            val javaScriptEnabled = json.get("javaScriptEnabled")?.asBoolean ?: true
            val userAgent = json.get("userAgent")?.asString
            val allowZoom = json.get("allowZoom")?.asBoolean ?: false
            
            // Build modifier with default fillMaxSize if no size specified
            var modifier = if (!json.has("width") && !json.has("height")) {
                ModifierBuilder.buildModifier(json).fillMaxSize()
            } else {
                ModifierBuilder.buildModifier(json)
            }
            
            // Apply border if specified (supports solid/dashed/dotted)
            if (json.has("borderWidth") && json.has("borderColor")) {
                val borderWidth = json.get("borderWidth").asFloat.dp
                val borderColor = ColorParser.parseColor(json, "borderColor") ?: Color.Gray
                val borderStyle = json.get("borderStyle")?.asString ?: "solid"
                modifier = when (borderStyle) {
                    "dashed" -> modifier.dashedBorder(borderWidth, borderColor, null)
                    "dotted" -> modifier.dottedBorder(borderWidth, borderColor, null)
                    else -> modifier.border(borderWidth, borderColor)
                }
            }
            
            // Track URL for updates
            var currentUrl by remember(url) { mutableStateOf(url) }
            
            // Update URL when data changes
            LaunchedEffect(url) {
                currentUrl = url
            }
            
            // Create WebView
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
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
                    // Update URL if it changed
                    if (currentUrl.isNotEmpty() && webView.url != currentUrl) {
                        webView.loadUrl(currentUrl)
                    }
                },
                modifier = modifier
            )
        }
    }
}