// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/button_enabled_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class ButtonEnabledTestData(
    var dynamicModeStatus: String = "OFF",
    var isButtonEnabled: Boolean = true,
    var title: String = "Button Enabled Test",
    var toggleDynamicMode: (() -> Unit)? = null,
    var testAction: (() -> Unit)? = null,
    var toggleEnabled: (() -> Unit)? = null,
    var neverCalled: (() -> Unit)? = null,
    var alwaysCalled: (() -> Unit)? = null
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): ButtonEnabledTestData {
            return ButtonEnabledTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "OFF",
                isButtonEnabled = map["isButtonEnabled"] as? Boolean ?: true,
                title = map["title"] as? String ?: "Button Enabled Test",
                toggleDynamicMode = map["toggleDynamicMode"] as? (() -> Unit)?,
                testAction = map["testAction"] as? (() -> Unit)?,
                toggleEnabled = map["toggleEnabled"] as? (() -> Unit)?,
                neverCalled = map["neverCalled"] as? (() -> Unit)?,
                alwaysCalled = map["alwaysCalled"] as? (() -> Unit)?
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["isButtonEnabled"] = isButtonEnabled
        map["title"] = title
        toggleDynamicMode?.let { map["toggleDynamicMode"] = it }
        testAction?.let { map["testAction"] = it }
        toggleEnabled?.let { map["toggleEnabled"] = it }
        neverCalled?.let { map["neverCalled"] = it }
        alwaysCalled?.let { map["alwaysCalled"] = it }
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
