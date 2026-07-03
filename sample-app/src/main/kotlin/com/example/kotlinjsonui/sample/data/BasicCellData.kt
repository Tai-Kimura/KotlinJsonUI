// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/basic_cell.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class BasicCellData(
    var title: String = "Basic Item",
    var subtitle: String = "Description"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): BasicCellData {
            return BasicCellData(
                title = map["title"] as? String ?: "Basic Item",
                subtitle = map["subtitle"] as? String ?: "Description"
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["title"] = title
        map["subtitle"] = subtitle
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
