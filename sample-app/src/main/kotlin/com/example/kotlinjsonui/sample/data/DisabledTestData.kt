// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/disabled_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class DisabledTestData(
    var dynamicModeStatus: String = "OFF",
    var isEnabled: Boolean = true,
    var textFieldValue: String = "",
    var title: String = "Disabled State Test",
    var toggleDynamicMode: (() -> Unit)? = null,
    var onEnabledButtonTap: (() -> Unit)? = null,
    var onDisabledButtonTap: (() -> Unit)? = null,
    var onTouchDisabledTap: (() -> Unit)? = null,
    var toggleEnableState: (() -> Unit)? = null,
    var onDynamicButtonTap: (() -> Unit)? = null
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): DisabledTestData {
            return DisabledTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "OFF",
                isEnabled = map["isEnabled"] as? Boolean ?: true,
                textFieldValue = map["textFieldValue"] as? String ?: "",
                title = map["title"] as? String ?: "Disabled State Test",
                toggleDynamicMode = map["toggleDynamicMode"] as? (() -> Unit)?,
                onEnabledButtonTap = map["onEnabledButtonTap"] as? (() -> Unit)?,
                onDisabledButtonTap = map["onDisabledButtonTap"] as? (() -> Unit)?,
                onTouchDisabledTap = map["onTouchDisabledTap"] as? (() -> Unit)?,
                toggleEnableState = map["toggleEnableState"] as? (() -> Unit)?,
                onDynamicButtonTap = map["onDynamicButtonTap"] as? (() -> Unit)?
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["isEnabled"] = isEnabled
        map["textFieldValue"] = textFieldValue
        map["title"] = title
        toggleDynamicMode?.let { map["toggleDynamicMode"] = it }
        onEnabledButtonTap?.let { map["onEnabledButtonTap"] = it }
        onDisabledButtonTap?.let { map["onDisabledButtonTap"] = it }
        onTouchDisabledTap?.let { map["onTouchDisabledTap"] = it }
        toggleEnableState?.let { map["toggleEnableState"] = it }
        onDynamicButtonTap?.let { map["onDynamicButtonTap"] = it }
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
