package com.kotlinjsonui.dynamic.components

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * When a dynamic Web/WebView reloads on recomposition.
 *
 * `update` runs on every recomposition. It used to reload whenever
 * `webView.url != currentUrl` — and `webView.url` is where the page ENDED UP,
 * so a redirected page never matched and was reloaded every time anything
 * around it recomposed (scroll position, form input and page state lost).
 * The comparand is now the url the view last LOADED, kept on its `tag`, as the
 * kjui codegen has done for WebView since jsonui-cli 1.8.103 and for Web since
 * kjui-web-codegen-reloads-bound-url-on-every-recomposition.
 */
class WebReloadDecisionTest {

    @Test
    fun anUnchangedUrlDoesNotReloadEvenAfterARedirect() {
        // The case the old comparison got wrong: the view's own url is the
        // redirect target, the tag is what was asked for.
        val asked = "https://example.com/start"
        @Suppress("UNUSED_VARIABLE")
        val viewUrlAfterRedirect = "https://example.com/landing"
        assertFalse(DynamicWebComponent.shouldReload(lastLoaded = asked, url = asked))
    }

    @Test
    fun aMovedBindingReloads() {
        assertTrue(DynamicWebComponent.shouldReload(lastLoaded = "https://a.test", url = "https://b.test"))
    }

    @Test
    fun aBindingThatArrivesAfterTheFirstCompositionLoads() {
        // Opened with "" (nothing loaded, tag never set), then filled.
        assertTrue(DynamicWebComponent.shouldReload(lastLoaded = null, url = "https://a.test"))
    }

    @Test
    fun anEmptyUrlNeverLoads() {
        // The html branch, or a binding that has not arrived.
        assertFalse(DynamicWebComponent.shouldReload(lastLoaded = null, url = ""))
        assertFalse(DynamicWebComponent.shouldReload(lastLoaded = "https://a.test", url = ""))
    }
}
