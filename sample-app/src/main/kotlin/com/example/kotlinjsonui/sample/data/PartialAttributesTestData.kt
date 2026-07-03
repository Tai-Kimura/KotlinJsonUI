// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/partial_attributes_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class PartialAttributesTestData(
    var navigateToPage1: (() -> Unit)? = null,
    var navigateToPage2: (() -> Unit)? = null
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): PartialAttributesTestData {
            return PartialAttributesTestData(
                navigateToPage1 = map["navigateToPage1"] as? (() -> Unit)?,
                navigateToPage2 = map["navigateToPage2"] as? (() -> Unit)?
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        navigateToPage1?.let { map["navigateToPage1"] = it }
        navigateToPage2?.let { map["navigateToPage2"] = it }
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
