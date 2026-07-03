// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/form_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


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
    var zipCode: String = "",
    var toggleDynamicMode: (() -> Unit)? = null,
    var submitForm: (() -> Unit)? = null,
    var clearForm: (() -> Unit)? = null
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): FormTestData {
            return FormTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "OFF",
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
                title = map["title"] as? String ?: "Form & Keyboard Test",
                zipCode = map["zipCode"] as? String ?: "",
                toggleDynamicMode = map["toggleDynamicMode"] as? (() -> Unit)?,
                submitForm = map["submitForm"] as? (() -> Unit)?,
                clearForm = map["clearForm"] as? (() -> Unit)?
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
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
        toggleDynamicMode?.let { map["toggleDynamicMode"] = it }
        submitForm?.let { map["submitForm"] = it }
        clearForm?.let { map["clearForm"] = it }
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
