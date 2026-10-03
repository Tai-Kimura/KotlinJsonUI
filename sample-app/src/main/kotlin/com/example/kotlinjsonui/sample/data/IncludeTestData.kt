// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/include_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data

import com.kotlinjsonui.core.KotlinJsonUI
import com.example.kotlinjsonui.sample.R

data class IncludeTestData(
    var dynamicModeStatus: String = "OFF",
    var mainCount: Int = 100,
    var mainStatus: String = "Main Active",
    var title: String = KotlinJsonUI.localizedString(R.string.test_menu_include_component_test, "Include Component Test"),
    var userName: String = "Test User",
    var toggleDynamicMode: (() -> Unit)? = null,
    var incrementCount: (() -> Unit)? = null,
    var decrementCount: (() -> Unit)? = null,
    var resetCount: (() -> Unit)? = null,
    var changeUserName: (() -> Unit)? = null,
    var toggleStatus: (() -> Unit)? = null,
    var mainCountLabel: String = "",
    var userNameLabel: String = "",
    var mainStatusLabel: String = "",
    var userTitleLine: String = "",
    var mainStatusLine: String = "",
    var mainCountLine: String = "",
    var countLine: String = "Count: 0",
    var statusLine: String = "Status: Default Status",
    var titleLine: String = "Title: Default Title"
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): IncludeTestData {
            return IncludeTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "OFF",
                mainCount = (map["mainCount"] as? Number)?.toInt() ?: 100,
                mainStatus = map["mainStatus"] as? String ?: "Main Active",
                title = map["title"] as? String ?: KotlinJsonUI.localizedString(R.string.test_menu_include_component_test, "Include Component Test"),
                userName = map["userName"] as? String ?: "Test User",
                toggleDynamicMode = map["toggleDynamicMode"] as? (() -> Unit)?,
                incrementCount = map["incrementCount"] as? (() -> Unit)?,
                decrementCount = map["decrementCount"] as? (() -> Unit)?,
                resetCount = map["resetCount"] as? (() -> Unit)?,
                changeUserName = map["changeUserName"] as? (() -> Unit)?,
                toggleStatus = map["toggleStatus"] as? (() -> Unit)?,
                mainCountLabel = map["mainCountLabel"] as? String ?: "",
                userNameLabel = map["userNameLabel"] as? String ?: "",
                mainStatusLabel = map["mainStatusLabel"] as? String ?: "",
                userTitleLine = map["userTitleLine"] as? String ?: "",
                mainStatusLine = map["mainStatusLine"] as? String ?: "",
                mainCountLine = map["mainCountLine"] as? String ?: "",
                countLine = map["countLine"] as? String ?: "Count: 0",
                statusLine = map["statusLine"] as? String ?: "Status: Default Status",
                titleLine = map["titleLine"] as? String ?: "Title: Default Title"
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
        map["mainCountLabel"] = mainCountLabel
        map["userNameLabel"] = userNameLabel
        map["mainStatusLabel"] = mainStatusLabel
        map["userTitleLine"] = userTitleLine
        map["mainStatusLine"] = mainStatusLine
        map["mainCountLine"] = mainCountLine
        map["countLine"] = countLine
        map["statusLine"] = statusLine
        map["titleLine"] = titleLine
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
