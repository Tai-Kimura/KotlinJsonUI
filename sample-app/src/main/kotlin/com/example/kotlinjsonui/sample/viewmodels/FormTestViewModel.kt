package com.example.kotlinjsonui.sample.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.FormTestData

class FormTestViewModel : ViewModel() {
    // JSON file reference for hot reload
    val jsonFileName = "form_test"
    
    // Data model
    private val _data = MutableStateFlow(FormTestData())
    val data: StateFlow<FormTestData> = _data.asStateFlow()
    
    // Action handlers
    fun submitForm() {
        // Submit form data
        val data = _data.value
        println("Form Submitted:")
        println("  First Name: ${data.firstName}")
        println("  Last Name: ${data.lastName}")
        println("  Email: ${data.email}")
        println("  Phone: ${data.phone}")
        println("  Company: ${data.company}")
        println("  Job Title: ${data.jobTitle}")
        println("  Address: ${data.address}")
        println("  City: ${data.city}")
        println("  Country: ${data.country}")
        println("  Zip: ${data.zipCode}")
        println("  Bio: ${data.bio}")
        println("  Comments: ${data.comments}")
        println("  Notes: ${data.notes}")
        println("  Terms Agreed: ${data.agreeToTerms}")
    }
    
    fun clearForm() {
        // Clear all form fields
        _data.value = _data.value.copy(
            firstName = "",
            lastName = "",
            email = "",
            phone = "",
            company = "",
            jobTitle = "",
            address = "",
            city = "",
            country = "",
            zipCode = "",
            bio = "",
            comments = "",
            notes = "",
            agreeToTerms = false
        )
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentDataMap = _data.value.toMap(this).toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = FormTestData.fromMap(currentDataMap)
    }
}
