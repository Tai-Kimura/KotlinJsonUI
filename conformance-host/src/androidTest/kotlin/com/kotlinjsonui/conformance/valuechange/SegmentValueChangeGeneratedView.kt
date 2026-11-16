// Pasted from what `kjui build` (kjui_tools of jsonui-cli rel/v1.8.121 = 74bdbfeb) emits for four
// Segments with a `valueChange` — alone (segv…) and beside an onValueChange (segw…), unbound and bound —
// for SegmentValueChangeProbeTest (ticket control-onclick-is-called-differently-on-every-path). Changed
// from the output: the package (com.kotlinjsonui.probe.* → com.kotlinjsonui.conformance.valuechange) and
// the unused `import com.kotlinjsonui.probe.R`.
package com.kotlinjsonui.conformance.valuechange

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.components.Segment
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun SegmentValueChangeGeneratedView(
    data: SegmentValueChangeData,
    viewModel: SegmentValueChangeViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from segment_value_change.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
        // Check if Dynamic Mode is active
        if (DynamicModeManager.isActive()) {
            // Dynamic Mode - use SafeDynamicView for real-time updates
            SafeDynamicView(
                layoutName = "segment_value_change",
                modifier = modifier,
                data = data.toMap(),
                fallback = {
                    // Show error or loading state when dynamic view is not available
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Dynamic view not available",
                            color = Color.Gray
                        )
                    }
                },
                onError = { error ->
                    // Log error or show error UI
                    android.util.Log.e("DynamicView", "Error loading segment_value_change: \$error")
                },
                onLoading = {
                    // Show loading indicator
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            ) { jsonContent ->
                // Parse and render the dynamic JSON content
                // This will be handled by the DynamicView implementation
            }
        } else {
            // Static Mode - use generated code
            Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Section0(data, viewModel)
            Section1(data, viewModel)
        }    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("segment_value_change")
    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: SegmentValueChangeData,
    viewModel: SegmentValueChangeViewModel
) {
    run {
        var seeded by remember { mutableStateOf(0) }
        Segment(
            selectedTabIndex = seeded,
            containerColor = Color.Transparent,
            modifier = Modifier
                .testTag("segvu")
                .semantics { testTagsAsResourceId = true }
        ) {
            Tab(
                selected = (seeded == 0),
                onClick = {
                    seeded = 0
                    data.onSegvuV?.invoke()
                    data.onSegvuC?.invoke()
                },
                text = { Text("p1") }
            )
            Tab(
                selected = (seeded == 1),
                onClick = {
                    seeded = 1
                    data.onSegvuV?.invoke()
                    data.onSegvuC?.invoke()
                },
                text = { Text("p2") }
            )
        }
    }
    Segment(
        selectedTabIndex = data.segvbIdx,
        containerColor = Color.Transparent,
        modifier = Modifier
            .testTag("segvb")
            .semantics { testTagsAsResourceId = true }
    ) {
        Tab(
            selected = (data.segvbIdx == 0),
            onClick = {
                viewModel.updateData(mapOf("segvbIdx" to 0))
                data.onSegvbV?.invoke()
                data.onSegvbC?.invoke()
            },
            text = { Text("q1") }
        )
        Tab(
            selected = (data.segvbIdx == 1),
            onClick = {
                viewModel.updateData(mapOf("segvbIdx" to 1))
                data.onSegvbV?.invoke()
                data.onSegvbC?.invoke()
            },
            text = { Text("q2") }
        )
    }
    run {
        var seeded by remember { mutableStateOf(0) }
        Segment(
            selectedTabIndex = seeded,
            containerColor = Color.Transparent,
            modifier = Modifier
                .testTag("segwu")
                .semantics { testTagsAsResourceId = true }
        ) {
            Tab(
                selected = (seeded == 0),
                onClick = {
                    seeded = 0
                    data.onSegwuV?.invoke()
                    data.onSegwuC?.invoke()
                },
                text = { Text("r1") }
            )
            Tab(
                selected = (seeded == 1),
                onClick = {
                    seeded = 1
                    data.onSegwuV?.invoke()
                    data.onSegwuC?.invoke()
                },
                text = { Text("r2") }
            )
        }
    }
}

@Composable
private fun Section1(
    data: SegmentValueChangeData,
    viewModel: SegmentValueChangeViewModel
) {
    Segment(
        selectedTabIndex = data.segwbIdx,
        containerColor = Color.Transparent,
        modifier = Modifier
            .testTag("segwb")
            .semantics { testTagsAsResourceId = true }
    ) {
        Tab(
            selected = (data.segwbIdx == 0),
            onClick = {
                viewModel.updateData(mapOf("segwbIdx" to 0))
                data.onSegwbV?.invoke()
                data.onSegwbC?.invoke()
            },
            text = { Text("s1") }
        )
        Tab(
            selected = (data.segwbIdx == 1),
            onClick = {
                viewModel.updateData(mapOf("segwbIdx" to 1))
                data.onSegwbV?.invoke()
                data.onSegwbC?.invoke()
            },
            text = { Text("s2") }
        )
    }
}
// >>> RESPONSIVE_HELPERS_END