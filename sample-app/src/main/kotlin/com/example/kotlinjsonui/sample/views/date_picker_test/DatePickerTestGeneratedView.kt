package com.example.kotlinjsonui.sample.views.date_picker_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.DatePickerTestData
import com.example.kotlinjsonui.sample.viewmodels.DatePickerTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.foundation.clickable

@Composable
fun DatePickerTestGeneratedView(
    data: DatePickerTestData,
    viewModel: DatePickerTestViewModel
) {
    // Generated Compose code from date_picker_test.json
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
                text = "Basic DatePicker",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            // Dropdown menu state
            var dropdown_1755498355_519Expanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    Modifier
                    .fillMaxWidth()
                    .height(50.dp)
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
                        .clickable { dropdown_1755498355_519Expanded = true }
                )

                DropdownMenu(
                    expanded = dropdown_1755498355_519Expanded,
                    onDismissRequest = { dropdown_1755498355_519Expanded = false }
                ) {
                }
            }
            Text(
                text = "DatePicker with Min/Max Dates",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 30.dp)
            )
            Text(
                text = "Min: 2025-01-01, Max: 2025-12-31",
                fontSize = 12.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(top = 5.dp)
            )
            // Dropdown menu state
            var dropdown_1755498355_241Expanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    Modifier
                    .fillMaxWidth()
                    .height(50.dp)
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
                        .clickable { dropdown_1755498355_241Expanded = true }
                )

                DropdownMenu(
                    expanded = dropdown_1755498355_241Expanded,
                    onDismissRequest = { dropdown_1755498355_241Expanded = false }
                ) {
                }
            }
            Text(
                text = "Time Picker",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 30.dp)
            )
            // Dropdown menu state
            var dropdown_1755498355_467Expanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    Modifier
                    .fillMaxWidth()
                    .height(50.dp)
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
                        .clickable { dropdown_1755498355_467Expanded = true }
                )

                DropdownMenu(
                    expanded = dropdown_1755498355_467Expanded,
                    onDismissRequest = { dropdown_1755498355_467Expanded = false }
                ) {
                }
            }
            Text(
                text = "DateTime Picker",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 30.dp)
            )
            // Dropdown menu state
            var dropdown_1755498355_299Expanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    Modifier
                    .fillMaxWidth()
                    .height(50.dp)
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
                        .clickable { dropdown_1755498355_299Expanded = true }
                )

                DropdownMenu(
                    expanded = dropdown_1755498355_299Expanded,
                    onDismissRequest = { dropdown_1755498355_299Expanded = false }
                ) {
                }
            }
            Text(
                text = "DatePicker with Minute Interval",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 30.dp)
            )
            Text(
                text = "15 minute intervals",
                fontSize = 12.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(top = 5.dp)
            )
            // Dropdown menu state
            var dropdown_1755498355_412Expanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    Modifier
                    .fillMaxWidth()
                    .height(50.dp)
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
                        .clickable { dropdown_1755498355_412Expanded = true }
                )

                DropdownMenu(
                    expanded = dropdown_1755498355_412Expanded,
                    onDismissRequest = { dropdown_1755498355_412Expanded = false }
                ) {
                }
            }
            Text(
                text = "Calendar Style DatePicker",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 30.dp)
            )
            // Dropdown menu state
            var dropdown_1755498355_185Expanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    Modifier
                    .fillMaxWidth()
                    .height(300.dp)
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
                        .clickable { dropdown_1755498355_185Expanded = true }
                )

                DropdownMenu(
                    expanded = dropdown_1755498355_185Expanded,
                    onDismissRequest = { dropdown_1755498355_185Expanded = false }
                ) {
                }
            }
            // Dropdown menu state
            var dropdown_1755498355_752Expanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 30.dp)
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
                        .clickable { dropdown_1755498355_752Expanded = true }
                )

                DropdownMenu(
                    expanded = dropdown_1755498355_752Expanded,
                    onDismissRequest = { dropdown_1755498355_752Expanded = false }
                ) {
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(15.dp)
                    .padding(top = 30.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
            ) {
                Text(
                    text = "Selected Values:",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#333333")),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                )
                Text(
                    text = "\${data.selectedDate}",
                    fontSize = 12.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier.padding(top = 5.dp)
                )
                Text(
                    text = "\${data.startDate}",
                    fontSize = 12.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
        }
        }
    }    // >>> GENERATED_CODE_END
}