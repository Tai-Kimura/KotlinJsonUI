package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class TextViewHintTestData(
    var dynamicModeStatus: String = "OFF",
    var flexibleText: String = "",
    var simpleText: String = ""
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): TextViewHintTestData {
            return TextViewHintTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "",
                flexibleText = map["flexibleText"] as? String ?: "",
                simpleText = map["simpleText"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: TextViewHintTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["flexibleText"] = flexibleText
        map["simpleText"] = simpleText
        
        return map
    }
}
