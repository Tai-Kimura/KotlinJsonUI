package com.example.kotlinjsonui.sample.views.radio_icons_test

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
import com.example.kotlinjsonui.sample.data.RadioIconsTestData
import com.example.kotlinjsonui.sample.viewmodels.RadioIconsTestViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.foundation.clickable
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box

@Composable
fun RadioIconsTestGeneratedView(
    data: RadioIconsTestData,
    viewModel: RadioIconsTestViewModel
) {
    // Generated Compose code from radio_icons_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "radio_icons_test",
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
                android.util.Log.e("DynamicView", "Error loading radio_icons_test: \$error")
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
    ) {
        LazyColumn(
        ) {
            item {
            Column(
                modifier = Modifier.background(Color(android.graphics.Color.parseColor("#F5F5F5")))
            ) {
                Text(
                    text = "Radio Custom Icons Test",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp)
                        .padding(bottom = 20.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Default Radio Group",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                ) {
                    RadioButton(
                        selected = data.selectedDefaultgroup == "option1",
                        onClick = { viewModel.updateData(mapOf("selectedDefaultgroup" to "option1")) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Option 1", color = Color.Black)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                ) {
                    RadioButton(
                        selected = data.selectedDefaultgroup == "option2",
                        onClick = { viewModel.updateData(mapOf("selectedDefaultgroup" to "option2")) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Option 2", color = Color.Black)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                ) {
                    RadioButton(
                        selected = data.selectedDefaultgroup == "option3",
                        onClick = { viewModel.updateData(mapOf("selectedDefaultgroup" to "option3")) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Option 3", color = Color.Black)
                }
                Text(
                    text = "Custom Icon Radio Group",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .padding(start = 20.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                ) {
                    val isSelected = data.selectedCustomgroup == "custom1"
                    IconButton(
                        onClick = { viewModel.updateData(mapOf("selectedCustomgroup" to "custom1")) }
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Star Option",
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Star Option", color = Color.Black)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                ) {
                    val isSelected = data.selectedCustomgroup == "custom2"
                    IconButton(
                        onClick = { viewModel.updateData(mapOf("selectedCustomgroup" to "custom2")) }
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Heart Option",
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Heart Option", color = Color.Black)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                ) {
                    Checkbox(
                        checked = data.selectedCustomgroup == "custom3",
                        onCheckedChange = { viewModel.updateData(mapOf("selectedCustomgroup" to "custom3")) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Square Option", color = Color.Black)
                }
                Text(
                    text = "Radio with Items",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .padding(start = 20.dp)
                )
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                        .padding(end = 20.dp)
                ) {
                    Text("Select Color:", color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.updateData(mapOf("selectedColor" to "Red"))
                            }
                    ) {
                        RadioButton(
                            selected = data.selectedColor == "Red",
                            onClick = {
                                viewModel.updateData(mapOf("selectedColor" to "Red"))
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Red", color = Color.Black)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.updateData(mapOf("selectedColor" to "Green"))
                            }
                    ) {
                        RadioButton(
                            selected = data.selectedColor == "Green",
                            onClick = {
                                viewModel.updateData(mapOf("selectedColor" to "Green"))
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Green", color = Color.Black)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.updateData(mapOf("selectedColor" to "Blue"))
                            }
                    ) {
                        RadioButton(
                            selected = data.selectedColor == "Blue",
                            onClick = {
                                viewModel.updateData(mapOf("selectedColor" to "Blue"))
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Blue", color = Color.Black)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.updateData(mapOf("selectedColor" to "Yellow"))
                            }
                    ) {
                        RadioButton(
                            selected = data.selectedColor == "Yellow",
                            onClick = {
                                viewModel.updateData(mapOf("selectedColor" to "Yellow"))
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Yellow", color = Color.Black)
                    }
                }
                Text(
                    text = "${data.selectedColor}",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                )
            }
            }
        }
    }    }
    // >>> GENERATED_CODE_END
}