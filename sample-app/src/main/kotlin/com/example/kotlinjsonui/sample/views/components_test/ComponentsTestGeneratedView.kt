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
            .background(Color(android.graphics.Color.parseColor("#F5F5F5")))
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding([20, 20, 20, 20].dp)
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier
                ) {
                    Text("Button")
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
// TODO: Implement component type: Toggle
// TODO: Implement component type: Checkbox
                Text(
                    text = "Progress & Slider",
                    fontSize = 18.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier
                )
// TODO: Implement component type: Progress
// TODO: Implement component type: Slider
                Text(
                    text = "Selection Components",
                    fontSize = 18.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier
                )
// TODO: Implement component type: Segment
// TODO: Implement component type: Radio
                Text(
                    text = "Loading Indicator",
                    fontSize = 18.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier
                )
// TODO: Implement component type: Indicator
                Text(
                    text = "Circle Image",
                    fontSize = 18.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier
                )
// TODO: Implement component type: CircleImage
                Text(
                    text = "Gradient View",
                    fontSize = 18.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier
                )
// TODO: Implement component type: GradientView
                Text(
                    text = "Blur View",
                    fontSize = 18.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier
                )
// TODO: Implement component type: BlurView
            }
        }
    }
    // >>> GENERATED_CODE_END
}