package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.FeatureCellViewModel

data class FeatureCellData(
    var badge: String = "FEATURED",
    var title: String = "Feature Title",
    var description: String = "Amazing feature description"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): FeatureCellData {
            return FeatureCellData(
                badge = map["badge"] as? String ?: "",
                title = map["title"] as? String ?: "",
                description = map["description"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: FeatureCellViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["badge"] = badge
        map["title"] = title
        map["description"] = description
        
        return map
    }
}
