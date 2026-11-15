package com.kotlinjsonui.image

import coil3.Extras
import coil3.network.NetworkHeaders
import coil3.network.NetworkRequest
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Collections
import java.util.concurrent.TimeUnit

/**
 * The read's events (jsonui-cli ticket jui-networkimage-load-listener, "知らせ" and the reply
 * 5-7): the order, progress thinned to a 1/64 step, the call routed to its load by its tag,
 * OkHttp's queue, and cancellation. A real OkHttp against MockWebServer; the main-thread
 * dispatch is replaced by an immediate one (no looper on the JVM).
 */
class KjuiImageLoadTest {
    private lateinit var server: MockWebServer
    private val events = Collections.synchronizedMap(mutableMapOf<String, MutableList<KjuiImageLoadEvent>>())
    private var now = 1_000L

    private val immediate = object : KjuiImageDispatch {
        override fun send(load: KjuiImageLoad, event: KjuiImageLoadEvent) = record(load, event)
        override fun progress(load: KjuiImageLoad) = record(load, load.progressSnapshot())
        override fun global(key: String, event: KjuiImageLoadEvent) {}
        private fun record(load: KjuiImageLoad, event: KjuiImageLoadEvent) {
            synchronized(events) { events.getOrPut(load.key) { mutableListOf() }.add(event) }
        }
    }

    @Before
    fun setUp() {
        server = MockWebServer().apply { start() }
        KjuiImageLoadTable.dispatchOverride = immediate
        KjuiImageLoadTable.clock = { now }
        KjuiImageLoadTable.clearForTest()
    }

    @After
    fun tearDown() {
        server.shutdown()
        KjuiImageLoadTable.dispatchOverride = null
        KjuiImageLoadTable.clearForTest()
    }

    private fun request(url: String, load: KjuiImageLoad) = NetworkRequest(
        url = url, method = "GET", headers = NetworkHeaders.EMPTY, body = null,
        extras = Extras.Builder().set(KjuiImageLoadKey, load).build(),
    )

    private suspend fun KjuiNetworkClient.read(url: String, load: KjuiImageLoad): Long =
        executeRequest(request(url, load)) { response ->
            val sink = Buffer()
            response.body!!.writeTo(sink)
            sink.size
        }

    private fun body(size: Int) = Buffer().write(ByteArray(size) { (it % 251).toByte() })

    @Test
    fun aReadIsRequestingThenRespondedThenThinnedProgressInOrder() = runBlocking {
        server.enqueue(MockResponse().setBody(body(65_536)).throttleBody(4_096, 1, TimeUnit.MILLISECONDS))
        val url = server.url("/a.png").toString()
        val load = KjuiImageLoadTable.acquire(url)
        val read = KjuiNetworkClient(KjuiImageLoaderFactory.buildClient()).read(url, load)
        assertEquals(65_536L, read)
        load.finished(KjuiImageSource.NETWORK)

        val got = events.getValue(url)
        assertEquals(KjuiImageLoadEvent.Requesting(0), got[0])
        assertEquals(KjuiImageLoadEvent.Responded(65_536L), got[1])
        val progress = got.filterIsInstance<KjuiImageLoadEvent.Progress>()
        assertTrue("1/64 steps and the last: ${progress.size}", progress.size in 2..65)
        assertEquals(65_536L, progress.last().received)
        assertTrue(progress.zipWithNext().all { (a, b) -> b.received > a.received })
        assertEquals(KjuiImageLoadEvent.Finished(KjuiImageSource.NETWORK), got.last())
        assertEquals(KjuiImagePhase.FINISHED, load.phase)
        assertEquals(now, load.respondedAt)
    }

