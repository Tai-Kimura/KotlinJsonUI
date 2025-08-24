package com.example.kotlinjsonui.sample.views.weight_test_with_fixed

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.WeightTestWithFixedData
import com.example.kotlinjsonui.sample.viewmodels.WeightTestWithFixedViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box

@Composable
fun WeightTestWithFixedGeneratedView(
    data: WeightTestWithFixedData,
    viewModel: WeightTestWithFixedViewModel
) {
    // Generated Compose code from weight_test_with_fixed.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "weight_test_with_fixed",
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
                android.util.Log.e("DynamicView", "Error loading weight_test_with_fixed: \$error")
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
                    .height(32.dp),
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
                    .padding(top = 20.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Fixed(80) + Weight(1) + Weight(2) + Fixed(60)",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = "Fixed: 80",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#FFD0D0")))
                        .width(80.dp)
                        .fillMaxHeight(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Weight: 1",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(android.graphics.Color.parseColor("#D0FFD0")))
                        .fillMaxHeight(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Weight: 2",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .weight(2f)
                        .background(Color(android.graphics.Color.parseColor("#D0D0FF")))
                        .fillMaxHeight(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Fixed: 60",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#FFFFD0")))
                        .width(60.dp)
                        .fillMaxHeight(),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = "Complex nested weights with fixed",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier
                    .padding(top = 30.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Text(
                        text = "1-1",
                        fontSize = 12.sp,
                        color = Color(android.graphics.Color.parseColor("#000000")),
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(android.graphics.Color.parseColor("#FFE0E0")))
                            .fillMaxWidth()
                            .height(0.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "1-2",
                        fontSize = 12.sp,
                        color = Color(android.graphics.Color.parseColor("#000000")),
                        modifier = Modifier
                            .weight(2f)
                            .background(Color(android.graphics.Color.parseColor("#FFC0C0")))
                            .fillMaxWidth()
                            .height(0.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = "Fix\n50",
                    fontSize = 12.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
                        .width(50.dp)
                        .fillMaxHeight(),
                    textAlign = TextAlign.Center
                )
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                ) {
                    Text(
                        text = "Fixed 30",
                        fontSize = 12.sp,
                        color = Color(android.graphics.Color.parseColor("#000000")),
                        modifier = Modifier
                            .background(Color(android.graphics.Color.parseColor("#E0FFE0")))
                            .fillMaxWidth()
                            .height(30.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Weight 1",
                        fontSize = 12.sp,
                        color = Color(android.graphics.Color.parseColor("#000000")),
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(android.graphics.Color.parseColor("#C0FFC0")))
                            .fillMaxWidth()
                            .height(0.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Text(
                text = "Multiple fixed sizes with weights",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier
                    .padding(top = 30.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = "40",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#FFD0D0")))
                        .width(40.dp)
                        .fillMaxHeight(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "W:1",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(android.graphics.Color.parseColor("#D0FFD0")))
                        .fillMaxHeight(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "60",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#FFD0D0")))
                        .width(60.dp)
                        .fillMaxHeight(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "W:1",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(android.graphics.Color.parseColor("#D0FFD0")))
                        .fillMaxHeight(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "80",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#FFD0D0")))
                        .width(80.dp)
                        .fillMaxHeight(),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = "Zero weights test",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier
                    .padding(top = 30.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = "W:0",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#FFA0A0")))
                        .fillMaxHeight(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "W:1",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(android.graphics.Color.parseColor("#A0FFA0")))
                        .fillMaxHeight(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Fixed:100",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#A0A0FF")))
                        .width(100.dp)
                        .fillMaxHeight(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "W:3",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .weight(3f)
                        .background(Color(android.graphics.Color.parseColor("#FFFFA0")))
                        .fillMaxHeight(),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = "Vertical: Fixed + Weight combinations",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier
                    .padding(top = 30.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = "Fixed: 30",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#FFE0E0")))
                        .fillMaxWidth()
                        .height(30.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Weight: 1",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(android.graphics.Color.parseColor("#E0FFE0")))
                        .fillMaxWidth()
                        .height(0.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Fixed: 40",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#E0E0FF")))
                        .fillMaxWidth()
                        .height(40.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Weight: 2",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .weight(2f)
                        .background(Color(android.graphics.Color.parseColor("#FFFFE0")))
                        .fillMaxWidth()
                        .height(0.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Fixed: 50",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#FFE0FF")))
                        .fillMaxWidth()
                        .height(50.dp),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = "Vertical: Nested horizontal weights",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier
                    .padding(top = 30.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = "Header: Fixed 25",
                    fontSize = 12.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#666666")))
                        .fillMaxWidth()
                        .height(25.dp),
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .height(0.dp)
                ) {
                    Text(
                        text = "Col 1",
                        fontSize = 12.sp,
                        color = Color(android.graphics.Color.parseColor("#000000")),
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(android.graphics.Color.parseColor("#FFD0D0")))
                            .fillMaxHeight(),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Fix:60",
                        fontSize = 12.sp,
                        color = Color(android.graphics.Color.parseColor("#000000")),
                        modifier = Modifier
                            .background(Color(android.graphics.Color.parseColor("#D0D0D0")))
                            .width(60.dp)
                            .fillMaxHeight(),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Col 2",
                        fontSize = 12.sp,
                        color = Color(android.graphics.Color.parseColor("#000000")),
                        modifier = Modifier
                            .weight(2f)
                            .background(Color(android.graphics.Color.parseColor("#D0FFD0")))
                            .fillMaxHeight(),
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = "Footer: Fixed 25",
                    fontSize = 12.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#666666")))
                        .fillMaxWidth()
                        .height(25.dp),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = "Vertical: Multiple weights only",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier
                    .padding(top = 30.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = "W:1",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(android.graphics.Color.parseColor("#FFCCCC")))
                        .fillMaxWidth()
                        .height(0.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "W:1",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(android.graphics.Color.parseColor("#CCFFCC")))
                        .fillMaxWidth()
                        .height(0.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "W:2",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .weight(2f)
                        .background(Color(android.graphics.Color.parseColor("#CCCCFF")))
                        .fillMaxWidth()
                        .height(0.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "W:1",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(android.graphics.Color.parseColor("#FFFFCC")))
                        .fillMaxWidth()
                        .height(0.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}