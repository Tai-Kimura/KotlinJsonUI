package com.example.kotlinjsonui.sample.views.date_picker_test

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
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
import com.example.kotlinjsonui.sample.data.DatePickerTestData
import com.example.kotlinjsonui.sample.viewmodels.DatePickerTestViewModel
import com.kotlinjsonui.components.DateSelectBox
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.components.SelectBox
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun DatePickerTestGeneratedView(
    data: DatePickerTestData,
    viewModel: DatePickerTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from date_picker_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "date_picker_test",
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
                android.util.Log.e("DynamicView", "Error loading date_picker_test: \$error")
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
            Section3(data, viewModel)
            Section4(data, viewModel)
            Section5(data, viewModel)
            Section6(data, viewModel)
            Section7(data, viewModel)
            Section8(data, viewModel)
            Section9(data, viewModel)
            Section10(data, viewModel)
            Section11(data, viewModel)
            Section12(data, viewModel)
            Section13(data, viewModel)
            Section14(data, viewModel)
            Section15(data, viewModel)
            Section16(data, viewModel)
            Section17(data, viewModel)
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: DatePickerTestData,
    viewModel: DatePickerTestViewModel
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
        val resolved_button1 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button1.family,
            fontWeight = resolved_button1.weight,
            fontSize = resolved_button1.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button1.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section1(
    data: DatePickerTestData,
    viewModel: DatePickerTestViewModel
) {
    val resolved_text1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = "${data.title}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text1.family,
        fontWeight = resolved_text1.weight,
        fontSize = resolved_text1.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text1.style ?: FontStyle.Normal,
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
    data: DatePickerTestData,
    viewModel: DatePickerTestViewModel
) {
    val resolved_text2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.date_picker_test_basic_datepicker),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text2.family,
        fontWeight = resolved_text2.weight,
        fontSize = resolved_text2.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text2.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section3(
    data: DatePickerTestData,
    viewModel: DatePickerTestViewModel
) {
    DateSelectBox(
        value = data.selectedDate,
        onValueChange = { newValue ->
            viewModel.updateData(mapOf("selectedDate" to newValue))
        },
        datePickerMode = "date",
        dateFormat = "yyyy-MM-dd",
        modifier = Modifier
            .testTag("basic_date_picker")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .height(50.dp)
    )
}

@Composable
private fun Section4(
    data: DatePickerTestData,
    viewModel: DatePickerTestViewModel
) {
    val resolved_text3 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.date_picker_test_datepicker_with_minmax_dates),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text3.family,
        fontWeight = resolved_text3.weight,
        fontSize = resolved_text3.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text3.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 30.dp)
    )
}

@Composable
private fun Section5(
    data: DatePickerTestData,
    viewModel: DatePickerTestViewModel
) {
    val resolved_text4 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 12.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.date_picker_test_min_20250101_max_20251231),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text4.family,
        fontWeight = resolved_text4.weight,
        fontSize = resolved_text4.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text4.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 15.6.sp),
        modifier = Modifier.padding(top = 5.dp)
    )
}

@Composable
private fun Section6(
    data: DatePickerTestData,
    viewModel: DatePickerTestViewModel
) {
    DateSelectBox(
        value = data.selectedDate2,
        onValueChange = { newValue ->
            viewModel.updateData(mapOf("selectedDate2" to newValue))
        },
        datePickerMode = "date",
        dateFormat = "yyyy-MM-dd",
        minimumDate = "2025-01-01",
        maximumDate = "2025-12-31",
        modifier = Modifier
            .testTag("limited_date_picker")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .height(50.dp)
    )
}

@Composable
private fun Section7(
    data: DatePickerTestData,
    viewModel: DatePickerTestViewModel
) {
    val resolved_text5 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.date_picker_test_time_picker),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text5.family,
        fontWeight = resolved_text5.weight,
        fontSize = resolved_text5.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text5.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 30.dp)
    )
}

@Composable
private fun Section8(
    data: DatePickerTestData,
    viewModel: DatePickerTestViewModel
) {
    DateSelectBox(
        value = data.selectedTime,
        onValueChange = { newValue ->
            viewModel.updateData(mapOf("selectedTime" to newValue))
        },
        datePickerMode = "time",
        dateFormat = "HH:mm",
        modifier = Modifier
            .testTag("time_picker")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .height(50.dp)
    )
}

