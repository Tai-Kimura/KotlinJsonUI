package com.kotlinjsonui.dynamic

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil3.SingletonImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream

/**
 * coil3's core has NO network fetcher: an ImageLoader without
 * coil-network-okhttp (or -ktor) on the runtime classpath turns every URL
 * into an ErrorResult — silently, from the screen's point of view (the
 * error/fallback painter shows, nothing is thrown). This pins that the
 * library's declared network artifact reaches the runtime classpath and is
 * discovered by the SINGLETON loader, which is the one AsyncImage uses, so
 * the dynamic NetworkImage/CircleImage components load URLs at all.
 *
 * Measured 2026-09-02 with the artifact removed: this test fails with an
 * ErrorResult ("Unable to create a fetcher") — the migration from coil2
 * (whose network stack was built in) would otherwise have shipped a
 * library that renders every remote image as its error painter.
 *
 * No CI lane runs androidTest here; execute locally against an emulator:
 *   ./gradlew :library-dynamic:connectedDebugAndroidTest \
 *     -Pandroid.testInstrumentationRunnerArguments.class=com.kotlinjsonui.dynamic.CoilNetworkFetcherTest
 */
@RunWith(AndroidJUnit4::class)
class CoilNetworkFetcherTest {

    @Test
    fun theSingletonLoaderFetchesAnHttpUrl() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val png = ByteArrayOutputStream().also {
            Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888).compress(Bitmap.CompressFormat.PNG, 100, it)
        }.toByteArray()

        MockWebServer().use { server ->
            server.enqueue(
                MockResponse()
                    .setHeader("Content-Type", "image/png")
                    .setBody(Buffer().write(png))
            )
            server.start()

            val request = ImageRequest.Builder(context)
                .data(server.url("/probe.png").toString())
                .build()
            val result = runBlocking { SingletonImageLoader.get(context).execute(request) }

            assertTrue(
                "a URL image must load through the singleton loader; got $result — " +
                    "is coil-network-okhttp missing from the runtime classpath?",
                result is SuccessResult
            )
        }
    }
}
