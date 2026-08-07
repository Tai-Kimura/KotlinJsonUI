package com.kotlinjsonui.conformance

import android.app.Application
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import okhttp3.OkHttpClient
import java.io.IOException

/**
 * One deterministic network for both host modes.
 *
 * The NetworkImage error fixtures point at `https://conformance.invalid/…` —
 * a host that never resolves, so the interesting state is the FAILURE. How
 * fast that failure arrives is an environment property: a local resolver
 * answers NXDOMAIN within the settle window, the hosted-CI emulator's
 * resolver does not, so the screenshot catches the dynamic render still in
 * Coil's loading state — a blank view where the error image should be. Run 5
 * measured exactly that: `NetworkImage_errorImage__static` inert against its
 * control on android while ios drew it, and a parity distance of 34 against
 * the codegen host, whose own request happened to fail in time. Same layout,
 * same library, different DNS latency.
 *
 * So `.invalid` is failed HERE, synchronously, before any resolver is asked.
 * RFC 2606 reserves the TLD for precisely this — a name guaranteed never to
 * exist — which makes the interceptor a statement of fact, not a mock: the
 * request could only ever fail, this just removes the environment from
 * deciding WHEN. An OkHttp application interceptor runs before DNS, so no
 * lookup happens at all.
 *
 * Both Coil generations get the client because the two render paths ride
 * different ones: the dynamic renderer (library-dynamic) is coil2, the
 * kjui-generated views are coil3. The codegen host is this same module built
 * with HOST_MODE=codegen — one Application covers both hosts, which is the
 * point: a fixture must not be able to render differently because the two
 * pipelines' networks disagreed.
 */
class ConformanceHostApp :
    Application(),
    coil.ImageLoaderFactory,
    coil3.SingletonImageLoader.Factory {

    private fun failFastClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val host = chain.request().url.host
                // `pending.invalid` MUST be matched before the `.invalid`
                // catch-all (INTERACTIVE_HOST_CONTRACT.md §5.2): it never
                // completes and never fails, so the in-flight state stops
                // being a moment and becomes the resting state of the run.
                // Still under `.invalid` (RFC 2606) so it cannot leave the
                // machine either way.
                if (host == "pending.invalid") {
                    try {
                        Thread.sleep(Long.MAX_VALUE)
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                    }
                    throw IOException("conformance: pending.invalid stall interrupted")
                }
                if (host == "conformance.invalid" || host.endsWith(".invalid")) {
                    throw IOException(
                        "conformance: '.invalid' never resolves (RFC 2606) — failing " +
                            "synchronously so the error state is deterministic"
                    )
                }
                chain.proceed(chain.request())
            }
            .build()

    /** coil2 — the dynamic renderer's stack. */
    override fun newImageLoader(): coil.ImageLoader =
        coil.ImageLoader.Builder(this)
            .okHttpClient(failFastClient())
            .build()

    /** coil3 — the kjui-generated views' stack. */
    override fun newImageLoader(context: coil3.PlatformContext): coil3.ImageLoader =
        coil3.ImageLoader.Builder(context)
            .components {
                add(OkHttpNetworkFetcherFactory(callFactory = { failFastClient() }))
            }
            .build()
}
