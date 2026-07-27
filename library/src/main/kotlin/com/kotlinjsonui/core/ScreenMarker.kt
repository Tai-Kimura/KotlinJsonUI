package com.kotlinjsonui.core

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
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
 *
 * The system-bar padding is not cosmetic either. With edge-to-edge (targetSdk
 * 35+) a generated screen's root sits at the window origin, which is underneath
 * the status bar. The accessibility framework subtracts the region covered by
 * windows above the app, so a node lying ENTIRELY inside that region reports
 * `isVisibleToUser == false` and UiAutomator's `By.res` — the driver's
 * predicate for "this screen is displayed" — does not return it, even though
 * the node is present in the tree. Measured on API 35 (tablet AVD, 320dpi):
 * the status bar WINDOW is `Rect(0, 0 - 2560, 48)` and the boundary is exactly
 * there — a 2px marker at y=46 is invisible, at y=47 it crosses the edge and
 * becomes visible. That is a placement bug, not a predicate one: nothing is
 * claiming to be a different screen, the screen is plainly on display.
 *
 * Sizing the marker large enough to reach past the bar is NOT the fix: the bar
 * differs per device, and a 48.dp marker that clears it on the tablet AVD is
 * still entirely inside it on the phone AVD.
 *
 * The placement uses PADDING on an outer node rather than `Modifier.offset`,
 * and the tagged node's own modifier chain is left exactly as it was. Measured
 * on API 35: every `offset`-based form — before the tag, after the semantics,
 * on an outer node, even with the opt-in moved to that outer node — stops the
 * marker being exposed as a resource id at all when an ancestor sets
 * `testTagsAsResourceId = false`, which is the case this self-opt-in exists
 * for. Padding both places the node and keeps it addressable.
 *
 * The cost is that the marker's outer node measures (startInset + 1.dp) x
 * (topInset + 1.dp) instead of 1.dp. Inside the generated `Box` that is free —
 * a `Box` sizes to its largest child and a screen's content is far taller than
 * a status bar — unless a screen's whole content is shorter than the status
 * bar, which no real screen is.
 */
@Composable
fun ScreenMarker(screenId: String) {
    val context = LocalContext.current
    val debuggable = remember(context) {
        (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
    if (!debuggable) return

    val density = LocalDensity.current
    val insets = WindowInsets.systemBars
    val layoutDirection = LocalLayoutDirection.current
    val topPadding = with(density) { insets.getTop(density).toDp() }
    val startPadding = with(density) { insets.getLeft(density, layoutDirection).toDp() }

    Box(Modifier.padding(start = startPadding, top = topPadding)) {
        Box(
            Modifier
                .size(1.dp)
                .testTag(screenMarkerTag(screenId))
                // The marker must be addressable by resource id even when the
                // screen's own tree does not opt in anywhere above it.
                .semantics { testTagsAsResourceId = true }
        )
    }
}
