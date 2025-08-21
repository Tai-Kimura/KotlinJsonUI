package com.example.kotlinjsonui.sample.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.SwitchEventsTestData

class SwitchEventsTestViewModel : ViewModel() {
    // JSON file reference for hot reload
    val jsonFileName = "switch_events_test"
    
    // Data model
    private val _data = MutableStateFlow(SwitchEventsTestData())
    val data: StateFlow<SwitchEventsTestData> = _data.asStateFlow()
    
    // Action handlers
    fun onGetStarted() {
        // Handle button tap
    }
    
    // Switch event handlers
    fun handleNotificationChange(enabled: Boolean) {
        println("Notifications: $enabled")
        val currentData = _data.value
        _data.value = currentData.copy(enableNotifications = enabled)
    }
    
    fun handleDarkModeChange(enabled: Boolean) {
        println("Dark mode: $enabled")
        val currentData = _data.value
        _data.value = currentData.copy(enableDarkMode = enabled)
    }
    
    fun handleWifiChange(enabled: Boolean) {
        println("WiFi: $enabled")
        val currentData = _data.value
        _data.value = currentData.copy(wifiEnabled = enabled)
    }
    
    fun handleBluetoothChange(enabled: Boolean) {
        println("Bluetooth: $enabled")
        val currentData = _data.value
        _data.value = currentData.copy(bluetoothEnabled = enabled)
    }
    
    fun handleLocationChange(enabled: Boolean) {
        println("Location: $enabled")
        val currentData = _data.value
        _data.value = currentData.copy(locationEnabled = enabled)
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentData = _data.value
        val newData = currentData.copy(
            enableNotifications = updates["enableNotifications"] as? Boolean ?: currentData.enableNotifications,
            enableDarkMode = updates["enableDarkMode"] as? Boolean ?: currentData.enableDarkMode,
            wifiEnabled = updates["wifiEnabled"] as? Boolean ?: currentData.wifiEnabled,
            bluetoothEnabled = updates["bluetoothEnabled"] as? Boolean ?: currentData.bluetoothEnabled,
            locationEnabled = updates["locationEnabled"] as? Boolean ?: currentData.locationEnabled
        )
        _data.value = newData
    }
}
