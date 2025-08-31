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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.colorResource

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
            .background(colorResource(R.color.white_12))
    ) {
        LazyColumn(
            modifier = Modifier.padding(16.dp)
        ) {
            item {
            Column(
            ) {
                Text(
                    text = stringResource(R.string.custom_component_test_custom_component_test),
                    fontSize = 28.sp,
                    color = colorResource(R.color.black),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 0.dp, end = 0.dp, bottom = 20.dp, start = 0.dp)
                )
                Text(
                    text = stringResource(R.string.custom_component_test_testing_samplecard_static_value),
                    fontSize = 18.sp,
                    color = colorResource(R.color.dark_gray),
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
                        .border(1.dp, colorResource(R.color.pale_gray), RoundedCornerShape(12.dp))
                        .background(colorResource(R.color.white))
                ) {
                    Column(
                    ) {
                        Text(
                            text = stringResource(R.string.custom_component_test_card_content),
                            fontSize = 14.sp,
                            color = colorResource(R.color.medium_gray_4),
                            modifier = Modifier
                        )
                        Text(
                            text = stringResource(R.string.custom_component_test_this_content_is_inside_the_cust),
                            fontSize = 12.sp,
                            color = colorResource(R.color.light_gray_8),
                            modifier = Modifier
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.custom_component_test_testing_samplecard_dynamic_valu),
                    fontSize = 18.sp,
                    color = colorResource(R.color.dark_gray),
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
                        .border(1.dp, colorResource(R.color.pale_gray), RoundedCornerShape(12.dp))
                        .background(colorResource(R.color.white))
                ) {
                    Column(
                    ) {
                        Text(
                            text = "${data.itemCount}",
                            fontSize = 16.sp,
                            color = colorResource(R.color.medium_blue_3),
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
                                                                    containerColor = colorResource(R.color.medium_blue_3)
                                                                )
                            ) {
                                Text(
                                    text = stringResource(R.string.custom_component_test_increment),
                                    color = colorResource(R.color.white),
                                )
                            }
                            Button(
                                onClick = { },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                                colors = ButtonDefaults.buttonColors(
                                                                    containerColor = colorResource(R.color.medium_red)
                                                                )
                            ) {
                                Text(
                                    text = stringResource(R.string.custom_component_test_decrement),
                                    color = colorResource(R.color.white),
                                )
                            }
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.custom_component_test_testing_multiple_cards),
                    fontSize = 18.sp,
                    color = colorResource(R.color.dark_gray),
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
                            .background(colorResource(R.color.white_13))
                    ) {
                        Text(
                            text = stringResource(R.string.custom_component_test_content_for_card_1),
                            fontSize = 14.sp,
                            color = colorResource(R.color.dark_green),
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
                            .background(colorResource(R.color.white_14))
                    ) {
                        Text(
                            text = stringResource(R.string.custom_component_test_content_for_card_2),
                            fontSize = 14.sp,
                            color = colorResource(R.color.medium_red_4),
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
                            .background(colorResource(R.color.white_15))
                    ) {
                        Text(
                            text = stringResource(R.string.custom_component_test_content_for_card_3),
                            fontSize = 14.sp,
                            color = colorResource(R.color.dark_purple),
                            modifier = Modifier
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.custom_component_test_testing_statusbadge_noncontaine),
                    fontSize = 18.sp,
                    color = colorResource(R.color.dark_gray),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 20.dp, end = 0.dp, bottom = 8.dp, start = 0.dp)
                )
                Column(
                ) {
                    StatusBadge(
                        title = "Static Status",
                        status = "Active",
                        color = Color.medium_green_2,
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
                        color = Color.medium_red,
                        count = 0
                    )
                }
                Button(
                    onClick = { },
                    modifier = Modifier.wrapContentWidth(),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_blue_3)
                                        )
                ) {
                    Text(
                        text = stringResource(R.string.custom_component_test_toggle_dynamic_mode),
                        color = colorResource(R.color.white),
                    )
                }
                Text(
                    text = "${data.dynamicModeStatus}",
                    fontSize = 14.sp,
                    color = colorResource(R.color.medium_gray_4),
                    modifier = Modifier,
                    textAlign = TextAlign.Center
                )
            }
            }
        }
    }    }
    // >>> GENERATED_CODE_END
}