package com.kotlinjsonui.image

import android.content.Context
import coil3.Extras
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.network.DeDupeConcurrentRequestStrategy
import coil3.network.NetworkClient
import coil3.network.NetworkFetcher
import coil3.network.NetworkHeaders
import coil3.network.NetworkRequest
import coil3.network.NetworkResponse
import coil3.network.NetworkResponseBody
import coil3.network.CacheStrategy
import coil3.network.ConnectivityChecker
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Dispatcher
import okhttp3.EventListener
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import okio.ForwardingSource
import okio.buffer
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** The ImageRequest extra a frame puts its [KjuiImageLoad] in; the NetworkClient tags the call with it. */
internal val KjuiImageLoadKey: Extras.Key<KjuiImageLoad?> = Extras.Key(default = null)

/**
 * The app's coil ImageLoader with the read's progress reported (ticket
 * jui-networkimage-load-listener): one OkHttpClient with an EventListener, a NetworkClient
 * that tags each call with its [KjuiImageLoad] and counts the body's bytes, and
 * DeDupeConcurrentRequestStrategy (one URL is read once; the frames drawing it share the
 * load). coil's own memory and disk caches.
 *
 * Install once, in Application.onCreate:
 * ```
 * SingletonImageLoader.setSafe(KjuiImageLoaderFactory())
 * ```
 * Without it NetworkImage still works and a renderer still draws; it is told only
 * requesting / finished / failed (from coil's request listener), never progress.
 *
 * @param baseClient the OkHttpClient to build from (its interceptors, timeouts and pool are
 *   kept; the event listener is replaced).
 * @param onLoadEvent every image's events, with its URL, on the main thread (progress thinned).
 * @param configure anything else for the ImageLoader (its caches, its components).
 */
class KjuiImageLoaderFactory(
    private val baseClient: OkHttpClient? = null,
    private val onLoadEvent: ((url: String, event: KjuiImageLoadEvent) -> Unit)? = null,
    private val configure: ImageLoader.Builder.() -> Unit = {},
) : SingletonImageLoader.Factory {

    @OptIn(ExperimentalCoilApi::class)
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        onLoadEvent?.let { KjuiImageLoadTable.addObserver(it) }
        KjuiImageLoadTable.factoryInstalled = true
        val client = buildClient(baseClient)
        return ImageLoader.Builder(context)
            .components {
                add(
                    NetworkFetcher.Factory(
                        networkClient = { KjuiNetworkClient(client) },
                        cacheStrategy = { CacheStrategy.DEFAULT },
                        connectivityChecker = { ctx: Context -> ConnectivityChecker(ctx) },
                        concurrentRequestStrategy = { DeDupeConcurrentRequestStrategy() },
                    )
                )
            }
            .apply(configure)
            .build()
    }

    companion object {
        /** The OkHttpClient the factory reads with: `base` (or a new one) with the load's EventListener. */
        fun buildClient(base: OkHttpClient? = null): OkHttpClient =
            (base?.newBuilder() ?: OkHttpClient.Builder())
                .eventListenerFactory { KjuiImageEventListener }
                .build()
    }
}

/**
 * Reads a call's [KjuiImageLoad] from its tag: queued while OkHttp's Dispatcher holds it,
 * requesting when it starts, responded at the response's head.
 */
internal object KjuiImageEventListener : EventListener() {
    private fun Call.load(): KjuiImageLoad? = request().tag(KjuiImageLoad::class.java)

    override fun dispatcherQueueStart(call: Call, dispatcher: Dispatcher) {
        call.load()?.queued()
    }

    override fun callStart(call: Call) {
        call.load()?.requesting()
    }

    override fun responseHeadersEnd(call: Call, response: Response) {
        val length = response.header("Content-Length")?.toLongOrNull()
        call.load()?.responded(length, KjuiImageLoadTable.clock())
    }
}

/**
 * coil's NetworkClient over OkHttp, as coil-network-okhttp's own, plus: the call carries
 * its [KjuiImageLoad] as a tag (coil's toRequest sets none, so an EventListener could not
 * tell whose call it was), the body's bytes are counted as they are read, and a cancelled
 * coroutine cancels the call. Enqueued, not executed, so OkHttp's Dispatcher (5 calls per
 * host) holds the order and reports the queue.
 */
internal class KjuiNetworkClient(private val client: Call.Factory) : NetworkClient {
    override suspend fun <T> executeRequest(
        request: NetworkRequest,
        block: suspend (response: NetworkResponse) -> T,
    ): T {
        val load = request.extras[KjuiImageLoadKey]
        val builder = Request.Builder().url(request.url)
        request.headers.asMap().forEach { (name, values) -> values.forEach { builder.addHeader(name, it) } }
        val body = request.body?.let { b -> Buffer().also { b.writeTo(it) }.readByteString().toRequestBody() }
        builder.method(request.method, body)
        if (load != null) builder.tag(KjuiImageLoad::class.java, load)

        val response = client.newCall(builder.build()).await()
        return response.use { r ->
            val headers = NetworkHeaders.Builder().apply { r.headers.forEach { (n, v) -> add(n, v) } }.build()
            val source = object : ForwardingSource(r.body.source()) {
                override fun read(sink: Buffer, byteCount: Long): Long {
                    val n = super.read(sink, byteCount)
                    if (n > 0) load?.bytes(n, KjuiImageLoadTable.clock())
                    return n
                }
            }.buffer()
            block(
                NetworkResponse(
                    code = r.code,
                    requestMillis = r.sentRequestAtMillis,
                    responseMillis = r.receivedResponseAtMillis,
                    headers = headers,
                    body = NetworkResponseBody(source),
                    delegate = r,
                )
            )
        }
    }
}

private suspend fun Call.await(): Response = suspendCancellableCoroutine { cont ->
    cont.invokeOnCancellation { cancel() }
    enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            cont.resume(response) { _, _, _ -> response.close() }
        }

        override fun onFailure(call: Call, e: IOException) {
            if (!cont.isCancelled) cont.resumeWithException(e)
        }
    })
}
