package com.kotlinjsonui.image

import android.graphics.Bitmap
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil3.SingletonImageLoader
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * KjuiNetworkImage on a device (jsonui-cli ticket jui-networkimage-load-listener, 受け入れの確かめ):
 * the cell that holds it is not recomposed while the read's progress arrives; an image the
 * memory cache holds makes no renderer and calls "loaded" alone; a renderer is made only
 * after loadingDelay.
 */
@RunWith(AndroidJUnit4::class)
class KjuiNetworkImageTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var server: MockWebServer

    companion object {
        private val made = AtomicInteger()
        private val draws = AtomicInteger()

        @BeforeClass @JvmStatic
        fun installLoader() {
            SingletonImageLoader.setSafe(KjuiImageLoaderFactory())
            KjuiLoadingRenderers.register("probe") {
                made.incrementAndGet()
                object : KjuiLoadingRendererInstance {
                    override fun DrawScope.draw(frame: KjuiLoadingFrame): KjuiNextFrame {
                        draws.incrementAndGet()
                        return KjuiNextFrame.Idle
                    }
                    override fun release() {}
                }
            }
        }
    }

    private fun jpeg(): Buffer {
        val bmp = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val rnd = java.util.Random(1)
        for (x in 0 until 200) for (y in 0 until 200) bmp.setPixel(x, y, rnd.nextInt() or 0xFF000000.toInt())
        val bytes = ByteArrayOutputStream().also { bmp.compress(Bitmap.CompressFormat.JPEG, 95, it) }.toByteArray()
        return Buffer().write(bytes)
    }

    @Before
    fun setUp() {
        server = MockWebServer().apply { start() }
        made.set(0)
        draws.set(0)
    }

    @After
    fun tearDown() = server.shutdown()

    @Test
    fun theCellIsNotRecomposedWhileProgressArrives_andTheRendererComesAfterTheDelay() {
        // ~40 KB at 2 KB / 20 ms: about 400 ms of progress after a 300 ms wait for the head.
        server.enqueue(MockResponse().setBody(jpeg()).setHeadersDelay(300, TimeUnit.MILLISECONDS)
            .throttleBody(2_048, 20, TimeUnit.MILLISECONDS))
        val url = server.url("/slow.jpg").toString()
        val compositions = AtomicInteger()
        val progress = AtomicInteger()
        val states = mutableListOf<String>()
        val events = java.util.Collections.synchronizedList(mutableListOf<String>())
        compose.setContent {
            SideEffect { compositions.incrementAndGet() }
            KjuiNetworkImage(
                model = url, contentDescription = "slow", modifier = Modifier.size(100.dp),
                loadingRenderer = "probe",
                onLoadStateChange = { synchronized(states) { states.add(it) } },
                onLoadEvent = { if (it is KjuiImageLoadEvent.Progress) progress.incrementAndGet() else events.add(it.toString()) },
            )
        }
        try {
            compose.waitUntil(30_000) { synchronized(states) { states.lastOrNull() == "loaded" } }
        } catch (e: Throwable) {
            throw AssertionError("states=${synchronized(states) { states.toList() }} progress=${progress.get()} " +
                "requests=${server.requestCount} events=$events", e)
        }
        compose.waitForIdle()
        assertTrue("progress reached the frame: ${progress.get()}", progress.get() >= 3)
        assertEquals("the cell composed once, however many events came", 1, compositions.get())
        assertEquals(listOf("loading", "loaded"), synchronized(states) { states.toList() })
        assertEquals("one renderer, made after the 200 ms delay", 1, made.get())
        assertTrue(draws.get() >= 1)
    }

    @Test
    fun aMemoryHitMakesNoRendererAndCallsLoadedAlone() {
        server.enqueue(MockResponse().setBody(jpeg()))
        val url = server.url("/memory.jpg").toString()
        var round by mutableIntStateOf(0)
        val states = mutableListOf<String>()
        compose.setContent {
            if (round == 0) {
                KjuiNetworkImage(model = url, contentDescription = "a", modifier = Modifier.size(100.dp))
            } else {
                KjuiNetworkImage(
                    model = url, contentDescription = "b", modifier = Modifier.size(100.dp),
                    loadingRenderer = "probe",
                    onLoadStateChange = { synchronized(states) { states.add(it) } },
                )
            }
        }
        // The first read fills the memory cache.
        compose.waitUntil(10_000) { server.requestCount == 1 }
        Thread.sleep(500)
        compose.runOnIdle { round = 1 }
        compose.waitUntil(5_000) { synchronized(states) { states.isNotEmpty() } }
        compose.waitForIdle()
        Thread.sleep(400)   // past loadingDelay: a renderer, if one were made, would be by now
        compose.waitForIdle()
        assertEquals(listOf("loaded"), synchronized(states) { states.toList() })
        assertEquals("no renderer for a memory hit", 0, made.get())
        assertEquals("no second read", 1, server.requestCount)
    }
}
