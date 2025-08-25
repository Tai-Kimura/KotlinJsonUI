package com.example.kotlinjsonui.sample.views.scroll_test

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
import com.example.kotlinjsonui.sample.data.ScrollTestData
import com.example.kotlinjsonui.sample.viewmodels.ScrollTestViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.core.SafeDynamicView

@Composable
fun ScrollTestGeneratedView(
    data: ScrollTestData,
    viewModel: ScrollTestViewModel
) {
    // Generated Compose code from scroll_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "scroll_test",
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
                android.util.Log.e("DynamicView", "Error loading scroll_test: \$error")
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
        modifier = Modifier.background(Color(android.graphics.Color.parseColor("#FFFFFF")))
    ) {
        Text(
            text = "ScrollView Test",
            fontSize = 20.sp,
            color = Color(android.graphics.Color.parseColor("#000000")),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(10.dp)
        )
        LazyColumn(
            modifier = Modifier
                .height(200.dp)
                .background(Color(android.graphics.Color.parseColor("#F0F0F0")))
        ) {
            item {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = "No vertical indicator",
                    fontSize = 16.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                )
                Text(
                    text = "Item 1",
                    fontSize = 16.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#FFE0E0")))
                        .padding(10.dp)
                )
                Text(
                    text = "Item 2",
                    fontSize = 16.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#E0FFE0")))
                        .padding(10.dp)
                )
                Text(
                    text = "Item 3",
                    fontSize = 16.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#E0E0FF")))
                        .padding(10.dp)
                )
                Text(
                    text = "Item 4",
                    fontSize = 16.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#FFFFE0")))
                        .padding(10.dp)
                )
                Text(
                    text = "Item 5",
                    fontSize = 16.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#FFE0FF")))
                        .padding(10.dp)
                )
            }
            }
        }
        LazyColumn(
            modifier = Modifier
                .height(150.dp)
                .background(Color(android.graphics.Color.parseColor("#F0F0F0")))
        ) {
            item {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = "Scroll Disabled",
                    fontSize = 16.sp,
                    color = Color(android.graphics.Color.parseColor("#FF0000")),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                )
                Text(
                    text = "This ScrollView cannot be scrolled",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                )
                Text(
                    text = "Item 1",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                )
                Text(
                    text = "Item 2",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                )
                Text(
                    text = "Item 3 (Hidden below)",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                )
            }
            }
        }
        LazyRow(
            modifier = Modifier
                .height(100.dp)
                .background(Color(android.graphics.Color.parseColor("#F0F0F0")))
        ) {
            item {
            Row(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = "Horizontal Scroll Item 1",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#FFE0E0")))
                        .padding(10.dp)
                        .width(150.dp)
                )
                Text(
                    text = "Horizontal Scroll Item 2",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#E0FFE0")))
                        .padding(10.dp)
                        .width(150.dp)
                )
                Text(
                    text = "Horizontal Scroll Item 3",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#E0E0FF")))
                        .padding(10.dp)
                        .width(150.dp)
                )
                Text(
                    text = "Horizontal Scroll Item 4",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#FFFFE0")))
                        .padding(10.dp)
                        .width(150.dp)
                )
                Text(
                    text = "Horizontal Scroll Item 5",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#FFE0FF")))
                        .padding(10.dp)
                        .width(150.dp)
                )
                Text(
                    text = "Horizontal Scroll Item 6",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#E0FFFF")))
                        .padding(10.dp)
                        .width(150.dp)
                )
                Text(
                    text = "Horizontal Scroll Item 7",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#FFF0E0")))
                        .padding(10.dp)
                        .width(150.dp)
                )
                Text(
                    text = "Horizontal Scroll Item 8 (End)",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#F0E0FF")))
                        .padding(10.dp)
                        .width(150.dp)
                )
            }
            }
        }
    }    }
    // >>> GENERATED_CODE_END
}