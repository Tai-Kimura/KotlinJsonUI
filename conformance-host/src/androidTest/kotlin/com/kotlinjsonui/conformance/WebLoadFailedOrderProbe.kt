package com.kotlinjsonui.conformance

import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.kotlinjsonui.components.VisibilityWrapper
import com.kotlinjsonui.core.KjuiWebLoadState
import com.kotlinjsonui.core.KjuiWebViewClient
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * kjui's emission for a WebView node and for the same node spelled Web
 * differ in one place: the Web form attaches its WebViewClient after
 * `loadUrl`. Does onLoadFailed still fire for a failing first load? NOT part
 * of the suite; skipped unless requested (`-e webOrderProbe 1`).
 *
 * The node is an app's WebView node, emitted by kjui_tools at jsonui-cli
 * 24f7fad0 both ways and pasted unchanged but for two things: its named
 * background colour (the app's resource manager resolves it) is given as a hex
 * value, and its id is renamed `probe_web`. The URL refuses the connection
 * (127.0.0.1:9).
 */
@OptIn(ExperimentalComposeUiApi::class)
@RunWith(AndroidJUnit4::class)
class WebLoadFailedOrderProbe {
    @Before
    fun skipUnlessRequested() {
        Assume.assumeTrue("set -e webOrderProbe 1",
            InstrumentationRegistry.getArguments().getString("webOrderProbe") == "1")
    }

    private class Data(val failures: AtomicInteger) {
        val webViewUrl: String = "http://127.0.0.1:9/"
        val webViewVisibility: String = "visible"
        val reloadToken: Int = 0
        val onLoadFailed: (() -> Unit)? = { failures.incrementAndGet() }
    }

    @Composable
    private fun AsWebView(data: Data) {
        VisibilityWrapper(
            visibility = data.webViewVisibility,
        ) {
        val webViewBgColor = Color(android.graphics.Color.parseColor("#333333")).toArgb()
        Box(
            modifier = Modifier
                .testTag("probe_web")
                .semantics { testTagsAsResourceId = true }
                .fillMaxWidth()
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        webViewClient = KjuiWebViewClient()
                        webChromeClient = WebChromeClient()
                        setBackgroundColor(webViewBgColor)
                        tag = data.webViewUrl
                        loadUrl(data.webViewUrl)
                    }
                },
                update = { webView ->
                    // Requires KotlinJsonUI >= 2.41.0 (Web onLoadFailed / reloadToken)
                    val loadState = KjuiWebLoadState.of(webView)
                    loadState.onLoadFailed = { data.onLoadFailed?.invoke() }
                    if (loadState.reloadTokenChanged(data.reloadToken)) {
                        webView.tag = null
                    }
                    val url = data.webViewUrl
                    if (webView.tag != url) {
                        webView.tag = url
                        webView.loadUrl(url)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        }
    }

    @Composable
    private fun AsWeb(data: Data) {
        VisibilityWrapper(
            visibility = data.webViewVisibility,
        ) {
        val webViewBgColor = Color(android.graphics.Color.parseColor("#333333")).toArgb()
        Box(
            modifier = Modifier
                .testTag("probe_web")
                .semantics { testTagsAsResourceId = true }
                .fillMaxWidth()
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        setBackgroundColor(webViewBgColor)
                        tag = data.webViewUrl
                        loadUrl(data.webViewUrl)
                        webViewClient = KjuiWebViewClient()
                        webChromeClient = WebChromeClient()
                    }
                },
                update = { webView ->
                    // Requires KotlinJsonUI >= 2.41.0 (Web onLoadFailed / reloadToken)
                    val loadState = KjuiWebLoadState.of(webView)
                    loadState.onLoadFailed = { data.onLoadFailed?.invoke() }
                    if (loadState.reloadTokenChanged(data.reloadToken)) {
                        webView.tag = null
                    }
                    val url = data.webViewUrl
                    if (webView.tag != url) {
                        webView.tag = url
                        webView.loadUrl(url)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        }
    }

    @Test
    fun onLoadFailedFiresForBothOrders() {
        val wv = AtomicInteger(); val web = AtomicInteger()
        val scenario = ActivityScenario.launch(FixtureHostActivity::class.java)
        scenario.onActivity { activity ->
            activity.setContent {
                Column(Modifier.semantics { testTagsAsResourceId = true }) {
                    Box(Modifier.height(200.dp)) { AsWebView(Data(wv)) }
                    Box(Modifier.height(200.dp)) { AsWeb(Data(web)) }
                }
            }
        }
        val deadline = System.currentTimeMillis() + 8_000
        while (System.currentTimeMillis() < deadline && (wv.get() == 0 || web.get() == 0)) Thread.sleep(200)
        Thread.sleep(1000)
        println("WEBORDER webview_onLoadFailed=${wv.get()} web_onLoadFailed=${web.get()}")
        scenario.close()
    }
}
