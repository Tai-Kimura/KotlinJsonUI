// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/segment_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class SegmentTestData(
    var selectedBasic: Int = 0,
    var selectedColor: Int = 1,
    var selectedEvent: Int = 1,
    var selectedDisabled: Int = 2,
    var selectedSize: String = "Medium",
    var handleSegmentChange: ((String, Int) -> Unit)? = null
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): SegmentTestData {
            return SegmentTestData(
                selectedBasic = (map["selectedBasic"] as? Number)?.toInt() ?: 0,
                selectedColor = (map["selectedColor"] as? Number)?.toInt() ?: 1,
                selectedEvent = (map["selectedEvent"] as? Number)?.toInt() ?: 1,
                selectedDisabled = (map["selectedDisabled"] as? Number)?.toInt() ?: 2,
                selectedSize = map["selectedSize"] as? String ?: "Medium",
                handleSegmentChange = map["handleSegmentChange"] as? ((String, Int) -> Unit)?
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["selectedBasic"] = selectedBasic
        map["selectedColor"] = selectedColor
        map["selectedEvent"] = selectedEvent
        map["selectedDisabled"] = selectedDisabled
        map["selectedSize"] = selectedSize
        handleSegmentChange?.let { map["handleSegmentChange"] = it }
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
