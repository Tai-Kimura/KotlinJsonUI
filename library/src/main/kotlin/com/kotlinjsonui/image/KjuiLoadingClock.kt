package com.kotlinjsonui.image

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.view.Choreographer

/**
 * The one clock the renderers and the fades run on (ticket jui-networkimage-load-listener,
 * §6.2 の 6, reply 1・11). Main thread only.
 *
 * - Frames are asked from Choreographer only while some frame waits for a time; a frame
 *   that returned Idle is not woken until its load moves or its renderer invalidates.
 * - A wait far ahead is a Handler post, not a frame per vsync.
 * - At most [STARTS_PER_FRAME] renderers are made per frame; drawing ones are not counted.
 * - Uptime milliseconds, not scaled by the animator duration scale (reduce motion leaves the
 *   fades as they are; the renderer reads [reduceMotion] itself).
 */
internal object KjuiLoadingClock : Choreographer.FrameCallback {
    const val STARTS_PER_FRAME = 4

    fun interface Waiter {
        fun wake()
    }

    private val handler = Handler(Looper.getMainLooper())
    private val due = LinkedHashMap<Waiter, Long>()
    private var framePosted = false
    private var frameTime = -1L
    private var startsThisFrame = 0
    private var startsFrame = -1L
    private val waitPost = Runnable { postFrame() }

    /** The current frame's time inside a frame, the uptime otherwise. */
    fun now(): Long = if (frameTime >= 0) frameTime else SystemClock.uptimeMillis()

    /** Wake `waiter` on the first frame at or after `uptimeMillis`. */
    fun wakeAt(waiter: Waiter, uptimeMillis: Long) {
        val current = due[waiter]
        if (current == null || uptimeMillis < current) due[waiter] = uptimeMillis
        reschedule()
    }

    /** Wake `waiter` on the next frame. */
    fun wakeNextFrame(waiter: Waiter) = wakeAt(waiter, 0L)

    fun cancel(waiter: Waiter) {
        due.remove(waiter)
    }

    /** May a renderer be made this frame? (4 a frame) */
    fun tryStart(): Boolean {
        val frame = if (frameTime >= 0) frameTime else SystemClock.uptimeMillis() / 16
        if (frame != startsFrame) {
            startsFrame = frame
            startsThisFrame = 0
        }
        if (startsThisFrame >= STARTS_PER_FRAME) return false
        startsThisFrame += 1
        return true
    }

    private fun reschedule() {
        if (due.isEmpty() || framePosted) return
        val earliest = due.values.min()
        val wait = earliest - SystemClock.uptimeMillis()
        handler.removeCallbacks(waitPost)
        if (wait > 32) handler.postAtTime(waitPost, earliest - 16) else postFrame()
    }

    private fun postFrame() {
        if (framePosted) return
        framePosted = true
        Choreographer.getInstance().postFrameCallback(this)
    }

    override fun doFrame(frameTimeNanos: Long) {
        framePosted = false
        frameTime = frameTimeNanos / 1_000_000
        try {
            val ready = due.filterValues { it <= frameTime }.keys.toList()
            ready.forEach { due.remove(it) }
            ready.forEach { it.wake() }
        } finally {
            frameTime = -1L
        }
        reschedule()
    }

    // ── reduce motion: Settings.Global.ANIMATOR_DURATION_SCALE == 0 (reply 9) ──

    @Volatile private var reduceMotionValue: Boolean? = null
    private val reduceMotionListeners = mutableSetOf<() -> Unit>()

    fun reduceMotion(context: Context): Boolean {
        reduceMotionValue?.let { return it }
        val app = context.applicationContext
        val read = { Settings.Global.getFloat(app.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f }
        app.contentResolver.registerContentObserver(
            Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE), false,
            object : ContentObserver(handler) {
                override fun onChange(selfChange: Boolean) {
                    reduceMotionValue = read()
                    reduceMotionListeners.toList().forEach { it() }
                }
            }
        )
        return read().also { reduceMotionValue = it }
    }

    fun onReduceMotionChange(listener: () -> Unit): () -> Unit {
        reduceMotionListeners.add(listener)
        return { reduceMotionListeners.remove(listener) }
    }
}
