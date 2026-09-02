// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/converter_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data

import com.kotlinjsonui.core.KotlinJsonUI
import com.example.kotlinjsonui.sample.R

data class ConverterTestData(
    var dynamicModeStatus: String = "OFF",
    var items: com.kotlinjsonui.data.CollectionDataSource = com.kotlinjsonui.data.CollectionDataSource(),
    var title: String = KotlinJsonUI.localizedString(R.string.test_menu_converter_components_test, "Converter Components Test"),
    var toggleDynamicMode: (() -> Unit)? = null
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): ConverterTestData {
            return ConverterTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "OFF",
                items = com.kotlinjsonui.data.CollectionDataSource(),
                title = map["title"] as? String ?: KotlinJsonUI.localizedString(R.string.test_menu_converter_components_test, "Converter Components Test"),
                toggleDynamicMode = map["toggleDynamicMode"] as? (() -> Unit)?
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["items"] = items
        map["title"] = title
        toggleDynamicMode?.let { map["toggleDynamicMode"] = it }
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
