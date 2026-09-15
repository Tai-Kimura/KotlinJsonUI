package com.kotlinjsonui.dynamic

import com.kotlinjsonui.core.WebLoadSignal

/**
 * Compatibility face of [WebLoadSignal], which now lives in `library`.
 *
 * 🔻 WHY IT MOVED. This object could only ever be reported to from the dynamic
 * module, so the codegen face — whose WebView is emitted into the CONSUMER's
 * module by `kjui build` — had no way to reach it, and was never wired.
 * Measured 2026-09-16 (conformance-mobile run 34987243780): in one run, on one
 * emulator image, the dynamic leg reported `markerAbsent=0` and the codegen leg
 * `markerAbsent=2`.
 *
 * Every member below forwards, so a host that sets `enabled` here turns the
 * signal on for BOTH paths. New code should use [WebLoadSignal] directly.
 */
@Deprecated(
    "Use com.kotlinjsonui.core.WebLoadSignal — it covers the codegen path too",
    ReplaceWith("WebLoadSignal", "com.kotlinjsonui.core.WebLoadSignal")
)
object DynamicWebLoadSignal {
    @JvmStatic
    var enabled: Boolean
        get() = WebLoadSignal.enabled
        set(value) { WebLoadSignal.enabled = value }

    @JvmStatic
    val startedCount: Int get() = WebLoadSignal.startedCount

    @JvmStatic
    val finishedCount: Int get() = WebLoadSignal.finishedCount

    @JvmStatic
    fun reset() = WebLoadSignal.reset()
}
