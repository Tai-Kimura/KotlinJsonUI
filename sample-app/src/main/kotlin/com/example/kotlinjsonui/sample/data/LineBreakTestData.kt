package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.LineBreakTestViewModel

data class LineBreakTestData(
    var dynamicModeStatus: String = "OFF",
    var longText: String = "This is a very long text that will be used to demonstrate different line break modes in SwiftJsonUI. The text should be long enough to test truncation and wrapping behaviors.",
    var title: String = "Line Break Mode Test"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): LineBreakTestData {
            return LineBreakTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "",
                longText = map["longText"] as? String ?: "",
                title = map["title"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: LineBreakTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["longText"] = longText
        map["title"] = title
        
        return map
    }
}
