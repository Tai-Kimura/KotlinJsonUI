package com.example.kotlinjsonui.sample.views.alignment_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.AlignmentTestData
import com.example.kotlinjsonui.sample.viewmodels.AlignmentTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.BiasAlignment
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
fun AlignmentTestGeneratedView(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel
) {
    // Generated Compose code from alignment_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "alignment_test",
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
                android.util.Log.e("DynamicView", "Error loading alignment_test: \$error")
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
                .padding(20.dp)
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
                    .align(Alignment.CenterHorizontally)
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(bottom = 20.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Parent Alignment - Single Properties",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
            ) {
                Text(
                    text = "alignTop",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .align(BiasAlignment(-1f, -1f))
                        .background(Color(android.graphics.Color.parseColor("#FFD0D0")))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#D8D8D8")))
            ) {
                Text(
                    text = "alignBottom",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .align(BiasAlignment(-1f, 1f))
                        .background(Color(android.graphics.Color.parseColor("#D0FFD0")))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#D0D0D0")))
            ) {
                Text(
                    text = "alignLeft",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .align(BiasAlignment(-1f, -1f))
                        .background(Color(android.graphics.Color.parseColor("#D0D0FF")))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#C8C8C8")))
            ) {
                Text(
                    text = "alignRight",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .align(BiasAlignment(1f, -1f))
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#FFFFD0")))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#C0C0C0")))
            ) {
                Text(
                    text = "centerHorizontal",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .align(BiasAlignment(0f, -1f))
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#FFD0FF")))
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#B8B8B8")))
            ) {
                Text(
                    text = "centerVertical",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .align(BiasAlignment(-1f, 0f))
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#D0FFFF")))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#B0B0B0")))
            ) {
                Text(
                    text = "centerInParent",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(Color(android.graphics.Color.parseColor("#FFCCCC")))
                        .padding(8.dp)
                )
            }
            Text(
                text = "HStack Alignment Tests",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(bottom = 10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#A8A8A8")))
            ) {
                Text(
                    text = "Top",
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .align(Alignment.Top)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#FFE0E0")))
                        .padding(8.dp)
                )
                Text(
                    text = "Default",
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
                        .padding(8.dp)
                )
                Text(
                    text = "Bottom",
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#E0E0FF")))
                        .padding(8.dp)
                )
                Text(
                    text = "Center",
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#E0FFE0")))
                        .padding(8.dp)
                )
            }
            Text(
                text = "VStack Alignment Tests",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(bottom = 10.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(android.graphics.Color.parseColor("#A0A0A0")))
            ) {
                Text(
                    text = "alignLeft",
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#FFE0E0")))
                        .padding(8.dp)
                )
                Text(
                    text = "Default",
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
                        .padding(8.dp)
                )
                Text(
                    text = "alignRight",
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .align(Alignment.End)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#E0E0FF")))
                        .padding(8.dp)
                )
                Text(
                    text = "centerHorizontal",
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#E0FFE0")))
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}