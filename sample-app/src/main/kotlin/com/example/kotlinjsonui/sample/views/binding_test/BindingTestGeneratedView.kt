package com.example.kotlinjsonui.sample.views.binding_test

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.BindingTestData
import com.example.kotlinjsonui.sample.viewmodels.BindingTestViewModel
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import com.kotlinjsonui.components.DateSelectBox
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.components.SelectBox
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun BindingTestGeneratedView(
    data: BindingTestData,
    viewModel: BindingTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from binding_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "binding_test",
            modifier = modifier,
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
        modifier = modifier
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
            Section0(data, viewModel)
            Section1(data, viewModel)
            Section2(data, viewModel)
            val textFieldState_field15 = rememberTextFieldState(initialText = data.textValue)
            LaunchedEffect(data.textValue) { if (textFieldState_field15.text.toString() != data.textValue) textFieldState_field15.edit { replace(0, length, data.textValue) } }
            LaunchedEffect(textFieldState_field15.text) { val newValue = textFieldState_field15.text.toString(); if (newValue != data.textValue) viewModel.updateData(mapOf("textValue" to newValue)) }
            CustomTextFieldWithMargins(
                state = textFieldState_field15,
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
                textStyle = TextStyle(color = Color(android.graphics.Color.parseColor("#000000")))
            )
            val resolved_text357 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = "${data.textValue}",
                color = colorResource(R.color.medium_gray_4),
                fontFamily = resolved_text357.family,
                fontWeight = resolved_text357.weight,
                fontSize = resolved_text357.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text357.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 18.2.sp),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
            )
            val resolved_text358 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = FontWeight.Bold,
                size = 18.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.binding_test_counter_binding),
                color = colorResource(R.color.dark_gray),
                fontFamily = resolved_text358.family,
                fontWeight = resolved_text358.weight,
                fontSize = resolved_text358.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text358.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 23.4.sp),
                modifier = Modifier.padding(top = 30.dp)
            )
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Button(
                    onClick = { data.decreaseCounter?.invoke() },
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .height(44.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_red),
                                            contentColor = colorResource(R.color.white)
                                        )
                ) {
                    Text(stringResource(R.string.binding_test_decrease))
                }
                Section3_1(data, viewModel)
                Button(
                    onClick = { data.increaseCounter?.invoke() },
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .height(44.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_green),
                                            contentColor = colorResource(R.color.white)
                                        )
                ) {
                    Text(stringResource(R.string.binding_test_increase))
                }
            }
            val resolved_text360 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = FontWeight.Bold,
                size = 18.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.binding_test_toggle_binding),
                color = colorResource(R.color.dark_gray),
                fontFamily = resolved_text360.family,
                fontWeight = resolved_text360.weight,
                fontSize = resolved_text360.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text360.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 23.4.sp),
                modifier = Modifier.padding(top = 30.dp)
            )
            Switch(
                checked = data.toggleValue,
                onCheckedChange = { newValue -> viewModel.updateData(mapOf("toggleValue" to newValue)) },
                modifier = Modifier
                    .testTag("toggle_switch")
                    .semantics { testTagsAsResourceId = true }
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
            )
            val resolved_text361 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = "${data.toggleValue}",
                color = colorResource(R.color.medium_gray_4),
                fontFamily = resolved_text361.family,
                fontWeight = resolved_text361.weight,
                fontSize = resolved_text361.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text361.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 18.2.sp),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
            )
            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorResource(R.color.medium_green))
            ) {
                val resolved_text362 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 16.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.binding_test_onoff),
                    color = colorResource(R.color.white),
                    fontFamily = resolved_text362.family,
                    fontWeight = resolved_text362.weight,
                    fontSize = resolved_text362.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text362.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 20.8.sp),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            val resolved_text363 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = FontWeight.Bold,
                size = 18.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.binding_test_slider_binding),
                color = colorResource(R.color.dark_gray),
                fontFamily = resolved_text363.family,
                fontWeight = resolved_text363.weight,
                fontSize = resolved_text363.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text363.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 23.4.sp),
                modifier = Modifier.padding(top = 30.dp)
            )
            Slider(
                value = data.sliderValue.toFloat(),
                onValueChange = { newValue -> viewModel.updateData(mapOf("sliderValue" to newValue.toDouble())); data.sliderChanged?.invoke("value_slider", newValue) },
                valueRange = 0f..100f,
                modifier = Modifier
                    .testTag("value_slider")
                    .semantics { testTagsAsResourceId = true }
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .fillMaxWidth()
                    .height(40.dp)
            )
            val resolved_text364 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = "${data.sliderValue}",
                color = colorResource(R.color.medium_gray_4),
                fontFamily = resolved_text364.family,
                fontWeight = resolved_text364.weight,
                fontSize = resolved_text364.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text364.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 18.2.sp),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
            )
            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .fillMaxWidth()
                    .height(20.dp)
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
            val resolved_text365 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = FontWeight.Bold,
                size = 18.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.binding_test_selectbox_binding),
                color = colorResource(R.color.dark_gray),
                fontFamily = resolved_text365.family,
                fontWeight = resolved_text365.weight,
                fontSize = resolved_text365.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text365.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 23.4.sp),
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
                    .semantics { testTagsAsResourceId = true }
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .fillMaxWidth()
                    .height(44.dp)
            )
            val resolved_text366 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = "${data.selectedOption}",
                color = colorResource(R.color.medium_gray_4),
                fontFamily = resolved_text366.family,
                fontWeight = resolved_text366.weight,
                fontSize = resolved_text366.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text366.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 18.2.sp),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
            )
            val resolved_text367 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = FontWeight.Bold,
                size = 18.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.binding_test_date_picker_wheels_style),
                color = colorResource(R.color.dark_gray),
                fontFamily = resolved_text367.family,
                fontWeight = resolved_text367.weight,
                fontSize = resolved_text367.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text367.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 23.4.sp),
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
                placeholder = stringResource(R.string.binding_test_),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray_4),
                textColor = colorResource(R.color.black),
                hintColor = colorResource(R.color.light_gray_8),
                cornerRadius = 8,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .height(44.dp)
            )
            val resolved_text368 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = FontWeight.Bold,
                size = 18.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.binding_test_date_picker_compact_style),
                color = colorResource(R.color.dark_gray),
                fontFamily = resolved_text368.family,
                fontWeight = resolved_text368.weight,
                fontSize = resolved_text368.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text368.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 23.4.sp),
                modifier = Modifier.padding(top = 30.dp)
            )
            DateSelectBox(
                value = data.selectedDate2,
                onValueChange = { newValue ->
                    viewModel.updateData(mapOf("selectedDate2" to newValue))
                },
                datePickerStyle = "compact",
                dateFormat = "MM/dd/yyyy",
                placeholder = stringResource(R.string.binding_test_select_date),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray_4),
                textColor = colorResource(R.color.black),
                hintColor = colorResource(R.color.light_gray_8),
                cornerRadius = 8,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .height(44.dp)
            )
            Section4(data, viewModel)
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: BindingTestData,
    viewModel: BindingTestViewModel
) {
    Button(
        onClick = { data.toggleDynamicMode?.invoke() },
        modifier = Modifier
            .wrapContentWidth()
            .height(44.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(android.graphics.Color.parseColor("#5856D6")),
                            contentColor = colorResource(R.color.white)
                        )
    ) {
        val resolved_button24 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button24.family,
            fontWeight = resolved_button24.weight,
            fontSize = resolved_button24.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button24.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section1(
    data: BindingTestData,
    viewModel: BindingTestViewModel
) {
    val resolved_text355 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = "${data.title}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text355.family,
        fontWeight = resolved_text355.weight,
        fontSize = resolved_text355.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text355.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 31.2.sp),
        modifier = Modifier
            .testTag("title_label")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 20.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section2(
    data: BindingTestData,
    viewModel: BindingTestViewModel
) {
    val resolved_text356 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.binding_test_text_binding),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text356.family,
        fontWeight = resolved_text356.weight,
        fontSize = resolved_text356.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text356.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section3_1(
    data: BindingTestData,
    viewModel: BindingTestViewModel
) {
    val resolved_text359 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 20.sp,
        italic = false
    ))
    Text(
        text = "${data.counter}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text359.family,
        fontWeight = resolved_text359.weight,
        fontSize = resolved_text359.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text359.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 26.0.sp),
        modifier = Modifier
            .padding(start = 5.dp)
            .padding(end = 5.dp)
            .width(100.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colorResource(R.color.pale_gray)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section4(
    data: BindingTestData,
    viewModel: BindingTestViewModel
) {
    val resolved_text369 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.selectedDate}",
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text369.family,
        fontWeight = resolved_text369.weight,
        fontSize = resolved_text369.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text369.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(bottom = 30.dp)
            .padding(start = 20.dp)
    )
}
// >>> RESPONSIVE_HELPERS_END