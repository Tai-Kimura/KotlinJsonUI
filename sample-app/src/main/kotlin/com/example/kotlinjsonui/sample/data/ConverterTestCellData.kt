// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/converter_test_cell.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class ConverterTestCellData(
    var item: Map<String, Any> = emptyMap()
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): ConverterTestCellData {
            return ConverterTestCellData(
                item = map["item"] as? Map<String, Any> ?: emptyMap()
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["item"] = item
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
