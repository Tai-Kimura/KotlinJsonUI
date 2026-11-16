package com.kotlinjsonui.image

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil3.SingletonImageLoader
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okio.Buffer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.util.Collections
import java.util.concurrent.TimeUnit

/**
 * What a renderer is told for each kind of response (jsonui-cli ticket
 * jui-networkimage-error-response-reported-as-progress): only a response that is the image
 * (2xx, or 304 for one the disk cache holds) is `responded` and counted as progress. A
 * redirect's own head is not the image's, and a 4xx / 5xx's body is an error page: the
 * renderer is told requesting, then failed, and never that bytes arrived. The VM's
 * onLoadStateChange is "loading", then "loaded" or "failed", as before.
 *
 * Each row: the events one frame was told (a run of Progress as `progress×n(last)`), then
 * the states onLoadStateChange called.
 */
@RunWith(AndroidJUnit4::class)
class KjuiNetworkImageStatusTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var server: MockWebServer
    private val etagSeen = Collections.synchronizedList(mutableListOf<String>())
    private val busy = java.util.concurrent.atomic.AtomicInteger()

    companion object {
        @BeforeClass @JvmStatic
        fun installLoader() {
            SingletonImageLoader.setSafe(KjuiImageLoaderFactory())
            KjuiLoadingRenderers.register("statusProbe") {
                object : KjuiLoadingRendererInstance {
                    override fun DrawScope.draw(frame: KjuiLoadingFrame) = KjuiNextFrame.Idle
                    override fun release() {}
                }
            }
        }

        private val image: ByteArray by lazy {
            val bmp = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888)
            val rnd = java.util.Random(7)
            for (x in 0 until 120) for (y in 0 until 120) bmp.setPixel(x, y, rnd.nextInt() or 0xFF000000.toInt())
            ByteArrayOutputStream().also { bmp.compress(Bitmap.CompressFormat.JPEG, 95, it) }.toByteArray()
        }
    }

    private fun body(text: String, size: Int = text.length) =
        Buffer().writeUtf8(text.repeat(maxOf(1, size / maxOf(1, text.length))).take(size))

    @Before
    fun setUp() {
        // The retries' waits shortened (KjuiNetworkImageRetryTest measures the real 2 s and 4 s).
        retryStepMillis = 50L
        server = MockWebServer()
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path.orEmpty().substringBefore('?')
                return when (path) {
                    "/ok.jpg", "/target.jpg" -> MockResponse().setBody(Buffer().write(image))
                    "/etag.jpg" -> {
                        val tag = request.getHeader("If-None-Match")
                        etagSeen.add(tag ?: "-")
                        if (tag == "\"v1\"") MockResponse().setResponseCode(304).setHeader("ETag", "\"v1\"")
                        else MockResponse().setBody(Buffer().write(image)).setHeader("ETag", "\"v1\"")
                            .setHeader("Cache-Control", "no-cache")
                    }
                    "/bare304.jpg" -> MockResponse().setResponseCode(304)
                    "/page.jpg" -> MockResponse().setHeader("Content-Type", "text/html")
                        .setBody(body("<html><body>not an image</body></html>", 20_000))
                    "/missing-png.jpg" -> MockResponse().setResponseCode(404).setBody(Buffer().write(image))
                    "/busy.jpg" -> if (busy.getAndIncrement() < 2) MockResponse().setResponseCode(503).setBody(body("busy"))
                        else MockResponse().setBody(Buffer().write(image))
                    "/moved.jpg" -> MockResponse().setResponseCode(302).setHeader("Location", "/target.jpg")
                        .setBody(body("moved"))
                    "/moved-missing.jpg" -> MockResponse().setResponseCode(302).setHeader("Location", "/missing.jpg")
                        .setBody(body("moved"))
                    "/missing.jpg" -> MockResponse().setResponseCode(404).setBody(body("Not Found"))
                    // A CDN's error page: 20 KB of HTML, sent slowly.
                    "/missing-big.jpg" -> MockResponse().setResponseCode(404)
                        .setBody(body("<html><body>gone</body></html>", 20_000))
                        .throttleBody(2_000, 20, TimeUnit.MILLISECONDS)
                    "/broken.jpg" -> MockResponse().setResponseCode(500).setBody(body("Internal Server Error"))
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
        retryStepMillis = 2_000L
    }

    private var shown by mutableStateOf<String?>(null)
    private var composed = false

    /** Shows [path] in a fresh frame and returns its row: the events, then the states. */
    private fun row(path: String, query: String = ""): String = rowAt(server.url(path).toString() + query)

    private fun rowAt(url: String): String {
        val events = Collections.synchronizedList(mutableListOf<String>())
        val states = Collections.synchronizedList(mutableListOf<String>())
        val sinks = events to states
        current = sinks
        if (!composed) {
            compose.setContent {
                shown?.let { u ->
                    val (ev, st) = current!!
                    key(u) {
                        KjuiNetworkImage(
                            model = u, contentDescription = "probe", modifier = Modifier.size(60.dp),
                            loadingRenderer = "statusProbe",
                            onLoadStateChange = { st.add(it) },
                            onLoadEvent = { ev.add(describe(it)) },
                        )
                    }
                }
            }
            composed = true
        }
        compose.runOnIdle { shown = null }
        compose.waitForIdle()
        compose.runOnIdle { shown = url }
        compose.waitUntil(20_000) { states.lastOrNull().let { it == "loaded" || it == "failed" } }
        compose.waitForIdle()
        Thread.sleep(150)
        compose.waitForIdle()
        val most = events.toList().filter { it.startsWith("progress(") }
            .maxOfOrNull { it.removePrefix("progress(").substringBefore('/').toLong() } ?: 0L
        val mostText = if (most == image.size.toLong()) "IMG" else most.toString()
        return "${collapse(events.toList()).joinToString(" ")} | ${states.toList().joinToString(" ")} | $mostText"
    }

    private var current: Pair<MutableList<String>, MutableList<String>>? = null

    private fun describe(e: KjuiImageLoadEvent): String = when (e) {
        KjuiImageLoadEvent.Queued -> "queued"
        is KjuiImageLoadEvent.Requesting -> "requesting(${e.attempt})"
        is KjuiImageLoadEvent.Responded -> "responded(${e.expected})"
        is KjuiImageLoadEvent.Progress -> "progress(${e.received}/${e.expected})"
        is KjuiImageLoadEvent.Finished -> "finished(${e.source})"
        // A cut connection fails as SocketException or a plain IOException, by when the cut lands.
        is KjuiImageLoadEvent.Failed -> "failed(${if (e.error is java.net.SocketException) "IOException" else e.error?.javaClass?.simpleName})"
        is KjuiImageLoadEvent.Retrying -> "retrying(${e.attempt})"
        KjuiImageLoadEvent.Cancelled -> "cancelled"
    }

    /** A run of progress as `progress×n(last)`; the image's own byte count as `IMG`. */
    private fun collapse(events: List<String>): List<String> {
        val out = mutableListOf<String>()
        var run = 0
        var last = ""
        fun flush() { if (run > 0) out.add("progress×$run($last)"); run = 0 }
        for (e in events) {
            if (e.startsWith("progress(")) { run++; last = e.removePrefix("progress(").removeSuffix(")") } else { flush(); out.add(e) }
        }
        flush()
        return out.map { it.replace(image.size.toString(), "IMG") }
    }

    private fun dropMemoryCache() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        SingletonImageLoader.get(ctx).memoryCache?.clear()
    }

    @Test
    fun onlyTheImagesOwnResponseIsRespondedAndCounted() {
        val rows = linkedMapOf<String, String>()
        rows["200"] = row("/ok.jpg")
        rows["200 (ETag, no-cache)"] = row("/etag.jpg")
        dropMemoryCache()
        rows["again from disk (ETag)"] = row("/etag.jpg", "")
        rows["304 unasked"] = row("/bare304.jpg")
        rows["302 -> 200"] = row("/moved.jpg")
        rows["302 -> 404"] = row("/moved-missing.jpg")
        rows["404 (9 B)"] = row("/missing.jpg")
        rows["404 (20 KB HTML, slow)"] = row("/missing-big.jpg")
        rows["500"] = row("/broken.jpg")
        rows["200 HTML (not an image)"] = row("/page.jpg")
        rows["404 with a PNG body"] = row("/missing-png.jpg")
        rows["503, 503, 200"] = row("/busy.jpg")
        // A connection cut before any answer: its own server, the cut queued (a Dispatcher's
        // DISCONNECT_AT_START is read from peek(), which answers an empty 200 by default).
        val cut = MockWebServer().apply {
            repeat(6) { enqueue(MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AT_START)) }
            start()
        }
        try { rows["connection cut"] = rowAt(cut.url("/cut.jpg").toString()) } finally { cut.shutdown() }
        val table = rows.entries.joinToString("\n") { (k, v) -> "$k :: $v" }
        Log.i("KjuiStatusTable", "\n$table\nIf-None-Match seen: $etagSeen\nrequests: ${server.requestCount}")

        // How many Progress a frame is told depends on the reads' timing: compared without the count.
        val image = "requesting(0) responded(IMG) progress(IMG/IMG) finished(NETWORK) | loading loaded | IMG"
        val failed = "requesting(0) failed(HttpException) | loading failed | 0"
        val retried = "requesting(0) retrying(1) requesting(1) retrying(2) requesting(2) failed(HttpException) | loading failed | 0"
        val expected = linkedMapOf(
            "200" to image,
            "200 (ETag, no-cache)" to image,
            // coil's CacheStrategy.DEFAULT reads its disk cache without asking the server.
            "again from disk (ETag)" to "finished(DISK) | loading loaded | 0",
            // coil asks again without its cache, then fails: two calls, neither the image.
            // It fails at once (the requester's ruling 2026-11-15); coil asked again without its cache.
            "304 unasked" to "requesting(0) failed(IOException) | loading failed | 0",
            "302 -> 200" to image,
            "302 -> 404" to failed,
            "404 (9 B)" to failed,
            "404 (20 KB HTML, slow)" to failed,
            // A passing failure is read twice more (ticket jui-networkimage-retry-rules-differ-by-os).
            "500" to retried,
            // A 2xx is the image's response: its bytes are counted; the decode fails.
            "200 HTML (not an image)" to "requesting(0) responded(19988) progress(19988/19988) failed(DecodeException) | loading failed | 19988",
            // coil fails a 4xx whatever its body is: no image is drawn from an error response.
            "404 with a PNG body" to failed,
            // coil does not retry: the first 503 fails the load.
            "503, 503, 200" to "requesting(0) retrying(1) requesting(1) retrying(2) requesting(2) responded(IMG) progress(IMG/IMG) finished(NETWORK) | loading loaded | IMG",
            // A cut connection: no response at all, so nothing is responded.
            "connection cut" to "requesting(0) retrying(1) requesting(1) retrying(2) requesting(2) failed(IOException) | loading failed | 0",
        )
        val drawn = rows.mapValues { (_, v) -> v.replace(Regex("progress×\\d+\\("), "progress(") }
        assertEquals("the table\n$table", expected.entries.joinToString("\n") { "${it.key} :: ${it.value}" },
            drawn.entries.joinToString("\n") { "${it.key} :: ${it.value}" })
    }
}
