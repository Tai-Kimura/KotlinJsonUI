package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.SegmentTestViewModel

data class SegmentTestData(
    var selectedBasicsegment: Int = 0,
    var selectedColorsegment: Int = 1,
    var selectedEventsegment: Int = 0,
    var selectedDisabledsegment: Int = 0,
    var selectedSize: String = "Medium"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): SegmentTestData {
            return SegmentTestData(
                selectedBasicsegment = (map["selectedBasicsegment"] as? Number)?.toInt() ?: 0,
                selectedColorsegment = (map["selectedColorsegment"] as? Number)?.toInt() ?: 0,
                selectedEventsegment = (map["selectedEventsegment"] as? Number)?.toInt() ?: 0,
                selectedDisabledsegment = (map["selectedDisabledsegment"] as? Number)?.toInt() ?: 0,
                selectedSize = map["selectedSize"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: SegmentTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["selectedBasicsegment"] = selectedBasicsegment
        map["selectedColorsegment"] = selectedColorsegment
        map["selectedEventsegment"] = selectedEventsegment
        map["selectedDisabledsegment"] = selectedDisabledsegment
        map["selectedSize"] = selectedSize
        
        return map
    }
}
