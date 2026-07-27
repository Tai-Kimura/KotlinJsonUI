package com.kotlinjsonui.conformance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * Screen-marker measurement probe (screen-identity track, Phase 0) — NOT part
 * of the conformance suite. Driven by [ScreenMarkerProbeTest], or manually:
 *
 *   adb shell am start -n com.kotlinjsonui.conformance/.ScreenMarkerProbeActivity \
 *     -e probe nav|dialog|sheet|split
 *
 * Android asks three questions the canon cannot assume:
 *
 * 1. WHAT SIZE survives? The marker is a dedicated sibling node, so it has no
 *    content of its own. Compose reports a node rendered outside the visible
 *    window as invisible and UiAutomator then cannot find it by resource-id
 *    (measured for system-bar overlap, see FixtureHostActivity). A zero-size
 *    node may be dropped the same way — in which case the marker needs a
 *    minimum size.
 *
 * 2. WHAT DAMAGES it? `mergeDescendants` and `clearAndSetSemantics` on an
 *    ancestor erase descendant test tags. The probe puts a marker under each
 *    so the failure is observable rather than assumed.
 *
 * 3. WHICH PREDICATE means "displayed"? The driver searches with
 *    `By.res(id)`, which spans EVERY window: a dialog or a modal bottom sheet
 *    lives in its own window while the screen underneath stays composed. The
 *    probe leaves both screens' markers in place so the test can compare an
 *    all-window search against a `rootInActiveWindow`-scoped one, and record
 *    `visibleBounds` for the covered screen.
 *
 * The probe asserts nothing on its own: it is an instrument.
 */
class ScreenMarkerProbeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mode = intent.getStringExtra(EXTRA_PROBE) ?: ProbeMode.currentMode
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .systemBarsPadding()
                            .semantics { testTagsAsResourceId = true }
                    ) {
                        when (mode) {
                            "dialog" -> DialogProbe()
                            "sheet" -> SheetProbe()
                            "split" -> SplitProbe()
                            "scroll" -> ScrollProbe()
                            else -> NavProbe()
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_PROBE = "probe"
    }
}

/** Lets the instrumented test pick a mode without relaunching by intent. */
object ProbeMode {
    @Volatile
    var currentMode: String = "nav"
}

// MARK: - Marker shapes under test

/** Candidate A — the canon's proposal: a zero-size sibling node. */
@Composable
private fun ZeroSizeMarker(screenId: String) {
    Box(Modifier.size(0.dp).testTag("__screen_$screenId"))
}

/** Candidate B — same node with a minimum footprint, in case A disappears. */
@Composable
private fun MinSizeMarker(screenId: String) {
    Box(Modifier.size(1.dp).testTag("__screen_$screenId"))
}

/**
 * Candidate C — a sized child inside a wrapper that reports ZERO size to its
 * parent. Candidate B costs 1.dp of layout in every Column-shaped screen,
 * which shifts real content and every screenshot baseline; this shape is the
 * Compose equivalent of the iOS overlay, and the open question is whether a
 * child placed outside its parent's 0x0 bounds is still reported to the
 * accessibility tree.
 */
@Composable
private fun ZeroLayoutMarker(screenId: String) {
    val sizePx = with(LocalDensity.current) { 1.dp.roundToPx() }
    Layout(content = { Box(Modifier.size(1.dp).testTag("__screen_$screenId")) }) { measurables, _ ->
        val placeable = measurables.first().measure(Constraints.fixed(sizePx, sizePx))
        layout(0, 0) { placeable.place(0, 0) }
    }
}

/**
 * A screen body shaped like generated code: a root container carrying its own
 * testTag plus identified children, with the marker as a SIBLING of the
 * content rather than a wrapper.
 */
@Composable
private fun ProbeScreenBody(
    screenId: String,
    minSizeMarker: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        Column(Modifier.testTag("${screenId}_root_view")) {
            Text("$screenId-child-0", modifier = Modifier.testTag("${screenId}_child_0"))
            Text("$screenId-child-1", modifier = Modifier.testTag("${screenId}_child_1"))
        }
        if (minSizeMarker) MinSizeMarker(screenId) else ZeroSizeMarker(screenId)
    }
}

// MARK: - Probes

