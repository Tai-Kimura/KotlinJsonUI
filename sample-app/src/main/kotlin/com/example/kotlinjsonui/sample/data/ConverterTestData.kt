package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class ConverterTestData(
    var dynamicModeStatus: String = "OFF",
    var items: CollectionDataSource = CollectionDataSource(),
    var title: String = "Converter Components Test"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): ConverterTestData {
            return ConverterTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "",
                items = map["items"] as? CollectionDataSource,
                title = map["title"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: ConverterTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["items"] = items
        map["title"] = title
        
        return map
    }
}
