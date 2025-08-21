package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.TextfieldTestViewModel

data class TextfieldTestData(
    var email: String = "",
    var password: String = "",
    var phone: String = "",
    var number: String = "",
    var search: String = "",
    var url: String = ""
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
                url = map["url"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: TextfieldTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["email"] = email
        map["password"] = password
        map["phone"] = phone
        map["number"] = number
        map["search"] = search
        map["url"] = url
        
        return map
    }
}
