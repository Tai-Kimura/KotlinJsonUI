// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/keyboard_avoidance_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class KeyboardAvoidanceTestData(
    var dynamicModeStatus: String = "OFF",
    var textField1: String = "",
    var textField2: String = "",
    var textField3: String = "",
    var textField4: String = "",
    var textField5: String = "",
    var textView: String = "",
    var title: String = "Keyboard Avoidance Test",
    var toggleDynamicMode: (() -> Unit)? = null,
    var submitForm: (() -> Unit)? = null
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): KeyboardAvoidanceTestData {
            return KeyboardAvoidanceTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "OFF",
                textField1 = map["textField1"] as? String ?: "",
                textField2 = map["textField2"] as? String ?: "",
                textField3 = map["textField3"] as? String ?: "",
                textField4 = map["textField4"] as? String ?: "",
                textField5 = map["textField5"] as? String ?: "",
                textView = map["textView"] as? String ?: "",
                title = map["title"] as? String ?: "Keyboard Avoidance Test",
                toggleDynamicMode = map["toggleDynamicMode"] as? (() -> Unit)?,
                submitForm = map["submitForm"] as? (() -> Unit)?
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["textField1"] = textField1
        map["textField2"] = textField2
        map["textField3"] = textField3
        map["textField4"] = textField4
        map["textField5"] = textField5
        map["textView"] = textView
        map["title"] = title
        toggleDynamicMode?.let { map["toggleDynamicMode"] = it }
        submitForm?.let { map["submitForm"] = it }
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
