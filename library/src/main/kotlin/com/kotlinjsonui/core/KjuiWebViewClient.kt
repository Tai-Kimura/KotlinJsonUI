package com.kotlinjsonui.core

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting

/**
 * The `WebViewClient` every KotlinJsonUI WebView gets, in both rendering
 * paths (`DynamicWebComponent` and the Composable `kjui build` emits).
 *
 * Navigation is handled exactly as `WebViewClient()` handles it. What it
 * adds:
 * - page loads are counted in [WebLoadSignal], which is inert unless a
 *   conformance host has turned it on;
 * - a main-frame load failure reaches the view's `onLoadFailed` handler
 *   through [KjuiWebLoadState] — a no-op for a view that declared none;
 * - a dead renderer process no longer takes the app down with it (see
 *   [onRenderProcessGone]).
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

    /**
     * The WebView's renderer process crashed or was killed (low memory).
     *
     * 🔻 WHY THIS EXISTS. `WebViewClient`'s default returns `false`, and on
     * API 26+ Android answers `false` by killing the APP's process: one
     * renderer OOM-kill took the whole app down, on both rendering paths,
     * since every KotlinJsonUI WebView gets this client. Returning `true`
     * keeps the app alive.
     *
     * ⚠️ THE VIEW IS DEAD AFTERWARDS. A WebView whose renderer is gone cannot
     * be reused, so it is taken out of its parent and destroyed; the area it
     * occupied stays blank until the screen is opened again. Re-creating the
     * page in place is deliberately NOT done here (scope ruling 2026-10-17).
     *
     * Neither `onLoadFailed` nor [WebLoadSignal] is told: this is not a load
     * failure — a handler that reloads would be calling into a destroyed
     * view — and a conformance run does not kill renderers.
     *
     * A subclass that overrides this wins as usual; one that does not gets
     * this. Never called below API 26 (the callback does not exist there).
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
        log("WebView renderer gone (crashed=${detail?.didCrash()}); releasing the dead WebView")
        if (view != null) releaseDeadWebView(view, parentOf(view))
        return true
    }

    companion object {
        private const val TAG = "KjuiWebViewClient"

        // Seams for the JVM tests, where `Log` and the final `View.getParent`
        // are android.jar stubs that throw.
        @VisibleForTesting
        internal var log: (String) -> Unit = { Log.d(TAG, it) }

        @VisibleForTesting
        internal var parentOf: (WebView) -> ViewGroup? = { it.parent as? ViewGroup }

        /** Detaches [view] from [parent] (if any) and destroys it. */
        private fun releaseDeadWebView(view: WebView, parent: ViewGroup?) {
            parent?.removeView(view)
            view.destroy()
        }
    }
}
