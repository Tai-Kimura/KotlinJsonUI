package com.kotlinjsonui.core

import java.util.concurrent.atomic.AtomicInteger

/**
 * Whether the WebViews this library puts on screen have finished loading.
 *
 * 🔻 WHY THIS EXISTS. A `WebView` is in the hierarchy the instant it is made,
 * so a conformance host whose only gate before a screenshot is "the fixture's
 * screen is on" captures whatever the page happens to have painted by then.
 * The capture time is not controlled — and BOTH SIDES of that race have been
 * seen in committed baselines on the iOS face: one bake caught
 * `Web/html__static` blank, an earlier one caught its control blank and hashed
 * all zeroes.
 *
 * ⚠️ AND `control_diff` IS GREEN THROUGH IT. A race satisfies "the fixture
 * differs from its control" for the wrong reason, so a green control-diff job
 * says nothing about whether the page painted.
 *
 * 🔻 WHY IT LIVES IN `library` AND NOT `library-dynamic`. It was in the
 * dynamic module first, and the codegen face was therefore never wired: the
 * generated WebView emits its own client, in the consumer's module, where an
 * `internal` member of another module cannot be reached at all. Measured
 * 2026-09-16 on conformance-mobile run 34987243780 — the dynamic leg reported
 * `markerAbsent=0, waitedThenSettled=2` and the codegen leg `markerAbsent=2`
 * in the same run, on the same emulator image. A signal that only one of the
 * two rendering paths can report to is not a signal about the library.
 *
 * 🔻 NOT AN ACCESSIBILITY MARKER, unlike the iOS side. The android
 * conformance host already reads in-process signals (`FixtureHost.renderedIds`
 * / `presentedIds`) precisely because they are a11y-independent, and a Compose
 * `testTag` on a wrapper merges semantics onto the content underneath it —
 * which is the exact failure the iOS host hit and wrote down. The host and the
 * app under test share a process here, so a counter is both simpler and safer.
 *
 * ⚠️ OFF UNLESS ASKED. A consumer's own instrumentation must not see its
 * counters move, so nothing is recorded until a host sets [enabled]. The
 * generated code that reports here ships in release builds — [KjuiWebViewClient]
 * is a library-provided `WebViewClient`, not a test hook, and with [enabled]
 * false it does exactly what `WebViewClient()` did.
 */
object WebLoadSignal {
    /** Set by a conformance host before the run. Off in production. */
    @Volatile
    @JvmStatic
    var enabled: Boolean = false

    private val started = AtomicInteger(0)
    private val finished = AtomicInteger(0)

    /** Loads begun since the last [reset]. */
    @JvmStatic
    val startedCount: Int get() = started.get()

    /** Loads that reached `onPageFinished` since the last [reset]. */
    @JvmStatic
    val finishedCount: Int get() = finished.get()

    /** Called by the host between fixtures, so each fixture is judged alone. */
    @JvmStatic
    fun reset() {
        started.set(0)
        finished.set(0)
    }

    /**
     * Public, unlike the dynamic-only predecessor's `internal`: the codegen
     * path's caller is generated code in the CONSUMER's module, so anything
     * less than public cannot be reached from the face this exists to cover.
     */
    @JvmStatic
    fun onPageStarted() {
        if (enabled) started.incrementAndGet()
    }

    @JvmStatic
    fun onPageFinished() {
        if (enabled) finished.incrementAndGet()
    }
}
