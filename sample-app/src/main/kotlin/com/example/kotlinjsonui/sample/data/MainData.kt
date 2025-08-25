package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.MainViewModel

data class MainData(
    var title: String = "'Welcome'"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): MainData {
            return MainData(
                title = map["title"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: MainViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["title"] = title
        
        // Add onclick action lambdas if viewModel is provided
        viewModel?.let { vm ->
            map["getStarted"] = { vm.getStarted() }
        }
        
        return map
    }
}
