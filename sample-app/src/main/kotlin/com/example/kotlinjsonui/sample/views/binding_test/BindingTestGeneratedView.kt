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
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.OutlinedTextField
import com.kotlinjsonui.components.SelectBox
import com.kotlinjsonui.components.DateSelectBox
import com.kotlinjsonui.components.SimpleDateSelectBox
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import androidx.compose.ui.text.TextStyle

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
            .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Button(
                onClick = { viewModel.toggleDynamicMode() },
                modifier = Modifier
                    .wrapContentWidth()
                    .height(32.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#5856D6"))
                                )
            ) {
                Text(
                    text = "\${data.dynamicModeStatus}",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Text(
                text = "${data.title}",
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
            CustomTextFieldWithMargins(
                value = data.textValue,
                onValueChange = { newValue -> viewModel.updateData(mapOf("textValue" to newValue)) },
                boxModifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(10.dp),
                placeholder = { Text("Enter text") },
                shape = RoundedCornerShape(8.dp),
                backgroundColor = Color(android.graphics.Color.parseColor("#FFFFFF")),
                borderColor = Color(android.graphics.Color.parseColor("#CCCCCC")),
                isOutlined = true,
                textStyle = TextStyle(color = Color(android.graphics.Color.parseColor("#000000")))
            )
            Text(
                text = "${data.textValue}",
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
                        .height(44.dp),
                    shape = RoundedCornerShape(8.dp),
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
                    text = "${data.counter}",
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
                        .height(44.dp),
                    shape = RoundedCornerShape(8.dp),
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
                text = "${data.toggleValue}",
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
                text = "${data.sliderValue}",
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
            SelectBox(
                value = data.selectedOption,
                onValueChange = { newValue ->
                    viewModel.updateData(mapOf("selectedOption" to newValue))
                },
                options = listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5", "Option 6", "Option 7", "Option 8", "Option 9", "Option 10", "Option 11", "Option 12", "Option 13", "Option 14", "Option 15", "Option 16", "Option 17", "Option 18", "Option 19", "Option 20", "Option 21", "Option 22", "Option 23", "Option 24", "Option 25", "Option 26", "Option 27", "Option 28", "Option 29", "Option 30"),
                placeholder = "選択してください",
                backgroundColor = Color(android.graphics.Color.parseColor("#FFFFFF")),
                borderColor = Color(android.graphics.Color.parseColor("#CCCCCC")),
                textColor = Color(android.graphics.Color.parseColor("#000000")),
                hintColor = Color(android.graphics.Color.parseColor("#999999")),
                cornerRadius = 8,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
            )
            Text(
                text = "${data.selectedOption}",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
            )
            Text(
                text = "Date Picker (Wheels Style)",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 30.dp)
            )
            DateSelectBox(
                value = data.selectedDate,
                onValueChange = { newValue ->
                    viewModel.updateData(mapOf("selectedDate" to newValue))
                },
                datePickerStyle = "wheels",
                dateFormat = "yyyy年MM月dd日",
                minimumDate = "2020-01-01",
                maximumDate = "2030-12-31",
                placeholder = "日付を選択",
                backgroundColor = Color(android.graphics.Color.parseColor("#FFFFFF")),
                borderColor = Color(android.graphics.Color.parseColor("#CCCCCC")),
                textColor = Color(android.graphics.Color.parseColor("#000000")),
                hintColor = Color(android.graphics.Color.parseColor("#999999")),
                cornerRadius = 8,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
            )
            Text(
                text = "Date Picker (Compact Style)",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 30.dp)
            )
            DateSelectBox(
                value = data.selectedDate2,
                onValueChange = { newValue ->
                    viewModel.updateData(mapOf("selectedDate2" to newValue))
                },
                datePickerStyle = "compact",
                dateFormat = "MM/dd/yyyy",
                placeholder = "Select date",
                backgroundColor = Color(android.graphics.Color.parseColor("#FFFFFF")),
                borderColor = Color(android.graphics.Color.parseColor("#CCCCCC")),
                textColor = Color(android.graphics.Color.parseColor("#000000")),
                hintColor = Color(android.graphics.Color.parseColor("#999999")),
                cornerRadius = 8,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
            )
            Text(
                text = "${data.selectedDate}",
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