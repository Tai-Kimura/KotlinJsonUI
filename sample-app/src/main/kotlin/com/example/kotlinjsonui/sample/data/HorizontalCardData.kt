package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.HorizontalCardViewModel

data class HorizontalCardData(
    var title: String = "Card Title",
    var description: String = "Card description"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): HorizontalCardData {
            return HorizontalCardData(
                title = map["title"] as? String ?: "",
                description = map["description"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: HorizontalCardViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["title"] = title
        map["description"] = description
        
        return map
    }
}