@Composable
private fun Section9(
    data: DatePickerTestData,
    viewModel: DatePickerTestViewModel
) {
    val resolved_text6 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.date_picker_test_datetime_picker),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text6.family,
        fontWeight = resolved_text6.weight,
        fontSize = resolved_text6.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text6.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 30.dp)
    )
}

@Composable
private fun Section10(
    data: DatePickerTestData,
    viewModel: DatePickerTestViewModel
) {
    DateSelectBox(
        value = data.selectedDateTime,
        onValueChange = { newValue ->
            viewModel.updateData(mapOf("selectedDateTime" to newValue))
        },
        datePickerMode = "dateAndTime",
        dateFormat = "yyyy-MM-dd HH:mm",
        modifier = Modifier
            .testTag("datetime_picker")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .height(50.dp)
    )
}

@Composable
private fun Section11(
    data: DatePickerTestData,
    viewModel: DatePickerTestViewModel
) {
    val resolved_text7 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.date_picker_test_datepicker_with_minute_interval),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text7.family,
        fontWeight = resolved_text7.weight,
        fontSize = resolved_text7.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text7.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 30.dp)
    )
}

@Composable
private fun Section12(
    data: DatePickerTestData,
    viewModel: DatePickerTestViewModel
) {
    val resolved_text8 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 12.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.date_picker_test_15_minute_intervals),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text8.family,
        fontWeight = resolved_text8.weight,
        fontSize = resolved_text8.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text8.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 15.6.sp),
        modifier = Modifier.padding(top = 5.dp)
    )
}

@Composable
private fun Section13(
    data: DatePickerTestData,
    viewModel: DatePickerTestViewModel
) {
    DateSelectBox(
        value = data.selectedTimeInterval,
        onValueChange = { newValue ->
            viewModel.updateData(mapOf("selectedTimeInterval" to newValue))
        },
        datePickerMode = "time",
        dateFormat = "HH:mm",
        minuteInterval = 15,
        modifier = Modifier
            .testTag("interval_picker")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .height(50.dp)
    )
}

@Composable
private fun Section14(
    data: DatePickerTestData,
    viewModel: DatePickerTestViewModel
) {
    val resolved_text9 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.date_picker_test_calendar_style_datepicker),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text9.family,
        fontWeight = resolved_text9.weight,
        fontSize = resolved_text9.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text9.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(top = 30.dp)
    )
}

@Composable
private fun Section15(
    data: DatePickerTestData,
    viewModel: DatePickerTestViewModel
) {
    DateSelectBox(
        value = data.selectedCalendarDate,
        onValueChange = { newValue ->
            viewModel.updateData(mapOf("selectedCalendarDate" to newValue))
        },
        datePickerMode = "date",
        datePickerStyle = "graphical",
        dateFormat = "yyyy-MM-dd",
        modifier = Modifier
            .testTag("calendar_picker")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .height(300.dp)
    )
}

@Composable
private fun Section16(
    data: DatePickerTestData,
    viewModel: DatePickerTestViewModel
) {
    DateSelectBox(
        value = "",
        onValueChange = { },
        placeholder = stringResource(R.string.date_picker_test_select_date_range),
        modifier = Modifier
            .testTag("date_range_select")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 30.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .height(50.dp)
    )
}

@Composable
private fun Section17(
    data: DatePickerTestData,
    viewModel: DatePickerTestViewModel
) {
    Column(
        modifier = Modifier
            .padding(top = 30.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(colorResource(R.color.pale_gray))
            .padding(15.dp)
    ) {
        val resolved_text10 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Bold,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.date_picker_test_selected_values),
            color = colorResource(R.color.dark_gray),
            fontFamily = resolved_text10.family,
            fontWeight = resolved_text10.weight,
            fontSize = resolved_text10.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text10.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 18.2.sp),
            modifier = Modifier
        )
        val resolved_text11 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 12.sp,
            italic = false
        ))
        Text(
            text = "${data.selectedDate}",
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text11.family,
            fontWeight = resolved_text11.weight,
            fontSize = resolved_text11.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text11.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 15.6.sp),
            modifier = Modifier.padding(top = 5.dp)
        )
        val resolved_text12 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 12.sp,
            italic = false
        ))
        Text(
            text = "${data.startDate}",
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text12.family,
            fontWeight = resolved_text12.weight,
            fontSize = resolved_text12.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text12.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 15.6.sp),
            modifier = Modifier.padding(top = 5.dp)
        )
    }
}
// >>> RESPONSIVE_HELPERS_END