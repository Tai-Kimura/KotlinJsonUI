package com.kotlinjsonui.dynamic

import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.viewinterop.AndroidView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.JsonParser
import com.kotlinjsonui.core.KjuiWebLoadState
import com.kotlinjsonui.core.KjuiWebViewClient
import com.kotlinjsonui.dynamic.components.DynamicWebComponent
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

/**
 * The `Web` attributes `onLoadFailed` and `reloadToken` on a real
 * android.webkit.WebView (ssot-web-component-has-no-load-failure-event-or-reload-trigger).
 *
 * What "main frame", "4xx" and "cancelled" mean is Chromium's to say, so these
 * are device arms: a name that can never resolve (RFC 2606 `.invalid`), 404 /
 * 500 from a MockWebServer to the main frame and to an iframe, a page that
 * loads, a load another one replaces, and a token that moves.
 *
 * Everything but the replacement arm goes through [DynamicWebComponent], so
 * the handler resolution, the view state and the library client are measured
 * together, the way a screen uses them.
 */
@RunWith(AndroidJUnit4::class)
class WebLoadFailureDeviceTest {

    @get:Rule
    val rule = createComposeRule()

    private lateinit var server: MockWebServer
    private val requested = CopyOnWriteArrayList<String>()

    @Before
    fun startServer() {
        server = MockWebServer()
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path ?: "/"
                requested += path
                return when (path) {
                    "/ok" -> page(200, "<p id=\"t\">ok</p><iframe src=\"/missing\"></iframe>")
                    "/boom" -> page(500, "<p id=\"t\">boom</p>")
                    else -> page(404, "<p id=\"t\">not found</p>")
                }
            }
        }
        server.start()
    }

    @After
    fun stopServer() {
        server.shutdown()
    }

    private fun page(status: Int, body: String) = MockResponse()
        .setResponseCode(status)
        .setHeader("Content-Type", "text/html")
        .setBody("<html><body>$body</body></html>")

    private fun url(path: String) = server.url(path).toString()

    private fun settle(millis: Long = 2_000) {
        rule.waitForIdle()
        Thread.sleep(millis)
        rule.waitForIdle()
    }

    /** Renders a dynamic Web; `failed` counts onLoadFailed. */
    private fun renderWeb(json: String, data: () -> Map<String, Any>) {
        rule.setContent {
            DynamicWebComponent.create(JsonParser.parseString(json).asJsonObject, data())
        }
    }

    private fun webJson(url: String, extra: String = "") =
        """{"type":"Web","width":200,"height":200,"url":"$url","onLoadFailed":"@{failed}"$extra}"""

    @Test
    fun anUnresolvableMainFrameHostReportsOnce() {
        val failed = AtomicInteger()
        val handler: () -> Unit = { failed.incrementAndGet() }
        renderWeb(webJson("https://conformance.invalid/")) { mapOf("failed" to handler) }
        rule.waitUntil(20_000) { failed.get() > 0 }
        settle()
        assertEquals(1, failed.get())
    }

    @Test
    fun aMainFrame404ReportsOnce() {
        val failed = AtomicInteger()
        val handler: () -> Unit = { failed.incrementAndGet() }
        renderWeb(webJson(url("/missing"))) { mapOf("failed" to handler) }
        rule.waitUntil(20_000) { "/missing" in requested }
        settle()
        assertEquals(1, failed.get())
    }

    @Test
    fun aMainFrame500ReportsOnce() {
        val failed = AtomicInteger()
        val handler: () -> Unit = { failed.incrementAndGet() }
        renderWeb(webJson(url("/boom"))) { mapOf("failed" to handler) }
        rule.waitUntil(20_000) { "/boom" in requested }
        settle()
        assertEquals(1, failed.get())
    }

    /**
     * The negative the arms above need — same server, same wait — and a page
     * that loads with a 404 in a subframe. The iframe's request is awaited, so
     * "nothing reported" is said after the 404 was served.
     */
    @Test
    fun aPageThatLoadsWithA404IframeReportsNothing() {
        val failed = AtomicInteger()
        val handler: () -> Unit = { failed.incrementAndGet() }
        renderWeb(webJson(url("/ok"))) { mapOf("failed" to handler) }
        rule.waitUntil(20_000) { "/ok" in requested && "/missing" in requested }
        settle()
        assertEquals(0, failed.get())
    }

    /**
     * reloadToken: the value present at creation loads nothing extra; each
     * change loads the url again — once; an unrelated recomposition does not.
     */
    @Test
    fun aMovedTokenReloadsTheUrlOnce() {
        var token by mutableStateOf(0)
        var unrelated by mutableStateOf("a")
        val handler: () -> Unit = {}
        renderWeb(webJson(url("/ok"), ""","reloadToken":"@{token}"""")) {
            mapOf("failed" to handler, "token" to token, "other" to unrelated)
        }
        rule.waitUntil(20_000) { requested.count { it == "/ok" } >= 1 }
        settle()
        assertEquals("created: one load", 1, requested.count { it == "/ok" })

        unrelated = "b"
        settle()
        assertEquals("an unrelated recomposition does not reload", 1, requested.count { it == "/ok" })

        token = 1
        rule.waitUntil(20_000) { requested.count { it == "/ok" } >= 2 }
        settle()
        assertEquals("a moved token reloads once", 2, requested.count { it == "/ok" })
    }

    /**
     * A load another one replaced mid-flight. Chromium's answer, whatever it
     * is, is recorded in [errors] and printed, so the arm says whether an
     * `net::ERR_ABORTED` actually reached the client — without it the filter
     * would not be what kept the count at zero.
     */
    @Test
    fun aLoadReplacedMidFlightReportsNothing() {
        val failed = AtomicInteger()
        val errors = CopyOnWriteArrayList<String>()
        lateinit var view: WebView
        rule.setContent {
            AndroidView(factory = { context ->
                WebView(context).also { web ->
                    view = web
                    web.webViewClient = object : KjuiWebViewClient() {
                        override fun onReceivedError(v: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                            errors += "${request?.isForMainFrame}:${error?.errorCode}:${error?.description}"
                            super.onReceivedError(v, request, error)
                        }
                    }
                    KjuiWebLoadState.of(web).onLoadFailed = { failed.incrementAndGet() }
                    // TEST-NET-1 (RFC 5737): never answers, so it is in flight
                    // when the second load replaces it.
                    web.loadUrl("https://192.0.2.1/")
                }
            })
        }
        settle(1_000)
        InstrumentationRegistry.getInstrumentation().runOnMainSync { view.loadUrl(url("/ok")) }
        rule.waitUntil(20_000) { "/ok" in requested && "/missing" in requested }
        settle()
        android.util.Log.i("WebLoadFailureDeviceTest", "errors seen: $errors")
        assertEquals("errors seen: $errors", 0, failed.get())
        assertTrue(errors.none { it.startsWith("true:") && !it.endsWith("net::ERR_ABORTED") })
    }
}
