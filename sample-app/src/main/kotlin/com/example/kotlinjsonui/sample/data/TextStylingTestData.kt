package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.TextStylingTestViewModel

data class TextStylingTestData(
    var dynamicModeStatus: String = "OFF",
    var title: String = "Text Styling Test"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): TextStylingTestData {
            return TextStylingTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "",
                title = map["title"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: TextStylingTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["title"] = title
        
        // Add onclick action lambdas if viewModel is provided
        viewModel?.let { vm ->
            map["toggleDynamicMode"] = { vm.toggleDynamicMode() }
        }
        
        return map
    }
}
