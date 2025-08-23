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
            notificationEnabled = enabled,
            notificationStatus = if (enabled) "Notifications are enabled" else "Notifications are disabled"
        )
    }
    
    fun handleDarkModeChange(enabled: Boolean) {
        println("Dark mode: $enabled")
        val currentData = _data.value
        _data.value = currentData.copy(
            darkModeEnabled = enabled,
            darkModeStatus = if (enabled) "Dark mode is on" else "Dark mode is off"
        )
    }
    
    fun handleWifiChange(enabled: Boolean) {
        println("WiFi: $enabled")
        val currentData = _data.value
        _data.value = currentData.copy(
            wifiEnabled = enabled
        )
        updateConnectionStatus()
    }
    
    fun handleBluetoothChange(enabled: Boolean) {
        println("Bluetooth: $enabled")
        val currentData = _data.value
        _data.value = currentData.copy(
            bluetoothEnabled = enabled
        )
        updateConnectionStatus()
    }
    
    fun handleLocationChange(enabled: Boolean) {
        println("Location: $enabled")
        val currentData = _data.value
        _data.value = currentData.copy(
            locationEnabled = enabled
        )
        updateConnectionStatus()
    }
    
    private fun updateConnectionStatus() {
        val currentData = _data.value
        val activeConnections = mutableListOf<String>()
        
        if (currentData.wifiEnabled) activeConnections.add("Wi-Fi")
        if (currentData.bluetoothEnabled) activeConnections.add("Bluetooth")
        if (currentData.locationEnabled) activeConnections.add("Location")
        
        val status = if (activeConnections.isNotEmpty()) {
            "Active: ${activeConnections.joinToString(", ")}"
        } else {
            "No active connections"
        }
        
        _data.value = currentData.copy(
            connectionStatus = status
        )
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentData = _data.value
        val newData = currentData.copy(
            notificationEnabled = updates["notificationEnabled"] as? Boolean ?: currentData.notificationEnabled,
            darkModeEnabled = updates["darkModeEnabled"] as? Boolean ?: currentData.darkModeEnabled,
            wifiEnabled = updates["wifiEnabled"] as? Boolean ?: currentData.wifiEnabled,
            bluetoothEnabled = updates["bluetoothEnabled"] as? Boolean ?: currentData.bluetoothEnabled,
            locationEnabled = updates["locationEnabled"] as? Boolean ?: currentData.locationEnabled,
            notificationStatus = updates["notificationStatus"] as? String ?: currentData.notificationStatus,
            darkModeStatus = updates["darkModeStatus"] as? String ?: currentData.darkModeStatus,
            connectionStatus = updates["connectionStatus"] as? String ?: currentData.connectionStatus
        )
        _data.value = newData
    }
}
