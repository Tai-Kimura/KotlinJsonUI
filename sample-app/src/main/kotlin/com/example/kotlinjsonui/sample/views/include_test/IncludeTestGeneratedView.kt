package com.example.kotlinjsonui.sample.views.include_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Application
import androidx.compose.ui.platform.LocalContext
import com.example.kotlinjsonui.sample.data.IncludeTestData
import com.example.kotlinjsonui.sample.viewmodels.IncludeTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.ButtonDefaults
import com.example.kotlinjsonui.sample.views.included1.Included1View
import com.example.kotlinjsonui.sample.data.Included1Data
import com.example.kotlinjsonui.sample.viewmodels.Included1ViewModel
import com.example.kotlinjsonui.sample.views.included2.Included2View
import com.example.kotlinjsonui.sample.data.Included2Data
import com.example.kotlinjsonui.sample.viewmodels.Included2ViewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.core.SafeDynamicView
import androidx.compose.ui.res.stringResource
import com.example.kotlinjsonui.sample.R
import androidx.compose.ui.res.colorResource

@Composable
fun IncludeTestGeneratedView(
    data: IncludeTestData,
    viewModel: IncludeTestViewModel
) {
    // Generated Compose code from include_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "include_test",
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
                android.util.Log.e("DynamicView", "Error loading include_test: \$error")
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
            .background(colorResource(R.color.white))
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                                    containerColor = colorResource(R.color.medium_blue_3)
                                )
            ) {
                Text(
                    text = "${data.dynamicModeStatus}",
                    fontSize = 14.sp,
                    color = colorResource(R.color.white),
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = "${data.title}",
                    fontSize = 24.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                )
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorResource(R.color.white_20))
                        .padding(15.dp)
                ) {
                    Text(
                        text = stringResource(R.string.include_test_control_panel),
                        fontSize = 18.sp,
                        color = colorResource(R.color.medium_blue_4),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                    )
                    Row(
                    ) {
                        Button(
                            onClick = { viewModel.incrementCount() },
                            shape = RoundedCornerShape(5.dp),
                            contentPadding = PaddingValues(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                                            containerColor = colorResource(R.color.medium_green_2)
                                                        )
                        ) {
                            Text(
                                text = "Count +",
                                color = colorResource(R.color.white),
                            )
                        }
                        Button(
                            onClick = { viewModel.decrementCount() },
                            shape = RoundedCornerShape(5.dp),
                            contentPadding = PaddingValues(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                                            containerColor = colorResource(R.color.medium_red_5)
                                                        )
                        ) {
                            Text(
                                text = stringResource(R.string.include_test_count),
                                color = colorResource(R.color.white),
                            )
                        }
                        Button(
                            onClick = { viewModel.resetCount() },
                            shape = RoundedCornerShape(5.dp),
                            contentPadding = PaddingValues(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                                            containerColor = colorResource(R.color.medium_blue_2)
                                                        )
                        ) {
                            Text(
                                text = stringResource(R.string.include_test_reset),
                                color = colorResource(R.color.white),
                            )
                        }
                    }
                    Row(
                    ) {
                        Button(
                            onClick = { viewModel.changeUserName() },
                            shape = RoundedCornerShape(5.dp),
                            contentPadding = PaddingValues(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                                            containerColor = colorResource(R.color.medium_purple)
                                                        )
                        ) {
                            Text(
                                text = stringResource(R.string.include_test_change_name),
                                color = colorResource(R.color.white),
                            )
                        }
                        Button(
                            onClick = { viewModel.toggleStatus() },
                            shape = RoundedCornerShape(5.dp),
                            contentPadding = PaddingValues(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                                            containerColor = colorResource(R.color.medium_cyan)
                                                        )
                        ) {
                            Text(
                                text = stringResource(R.string.include_test_toggle_status),
                                color = colorResource(R.color.white),
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(colorResource(R.color.white))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.include_test_current_values),
                            fontSize = 14.sp,
                            color = colorResource(R.color.dark_gray),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                        )
                        Text(
                            text = "${data.mainCount}",
                            fontSize = 14.sp,
                            color = colorResource(R.color.medium_gray_4),
                            modifier = Modifier
                        )
                        Text(
                            text = "${data.userName}",
                            fontSize = 14.sp,
                            color = colorResource(R.color.medium_gray_4),
                            modifier = Modifier
                        )
                        Text(
                            text = "${data.mainStatus}",
                            fontSize = 14.sp,
                            color = colorResource(R.color.medium_gray_4),
                            modifier = Modifier
                        )
                    }
                }
                Column(
                ) {
                    Text(
                        text = stringResource(R.string.include_test_1_basic_include_with_static_dat),
                        fontSize = 16.sp,
                        color = colorResource(R.color.medium_gray_4),
                        modifier = Modifier
                    )
                    val context = LocalContext.current
                    val included1Instance5 = remember { Included1ViewModel(context.applicationContext as Application) }

                    Included1View(
                        viewModel = included1Instance5
                    )
                }
                Column(
                ) {
                    Text(
                        text = stringResource(R.string.include_test_2_include_with_data_static_valu),
                        fontSize = 16.sp,
                        color = colorResource(R.color.medium_gray_4),
                        modifier = Modifier
                    )
                    val context = LocalContext.current
                    val included2Instance5 = remember { Included2ViewModel(context.applicationContext as Application) }

                    Included2View(
                        viewModel = included2Instance5
                    )
                }
                Column(
                ) {
                    Text(
                        text = stringResource(R.string.include_test_3_include_with_data_using_refer),
                        fontSize = 16.sp,
                        color = colorResource(R.color.medium_gray_4),
                        modifier = Modifier
                    )
                    val context = LocalContext.current
                    val included2Instance5 = remember { Included2ViewModel(context.applicationContext as Application) }

                    // Update included view when parent data changes
                    LaunchedEffect(data.userName, data.mainStatus, data.mainCount) {
                        val updates = mutableMapOf<String, Any>()
                        updates["viewTitle"] = data.userName
                        updates["viewStatus"] = data.mainStatus
                        updates["viewCount"] = data.mainCount
                        included2Instance5.updateData(updates)
                    }
                    Included2View(
                        viewModel = included2Instance5
                    )
                }
                Column(
                ) {
                    Text(
                        text = stringResource(R.string.include_test_4_include_with_shareddata_and_d),
                        fontSize = 16.sp,
                        color = colorResource(R.color.medium_gray_4),
                        modifier = Modifier
                    )
                    val context = LocalContext.current
                    val included2Instance5 = remember { Included2ViewModel(context.applicationContext as Application) }

                    // Update included view when parent data changes
                    LaunchedEffect(data.userName, data.mainStatus, data.mainCount) {
                        val updates = mutableMapOf<String, Any>()
                        updates["viewStatus"] = "Overridden Status"
                        // Shared data for two-way binding
                        updates["viewTitle"] = data.userName
                        updates["viewStatus"] = data.mainStatus
                        updates["viewCount"] = data.mainCount
                        included2Instance5.updateData(updates)
                    }
                    Included2View(
                        viewModel = included2Instance5
                    )
                }
                Column(
                ) {
                    Text(
                        text = stringResource(R.string.include_test_5_another_included1_with_refere),
                        fontSize = 16.sp,
                        color = colorResource(R.color.medium_gray_4),
                        modifier = Modifier
                    )
                    val context = LocalContext.current
                    val included1Instance5 = remember { Included1ViewModel(context.applicationContext as Application) }

                    // Update included view when parent data changes
                    LaunchedEffect(data.userName, data.mainStatus, data.mainCount) {
                        val updates = mutableMapOf<String, Any>()
                        updates["title"] = data.userName
                        updates["message"] = data.mainStatus
                        updates["count"] = data.mainCount
                        included1Instance5.updateData(updates)
                    }
                    Included1View(
                        viewModel = included1Instance5
                    )
                }
            }
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}