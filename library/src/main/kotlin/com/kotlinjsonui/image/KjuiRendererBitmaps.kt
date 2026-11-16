package com.kotlinjsonui.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Pictures a renderer draws from (ticket jui-networkimage-load-listener, §6.2 の 7): drawn off
 * the main thread into a software bitmap, copied to `Bitmap.Config.HARDWARE` (API 26+; the
 * software one below that), and kept in a cache bounded by bytes.
 *
 * A renderer asks for a picture by key in its draw; the first call starts drawing it and
 * returns null, and the renderer's host is invalidated when it is ready:
 * ```
 * val badge = KjuiRendererBitmaps.get("badge:$w×$h", w, h, host) { canvas -> drawBadge(canvas) }
 *     ?: return KjuiNextFrame.Idle
 * drawImage(badge)
 * ```
 */
object KjuiRendererBitmaps {
    /** The cache's limit in bytes (the pictures' byteCount). Default 8 MiB. */
    @Volatile var maxBytes: Int = 8 * 1024 * 1024
        set(value) {
            field = value
            cache.resize(value)
        }

    private val cache = object : LruCache<String, ImageBitmap>(8 * 1024 * 1024) {
        override fun sizeOf(key: String, value: ImageBitmap) = value.width * value.height * 4
    }
    private val inFlight = ConcurrentHashMap<String, MutableList<KjuiLoadingHost>>()
    private val executor: ExecutorService by lazy {
        Executors.newSingleThreadExecutor { r -> Thread(r, "kjui-renderer-bitmaps").apply { isDaemon = true } }
    }
    private val main by lazy { Handler(Looper.getMainLooper()) }

    /**
     * The picture under `key`, or null while it is being drawn (`host` is invalidated when it
     * is ready). `draw` runs on a background thread on a `width` × `height` canvas.
     */
    fun get(key: String, width: Int, height: Int, host: KjuiLoadingHost, draw: (Canvas) -> Unit): ImageBitmap? {
        cache.get(key)?.let { return it }
        if (width <= 0 || height <= 0) return null
        val waiting = inFlight.computeIfAbsent(key) { mutableListOf() }
        val first: Boolean
        synchronized(waiting) {
            first = waiting.isEmpty()
            waiting.add(host)
        }
        if (first) {
            executor.execute {
                val bitmap = runCatching {
                    val soft = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    draw(Canvas(soft))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        soft.copy(Bitmap.Config.HARDWARE, false)?.also { soft.recycle() } ?: soft
                    } else soft
                }.getOrNull()
                main.post {
                    bitmap?.let { cache.put(key, it.asImageBitmap()) }
                    val hosts = inFlight.remove(key).orEmpty()
                    synchronized(hosts) { hosts.toList() }.forEach { it.invalidate() }
                }
            }
        }
        return null
    }

    /** Drop every picture (e.g. on a theme change, or the app's onTrimMemory). */
    fun clear() = cache.evictAll()
}
