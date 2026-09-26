// Pasted from what `kjui build` emits for the static layouts of StaticValuedControlsProbeTest —
// kjui_tools of jsonui-cli triage/selectbox-item-binding 43e735a3 (rel/v1.8.121 = 621d3136 merged in): a static value seeds the control's own state (ticket
// static-valued-controls-do-not-change-on-a-users-tap). Changed from the output: the package
// (com.kotlinjsonui.probe.* → com.kotlinjsonui.conformance.staticvalued) and the unused
// `import com.kotlinjsonui.probe.R`.
package com.kotlinjsonui.conformance.staticvalued

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.components.Segment
import com.kotlinjsonui.components.SelectBox
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.dynamic.LocalSafeAreaConfig
import com.kotlinjsonui.dynamic.SafeAreaConfig
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun StaticControlsGeneratedView(
    data: StaticControlsData,
    viewModel: StaticControlsViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from static_controls.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
        // Check if Dynamic Mode is active
        if (DynamicModeManager.isActive()) {
            // Dynamic Mode - use SafeDynamicView for real-time updates
            SafeDynamicView(
                layoutName = "static_controls",
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
                    android.util.Log.e("DynamicView", "Error loading static_controls: \$error")
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
            CompositionLocalProvider(LocalRadioGroupSelections provides remember { mutableStateMapOf<String, String>() }) {
        Section10(data, viewModel, modifier)
        }    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("static_controls")
    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section1(
    data: StaticControlsData,
    viewModel: StaticControlsViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier
            .testTag("tab")
            .semantics { testTagsAsResourceId = true },
        bottomBar = {
            NavigationBar(
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Filled.Circle else Icons.Outlined.Circle,
                            contentDescription = "ta"
                        )
                    },
                    label = { Text("ta") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Filled.Circle else Icons.Outlined.Circle,
                            contentDescription = "tb"
                        )
                    },
                    label = { Text("tb") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
            CompositionLocalProvider(
                LocalSafeAreaConfig provides SafeAreaConfig(ignoreBottom = true)
            ) {
                when (selectedTab) {
                    0 -> {
                        Text("ta content")
                    }
                    1 -> {
                        Text("tb content")
                    }
                }
            }
        }
    }
}

@Composable
private fun Section2(
    data: StaticControlsData,
    viewModel: StaticControlsViewModel
) {
    val radioGroups = LocalRadioGroupSelections.current
    RadioButton(
        selected = radioGroups["grp"] == "rg1" || radioGroups["grp"] == null,
        onClick = { radioGroups["grp"] = "rg1" }
    )
}

@Composable
private fun Section3(
    data: StaticControlsData,
    viewModel: StaticControlsViewModel
) {
    Spacer(modifier = Modifier.width(8.dp))
}

@Composable
private fun Section4(
    data: StaticControlsData,
    viewModel: StaticControlsViewModel
) {
    Text("rg1", color = Color.Black)
}

@Composable
private fun Section5(
    data: StaticControlsData,
    viewModel: StaticControlsViewModel
) {
    val radioGroups = LocalRadioGroupSelections.current
    RadioButton(
        selected = radioGroups["grp"] == "rg2",
        onClick = { radioGroups["grp"] = "rg2" }
    )
}

@Composable
private fun Section6(
    data: StaticControlsData,
    viewModel: StaticControlsViewModel
) {
    Spacer(modifier = Modifier.width(8.dp))
}

@Composable
private fun Section7(
    data: StaticControlsData,
    viewModel: StaticControlsViewModel
) {
    Text("rg2", color = Color.Black)
}

@Composable
private fun Section10(
    data: StaticControlsData,
    viewModel: StaticControlsViewModel,
    modifier: Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        run {
            var seeded by remember { mutableStateOf(false) }
            Switch(
                checked = seeded,
                onCheckedChange = { seeded = it },
                modifier = Modifier
                    .testTag("sw")
                    .semantics { testTagsAsResourceId = true }
            )
        }
        run {
            var seeded by remember { mutableStateOf(false) }
            Switch(
                checked = seeded,
                onCheckedChange = { seeded = it },
                modifier = Modifier
                    .testTag("tg")
                    .semantics { testTagsAsResourceId = true }
            )
        }
        run {
            var seeded by remember { mutableStateOf(false) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .testTag("cb")
                    .semantics { testTagsAsResourceId = true }
            ) {
                Checkbox(
                    checked = seeded,
                    onCheckedChange = { seeded = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("cbl")
            }
        }
        run {
            var seeded by remember { mutableStateOf("ra") }
            Column(
                modifier = Modifier
                    .testTag("rv")
                    .semantics { testTagsAsResourceId = true }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            seeded = "ra"
                        }
                ) {
                    RadioButton(
                        selected = seeded == "ra",
                        onClick = {
                            seeded = "ra"
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ra", color = Color.Black)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            seeded = "rb"
                        }
                ) {
                    RadioButton(
                        selected = seeded == "rb",
                        onClick = {
                            seeded = "rb"
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("rb", color = Color.Black)
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .testTag("rg1")
                .semantics { testTagsAsResourceId = true }
        ) {
            Section2(data, viewModel)
            Section3(data, viewModel)
            Section4(data, viewModel)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .testTag("rg2")
                .semantics { testTagsAsResourceId = true }
        ) {
            Section5(data, viewModel)
            Section6(data, viewModel)
            Section7(data, viewModel)
        }
        run {
            var seeded by remember { mutableStateOf(0) }
            Segment(
                selectedTabIndex = seeded,
                containerColor = Color.Transparent,
                modifier = Modifier
                    .testTag("seg")
                    .semantics { testTagsAsResourceId = true }
            ) {
                Tab(
                    selected = (seeded == 0),
                    onClick = {
                        seeded = 0
                    },
                    text = { Text("sx") }
                )
                Tab(
                    selected = (seeded == 1),
                    onClick = {
                        seeded = 1
                    },
                    text = { Text("sy") }
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(130.dp)
        ) {
            // TabView with NavigationBar
            Section1(data, viewModel)
        }
        run {
            var seeded by remember { mutableStateOf(0.2f) }
            Slider(
                value = seeded,
                onValueChange = { seeded = it },
                valueRange = 0f..1f,
                modifier = Modifier
                    .testTag("sl")
                    .semantics { testTagsAsResourceId = true }
            )
        }
        run {
            var seeded by remember { mutableStateOf("pp") }
            SelectBox(
                value = seeded,
                onValueChange = { seeded = it },
                options = listOf("pp", "qq"),
                modifier = Modifier
                    .testTag("sb")
                    .semantics { testTagsAsResourceId = true }
                    .requiredHeight(40.dp)
            )
        }
        run {
            var seeded by remember { mutableStateOf("pp") }
            SelectBox(
                value = seeded,
                onValueChange = { seeded = it },
                options = listOf("pp", "qq"),
                modifier = Modifier
                    .testTag("sbi")
                    .semantics { testTagsAsResourceId = true }
                    .requiredHeight(40.dp)
            )
        }
    }
}

// Each unbound group of single Radios in this view: group name -> the chosen item
// (absent until the user chooses — the checked item shows until then).
private val LocalRadioGroupSelections = compositionLocalOf<MutableMap<String, String>> { mutableStateMapOf() }
// >>> RESPONSIVE_HELPERS_END