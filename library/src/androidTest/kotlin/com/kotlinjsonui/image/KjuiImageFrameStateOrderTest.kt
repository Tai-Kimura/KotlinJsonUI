package com.kotlinjsonui.image

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil3.asImage
import coil3.decode.DataSource
import coil3.request.ErrorResult
import coil3.request.SuccessResult
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Collections

/**
 * onLoadStateChange's order (shared/core/attribute_semantics.json networkImage.loadStates):
 * one read says "loading" and then "loaded" or "failed"; only a memory hit says "loaded"
 * alone. onStart posts "loading" a turn later, so that a memory hit's success, which comes
 * first, says "loaded" alone. On a loaded device the read's end can come ahead of that post
 * (the request resumes in a frame callback), and the end said "failed" alone (jsonui-cli
 * ticket kjui-networkimage-quick-failure-skips-loading-under-load). Here the end is called
 * inside the same main-thread turn as onStart, ahead of the post, each time.
 */
@RunWith(AndroidJUnit4::class)
class KjuiImageFrameStateOrderTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext

    private fun states(end: (KjuiImageFrame, coil3.request.ImageRequest) -> Unit, endFirst: Boolean): List<String> {
        val seen = Collections.synchronizedList(mutableListOf<String>())
        var frame: KjuiImageFrame? = null
        instrumentation.runOnMainSync {
            val f = KjuiImageFrame("https://order.invalid/${System.nanoTime()}.png")
            frame = f
            f.onState = { seen.add(it) }
            val request = f.request("https://order.invalid/a.png", context)
            request.listener!!.onStart(request)
            if (endFirst) end(f, request)
        }
        instrumentation.waitForIdleSync()
        if (!endFirst) {
            instrumentation.runOnMainSync { end(frame!!, frame!!.request("https://order.invalid/a.png", context)) }
            instrumentation.waitForIdleSync()
        }
        instrumentation.runOnMainSync { frame!!.stop() }
        return seen.toList()
    }

    private val image by lazy { Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888).asImage() }

    private fun fail(request: coil3.request.ImageRequest) =
        request.listener!!.onError(request, ErrorResult(null, request, IOException("probe")))

    private fun succeed(request: coil3.request.ImageRequest, source: DataSource) =
        request.listener!!.onSuccess(request, SuccessResult(image, request, source))

    @Test
    fun aFailureAheadOfThePostedLoadingSaysLoadingFirst() {
        assertEquals(listOf("loading", "failed"), states({ _, r -> fail(r) }, endFirst = true))
    }

    @Test
    fun aNetworkReadAheadOfThePostedLoadingSaysLoadingFirst() {
        assertEquals(listOf("loading", "loaded"), states({ _, r -> succeed(r, DataSource.NETWORK) }, endFirst = true))
        assertEquals(listOf("loading", "loaded"), states({ _, r -> succeed(r, DataSource.DISK) }, endFirst = true))
    }

    @Test
    fun aMemoryHitSaysLoadedAlone() {
        assertEquals(listOf("loaded"), states({ _, r -> succeed(r, DataSource.MEMORY_CACHE) }, endFirst = true))
        assertEquals(listOf("loaded"), states({ _, r -> succeed(r, DataSource.MEMORY) }, endFirst = true))
    }

    @Test
    fun anEndAfterThePostedLoadingSaysEachOnce() {
        assertEquals(listOf("loading", "failed"), states({ _, r -> fail(r) }, endFirst = false))
        assertEquals(listOf("loading", "loaded"), states({ _, r -> succeed(r, DataSource.NETWORK) }, endFirst = false))
    }
}
