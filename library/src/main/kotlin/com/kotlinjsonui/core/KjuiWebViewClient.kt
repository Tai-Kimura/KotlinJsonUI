package com.kotlinjsonui.core

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * The `WebViewClient` every KotlinJsonUI WebView gets, in both rendering
 * paths (`DynamicWebComponent` and the Composable `kjui build` emits).
 *
 * Behaviourally identical to `WebViewClient()` — it adds nothing to
 * navigation handling. What it adds is that page loads are counted in
 * [WebLoadSignal], which is inert unless a conformance host has turned it on.
 *
 * ⚠️ SUBCLASS, DON'T REPLACE. A consumer that needs its own client should
 * extend this one and call `super` from the two overrides below; assigning a
 * bare `WebViewClient()` instead silently removes the load signal, which is
 * exactly the shape that left the codegen face uncovered (see [WebLoadSignal]).
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
}
