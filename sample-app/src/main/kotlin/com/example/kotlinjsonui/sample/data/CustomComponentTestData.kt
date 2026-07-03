// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/custom_component_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data

import androidx.compose.ui.graphics.Color

data class CustomComponentTestData(
    var cardTitle: String = "Dynamic Card Title",
    var cardSubtitle: String = "Dynamic subtitle for testing",
    var itemCount: Int = 10,
    var currentStatus: String = "Online",
    var statusColor: Color = Color.Green,
    var notificationCount: Int = 3,
    var dynamicModeStatus: String = "OFF",
    var toggleDynamicMode: (() -> Unit)? = null,
    var incrementCount: (() -> Unit)? = null,
    var decrementCount: (() -> Unit)? = null
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): CustomComponentTestData {
            return CustomComponentTestData(
                cardTitle = map["cardTitle"] as? String ?: "Dynamic Card Title",
                cardSubtitle = map["cardSubtitle"] as? String ?: "Dynamic subtitle for testing",
                itemCount = (map["itemCount"] as? Number)?.toInt() ?: 10,
                currentStatus = map["currentStatus"] as? String ?: "Online",
                statusColor = map["statusColor"] as? Color ?: Color.Green,
                notificationCount = (map["notificationCount"] as? Number)?.toInt() ?: 3,
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "OFF",
                toggleDynamicMode = map["toggleDynamicMode"] as? (() -> Unit)?,
                incrementCount = map["incrementCount"] as? (() -> Unit)?,
                decrementCount = map["decrementCount"] as? (() -> Unit)?
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["cardTitle"] = cardTitle
        map["cardSubtitle"] = cardSubtitle
        map["itemCount"] = itemCount
        map["currentStatus"] = currentStatus
        map["statusColor"] = statusColor
        map["notificationCount"] = notificationCount
        map["dynamicModeStatus"] = dynamicModeStatus
        toggleDynamicMode?.let { map["toggleDynamicMode"] = it }
        incrementCount?.let { map["incrementCount"] = it }
        decrementCount?.let { map["decrementCount"] = it }
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
