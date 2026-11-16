package com.kotlinjsonui.core

import android.view.View
import com.kotlinjsonui.R

/**
 * The per-WebView state behind the `Web` attributes `onLoadFailed` and
 * `reloadToken` (jsonui-cli shared/core/attribute_definitions.json), in both
 * rendering paths — the Composable `kjui build` emits and
 * `DynamicWebComponent`.
 *
 * 🔻 WHY IT LIVES ON THE VIEW. The `AndroidView` `update` block feeds it and
 * [KjuiWebViewClient] reads it, and the only thing those two share is the
 * WebView: `WebView.getWebViewClient()` is API 26 and this library's floor
 * is 24. So [of] keeps one instance in the view's keyed tag.
 *
 * `update` runs again whenever the data it reads changes, so [onLoadFailed]
 * is replaced on every pass and a failure always reaches the CURRENT
 * handler, not the one the view was created with.
 */
class KjuiWebLoadState {
    /** Called on a main-frame load failure (see [KjuiWebViewClient]). */
    var onLoadFailed: (() -> Unit)? = null

    private var seenToken = false
    private var lastToken: Any? = null

    /**
     * True when [token] differs from the value this view last saw. The first
     * call only records it: the value present when the view is created is
     * that load's own, not a request to load again.
     */
    fun reloadTokenChanged(token: Any?): Boolean {
        if (!seenToken) {
            seenToken = true
            lastToken = token
            return false
        }
        if (token == lastToken) return false
        lastToken = token
        return true
    }

    /** Hands a failure to the current handler, if one is bound. */
    fun reportLoadFailure() {
        onLoadFailed?.invoke()
    }

    companion object {
        /** The state kept on [view], made on first use. */
        @JvmStatic
        fun of(view: View): KjuiWebLoadState =
            existing(view) ?: KjuiWebLoadState().also {
                view.setTag(R.id.kjui_web_load_state, it)
            }

        /** The state kept on [view], or null when nothing declared one. */
        @JvmStatic
        fun existing(view: View?): KjuiWebLoadState? =
            view?.getTag(R.id.kjui_web_load_state) as? KjuiWebLoadState

        /**
         * Whether `onReceivedError` is a load failure. Only the main frame
         * counts — an image, stylesheet or subframe failing is not the page
         * failing — and a navigation that another one replaced ends in
         * `net::ERR_ABORTED`, which is a cancellation, not a failure (a
         * reloadToken retry issued mid-load is exactly that). Android has no
         * error code of its own for it (`ERROR_UNKNOWN`), so it is matched by
         * Chromium's name.
         */
        @JvmStatic
        fun isLoadFailure(isForMainFrame: Boolean, errorDescription: CharSequence?): Boolean =
            isForMainFrame && errorDescription?.toString() != ABORTED

        /** Whether `onReceivedHttpError` is a load failure: main frame, 4xx/5xx. */
        @JvmStatic
        fun isHttpLoadFailure(isForMainFrame: Boolean, statusCode: Int): Boolean =
            isForMainFrame && statusCode >= 400

        private const val ABORTED = "net::ERR_ABORTED"
    }
}
