// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/date_picker_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


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
    var title: String = "Date Picker Test",
    var toggleDynamicMode: (() -> Unit)? = null
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): DatePickerTestData {
            return DatePickerTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "OFF",
                endDate = map["endDate"] as? String ?: "2025-12-31",
                selectedDate = map["selectedDate"] as? String ?: "2025-08-11",
                selectedDate2 = map["selectedDate2"] as? String ?: "",
                selectedTime = map["selectedTime"] as? String ?: "14:30",
                selectedDateTime = map["selectedDateTime"] as? String ?: "",
                selectedTimeInterval = map["selectedTimeInterval"] as? String ?: "",
                selectedCalendarDate = map["selectedCalendarDate"] as? String ?: "",
                startDate = map["startDate"] as? String ?: "2025-01-01",
                title = map["title"] as? String ?: "Date Picker Test",
                toggleDynamicMode = map["toggleDynamicMode"] as? (() -> Unit)?
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
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
        toggleDynamicMode?.let { map["toggleDynamicMode"] = it }
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
