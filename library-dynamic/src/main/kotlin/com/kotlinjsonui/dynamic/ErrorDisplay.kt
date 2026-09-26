package com.kotlinjsonui.dynamic

import android.content.Context
import com.kotlinjsonui.core.Configuration

/**
 * What DynamicView draws where it cannot draw a node — an unknown type, a
 * node with no `type` (4f's ruling R, the rule SwiftJsonUI Dynamic follows):
 * the error view ([Configuration.showErrorsInDebug]) only in a debuggable app
 * ([DebugDiagnostics], ApplicationInfo.FLAG_DEBUGGABLE — the library's own
 * BuildConfig.DEBUG is false in the published aar); otherwise the app's
 * [Configuration.fallbackComponent], or nothing. The flag alone decided it,
 * so a release app drew the error view. Naming the node is not this: it is
 * always said (onError, Log.w).
 */
internal object ErrorDisplay {
    enum class Draw { ERROR_VIEW, FALLBACK, NOTHING }

    fun showsErrors(context: Context?): Boolean =
        Configuration.showErrorsInDebug && DebugDiagnostics.isAppDebuggable(context)

    fun forUnknownType(context: Context?): Draw = when {
        showsErrors(context) -> Draw.ERROR_VIEW
        Configuration.fallbackComponent != null -> Draw.FALLBACK
        else -> Draw.NOTHING
    }
}
