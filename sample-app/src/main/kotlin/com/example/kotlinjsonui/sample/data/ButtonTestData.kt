package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.ButtonTestViewModel

data class ButtonTestData(
    var title: String = "'ButtonTest'"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): ButtonTestData {
            return ButtonTestData(
                title = map["title"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: ButtonTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["title"] = title
        
        // Add onclick action lambdas if viewModel is provided
        viewModel?.let { vm ->
            map["onGetStarted"] = { vm.onGetStarted() }
        }
        
        return map
    }
}
