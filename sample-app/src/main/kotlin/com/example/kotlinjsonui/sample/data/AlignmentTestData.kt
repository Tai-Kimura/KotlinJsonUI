package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.AlignmentTestViewModel

data class AlignmentTestData(
    var dynamicModeStatus: String = "OFF",
    var title: String = "Single Alignment Test"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): AlignmentTestData {
            return AlignmentTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "",
                title = map["title"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: AlignmentTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["title"] = title
        
        return map
    }
}
