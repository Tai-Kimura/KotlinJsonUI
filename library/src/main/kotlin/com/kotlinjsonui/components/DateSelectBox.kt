package com.kotlinjsonui.components

import androidx.compose.foundation.background
import com.kotlinjsonui.core.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * A date/time selection component for KotlinJsonUI
 * 
 * @param value The currently selected date value (ISO 8601 format)
 * @param onValueChange Callback when a new value is selected
 * @param datePickerMode Mode of picker: "date", "time", "dateAndTime"
 * @param datePickerStyle Style of date picker: "wheels", "inline", "compact", "graphical"
 * @param dateFormat Format string for displaying the date
 * @param minuteInterval Interval for minute selection (e.g., 15 for 15-minute intervals)
 * @param minimumDate Minimum selectable date (ISO 8601 format)
 * @param maximumDate Maximum selectable date (ISO 8601 format)
 * @param modifier Modifier for the component
 * @param placeholder Optional placeholder text when no value is selected
 * @param enabled Whether the component is enabled
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectBox(
    value: String,
    onValueChange: (String) -> Unit,
    datePickerMode: String = "date",
    datePickerStyle: String = "compact",
    dateFormat: String = "yyyy-MM-dd",
    minuteInterval: Int = 1,
    minimumDate: String? = null,
    maximumDate: String? = null,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    backgroundColor: Color = Color.White,
    borderColor: Color = Color(0xFFCCCCCC),
    textColor: Color = Color.Black,
    hintColor: Color = Color(0xFF999999),
    cornerRadius: Int = 8,
    sheetBackgroundColor: Color = Configuration.DatePicker.defaultSheetBackgroundColor,
    sheetTextColor: Color = Configuration.DatePicker.defaultSheetTextColor
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true  // Always expand to full height
    )
    val scope = rememberCoroutineScope()
    
    // Parse the current date/time value
    val calendar = remember { Calendar.getInstance() }
    val dateFormatter = remember(dateFormat) { SimpleDateFormat(dateFormat, Locale.getDefault()) }
    val isoFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val isoTimeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val isoDateTimeFormatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    
    // Parse current value if not empty
    LaunchedEffect(value) {
        if (value.isNotEmpty()) {
            try {
                val date = when (datePickerMode) {
                    "time" -> isoTimeFormatter.parse(value)
                    "dateAndTime" -> isoDateTimeFormatter.parse(value)
                    else -> isoFormatter.parse(value)
                }
                if (date != null) {
                    calendar.time = date
                }
            } catch (e: Exception) {
                try {
                    val date = dateFormatter.parse(value)
                    if (date != null) {
                        calendar.time = date
                    }
                } catch (e2: Exception) {
                    // Invalid format
                }
            }
        }
    }
    
    // Format display text
    val displayText = remember(value, dateFormat) {
        if (value.isEmpty()) {
            ""
        } else {
            try {
                val date = when (datePickerMode) {
                    "time" -> isoTimeFormatter.parse(value)
                    "dateAndTime" -> isoDateTimeFormatter.parse(value)
                    else -> isoFormatter.parse(value)
                } ?: dateFormatter.parse(value)
                
                if (date != null) {
                    dateFormatter.format(date)
                } else {
                    value
                }
            } catch (e: Exception) {
                value
            }
        }
    }
    
    // Parse min/max dates
    val minCalendar = remember(minimumDate) {
        minimumDate?.let {
            Calendar.getInstance().apply {
                try {
                    time = isoFormatter.parse(it) ?: Date()
                } catch (e: Exception) {
                    // Invalid date format
                }
            }
        }
    }
    
    val maxCalendar = remember(maximumDate) {
        maximumDate?.let {
            Calendar.getInstance().apply {
                try {
                    time = isoFormatter.parse(it) ?: Date()
                } catch (e: Exception) {
                    // Invalid date format
                }
            }
        }
    }
    
    // Choose icon based on mode
    val icon = when (datePickerMode) {
        "time" -> Icons.Default.Schedule
        else -> Icons.Default.DateRange
    }
    
    // Custom SelectBox field
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(enabled = enabled) { 
                showBottomSheet = true
            }
            .border(
                width = 1.dp,
                color = if (enabled) borderColor else borderColor.copy(alpha = 0.38f),
                shape = RoundedCornerShape(cornerRadius.dp)
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(cornerRadius.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (displayText.isNotEmpty()) displayText else (placeholder ?: ""),
                color = if (displayText.isNotEmpty()) textColor else hintColor,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = icon,
                contentDescription = if (datePickerMode == "time") "Time" else "Calendar",
                tint = textColor
            )
        }
    }
    
    // Bottom Sheet with picker
    if (showBottomSheet) {
        // Ensure sheet is fully expanded when shown
        LaunchedEffect(showBottomSheet) {
            sheetState.expand()
        }
        
        ModalBottomSheet(
            onDismissRequest = { 
                scope.launch {
                    sheetState.hide()
                    showBottomSheet = false
                }
            },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            containerColor = sheetBackgroundColor,
            contentColor = sheetTextColor,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .navigationBarsPadding()  // Proper padding for navigation bar
                    .padding(bottom = 16.dp)
            ) {
                when (datePickerMode) {
                    "time" -> {
                        // Time picker
                        if (datePickerStyle == "wheels") {
                            // iOS-style wheel time picker
                            Text(
                                text = "時刻を選択",
                                style = MaterialTheme.typography.titleMedium,
                                color = sheetTextColor,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(vertical = 16.dp)
                            )
                            
                            Divider(color = sheetTextColor.copy(alpha = 0.12f))
                            
                            TimeWheelPicker(
                                selectedTime = calendar,
                                onTimeSelected = { hour, minute ->
                                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                                    calendar.set(Calendar.MINUTE, minute)
                                },
                                minuteInterval = minuteInterval,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                textColor = sheetTextColor
                            )

                            // Confirm and Cancel buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                TextButton(
                                    onClick = {
                                        scope.launch {
                                            sheetState.hide()
                                            showBottomSheet = false
                                        }
                                    },
                                    modifier = Modifier.weight(1f).testTag("kjui_x7q_cancel"),
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = Configuration.DatePicker.SheetButton.defaultCancelButtonTextColor
                                    )
                                ) {
                                    Text(
                                        text = "キャンセル",
                                        fontSize = Configuration.DatePicker.SheetButton.defaultFontSize.sp,
                                        fontWeight = FontWeight(Configuration.DatePicker.SheetButton.defaultFontWeight),
                                        color = Configuration.DatePicker.SheetButton.defaultCancelButtonTextColor
                                    )
                                }

                                Button(
                                    onClick = {
                                        val newValue = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
                                        onValueChange(newValue)
                                        scope.launch {
                                            sheetState.hide()
                                            showBottomSheet = false
                                        }
                                    },
                                    modifier = Modifier.weight(1f).testTag("kjui_x7q_done"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Configuration.DatePicker.SheetButton.defaultButtonBackgroundColor,
                                        contentColor = Configuration.DatePicker.SheetButton.defaultButtonTextColor
                                    )
                                ) {
                                    Text(
                                        text = "確定",
                                        fontSize = Configuration.DatePicker.SheetButton.defaultFontSize.sp,
                                        fontWeight = FontWeight(Configuration.DatePicker.SheetButton.defaultFontWeight),
                                        color = Configuration.DatePicker.SheetButton.defaultButtonTextColor
                                    )
                                }
                            }
                        } else {
                            // Default time picker in sheet
                            Text(
                                text = "時刻を選択",
                                style = MaterialTheme.typography.titleMedium,
                                color = sheetTextColor,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(vertical = 16.dp)
                            )
                            
                            Divider(color = sheetTextColor.copy(alpha = 0.12f))
                            
                            // Use wheel picker as default for time
                            TimeWheelPicker(
                                selectedTime = calendar,
                                onTimeSelected = { hour, minute ->
                                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                                    calendar.set(Calendar.MINUTE, minute)
                                },
                                minuteInterval = minuteInterval,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                textColor = sheetTextColor
                            )

                            // Confirm and Cancel buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                TextButton(
                                    onClick = {
                                        scope.launch {
                                            sheetState.hide()
                                            showBottomSheet = false
                                        }
                                    },
                                    modifier = Modifier.weight(1f).testTag("kjui_x7q_cancel"),
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = Configuration.DatePicker.SheetButton.defaultCancelButtonTextColor
                                    )
                                ) {
                                    Text(
                                        text = "キャンセル",
                                        fontSize = Configuration.DatePicker.SheetButton.defaultFontSize.sp,
                                        fontWeight = FontWeight(Configuration.DatePicker.SheetButton.defaultFontWeight),
                                        color = Configuration.DatePicker.SheetButton.defaultCancelButtonTextColor
                                    )
                                }

                                Button(
                                    onClick = {
                                        val newValue = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
                                        onValueChange(newValue)
                                        scope.launch {
                                            sheetState.hide()
                                            showBottomSheet = false
                                        }
                                    },
                                    modifier = Modifier.weight(1f).testTag("kjui_x7q_done"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Configuration.DatePicker.SheetButton.defaultButtonBackgroundColor,
                                        contentColor = Configuration.DatePicker.SheetButton.defaultButtonTextColor
                                    )
                                ) {
                                    Text(
                                        text = "確定",
                                        fontSize = Configuration.DatePicker.SheetButton.defaultFontSize.sp,
                                        fontWeight = FontWeight(Configuration.DatePicker.SheetButton.defaultFontWeight),
                                        color = Configuration.DatePicker.SheetButton.defaultButtonTextColor
                                    )
                                }
                            }
                        }
                    }
                    
                    "dateAndTime" -> {
                        // Date and time picker
                        if (datePickerStyle == "wheels") {
                            // iOS-style wheel date/time picker
                            Text(
                                text = "日付と時刻を選択",
                                style = MaterialTheme.typography.titleMedium,
                                color = sheetTextColor,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(vertical = 16.dp)
                            )

                            Divider(color = sheetTextColor.copy(alpha = 0.12f))

                            DateTimeWheelPicker(
                                selectedDateTime = calendar,
                                onDateTimeSelected = { year, month, day, hour, minute ->
                                    calendar.set(year, month, day, hour, minute)
                                },
                                minDate = minCalendar,
                                maxDate = maxCalendar,
                                minuteInterval = minuteInterval,
                                dateFormat = dateFormat,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                textColor = sheetTextColor
                            )

                            // Confirm and Cancel buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                TextButton(
                                    onClick = {
                                        scope.launch {
                                            sheetState.hide()
                                            showBottomSheet = false
                                        }
                                    },
                                    modifier = Modifier.weight(1f).testTag("kjui_x7q_cancel"),
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = Configuration.DatePicker.SheetButton.defaultCancelButtonTextColor
                                    )
                                ) {
                                    Text(
                                        text = "キャンセル",
                                        fontSize = Configuration.DatePicker.SheetButton.defaultFontSize.sp,
                                        fontWeight = FontWeight(Configuration.DatePicker.SheetButton.defaultFontWeight),
                                        color = Configuration.DatePicker.SheetButton.defaultCancelButtonTextColor
                                    )
                                }

                                Button(
                                    onClick = {
                                        val newValue = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(calendar.time)
                                        onValueChange(newValue)
                                        scope.launch {
                                            sheetState.hide()
                                            showBottomSheet = false
                                        }
                                    },
                                    modifier = Modifier.weight(1f).testTag("kjui_x7q_done")
                                ) {
                                    Text(
                                        text = "確定",
                                        fontSize = Configuration.DatePicker.SheetButton.defaultFontSize.sp,
                                        fontWeight = FontWeight(Configuration.DatePicker.SheetButton.defaultFontWeight),
                                        color = Configuration.DatePicker.SheetButton.defaultButtonTextColor
                                    )
                                }
                            }
                        } else {
                            // Default date/time picker in sheet
                            Text(
                                text = "日付と時刻を選択",
                                style = MaterialTheme.typography.titleMedium,
                                color = sheetTextColor,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(vertical = 16.dp)
                            )

                            Divider(color = sheetTextColor.copy(alpha = 0.12f))

                            // Use wheel picker as default for date/time
                            DateTimeWheelPicker(
                                selectedDateTime = calendar,
                                onDateTimeSelected = { year, month, day, hour, minute ->
                                    calendar.set(year, month, day, hour, minute)
                                },
                                minDate = minCalendar,
                                maxDate = maxCalendar,
                                minuteInterval = minuteInterval,
                                dateFormat = dateFormat,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                textColor = sheetTextColor
                            )

                            // Confirm and Cancel buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                TextButton(
                                    onClick = {
                                        scope.launch {
                                            sheetState.hide()
                                            showBottomSheet = false
                                        }
                                    },
                                    modifier = Modifier.weight(1f).testTag("kjui_x7q_cancel"),
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = Configuration.DatePicker.SheetButton.defaultCancelButtonTextColor
                                    )
                                ) {
                                    Text(
                                        text = "キャンセル",
                                        fontSize = Configuration.DatePicker.SheetButton.defaultFontSize.sp,
                                        fontWeight = FontWeight(Configuration.DatePicker.SheetButton.defaultFontWeight),
                                        color = Configuration.DatePicker.SheetButton.defaultCancelButtonTextColor
                                    )
                                }

                                Button(
                                    onClick = {
                                        val newValue = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(calendar.time)
                                        onValueChange(newValue)
                                        scope.launch {
                                            sheetState.hide()
                                            showBottomSheet = false
                                        }
                                    },
                                    modifier = Modifier.weight(1f).testTag("kjui_x7q_done")
                                ) {
                                    Text(
                                        text = "確定",
                                        fontSize = Configuration.DatePicker.SheetButton.defaultFontSize.sp,
                                        fontWeight = FontWeight(Configuration.DatePicker.SheetButton.defaultFontWeight),
                                        color = Configuration.DatePicker.SheetButton.defaultButtonTextColor
                                    )
                                }
                            }
                        }
                    }

                    else -> {
                        // Date picker (default)
                        when (datePickerStyle) {
                            "wheels" -> {
                                // iOS-style wheel date picker
                                Text(
                                    text = "日付を選択",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = sheetTextColor,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(vertical = 16.dp)
                                )

                                Divider()

                                DateWheelPicker(
                                    selectedDate = calendar,
                                    onDateSelected = { year, month, day ->
                                        calendar.set(year, month, day)
                                    },
                                    minDate = minCalendar,
                                    maxDate = maxCalendar,
                                    dateFormat = dateFormat,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 16.dp),
                                    textColor = sheetTextColor
                                )

                                // Confirm and Cancel buttons
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    TextButton(
                                        onClick = {
                                            scope.launch {
                                                sheetState.hide()
                                                showBottomSheet = false
                                            }
                                        },
                                        modifier = Modifier.weight(1f).testTag("kjui_x7q_cancel")
                                    ) {
                                        Text(
                                            text = "キャンセル",
                                            fontSize = Configuration.DatePicker.SheetButton.defaultFontSize.sp,
                                            fontWeight = FontWeight(Configuration.DatePicker.SheetButton.defaultFontWeight),
                                            color = Configuration.DatePicker.SheetButton.defaultCancelButtonTextColor
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            val newValue = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                                            onValueChange(newValue)
                                            scope.launch {
                                                sheetState.hide()
                                                showBottomSheet = false
                                            }
                                        },
                                        modifier = Modifier.weight(1f).testTag("kjui_x7q_done")
                                    ) {
                                        Text(
                                            text = "確定",
                                            fontSize = Configuration.DatePicker.SheetButton.defaultFontSize.sp,
                                            fontWeight = FontWeight(Configuration.DatePicker.SheetButton.defaultFontWeight),
                                            color = Configuration.DatePicker.SheetButton.defaultButtonTextColor
                                        )
                                    }
                                }
                            }

                            "graphical", "inline" -> {
                                // Material3 DatePicker
                                DateCalendarPicker(
                                    selectedDate = calendar,
                                    onDateSelected = { year, month, day ->
                                        calendar.set(year, month, day)
                                        val newValue = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                                        onValueChange(newValue)
                                        scope.launch {
                                            sheetState.hide()
                                            showBottomSheet = false
                                        }
                                    },
                                    minDate = minCalendar,
                                    maxDate = maxCalendar,
                                    dateFormat = dateFormat,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    sheetTextColor = sheetTextColor
                                )
                            }
                            
                            else -> {
                                // Default: calendar picker in sheet
                                DateCalendarPicker(
                                    selectedDate = calendar,
                                    onDateSelected = { year, month, day ->
                                        calendar.set(year, month, day)
                                        val newValue = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                                        onValueChange(newValue)
                                        scope.launch {
                                            sheetState.hide()
                                            showBottomSheet = false
                                        }
                                    },
                                    minDate = minCalendar,
                                    maxDate = maxCalendar,
                                    dateFormat = dateFormat,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    sheetTextColor = sheetTextColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


/**
 * Date wheel picker component (iOS-style spinner)
 */
@Composable
fun DateWheelPicker(
    selectedDate: Calendar,
    onDateSelected: (year: Int, month: Int, day: Int) -> Unit,
    minDate: Calendar? = null,
    maxDate: Calendar? = null,
    dateFormat: String,
    modifier: Modifier = Modifier,
    textColor: Color = Configuration.DatePicker.defaultSheetTextColor,
    enableTestTags: Boolean = true
) {
    var selectedYear by remember { mutableStateOf(selectedDate.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(selectedDate.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableStateOf(selectedDate.get(Calendar.DAY_OF_MONTH)) }

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = remember {
        val minYear = minDate?.get(Calendar.YEAR) ?: (currentYear - 100)
        val maxYear = maxDate?.get(Calendar.YEAR) ?: (currentYear + 100)
        (minYear..maxYear).toList()
    }

    // Determine month format based on dateFormat string
    val months = if (dateFormat.contains("MM月") || dateFormat.contains("月")) {
        listOf("1月", "2月", "3月", "4月", "5月", "6月",
               "7月", "8月", "9月", "10月", "11月", "12月")
    } else {
        listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
               "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    }

    val daysInMonth = remember(selectedYear, selectedMonth) {
        val cal = Calendar.getInstance()
        cal.set(selectedYear, selectedMonth, 1)
        cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    val days = remember(daysInMonth) {
        (1..daysInMonth).toList()
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Year spinner
        NumberPicker(
            value = selectedYear,
            onValueChange = {
                selectedYear = it
                onDateSelected(it, selectedMonth, selectedDay)
            },
            range = years,
            label = "年",
            modifier = Modifier.weight(1f),
            textColor = textColor,
            testTagPrefix = if (enableTestTags) "kjui_x7q_year" else null
        )

        // Month spinner (uses 1-12 for testTag instead of 0-11)
        NumberPicker(
            value = selectedMonth,
            onValueChange = {
                selectedMonth = it
                onDateSelected(selectedYear, it, selectedDay)
            },
            range = (0..11).toList(),
            labels = months,
            label = "月",
            modifier = Modifier.weight(1f),
            textColor = textColor,
            testTagPrefix = if (enableTestTags) "kjui_x7q_month" else null
        )

        // Day spinner
        NumberPicker(
            value = selectedDay,
            onValueChange = {
                selectedDay = it
                onDateSelected(selectedYear, selectedMonth, it)
            },
            range = days,
            label = "日",
            modifier = Modifier.weight(1f),
            textColor = textColor,
            testTagPrefix = if (enableTestTags) "kjui_x7q_day" else null
        )
    }
}

/**
 * Time wheel picker component (iOS-style spinner)
 */
@Composable
fun TimeWheelPicker(
    selectedTime: Calendar,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    minuteInterval: Int = 1,
    modifier: Modifier = Modifier,
    textColor: Color = Configuration.DatePicker.defaultSheetTextColor,
    enableTestTags: Boolean = true
) {
    var selectedHour by remember { mutableStateOf(selectedTime.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(selectedTime.get(Calendar.MINUTE)) }

    val hours = (0..23).toList()
    val minutes = (0..59 step minuteInterval).toList()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Hour spinner
        NumberPicker(
            value = selectedHour,
            onValueChange = {
                selectedHour = it
                onTimeSelected(it, selectedMinute)
            },
            range = hours,
            label = "時",
            modifier = Modifier.weight(1f),
            textColor = textColor,
            testTagPrefix = if (enableTestTags) "kjui_x7q_hour" else null
        )

        // Minute spinner
        NumberPicker(
            value = selectedMinute,
            onValueChange = {
                selectedMinute = it
                onTimeSelected(selectedHour, it)
            },
            range = minutes,
            label = "分",
            modifier = Modifier.weight(1f),
            textColor = textColor,
            testTagPrefix = if (enableTestTags) "kjui_x7q_minute" else null
        )
    }
}

/**
 * Date and time wheel picker component
 */
@Composable
fun DateTimeWheelPicker(
    selectedDateTime: Calendar,
    onDateTimeSelected: (year: Int, month: Int, day: Int, hour: Int, minute: Int) -> Unit,
    minDate: Calendar? = null,
    maxDate: Calendar? = null,
    minuteInterval: Int = 1,
    dateFormat: String,
    modifier: Modifier = Modifier,
    textColor: Color = Configuration.DatePicker.defaultSheetTextColor
) {
    Column(modifier = modifier) {
        DateWheelPicker(
            selectedDate = selectedDateTime,
            onDateSelected = { year, month, day ->
                onDateTimeSelected(
                    year, month, day,
                    selectedDateTime.get(Calendar.HOUR_OF_DAY),
                    selectedDateTime.get(Calendar.MINUTE)
                )
            },
            minDate = minDate,
            maxDate = maxDate,
            dateFormat = dateFormat,
            modifier = Modifier.fillMaxWidth(),
            textColor = textColor
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TimeWheelPicker(
            selectedTime = selectedDateTime,
            onTimeSelected = { hour, minute ->
                onDateTimeSelected(
                    selectedDateTime.get(Calendar.YEAR),
                    selectedDateTime.get(Calendar.MONTH),
                    selectedDateTime.get(Calendar.DAY_OF_MONTH),
                    hour, minute
                )
            },
            minuteInterval = minuteInterval,
            modifier = Modifier.fillMaxWidth(),
            textColor = textColor
        )
    }
}

/**
 * Simple number picker component with scrollable list
 */
@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: List<Int>,
    labels: List<String>? = null,
    label: String? = null,
    modifier: Modifier = Modifier,
    textColor: Color = Configuration.DatePicker.defaultSheetTextColor,
    testTagPrefix: String? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = textColor
            )
        }
        
        Box(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Use LazyColumn for scrollable list
            val listState = rememberLazyListState()
            val currentIndex = range.indexOf(value)
            
            // Scroll to the selected item on first composition
            LaunchedEffect(Unit) {
                if (currentIndex >= 0) {
                    listState.scrollToItem(
                        index = currentIndex,
                        scrollOffset = -50 // Center the item
                    )
                }
            }
            
            // Detect when scrolling stops and snap to nearest item
            LaunchedEffect(listState.isScrollInProgress) {
                if (!listState.isScrollInProgress) {
                    val centerIndex = listState.firstVisibleItemIndex + 1
                    if (centerIndex in range.indices) {
                        onValueChange(range[centerIndex])
                    }
                }
            }
            
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Add padding items at the start
                item { 
                    Spacer(modifier = Modifier.height(50.dp))
                }
                
                itemsIndexed(range) { index, item ->
                    val isSelected = item == value
                    val distance = kotlin.math.abs(index - currentIndex)
                    Text(
                        text = labels?.getOrNull(index) ?: item.toString().padStart(2, '0'),
                        style = when {
                            isSelected -> MaterialTheme.typography.headlineMedium
                            distance == 1 -> MaterialTheme.typography.titleMedium
                            else -> MaterialTheme.typography.bodyLarge
                        },
                        color = if (isSelected) {
                            textColor
                        } else {
                            textColor.copy(
                                alpha = when (distance) {
                                    1 -> 0.6f
                                    2 -> 0.4f
                                    else -> 0.3f
                                }
                            )
                        },
                        modifier = Modifier
                            .then(
                                if (testTagPrefix != null) Modifier.testTag("${testTagPrefix}_$item")
                                else Modifier
                            )
                            .clickable {
                                onValueChange(item)
                            }
                            .padding(vertical = if (isSelected) 4.dp else 6.dp)
                    )
                }
                
                // Add padding items at the end
                item { 
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
            
            // Add selection indicator lines
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .align(Alignment.Center)
            ) {
                Divider(
                    modifier = Modifier.align(Alignment.TopCenter),
                    color = textColor.copy(alpha = 0.2f),
                    thickness = 1.dp
                )
                Divider(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    color = textColor.copy(alpha = 0.2f),
                    thickness = 1.dp
                )
            }
        }
    }
}

/**
 * Calendar date picker component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateCalendarPicker(
    selectedDate: Calendar,
    onDateSelected: (year: Int, month: Int, day: Int) -> Unit,
    minDate: Calendar? = null,
    maxDate: Calendar? = null,
    dateFormat: String,
    modifier: Modifier = Modifier,
    sheetTextColor: Color = Configuration.DatePicker.defaultSheetTextColor
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.timeInMillis,
        yearRange = (minDate?.get(Calendar.YEAR) ?: 1900)..(maxDate?.get(Calendar.YEAR) ?: 2100)
    )
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "日付を選択",
            style = MaterialTheme.typography.titleMedium,
            color = sheetTextColor,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        DatePicker(
            state = datePickerState,
            modifier = Modifier.fillMaxWidth(),
            showModeToggle = false,
            colors = DatePickerDefaults.colors(
                containerColor = Color.Transparent,
                titleContentColor = sheetTextColor,
                headlineContentColor = sheetTextColor,
                weekdayContentColor = sheetTextColor,
                subheadContentColor = sheetTextColor,
                yearContentColor = sheetTextColor,
                currentYearContentColor = sheetTextColor,
                selectedYearContentColor = Configuration.DatePicker.defaultSheetBackgroundColor,
                selectedYearContainerColor = sheetTextColor,
                dayContentColor = sheetTextColor,
                selectedDayContentColor = Configuration.DatePicker.defaultSheetBackgroundColor,
                selectedDayContainerColor = sheetTextColor,
                todayContentColor = sheetTextColor,
                todayDateBorderColor = sheetTextColor
            )
        )
        
        // Confirm button
        Button(
            onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val cal = Calendar.getInstance().apply { timeInMillis = millis }
                    onDateSelected(
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .testTag("kjui_x7q_done"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Configuration.DatePicker.SheetButton.defaultButtonBackgroundColor,
                contentColor = Configuration.DatePicker.SheetButton.defaultButtonTextColor
            )
        ) {
            Text(
                text = "選択",
                fontSize = Configuration.DatePicker.SheetButton.defaultFontSize.sp,
                fontWeight = FontWeight(Configuration.DatePicker.SheetButton.defaultFontWeight),
                color = Configuration.DatePicker.SheetButton.defaultButtonTextColor
            )
        }
    }
}