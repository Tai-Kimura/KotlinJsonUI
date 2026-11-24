package com.kotlinjsonui.dynamic

import android.os.SystemClock
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil3.SingletonImageLoader
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.image.KjuiImageLoaderFactory
import com.kotlinjsonui.image.KjuiLoadingFrame
import com.kotlinjsonui.image.KjuiLoadingRendererInstance
import com.kotlinjsonui.image.KjuiLoadingRenderers
import com.kotlinjsonui.image.KjuiNextFrame
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
 * A Dynamic NetworkImage with no src draws defaultImage (shared/core/attribute_semantics.json
 * networkImage.noSrc; jsonui-cli ticket kjui-networkimage-empty-src-draws-error-image); a read
 * that fails draws errorImage. No src: the attribute absent, "", only spaces, or a binding
 * that resolves to "" or to nothing. Nothing is read, onLoadStateChange is not called and no
 * renderer is made, as SwiftJsonUI's NetworkImage does with no URL.
 *
 * Each row: the colour drawn (default red, error blue, loading green), the states
 * onLoadStateChange called, the renderers made.
 */
@RunWith(AndroidJUnit4::class)
class DynamicNetworkImageNoSrcTest {
    @get:Rule val rule = createComposeRule()
    private lateinit var server: MockWebServer

    companion object {
        private val made = AtomicInteger()
        private const val POLL_MS = 100L
        private const val FLOOR_MS = 1_200L
        private const val TIMEOUT_MS = 10_000L

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

    private var shown by mutableStateOf<Pair<JsonObject, Map<String, Any>>?>(null)
    private var composed = false

    private fun colour(): String {
        val map = rule.onNodeWithTag("img", useUnmergedTree = true).captureToImage().toPixelMap()
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

    /**
     * The colour drawn once the image has settled: [expected] read twice in a row and at least
     * [atLeast] onLoadStateChange calls seen, and no sooner than [FLOOR_MS] after it was shown
     * (a view that reads nothing has that long to report or draw something it should not).
     * The image is read and decoded off Compose's idle, so waitForIdle does not wait for the
     * drawing, and on a loaded emulator it takes longer than any fixed sleep. Past
     * [TIMEOUT_MS] the colour drawn then is returned, and the comparison fails on it.
     */
    private fun settled(expected: String, atLeast: Int, states: () -> Int, shownAt: Long): String {
        var last: String? = null
        while (true) {
            Thread.sleep(POLL_MS)
            rule.waitForIdle()
            val drawn = colour()
            val waited = SystemClock.uptimeMillis() - shownAt
            if (waited >= FLOOR_MS && drawn == expected && last == expected && states() >= atLeast) return drawn
            if (waited >= TIMEOUT_MS) return drawn
            last = drawn
        }
    }

    /**
     * Shows a NetworkImage with [src] (a JSON fragment, or "" for none) and returns its row once
     * [expect] is drawn and [expectStates] states are reported (or the wait is over).
     */
    private fun row(src: String, mode: String, expect: String, expectStates: Int, data: Map<String, Any> = emptyMap()): String {
        val states = Collections.synchronizedList(mutableListOf<String>())
        made.set(0)
        val extra = buildString {
            if (src.isNotEmpty()) append(""", "src": $src""")
            if (mode != "plain") append(""", "onLoadStateChange": "@{onState}"""")
            if (mode == "renderer") append(""", "loadingRenderer": "noSrcProbe"""")
        }
        val json = JsonParser.parseString(
            """{"type": "NetworkImage", "id": "img", "width": 48, "height": 48,
               "defaultImage": "nosrc_probe_default", "errorImage": "nosrc_probe_error",
               "placeholder": "nosrc_probe_loading" $extra}"""
        ).asJsonObject
        val onState: (String, Any) -> Unit = { _, value -> states.add(value.toString()) }
        if (!composed) {
            rule.setContent { shown?.let { (j, d) -> key(j, d) { DynamicView(json = j, data = d) } } }
            composed = true
        }
        rule.runOnIdle { shown = null }
        rule.waitForIdle()
        rule.runOnIdle { shown = json to (data + ("onState" to onState)) }
        val drawn = settled(expect, expectStates, { states.size }, SystemClock.uptimeMillis())
        return "$drawn | ${states.toList().joinToString(" ").ifEmpty { "-" }} | renderers ${made.get()}"
    }

    @Test
    fun noSrcDrawsDefaultImage_aFailedReadDrawsErrorImage() {
        val failing = server.url("/missing.jpg").toString()
        val rows = linkedMapOf<String, String>()
        for (mode in listOf("plain", "state", "renderer")) {
            rows["$mode absent"] = row("", mode, "default", 0)
            rows["$mode \"\""] = row("\"\"", mode, "default", 0)
            rows["$mode spaces"] = row("\"   \"", mode, "default", 0)
            rows["$mode binding \"\""] = row("\"@{u}\"", mode, "default", 0, mapOf("u" to ""))
            rows["$mode binding unset"] = row("\"@{u}\"", mode, "default", 0)
            rows["$mode 404"] = row("\"$failing\"", mode, "error", if (mode == "plain") 0 else 2)
        }
        val table = rows.entries.joinToString("\n") { (k, v) -> "$k :: $v" }
        Log.i("KjuiNoSrcTable", "\n$table")

        val expected = linkedMapOf<String, String>()
        for (mode in listOf("plain", "state", "renderer")) {
            for (name in listOf("absent", "\"\"", "spaces", "binding \"\"", "binding unset")) {
                expected["$mode $name"] = "default | - | renderers 0"
            }
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
     * A bound src that becomes "" draws defaultImage, not the image it drew; the states stay
     * as they were, and a URL that comes after is read and reported again from "loading".
     */
    @Test
    fun aBoundSrcThatBecomesEmptyDrawsDefaultImage() {
        val url = server.url("/ok.png").toString()
        val states = Collections.synchronizedList(mutableListOf<String>())
        val onState: (String, Any) -> Unit = { _, value -> states.add(value.toString()) }
        val json = JsonParser.parseString(
            """{"type": "NetworkImage", "id": "img", "width": 48, "height": 48, "src": "@{u}",
               "defaultImage": "nosrc_probe_default", "errorImage": "nosrc_probe_error",
               "onLoadStateChange": "@{onState}"}"""
        ).asJsonObject
        var data by mutableStateOf<Map<String, Any>>(mapOf("u" to url, "onState" to onState))
        var shownAt = SystemClock.uptimeMillis()
        rule.setContent { DynamicView(json = json, data = data) }
        val steps = mutableListOf<String>()
        fun step(name: String, expect: String, expectStates: Int) {
            val drawn = settled(expect, expectStates, { states.size }, shownAt)
            steps.add("$name: $drawn | ${states.toList().joinToString(" ")}")
        }
        step("url", "image", 2)
        shownAt = SystemClock.uptimeMillis()
        rule.runOnIdle { data = mapOf("u" to "", "onState" to onState) }
        step("\"\"", "default", 2)
        shownAt = SystemClock.uptimeMillis()
        rule.runOnIdle { data = mapOf("u" to "$url?again", "onState" to onState) }
        step("url again", "image", 4)
        Log.i("KjuiNoSrcTable", "\n" + steps.joinToString("\n"))
        assertEquals(
            listOf(
                "url: image | loading loaded",
                "\"\": default | loading loaded",
                "url again: image | loading loaded loading loaded",
            ),
            steps,
        )
    }
}
