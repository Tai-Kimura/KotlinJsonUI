package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class RelativeTestData(
    var dynamicModeStatus: String = "OFF",
    var title: String = "RelativePosition Test - Margins & Padding"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): RelativeTestData {
            return RelativeTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "",
                title = map["title"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: RelativeTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["title"] = title
        
        return map
    }
}
