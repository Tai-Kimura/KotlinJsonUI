package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.CollectionTestViewModel

data class CollectionTestData(
    var dynamicModeEnabled: Boolean = false,
    var dynamicModeStatus: String = "OFF",
    var items1: com.kotlinjsonui.data.CollectionDataSource = com.kotlinjsonui.data.CollectionDataSource(),
    var mixedItems: com.kotlinjsonui.data.CollectionDataSource = com.kotlinjsonui.data.CollectionDataSource(),
    var horizontalItems: com.kotlinjsonui.data.CollectionDataSource = com.kotlinjsonui.data.CollectionDataSource(),
    var sectionedItems: com.kotlinjsonui.data.CollectionDataSource = com.kotlinjsonui.data.CollectionDataSource(),
    var multiSectionItems: com.kotlinjsonui.data.CollectionDataSource = com.kotlinjsonui.data.CollectionDataSource()
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): CollectionTestData {
            return CollectionTestData(
                dynamicModeEnabled = map["dynamicModeEnabled"] as? Boolean ?: false,
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "",
                items1 = com.kotlinjsonui.data.CollectionDataSource(),
                mixedItems = com.kotlinjsonui.data.CollectionDataSource(),
                horizontalItems = com.kotlinjsonui.data.CollectionDataSource(),
                sectionedItems = com.kotlinjsonui.data.CollectionDataSource(),
                multiSectionItems = com.kotlinjsonui.data.CollectionDataSource()
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: CollectionTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeEnabled"] = dynamicModeEnabled
        map["dynamicModeStatus"] = dynamicModeStatus
        map["items1"] = items1
        map["mixedItems"] = mixedItems
        map["horizontalItems"] = horizontalItems
        map["sectionedItems"] = sectionedItems
        map["multiSectionItems"] = multiSectionItems
        
        // Add onclick action lambdas if viewModel is provided
        viewModel?.let { vm ->
            map["toggleDynamicMode"] = { vm.toggleDynamicMode() }
        }
        
        return map
    }
}
