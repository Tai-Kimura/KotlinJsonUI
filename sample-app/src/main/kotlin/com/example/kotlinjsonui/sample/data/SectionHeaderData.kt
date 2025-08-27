package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.SectionHeaderViewModel

data class SectionHeaderData(
    var title: String = "Section Header"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): SectionHeaderData {
            return SectionHeaderData(
                title = map["title"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: SectionHeaderViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["title"] = title
        
        return map
    }
}
