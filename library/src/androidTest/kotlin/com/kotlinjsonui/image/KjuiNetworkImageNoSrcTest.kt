package com.kotlinjsonui.image

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil3.SingletonImageLoader
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

/**
 * A NetworkImage with no src draws defaultImage (shared/core/attribute_semantics.json
 * networkImage.noSrc, the requester's ruling 2026-08-03; jsonui-cli ticket
 * kjui-networkimage-empty-src-draws-error-image); a read that fails draws errorImage. A src
 * of null, "" or only spaces is no src: nothing is read, onLoadStateChange is not called and
 * no renderer is made, as SwiftJsonUI's NetworkImage does with no URL.
 *
 * Each row: the colour drawn (default red, error blue, loading green), the states
 * onLoadStateChange called, the renderers made. Measured with each way a frame is drawn:
 * nothing new declared (coil's AsyncImage as before), onLoadStateChange only, and a renderer.
 */
@RunWith(AndroidJUnit4::class)
class KjuiNetworkImageNoSrcTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var server: MockWebServer

    companion object {
        private val made = AtomicInteger()

        /** A yellow square: the loaded image, found by its colour. */
        private val yellowPng: ByteArray by lazy {
            val bmp = android.graphics.Bitmap.createBitmap(24, 24, android.graphics.Bitmap.Config.ARGB_8888)
            bmp.eraseColor(android.graphics.Color.YELLOW)
            java.io.ByteArrayOutputStream().also { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }.toByteArray()
        }

        @BeforeClass @JvmStatic
        fun installLoader() {
            // The no-src rows read nothing; a 404 fails with or without the factory. When an
            // earlier class made coil's loader first, that one is used.
            runCatching { SingletonImageLoader.setSafe(KjuiImageLoaderFactory()) }
            KjuiLoadingRenderers.register("noSrcProbe") {
                made.incrementAndGet()
                object : KjuiLoadingRendererInstance {
                    override fun DrawScope.draw(frame: KjuiLoadingFrame) = KjuiNextFrame.Idle
                    override fun release() {}
                }
            }
        }
    }

    @Before
    fun setUp() {
        server = MockWebServer().apply {
            dispatcher = object : okhttp3.mockwebserver.Dispatcher() {
                override fun dispatch(request: okhttp3.mockwebserver.RecordedRequest): MockResponse =
                    if (request.path.orEmpty().startsWith("/ok.png")) MockResponse().setBody(okio.Buffer().write(yellowPng))
                    else MockResponse().setResponseCode(404).setBody("Not Found")
            }
            start()
        }
    }

    @After
    fun tearDown() = server.shutdown()

    private fun drawable(name: String): Int {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        return ctx.resources.getIdentifier(name, "drawable", ctx.packageName).also { check(it != 0) { name } }
    }

    private class Case(val model: Any?, val mode: String)

    private var shown by mutableStateOf<Case?>(null)
    private var composed = false
    private var states: MutableList<String> = Collections.synchronizedList(mutableListOf())

    private fun colour(): String {
        val map = compose.onNodeWithTag("img", useUnmergedTree = true).captureToImage().toPixelMap()
        val c = map[map.width / 2, map.height / 2]
        return when {
            c.red > 0.9f && c.green < 0.1f && c.blue < 0.1f -> "default"
            c.blue > 0.9f && c.red < 0.1f && c.green < 0.1f -> "error"
            c.green > 0.9f && c.red < 0.1f && c.blue < 0.1f -> "loading"
            c.red > 0.9f && c.green > 0.9f && c.blue < 0.1f -> "image"
            c.alpha < 0.1f -> "nothing"
            else -> "other(${c.red},${c.green},${c.blue},${c.alpha})"
        }
    }

    /** Shows [model] drawn as [mode] and returns its row once it has settled. */
    private fun row(model: Any?, mode: String): String {
        val defaultId = drawable("nosrc_probe_default")
        val errorId = drawable("nosrc_probe_error")
        val loadingId = drawable("nosrc_probe_loading")
        states = Collections.synchronizedList(mutableListOf())
        made.set(0)
        if (!composed) {
            compose.setContent {
                shown?.let { c ->
                    val sink = states
                    key(c) {
                        KjuiNetworkImage(
                            model = c.model, contentDescription = "probe",
                            modifier = Modifier.size(48.dp).testTag("img"),
                            placeholder = loadingId, error = errorId, fallback = defaultId,
                            loadingRenderer = if (c.mode == "renderer") "noSrcProbe" else null,
                            onLoadStateChange = if (c.mode == "plain") null else { s -> sink.add(s) },
                        )
                    }
                }
            }
            composed = true
        }
        compose.runOnIdle { shown = null }
        compose.waitForIdle()
        compose.runOnIdle { shown = Case(model, mode) }
        // A read ends in "failed" (404) well inside this; a frame with no src stays as it is.
        Thread.sleep(1_200)
        compose.waitForIdle()
        // A read still in flight shows the loading image: wait for it to end (a 404 ends quickly).
        var drawn = colour()
        val until = System.currentTimeMillis() + 5_000
        while (drawn == "loading" && System.currentTimeMillis() < until) {
            Thread.sleep(200)
            compose.waitForIdle()
            drawn = colour()
        }
        return "$drawn | ${states.toList().joinToString(" ").ifEmpty { "-" }} | renderers ${made.get()}"
    }

    @Test
    fun noSrcDrawsDefaultImage_aFailedReadDrawsErrorImage() {
        val failing = server.url("/missing.jpg").toString()
        val srcs = listOf<Pair<String, Any?>>("null" to null, "\"\"" to "", "spaces" to "   ", "404" to failing)
        val rows = linkedMapOf<String, String>()
        for (mode in listOf("plain", "state", "renderer")) for ((name, model) in srcs) rows["$mode $name"] = row(model, mode)
        val table = rows.entries.joinToString("\n") { (k, v) -> "$k :: $v" }
        Log.i("KjuiNoSrcTable", "\n$table")

        val expected = linkedMapOf<String, String>()
        for (mode in listOf("plain", "state", "renderer")) {
            for (name in listOf("null", "\"\"", "spaces")) expected["$mode $name"] = "default | - | renderers 0"
            expected["$mode 404"] = when (mode) {
                "plain" -> "error | - | renderers 0"
                "state" -> "error | loading failed | renderers 0"
                // The renderer is made after loadingDelay (200 ms); a quick 404 may end first.
                else -> "error | loading failed | renderers *"
            }
        }
        val drawn = rows.mapValues { (k, v) -> if (expected[k]?.endsWith("*") == true) v.replace(Regex("renderers \\d+$"), "renderers *") else v }
        assertEquals("the table\n$table", expected.entries.joinToString("\n") { "${it.key} :: ${it.value}" },
            drawn.entries.joinToString("\n") { "${it.key} :: ${it.value}" })
    }

    /**
     * A src that becomes empty draws defaultImage, not the image it drew; the states stay as
     * they were (no new state is reported for no src), and a URL that comes after is read
     * and reported again from "loading".
     */
    @Test
    fun aSrcThatBecomesEmptyDrawsDefaultImage() {
        val url = server.url("/ok.png").toString()
        var src by mutableStateOf<Any?>(url)
        val seen = Collections.synchronizedList(mutableListOf<String>())
        val defaultId = drawable("nosrc_probe_default")
        val errorId = drawable("nosrc_probe_error")
        compose.setContent {
            KjuiNetworkImage(
                model = src, contentDescription = "probe", modifier = Modifier.size(48.dp).testTag("img"),
                error = errorId, fallback = defaultId, onLoadStateChange = { seen.add(it) },
            )
        }
        val steps = mutableListOf<String>()
        fun step(name: String) {
            Thread.sleep(1_200)
            compose.waitForIdle()
            steps.add("$name: ${colour()} | ${seen.toList().joinToString(" ")}")
        }
        step("url")
        compose.runOnIdle { src = "" }
        step("\"\"")
        compose.runOnIdle { src = url + "?again" }
        step("url again")
        compose.runOnIdle { src = null }
        step("null")
        Log.i("KjuiNoSrcTable", "\n" + steps.joinToString("\n"))
        assertEquals(
            listOf(
                "url: image | loading loaded",
                "\"\": default | loading loaded",
                "url again: image | loading loaded loading loaded",
                "null: default | loading loaded loading loaded",
            ),
            steps,
        )
    }
}
