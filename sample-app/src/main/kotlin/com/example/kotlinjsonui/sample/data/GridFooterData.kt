package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.GridFooterViewModel

data class GridFooterData(
    var title: String = "Cell"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): GridFooterData {
            return GridFooterData(
                title = map["title"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: GridFooterViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["title"] = title
        
        return map
    }
}
