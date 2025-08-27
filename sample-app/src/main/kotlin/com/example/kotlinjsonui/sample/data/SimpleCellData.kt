package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.SimpleCellViewModel

data class SimpleCellData(
    var title: String = "",
    var value: String = ""
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): SimpleCellData {
            return SimpleCellData(
                title = map["title"] as? String ?: "",
                value = map["value"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: SimpleCellViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["title"] = title
        map["value"] = value
        
        return map
    }
}
