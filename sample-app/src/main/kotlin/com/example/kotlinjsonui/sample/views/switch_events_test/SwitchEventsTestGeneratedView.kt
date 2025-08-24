package com.example.kotlinjsonui.sample.views.switch_events_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.SwitchEventsTestData
import com.example.kotlinjsonui.sample.viewmodels.SwitchEventsTestViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box

@Composable
fun SwitchEventsTestGeneratedView(
    data: SwitchEventsTestData,
    viewModel: SwitchEventsTestViewModel
) {
    // Generated Compose code from switch_events_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "switch_events_test",
            data = data.toMap(viewModel),
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
        Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        LazyColumn(
        ) {
            item {
            Column(
                modifier = Modifier.background(Color(android.graphics.Color.parseColor("#F5F5F5")))
            ) {
                Text(
                    text = "Switch Events Test",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp)
                        .padding(bottom = 20.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Switch with onValueChange",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                )
                Switch(
                    checked = data.notificationEnabled,
                    onCheckedChange = { viewModel.handleNotificationChange(it) },
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                        .padding(end = 20.dp)
                )
                Text(
                    text = "${data.notificationStatus}",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                )
                Text(
                    text = "Switch with Custom Tint",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .padding(start = 20.dp)
                )
                Switch(
                    checked = data.darkModeEnabled,
                    onCheckedChange = { viewModel.handleDarkModeChange(it) },
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                        .padding(end = 20.dp)
                )
                Text(
                    text = "${data.darkModeStatus}",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                )
                Text(
                    text = "Multiple Switches with Events",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .padding(start = 20.dp)
                )
                Switch(
                    checked = data.wifiEnabled,
                    onCheckedChange = { viewModel.handleWifiChange(it) },
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                        .padding(end = 20.dp)
                )
                Switch(
                    checked = data.bluetoothEnabled,
                    onCheckedChange = { viewModel.handleBluetoothChange(it) },
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                        .padding(end = 20.dp)
                )
                Switch(
                    checked = data.locationEnabled,
                    onCheckedChange = { viewModel.handleLocationChange(it) },
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                        .padding(end = 20.dp)
                )
                Text(
                    text = "${data.connectionStatus}",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(start = 20.dp)
                )
            }
            }
        }
    }    }
    // >>> GENERATED_CODE_END
}