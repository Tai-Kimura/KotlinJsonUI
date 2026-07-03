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
    var emailDisplay: String = "(not entered)",
    var password: String = "",
    var passwordLength: String = "0",
    var notes: String = "",
    var handleEmailChange: ((String, String) -> Unit)? = null,
    var handlePasswordChange: ((String, String) -> Unit)? = null
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): TextfieldEventsTestData {
            return TextfieldEventsTestData(
                email = map["email"] as? String ?: "",
                emailDisplay = map["emailDisplay"] as? String ?: "(not entered)",
                password = map["password"] as? String ?: "",
                passwordLength = map["passwordLength"] as? String ?: "0",
                notes = map["notes"] as? String ?: "",
                handleEmailChange = map["handleEmailChange"] as? ((String, String) -> Unit)?,
                handlePasswordChange = map["handlePasswordChange"] as? ((String, String) -> Unit)?
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["email"] = email
        map["emailDisplay"] = emailDisplay
        map["password"] = password
        map["passwordLength"] = passwordLength
        map["notes"] = notes
        handleEmailChange?.let { map["handleEmailChange"] = it }
        handlePasswordChange?.let { map["handlePasswordChange"] = it }
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
