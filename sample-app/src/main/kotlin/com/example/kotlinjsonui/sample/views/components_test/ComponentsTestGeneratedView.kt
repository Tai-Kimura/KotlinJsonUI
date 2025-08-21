package com.example.kotlinjsonui.sample.views.components_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.ComponentsTestData
import com.example.kotlinjsonui.sample.viewmodels.ComponentsTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.blur
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues

@Composable
fun ComponentsTestGeneratedView(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    // Generated Compose code from components_test.json
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
                .wrapContentHeight()
                .padding(top = 20.dp, end = 20.dp, bottom = 20.dp, start = 20.dp)
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
                    text = "Dynamic: \${data.dynamicModeStatus}",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Text(
                text = "New Components Test",
                fontSize = 24.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier
            )
            Text(
                text = "Toggle/Checkbox Components",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier
            )
            Switch(
                checked = false,
                onCheckedChange = { },
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = false,
                    onCheckedChange = { }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("I agree to terms")
            }
            Text(
                text = "Progress & Slider",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier
            )
            LinearProgressIndicator(
            )
            Slider(
                value = 0f,
                onValueChange = { },
                valueRange = 0f..100f,
            )
            Text(
                text = "Selection Components",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier
            )
            TabRow(
                selectedTabIndex = data.selectedSegment1,
            ) {
                Tab(
                    selected = (data.selectedSegment1 == 0),
                    onClick = {
                        viewModel.updateData(mapOf("selectedSegment1" to 0))
                    },
                    text = { Text("List") }
                )
                Tab(
                    selected = (data.selectedSegment1 == 1),
                    onClick = {
                        viewModel.updateData(mapOf("selectedSegment1" to 1))
                    },
                    text = { Text("Grid") }
                )
                Tab(
                    selected = (data.selectedSegment1 == 2),
                    onClick = {
                        viewModel.updateData(mapOf("selectedSegment1" to 2))
                    },
                    text = { Text("Map") }
                )
            }
            Column(
            ) {
                Text("Select Size")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.updateData(mapOf("selectedRadio1" to "Small"))
                        }
                ) {
                    RadioButton(
                        selected = data.selectedRadio1 == "Small",
                        onClick = {
                            viewModel.updateData(mapOf("selectedRadio1" to "Small"))
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Small")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.updateData(mapOf("selectedRadio1" to "Medium"))
                        }
                ) {
                    RadioButton(
                        selected = data.selectedRadio1 == "Medium",
                        onClick = {
                            viewModel.updateData(mapOf("selectedRadio1" to "Medium"))
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Medium")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.updateData(mapOf("selectedRadio1" to "Large"))
                        }
                ) {
                    RadioButton(
                        selected = data.selectedRadio1 == "Large",
                        onClick = {
                            viewModel.updateData(mapOf("selectedRadio1" to "Large"))
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Large")
                }
            }
            Text(
                text = "Loading Indicator",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier
            )
            CircularProgressIndicator(
            )
            Text(
                text = "Circle Image",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier
            )
            AsyncImage(
                model = "person.circle.fill",
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Text(
                text = "Gradient View",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Brush.horizontalGradient(listOf(Color(android.graphics.Color.parseColor("#FF6B6B")), Color(android.graphics.Color.parseColor("#4ECDC4")))))
                    .clip(RoundedCornerShape(10.dp))
            ) {
            }
            Text(
                text = "Blur View",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .blur(10.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
            }
        }
        }
    }    // >>> GENERATED_CODE_END
}