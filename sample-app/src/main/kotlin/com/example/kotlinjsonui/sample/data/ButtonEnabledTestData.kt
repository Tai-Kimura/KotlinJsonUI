package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.ButtonEnabledTestViewModel

data class ButtonEnabledTestData(
    var dynamicModeStatus: String = "OFF",
    var isButtonEnabled: Boolean = true,
    var title: String = "Button Enabled Test"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): ButtonEnabledTestData {
            return ButtonEnabledTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "",
                isButtonEnabled = map["isButtonEnabled"] as? Boolean ?: false,
                title = map["title"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: ButtonEnabledTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["isButtonEnabled"] = isButtonEnabled
        map["title"] = title
        
        // Add onclick action lambdas if viewModel is provided
        viewModel?.let { vm ->
            map["testAction"] = { vm.testAction() }
            map["toggleEnabled"] = { vm.toggleEnabled() }
            map["neverCalled"] = { vm.neverCalled() }
            map["alwaysCalled"] = { vm.alwaysCalled() }
        }
        
        return map
    }
}
