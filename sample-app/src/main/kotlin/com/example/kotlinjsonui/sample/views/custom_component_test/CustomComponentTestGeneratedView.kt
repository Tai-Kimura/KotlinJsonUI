package com.example.kotlinjsonui.sample.views.custom_component_test

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
import com.example.kotlinjsonui.sample.data.CustomComponentTestData
import com.example.kotlinjsonui.sample.viewmodels.CustomComponentTestViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.SafeDynamicView
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.blur
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.kotlinjsonui.sample.R
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.border
import com.example.kotlinjsonui.sample.extensions.SampleCard
import com.example.kotlinjsonui.sample.extensions.StatusBadge

@Composable
fun CustomComponentTestGeneratedView(
    data: CustomComponentTestData,
    viewModel: CustomComponentTestViewModel
) {
    // Generated Compose code from custom_component_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "custom_component_test",
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
                android.util.Log.e("DynamicView", "Error loading custom_component_test: \$error")
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
            .background(Color(android.graphics.Color.parseColor("#F5F5F7")))
    ) {
        LazyColumn(
            modifier = Modifier.padding(16.dp)
        ) {
            item {
            Column(
            ) {
                Text(
                    text = "Custom Component Test",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 0.dp, end = 0.dp, bottom = 20.dp, start = 0.dp)
                )
                Text(
                    text = "Testing SampleCard - Static Values",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 0.dp, end = 0.dp, bottom = 8.dp, start = 0.dp)
                )
                SampleCard(
                    title = "Static Title",
                    subtitle = "This is a static subtitle",
                    count = 42,
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(android.graphics.Color.parseColor("#E0E0E0")), RoundedCornerShape(12.dp))
                        .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
                ) {
                    Column(
                    ) {
                        Text(
                            text = "Card Content",
                            fontSize = 14.sp,
                            modifier = Modifier
                        )
                        Text(
                            text = "This content is inside the custom component",
                            fontSize = 12.sp,
                            modifier = Modifier
                        )
                    }
                }
                Text(
                    text = "Testing SampleCard - Dynamic Values",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 20.dp, end = 0.dp, bottom = 8.dp, start = 0.dp)
                )
                SampleCard(
                    title = data.cardTitle,
                    subtitle = data.cardSubtitle,
                    count = data.itemCount,
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(android.graphics.Color.parseColor("#E0E0E0")), RoundedCornerShape(12.dp))
                        .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
                ) {
                    Column(
                    ) {
                        Text(
                            text = "${data.itemCount}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                        )
                        Row(
                        ) {
                            Button(
                                onClick = { },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                                colors = ButtonDefaults.buttonColors(
                                                                    containerColor = Color(android.graphics.Color.parseColor("#5856D6"))
                                                                )
                            ) {
                                Text("Increment")
                            }
                            Button(
                                onClick = { },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                                colors = ButtonDefaults.buttonColors(
                                                                    containerColor = Color(android.graphics.Color.parseColor("#FF3B30"))
                                                                )
                            ) {
                                Text("Decrement")
                            }
                        }
                    }
                }
                Text(
                    text = "Testing Multiple Cards",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 20.dp, end = 0.dp, bottom = 8.dp, start = 0.dp)
                )
                Column(
                ) {
                    SampleCard(
                        title = "Card 1",
                        subtitle = "First custom card",
                        count = 1,
                        modifier = Modifier
                            .padding(12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(android.graphics.Color.parseColor("#E8F5E9")))
                    ) {
                        Text(
                            text = "Content for card 1",
                            fontSize = 14.sp,
                            modifier = Modifier
                        )
                    }
                    SampleCard(
                        title = "Card 2",
                        subtitle = "Second custom card",
                        count = 2,
                        modifier = Modifier
                            .padding(12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(android.graphics.Color.parseColor("#FFF3E0")))
                    ) {
                        Text(
                            text = "Content for card 2",
                            fontSize = 14.sp,
                            modifier = Modifier
                        )
                    }
                    SampleCard(
                        title = "Card 3",
                        subtitle = "Third custom card",
                        count = 3,
                        modifier = Modifier
                            .padding(12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(android.graphics.Color.parseColor("#F3E5F5")))
                    ) {
                        Text(
                            text = "Content for card 3",
                            fontSize = 14.sp,
                            modifier = Modifier
                        )
                    }
                }
                Text(
                    text = "Testing StatusBadge - Non-Container Component",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 20.dp, end = 0.dp, bottom = 8.dp, start = 0.dp)
                )
                Column(
                ) {
                    StatusBadge(
                        title = "Static Status",
                        status = "Active",
                        color = Color(android.graphics.Color.parseColor("#4CAF50")),
                        count = 5
                    )
                    StatusBadge(
                        title = "Dynamic Status",
                        status = data.currentStatus,
                        color = data.statusColor,
                        count = data.notificationCount
                    )
                    StatusBadge(
                        title = "Error Status",
                        status = "Error",
                        color = Color(android.graphics.Color.parseColor("#FF3B30")),
                        count = 0
                    )
                }
                Button(
                    onClick = { },
                    modifier = Modifier.width(100%.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(android.graphics.Color.parseColor("#5856D6"))
                                        )
                ) {
                    Text("Toggle Dynamic Mode")
                }
                Text(
                    text = "${data.dynamicModeStatus}",
                    fontSize = 14.sp,
                    modifier = Modifier,
                    textAlign = TextAlign.Center
                )
            }
            }
        }
    }    }
    // >>> GENERATED_CODE_END
}