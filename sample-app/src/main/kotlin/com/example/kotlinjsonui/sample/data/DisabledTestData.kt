package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.DisabledTestViewModel

data class DisabledTestData(
    var dynamicModeStatus: String = "OFF",
    var isEnabled: Boolean = true,
    var textFieldValue: String = "",
    var title: String = "Disabled State Test"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): DisabledTestData {
            return DisabledTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "",
                isEnabled = map["isEnabled"] as? Boolean ?: false,
                textFieldValue = map["textFieldValue"] as? String ?: "",
                title = map["title"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: DisabledTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["isEnabled"] = isEnabled
        map["textFieldValue"] = textFieldValue
        map["title"] = title
        
        // Add onclick action lambdas if viewModel is provided
        viewModel?.let { vm ->
            map["onEnabledButtonTap"] = { vm.onEnabledButtonTap() }
            map["onDisabledButtonTap"] = { vm.onDisabledButtonTap() }
            map["onTouchDisabledTap"] = { vm.onTouchDisabledTap() }
            map["toggleEnableState"] = { vm.toggleEnableState() }
            map["onDynamicButtonTap"] = { vm.onDynamicButtonTap() }
        }
        
        return map
    }
}
