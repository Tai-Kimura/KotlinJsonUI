package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.WeightTestWithFixedViewModel

data class WeightTestWithFixedData(
    var dynamicModeStatus: String = "OFF",
    var title: String = "Weight + Fixed Size Test"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): WeightTestWithFixedData {
            return WeightTestWithFixedData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "",
                title = map["title"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: WeightTestWithFixedViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["title"] = title
        
        return map
    }
}
