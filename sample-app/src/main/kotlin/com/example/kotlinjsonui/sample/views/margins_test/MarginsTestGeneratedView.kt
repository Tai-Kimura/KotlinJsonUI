package com.example.kotlinjsonui.sample.views.margins_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.MarginsTestData
import com.example.kotlinjsonui.sample.viewmodels.MarginsTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.core.SafeDynamicView

@Composable
fun MarginsTestGeneratedView(
    data: MarginsTestData,
    viewModel: MarginsTestViewModel
) {
    // Generated Compose code from margins_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "margins_test",
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
                android.util.Log.e("DynamicView", "Error loading margins_test: \$error")
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
        LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Button(
                onClick = { viewModel.toggleDynamicMode() },
                modifier = Modifier
                    .wrapContentWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#5856D6"))
                                )
            ) {
                Text(
                    text = "Dynamic: ${data.dynamicModeStatus}",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Text(
                text = "${data.title}",
                fontSize = 24.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 20.dp)
            )
            Text(
                text = "All margins: [20, 20, 20, 20]",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 20.dp, end = 20.dp, bottom = 20.dp, start = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFE0E0")))
            )
            Text(
                text = "Left margin: 40",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 10.dp)
                    .padding(start = 40.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0FFE0")))
            )
            Text(
                text = "Right margin: 40",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 10.dp)
                    .padding(end = 40.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0E0FF")))
            )
            Text(
                text = "Top margin: 30",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 30.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFFFE0")))
            )
            Text(
                text = "Bottom margin: 30",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(bottom = 30.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFE0FF")))
            )
            Text(
                text = "Label with padding: 20",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0FFFF")))
                    .padding(20.dp)
            )
            Text(
                text = "Label with leftPadding: 30, rightPadding: 30",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFCCCC")))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#DDDDDD")))
            ) {
                Text(
                    text = "Parent has leftPadding: 20, rightPadding: 20",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#CCCCCC")))
            ) {
                Text(
                    text = "MaxWidth: 200",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .widthIn(max = 200.dp)
                        .background(Color(android.graphics.Color.parseColor("#FFCCCC")))
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#BBBBBB")))
            ) {
                Text(
                    text = "MinWidth: 150",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .widthIn(min = 150.dp)
                        .background(Color(android.graphics.Color.parseColor("#CCFFCC")))
                )
            }
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}