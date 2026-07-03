// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/radio_icons_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class RadioIconsTestData(
    var selectedDefaultgroup: String = "option1",
    var selectedCustomgroup: String = "custom1",
    var selectedColor: String = "Red"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): RadioIconsTestData {
            return RadioIconsTestData(
                selectedDefaultgroup = map["selectedDefaultgroup"] as? String ?: "option1",
                selectedCustomgroup = map["selectedCustomgroup"] as? String ?: "custom1",
                selectedColor = map["selectedColor"] as? String ?: "Red"
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["selectedDefaultgroup"] = selectedDefaultgroup
        map["selectedCustomgroup"] = selectedCustomgroup
        map["selectedColor"] = selectedColor
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
