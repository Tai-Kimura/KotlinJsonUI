package com.example.kotlinjsonui.sample.views.test_menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.TestMenuData
import com.example.kotlinjsonui.sample.viewmodels.TestMenuViewModel
import androidx.compose.foundation.background

@Composable
fun TestMenuGeneratedView(
    data: TestMenuData,
    viewModel: TestMenuViewModel
) {
    // Generated Compose code from test_menu.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(20.dp)
                    .background(Color(android.graphics.Color.parseColor("#F5F5F5")))
            ) {
                Text(
                    text = "SwiftJsonUI Feature Tests",
                    fontSize = 28.sp,
                    color = Color(android.graphics.Color.parseColor("#333333")),
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Row(
                    modifier = Modifier
                        .padding(15.dp)
                        .padding(bottom = 10.dp)
                        .background(Color(android.graphics.Color.parseColor("#E8F4FF")))
                ) {
                    Text(
                        text = "Dynamic Mode Status:",
                        fontSize = 16.sp,
                        color = Color(android.graphics.Color.parseColor("#333333")),
                        modifier = Modifier
                    )
                    Text(
                        text = "${data.dynamicModeStatus}",
                        fontSize = 16.sp,
                        color = Color(android.graphics.Color.parseColor("#007AFF")),
                        modifier = Modifier
                    )
                }
                Button(
                    onClick = { viewModel.toggleDynamicMode() },
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                ) {
                    Text("Toggle Dynamic Mode")
                }
                Text(
                    text = "Layout & Positioning",
                    fontSize = 20.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Button(
                    onClick = { viewModel.navigateToMarginsTest() },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                ) {
                    Text("Margins & Padding Test")
                }
                Button(
                    onClick = { viewModel.navigateToAlignmentTest() },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                ) {
                    Text("Alignment Test")
                }
                Button(
                    onClick = { viewModel.navigateToAlignmentComboTest() },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                ) {
                    Text("Alignment Combo Test")
                }
                Button(
                    onClick = { viewModel.navigateToWeightTest() },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                ) {
                    Text("Weight Distribution Test")
                }
                Button(
                    onClick = { viewModel.navigateToWeightTestWithFixed() },
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                ) {
                    Text("Weight + Fixed Size Test")
                }
                Text(
                    text = "Style & Appearance",
                    fontSize = 20.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Button(
                    onClick = { viewModel.navigateToVisibilityTest() },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                ) {
                    Text("Visibility & Opacity Test")
                }
                Button(
                    onClick = { viewModel.navigateToDisabledTest() },
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                ) {
                    Text("Disabled States Test")
                }
                Text(
                    text = "Text Features",
                    fontSize = 20.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Button(
                    onClick = { viewModel.navigateToTextStylingTest() },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                ) {
                    Text("Text Styling Test")
                }
                Button(
                    onClick = { viewModel.navigateToComponentsTest() },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                ) {
                    Text("New Components Test")
                }
                Button(
                    onClick = { viewModel.navigateToLineBreakTest() },
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                ) {
                    Text("Line Break & Spacing Test")
                }
                Text(
                    text = "Input Components",
                    fontSize = 20.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Button(
                    onClick = { viewModel.navigateToSecureFieldTest() },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                ) {
                    Text("Secure Field Test")
                }
                Button(
                    onClick = { viewModel.navigateToDatePickerTest() },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                ) {
                    Text("Date Picker Test")
                }
                Button(
                    onClick = { viewModel.navigateToTextviewHintTest() },
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                ) {
                    Text("TextView Hint Test")
                }
                Text(
                    text = "Advanced Features",
                    fontSize = 20.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Button(
                    onClick = { viewModel.navigateToRelativeTest() },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                ) {
                    Text("Relative Positioning Test")
                }
                Button(
                    onClick = { viewModel.navigateToBindingTest() },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                ) {
                    Text("Binding Properties Test")
                }
                Button(
                    onClick = { viewModel.navigateToConverterTest() },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                ) {
                    Text("Converter Components Test")
                }
                Button(
                    onClick = { viewModel.navigateToIncludeTest() },
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                ) {
                    Text("Include Component Test")
                }
                Text(
                    text = "Forms & Keyboard",
                    fontSize = 20.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Button(
                    onClick = { viewModel.navigateToFormTest() },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                ) {
                    Text("Form & Keyboard Avoidance Test")
                }
            }
        }
    }
    // >>> GENERATED_CODE_END
}