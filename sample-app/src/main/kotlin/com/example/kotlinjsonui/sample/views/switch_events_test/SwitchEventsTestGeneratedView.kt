package com.example.kotlinjsonui.sample.views.switch_events_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.SwitchEventsTestData
import com.example.kotlinjsonui.sample.viewmodels.SwitchEventsTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont
import com.kotlinjsonui.dynamic.LocalSafeAreaConfig
import com.kotlinjsonui.dynamic.SafeAreaConfig

@Composable
fun SwitchEventsTestGeneratedView(
    data: SwitchEventsTestData,
    viewModel: SwitchEventsTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from switch_events_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "switch_events_test",
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
                android.util.Log.e("DynamicView", "Error loading switch_events_test: \$error")
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
        val safeAreaConfig = LocalSafeAreaConfig.current
    val edges = mutableListOf("all").apply {
        if (safeAreaConfig.ignoreBottom) {
            remove("bottom")
            if (contains("all")) { remove("all"); addAll(listOf("top", "start", "end")) }
        }
        if (safeAreaConfig.ignoreTop) {
            remove("top")
            if (contains("all")) { remove("all"); addAll(listOf("bottom", "start", "end")) }
        }
    }.distinct()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(if (edges.contains("all")) Modifier.systemBarsPadding() else Modifier)
            .then(if (!edges.contains("all") && edges.contains("top")) Modifier.statusBarsPadding() else Modifier)
            .then(if (!edges.contains("all") && edges.contains("bottom")) Modifier.navigationBarsPadding() else Modifier)
            .imePadding()
    ) {
        LazyColumn(
            modifier = Modifier.imePadding()
        ) {
            item {
            Column(
                modifier = Modifier
                    .testTag("container")
                    .semantics { testTagsAsResourceId = true }
                    .background(colorResource(R.color.white_23))
            ) {
                val resolved_text277 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = FontWeight.Bold,
                    size = 24.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.switch_events_test_switch_events_test),
                    fontFamily = resolved_text277.family,
                    fontWeight = resolved_text277.weight,
                    fontSize = resolved_text277.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text277.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 31.2.sp),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp)
                        .padding(bottom = 20.dp),
                    textAlign = TextAlign.Center
                )
                Section1(data, viewModel)
                Section2(data, viewModel)
                Section3(data, viewModel)
                Section4(data, viewModel)
                Section5(data, viewModel)
                Section6(data, viewModel)
                Section7(data, viewModel)
                Section8(data, viewModel)
                Section9(data, viewModel)
                Section10(data, viewModel)
                Section11(data, viewModel)
            }
            }
        }
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section1(
    data: SwitchEventsTestData,
    viewModel: SwitchEventsTestViewModel
) {
    val resolved_text278 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.switch_events_test_switch_with_onvaluechange),
        fontFamily = resolved_text278.family,
        fontWeight = resolved_text278.weight,
        fontSize = resolved_text278.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text278.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
    )
}

@Composable
private fun Section2(
    data: SwitchEventsTestData,
    viewModel: SwitchEventsTestViewModel
) {
    Switch(
        checked = data.notificationEnabled,
        onCheckedChange = { newValue -> viewModel.updateData(mapOf("notificationEnabled" to newValue)); data.handleNotificationChange?.invoke("notificationSwitch", newValue) },
        modifier = Modifier
            .testTag("notificationSwitch")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
    )
}

@Composable
private fun Section3(
    data: SwitchEventsTestData,
    viewModel: SwitchEventsTestViewModel
) {
    val resolved_text279 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.notificationStatus}",
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text279.family,
        fontWeight = resolved_text279.weight,
        fontSize = resolved_text279.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text279.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .testTag("notificationStatus")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
    )
}

@Composable
private fun Section4(
    data: SwitchEventsTestData,
    viewModel: SwitchEventsTestViewModel
) {
    val resolved_text280 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.switch_events_test_switch_with_custom_tint),
        fontFamily = resolved_text280.family,
        fontWeight = resolved_text280.weight,
        fontSize = resolved_text280.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text280.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .padding(start = 20.dp)
    )
}

@Composable
private fun Section5(
    data: SwitchEventsTestData,
    viewModel: SwitchEventsTestViewModel
) {
    Switch(
        checked = data.darkModeEnabled,
        onCheckedChange = { newValue -> viewModel.updateData(mapOf("darkModeEnabled" to newValue)); data.handleDarkModeChange?.invoke("darkModeSwitch", newValue) },
        modifier = Modifier
            .testTag("darkModeSwitch")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp),
        colors = SwitchDefaults.colors(
            checkedTrackColor = colorResource(R.color.light_red)
        )
    )
}

@Composable
private fun Section6(
    data: SwitchEventsTestData,
    viewModel: SwitchEventsTestViewModel
) {
    val resolved_text281 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.darkModeStatus}",
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text281.family,
        fontWeight = resolved_text281.weight,
        fontSize = resolved_text281.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text281.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
    )
}

@Composable
private fun Section7(
    data: SwitchEventsTestData,
    viewModel: SwitchEventsTestViewModel
) {
    val resolved_text282 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.switch_events_test_multiple_switches_with_events),
        fontFamily = resolved_text282.family,
        fontWeight = resolved_text282.weight,
        fontSize = resolved_text282.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text282.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .padding(start = 20.dp)
    )
}

@Composable
private fun Section8(
    data: SwitchEventsTestData,
    viewModel: SwitchEventsTestViewModel
) {
    Switch(
        checked = data.wifiEnabled,
        onCheckedChange = { newValue -> viewModel.updateData(mapOf("wifiEnabled" to newValue)); data.handleWifiChange?.invoke("wifiSwitch", newValue) },
        modifier = Modifier
            .testTag("wifiSwitch")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp),
        colors = SwitchDefaults.colors(
            checkedTrackColor = colorResource(R.color.medium_green_2)
        )
    )
}

@Composable
private fun Section9(
    data: SwitchEventsTestData,
    viewModel: SwitchEventsTestViewModel
) {
    Switch(
        checked = data.bluetoothEnabled,
        onCheckedChange = { newValue -> viewModel.updateData(mapOf("bluetoothEnabled" to newValue)); data.handleBluetoothChange?.invoke("bluetoothSwitch", newValue) },
        modifier = Modifier
            .testTag("bluetoothSwitch")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp),
        colors = SwitchDefaults.colors(
            checkedTrackColor = colorResource(R.color.medium_blue_2)
        )
    )
}

@Composable
private fun Section10(
    data: SwitchEventsTestData,
    viewModel: SwitchEventsTestViewModel
) {
    Switch(
        checked = data.locationEnabled,
        onCheckedChange = { newValue -> viewModel.updateData(mapOf("locationEnabled" to newValue)); data.handleLocationChange?.invoke("locationSwitch", newValue) },
        modifier = Modifier
            .testTag("locationSwitch")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp),
        colors = SwitchDefaults.colors(
            checkedTrackColor = colorResource(R.color.medium_red_5)
        )
    )
}

@Composable
private fun Section11(
    data: SwitchEventsTestData,
    viewModel: SwitchEventsTestViewModel
) {
    val resolved_text283 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.connectionStatus}",
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text283.family,
        fontWeight = resolved_text283.weight,
        fontSize = resolved_text283.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text283.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(start = 20.dp)
    )
}
// >>> RESPONSIVE_HELPERS_END