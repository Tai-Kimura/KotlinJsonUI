package com.example.kotlinjsonui.sample.views.keyboard_avoidance_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.KeyboardAvoidanceTestData
import com.example.kotlinjsonui.sample.viewmodels.KeyboardAvoidanceTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip

@Composable
fun KeyboardAvoidanceTestGeneratedView(
    data: KeyboardAvoidanceTestData,
    viewModel: KeyboardAvoidanceTestViewModel
) {
    // Generated Compose code from keyboard_avoidance_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(android.graphics.Color.parseColor("#F5F5F5")))
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
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
                modifier = Modifier.padding(bottom = 20.dp)
            )
            Text(
                text = "TextField 1",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = "\${data.textField1}",
                onValueChange = { newValue -> currentData.value = currentData.value.copy(textField1 = newValue) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(12.dp)
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(android.graphics.Color.parseColor("#FFFFFF"))),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Text(
                text = "TextField 2",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = "\${data.textField2}",
                onValueChange = { newValue -> currentData.value = currentData.value.copy(textField2 = newValue) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(12.dp)
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(android.graphics.Color.parseColor("#FFFFFF"))),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Text(
                text = "TextField 3",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = "\${data.textField3}",
                onValueChange = { newValue -> currentData.value = currentData.value.copy(textField3 = newValue) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(12.dp)
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(android.graphics.Color.parseColor("#FFFFFF"))),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Text(
                text = "TextField 4",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = "\${data.textField4}",
                onValueChange = { newValue -> currentData.value = currentData.value.copy(textField4 = newValue) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(12.dp)
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(android.graphics.Color.parseColor("#FFFFFF"))),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Text(
                text = "TextField 5 (at bottom)",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = "\${data.textField5}",
                onValueChange = { newValue -> currentData.value = currentData.value.copy(textField5 = newValue) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(12.dp)
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(android.graphics.Color.parseColor("#FFFFFF"))),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Text(
                text = "TextView (Multi-line)",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 8.dp)
            )
// TODO: Implement component type: TextView
            Button(
                onClick = { viewModel.submitForm() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Submit",
                    fontSize = 18.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
        }
        }
    }    // >>> GENERATED_CODE_END
}