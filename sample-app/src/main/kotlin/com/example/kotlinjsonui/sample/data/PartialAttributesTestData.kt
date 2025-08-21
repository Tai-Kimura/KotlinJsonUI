package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.PartialAttributesTestViewModel

data class PartialAttributesTestData(
    // No data properties defined in JSON
    val placeholder: String = "placeholder"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): PartialAttributesTestData {
            return PartialAttributesTestData(
                placeholder = "placeholder"
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: PartialAttributesTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        // No properties to add
        
        return map
    }
}
