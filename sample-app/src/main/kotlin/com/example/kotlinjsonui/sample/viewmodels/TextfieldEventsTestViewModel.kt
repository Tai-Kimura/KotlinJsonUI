package com.example.kotlinjsonui.sample.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.TextfieldEventsTestData

class TextfieldEventsTestViewModel : ViewModel() {
    // JSON file reference for hot reload
    val jsonFileName = "textfield_events_test"
    
    // Data model
    private val _data = MutableStateFlow(TextfieldEventsTestData())
    val data: StateFlow<TextfieldEventsTestData> = _data.asStateFlow()
    
    // TextField event handlers
    fun handleEmailChange(value: String) {
        println("Email changed: $value")
        val currentData = _data.value
        _data.value = currentData.copy(
            email = value,
            emailDisplay = if (value.isEmpty()) "(not entered)" else value
        )
    }
    
    fun handlePasswordChange(value: String) {
        println("Password changed: $value")
        val currentData = _data.value
        _data.value = currentData.copy(
            password = value,
            passwordLength = value.length.toString()
        )
    }
    
    fun handleNotesChange(value: String) {
        println("Notes changed: $value")
        _data.value = _data.value.copy(notes = value)
    }
    
    // Action handlers
    fun onGetStarted() {
        // Handle button tap
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentData = _data.value
        val newData = currentData.copy(
            email = updates["email"] as? String ?: currentData.email,
            emailDisplay = updates["emailDisplay"] as? String ?: currentData.emailDisplay,
            password = updates["password"] as? String ?: currentData.password,
            passwordLength = updates["passwordLength"] as? String ?: currentData.passwordLength,
            notes = updates["notes"] as? String ?: currentData.notes
        )
        _data.value = newData
    }
}
