package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.SwitchEventsTestViewModel

data class SwitchEventsTestData(
    var notificationStatus: String = "Notifications are enabled",
    var darkModeStatus: String = "Dark mode is off",
    var connectionStatus: String = "Active: Wi-Fi, Location"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): SwitchEventsTestData {
            return SwitchEventsTestData(
                notificationStatus = map["notificationStatus"] as? String ?: "",
                darkModeStatus = map["darkModeStatus"] as? String ?: "",
                connectionStatus = map["connectionStatus"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: SwitchEventsTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["notificationStatus"] = notificationStatus
        map["darkModeStatus"] = darkModeStatus
        map["connectionStatus"] = connectionStatus
        
        return map
    }
}
