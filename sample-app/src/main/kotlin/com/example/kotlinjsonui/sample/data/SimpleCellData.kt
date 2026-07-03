// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/simple_cell.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class SimpleCellData(
    var title: String = "",
    var value: String = ""
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): SimpleCellData {
            return SimpleCellData(
                title = map["title"] as? String ?: "",
                value = map["value"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["title"] = title
        map["value"] = value
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
