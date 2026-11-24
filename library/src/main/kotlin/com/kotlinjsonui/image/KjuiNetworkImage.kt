package com.kotlinjsonui.image

import android.content.pm.ApplicationInfo
import android.os.Handler
import android.os.Looper
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import coil3.compose.AsyncImage
import coil3.decode.DataSource
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult

/**
 * The NetworkImage every kjui-generated view draws (ticket jui-networkimage-load-listener).
 *
 * With nothing new declared it is coil's AsyncImage as the generator wrote it before: the
 * same model, the same modifier, no Box, layer or node added; only the state images come
 * as resource ids.
 *
 * - [loadingRenderer]: the name of a renderer the app registered
 *   ([KjuiLoadingRenderers.register]). While a read from disk or the network takes longer
 *   than [loadingDelayMillis], the renderer draws under the image, told the bytes received;
 *   the placeholder is not drawn. An image the memory cache holds is drawn from the first
 *   frame and no renderer is made. Absent, "" or not registered: the placeholder, as before.
 * - [transitionMillis]: T, the fades into the image and the error image (-1: 240 when a
 *   renderer was found, else 0). With a renderer on the frame: the image over T
 *   (ease-in-out cubic), the error image after T/6 over 5T/6; done before the renderer was
 *   made: T/2 (ease-out quadratic); no renderer named and T written: T (ease-out quadratic).
 *   Never for a memory hit. Not scaled by reduce motion.
 * - [onLoadStateChange]: "loading" / "loaded" / "failed", each only on a change; a memory
 *   hit calls "loaded" alone; a frame that leaves calls nothing.
 * - [onLoadEvent]: the URL's load events (main thread; progress thinned).
 */
@Composable
fun KjuiNetworkImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    colorFilter: ColorFilter? = null,
    @DrawableRes placeholder: Int = 0,
    @DrawableRes error: Int = 0,
    @DrawableRes fallback: Int = 0,
    loadingRenderer: String? = null,
    loadingDelayMillis: Int = 200,
    transitionMillis: Int = -1,
    onLoadStateChange: ((String) -> Unit)? = null,
    onLoadEvent: ((KjuiImageLoadEvent) -> Unit)? = null,
) {
    // No src — null, "" or only spaces — draws `fallback` (defaultImage: networkImage.noSrc in
    // shared/core/attribute_semantics.json) and reads nothing, so nothing is reported, as
    // SwiftJsonUI's NetworkImage does with no URL (jsonui-cli ticket
    // kjui-networkimage-empty-src-draws-error-image). coil takes `fallback` for null data
    // only; "" and spaces were read, failed and drew `error`.
    val src = noSrcAsNull(model)
    val context = LocalContext.current
    val debug = remember(context) { context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0 }
    val factory = KjuiLoadingRenderers.resolve(loadingRenderer, debug)
    val t = when {
        transitionMillis >= 0 -> transitionMillis.coerceAtMost(1000)
        factory != null -> 240
        else -> 0
    }
    val tracked = factory != null || t > 0 || onLoadStateChange != null || onLoadEvent != null ||
        KjuiImageLoadTable.hasObservers
    val key = modelKey(src)

    if (!tracked || key == null) {
        // As before: coil's AsyncImage, nothing added.
        AsyncImage(
            model = src,
            contentDescription = contentDescription,
            modifier = modifier,
            placeholder = placeholder.takeIf { it != 0 }?.let { painterResource(it) },
            error = error.takeIf { it != 0 }?.let { painterResource(it) },
            fallback = fallback.takeIf { it != 0 }?.let { painterResource(it) },
            alignment = alignment,
            contentScale = contentScale,
            colorFilter = colorFilter,
        )
        return
    }

    val stateCallback = rememberUpdatedState(onLoadStateChange)
    val eventCallback = rememberUpdatedState(onLoadEvent)
    val isDark = isSystemInDarkTheme()
    val frame = remember(key) { KjuiImageFrame(key) }
    frame.factory = factory
    frame.transitionMillis = t
    frame.rendererNamed = factory != null
    frame.delayMillis = loadingDelayMillis.coerceIn(0, 2000)
    frame.isDark = isDark
    frame.reduceMotion = KjuiLoadingClock.reduceMotion(context)
    frame.onState = { stateCallback.value?.invoke(it) }
    frame.onEvent = { eventCallback.value?.invoke(it) }

    val request = remember(src, frame) { frame.request(src, context) }
    val alpha = frame.alpha
    // With a renderer the generated modifier goes on the outer box, which hands its own
    // minimum size down (propagateMinConstraints): the image is sized as AsyncImage was.
    val imageModifier = if (factory != null) Modifier else modifier
    val layered = if (factory != null || t > 0) {
        imageModifier.graphicsLayer {
            this.alpha = alpha.floatValue
            compositingStrategy = CompositingStrategy.ModulateAlpha
        }
    } else imageModifier

    val image: @Composable () -> Unit = {
        AsyncImage(
            model = request,
            contentDescription = contentDescription,
            modifier = layered,
            placeholder = if (factory != null) null else placeholder.takeIf { it != 0 }?.let { painterResource(it) },
            error = error.takeIf { it != 0 }?.let { painterResource(it) },
            fallback = fallback.takeIf { it != 0 }?.let { painterResource(it) },
            alignment = alignment,
            contentScale = contentScale,
            colorFilter = colorFilter,
        )
    }

    if (factory == null) {
        image()
    } else {
        // The generated modifier (testTag included) on the outer box; the renderer and the
        // image inside it. One semantics node: the image's contentDescription on the tagged box.
        Box(modifier.semantics(mergeDescendants = true) {}, propagateMinConstraints = true) {
            Spacer(Modifier.matchParentSize().then(KjuiRendererElement(frame)))
            image()
        }
    }
}

