// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/line_break_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class LineBreakTestData(
    var dynamicModeStatus: String = "OFF",
    var longText: String = "This is a very long text that will be used to demonstrate different line break modes in SwiftJsonUI. The text should be long enough to test truncation and wrapping behaviors.",
    var title: String = "Line Break Mode Test",
    var toggleDynamicMode: (() -> Unit)? = null
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): LineBreakTestData {
            return LineBreakTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "OFF",
                longText = map["longText"] as? String ?: "This is a very long text that will be used to demonstrate different line break modes in SwiftJsonUI. The text should be long enough to test truncation and wrapping behaviors.",
                title = map["title"] as? String ?: "Line Break Mode Test",
                toggleDynamicMode = map["toggleDynamicMode"] as? (() -> Unit)?
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["longText"] = longText
        map["title"] = title
        toggleDynamicMode?.let { map["toggleDynamicMode"] = it }
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
