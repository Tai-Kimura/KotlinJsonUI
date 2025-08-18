package com.example.kotlinjsonui.sample.views.binding_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.BindingTestData
import com.example.kotlinjsonui.sample.viewmodels.BindingTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.foundation.clickable

@Composable
fun BindingTestGeneratedView(
    data: BindingTestData,
    viewModel: BindingTestViewModel
) {
    // Generated Compose code from binding_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(android.graphics.Color.parseColor("#F8F8F8")))
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Button(
                onClick = { },
            ) {
                Text("Button")
            }
            Text(
                text = "\${data.title}",
                fontSize = 24.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Text Binding",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            TextField(
                value = "\${data.textValue}",
                onValueChange = { newValue -> currentData.value = currentData.value.copy(textValue = newValue) },
                placeholder = { Text("Enter text") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(10.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color(android.graphics.Color.parseColor("#CCCCCC")), RoundedCornerShape(8.dp))
                    .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
            )
            Text(
                text = "\${data.textValue}",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
            )
            Text(
                text = "Counter Binding",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 30.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
            ) {
                Button(
                    onClick = { viewModel.decreaseCounter() },
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .width(0.dp)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(android.graphics.Color.parseColor("#FF3B30"))
                                        )
                ) {
                    Text(
                        text = "Decrease",
                        color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    )
                }
                Text(
                    text = "\${data.counter}",
                    fontSize = 20.sp,
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .padding(end = 5.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
                        .width(100.dp)
                        .height(44.dp),
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { viewModel.increaseCounter() },
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .width(0.dp)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(android.graphics.Color.parseColor("#34C759"))
                                        )
                ) {
                    Text(
                        text = "Increase",
                        color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    )
                }
            }
            Text(
                text = "Toggle Binding",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 30.dp)
            )
            Switch(
                checked = false,
                onCheckedChange = { },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
            )
            Text(
                text = "\${data.toggleValue}",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(android.graphics.Color.parseColor("#34C759")))
            ) {
                Text(
                    text = "ON/OFF",
                    fontSize = 16.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Text(
                text = "Slider Binding",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 30.dp)
            )
            Slider(
                value = 0f,
                onValueChange = { viewModel.sliderChanged(it) },
                valueRange = 0f..100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
            )
            Text(
                text = "\${data.sliderValue}",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(android.graphics.Color.parseColor("#007AFF")))
                ) {
                }
            }
            Text(
                text = "SelectBox Binding",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 30.dp)
            )
            // Dropdown menu state
            var dropdown_1755497229_9Expanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { dropdown_1755497229_9Expanded = true }
                )

                DropdownMenu(
                    expanded = dropdown_1755497229_9Expanded,
                    onDismissRequest = { dropdown_1755497229_9Expanded = false }
                ) {
                }
            }
            Text(
                text = "\${data.selectedOption}",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(bottom = 30.dp)
                    .padding(start = 20.dp)
            )
        }
        }
    }    // >>> GENERATED_CODE_END
}