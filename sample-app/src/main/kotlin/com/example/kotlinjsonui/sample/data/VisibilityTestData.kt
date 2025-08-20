package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.VisibilityTestViewModel

data class VisibilityTestData(
    var dynamicModeStatus: String = "OFF",
    var title: String = "Visibility & Opacity Test",
    var textVisibility: String = "visible",
    var isHidden: Boolean = false
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): VisibilityTestData {
            return VisibilityTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "",
                title = map["title"] as? String ?: "",
                textVisibility = map["textVisibility"] as? String ?: "",
                isHidden = map["isHidden"] as? Boolean ?: false
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: VisibilityTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["title"] = title
        map["textVisibility"] = textVisibility
        map["isHidden"] = isHidden
        
        return map
    }
}
