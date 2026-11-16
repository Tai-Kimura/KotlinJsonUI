// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/textfield_events_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class TextfieldEventsTestData(
    var email: String = "",
    var password: String = "",
    var notes: String = "",
    var handleEmailChange: ((String, String) -> Unit)? = null,
    var handlePasswordChange: ((String, String) -> Unit)? = null,
    var emailDisplayText: String = "",
    var passwordLengthText: String = "",
    var emailFieldIsFocused: Boolean = false,
    var passwordFieldIsFocused: Boolean = false,
    var notesFieldIsFocused: Boolean = false
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): TextfieldEventsTestData {
            return TextfieldEventsTestData(
                email = map["email"] as? String ?: "",
                password = map["password"] as? String ?: "",
                notes = map["notes"] as? String ?: "",
                handleEmailChange = map["handleEmailChange"] as? ((String, String) -> Unit)?,
                handlePasswordChange = map["handlePasswordChange"] as? ((String, String) -> Unit)?,
                emailDisplayText = map["emailDisplayText"] as? String ?: "",
                passwordLengthText = map["passwordLengthText"] as? String ?: "",
                emailFieldIsFocused = map["emailFieldIsFocused"] as? Boolean ?: false,
                passwordFieldIsFocused = map["passwordFieldIsFocused"] as? Boolean ?: false,
                notesFieldIsFocused = map["notesFieldIsFocused"] as? Boolean ?: false
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["email"] = email
        map["password"] = password
        map["notes"] = notes
        handleEmailChange?.let { map["handleEmailChange"] = it }
        handlePasswordChange?.let { map["handlePasswordChange"] = it }
        map["emailDisplayText"] = emailDisplayText
        map["passwordLengthText"] = passwordLengthText
        map["emailFieldIsFocused"] = emailFieldIsFocused
        map["passwordFieldIsFocused"] = passwordFieldIsFocused
        map["notesFieldIsFocused"] = notesFieldIsFocused
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
