package com.kotlinjsonui.image

import android.graphics.Bitmap
import android.os.SystemClock
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
import coil3.SingletonImageLoader
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okhttp3.mockwebserver.SocketPolicy
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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Which failures a NetworkImage reads again (jsonui-cli ticket
 * jui-networkimage-retry-rules-differ-by-os, the requester's ruling 2026-11-15): a 4xx but 408
 * and 429 fails at once; a passing failure (5xx, a cut connection, 408, 429) is read again after
 * 2 s and after 4 s, the renderer told retrying(n) before each wait; onLoadStateChange stays
 * "loading" through the retries and ends "failed" or "loaded".
 *
 * Each row: the attempts (requesting events), the requests the server saw, the waits
 * (retrying → the next requesting), the events a frame was told (a run of progress as one),
 * the states.
 */
@RunWith(AndroidJUnit4::class)
class KjuiNetworkImageRetryTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var server: MockWebServer
    private val hits = ConcurrentHashMap<String, AtomicInteger>()

    companion object {
        @BeforeClass @JvmStatic
        fun installLoader() {
            runCatching { SingletonImageLoader.setSafe(KjuiImageLoaderFactory()) }
            KjuiLoadingRenderers.register("retryProbe") {
                object : KjuiLoadingRendererInstance {
                    override fun DrawScope.draw(frame: KjuiLoadingFrame) = KjuiNextFrame.Idle
                    override fun release() {}
                }
            }
        }

        private val image: ByteArray by lazy {
            val bmp = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888)
            bmp.eraseColor(android.graphics.Color.GREEN)
            ByteArrayOutputStream().also { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }.toByteArray()
        }
    }

    @Before
    fun setUp() {
        server = MockWebServer()
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path.orEmpty().substringBefore('?')
                val n = hits.computeIfAbsent(path) { AtomicInteger() }.incrementAndGet()
                fun code(c: Int) = MockResponse().setResponseCode(c).setBody("status $c")
                return when (path) {
                    "/200.png" -> MockResponse().setBody(Buffer().write(image))
                    "/400.png" -> code(400)
                    "/404.png", "/gone.png" -> code(404)
                    "/301-404.png" -> MockResponse().setResponseCode(301).setHeader("Location", "/gone.png")
                    "/408.png" -> code(408)
                    "/429.png" -> code(429)
                    "/500.png" -> code(500)
                    "/503.png" -> code(503)
                    "/503-503-200.png" -> if (n <= 2) code(503) else MockResponse().setBody(Buffer().write(image))
                    "/304.png" -> MockResponse().setResponseCode(304)
                    else -> code(404)
                }
            }
        }
        server.start()
    }

    @After
    fun tearDown() = server.shutdown()

    private var shown by mutableStateOf<String?>(null)
    private var sinks: Pair<MutableList<Pair<Long, String>>, MutableList<String>>? = null
    private var composed = false

    private fun describe(e: KjuiImageLoadEvent): String {
        val s = e.toString()
        val name = s.substringBefore('(').replaceFirstChar { it.lowercase() }
        // A cut connection fails as SocketException or a plain IOException, by when the cut lands.
        if (e is KjuiImageLoadEvent.Failed) return "failed(${if (e.error is java.net.SocketException) "IOException" else e.error?.javaClass?.simpleName})"
        return if (s.contains("attempt=") && !s.startsWith("Requesting")) "$name(${s.substringAfter("attempt=").substringBefore(')')})" else name
    }

    private fun row(path: String): String = rowAt(server.url(path).toString(), path) { hits.filterKeys { it == path || (path == "/301-404.png" && it == "/gone.png") }.values.sumOf { it.get() } }

    private fun rowAt(url: String, path: String, seenBy: () -> Int): String {
        val events = Collections.synchronizedList(mutableListOf<Pair<Long, String>>())
        val states = Collections.synchronizedList(mutableListOf<String>())
        sinks = events to states
        if (!composed) {
            compose.setContent {
                shown?.let { u ->
                    val (ev, st) = sinks!!
                    key(u) {
                        KjuiNetworkImage(
                            model = u, contentDescription = "probe", modifier = Modifier.size(40.dp), loadingRenderer = "retryProbe",
                            onLoadStateChange = { st.add(it) },
                            onLoadEvent = { ev.add(SystemClock.uptimeMillis() to describe(it)) },
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
        Thread.sleep(300)
        compose.waitForIdle()
        val list = events.toList()
        val names = mutableListOf<String>()
        for ((_, n) in list) if (!(n == "progress" && names.lastOrNull() == "progress")) names += n
        val waits = list.indices.filter { list[it].second.startsWith("retrying") }.mapNotNull { i ->
            list.drop(i + 1).firstOrNull { it.second == "requesting" }?.let { "%ds".format(kotlin.math.round((it.first - list[i].first) / 1000.0).toInt()) }
        }
        val attempts = list.count { it.second == "requesting" }
        val seen = seenBy()
        return "attempts $attempts | server $seen | waits ${waits.ifEmpty { listOf("-") }.joinToString(",")} | ${names.joinToString(" ")} | ${states.joinToString(",")}"
    }

    @Test
    fun theFamilyTable() {
        val rows = linkedMapOf<String, String>()
        for (p in listOf("200", "400", "404", "301-404", "408", "429", "500", "503", "503-503-200", "304")) rows[p] = row("/$p.png")
        // A connection cut before any answer: its own server with the cut queued (a Dispatcher's
        // DISCONNECT_AT_START is read from peek(), which answers an empty 200 by default).
        val cut = MockWebServer().apply { repeat(12) { enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START)) }; start() }
        try { rows["cut"] = rowAt(cut.url("/cut.png").toString(), "/cut.png") { cut.requestCount } } finally { cut.shutdown() }
        val table = rows.entries.joinToString("\n") { (k, v) -> "$k :: $v" }
        Log.i("KjuiRetryTable", "\n$table")
        val once = "attempts 1 | server 1 | waits - | requesting failed(HttpException) | loading,failed"
        val thrice = "attempts 3 | server 3 | waits 2s,4s | requesting retrying(1) requesting retrying(2) requesting failed(HttpException) | loading,failed"
        val expected = linkedMapOf(
            "200" to "attempts 1 | server 1 | waits - | requesting responded progress finished | loading,loaded",
            "400" to once,
            "404" to once,
            "301-404" to "attempts 1 | server 2 | waits - | requesting failed(HttpException) | loading,failed",
            // OkHttp repeats a 408 itself once per call: 3 attempts, 6 requests.
            "408" to thrice.replace("server 3", "server 6"),
            "429" to thrice,
            "500" to thrice,
            "503" to thrice,
            "503-503-200" to "attempts 3 | server 3 | waits 2s,4s | requesting retrying(1) requesting retrying(2) requesting responded progress finished | loading,loaded",
            "304" to "attempts 1 | server 1 | waits - | requesting failed(IOException) | loading,failed",
            "cut" to "attempts 3 | server 3 | waits 2s,4s | requesting retrying(1) requesting retrying(2) requesting failed(IOException) | loading,failed",
        )
        assertEquals("the table\n$table", expected.entries.joinToString("\n") { (k, v) -> "$k :: $v" }, table)
    }
}