@Composable
private fun NavProbe() {
    val navController = rememberNavController()
    Column {
        // Shape measurements live on the start destination so a single launch
        // answers question 1 and question 2.
        ProbeScreenBody("zerosize")
        ProbeScreenBody("minsize", minSizeMarker = true)

        // Candidate C measured in the same content shape: does a child
        // placed outside its zero-size parent survive into the a11y tree?
        Column {
            Text("zerolayout-content", modifier = Modifier.testTag("zerolayout_child_0"))
            ZeroLayoutMarker("zerolayout")
        }

        // The LIBRARY's real composable, in the shape code generation emits
        // it (a sibling inside a Box) and deliberately WITHOUT an ancestor
        // that opts into testTagsAsResourceId — the library sets that on the
        // marker node itself, and this is what proves that is enough.
        Box(Modifier.semantics { testTagsAsResourceId = false }) {
            Box {
                Text("library-content", modifier = Modifier.testTag("library_child_0"))
                com.kotlinjsonui.core.ScreenMarker("library_shape")
            }
        }

        // A marker underneath a merged ancestor: expected to vanish.
        Column(Modifier.semantics(mergeDescendants = true) { }) {
            Text("merged-content")
            ZeroSizeMarker("merged_host")
            MinSizeMarker("merged_host_min")
        }
        // A marker underneath a cleared ancestor: expected to vanish.
        Column(Modifier.clearAndSetSemantics { }) {
            Text("cleared-content")
            ZeroSizeMarker("cleared_host")
            MinSizeMarker("cleared_host_min")
        }

        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                Column {
                    ProbeScreenBody("probe_home", minSizeMarker = true)
                    Button(
                        onClick = { navController.navigate("detail") },
                        modifier = Modifier.testTag("go_detail")
                    ) { Text("go-detail") }
                }
            }
            composable("detail") {
                ProbeScreenBody("detail_screen", minSizeMarker = true)
            }
        }
    }
}

@Composable
private fun DialogProbe() {
    var open by remember { mutableStateOf(false) }
    Column {
        ProbeScreenBody("probe_home", minSizeMarker = true)
        Button(onClick = { open = true }, modifier = Modifier.testTag("open_dialog")) {
            Text("open-dialog")
        }
    }
    if (open) {
        AlertDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = { open = false }, modifier = Modifier.testTag("close_dialog")) {
                    Text("close")
                }
            },
            text = {
                // The dialog content is its own window; the marker rides in it.
                Box(Modifier.semantics { testTagsAsResourceId = true }) {
                    ProbeScreenBody("dialog_screen", minSizeMarker = true)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SheetProbe() {
    var open by remember { mutableStateOf(false) }
    Column {
        ProbeScreenBody("probe_home", minSizeMarker = true)
        Button(onClick = { open = true }, modifier = Modifier.testTag("open_sheet")) {
            Text("open-sheet")
        }
    }
    if (open) {
        ModalBottomSheet(onDismissRequest = { open = false }) {
            Box(Modifier.semantics { testTagsAsResourceId = true }) {
                ProbeScreenBody("sheet_screen", minSizeMarker = true)
            }
        }
    }
}

/**
 * A screen whose root IS a scrollable. The marker has to keep reporting
 * "displayed" after the user scrolls, so its placement relative to the
 * scrollable decides whether the assertion is stable:
 *
 * - `inside_scroll`  — marker is a child of the scrolling content (what a
 *   naive "append a sibling to the root container" implementation produces)
 * - `outside_scroll` — marker is a sibling of the scrollable, in a wrapping
 *   Box, so scrolling cannot move it
 */
@Composable
private fun ScrollProbe() {
    Row(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text("inside-top", modifier = Modifier.testTag("inside_scroll_child_0"))
            MinSizeMarker("inside_scroll")
            repeat(60) { Text("inside-filler-$it") }
            Text("inside-bottom", modifier = Modifier.testTag("inside_scroll_bottom"))
        }
        Box(Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .testTag("outside_scroll_scroller")
            ) {
                Text("outside-top", modifier = Modifier.testTag("outside_scroll_child_0"))
                repeat(60) { Text("outside-filler-$it") }
                Text("outside-bottom", modifier = Modifier.testTag("outside_scroll_bottom"))
            }
            // Sibling of the scrollable, not of the scrolling content.
            MinSizeMarker("outside_scroll")
        }
    }
}

/**
 * Side-by-side panes: both screens are genuinely displayed at once, which is
 * why the assertion means "this screen is displayed", never "only this one".
 */
@Composable
private fun SplitProbe() {
    Row(Modifier.fillMaxSize()) {
        ProbeScreenBody("left_pane", minSizeMarker = true, modifier = Modifier.weight(1f))
        ProbeScreenBody("right_pane", minSizeMarker = true, modifier = Modifier.weight(1f))
    }
}
