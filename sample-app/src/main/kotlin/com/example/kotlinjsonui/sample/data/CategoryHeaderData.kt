package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.CategoryHeaderViewModel

data class CategoryHeaderData(
    var title: String = "Cell"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): CategoryHeaderData {
            return CategoryHeaderData(
                title = map["title"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: CategoryHeaderViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["title"] = title
        
        return map
    }
}
