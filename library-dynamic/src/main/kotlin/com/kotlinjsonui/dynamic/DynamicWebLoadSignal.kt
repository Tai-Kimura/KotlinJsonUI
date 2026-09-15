package com.kotlinjsonui.dynamic

import java.util.concurrent.atomic.AtomicInteger

/**
 * Whether the WebViews this library creates have finished loading.
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
 * says nothing about whether the page painted. That inference was made twice
 * about the android face on 2026-09-15 and is wrong both times; the note in
 * SwiftJsonUI's `WebView.swift` had already recorded it.
 *
 * 🔻 NOT AN ACCESSIBILITY MARKER, unlike the iOS side. The android
 * conformance host already reads in-process signals (`FixtureHost.renderedIds`
 * / `presentedIds`) precisely because they are a11y-independent, and a Compose
 * `testTag` on a wrapper merges semantics onto the content underneath it —
 * which is the exact failure the iOS host hit and wrote down. The host and the
 * app under test share a process here, so a counter is both simpler and safer.
 *
 * ⚠️ OFF UNLESS ASKED. A consumer's own instrumentation must not see its
 * counters move, so nothing is recorded until a host sets [enabled].
 */
object DynamicWebLoadSignal {
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

    internal fun onPageStarted() {
        if (enabled) started.incrementAndGet()
    }

    internal fun onPageFinished() {
        if (enabled) finished.incrementAndGet()
    }
}
