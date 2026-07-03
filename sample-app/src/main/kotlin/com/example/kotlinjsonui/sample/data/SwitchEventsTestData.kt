// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/switch_events_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class SwitchEventsTestData(
    var notificationEnabled: Boolean = true,
    var darkModeEnabled: Boolean = false,
    var wifiEnabled: Boolean = true,
    var bluetoothEnabled: Boolean = false,
    var locationEnabled: Boolean = true,
    var notificationStatus: String = "Notifications are enabled",
    var darkModeStatus: String = "Dark mode is off",
    var connectionStatus: String = "Active: Wi-Fi, Location",
    var handleNotificationChange: ((String, Boolean) -> Unit)? = null,
    var handleDarkModeChange: ((String, Boolean) -> Unit)? = null,
    var handleWifiChange: ((String, Boolean) -> Unit)? = null,
    var handleBluetoothChange: ((String, Boolean) -> Unit)? = null,
    var handleLocationChange: ((String, Boolean) -> Unit)? = null
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): SwitchEventsTestData {
            return SwitchEventsTestData(
                notificationEnabled = map["notificationEnabled"] as? Boolean ?: true,
                darkModeEnabled = map["darkModeEnabled"] as? Boolean ?: false,
                wifiEnabled = map["wifiEnabled"] as? Boolean ?: true,
                bluetoothEnabled = map["bluetoothEnabled"] as? Boolean ?: false,
                locationEnabled = map["locationEnabled"] as? Boolean ?: true,
                notificationStatus = map["notificationStatus"] as? String ?: "Notifications are enabled",
                darkModeStatus = map["darkModeStatus"] as? String ?: "Dark mode is off",
                connectionStatus = map["connectionStatus"] as? String ?: "Active: Wi-Fi, Location",
                handleNotificationChange = map["handleNotificationChange"] as? ((String, Boolean) -> Unit)?,
                handleDarkModeChange = map["handleDarkModeChange"] as? ((String, Boolean) -> Unit)?,
                handleWifiChange = map["handleWifiChange"] as? ((String, Boolean) -> Unit)?,
                handleBluetoothChange = map["handleBluetoothChange"] as? ((String, Boolean) -> Unit)?,
                handleLocationChange = map["handleLocationChange"] as? ((String, Boolean) -> Unit)?
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["notificationEnabled"] = notificationEnabled
        map["darkModeEnabled"] = darkModeEnabled
        map["wifiEnabled"] = wifiEnabled
        map["bluetoothEnabled"] = bluetoothEnabled
        map["locationEnabled"] = locationEnabled
        map["notificationStatus"] = notificationStatus
        map["darkModeStatus"] = darkModeStatus
        map["connectionStatus"] = connectionStatus
        handleNotificationChange?.let { map["handleNotificationChange"] = it }
        handleDarkModeChange?.let { map["handleDarkModeChange"] = it }
        handleWifiChange?.let { map["handleWifiChange"] = it }
        handleBluetoothChange?.let { map["handleBluetoothChange"] = it }
        handleLocationChange?.let { map["handleLocationChange"] = it }
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
