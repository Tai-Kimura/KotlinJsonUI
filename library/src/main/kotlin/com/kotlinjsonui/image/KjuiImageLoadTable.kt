package com.kotlinjsonui.image

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.Choreographer
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * How a load's events reach the main thread. Every event but progress is posted in order;
 * progress is folded to one per frame, and a pending progress is delivered before the next
 * event so the order holds (ticket jui-networkimage-load-listener, reply 6).
 */
internal interface KjuiImageDispatch {
    fun send(load: KjuiImageLoad, event: KjuiImageLoadEvent)
    fun progress(load: KjuiImageLoad)
    fun global(key: String, event: KjuiImageLoadEvent)
}

/**
 * The loads in flight, one per URL (DeDupeConcurrentRequestStrategy reads a URL once; this
 * table gives every frame drawing it the same [KjuiImageLoad]), and the app-wide listeners.
 */
object KjuiImageLoadTable {
    private val loads = ConcurrentHashMap<String, Entry>()
    private val globalListeners = CopyOnWriteArrayList<(String, KjuiImageLoadEvent) -> Unit>()

    private class Entry(val load: KjuiImageLoad) { var viewers = 0 }

    /** Uptime milliseconds; replaced in unit tests. */
    internal var clock: () -> Long = { SystemClock.uptimeMillis() }

    private val mainDispatch by lazy { MainDispatch() }

    /** Replaced in unit tests (no main looper there). */
    internal var dispatchOverride: KjuiImageDispatch? = null

    internal val dispatch: KjuiImageDispatch get() = dispatchOverride ?: mainDispatch

    /** Listen to every image's events, with its URL (main thread). Close to stop. */
    fun addObserver(listener: (url: String, event: KjuiImageLoadEvent) -> Unit): AutoCloseable {
        globalListeners.add(listener)
        return AutoCloseable { globalListeners.remove(listener) }
    }

    internal val hasObservers: Boolean get() = globalListeners.isNotEmpty()

    /** KjuiImageLoaderFactory built the app's loader: its calls report themselves. */
    @Volatile internal var factoryInstalled = false

    internal fun notifyGlobal(key: String, event: KjuiImageLoadEvent) {
        globalListeners.forEach { it(key, event) }
    }

    /** The URL's load a frame draws from: the one in flight, or a new one. */
    @Synchronized
    internal fun acquire(key: String): KjuiImageLoad {
        val current = loads[key]
        val entry = if (current == null || current.load.phase.isTerminal && current.viewers == 0) {
            Entry(KjuiImageLoad(key, clock(), dispatch)).also { loads[key] = it }
        } else current
        entry.viewers += 1
        return entry.load
    }

    /** A frame stopped drawing `load`. The last one out of a load in flight cancels it. */
    @Synchronized
    internal fun release(load: KjuiImageLoad) {
        val entry = loads[load.key]?.takeIf { it.load === load } ?: return
        entry.viewers -= 1
        if (entry.viewers <= 0) {
            loads.remove(load.key)
            if (!load.phase.isTerminal && load.phase != KjuiImagePhase.IDLE) load.cancelled()
        }
    }

    /** The load a network call for `key` reports to, if a frame is drawing it. */
    internal fun peek(key: String): KjuiImageLoad? = loads[key]?.load

    internal fun viewers(key: String): Int = loads[key]?.viewers ?: 0

    internal fun clearForTest() = loads.clear()

    private class MainDispatch : KjuiImageDispatch {
        private val handler = Handler(Looper.getMainLooper())
        private val pending = ConcurrentHashMap.newKeySet<KjuiImageLoad>()

        override fun send(load: KjuiImageLoad, event: KjuiImageLoadEvent) {
            handler.post {
                flushProgress(load)
                load.deliver(event)
            }
        }

        override fun progress(load: KjuiImageLoad) {
            if (!pending.add(load)) return
            handler.post {
                Choreographer.getInstance().postFrameCallback { flushProgress(load) }
            }
        }

        private fun flushProgress(load: KjuiImageLoad) {
            if (pending.remove(load)) load.deliver(load.progressSnapshot())
        }

        override fun global(key: String, event: KjuiImageLoadEvent) = notifyGlobal(key, event)
    }
}
