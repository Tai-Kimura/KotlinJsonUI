package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.DatePickerTestViewModel

data class DatePickerTestData(
    var dynamicModeStatus: String = "OFF",
    var endDate: String = "2025-12-31",
    var selectedDate: String = "2025-08-11",
    var selectedDate2: String = "",
    var selectedTime: String = "14:30",
    var selectedDateTime: String = "",
    var selectedTimeInterval: String = "",
    var selectedCalendarDate: String = "",
    var startDate: String = "2025-01-01",
    var title: String = "Date Picker Test"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): DatePickerTestData {
            return DatePickerTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "",
                endDate = map["endDate"] as? String ?: "",
                selectedDate = map["selectedDate"] as? String ?: "",
                selectedDate2 = map["selectedDate2"] as? String ?: "",
                selectedTime = map["selectedTime"] as? String ?: "",
                selectedDateTime = map["selectedDateTime"] as? String ?: "",
                selectedTimeInterval = map["selectedTimeInterval"] as? String ?: "",
                selectedCalendarDate = map["selectedCalendarDate"] as? String ?: "",
                startDate = map["startDate"] as? String ?: "",
                title = map["title"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: DatePickerTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["endDate"] = endDate
        map["selectedDate"] = selectedDate
        map["selectedDate2"] = selectedDate2
        map["selectedTime"] = selectedTime
        map["selectedDateTime"] = selectedDateTime
        map["selectedTimeInterval"] = selectedTimeInterval
        map["selectedCalendarDate"] = selectedCalendarDate
        map["startDate"] = startDate
        map["title"] = title
        
        return map
    }
}
