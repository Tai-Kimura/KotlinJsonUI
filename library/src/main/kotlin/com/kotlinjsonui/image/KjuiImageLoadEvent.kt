package com.kotlinjsonui.image

/**
 * What a NetworkImage's read is doing (jsonui-cli ticket jui-networkimage-load-listener,
 * "知らせ"). The same names as SwiftJsonUI's NetworkImageLoadEvent; `Queued` is Android's
 * only (OkHttp's Dispatcher queue). `Retrying` is told before each wait to read again, as on
 * iOS (the requester's ruling 2026-11-15, ticket jui-networkimage-retry-rules-differ-by-os).
 *
 * Delivered on the main thread. Only [Progress] is thinned (a 1/64 step of the expected
 * length, and the last one; then at most one per frame); every other event is delivered,
 * in order.
 */
sealed interface KjuiImageLoadEvent {
    /** Waiting in OkHttp's Dispatcher queue (the per-host limit). */
    data object Queued : KjuiImageLoadEvent

    /** The call started (connecting included). Again, with `attempt` + 1, when a new call reads the same URL. */
    data class Requesting(val attempt: Int) : KjuiImageLoadEvent

    /**
     * The image's response head arrived: a 2xx, or a 304 answering a conditional request.
     * `expected` is its Content-Length, null when not sent. A redirect's head and a 4xx / 5xx
     * are not the image: neither is told here nor counted as [Progress] (the load goes on to
     * the redirect's target, or to [Failed]).
     */
    data class Responded(val expected: Long?) : KjuiImageLoadEvent

    /** The image's bytes read so far (an error page's are not counted). */
    /**
     * Waiting to read again after a passing failure (5xx, 408, 429, a cut connection);
     * `attempt` is the next attempt (1, 2). Then `Requesting(attempt)`, from 0 bytes.
     */
    data class Retrying(val attempt: Int) : KjuiImageLoadEvent

    data class Progress(val received: Long, val expected: Long?) : KjuiImageLoadEvent

    /** Decoded and ready to draw. */
    data class Finished(val source: KjuiImageSource) : KjuiImageLoadEvent

    data class Failed(val error: Throwable?) : KjuiImageLoadEvent

    data object Cancelled : KjuiImageLoadEvent
}

/** Where a finished image came from. */
enum class KjuiImageSource { MEMORY, DISK, NETWORK }

/**
 * A load's phase, the same names on both OSes: `receiving` from the first body byte;
 * `queued` is Android's only. A renderer that joins late reads it to know where the load
 * stands.
 */
enum class KjuiImagePhase { IDLE, QUEUED, REQUESTING, RESPONDED, RECEIVING, RETRYING, FINISHED, FAILED, CANCELLED;

    val isTerminal: Boolean get() = this == FINISHED || this == FAILED || this == CANCELLED
}
