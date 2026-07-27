package com.kotlinjsonui.conformance

import android.content.Intent
import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Screen-marker measurement (screen-identity track, Phase 0) — NOT part of the
 * conformance suite (`run_conformance.sh` filters to ConformanceSuiteTest) and
 * skipped unless explicitly requested:
 *
 *   adb shell am instrument -w \
 *     -e class com.kotlinjsonui.conformance.ScreenMarkerProbeTest \
 *     -e screenMarkerProbe 1 \
 *     com.kotlinjsonui.conformance.test/androidx.test.runner.AndroidJUnitRunner
 *
 * These tests answer, by measurement rather than by reading Compose's docs:
 *
 * - does a zero-size marker node survive into the accessibility tree, or does
 *   it need a minimum footprint?
 * - do `mergeDescendants` / `clearAndSetSemantics` on an ancestor erase it?
 * - for a covered screen (dialog / modal bottom sheet / popped destination),
 *   what does an all-window `By.res` search report, what does a
 *   `rootInActiveWindow`-scoped search report, and what are the visibleBounds?
 *
 * Only the shape checks assert. The predicate work RECORDS both search modes
 * so the canonical Android predicate is chosen from data.
 */
@RunWith(AndroidJUnit4::class)
class ScreenMarkerProbeTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val device: UiDevice = UiDevice.getInstance(instrumentation)

    @Before
    fun skipUnlessRequested() {
        val enabled = InstrumentationRegistry.getArguments().getString("screenMarkerProbe") == "1"
        Assume.assumeTrue("set -e screenMarkerProbe 1 to run the screen-marker probe", enabled)
    }

    // MARK: - Measurement helpers

    private fun marker(screenId: String) = "__screen_$screenId"

    /** All-window search — what the driver does today (`device.findObject`). */
    private fun foundAnyWindow(id: String): Int = device.findObjects(By.res(id)).size

    /** Same search, but scoped to the window that currently has a11y focus. */
    private fun foundActiveWindow(id: String): Boolean {
        val root = instrumentation.uiAutomation.rootInActiveWindow ?: return false
        return findInTree(root, id) != null
    }

    private fun findInTree(node: AccessibilityNodeInfo?, id: String): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.viewIdResourceName == id) return node
        for (i in 0 until node.childCount) {
            findInTree(node.getChild(i), id)?.let { return it }
        }
        return null
    }

    /** exists / bounds / visibility, as one printable row. */
    private fun snapshot(label: String, id: String): String {
        val anyWindow = foundAnyWindow(id)
        val activeWindow = foundActiveWindow(id)
        val obj = device.findObject(By.res(id))
        val bounds: Rect? = obj?.visibleBounds
        val boundsText = when {
            obj == null -> "-"
            bounds == null -> "null"
            bounds.isEmpty -> "EMPTY"
            else -> "${bounds.width()}x${bounds.height()}@(${bounds.left},${bounds.top})"
        }
        // The driver's own primitive is `findObject` + null check, so record
        // it separately from `findObjects`: the two disagree for a node that
        // is in the tree but scrolled out of the viewport.
        val node = instrumentation.uiAutomation.rootInActiveWindow?.let { findInTree(it, id) }
        val nodeText = if (node == null) "-" else {
            val r = Rect().also { node.getBoundsInScreen(it) }
            "visibleToUser=${node.isVisibleToUser} bounds=${r.width()}x${r.height()}@(${r.left},${r.top})"
        }
        return "$label: findObjects=$anyWindow findObject=${obj != null} inTree=$activeWindow " +
            "visibleBounds=$boundsText node[$nodeText] " +
            "windows=${instrumentation.uiAutomation.windows.size}"
    }

    private fun launch(mode: String): ActivityScenario<ScreenMarkerProbeActivity> {
        ProbeMode.currentMode = mode
        val intent = Intent(instrumentation.targetContext, ScreenMarkerProbeActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .putExtra(ScreenMarkerProbeActivity.EXTRA_PROBE, mode)
        return ActivityScenario.launch(intent)
    }

    private fun awaitMarker(screenId: String, timeoutMs: Long = 10_000): Boolean =
        device.wait(Until.hasObject(By.res(marker(screenId))), timeoutMs) != null

    // MARK: - Shape: does the marker survive, and what erases it?

    @Test
    fun markerShapeSurvivesOrNot() {
        launch("nav").use {
            // Wait on the min-size marker: if the zero-size one turns out to be
            // invisible, waiting on it would hang the whole probe.
            check(awaitMarker("minsize")) { "min-size marker never appeared" }

            println("[screen-marker] SHAPE")
            println("  " + snapshot("zeroSize(marker)", marker("zerosize")))
            println("  " + snapshot("minSize(marker)", marker("minsize")))
            println("  " + snapshot("zeroSize(root id)", "zerosize_root_view"))
            println("  " + snapshot("zeroSize(child id)", "zerosize_child_0"))
            println("  " + snapshot("minSize(root id)", "minsize_root_view"))
            println("  " + snapshot("minSize(child id)", "minsize_child_0"))
            println("  " + snapshot("zeroLayout(marker)", marker("zerolayout")))
            println("  " + snapshot("zeroLayout(sibling id)", "zerolayout_child_0"))
            println("  " + snapshot("library ScreenMarker()", marker("library_shape")))
            println("  " + snapshot("library(sibling id)", "library_child_0"))
            println("  " + snapshot("library PRE-inset-offset", marker("library_shape_old")))
            println("  " + snapshot("library old(sibling id)", "library_old_child_0"))
            println("  " + snapshot("offset BEFORE tag", marker("order_a")))
            println("  " + snapshot("offset AFTER semantics", marker("order_b")))
            println("  " + snapshot("offset on OUTER node", marker("order_c")))
            println("  " + snapshot("nested PADDING", marker("order_d")))
            println("  " + snapshot("outer opt-in + offset", marker("order_e")))

            println("[screen-marker] ANCESTOR SEMANTICS")
            println("  " + snapshot("mergeDescendants(zeroSize)", marker("merged_host")))
            println("  " + snapshot("mergeDescendants(minSize)", marker("merged_host_min")))
            println("  " + snapshot("clearAndSetSemantics(zeroSize)", marker("cleared_host")))
            println("  " + snapshot("clearAndSetSemantics(minSize)", marker("cleared_host_min")))

            // The one invariant the canon already commits to: whatever shape
            // wins, marking a screen must not cost it its own ids.
            check(foundAnyWindow("minsize_root_view") == 1) { "marker clobbered the root id" }
            check(foundAnyWindow("minsize_child_0") == 1) { "marker clobbered a child id" }
            check(foundAnyWindow(marker("minsize")) == 1) { "a screen must expose exactly one marker" }
            // The library's own self-opt-in has to survive under an ancestor
            // that sets testTagsAsResourceId = false — a tree that opts in
            // higher up masks a break here, and that is how a modifier-order
            // mistake in ScreenMarker nearly shipped.
            check(foundAnyWindow(marker("library_shape")) == 1) {
                "library ScreenMarker() is not addressable under an opted-out ancestor: " +
                    snapshot("library", marker("library_shape"))
            }
        }
    }

    // MARK: - Predicate: what does a covered screen look like?

    @Test
    fun navHostDestinationReplacesTheSource() {
        launch("nav").use {
            check(awaitMarker("probe_home")) { "start destination marker missing" }
            println("[screen-marker] NAVHOST (before navigate)")
            println("  " + snapshot("source(probe_home)", marker("probe_home")))
            println("  " + snapshot("destination(detail_screen)", marker("detail_screen")))

            device.findObject(By.res("go_detail")).click()

            // Sampled without settling: NavHost composes source and destination
            // together for the duration of the transition, so this is the
            // window in which a naive presence check can see BOTH.
            println("[screen-marker] NAVHOST (immediately after navigate)")
            println("  " + snapshot("source(probe_home)", marker("probe_home")))
            println("  " + snapshot("destination(detail_screen)", marker("detail_screen")))

            check(awaitMarker("detail_screen")) { "destination marker never appeared" }
            device.waitForIdle()
            Thread.sleep(700) // well past the 300ms default NavHost transition
            println("[screen-marker] NAVHOST (settled)")
            println("  " + snapshot("source(probe_home)", marker("probe_home")))
            println("  " + snapshot("destination(detail_screen)", marker("detail_screen")))
        }
    }

    @Test
    fun dialogCoversThePresenter() {
        launch("dialog").use {
            check(awaitMarker("probe_home")) { "presenter marker missing" }
            device.findObject(By.res("open_dialog")).click()
            check(awaitMarker("dialog_screen")) { "dialog marker never appeared" }
            device.waitForIdle()

            println("[screen-marker] DIALOG")
            println("  " + snapshot("covering(dialog_screen)", marker("dialog_screen")))
            println("  " + snapshot("covered(probe_home)", marker("probe_home")))
            println("  " + snapshot("covered_child(probe_home_child_0)", "probe_home_child_0"))
        }
    }

    @Test
    fun modalBottomSheetCoversThePresenter() {
        launch("sheet").use {
            check(awaitMarker("probe_home")) { "presenter marker missing" }
            device.findObject(By.res("open_sheet")).click()
            check(awaitMarker("sheet_screen")) { "sheet marker never appeared" }
            device.waitForIdle()
            Thread.sleep(700) // let the sheet finish sliding up

            println("[screen-marker] MODAL BOTTOM SHEET")
            println("  " + snapshot("covering(sheet_screen)", marker("sheet_screen")))
            println("  " + snapshot("covered(probe_home)", marker("probe_home")))
            println("  " + snapshot("covered_child(probe_home_child_0)", "probe_home_child_0"))
        }
    }

    @Test
    fun scrollingDoesNotHideTheMarker() {
        launch("scroll").use {
            check(awaitMarker("inside_scroll")) { "inside-scroll marker missing before scroll" }
            println("[screen-marker] SCROLL (before)")
            println("  " + snapshot("inside_scroll", marker("inside_scroll")))
            println("  " + snapshot("outside_scroll", marker("outside_scroll")))

            // Scroll BOTH panes to their bottom: the marker sitting in the
            // scrolling content leaves the viewport, the sibling one cannot.
            // Fixed coordinates — a UiObject2 held across a swipe goes stale.
            val leftX = device.displayWidth / 4
            val rightX = device.displayWidth * 3 / 4
            val fromY = device.displayHeight * 3 / 4
            val toY = device.displayHeight / 4
            repeat(4) {
                device.swipe(leftX, fromY, leftX, toY, 8)
                device.swipe(rightX, fromY, rightX, toY, 8)
            }
            device.waitForIdle()

            println("[screen-marker] SCROLL (after scrolling both panes to the bottom)")
            println("  " + snapshot("inside_scroll", marker("inside_scroll")))
            println("  " + snapshot("outside_scroll", marker("outside_scroll")))
            println("  " + snapshot("inside_bottom(reached?)", "inside_scroll_bottom"))
            println("  " + snapshot("outside_bottom(reached?)", "outside_scroll_bottom"))
        }
    }

    // MARK: - Edge-to-edge: the shape a generated screen actually has

    /**
     * Every other probe here runs under a root that pads the system bars away.
     * A generated screen has no such padding, so its marker lands at the window
     * origin with the status bar on top of it — which is the one placement none
     * of these measurements ever covered.
     *
     * Records three candidates at the SAME origin, one launch each: the shipped
     * node, the same node placed past the insets, and a larger node (to tell
     * "this region is hidden" apart from "this node is too small").
     */
    @Test
    fun edgeToEdgeMarkerPlacement() {
        for (mode in listOf("edge_current", "edge_origin", "edge_big", "edge_nested")) {
            launch(mode).use {
                // Wait on content, NOT on the marker: waiting on the marker
                // would hang for exactly the candidate under suspicion.
                check(device.wait(Until.hasObject(By.res("edge_child_0")), 10_000) != null) {
                    "$mode: probe content never appeared"
                }
                device.waitForIdle()
                println("[screen-marker] EDGE-TO-EDGE $mode")
                println("  " + snapshot("marker", marker("edge_marker")))
                println("  " + snapshot("content", "edge_child_0"))
                println("  insets=" + insetsLabel())
            }
        }
    }

    /**
     * REGRESSION GUARD — the library marker must satisfy the DRIVER's own
     * predicate on the shape a generated screen actually has.
     *
     * This is the check that was missing. Every other probe here runs under a
     * root that pads the system bars away, so the marker was never measured in
     * the one position generated code puts it in: the window origin, under the
     * status bar. Asserting presence in the tree would not have caught it
     * either — the node IS in the tree at y=0; it is `By.res` that drops it.
     */
    @Test
    fun edgeToEdgeMarkerSatisfiesTheDriverPredicate() {
        launch("edge_current").use {
            check(device.wait(Until.hasObject(By.res("edge_child_0")), 10_000) != null) {
                "probe content never appeared"
            }
            device.waitForIdle()
            val id = marker("edge_marker")
            // Spelled exactly as the driver spells it, not as a presence test:
            // `inTree` would pass with the bug in place.
            val found = device.findObject(By.res(id)) != null
            check(found) {
                "marker is not findable by By.res at the window origin — " +
                    snapshot("marker", id)
            }
        }
    }

    /**
     * Sweeps the marker's y offset to find where visibility actually flips.
     * A single reading was ambiguous — y=48 measured visible while y=0 did not,
     * under a 72px inset — so the boundary is read off rather than assumed.
     */
    @Test
    fun edgeToEdgeVisibilityBoundary() {
        launch("edge_sweep").use {
            check(device.wait(Until.hasObject(By.res("__screen_sweep_144")), 10_000) != null) {
                "sweep never appeared"
            }
            device.waitForIdle()
            println("[screen-marker] EDGE SWEEP  insets=" + insetsLabel())
            // The windows above the app are what the a11y framework subtracts
            // from its visible region, so their bounds are the candidate
            // explanation for wherever the boundary lands.
            for (w in instrumentation.uiAutomation.windows) {
                val r = Rect().also { w.getBoundsInScreen(it) }
                println("  window type=${w.type} layer=${w.layer} bounds=$r")
            }
            for (y in SWEEP_OFFSETS) {
                println("  " + snapshot("y=$y", "__screen_sweep_$y"))
            }
        }
    }

    /** The inset row the probe renders, so bounds read against a measured number. */
    private fun insetsLabel(): String {
        val root = instrumentation.uiAutomation.rootInActiveWindow ?: return "-"
        return findByPrefix(root, "edge_insets_")?.viewIdResourceName ?: "-"
    }

    private fun findByPrefix(node: AccessibilityNodeInfo?, prefix: String): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.viewIdResourceName?.startsWith(prefix) == true) return node
        for (i in 0 until node.childCount) {
            findByPrefix(node.getChild(i), prefix)?.let { return it }
        }
        return null
    }

    @Test
    fun splitPaneShowsBothMarkers() {
        launch("split").use {
            check(awaitMarker("left_pane")) { "left pane marker missing" }
            check(awaitMarker("right_pane")) { "right pane marker missing" }

            // Both panes are genuinely displayed: this is why the assertion
            // means "displayed", never "displayed exclusively".
            println("[screen-marker] SPLIT PANE")
            println("  " + snapshot("left_pane", marker("left_pane")))
            println("  " + snapshot("right_pane", marker("right_pane")))
        }
    }
}
