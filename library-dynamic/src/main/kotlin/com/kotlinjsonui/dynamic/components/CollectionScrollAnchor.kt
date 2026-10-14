package com.kotlinjsonui.dynamic.components

import android.util.Log
import com.kotlinjsonui.dynamic.DebugDiagnostics

/**
 * Where a scrollTo target lands, from a BOUND scrollAnchor
 * (`"scrollAnchor": "@{prop}"`, jsonui-cli 1.9.29,
 * jui-collection-scrollto-per-request-anchor): the Collection resolves it from
 * the data of the composition that changed the scrollTo value, so the anchor a
 * request lands by is the bound value at that moment, and a change of the
 * anchor alone scrolls nothing (the ScrollToEffect keys on the scrollTo value
 * only). Top / center / bottom; anything else lands as bottom, and a
 * debuggable app hears it once per (Collection, value) — the rule SwiftJsonUI's
 * CollectionScrollAnchor, kjui's generated code and rjui follow.
 */
internal object CollectionScrollAnchor {
    private const val TAG = "JsonUI"

    /** Test hook: receives every emitted warning message. */
    var warningSink: ((String) -> Unit)? = null

    private val warned = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

    /** Test hook: forget which pairs were named. */
    fun resetForTest() = warned.clear()

    fun resolve(value: String?, collection: String?, context: android.content.Context?): String {
        if (value == "top" || value == "center" || value == "bottom") return value
        val name = collection ?: "(unnamed)"
        if (DebugDiagnostics.isAppDebuggable(context) && warned.add("$name:$value")) {
            val message = "Collection $name: scrollAnchor \"$value\" is not top, center or bottom — lands as bottom"
            warningSink?.invoke(message)
            Log.w(TAG, message)
        }
        return "bottom"
    }
}
