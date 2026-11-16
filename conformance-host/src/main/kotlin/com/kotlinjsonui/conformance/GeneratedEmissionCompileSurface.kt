package com.kotlinjsonui.conformance

import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest

/**
 * The compile surface a consumer inherits from the library, pinned here
 * because nothing else compiles it.
 *
 * kjui codegen emits `coil3.network.NetworkHeaders` + `httpHeaders` when a
 * NetworkImage declares `headers` (kjui_tools import_manager.rb), and
 * `kjui init` writes no coil dependency of its own — so a consumer's compile
 * classpath gets coil3 only through this library's `api` dependencies. No
 * existing gate covers that path: conformance deliberately excludes
 * NetworkImage#headers ("network resource (v1 fixtures are offline)"), so
 * there is no fixture, and the emitted-kotlin gate compiles against
 * stdlib/coroutines only, with no Android and no coil on the classpath.
 *
 * This module declares NO coil dependency (see build.gradle.kts), which is
 * what makes compiling this file a measurement rather than a restatement:
 * demote the library's `api("coil3:coil-compose")` /
 * `api("coil3:coil-network-okhttp")` to `implementation` and this stops
 * compiling. It is deliberately unused at runtime — the assertion is that it
 * COMPILES.
 */
@Suppress("unused")
internal fun generatedHeadersEmissionCompiles(context: coil3.PlatformContext): ImageRequest =
    ImageRequest.Builder(context)
        .data("https://example.invalid/image.png")
        .httpHeaders(NetworkHeaders.Builder().set("Authorization", "Bearer token").build())
        .build()
