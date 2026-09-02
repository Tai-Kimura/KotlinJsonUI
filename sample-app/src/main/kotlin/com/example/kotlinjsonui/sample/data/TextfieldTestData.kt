// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/textfield_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class TextfieldTestData(
    var email: String = "",
    var password: String = "",
    var phone: String = "",
    var number: String = "",
    var search: String = "",
    var url: String = "",
    var emailFieldIsFocused: Boolean = false,
    var passwordFieldIsFocused: Boolean = false,
    var phoneFieldIsFocused: Boolean = false,
    var numberFieldIsFocused: Boolean = false,
    var searchFieldIsFocused: Boolean = false,
    var urlFieldIsFocused: Boolean = false
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): TextfieldTestData {
            return TextfieldTestData(
                email = map["email"] as? String ?: "",
                password = map["password"] as? String ?: "",
                phone = map["phone"] as? String ?: "",
                number = map["number"] as? String ?: "",
                search = map["search"] as? String ?: "",
                url = map["url"] as? String ?: "",
                emailFieldIsFocused = map["emailFieldIsFocused"] as? Boolean ?: false,
                passwordFieldIsFocused = map["passwordFieldIsFocused"] as? Boolean ?: false,
                phoneFieldIsFocused = map["phoneFieldIsFocused"] as? Boolean ?: false,
                numberFieldIsFocused = map["numberFieldIsFocused"] as? Boolean ?: false,
                searchFieldIsFocused = map["searchFieldIsFocused"] as? Boolean ?: false,
                urlFieldIsFocused = map["urlFieldIsFocused"] as? Boolean ?: false
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["email"] = email
        map["password"] = password
        map["phone"] = phone
        map["number"] = number
        map["search"] = search
        map["url"] = url
        map["emailFieldIsFocused"] = emailFieldIsFocused
        map["passwordFieldIsFocused"] = passwordFieldIsFocused
        map["phoneFieldIsFocused"] = phoneFieldIsFocused
        map["numberFieldIsFocused"] = numberFieldIsFocused
        map["searchFieldIsFocused"] = searchFieldIsFocused
        map["urlFieldIsFocused"] = urlFieldIsFocused
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
