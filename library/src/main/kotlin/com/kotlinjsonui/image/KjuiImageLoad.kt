package com.kotlinjsonui.image

import java.util.concurrent.CopyOnWriteArrayList

/**
 * One URL's read, shared by every frame drawing that URL (KjuiImageLoadTable). Not Compose
 * state: progress reaches a renderer's draw only, never a recomposition.
 *
 * Written from OkHttp's threads, read on the main thread. Times are uptime milliseconds
 * (SystemClock.uptimeMillis, the clock KjuiLoadingFrame.now() reads).
 */
class KjuiImageLoad internal constructor(
    val key: String,
    /** When this read began (the start of loadingDelay and of sinceStartMillis). */
    val startedAt: Long,
    private val dispatch: KjuiImageDispatch,
) {
    @Volatile var received: Long = 0L; private set
    @Volatile var expected: Long? = null; private set
    @Volatile var phase: KjuiImagePhase = KjuiImagePhase.IDLE; private set
    /** 0 for the first call; + 1 for each new call that reads this URL again. */
    @Volatile var attempt: Int = 0; private set
    @Volatile var respondedAt: Long? = null; private set
    @Volatile var lastByteAt: Long? = null; private set
    /** The level a renderer drew last (0..1); a frame that joins later starts from it. */
    @Volatile var displayLevel: Float = 0f

    private val listeners = CopyOnWriteArrayList<(KjuiImageLoadEvent) -> Unit>()
    private var lastSentReceived = 0L
    private var callSeen = false
    private val lock = Any()

    /** Listen to this load's events (main thread). Close to stop. */
    fun observe(listener: (KjuiImageLoadEvent) -> Unit): AutoCloseable {
        listeners.add(listener)
        return AutoCloseable { listeners.remove(listener) }
    }

    internal fun deliver(event: KjuiImageLoadEvent) {
        listeners.forEach { it(event) }
        dispatch.global(key, event)
    }

    // ── from the network (any thread) ──

    internal fun queued() {
        if (phase.isTerminal) return
        phase = KjuiImagePhase.QUEUED
        dispatch.send(this, KjuiImageLoadEvent.Queued)
    }

    /** A call for this URL started: the first, or a new one after the earlier went away. */
    internal fun requesting() {
        val n: Int
        synchronized(lock) {
            if (phase == KjuiImagePhase.FINISHED) return
            if (callSeen) {
                attempt += 1
                // A new attempt reads from 0 bytes, as on iOS: what an earlier one received or
                // was told to expect is not this one's.
                received = 0L
                expected = null
                respondedAt = null
                lastSentReceived = 0L
            }
            callSeen = true
            n = attempt
        }
        phase = KjuiImagePhase.REQUESTING
        dispatch.send(this, KjuiImageLoadEvent.Requesting(n))
    }

    internal fun responded(contentLength: Long?, now: Long) {
        if (phase.isTerminal) return
        expected = contentLength?.takeIf { it >= 0 }
        respondedAt = now
        phase = KjuiImagePhase.RESPONDED
        dispatch.send(this, KjuiImageLoadEvent.Responded(expected))
    }

    internal fun bytes(count: Long, now: Long) {
        if (count <= 0 || phase.isTerminal) return
        val total: Long
        val due: Boolean
        synchronized(lock) {
            received += count
            total = received
            val step = expected?.let { maxOf(1L, it / 64) } ?: 1L
            due = total - lastSentReceived >= step || (expected != null && total >= expected!! && lastSentReceived < total)
            if (due) lastSentReceived = total
        }
        lastByteAt = now
        phase = KjuiImagePhase.RECEIVING
        if (due) dispatch.progress(this)
    }

    /** A passing failure: the next attempt, [next], is read after a wait. */
    internal fun retrying(next: Int) {
        if (phase.isTerminal) return
        phase = KjuiImagePhase.RETRYING
        dispatch.send(this, KjuiImageLoadEvent.Retrying(next))
    }

    // ── from coil's result (main thread) ──

    internal fun finished(source: KjuiImageSource) {
        if (phase.isTerminal) return
        if (received > 0 && received != lastSentReceived) {
            lastSentReceived = received
            dispatch.progress(this)
        }
        phase = KjuiImagePhase.FINISHED
        dispatch.send(this, KjuiImageLoadEvent.Finished(source))
    }

    internal fun failed(error: Throwable?) {
        if (phase.isTerminal) return
        phase = KjuiImagePhase.FAILED
        dispatch.send(this, KjuiImageLoadEvent.Failed(error))
    }

    internal fun cancelled() {
        if (phase.isTerminal) return
        phase = KjuiImagePhase.CANCELLED
        dispatch.send(this, KjuiImageLoadEvent.Cancelled)
    }

    internal fun progressSnapshot() = KjuiImageLoadEvent.Progress(received, expected)
}
