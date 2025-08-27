package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.SectionFooterViewModel

data class SectionFooterData(
    var text: String = "End of section"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): SectionFooterData {
            return SectionFooterData(
                text = map["text"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: SectionFooterViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["text"] = text
        
        return map
    }
}
