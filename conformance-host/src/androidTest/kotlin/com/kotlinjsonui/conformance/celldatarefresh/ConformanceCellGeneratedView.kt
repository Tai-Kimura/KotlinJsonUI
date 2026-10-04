// Pasted from what `kjui build` (kjui_tools of jsonui-cli train/1.9.11 = 5447c616) emits for three Collections
// of conformance_cell cells — cellIdProperty "cellId", "key", and "cellId" with autoChangeTrackingId — and the
// cell view and view model it scaffolds, for CellDataRefreshProbeTest (jsonui-cli ticket
// ios-cell-ignores-data-change-when-cellid-is-fixed-android-updates). Changed from the output: the packages
// (com.kotlinjsonui.probe.* → com.kotlinjsonui.conformance.celldatarefresh) and the cell's two colorResource(R.color.…)
// (the probe project's resources) → the literal colors conformance_cell.json declares.
package com.kotlinjsonui.conformance.celldatarefresh

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun ConformanceCellGeneratedView(
    data: ConformanceCellData,
    viewModel: ConformanceCellViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from conformance_cell.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
    DriveEmbedInitParams(viewModel)
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "conformance_cell",
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
                android.util.Log.e("DynamicView", "Error loading conformance_cell: \$error")
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
        Box(
        modifier = modifier
            .testTag("cell_root")
            .semantics { testTagsAsResourceId = true }
            .requiredWidth(60.dp)
            .requiredHeight(28.dp)
            .background(Color(0xFF3366CC))
    ) {
        val resolved_text1 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 11.sp,
            italic = false
        ))
        Text(
            text = "${data.title}",
            color = Color(0xFFFFFFFF),
            fontFamily = resolved_text1.family,
            fontWeight = resolved_text1.weight,
            fontSize = resolved_text1.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text1.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 14.3.sp),
            modifier = Modifier
                .testTag("cell_title")
                .semantics { testTagsAsResourceId = true }
                .padding(4.dp)
        )
    }    }
    // >>> GENERATED_CODE_END
}