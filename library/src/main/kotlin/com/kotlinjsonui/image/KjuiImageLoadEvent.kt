package com.kotlinjsonui.image

/**
 * What a NetworkImage's read is doing (jsonui-cli ticket jui-networkimage-load-listener,
 * "知らせ"). The same names as SwiftJsonUI's NetworkImageLoadEvent; `Queued` is Android's
 * only (OkHttp's Dispatcher queue), and Android has no `retrying` (coil does not retry).
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

    /** The response's head arrived. `expected` is its Content-Length, null when not sent. */
    data class Responded(val expected: Long?) : KjuiImageLoadEvent

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
 * `queued` is Android's only (iOS has `retrying` instead). A renderer that joins late
 * reads it to know where the load stands.
 */
enum class KjuiImagePhase { IDLE, QUEUED, REQUESTING, RESPONDED, RECEIVING, FINISHED, FAILED, CANCELLED;

    val isTerminal: Boolean get() = this == FINISHED || this == FAILED || this == CANCELLED
}
