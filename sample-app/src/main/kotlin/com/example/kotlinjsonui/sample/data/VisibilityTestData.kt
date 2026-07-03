// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/visibility_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class VisibilityTestData(
    var dynamicModeStatus: String = "OFF",
    var title: String = "Visibility & Opacity Test",
    var textVisibility: String = "visible",
    var isHidden: Boolean = false,
    var toggleDynamicMode: (() -> Unit)? = null,
    var toggleVisibility: (() -> Unit)? = null,
    var toggleHidden: (() -> Unit)? = null
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): VisibilityTestData {
            return VisibilityTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "OFF",
                title = map["title"] as? String ?: "Visibility & Opacity Test",
                textVisibility = map["textVisibility"] as? String ?: "visible",
                isHidden = map["isHidden"] as? Boolean ?: false,
                toggleDynamicMode = map["toggleDynamicMode"] as? (() -> Unit)?,
                toggleVisibility = map["toggleVisibility"] as? (() -> Unit)?,
                toggleHidden = map["toggleHidden"] as? (() -> Unit)?
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["title"] = title
        map["textVisibility"] = textVisibility
        map["isHidden"] = isHidden
        toggleDynamicMode?.let { map["toggleDynamicMode"] = it }
        toggleVisibility?.let { map["toggleVisibility"] = it }
        toggleHidden?.let { map["toggleHidden"] = it }
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
