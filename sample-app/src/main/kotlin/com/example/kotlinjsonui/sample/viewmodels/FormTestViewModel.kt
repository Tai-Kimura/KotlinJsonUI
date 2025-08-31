package com.example.kotlinjsonui.sample.viewmodels
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.FormTestData
import com.kotlinjsonui.core.DynamicModeManager
class FormTestViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "form_test"
    
    // Data model
    private val _data = MutableStateFlow(FormTestData())
    val data: StateFlow<FormTestData> = _data.asStateFlow()

    // Dynamic mode toggle
    fun toggleDynamicMode() {
        // Toggle the actual DynamicModeManager
        val newState = DynamicModeManager.toggleDynamicMode(getApplication())
        
        // Update the UI status based on actual state
        val statusText = if (newState == true) "ON" else "OFF"
        _data.value = _data.value.copy(dynamicModeStatus = "Dynamic Mode: ${statusText}")
    }
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
