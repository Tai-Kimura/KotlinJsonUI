// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/implemented_attributes_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class ImplementedAttributesTestData(
    var textFieldValue: String = "",
    var selectedRadiogroup: String = "radio1",
    var selectedSegment: Int = 0,
    var handleFocus: (() -> Unit)? = null,
    var handleBlur: (() -> Unit)? = null,
    var handleBeginEditing: (() -> Unit)? = null,
    var handleEndEditing: (() -> Unit)? = null
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): ImplementedAttributesTestData {
            return ImplementedAttributesTestData(
                textFieldValue = map["textFieldValue"] as? String ?: "",
                selectedRadiogroup = map["selectedRadiogroup"] as? String ?: "radio1",
                selectedSegment = (map["selectedSegment"] as? Number)?.toInt() ?: 0,
                handleFocus = map["handleFocus"] as? (() -> Unit)?,
                handleBlur = map["handleBlur"] as? (() -> Unit)?,
                handleBeginEditing = map["handleBeginEditing"] as? (() -> Unit)?,
                handleEndEditing = map["handleEndEditing"] as? (() -> Unit)?
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["textFieldValue"] = textFieldValue
        map["selectedRadiogroup"] = selectedRadiogroup
        map["selectedSegment"] = selectedSegment
        handleFocus?.let { map["handleFocus"] = it }
        handleBlur?.let { map["handleBlur"] = it }
        handleBeginEditing?.let { map["handleBeginEditing"] = it }
        handleEndEditing?.let { map["handleEndEditing"] = it }
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
