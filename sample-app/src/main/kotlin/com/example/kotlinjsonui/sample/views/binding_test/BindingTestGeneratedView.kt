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
                    modifier = Modifier
                ) {
                    Text("Button")
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
TextField(
                    value = "${data.textValue}",
                    onValueChange = { newValue -> currentData.value = currentData.value.copy(textValue = newValue) },
                    placeholder = { Text("Enter text") },
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
                )
                Text(
                    text = "${data.textValue}",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier.padding(top = 10.dp)
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
                    ) {
                        Text("Decrease")
                    }
                    Text(
                        text = "${data.counter}",
                        fontSize = 20.sp,
                        color = Color(android.graphics.Color.parseColor("#000000")),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
                            .width(100.dp)
                            .height(44.dp)
                    )
                    Button(
                        onClick = { viewModel.increaseCounter() },
                        modifier = Modifier
                    ) {
                        Text("Increase")
                    }
                }
                Text(
                    text = "Toggle Binding",
                    fontSize = 18.sp,
                    color = Color(android.graphics.Color.parseColor("#333333")),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 30.dp)
                )
// TODO: Implement component type: Switch
                Text(
                    text = "${data.toggleValue}",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier.padding(top = 10.dp)
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
// TODO: Implement component type: Slider
                Text(
                    text = "${data.sliderValue}",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier.padding(top = 10.dp)
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
// TODO: Implement component type: SelectBox
                Text(
                    text = "${data.selectedOption}",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                        .padding(top = 10.dp)
                )
            }
        }
    }
    // >>> GENERATED_CODE_END
}