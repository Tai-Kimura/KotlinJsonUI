package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.CollectionTestViewModel

data class CollectionTestData(
    var dynamicModeEnabled: Boolean = false,
    var products: List<Map<String, Any>> = [],
    var simpleItems: List<Map<String, Any>> = []
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): CollectionTestData {
            return CollectionTestData(
                dynamicModeEnabled = map["dynamicModeEnabled"] as? Boolean ?: false,
                products = map["products"] as? List<Map<String, Any>>,
                simpleItems = map["simpleItems"] as? List<Map<String, Any>>
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: CollectionTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeEnabled"] = dynamicModeEnabled
        map["products"] = products
        map["simpleItems"] = simpleItems
        
        return map
    }
}
