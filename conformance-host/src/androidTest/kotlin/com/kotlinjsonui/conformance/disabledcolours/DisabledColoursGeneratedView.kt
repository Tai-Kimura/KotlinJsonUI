// Pasted from what `kjui build` emits for DisabledColoursProbeTest's layout — kjui_tools of jsonui-cli
// fix/bound-enabled-disabled-colors 27adb275 (ticket 2026-10-23-bound-enabled-disabled-colors-three-faces): a bound
// `enabled` chooses the disabled colours. Changed from the output: the package ({com.example.app.*,
// com.kotlinjsonui.generated} → com.kotlinjsonui.conformance.disabledcolours, so the generated ColorManager is the probe's own and never the codegen
// host's), the imports that named them, and the project's resources written as their values
// (colorResource(R.color.dark_blue) → Color(0xFF0000FF), R.color.light_gray → Color(0xFF999999),
// stringResource(R.string.disabled_colours_mmmm) → "MMMM"). The `if (data.on) … else …` choices are as emitted.
package com.kotlinjsonui.conformance.disabledcolours

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
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.disabled
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
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.core.rememberTestBridge
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun DisabledColoursGeneratedView(
    data: DisabledColoursData,
    viewModel: DisabledColoursViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from disabled_colours.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.44.0 (test bridge: initialState and state)
        val testBridge = rememberTestBridge(
            "disabled_colours",
            snapshot = { data.testSnapshot() },
            apply = { data.applyTestInitialState(it, viewModel::updateData) }
        )
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
        // Check if Dynamic Mode is active
        if (DynamicModeManager.isActive()) {
            // Dynamic Mode - use SafeDynamicView for real-time updates
            SafeDynamicView(
                layoutName = "disabled_colours",
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
                    android.util.Log.e("DynamicView", "Error loading disabled_colours: \$error")
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
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .testTag("kg_dc_root")
                .semantics { testTagsAsResourceId = true }
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .wrapContentWidth(align = BiasAlignment.Horizontal(-1.0f), unbounded = true)
                    .requiredWidth(120.dp)
                    .wrapContentHeight(align = BiasAlignment.Vertical(-1.0f), unbounded = true)
                    .requiredHeight(60.dp)
                    .testTag("kg_dc_view")
                    .semantics { testTagsAsResourceId = true }
                    .background(if (data.on) ColorManager.compose.color(data.bg) ?: Color.Unspecified else Color(0xFF999999))
                    .semantics { if (!data.on) disabled() }
            ) {
            }
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .wrapContentWidth(align = BiasAlignment.Horizontal(-1.0f), unbounded = true)
                    .requiredWidth(120.dp)
                    .wrapContentHeight(align = BiasAlignment.Vertical(-1.0f), unbounded = true)
                    .requiredHeight(60.dp)
                    .testTag("kg_dc_view_fixed")
                    .semantics { testTagsAsResourceId = true }
                    .background(if (data.on) Color(0xFF0000FF) else Color(0xFF999999))
                    .semantics { if (!data.on) disabled() }
            ) {
            }
            val resolved_text1 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 40.sp,
                italic = false
            ))
            Text(
                text = "MMMM",
                color = if (data.on) ColorManager.compose.color(data.fg) ?: Color.Unspecified else Color(android.graphics.Color.parseColor("#555555")),
                fontFamily = resolved_text1.family,
                fontWeight = resolved_text1.weight,
                fontSize = resolved_text1.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text1.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 52.0.sp),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .testTag("kg_dc_label")
                    .semantics { testTagsAsResourceId = true }
                    .semantics { if (!data.on) disabled() }
            )
        }    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("disabled_colours", bridge = testBridge)
    }
    // >>> GENERATED_CODE_END
}