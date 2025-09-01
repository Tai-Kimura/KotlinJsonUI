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
import com.kotlinjsonui.components.Segment
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.ui.res.stringResource
import com.example.kotlinjsonui.sample.R
import androidx.compose.ui.res.colorResource

@Composable
fun ComponentsTestGeneratedView(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    // Generated Compose code from components_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "components_test",
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
                android.util.Log.e("DynamicView", "Error loading components_test: \$error")
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
                .wrapContentHeight()
                .padding(top = 20.dp, end = 20.dp, bottom = 20.dp, start = 20.dp)
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
            Text(
                text = stringResource(R.string.components_test_new_components_test),
                fontSize = 24.sp,
                color = colorResource(R.color.dark_gray),
                modifier = Modifier
            )
            Text(
                text = stringResource(R.string.components_test_togglecheckbox_components),
                fontSize = 18.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier
            )
            Switch(
                checked = data.toggle1IsOn,
                onCheckedChange = { newValue -> viewModel.updateData(mapOf("toggle1IsOn" to newValue)) },
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
                text = stringResource(R.string.components_test_progress_slider),
                fontSize = 18.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier
            )
            LinearProgressIndicator(
            )
            Slider(
                value = data.slider1Value.toFloat(),
                onValueChange = { newValue -> viewModel.updateData(mapOf("slider1Value" to newValue.toDouble())) },
                valueRange = 0f..100f,
            )
            Text(
                text = stringResource(R.string.components_test_selection_components),
                fontSize = 18.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier
            )
            Segment(
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
                Text("Select Size", color = Color.Black)
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
                    Text("Small", color = Color.Black)
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
                    Text("Medium", color = Color.Black)
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
                    Text("Large", color = Color.Black)
                }
            }
            Text(
                text = stringResource(R.string.components_test_loading_indicator),
                fontSize = 18.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier
            )
            CircularProgressIndicator(
            )
            Text(
                text = stringResource(R.string.components_test_circle_image),
                fontSize = 18.sp,
                color = colorResource(R.color.medium_gray_4),
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
                text = stringResource(R.string.components_test_gradient_view),
                fontSize = 18.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Brush.horizontalGradient(listOf(colorResource(R.color.light_red), colorResource(R.color.light_lime))))
                    .clip(RoundedCornerShape(10.dp))
            ) {
            }
            Text(
                text = stringResource(R.string.components_test_blur_view),
                fontSize = 18.sp,
                color = colorResource(R.color.medium_gray_4),
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
    }    }
    // >>> GENERATED_CODE_END
}