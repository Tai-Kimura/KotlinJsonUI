// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/included2.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class Included2Data(
    var viewCount: Int = 0,
    var viewStatus: String = "Default Status",
    var viewTitle: String = "Default Title"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): Included2Data {
            return Included2Data(
                viewCount = (map["viewCount"] as? Number)?.toInt() ?: 0,
                viewStatus = map["viewStatus"] as? String ?: "Default Status",
                viewTitle = map["viewTitle"] as? String ?: "Default Title"
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["viewCount"] = viewCount
        map["viewStatus"] = viewStatus
        map["viewTitle"] = viewTitle
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
