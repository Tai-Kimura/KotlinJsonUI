// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/text_view_hint_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class TextViewHintTestData(
    var dynamicModeStatus: String = "OFF",
    var flexibleText: String = "",
    var simpleText: String = "",
    var toggleDynamicMode: (() -> Unit)? = null,
    var simpleTextViewIsFocused: Boolean = false,
    var flexibleTextViewIsFocused: Boolean = false
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): TextViewHintTestData {
            return TextViewHintTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "OFF",
                flexibleText = map["flexibleText"] as? String ?: "",
                simpleText = map["simpleText"] as? String ?: "",
                toggleDynamicMode = map["toggleDynamicMode"] as? (() -> Unit)?,
                simpleTextViewIsFocused = map["simpleTextViewIsFocused"] as? Boolean ?: false,
                flexibleTextViewIsFocused = map["flexibleTextViewIsFocused"] as? Boolean ?: false
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["flexibleText"] = flexibleText
        map["simpleText"] = simpleText
        toggleDynamicMode?.let { map["toggleDynamicMode"] = it }
        map["simpleTextViewIsFocused"] = simpleTextViewIsFocused
        map["flexibleTextViewIsFocused"] = flexibleTextViewIsFocused
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
