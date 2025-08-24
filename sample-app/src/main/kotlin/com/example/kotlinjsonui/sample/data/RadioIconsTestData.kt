package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.RadioIconsTestViewModel

data class RadioIconsTestData(
    var selectedDefaultgroup: String = "option1",
    var selectedCustomgroup: String = "custom1",
    var selectedColor: String = "Red"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): RadioIconsTestData {
            return RadioIconsTestData(
                selectedDefaultgroup = map["selectedDefaultgroup"] as? String ?: "",
                selectedCustomgroup = map["selectedCustomgroup"] as? String ?: "",
                selectedColor = map["selectedColor"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: RadioIconsTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["selectedDefaultgroup"] = selectedDefaultgroup
        map["selectedCustomgroup"] = selectedCustomgroup
        map["selectedColor"] = selectedColor
        
        return map
    }
}
