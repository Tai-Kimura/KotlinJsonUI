package com.kotlinjsonui.core

import android.content.ContextWrapper
import android.view.View
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebView
import android.widget.FrameLayout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * A WebView renderer that crashes or is OOM-killed must not kill the app:
 * `WebViewClient.onRenderProcessGone` returns `false` by default, and on
 * API 26+ that makes Android kill the app's process.
 *
 * The android.jar here is the mockable stub (no Robolectric or mocking
 * library in this module), so the views are fakes that record
 * the calls the client makes. `Log` and the final `View.getParent()` throw
 * in that stub, so the client reads both through seams the tests replace.
 * The detail is passed as null: `RenderProcessGoneDetail`'s constructor is
 * deprecated (a warning, an error under CI's gate) and the client only logs it.
 */
class KjuiWebViewClientRenderProcessGoneTest {

    private val logged = mutableListOf<String>()
    private val parent = FakeParent()
    private lateinit var originalLog: (String) -> Unit
    private lateinit var originalParentOf: (WebView) -> ViewGroup?

    @Before
    fun installSeams() {
        originalLog = KjuiWebViewClient.log
        originalParentOf = KjuiWebViewClient.parentOf
        KjuiWebViewClient.log = { logged += it }
        KjuiWebViewClient.parentOf = { parent }
    }

    @After
    fun restoreSeams() {
        KjuiWebViewClient.log = originalLog
        KjuiWebViewClient.parentOf = originalParentOf
    }

    private class FakeWebView : WebView(ContextWrapper(null)) {
        var destroyed = 0
        override fun destroy() { destroyed++ }
    }

    private class FakeParent : FrameLayout(ContextWrapper(null)) {
        val removed = mutableListOf<View>()
        override fun removeView(view: View?) { view?.let { removed += it } }
    }

    @Test
    fun aGoneRendererIsHandledSoTheAppIsNotKilled() {
        val view = FakeWebView()
        assertTrue(KjuiWebViewClient().onRenderProcessGone(view, null))
    }

    @Test
    fun theDeadWebViewIsDestroyedOnce() {
        val view = FakeWebView()
        KjuiWebViewClient().onRenderProcessGone(view, null)
        assertEquals(1, view.destroyed)
    }

    @Test
    fun itIsLoggedOncePerRendererDeath() {
        KjuiWebViewClient().onRenderProcessGone(FakeWebView(), null)
        assertEquals(1, logged.size)
    }

    @Test
    fun aNullViewIsStillHandled() {
        assertTrue(KjuiWebViewClient().onRenderProcessGone(null, null))
    }

    @Test
    fun theDeadWebViewIsRemovedFromItsParent() {
        val view = FakeWebView()
        KjuiWebViewClient().onRenderProcessGone(view, null)
        assertEquals(listOf<View>(view), parent.removed)
    }

    @Test
    fun aWebViewWithNoParentIsStillDestroyed() {
        KjuiWebViewClient.parentOf = { null }
        val view = FakeWebView()
        assertTrue(KjuiWebViewClient().onRenderProcessGone(view, null))
        assertEquals(1, view.destroyed)
    }

    @Test
    fun aSubclassThatOverridesItStillWins() {
        var own = 0
        val client = object : KjuiWebViewClient() {
            override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
                own++
                return false
            }
        }
        val view = FakeWebView()
        assertFalse(client.onRenderProcessGone(view, null))
        assertEquals(1, own)
        assertEquals(0, view.destroyed)
        assertTrue(parent.removed.isEmpty())
    }

    @Test
    fun aSubclassThatDoesNotOverrideItGetsTheSafeBehavior() {
        val client = object : KjuiWebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) = super.onPageFinished(view, url)
        }
        val view = FakeWebView()
        assertTrue(client.onRenderProcessGone(view, null))
        assertEquals(1, view.destroyed)
        assertEquals(listOf<View>(view), parent.removed)
    }
}
