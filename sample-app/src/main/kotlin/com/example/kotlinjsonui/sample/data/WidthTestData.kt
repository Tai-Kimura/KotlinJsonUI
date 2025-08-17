package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class WidthTestData(
    var dynamicModeStatus: String = "OFF"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): WidthTestData {
            return WidthTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: WidthTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        
        return map
    }
}
