package com.kotlinjsonui.conformance

import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos

/**
 * The one change a fixture's manifest state may ask the host to make after
 * the fixture's first draw (jsonui-cli INTERACTIVE_HOST_CONTRACT.md
 * "afterFirstDraw", 2026-10-14). A scrollTo request is a CHANGE of the bound
 * value — the value a list is drawn with scrolls nowhere — so the
 * scrollAnchor fixtures photographed a list nothing had asked to scroll until
 * the host made one (conformance-collection-scrollanchor-family-does-not-
 * discriminate).
 *
 * Both faces use it: the dynamic path through the state's DataBindingContext
 * ([rememberConformanceData]), the codegen path through each generated
 * ViewModel's `updateData` (scripts/generate_codegen_host.rb). It fires once,
 * after the first frame has been drawn, and stamps two times for the suite,
 * in ms after that frame: when the change was made, and when the first frame
 * drawn WITH it was drawn. The instrumentation shares this process, so
 * ConformanceSuiteTest reads [AfterFirstDrawStamp] directly, waits for
 * `appliedAtMs`, and only then reads where the picture comes to rest.
 */
object AfterFirstDrawStamp {
    @Volatile var fixtureId: String? = null
        private set
    @Volatile var firedAtMs: Long? = null
        private set
    @Volatile var appliedAtMs: Long? = null
        private set
    @Volatile private var firstFrame: Long = 0L

    @Synchronized
    fun reset(fixtureId: String) {
        this.fixtureId = fixtureId
        firedAtMs = null
        appliedAtMs = null
        firstFrame = 0L
    }

    @Synchronized
    fun markFired(fixtureId: String) {
        if (this.fixtureId != fixtureId || firedAtMs != null) return
        firstFrame = SystemClock.uptimeMillis()
        firedAtMs = 0L
    }

    @Synchronized
    fun markApplied(fixtureId: String) {
        if (this.fixtureId != fixtureId || firedAtMs == null || appliedAtMs != null) return
        appliedAtMs = SystemClock.uptimeMillis() - firstFrame
    }
}

/** Two frame callbacks: the first is the frame being produced, the second
 *  comes after it was drawn — "after the draw", not "after composition". */
private suspend fun afterNextDrawnFrame() {
    withFrameNanos { }
    withFrameNanos { }
}

/**
 * Fires [fire] once after the first drawn frame and stamps it; stamps the
 * applied time once a frame drawn while [applied] holds has been drawn.
 */
@Composable
fun ConformanceAfterFirstDraw(fixtureId: String, applied: Boolean, fire: () -> Unit) {
    LaunchedEffect(fixtureId) {
        AfterFirstDrawStamp.reset(fixtureId)
        afterNextDrawnFrame()
        AfterFirstDrawStamp.markFired(fixtureId)
        fire()
    }
    LaunchedEffect(fixtureId, applied) {
        if (!applied) return@LaunchedEffect
        afterNextDrawnFrame()
        AfterFirstDrawStamp.markApplied(fixtureId)
    }
}
