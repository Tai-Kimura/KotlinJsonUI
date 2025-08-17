package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class Included2Data(
    var viewCount: Int = 0,
    var viewStatus: String = "Default Status",
    var viewTitle: String = "Default Title"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): Included2Data {
            return Included2Data(
                viewCount = (map["viewCount"] as? Number)?.toInt() ?: 0,
                viewStatus = map["viewStatus"] as? String ?: "",
                viewTitle = map["viewTitle"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: Included2ViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["viewCount"] = viewCount
        map["viewStatus"] = viewStatus
        map["viewTitle"] = viewTitle
        
        return map
    }
}