/** null for no src: null, a blank String, or an ImageRequest whose data is one of those. */
internal fun noSrcAsNull(model: Any?): Any? = when (model) {
    is CharSequence -> model.takeIf { it.isNotBlank() }
    is ImageRequest -> when (val data = model.data) {
        is CharSequence -> if (data.isBlank()) model.newBuilder().data(null).build() else model
        else -> model
    }
    else -> model
}

private fun modelKey(model: Any?): String? = when (model) {
    null -> null
    is ImageRequest -> model.data.takeIf { it != coil3.request.NullRequestData }?.toString()
    else -> model.toString().takeIf { it.isNotEmpty() }
}

private val easeInOutCubic = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)
private fun easeOutQuad(t: Float) = 1f - (1f - t) * (1f - t)

/**
 * One NetworkImage frame: its share of the URL's load, its renderer, its fade. Main thread.
 */
internal class KjuiImageFrame(val key: String) : KjuiLoadingClock.Waiter, RememberObserver {
    var factory: ((KjuiLoadingHost) -> KjuiLoadingRendererInstance)? = null
    var rendererNamed = false
    var transitionMillis = 0
    var delayMillis = 200
    var isDark = false
    var reduceMotion = false
    var onState: (String) -> Unit = {}
    var onEvent: (KjuiImageLoadEvent) -> Unit = {}

    /** The image layer's opacity: 1 from the start (a memory hit draws at once). */
    val alpha: MutableFloatState = mutableFloatStateOf(1f)

    /** The URL's load, held from the frame's composition to its leaving (RememberObserver). */
    var load: KjuiImageLoad? = KjuiImageLoadTable.acquire(key); private set
    private var observation: AutoCloseable? = null
    private var unReduceMotion: (() -> Unit)? = null
    private var instance: KjuiLoadingRendererInstance? = null
    private var rendererDone = false
    private var end: KjuiLoadEnd? = null
    private var node: KjuiRendererNode? = null
    private var lastState: String? = null
    private var fade: Fade? = null
    private val main = Handler(Looper.getMainLooper())

