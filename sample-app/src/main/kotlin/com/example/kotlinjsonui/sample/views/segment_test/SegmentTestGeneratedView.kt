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

@Composable
fun SegmentTestGeneratedView(
    data: SegmentTestData,
    viewModel: SegmentTestViewModel
) {
    // Generated Compose code from segment_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
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
                TabRow(
                    selectedTabIndex = 0,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                        .padding(end = 20.dp)
                ) {
                    Tab(
                        selected = (0 == 0),
                        onClick = {
                            // Static selected index
                        },
                        text = { Text("Option 1") }
                    )
                    Tab(
                        selected = (0 == 1),
                        onClick = {
                            // Static selected index
                        },
                        text = { Text("Option 2") }
                    )
                    Tab(
                        selected = (0 == 2),
                        onClick = {
                            // Static selected index
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
                TabRow(
                    selectedTabIndex = 1,
                    contentColor = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                        .padding(end = 20.dp)
                ) {
                    Tab(
                        selected = (1 == 0),
                        onClick = {
                            // Static selected index
                        },
                        text = { Text("Red") }
                    )
                    Tab(
                        selected = (1 == 1),
                        onClick = {
                            // Static selected index
                        },
                        text = { Text("Green") }
                    )
                    Tab(
                        selected = (1 == 2),
                        onClick = {
                            // Static selected index
                        },
                        text = { Text("Blue") }
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
                TabRow(
                    selectedTabIndex = 1,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                        .padding(end = 20.dp)
                ) {
                    Tab(
                        selected = (1 == 0),
                        onClick = {
                            // Static selected index
                        },
                        text = { Text("Small") }
                    )
                    Tab(
                        selected = (1 == 1),
                        onClick = {
                            // Static selected index
                        },
                        text = { Text("Medium") }
                    )
                    Tab(
                        selected = (1 == 2),
                        onClick = {
                            // Static selected index
                        },
                        text = { Text("Large") }
                    )
                    Tab(
                        selected = (1 == 3),
                        onClick = {
                            // Static selected index
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
                TabRow(
                    selectedTabIndex = 2,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                        .padding(end = 20.dp)
                ) {
                    Tab(
                        selected = (2 == 0),
                        onClick = {
                            // Static selected index
                        },
                        text = { Text("Disabled 1") }
                    )
                    Tab(
                        selected = (2 == 1),
                        onClick = {
                            // Static selected index
                        },
                        text = { Text("Disabled 2") }
                    )
                    Tab(
                        selected = (2 == 2),
                        onClick = {
                            // Static selected index
                        },
                        text = { Text("Disabled 3") }
                    )
                }
            }
            }
        }
    }    // >>> GENERATED_CODE_END
}