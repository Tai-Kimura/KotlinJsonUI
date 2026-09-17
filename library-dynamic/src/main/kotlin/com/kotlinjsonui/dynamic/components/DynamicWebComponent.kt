package com.kotlinjsonui.dynamic.components

import com.kotlinjsonui.core.KjuiWebViewClient

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.graphics.toArgb
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.WebAttributes
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import com.kotlinjsonui.dynamic.rememberTypedAttrs

/**
 * Dynamic Web Component Converter
 * Converts JSON to WebView composable at runtime.
 * Merges web_component.rb + webview_component.rb into a single component.
 *
 * Attribute access goes through the generated [WebAttributes] extraction
 * (typed, alias-aware, L1-marker-aware) via the [TypedAttrs] bridge; the
 * node itself is only passed wholesale to the shared ModifierBuilder
 * pipeline. `DynamicWebViewComponent` delegates here and only changes the
 * [UnappliedAttributes] label to "WebView".
 *
 * Supported JSON attributes:
 * - url: String URL or @{binding} for web page
 * - background: String color for WebView background (matches SwiftJsonUI)
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
            data: Map<String, Any> = emptyMap(),
            componentType: String = "Web"
        ) {
            // Apply lifecycle effects first
            ModifierBuilder.ApplyLifecycleEffects(json, data)

            val context = LocalContext.current
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                WebAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                componentType, json,
                declared = WebAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // Parse URL with binding + resource support
            val url = TypedAttrs.rawString(a.url)
                ?.let { ResourceResolver.resolveTextValue(it, data, context) }
                ?: ""

            // Inline HTML content — only in force when no url is declared
            // (codegen: `if json_data['url'].nil? && json_data['html']`).
            // A null base URL keeps the document in an opaque origin.
            val html = if (TypedAttrs.rawString(a.url) == null) {
                a.html?.let { ResourceResolver.resolveTextValue(it, data, context) }
            } else null

            // Parse WebView settings ('javaScriptEnabled', 'userAgent' and
            // 'allowZoom' are undeclared legacy runtime extras — kept on the
            // binding-aware json readers)
            val javaScriptEnabled = ResourceResolver.resolveBoolean(json, "javaScriptEnabled", data, true)
            val userAgent = ResourceResolver.resolveString(json, "userAgent", data)
            val allowZoom = ResourceResolver.resolveBoolean(json, "allowZoom", data, false)

            // Background color (applied to native WebView; supports @{binding})
            val bgColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.common.background), data, context
            )

            // Build modifier: testTag → margins → size → alpha → clickable → padding
            // (presence check, not a value read — width/height are applied
            // wholesale by ModifierBuilder; absence of both enables fillMaxSize)
            val defaultFillMax = TypedAttrs.rawKey(json, "width") == null && TypedAttrs.rawKey(json, "height") == null
            var modifier = ModifierBuilder.buildModifier(json, data, context = context)

            // Default to fillMaxSize if no width/height specified
            if (defaultFillMax) {
                modifier = modifier.fillMaxSize()
            }

            // Apply corner radius clip
            TypedAttrs.float(a.common.cornerRadius, data)?.let { radius ->
                modifier = modifier.clip(RoundedCornerShape(radius.dp))
            }

            // Track URL for updates via recomposition
            var currentUrl by remember(url) { mutableStateOf(url) }

            LaunchedEffect(url) {
                currentUrl = url
            }

            // 🔻 THE ID LIVES ON A COMPOSE NODE, NOT ON THE AndroidView. A
            // testTag on the AndroidView's own modifier is never projected as
            // a UiAutomator resource-id: the holder exposes the real
            // android.webkit.WebView, whose resource-id is empty, and the
            // Compose semantics beside it are not what the accessibility
            // tree shows (bar face, 2026-09-17: `class="android.webkit.WebView"
            // resource-id=""` while the screen marker on a Compose node was
            // found). So an id wraps the view in a Box that carries the whole
            // modifier chain — tag, margins, size, alpha, clip — and the
            // AndroidView fills the Box. Measured on conf_ci: the Box node
            // carries `resource-id="<id>"` and the WebView is its child.
            // Same shape as the kjui codegen (web_component.rb /
            // webview_component.rb) so the parity host sees one picture.
            // Without an id nothing is wrapped and the tree is what it was.
            val hasId = a.common.id != null
            val viewModifier = if (hasId) Modifier.fillMaxSize() else modifier
            Box(modifier = if (hasId) modifier else Modifier) {
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

                        // 🔻 A BARE `WebViewClient()` EMITS NO COMPLETION SIGNAL.
                        // The class HAS `onPageFinished`; assigning the default
                        // instance means nothing overrides it, so a host has
                        // nothing to wait on and captures whatever the page
                        // happened to paint. See `WebLoadSignal` for why
                        // a green `control_diff` does not settle that question.
                        //
                        // ⚠️ The counters are inert until a conformance host
                        // sets `WebLoadSignal.enabled`; a consumer's own
                        // instrumentation sees no change.
                        webViewClient = KjuiWebViewClient()

                        if (javaScriptEnabled) {
                            webChromeClient = WebChromeClient()
                        }

                        // Apply background color to native WebView
                        bgColor?.let { setBackgroundColor(it.toArgb()) }

                        if (currentUrl.isNotEmpty()) {
                            loadUrl(currentUrl)
                        } else if (html != null) {
                            loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
                        }
                    }
                },
                update = { webView ->
                    // Reload URL if binding value changed
                    if (currentUrl.isNotEmpty() && webView.url != currentUrl) {
                        webView.loadUrl(currentUrl)
                    }
                },
                modifier = viewModifier
            )
            }
        }

        /** Web-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "url", "html"
        )
    }
}