    private class Fade(val start: Long, val delay: Long, val length: Long, val curve: (Float) -> Float)

    override fun onRemembered() {
        val l = load ?: return
        observation = l.observe { e ->
            onEvent(e)
            node?.wake()
        }
        unReduceMotion = KjuiLoadingClock.onReduceMotionChange { node?.wake() }
    }

    override fun onForgotten() = stop()

    override fun onAbandoned() = stop()

    fun stop() {
        observation?.close()
        unReduceMotion?.invoke()
        KjuiLoadingClock.cancel(this)
        releaseRenderer()
        load?.let { KjuiImageLoadTable.release(it) }
        load = null
    }

    private fun releaseRenderer() {
        instance?.release()
        instance = null
    }

    fun request(model: Any?, context: android.content.Context): ImageRequest {
        val builder = (model as? ImageRequest)?.newBuilder() ?: ImageRequest.Builder(context).data(model)
        load?.let { builder.extras[KjuiImageLoadKey] = it }
        return builder.listener(
            onStart = {
                // "loading" waits a turn: a memory hit's success arrives first and says "loaded" alone.
                main.post { if (end == null && lastState == null) state("loading") }
                // Without KjuiImageLoaderFactory no call reports itself: coil's start stands in.
                if (!KjuiImageLoadTable.factoryInstalled) {
                    main.post { if (end == null && load?.phase == KjuiImagePhase.IDLE) load?.requesting() }
                }
            },
            onCancel = { },
            onError = { _, result: ErrorResult -> ended(KjuiLoadEnd.Result.FAILED, null, result.throwable) },
            onSuccess = { _, result: SuccessResult -> ended(KjuiLoadEnd.Result.LOADED, source(result.dataSource), null) },
        ).build()
    }

    private fun source(ds: DataSource) = when (ds) {
        DataSource.MEMORY_CACHE, DataSource.MEMORY -> KjuiImageSource.MEMORY
        DataSource.DISK -> KjuiImageSource.DISK
        DataSource.NETWORK -> KjuiImageSource.NETWORK
    }

    private fun state(s: String) {
        if (s == lastState) return
        lastState = s
        onState(s)
    }

    /**
     * This frame's own success / error. coil calls its listener on a worker thread: the
     * image's opacity drops to 0 here, before the image can be drawn (snapshot state takes a
     * write from any thread), and the rest runs on the main thread.
     */
    private fun ended(result: KjuiLoadEnd.Result, source: KjuiImageSource?, error: Throwable?) {
        if (source != KjuiImageSource.MEMORY && transitionMillis > 0) alpha.floatValue = 0f
        if (Looper.myLooper() == Looper.getMainLooper()) endedOnMain(result, source, error)
        else main.post { endedOnMain(result, source, error) }
    }

    /** The fade starts now; the renderer is told now. */
    private fun endedOnMain(result: KjuiLoadEnd.Result, source: KjuiImageSource?, error: Throwable?) {
        val now = KjuiLoadingClock.now()
        val l = load
        if (result == KjuiLoadEnd.Result.LOADED) l?.finished(source!!) else l?.failed(error)
        // A read that ends before onStart's posted "loading" ran (a quick failure, or any
        // read on a loaded device, where the coroutine resumes in a frame callback ahead of
        // the post) still says "loading" first: only a memory hit says "loaded" alone.
        if (lastState == null && source != KjuiImageSource.MEMORY) state("loading")
        state(if (result == KjuiLoadEnd.Result.LOADED) "loaded" else "failed")
        end = KjuiLoadEnd(result, source, now)
        val t = transitionMillis.toLong()
        if (source == KjuiImageSource.MEMORY || t <= 0) {
            alpha.floatValue = 1f
            node?.wake()
            return
        }
        val attached = instance != null
        fade = when {
            !rendererNamed -> Fade(now, 0, t, ::easeOutQuad)
            !attached -> Fade(now, 0, t / 2, ::easeOutQuad)
            result == KjuiLoadEnd.Result.LOADED -> Fade(now, 0, t) { easeInOutCubic.transform(it) }
            else -> Fade(now, t / 6, t * 5 / 6) { easeInOutCubic.transform(it) }
        }
        alpha.floatValue = 0f
        KjuiLoadingClock.wakeNextFrame(this)
    }

