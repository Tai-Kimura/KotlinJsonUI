package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class SecureFieldTestData(
    var dynamicModeStatus: String = "OFF",
    var confirmPassword: String = "",
    var password: String = "",
    var regularText: String = "",
    var title: String = "Secure Field Test"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): SecureFieldTestData {
            return SecureFieldTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "",
                confirmPassword = map["confirmPassword"] as? String ?: "",
                password = map["password"] as? String ?: "",
                regularText = map["regularText"] as? String ?: "",
                title = map["title"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: SecureFieldTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["confirmPassword"] = confirmPassword
        map["password"] = password
        map["regularText"] = regularText
        map["title"] = title
        
        return map
    }
}
