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
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.PaddingValues

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
            .background(Color(android.graphics.Color.parseColor("#F5F5F5")))
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
                text = "KotlinJsonUI Feature Tests",
                fontSize = 28.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = { viewModel.toggleDynamicMode() },
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .wrapContentWidth()
                    .height(32.dp),
                shape = RoundedCornerShape(6.dp),
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
                text = "Layout & Positioning",
                fontSize = 20.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Button(
                onClick = { viewModel.navigateToMarginsTest() },
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .wrapContentWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#007AFF"))
                                )
            ) {
                Text(
                    text = "Margins & Padding Test",
                    fontSize = 16.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToAlignmentTest() },
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .wrapContentWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#007AFF"))
                                )
            ) {
                Text(
                    text = "Alignment Test",
                    fontSize = 16.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToAlignmentComboTest() },
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .wrapContentWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#007AFF"))
                                )
            ) {
                Text(
                    text = "Alignment Combo Test",
                    fontSize = 16.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToWeightTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#007AFF"))
                                )
            ) {
                Text(
                    text = "Weight Distribution Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToWeightTestWithFixed() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#007AFF"))
                                )
            ) {
                Text(
                    text = "Weight + Fixed Size Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToWidthTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#007AFF"))
                                )
            ) {
                Text(
                    text = "Width Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToRelativeTest() },
                modifier = Modifier.padding(bottom = 20.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#007AFF"))
                                )
            ) {
                Text(
                    text = "Relative Positioning Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Text(
                text = "Style & Appearance",
                fontSize = 20.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Button(
                onClick = { viewModel.navigateToVisibilityTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#34C759"))
                                )
            ) {
                Text(
                    text = "Visibility & Opacity Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToDisabledTest() },
                modifier = Modifier.padding(bottom = 20.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#34C759"))
                                )
            ) {
                Text(
                    text = "Disabled States Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Text(
                text = "Text Features",
                fontSize = 20.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Button(
                onClick = { viewModel.navigateToTextStylingTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#FF9500"))
                                )
            ) {
                Text(
                    text = "Text Styling Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToTextDecorationTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#FF9500"))
                                )
            ) {
                Text(
                    text = "Text Decoration Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToLineBreakTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#FF9500"))
                                )
            ) {
                Text(
                    text = "Line Break & Spacing Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToPartialAttributesTest() },
                modifier = Modifier.padding(bottom = 20.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#FF9500"))
                                )
            ) {
                Text(
                    text = "Partial Attributes Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Text(
                text = "Input Components",
                fontSize = 20.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Button(
                onClick = { viewModel.navigateToTextFieldTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#AF52DE"))
                                )
            ) {
                Text(
                    text = "TextField Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToTextFieldEventsTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#AF52DE"))
                                )
            ) {
                Text(
                    text = "TextField Events Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToSecureFieldTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#AF52DE"))
                                )
            ) {
                Text(
                    text = "Secure Field Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToTextViewHintTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#AF52DE"))
                                )
            ) {
                Text(
                    text = "TextView Hint Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToDatePickerTest() },
                modifier = Modifier.padding(bottom = 20.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#AF52DE"))
                                )
            ) {
                Text(
                    text = "Date Picker Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Text(
                text = "UI Components",
                fontSize = 20.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Button(
                onClick = { viewModel.navigateToComponentsTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#5856D6"))
                                )
            ) {
                Text(
                    text = "Components Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToButtonTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#5856D6"))
                                )
            ) {
                Text(
                    text = "Button Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToButtonEnabledTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#5856D6"))
                                )
            ) {
                Text(
                    text = "Button Enabled Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToSwitchEventsTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#5856D6"))
                                )
            ) {
                Text(
                    text = "Switch Events Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToRadioIconsTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#5856D6"))
                                )
            ) {
                Text(
                    text = "Radio Custom Icons Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToSegmentTest() },
                modifier = Modifier.padding(bottom = 20.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#5856D6"))
                                )
            ) {
                Text(
                    text = "Segment Control Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Text(
                text = "Advanced Features",
                fontSize = 20.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Button(
                onClick = { viewModel.navigateToBindingTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#FF3B30"))
                                )
            ) {
                Text(
                    text = "Binding Properties Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToConverterTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#FF3B30"))
                                )
            ) {
                Text(
                    text = "Converter Components Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToIncludeTest() },
                modifier = Modifier.padding(bottom = 20.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#FF3B30"))
                                )
            ) {
                Text(
                    text = "Include Component Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Text(
                text = "Forms & Scrolling",
                fontSize = 20.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Button(
                onClick = { viewModel.navigateToFormTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#5AC8FA"))
                                )
            ) {
                Text(
                    text = "Form Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToKeyboardAvoidanceTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#5AC8FA"))
                                )
            ) {
                Text(
                    text = "Keyboard Avoidance Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.navigateToScrollTest() },
                modifier = Modifier.padding(bottom = 20.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#5AC8FA"))
                                )
            ) {
                Text(
                    text = "Scroll Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Text(
                text = "🔥 Complete Test Suite",
                fontSize = 20.sp,
                color = Color(android.graphics.Color.parseColor("#FF0000")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Button(
                onClick = { viewModel.navigateToImplementedAttributesTest() },
                modifier = Modifier.padding(bottom = 20.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#00C853"))
                                )
            ) {
                Text(
                    text = "🔥 All Implemented Attributes Test",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
        }
        }
    }    // >>> GENERATED_CODE_END
}