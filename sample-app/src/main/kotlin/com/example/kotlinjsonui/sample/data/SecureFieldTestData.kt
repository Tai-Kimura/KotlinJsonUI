// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/secure_field_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data

import com.kotlinjsonui.core.KotlinJsonUI
import com.example.kotlinjsonui.sample.R

data class SecureFieldTestData(
    var dynamicModeStatus: String = "OFF",
    var confirmPassword: String = "",
    var password: String = "",
    var regularText: String = "",
    var title: String = KotlinJsonUI.localizedString(R.string.test_menu_secure_field_test_2, "Secure Field Test"),
    var toggleDynamicMode: (() -> Unit)? = null,
    var regularFieldIsFocused: Boolean = false,
    var passwordFieldIsFocused: Boolean = false,
    var confirmPasswordFieldIsFocused: Boolean = false
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): SecureFieldTestData {
            return SecureFieldTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "OFF",
                confirmPassword = map["confirmPassword"] as? String ?: "",
                password = map["password"] as? String ?: "",
                regularText = map["regularText"] as? String ?: "",
                title = map["title"] as? String ?: KotlinJsonUI.localizedString(R.string.test_menu_secure_field_test_2, "Secure Field Test"),
                toggleDynamicMode = map["toggleDynamicMode"] as? (() -> Unit)?,
                regularFieldIsFocused = map["regularFieldIsFocused"] as? Boolean ?: false,
                passwordFieldIsFocused = map["passwordFieldIsFocused"] as? Boolean ?: false,
                confirmPasswordFieldIsFocused = map["confirmPasswordFieldIsFocused"] as? Boolean ?: false
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["confirmPassword"] = confirmPassword
        map["password"] = password
        map["regularText"] = regularText
        map["title"] = title
        toggleDynamicMode?.let { map["toggleDynamicMode"] = it }
        map["regularFieldIsFocused"] = regularFieldIsFocused
        map["passwordFieldIsFocused"] = passwordFieldIsFocused
        map["confirmPasswordFieldIsFocused"] = confirmPasswordFieldIsFocused
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
