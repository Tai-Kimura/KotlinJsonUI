// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/included2.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class Included2Data(
    var countLine: String = "Count: 0",
    var statusLine: String = "Status: Default Status",
    var titleLine: String = "Title: Default Title"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): Included2Data {
            return Included2Data(
                countLine = map["countLine"] as? String ?: "Count: 0",
                statusLine = map["statusLine"] as? String ?: "Status: Default Status",
                titleLine = map["titleLine"] as? String ?: "Title: Default Title"
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["countLine"] = countLine
        map["statusLine"] = statusLine
        map["titleLine"] = titleLine
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
