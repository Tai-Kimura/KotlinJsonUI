package com.kotlinjsonui.core

import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * The `WebViewClient` every KotlinJsonUI WebView gets, in both rendering
 * paths (`DynamicWebComponent` and the Composable `kjui build` emits).
 *
 * Navigation is handled exactly as `WebViewClient()` handles it. What it
 * adds:
 * - page loads are counted in [WebLoadSignal], which is inert unless a
 *   conformance host has turned it on;
 * - a main-frame load failure reaches the view's `onLoadFailed` handler
 *   through [KjuiWebLoadState] — a no-op for a view that declared none.
 *
 * ⚠️ SUBCLASS, DON'T REPLACE. A consumer that needs its own client should
 * extend this one and call `super` from the overrides below; assigning a
 * bare `WebViewClient()` instead silently removes the load signal and the
 * failure report, which is exactly the shape that left the codegen face
 * uncovered (see [WebLoadSignal]).
 */
open class KjuiWebViewClient : WebViewClient() {
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        WebLoadSignal.onPageStarted()
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        WebLoadSignal.onPageFinished()
    }

    /** The navigation itself failed (no connection, timeout, DNS, TLS). */
    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        super.onReceivedError(view, request, error)
        if (KjuiWebLoadState.isLoadFailure(request?.isForMainFrame == true, error?.description)) {
            KjuiWebLoadState.existing(view)?.reportLoadFailure()
        }
    }

    /**
     * The response arrived with a 4xx/5xx. The server's page is still shown,
     * as before; the handler decides whether to cover it.
     */
    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        super.onReceivedHttpError(view, request, errorResponse)
        if (KjuiWebLoadState.isHttpLoadFailure(request?.isForMainFrame == true, errorResponse?.statusCode ?: 0)) {
            KjuiWebLoadState.existing(view)?.reportLoadFailure()
        }
    }
}