    /** The clock: advance the fade, draw the renderer again. */
    override fun wake() {
        stepFade()
        node?.wake()
    }

    private fun stepFade() {
        val f = fade ?: return
        val now = KjuiLoadingClock.now()
        val p = ((now - f.start - f.delay).toFloat() / f.length.coerceAtLeast(1)).coerceIn(0f, 1f)
        alpha.floatValue = f.curve(p)
        if (p >= 1f) fade = null else KjuiLoadingClock.wakeNextFrame(this)
    }

    // ── the renderer node ──

    fun attach(n: KjuiRendererNode) {
        node = n
    }

    fun detach(n: KjuiRendererNode) {
        if (node === n) {
            node = null
            releaseRenderer()
        }
    }

    private val host = KjuiLoadingHost { main.post { node?.invalidateNow() } }

    fun drawRenderer(scope: ContentDrawScope) {
        val f = factory ?: return
        val l = load ?: return
        if (rendererDone) return
        val now = KjuiLoadingClock.now()
        if (instance == null) {
            if (end != null) return                     // done before the renderer was made
            val readyAt = l.startedAt + delayMillis
            if (now < readyAt) {
                KjuiLoadingClock.wakeAt(this, readyAt)
                return
            }
            if (!KjuiLoadingClock.tryStart()) {
                KjuiLoadingClock.wakeNextFrame(this)
                return
            }
            instance = f(host)
        }
        val inst = instance ?: return
        val frame = KjuiLoadingFrame(
            load = l,
            sinceStartMillis = now - l.startedAt,
            reduceMotion = reduceMotion,
            isDark = isDark,
            end = end,
            clock = { KjuiLoadingClock.now() },
        )
        val next = with(inst) { scope.draw(frame) }
        when (next) {
            KjuiNextFrame.Idle -> Unit
            is KjuiNextFrame.At -> KjuiLoadingClock.wakeAt(this, next.uptimeMillis)
            is KjuiNextFrame.Continuous -> {
                val fps = listOf(60, 30, 20, 15, 12, 10, 6, 5, 4, 3, 2, 1).first { it <= next.fps.coerceAtLeast(1) }
                KjuiLoadingClock.wakeAt(this, now + 1000L / fps - 2)
            }
            // Done before the frame ended reads as Idle: the load's next event draws again.
            KjuiNextFrame.Done -> if (end != null) {
                rendererDone = fade == null
                if (!rendererDone) KjuiLoadingClock.wakeNextFrame(this)
                else releaseRenderer()
            }
        }
    }
}

private class KjuiRendererElement(val frame: KjuiImageFrame) : ModifierNodeElement<KjuiRendererNode>() {
    override fun create() = KjuiRendererNode(frame)
    override fun update(node: KjuiRendererNode) {
        if (node.frame !== frame) {
            node.frame.detach(node)
            node.frame = frame
            frame.attach(node)
        }
    }
    override fun equals(other: Any?) = other is KjuiRendererElement && other.frame === frame
    override fun hashCode() = System.identityHashCode(frame)
    override fun InspectorInfo.inspectableProperties() { name = "kjuiLoadingRenderer" }
}

/** Draws the frame's renderer; no semantics (screen readers read the image's alt). */
internal class KjuiRendererNode(var frame: KjuiImageFrame) : Modifier.Node(), DrawModifierNode {
    override fun onAttach() = frame.attach(this)
    override fun onDetach() = frame.detach(this)

    fun wake() {
        if (isAttached) invalidateDraw()
    }

    fun invalidateNow() = wake()

    override fun ContentDrawScope.draw() {
        drawContent()
        frame.drawRenderer(this)
    }
}
