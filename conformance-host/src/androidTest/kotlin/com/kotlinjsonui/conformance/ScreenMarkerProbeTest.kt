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
