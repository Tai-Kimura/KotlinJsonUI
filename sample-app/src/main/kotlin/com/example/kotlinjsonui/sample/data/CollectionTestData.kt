// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/collection_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class CollectionTestData(
    var dynamicModeEnabled: Boolean = false,
    var dynamicModeStatus: String = "OFF",
    var items1: com.kotlinjsonui.data.CollectionDataSource = com.kotlinjsonui.data.CollectionDataSource(),
    var mixedItems: com.kotlinjsonui.data.CollectionDataSource = com.kotlinjsonui.data.CollectionDataSource(),
    var horizontalItems: com.kotlinjsonui.data.CollectionDataSource = com.kotlinjsonui.data.CollectionDataSource(),
    var sectionedItems: com.kotlinjsonui.data.CollectionDataSource = com.kotlinjsonui.data.CollectionDataSource(),
    var multiSectionItems: com.kotlinjsonui.data.CollectionDataSource = com.kotlinjsonui.data.CollectionDataSource(),
    var toggleDynamicMode: (() -> Unit)? = null
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): CollectionTestData {
            return CollectionTestData(
                dynamicModeEnabled = map["dynamicModeEnabled"] as? Boolean ?: false,
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "OFF",
                items1 = com.kotlinjsonui.data.CollectionDataSource(),
                mixedItems = com.kotlinjsonui.data.CollectionDataSource(),
                horizontalItems = com.kotlinjsonui.data.CollectionDataSource(),
                sectionedItems = com.kotlinjsonui.data.CollectionDataSource(),
                multiSectionItems = com.kotlinjsonui.data.CollectionDataSource(),
                toggleDynamicMode = map["toggleDynamicMode"] as? (() -> Unit)?
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeEnabled"] = dynamicModeEnabled
        map["dynamicModeStatus"] = dynamicModeStatus
        map["items1"] = items1
        map["mixedItems"] = mixedItems
        map["horizontalItems"] = horizontalItems
        map["sectionedItems"] = sectionedItems
        map["multiSectionItems"] = multiSectionItems
        toggleDynamicMode?.let { map["toggleDynamicMode"] = it }
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
