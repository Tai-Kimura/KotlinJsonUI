package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.FormTestViewModel

data class FormTestData(
    var dynamicModeStatus: String = "OFF",
    var address: String = "",
    var agreeToTerms: Boolean = false,
    var bio: String = "",
    var city: String = "",
    var comments: String = "",
    var company: String = "",
    var country: String = "",
    var email: String = "",
    var firstName: String = "",
    var jobTitle: String = "",
    var lastName: String = "",
    var notes: String = "",
    var phone: String = "",
    var title: String = "Form & Keyboard Test",
    var zipCode: String = ""
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): FormTestData {
            return FormTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "",
                address = map["address"] as? String ?: "",
                agreeToTerms = map["agreeToTerms"] as? Boolean ?: false,
                bio = map["bio"] as? String ?: "",
                city = map["city"] as? String ?: "",
                comments = map["comments"] as? String ?: "",
                company = map["company"] as? String ?: "",
                country = map["country"] as? String ?: "",
                email = map["email"] as? String ?: "",
                firstName = map["firstName"] as? String ?: "",
                jobTitle = map["jobTitle"] as? String ?: "",
                lastName = map["lastName"] as? String ?: "",
                notes = map["notes"] as? String ?: "",
                phone = map["phone"] as? String ?: "",
                title = map["title"] as? String ?: "",
                zipCode = map["zipCode"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: FormTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["address"] = address
        map["agreeToTerms"] = agreeToTerms
        map["bio"] = bio
        map["city"] = city
        map["comments"] = comments
        map["company"] = company
        map["country"] = country
        map["email"] = email
        map["firstName"] = firstName
        map["jobTitle"] = jobTitle
        map["lastName"] = lastName
        map["notes"] = notes
        map["phone"] = phone
        map["title"] = title
        map["zipCode"] = zipCode
        
        // Add onclick action lambdas if viewModel is provided
        viewModel?.let { vm ->
            map["toggleDynamicMode"] = { vm.toggleDynamicMode() }
            map["submitForm"] = { vm.submitForm() }
            map["clearForm"] = { vm.clearForm() }
        }
        
        return map
    }
}
