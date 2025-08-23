package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.SegmentTestViewModel

data class SegmentTestData(
    var selectedBasic: Int = 0,
    var selectedColor: Int = 1,
    var selectedEvent: Int = 1,
    var selectedDisabled: Int = 2,
    var selectedSize: String = "Medium"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): SegmentTestData {
            return SegmentTestData(
                selectedBasic = (map["selectedBasic"] as? Number)?.toInt() ?: 0,
                selectedColor = (map["selectedColor"] as? Number)?.toInt() ?: 0,
                selectedEvent = (map["selectedEvent"] as? Number)?.toInt() ?: 0,
                selectedDisabled = (map["selectedDisabled"] as? Number)?.toInt() ?: 0,
                selectedSize = map["selectedSize"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: SegmentTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["selectedBasic"] = selectedBasic
        map["selectedColor"] = selectedColor
        map["selectedEvent"] = selectedEvent
        map["selectedDisabled"] = selectedDisabled
        map["selectedSize"] = selectedSize
        
        return map
    }
}
