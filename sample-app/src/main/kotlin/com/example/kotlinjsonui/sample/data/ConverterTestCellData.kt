package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.ConverterTestCellViewModel

data class ConverterTestCellData(
    var subtitle: String = "Description",
    var title: String = "Item"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): ConverterTestCellData {
            return ConverterTestCellData(
                subtitle = map["subtitle"] as? String ?: "",
                title = map["title"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: ConverterTestCellViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["subtitle"] = subtitle
        map["title"] = title
        
        return map
    }
}
