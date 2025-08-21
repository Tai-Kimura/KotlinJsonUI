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

@Composable
fun IncludeTestGeneratedView(
    data: IncludeTestData,
    viewModel: IncludeTestViewModel
) {
    // Generated Compose code from include_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
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
                .padding(20.dp)
        ) {
            Button(
                onClick = { },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Button",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
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
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                )
                Column(
                    modifier = Modifier
                        .padding(15.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(android.graphics.Color.parseColor("#E8F4FD")))
                ) {
                    Text(
                        text = "Control Panel",
                        fontSize = 18.sp,
                        color = Color(android.graphics.Color.parseColor("#0066CC")),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                    )
                    Row(
                    ) {
                        Button(
                            onClick = { viewModel.incrementCount() },
                            modifier = Modifier.padding(10.dp),
                            shape = RoundedCornerShape(5.dp),
                            colors = ButtonDefaults.buttonColors(
                                                            containerColor = Color(android.graphics.Color.parseColor("#4CAF50"))
                                                        )
                        ) {
                            Text(
                                text = "Count +",
                                color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                            )
                        }
                        Button(
                            onClick = { viewModel.decrementCount() },
                            modifier = Modifier.padding(10.dp),
                            shape = RoundedCornerShape(5.dp),
                            colors = ButtonDefaults.buttonColors(
                                                            containerColor = Color(android.graphics.Color.parseColor("#FF9800"))
                                                        )
                        ) {
                            Text(
                                text = "Count -",
                                color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                            )
                        }
                        Button(
                            onClick = { viewModel.resetCount() },
                            modifier = Modifier.padding(10.dp),
                            shape = RoundedCornerShape(5.dp),
                            colors = ButtonDefaults.buttonColors(
                                                            containerColor = Color(android.graphics.Color.parseColor("#2196F3"))
                                                        )
                        ) {
                            Text(
                                text = "Reset",
                                color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                            )
                        }
                    }
                    Row(
                    ) {
                        Button(
                            onClick = { viewModel.changeUserName() },
                            modifier = Modifier.padding(10.dp),
                            shape = RoundedCornerShape(5.dp),
                            colors = ButtonDefaults.buttonColors(
                                                            containerColor = Color(android.graphics.Color.parseColor("#9C27B0"))
                                                        )
                        ) {
                            Text(
                                text = "Change Name",
                                color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                            )
                        }
                        Button(
                            onClick = { viewModel.toggleStatus() },
                            modifier = Modifier.padding(10.dp),
                            shape = RoundedCornerShape(5.dp),
                            colors = ButtonDefaults.buttonColors(
                                                            containerColor = Color(android.graphics.Color.parseColor("#607D8B"))
                                                        )
                        ) {
                            Text(
                                text = "Toggle Status",
                                color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
                    ) {
                        Text(
                            text = "Current Values: ",
                            fontSize = 14.sp,
                            color = Color(android.graphics.Color.parseColor("#333333")),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                        )
                        Text(
                            text = "${data.mainCount}",
                            fontSize = 14.sp,
                            color = Color(android.graphics.Color.parseColor("#666666")),
                            modifier = Modifier
                        )
                        Text(
                            text = "${data.userName}",
                            fontSize = 14.sp,
                            color = Color(android.graphics.Color.parseColor("#666666")),
                            modifier = Modifier
                        )
                        Text(
                            text = "${data.mainStatus}",
                            fontSize = 14.sp,
                            color = Color(android.graphics.Color.parseColor("#666666")),
                            modifier = Modifier
                        )
                    }
                }
                Column(
                ) {
                    Text(
                        text = "1. Basic Include with static data:",
                        fontSize = 16.sp,
                        color = Color(android.graphics.Color.parseColor("#666666")),
                        modifier = Modifier
                    )
                    Included1View(
                        viewModel = viewModel.included1ViewModel
                    )
                }
                Column(
                ) {
                    Text(
                        text = "2. Include with data (static values):",
                        fontSize = 16.sp,
                        color = Color(android.graphics.Color.parseColor("#666666")),
                        modifier = Modifier
                    )
                    Included2View(
                        viewModel = viewModel.included2ViewModel
                    )
                }
                Column(
                ) {
                    Text(
                        text = "3. Include with data (using @{} references):",
                        fontSize = 16.sp,
                        color = Color(android.graphics.Color.parseColor("#666666")),
                        modifier = Modifier
                    )
                    Included2View(
                        viewModel = viewModel.included2ViewModel
                    )
                }
                Column(
                ) {
                    Text(
                        text = "4. Include with shared_data and data override:",
                        fontSize = 16.sp,
                        color = Color(android.graphics.Color.parseColor("#666666")),
                        modifier = Modifier
                    )
                    Included2View(
                        viewModel = viewModel.included2ViewModel
                    )
                }
                Column(
                ) {
                    Text(
                        text = "5. Another included_1 with @{} references:",
                        fontSize = 16.sp,
                        color = Color(android.graphics.Color.parseColor("#666666")),
                        modifier = Modifier
                    )
                    Included1View(
                        viewModel = viewModel.included1ViewModel
                    )
                }
            }
        }
        }
    }    // >>> GENERATED_CODE_END
}