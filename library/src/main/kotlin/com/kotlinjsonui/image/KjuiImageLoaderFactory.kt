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
import kotlinx.coroutines.delay
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
import java.net.UnknownHostException
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
 * requesting / finished / failed (from coil's request listener), never progress, and a failed
 * read is not read again: the retries after a passing failure (5xx, 408, 429, a cut connection;
 * 2 s and 4 s, `Retrying(n)` before each wait) are this factory's NetworkClient's.
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
        // Only the image's own response (jsonui-cli ticket
        // jui-networkimage-error-response-reported-as-progress): a redirect's head is not it,
        // and a 4xx / 5xx brings an error page. The renderer hears requesting, then failed.
        if (!response.isTheImage()) return
        val length = response.header("Content-Length")?.toLongOrNull()
        call.load()?.responded(length, KjuiImageLoadTable.clock())
    }
}

/**
 * The response is the image: a 2xx, or a 304 answering a conditional request (the cached
 * copy is the image). A 304 nobody asked for is not (coil fails it).
 */
internal fun Response.isTheImage(): Boolean =
    code in 200..299 ||
        (code == 304 && (request.header("If-None-Match") != null || request.header("If-Modified-Since") != null))

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

        val call = builder.build()
        var attempt = 0
        var response: Response
        while (true) {
            // A passing failure — 5xx, 408, 429, a cut connection — is read again after 2 s and
            // after 4 s, the load told `retrying(n)` before each wait; a 4xx but 408 and 429 is the
            // request's own fault and fails at once (the requester's ruling 2026-11-15, ticket
            // jui-networkimage-retry-rules-differ-by-os), as SwiftJsonUI's NetworkImage does.
            val outcome = try {
                client.newCall(call).await()
            } catch (e: IOException) {
                if (e is UnknownHostException || attempt >= MAX_RETRIES) throw e
                null
            }
            if (outcome != null) {
                // A 304 nobody asked for has no image and no body: it fails, and is not read again
                // (coil would ask once more without its cache).
                if (outcome.code == 304 && !outcome.isTheImage()) {
                    outcome.close()
                    throw IOException("HTTP 304 to a request that was not conditional")
                }
                if (!outcome.isPassingFailure() || attempt >= MAX_RETRIES) { response = outcome; break }
                outcome.close()
            }
            attempt += 1
            load?.retrying(attempt)
            delay(attempt * retryStepMillis)
        }
        return response.use { r ->
            val headers = NetworkHeaders.Builder().apply { r.headers.forEach { (n, v) -> add(n, v) } }.build()
            // coil reads a 4xx's body too (an error page): it is not the image's bytes.
            val counted = if (r.isTheImage()) load else null
            val source = object : ForwardingSource(r.body.source()) {
                override fun read(sink: Buffer, byteCount: Long): Long {
                    val n = super.read(sink, byteCount)
                    if (n > 0) counted?.bytes(n, KjuiImageLoadTable.clock())
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

/** Retries after a passing failure (iOS's maxRetries). */
internal const val MAX_RETRIES = 2

/** The wait before retry n is n × this (2 s, as on iOS); tests shorten it. */
@Volatile internal var retryStepMillis: Long = 2_000L

/** A failure another read may not meet: 5xx, 408 (timeout), 429 (too many requests). */
internal fun Response.isPassingFailure(): Boolean = code in 500..599 || code == 408 || code == 429

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
