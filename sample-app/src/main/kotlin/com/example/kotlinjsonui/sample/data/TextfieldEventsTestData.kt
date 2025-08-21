package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.TextfieldEventsTestViewModel

data class TextfieldEventsTestData(
    var email: String = "",
    var emailDisplay: String = "(not entered)",
    var password: String = "",
    var passwordLength: String = "0",
    var notes: String = ""
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): TextfieldEventsTestData {
            return TextfieldEventsTestData(
                email = map["email"] as? String ?: "",
                emailDisplay = map["emailDisplay"] as? String ?: "",
                password = map["password"] as? String ?: "",
                passwordLength = map["passwordLength"] as? String ?: "",
                notes = map["notes"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: TextfieldEventsTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["email"] = email
        map["emailDisplay"] = emailDisplay
        map["password"] = password
        map["passwordLength"] = passwordLength
        map["notes"] = notes
        
        return map
    }
}
