package com.example.kotlinjsonui.sample

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil3.SingletonImageLoader
import com.kotlinjsonui.image.KjuiImageLoaderFactory
import com.kotlinjsonui.image.KjuiLoadingFrame
import com.kotlinjsonui.image.KjuiLoadingRendererInstance
import com.kotlinjsonui.image.KjuiLoadingRenderers
import com.kotlinjsonui.image.KjuiNetworkImage
import com.kotlinjsonui.image.KjuiNextFrame
import java.io.ByteArrayOutputStream
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

/**
 * A NetworkImage loading-renderer demo (debug builds only; jsonui-cli ticket
 * jui-networkimage-load-listener, D). A local server sends each image slowly; the test
 * renderer is a box filled from the bottom to received / expected. "Again" draws the same
 * grid again: the images are in the memory cache, so no renderer is made.
 *
 *   adb shell am start -n com.example.kotlinjsonui.sample.dev.debug/com.example.kotlinjsonui.sample.NetworkImageLoadDemoActivity
 */
class NetworkImageLoadDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SingletonImageLoader.setSafe(KjuiImageLoaderFactory())
        KjuiLoadingRenderers.register("fillBox") { FillBoxRenderer() }
        val port = SlowImageServer.start()
        setContent {
            var round by remember { mutableIntStateOf(0) }
            Column(Modifier.fillMaxSize().background(Color(0xFF15171C)).statusBarsPadding()) {
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { round += 1 }, modifier = Modifier.testTag("again")) { Text("Again ($round)") }
                }
                // `round` re-keys the grid: a fresh composition of the same URLs.
                androidx.compose.runtime.key(round) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items((0 until 30).toList()) { i ->
                            KjuiNetworkImage(
                                model = "http://127.0.0.1:$port/img/$i.jpg",
                                contentDescription = "image $i",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().aspectRatio(0.75f)
                                    .clip(RoundedCornerShape(10.dp)).background(Color(0xFF23262E)),
                                loadingRenderer = "fillBox",
                            )
                        }
                    }
                }
            }
        }
    }
}

/** The test renderer: a box outlined, filled from the bottom to received / expected, eased toward it. */
private class FillBoxRenderer : KjuiLoadingRendererInstance {
    private var level = -1f
    private var lastAt = 0L
    private var goneAt = -1L

    override fun DrawScope.draw(frame: KjuiLoadingFrame): KjuiNextFrame {
        val now = frame.now()
        val load = frame.load
        if (level < 0f) level = load.displayLevel
        val target = load.expected?.takeIf { it > 0 }?.let { (load.received.toFloat() / it).coerceIn(0f, 1f) } ?: 0f
        val dt = if (lastAt == 0L) 16L else (now - lastAt).coerceIn(1, 100)
        lastAt = now
        level += (target - level) * (1f - Math.exp(-dt / 90.0).toFloat())
        load.displayLevel = level

        var opacity = 1f
        frame.end?.let { end ->
            if (goneAt < 0) goneAt = end.startUptimeMillis
            opacity = (1f - (now - goneAt) / 160f).coerceIn(0f, 1f)
        }
        val w = size.width * 0.42f
        val h = size.height * 0.55f
        val left = (size.width - w) / 2
        val top = (size.height - h) / 2
        val fillH = h * level
        drawRoundRect(Color(0xFFE0A84A).copy(alpha = 0.85f * opacity), Offset(left, top + h - fillH), Size(w, fillH), CornerRadius(6f))
        drawRoundRect(Color(0xFFDDDDDD).copy(alpha = opacity), Offset(left, top), Size(w, h), CornerRadius(6f), style = Stroke(3f))
        return when {
            frame.end != null && opacity <= 0f -> KjuiNextFrame.Done
            frame.end != null -> KjuiNextFrame.Continuous(60)
            kotlin.math.abs(target - level) > 0.002f -> KjuiNextFrame.Continuous(60)
            else -> KjuiNextFrame.Idle
        }
    }

    override fun release() {}
}

/** A local HTTP server that sends each image at about 48 KB/s, with Content-Length. */
private object SlowImageServer {
    private var port = 0
    private val cache = HashMap<Int, ByteArray>()

    @Synchronized
    fun start(): Int {
        if (port != 0) return port
        val socket = ServerSocket(0)
        port = socket.localPort
        thread(isDaemon = true, name = "slow-image-server") {
            while (true) {
                val client = socket.accept()
                thread(isDaemon = true) { serve(client) }
            }
        }
        return port
    }

    private fun image(i: Int): ByteArray = synchronized(cache) {
        cache.getOrPut(i) {
            val bmp = Bitmap.createBitmap(600, 800, Bitmap.Config.ARGB_8888)
            val c = Canvas(bmp)
            val hue = (i * 37 % 360).toFloat()
            val a = android.graphics.Color.HSVToColor(floatArrayOf(hue, 0.55f, 0.85f))
            val b = android.graphics.Color.HSVToColor(floatArrayOf((hue + 60) % 360, 0.7f, 0.35f))
            c.drawPaint(Paint().apply { shader = LinearGradient(0f, 0f, 600f, 800f, a, b, Shader.TileMode.CLAMP) })
            // Noise keeps the JPEG large enough to take a while.
            val p = Paint()
            val rnd = java.util.Random(i.toLong())
            repeat(4000) {
                p.color = android.graphics.Color.argb(60, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                c.drawCircle(rnd.nextFloat() * 600, rnd.nextFloat() * 800, 2f + rnd.nextFloat() * 6, p)
            }
            p.color = android.graphics.Color.WHITE
            p.textSize = 120f
            c.drawText("$i", 40f, 160f, p)
            ByteArrayOutputStream().also { bmp.compress(Bitmap.CompressFormat.JPEG, 92, it) }.toByteArray()
        }
    }

    private fun serve(client: Socket) {
        client.use { s -> respond(s) }
    }

    private fun respond(s: Socket) {
        val reader = s.getInputStream().bufferedReader()
        val line = reader.readLine() ?: return
        while (true) {
            if (reader.readLine().isNullOrEmpty()) break
        }
        val i = Regex("/img/(\\d+)").find(line)?.groupValues?.get(1)?.toInt() ?: 0
        val body = image(i)
        val out = s.getOutputStream()
        out.write("HTTP/1.1 200 OK\r\nContent-Type: image/jpeg\r\nContent-Length: ${body.size}\r\nConnection: close\r\n\r\n".toByteArray())
        Thread.sleep(250L + (i % 5) * 120L)                 // time to the first byte
        var off = 0
        while (off < body.size) {
            val n = minOf(2400, body.size - off)
            out.write(body, off, n)
            out.flush()
            off += n
            Thread.sleep(50)
        }
    }
}
