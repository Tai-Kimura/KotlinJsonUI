package com.kotlinjsonui.core

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp

/** Prefix every screen marker test tag carries. */
const val SCREEN_MARKER_PREFIX = "__screen_"

/** Marker test tag for a screen id. */
fun screenMarkerTag(screenId: String): String = "$SCREEN_MARKER_PREFIX$screenId"

/**
 * Runtime identity beacon for a screen, so a test can assert that the screen
 * is displayed without knowing anything about its contents.
 *
 * Emitted by `kjui build` for SCREEN layouts only — cells, partials and
 * responsive variant composables must not carry a marker — as a sibling
 * inside the transparent `Box` that wraps both rendering branches, so the
 * marker covers static AND dynamic mode and costs no layout.
 *
 * **Debug builds only.** The marker is test scaffolding and has no place in
 * a shipped app. The gate is a RUNTIME check of the host application's
 * debuggable flag, not a compile-time one: this library ships as an AAR, so
 * its own `BuildConfig.DEBUG` describes how the library was built, not the
 * app — the same reason `DynamicModeManager` resolves availability from
 * `ApplicationInfo.FLAG_DEBUGGABLE`. UI tests therefore run against a debug
 * build, which is already true for Dynamic mode.
 *
 * The 1.dp size is not cosmetic: measured on API 35, a zero-size node is
 * absent from the accessibility tree and unfindable by resource id, as is a
 * sized child placed outside a zero-size parent. Inside a `Box` the marker
 * overlays its sibling content, so the footprint costs nothing.
 */
@Composable
fun ScreenMarker(screenId: String) {
    val context = LocalContext.current
    val debuggable = remember(context) {
        (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
    if (!debuggable) return

    Box(
        Modifier
            .size(1.dp)
            .testTag(screenMarkerTag(screenId))
            // The marker must be addressable by resource id even when the
            // screen's own tree does not opt in anywhere above it.
            .semantics { testTagsAsResourceId = true }
    )
}
