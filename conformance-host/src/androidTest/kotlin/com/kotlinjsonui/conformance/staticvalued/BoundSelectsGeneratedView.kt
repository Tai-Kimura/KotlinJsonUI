// Pasted from what `kjui build` (kjui_tools of jsonui-cli triage/selectbox-item-binding 640dd2f6, on
// rel/v1.8.121) emits for four SelectBoxes bound to the data — selectedItem, selectedValue, selectedDate,
// selectedIndex — for ticket selectbox-selected-item-binding-is-read-once. Changed from the output: the
// package (com.kotlinjsonui.probe.* → com.kotlinjsonui.conformance.staticvalued) and the unused
// `import com.kotlinjsonui.probe.R`.
package com.kotlinjsonui.conformance.staticvalued

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
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
import com.kotlinjsonui.components.DateSelectBox
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.components.SelectBox
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun BoundSelectsGeneratedView(
    data: BoundSelectsData,
    viewModel: BoundSelectsViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from bound_selects.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
        // Check if Dynamic Mode is active
        if (DynamicModeManager.isActive()) {
            // Dynamic Mode - use SafeDynamicView for real-time updates
            SafeDynamicView(
                layoutName = "bound_selects",
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
                    android.util.Log.e("DynamicView", "Error loading bound_selects: \$error")
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
            SelectBox(
                value = data.sbiSel,
                onValueChange = { newValue ->
                    viewModel.updateData(mapOf("sbiSel" to newValue))
                },
                options = listOf("pp", "qq"),
                modifier = Modifier
                    .testTag("sbi")
                    .semantics { testTagsAsResourceId = true }
                    .requiredHeight(40.dp)
            )
            SelectBox(
                value = data.sbvSel,
                onValueChange = { newValue ->
                    viewModel.updateData(mapOf("sbvSel" to newValue))
                },
                options = listOf("pp", "qq"),
                modifier = Modifier
                    .testTag("sbv")
                    .semantics { testTagsAsResourceId = true }
                    .requiredHeight(40.dp)
            )
            DateSelectBox(
                value = data.sbdDate,
                onValueChange = { newValue ->
                    viewModel.updateData(mapOf("sbdDate" to newValue))
                },
                datePickerMode = "date",
                dateFormat = "yyyy-MM-dd",
                modifier = Modifier
                    .testTag("sbd")
                    .semantics { testTagsAsResourceId = true }
                    .fillMaxWidth()
                    .requiredHeight(40.dp)
            )
            SelectBox(
                value = listOf("pp", "qq").getOrElse(data.sbIdx) { "" },
                onValueChange = { newValue ->
                    val index = listOf("pp", "qq").indexOf(newValue)
                    viewModel.updateData(mapOf("sbIdx" to index))
                },
                options = listOf("pp", "qq"),
                modifier = Modifier
                    .testTag("sb")
                    .semantics { testTagsAsResourceId = true }
                    .requiredHeight(40.dp)
            )
        }    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("bound_selects")
    }
    // >>> GENERATED_CODE_END
}