// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/include_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class IncludeTestData(
    var dynamicModeStatus: String = "OFF",
    var mainCount: Int = 100,
    var mainStatus: String = "Main Active",
    var title: String = "Include Component Test",
    var userName: String = "Test User",
    var toggleDynamicMode: (() -> Unit)? = null,
    var incrementCount: (() -> Unit)? = null,
    var decrementCount: (() -> Unit)? = null,
    var resetCount: (() -> Unit)? = null,
    var changeUserName: (() -> Unit)? = null,
    var toggleStatus: (() -> Unit)? = null,
    var viewCount: Int = 0,
    var viewStatus: String = "Default Status",
    var viewTitle: String = "Default Title"
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): IncludeTestData {
            return IncludeTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "OFF",
                mainCount = (map["mainCount"] as? Number)?.toInt() ?: 100,
                mainStatus = map["mainStatus"] as? String ?: "Main Active",
                title = map["title"] as? String ?: "Include Component Test",
                userName = map["userName"] as? String ?: "Test User",
                toggleDynamicMode = map["toggleDynamicMode"] as? (() -> Unit)?,
                incrementCount = map["incrementCount"] as? (() -> Unit)?,
                decrementCount = map["decrementCount"] as? (() -> Unit)?,
                resetCount = map["resetCount"] as? (() -> Unit)?,
                changeUserName = map["changeUserName"] as? (() -> Unit)?,
                toggleStatus = map["toggleStatus"] as? (() -> Unit)?,
                viewCount = (map["viewCount"] as? Number)?.toInt() ?: 0,
                viewStatus = map["viewStatus"] as? String ?: "Default Status",
                viewTitle = map["viewTitle"] as? String ?: "Default Title"
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["mainCount"] = mainCount
        map["mainStatus"] = mainStatus
        map["title"] = title
        map["userName"] = userName
        toggleDynamicMode?.let { map["toggleDynamicMode"] = it }
        incrementCount?.let { map["incrementCount"] = it }
        decrementCount?.let { map["decrementCount"] = it }
        resetCount?.let { map["resetCount"] = it }
        changeUserName?.let { map["changeUserName"] = it }
        toggleStatus?.let { map["toggleStatus"] = it }
        map["viewCount"] = viewCount
        map["viewStatus"] = viewStatus
        map["viewTitle"] = viewTitle
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