    @Test
    fun eachCallReportsToItsOwnLoadByItsTag() = runBlocking {
        server.dispatcher = object : okhttp3.mockwebserver.Dispatcher() {
            override fun dispatch(request: okhttp3.mockwebserver.RecordedRequest) =
                MockResponse().setBody(body(if (request.path == "/a.png") 10_000 else 30_000))
        }
        val a = server.url("/a.png").toString()
        val b = server.url("/b.png").toString()
        val client = KjuiNetworkClient(KjuiImageLoaderFactory.buildClient())
        val la = KjuiImageLoadTable.acquire(a)
        val lb = KjuiImageLoadTable.acquire(b)
        listOf(async(Dispatchers.IO) { client.read(a, la) }, async(Dispatchers.IO) { client.read(b, lb) }).awaitAll()
        assertEquals(10_000L, la.received)
        assertEquals(30_000L, lb.received)
        assertEquals(KjuiImageLoadEvent.Responded(10_000L), events.getValue(a)[1])
        assertEquals(KjuiImageLoadEvent.Responded(30_000L), events.getValue(b)[1])
    }

    @Test
    fun theSixthCallToOneHostWaitsQueuedBehindFive() = runBlocking {
        repeat(6) { server.enqueue(MockResponse().setBody(body(100)).setHeadersDelay(400, TimeUnit.MILLISECONDS)) }
        val client = KjuiNetworkClient(KjuiImageLoaderFactory.buildClient())
        val urls = (0 until 6).map { server.url("/q$it.png").toString() }
        val loads = urls.map { KjuiImageLoadTable.acquire(it) }
        urls.zip(loads).map { (u, l) -> async(Dispatchers.IO) { client.read(u, l) } }.awaitAll()
        val queued = urls.count { u -> events.getValue(u).contains(KjuiImageLoadEvent.Queued) }
        assertEquals("the per-host limit of 5 holds; one waited", 1, queued)
        urls.forEach { u -> assertTrue(events.getValue(u).any { it is KjuiImageLoadEvent.Requesting }) }
    }

    @Test
    fun aCancelledReadCancelsItsCall() = runBlocking {
        server.enqueue(MockResponse().setBody(body(100)).setHeadersDelay(5, TimeUnit.SECONDS))
        val okhttp = KjuiImageLoaderFactory.buildClient()
        val client = KjuiNetworkClient(okhttp)
        val url = server.url("/slow.png").toString()
        val load = KjuiImageLoadTable.acquire(url)
        val started = CompletableDeferred<Unit>()
        val job = launch(Dispatchers.IO) {
            started.complete(Unit)
            client.read(url, load)
        }
        started.await()
        withTimeout(2_000) { while (okhttp.dispatcher.runningCallsCount() == 0) kotlinx.coroutines.delay(10) }
        job.cancel()
        withTimeout(2_000) { while (okhttp.dispatcher.runningCallsCount() > 0) kotlinx.coroutines.delay(10) }
        assertEquals(0, okhttp.dispatcher.runningCallsCount())
    }

    @Test
    fun progressIsThinnedToA64thOfTheLength() {
        val load = KjuiImageLoadTable.acquire("u")
        load.responded(6_400, now)
        repeat(640) { load.bytes(10, now) }
        val progress = events.getValue("u").filterIsInstance<KjuiImageLoadEvent.Progress>()
        assertEquals(64, progress.size)
        assertEquals(6_400L, progress.last().received)
    }

    @Test
    fun oneUrlIsOneLoadWhileAFrameDrawsIt() {
        val first = KjuiImageLoadTable.acquire("x")
        assertSame(first, KjuiImageLoadTable.acquire("x"))
        assertEquals(2, KjuiImageLoadTable.viewers("x"))
        first.requesting()
        KjuiImageLoadTable.release(first)
        KjuiImageLoadTable.release(first)
        assertEquals("the last frame out cancels the read in flight", KjuiImagePhase.CANCELLED, first.phase)
        val next = KjuiImageLoadTable.acquire("x")
        assertTrue("a new read after the cancelled one", next !== first)
    }

    @Test
    fun aNewCallForTheSameLoadCountsAnAttempt() {
        val load = KjuiImageLoadTable.acquire("y")
        load.requesting()
        load.requesting()
        assertEquals(1, load.attempt)
        assertEquals(KjuiImageLoadEvent.Requesting(1), events.getValue("y").last())
    }
}
