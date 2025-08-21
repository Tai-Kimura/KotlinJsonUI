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
        _data.value = currentData.copy(
            notificationStatus = if (enabled) "Notifications are enabled" else "Notifications are disabled"
        )
    }
    
    fun handleDarkModeChange(enabled: Boolean) {
        println("Dark mode: $enabled")
        val currentData = _data.value
        _data.value = currentData.copy(
            darkModeStatus = if (enabled) "Dark mode is on" else "Dark mode is off"
        )
    }
    
    fun handleWifiChange(enabled: Boolean) {
        println("WiFi: $enabled")
        updateConnectionStatus()
    }
    
    fun handleBluetoothChange(enabled: Boolean) {
        println("Bluetooth: $enabled")
        updateConnectionStatus()
    }
    
    fun handleLocationChange(enabled: Boolean) {
        println("Location: $enabled")
        updateConnectionStatus()
    }
    
    private fun updateConnectionStatus() {
        // For now, just keep the default status
        // In a real app, you'd track the actual states
        val currentData = _data.value
        _data.value = currentData.copy(
            connectionStatus = "Connection status updated"
        )
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentData = _data.value
        val newData = currentData.copy(
            notificationStatus = updates["notificationStatus"] as? String ?: currentData.notificationStatus,
            darkModeStatus = updates["darkModeStatus"] as? String ?: currentData.darkModeStatus,
            connectionStatus = updates["connectionStatus"] as? String ?: currentData.connectionStatus
        )
        _data.value = newData
    }
}
