package com.example.kotlinjsonui.sample.views.segment_test

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
import com.example.kotlinjsonui.sample.data.SegmentTestData
import com.example.kotlinjsonui.sample.viewmodels.SegmentTestViewModel
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import com.kotlinjsonui.components.Segment
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box

@Composable
fun SegmentTestGeneratedView(
    data: SegmentTestData,
    viewModel: SegmentTestViewModel
) {
    // Generated Compose code from segment_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "segment_test",
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
                android.util.Log.e("DynamicView", "Error loading segment_test: \$error")
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
                    text = "Segment Control Test",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp)
                        .padding(bottom = 20.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Basic Segment",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                )
                Segment(
                    selectedTabIndex = data.selectedBasic,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                        .padding(end = 20.dp)
                ) {
                    Tab(
                        selected = (data.selectedBasic == 0),
                        onClick = {
                            viewModel.updateData(mapOf("selectedBasic" to 0))
                        },
                        text = { Text("Option 1") }
                    )
                    Tab(
                        selected = (data.selectedBasic == 1),
                        onClick = {
                            viewModel.updateData(mapOf("selectedBasic" to 1))
                        },
                        text = { Text("Option 2") }
                    )
                    Tab(
                        selected = (data.selectedBasic == 2),
                        onClick = {
                            viewModel.updateData(mapOf("selectedBasic" to 2))
                        },
                        text = { Text("Option 3") }
                    )
                }
                Text(
                    text = "Segment with Custom Colors",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .padding(start = 20.dp)
                )
                Segment(
                    selectedTabIndex = data.selectedColor,
                    contentColor = Color(android.graphics.Color.parseColor("#666666")),
                    selectedContentColor = Color(android.graphics.Color.parseColor("#FF0000")),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                        .padding(end = 20.dp)
                ) {
                    Tab(
                        selected = (data.selectedColor == 0),
                        onClick = {
                            viewModel.updateData(mapOf("selectedColor" to 0))
                        },
                        text = {
                            Text(
                                "Red",
                                color = if (data.selectedColor == 0) Color(android.graphics.Color.parseColor("#FF0000")) else Color(android.graphics.Color.parseColor("#666666"))
                            )
                        }
                    )
                    Tab(
                        selected = (data.selectedColor == 1),
                        onClick = {
                            viewModel.updateData(mapOf("selectedColor" to 1))
                        },
                        text = {
                            Text(
                                "Green",
                                color = if (data.selectedColor == 1) Color(android.graphics.Color.parseColor("#FF0000")) else Color(android.graphics.Color.parseColor("#666666"))
                            )
                        }
                    )
                    Tab(
                        selected = (data.selectedColor == 2),
                        onClick = {
                            viewModel.updateData(mapOf("selectedColor" to 2))
                        },
                        text = {
                            Text(
                                "Blue",
                                color = if (data.selectedColor == 2) Color(android.graphics.Color.parseColor("#FF0000")) else Color(android.graphics.Color.parseColor("#666666"))
                            )
                        }
                    )
                }
                Text(
                    text = "Segment with onChange Event",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .padding(start = 20.dp)
                )
                Segment(
                    selectedTabIndex = data.selectedEvent,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                        .padding(end = 20.dp)
                ) {
                    Tab(
                        selected = (data.selectedEvent == 0),
                        onClick = {
                            viewModel.handleSegmentChange(0)
                        },
                        text = { Text("Small") }
                    )
                    Tab(
                        selected = (data.selectedEvent == 1),
                        onClick = {
                            viewModel.handleSegmentChange(1)
                        },
                        text = { Text("Medium") }
                    )
                    Tab(
                        selected = (data.selectedEvent == 2),
                        onClick = {
                            viewModel.handleSegmentChange(2)
                        },
                        text = { Text("Large") }
                    )
                    Tab(
                        selected = (data.selectedEvent == 3),
                        onClick = {
                            viewModel.handleSegmentChange(3)
                        },
                        text = { Text("Extra Large") }
                    )
                }
                Text(
                    text = "${data.selectedSize}",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                )
                Text(
                    text = "Disabled Segment",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .padding(start = 20.dp)
                )
                Segment(
                    selectedTabIndex = data.selectedDisabled,
                    enabled = false,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                        .padding(end = 20.dp)
                ) {
                    Tab(
                        selected = (data.selectedDisabled == 0),
                        enabled = false,
                        onClick = {
                            viewModel.updateData(mapOf("selectedDisabled" to 0))
                        },
                        text = { Text("Disabled 1") }
                    )
                    Tab(
                        selected = (data.selectedDisabled == 1),
                        enabled = false,
                        onClick = {
                            viewModel.updateData(mapOf("selectedDisabled" to 1))
                        },
                        text = { Text("Disabled 2") }
                    )
                    Tab(
                        selected = (data.selectedDisabled == 2),
                        enabled = false,
                        onClick = {
                            viewModel.updateData(mapOf("selectedDisabled" to 2))
                        },
                        text = { Text("Disabled 3") }
                    )
                }
            }
            }
        }
    }    }
    // >>> GENERATED_CODE_END
}