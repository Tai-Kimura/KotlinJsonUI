package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.BasicCellViewModel

data class BasicCellData(
    var title: String = "Basic Item",
    var subtitle: String = "Description"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): BasicCellData {
            return BasicCellData(
                title = map["title"] as? String ?: "",
                subtitle = map["subtitle"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: BasicCellViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["title"] = title
        map["subtitle"] = subtitle
        
        return map
    }
}
