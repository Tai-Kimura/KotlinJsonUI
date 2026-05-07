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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.ui.res.stringResource
import com.example.kotlinjsonui.sample.R
import androidx.compose.ui.res.colorResource
import androidx.compose.foundation.layout.imePadding
import com.kotlinjsonui.core.Configuration
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription

@Composable
fun BindingTestGeneratedView(
    data: BindingTestData,
    viewModel: BindingTestViewModel
) {
    // Generated Compose code from binding_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "binding_test",
            data = data.toMap(),
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
                android.util.Log.e("DynamicView", "Error loading binding_test: \$error")
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
            .imePadding()
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Button(
                onClick = { data.toggleDynamicMode?.invoke() },
                modifier = Modifier
                    .wrapContentWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue_3),
                                    contentColor = colorResource(R.color.white)
                                )
            ) {
                Text(
                    text = "${data.dynamicModeStatus}",
                    fontSize = 14.sp
                )
            }
            Text(
                text = "${data.title}",
                fontSize = 24.sp,
                color = colorResource(R.color.black),
                style = TextStyle(lineHeight = 24.sp),
                modifier = Modifier
                    .testTag("title_label")
                    .semantics { contentDescription = "title_label" }
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 20.dp)
            )
            Text(
                text = stringResource(R.string.binding_test_text_binding),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                style = TextStyle(lineHeight = 18.sp),
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
                    .height(44.dp),
                placeholder = { Text(
                                    text = stringResource(R.string.binding_test_enter_text),
                                    color = Configuration.TextField.defaultPlaceholderColor
                                ) },
                shape = RoundedCornerShape(8.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray_4),
                isOutlined = true,
                textStyle = TextStyle(color = colorResource(R.color.black))
            )
            Text(
                text = "${data.textValue}",
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                style = TextStyle(lineHeight = 14.sp),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
            )
            Text(
                text = stringResource(R.string.binding_test_counter_binding),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                style = TextStyle(lineHeight = 18.sp),
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
                    onClick = { data.decreaseCounter?.invoke() },
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .height(44.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_red),
                                            contentColor = colorResource(R.color.white)
                                        )
                ) {
                    Text(stringResource(R.string.binding_test_decrease))
                }
                Text(
                    text = "${data.counter}",
                    fontSize = 20.sp,
                    color = colorResource(R.color.black),
                    style = TextStyle(lineHeight = 20.sp),
                    modifier = Modifier
                        .width(100.dp)
                        .height(44.dp)
                        .padding(start = 5.dp)
                        .padding(end = 5.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorResource(R.color.pale_gray)),
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { data.increaseCounter?.invoke() },
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .height(44.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_green),
                                            contentColor = colorResource(R.color.white)
                                        )
                ) {
                    Text(stringResource(R.string.binding_test_increase))
                }
            }
            Text(
                text = stringResource(R.string.binding_test_toggle_binding),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                style = TextStyle(lineHeight = 18.sp),
                modifier = Modifier.padding(top = 30.dp)
            )
            Switch(
                checked = data.toggleValue,
                onCheckedChange = { newValue -> viewModel.updateData(mapOf("toggleValue" to newValue)) },
                modifier = Modifier
                    .testTag("toggle_switch")
                    .semantics { contentDescription = "toggle_switch" }
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
            )
            Text(
                text = "${data.toggleValue}",
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                style = TextStyle(lineHeight = 14.sp),
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
                    .background(colorResource(R.color.medium_green))
            ) {
                Text(
                    text = stringResource(R.string.binding_test_onoff),
                    fontSize = 16.sp,
                    color = colorResource(R.color.white),
                    style = TextStyle(lineHeight = 16.sp),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Text(
                text = stringResource(R.string.binding_test_slider_binding),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                style = TextStyle(lineHeight = 18.sp),
                modifier = Modifier.padding(top = 30.dp)
            )
            Slider(
                value = data.sliderValue.toFloat(),
                onValueChange = { // ERROR: sliderChanged - camelCase events require binding format @{functionName} },
                valueRange = 0f..100f,
                modifier = Modifier
                    .testTag("value_slider")
                    .semantics { contentDescription = "value_slider" }
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
            )
            Text(
                text = "${data.sliderValue}",
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                style = TextStyle(lineHeight = 14.sp),
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
                    .background(colorResource(R.color.pale_gray))
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .background(colorResource(R.color.medium_blue))
                ) {
                }
            }
            Text(
                text = stringResource(R.string.binding_test_selectbox_binding),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                style = TextStyle(lineHeight = 18.sp),
                modifier = Modifier.padding(top = 30.dp)
            )
            SelectBox(
                value = data.selectedOption,
                onValueChange = { newValue ->
                    viewModel.updateData(mapOf("selectedOption" to newValue))
                },
                options = listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5", "Option 6", "Option 7", "Option 8", "Option 9", "Option 10", "Option 11", "Option 12", "Option 13", "Option 14", "Option 15", "Option 16", "Option 17", "Option 18", "Option 19", "Option 20", "Option 21", "Option 22", "Option 23", "Option 24", "Option 25", "Option 26", "Option 27", "Option 28", "Option 29", "Option 30"),
                placeholder = "選択してください",
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray_4),
                textColor = colorResource(R.color.black),
                hintColor = colorResource(R.color.light_gray_8),
                cornerRadius = 8,
                modifier = Modifier
                    .testTag("option_select")
                    .semantics { contentDescription = "option_select" }
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
            )
            Text(
                text = "${data.selectedOption}",
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                style = TextStyle(lineHeight = 14.sp),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
            )
            Text(
                text = stringResource(R.string.binding_test_date_picker_wheels_style),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                style = TextStyle(lineHeight = 18.sp),
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
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray_4),
                textColor = colorResource(R.color.black),
                hintColor = colorResource(R.color.light_gray_8),
                cornerRadius = 8,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
            )
            Text(
                text = stringResource(R.string.binding_test_date_picker_compact_style),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                style = TextStyle(lineHeight = 18.sp),
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
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray_4),
                textColor = colorResource(R.color.black),
                hintColor = colorResource(R.color.light_gray_8),
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
                color = colorResource(R.color.medium_gray_4),
                style = TextStyle(lineHeight = 14.sp),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(bottom = 30.dp)
                    .padding(start = 20.dp)
            )
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}