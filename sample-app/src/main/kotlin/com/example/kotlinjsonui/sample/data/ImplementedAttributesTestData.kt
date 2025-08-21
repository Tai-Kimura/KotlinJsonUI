package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.ImplementedAttributesTestViewModel

data class ImplementedAttributesTestData(
    var textFieldValue: String = "",
    var selectedRadiogroup: String = "radio1",
    var selectedSegment: Int = 0
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): ImplementedAttributesTestData {
            return ImplementedAttributesTestData(
                textFieldValue = map["textFieldValue"] as? String ?: "",
                selectedRadiogroup = map["selectedRadiogroup"] as? String ?: "",
                selectedSegment = (map["selectedSegment"] as? Number)?.toInt() ?: 0
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: ImplementedAttributesTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["textFieldValue"] = textFieldValue
        map["selectedRadiogroup"] = selectedRadiogroup
        map["selectedSegment"] = selectedSegment
        
        return map
    }
}
