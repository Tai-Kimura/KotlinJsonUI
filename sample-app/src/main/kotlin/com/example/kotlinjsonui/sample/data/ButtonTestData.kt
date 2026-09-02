// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/button_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data

import com.kotlinjsonui.core.KotlinJsonUI
import com.example.kotlinjsonui.sample.R

data class ButtonTestData(
    var title: String = KotlinJsonUI.localizedString(R.string.test_menu_button_test_2, "Button Test")
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): ButtonTestData {
            return ButtonTestData(
                title = map["title"] as? String ?: KotlinJsonUI.localizedString(R.string.test_menu_button_test_2, "Button Test")
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["title"] = title
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
