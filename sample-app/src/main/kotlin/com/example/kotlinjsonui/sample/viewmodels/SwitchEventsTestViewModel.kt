package com.example.kotlinjsonui.sample.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.SwitchEventsTestData

class SwitchEventsTestViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "switch_events_test"

    // Data model
    private val _data = MutableStateFlow(SwitchEventsTestData())
    // The layout binds each display line as one value (binding-mixed-text,
    // jsonui-cli 1.9.6: a layout holds no logic); the line is composed here,
    // from the state, every time it changes.
    val data: StateFlow<SwitchEventsTestData> = _data.map(::withDisplayTexts)
        .stateIn(viewModelScope, SharingStarted.Eagerly, withDisplayTexts(_data.value))

    private fun withDisplayTexts(d: SwitchEventsTestData): SwitchEventsTestData = d.copy(
        connectionStatusText = "Status: ${connectionStatus(d)}"
    )

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
    }

    fun handleBluetoothChange(enabled: Boolean) {
        println("Bluetooth: $enabled")
        val currentData = _data.value
        _data.value = currentData.copy(
            bluetoothEnabled = enabled
        )
    }

    fun handleLocationChange(enabled: Boolean) {
        println("Location: $enabled")
        val currentData = _data.value
        _data.value = currentData.copy(
            locationEnabled = enabled
        )
    }

    private fun connectionStatus(d: SwitchEventsTestData): String {
        val active = listOfNotNull(
            "Wi-Fi".takeIf { d.wifiEnabled },
            "Bluetooth".takeIf { d.bluetoothEnabled },
            "Location".takeIf { d.locationEnabled }
        )
        return if (active.isNotEmpty()) "Active: ${active.joinToString(", ")}" else "No active connections"
    }

    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentData = _data.value
        val newData = currentData.copy(
            notificationEnabled = updates["notificationEnabled"] as? Boolean
                ?: currentData.notificationEnabled,
            darkModeEnabled = updates["darkModeEnabled"] as? Boolean
                ?: currentData.darkModeEnabled,
            wifiEnabled = updates["wifiEnabled"] as? Boolean ?: currentData.wifiEnabled,
            bluetoothEnabled = updates["bluetoothEnabled"] as? Boolean
                ?: currentData.bluetoothEnabled,
            locationEnabled = updates["locationEnabled"] as? Boolean
                ?: currentData.locationEnabled,
            notificationStatus = updates["notificationStatus"] as? String
                ?: currentData.notificationStatus,
            darkModeStatus = updates["darkModeStatus"] as? String
                ?: currentData.darkModeStatus
        )
        _data.value = newData
    }
    
    init {
        // Wire JSON-declared event handlers: current kjui codegen invokes
        // handlers through the data model (data.<name>?.invoke(...)).
        _data.value = _data.value.copy(
            handleNotificationChange = { _, value -> handleNotificationChange(value) },
            handleDarkModeChange = { _, value -> handleDarkModeChange(value) },
            handleWifiChange = { _, value -> handleWifiChange(value) },
            handleBluetoothChange = { _, value -> handleBluetoothChange(value) },
            handleLocationChange = { _, value -> handleLocationChange(value) }
        )
    }
}
