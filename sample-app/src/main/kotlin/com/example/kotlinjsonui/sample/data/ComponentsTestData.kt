// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/components_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class ComponentsTestData(
    var dynamicModeStatus: String = "OFF",
    var checkbox1IsOn: Boolean = false,
    var progress1Value: Double = 0.6,
    var selectedRadio1: String = "Medium",
    var selectedSegment1: Int = 0,
    var slider1Value: Double = 0.5,
    var toggle1IsOn: Boolean = false,
    var toggleDynamicMode: (() -> Unit)? = null
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): ComponentsTestData {
            return ComponentsTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "OFF",
                checkbox1IsOn = map["checkbox1IsOn"] as? Boolean ?: false,
                progress1Value = (map["progress1Value"] as? Number)?.toDouble() ?: 0.6,
                selectedRadio1 = map["selectedRadio1"] as? String ?: "Medium",
                selectedSegment1 = (map["selectedSegment1"] as? Number)?.toInt() ?: 0,
                slider1Value = (map["slider1Value"] as? Number)?.toDouble() ?: 0.5,
                toggle1IsOn = map["toggle1IsOn"] as? Boolean ?: false,
                toggleDynamicMode = map["toggleDynamicMode"] as? (() -> Unit)?
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["checkbox1IsOn"] = checkbox1IsOn
        map["progress1Value"] = progress1Value
        map["selectedRadio1"] = selectedRadio1
        map["selectedSegment1"] = selectedSegment1
        map["slider1Value"] = slider1Value
        map["toggle1IsOn"] = toggle1IsOn
        toggleDynamicMode?.let { map["toggleDynamicMode"] = it }
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
