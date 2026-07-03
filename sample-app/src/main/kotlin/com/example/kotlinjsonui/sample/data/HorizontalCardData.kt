// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/horizontal_card.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class HorizontalCardData(
    var title: String = "Card Title",
    var description: String = "Card description"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): HorizontalCardData {
            return HorizontalCardData(
                title = map["title"] as? String ?: "Card Title",
                description = map["description"] as? String ?: "Card description"
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["title"] = title
        map["description"] = description
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
