// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/feature_cell.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class FeatureCellData(
    var badge: String = "FEATURED",
    var title: String = "Feature Title",
    var description: String = "Amazing feature description"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): FeatureCellData {
            return FeatureCellData(
                badge = map["badge"] as? String ?: "FEATURED",
                title = map["title"] as? String ?: "Feature Title",
                description = map["description"] as? String ?: "Amazing feature description"
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["badge"] = badge
        map["title"] = title
        map["description"] = description
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
