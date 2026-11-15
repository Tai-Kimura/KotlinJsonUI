package com.kotlinjsonui.image

import android.util.Log
import androidx.compose.ui.graphics.drawscope.DrawScope
import java.util.concurrent.ConcurrentHashMap

/**
 * When a renderer wants its next draw (ticket jui-networkimage-load-listener, reply 1).
 * A frame that returns [Idle] is not drawn again until its load moves (any event) or its
 * renderer calls [KjuiLoadingHost.invalidate].
 */
sealed interface KjuiNextFrame {
    /** Do not read the clock; wait for the load or for invalidate(). */
    data object Idle : KjuiNextFrame

    /** Draw once more at this uptime (milliseconds, KjuiLoadingFrame.now()'s clock). */
    data class At(val uptimeMillis: Long) : KjuiNextFrame

    /** Draw every frame at this rate (rounded to a divisor of 60). */
    data class Continuous(val fps: Int) : KjuiNextFrame

    /** The renderer has nothing more to draw. */
    data object Done : KjuiNextFrame
}

/**
 * A renderer for one frame and one load: made by the factory registered under its name
 * when the load has run loadingDelay, released when the frame leaves, the URL changes or
 * the node is reused. Draws with DrawScope (not a composable); holds its own state.
 */
interface KjuiLoadingRendererInstance {
    fun DrawScope.draw(frame: KjuiLoadingFrame): KjuiNextFrame
    fun release()
}

/** Given to a renderer's factory: [invalidate] draws its frame once more on the next frame (any thread). */
fun interface KjuiLoadingHost {
    fun invalidate()
}

/** How a frame's load ended, for its renderer: the frame's own success / error (not the shared load's). */
class KjuiLoadEnd(
    val result: Result,
    /** Where the image came from; null when it failed. */
    val source: KjuiImageSource?,
    /** When the library starts fading the image (or the error image) in: the same uptime its fade starts from. */
    val startUptimeMillis: Long,
) {
    enum class Result { LOADED, FAILED }
}

/** What a renderer draws from. */
class KjuiLoadingFrame internal constructor(
    val load: KjuiImageLoad,
    /** Milliseconds since the URL's read began (its shared load), not since this frame came. */
    val sinceStartMillis: Long,
    /** Settings.Global.ANIMATOR_DURATION_SCALE is 0. */
    val reduceMotion: Boolean,
    val isDark: Boolean,
    /** Set from the frame's own success / error on; draw is called until the fade ends or the renderer returns Done. */
    val end: KjuiLoadEnd?,
    private val clock: () -> Long,
) {
    /** The shared clock (uptime milliseconds), read only when called. */
    fun now(): Long = clock()
}

/** The renderers an app registered, by name. */
object KjuiLoadingRenderers {
    private val factories = ConcurrentHashMap<String, (KjuiLoadingHost) -> KjuiLoadingRendererInstance>()
    private val warned = ConcurrentHashMap.newKeySet<String>()

    /** Register a renderer: `factory` makes one instance per frame × load. */
    fun register(name: String, factory: (host: KjuiLoadingHost) -> KjuiLoadingRendererInstance) {
        factories[name] = factory
    }

    fun unregister(name: String) {
        factories.remove(name)
    }

    /**
     * The factory named, or null: none named, "" (a binding turning it off) or a name
     * nothing registered — the loading state as before (the placeholder chain). An
     * unregistered name is logged once in DEBUG builds.
     */
    internal fun resolve(name: String?, debug: Boolean): ((KjuiLoadingHost) -> KjuiLoadingRendererInstance)? {
        if (name.isNullOrEmpty()) return null
        val factory = factories[name]
        if (factory == null && debug && warned.add(name)) {
            Log.w("KjuiNetworkImage", "loadingRenderer \"$name\" is not registered (KjuiLoadingRenderers.register); the placeholder is drawn instead")
        }
        return factory
    }
}
