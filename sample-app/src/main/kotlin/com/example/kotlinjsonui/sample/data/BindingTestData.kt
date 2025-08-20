package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.BindingTestViewModel

data class BindingTestData(
    var dynamicModeStatus: String = "OFF",
    var counter: Int = 0,
    var selectedOption: String = "Option 1",
    var sliderValue: Double = 50.0,
    var textValue: String = "Type something here",
    var title: String = "Data Binding Test",
    var toggleValue: Boolean = false,
    var selectedDate: String = "",
    var selectedDate2: String = "2024-01-15"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): BindingTestData {
            return BindingTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "",
                counter = (map["counter"] as? Number)?.toInt() ?: 0,
                selectedOption = map["selectedOption"] as? String ?: "",
                sliderValue = (map["sliderValue"] as? Number)?.toDouble() ?: 0.0,
                textValue = map["textValue"] as? String ?: "",
                title = map["title"] as? String ?: "",
                toggleValue = map["toggleValue"] as? Boolean ?: false,
                selectedDate = map["selectedDate"] as? String ?: "",
                selectedDate2 = map["selectedDate2"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: BindingTestViewModel? = null): MutableMap<String, Any> {
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
        
        // Add onclick action lambdas if viewModel is provided
        viewModel?.let { vm ->
            map["decreaseCounter"] = { vm.decreaseCounter() }
            map["increaseCounter"] = { vm.increaseCounter() }
            map["toggleChanged"] = { vm.toggleChanged() }
        }
        
        return map
    }
}
