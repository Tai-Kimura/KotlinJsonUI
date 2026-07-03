// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/binding_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class BindingTestData(
    var dynamicModeStatus: String = "OFF",
    var counter: Int = 0,
    var selectedOption: String = "Option 1",
    var sliderValue: Double = 50.0,
    var textValue: String = "Type something here",
    var title: String = "Data Binding Test",
    var toggleValue: Boolean = false,
    var selectedDate: String = "",
    var selectedDate2: String = "2024-01-15",
    var toggleDynamicMode: (() -> Unit)? = null,
    var decreaseCounter: (() -> Unit)? = null,
    var increaseCounter: (() -> Unit)? = null,
    var sliderChanged: ((String, Float) -> Unit)? = null
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): BindingTestData {
            return BindingTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "OFF",
                counter = (map["counter"] as? Number)?.toInt() ?: 0,
                selectedOption = map["selectedOption"] as? String ?: "Option 1",
                sliderValue = (map["sliderValue"] as? Number)?.toDouble() ?: 50.0,
                textValue = map["textValue"] as? String ?: "Type something here",
                title = map["title"] as? String ?: "Data Binding Test",
                toggleValue = map["toggleValue"] as? Boolean ?: false,
                selectedDate = map["selectedDate"] as? String ?: "",
                selectedDate2 = map["selectedDate2"] as? String ?: "2024-01-15",
                toggleDynamicMode = map["toggleDynamicMode"] as? (() -> Unit)?,
                decreaseCounter = map["decreaseCounter"] as? (() -> Unit)?,
                increaseCounter = map["increaseCounter"] as? (() -> Unit)?,
                sliderChanged = map["sliderChanged"] as? ((String, Float) -> Unit)?
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["counter"] = counter
        map["selectedOption"] = selectedOption
        map["sliderValue"] = sliderValue
        map["textValue"] = textValue
        map["title"] = title
        map["toggleValue"] = toggleValue
        map["selectedDate"] = selectedDate
        map["selectedDate2"] = selectedDate2
        toggleDynamicMode?.let { map["toggleDynamicMode"] = it }
        decreaseCounter?.let { map["decreaseCounter"] = it }
        increaseCounter?.let { map["increaseCounter"] = it }
        sliderChanged?.let { map["sliderChanged"] = it }
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
