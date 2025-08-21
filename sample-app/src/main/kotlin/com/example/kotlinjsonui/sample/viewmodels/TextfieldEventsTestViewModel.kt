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
    fun handleUsernameChange(value: String) {
        println("Username changed: $value")
        updateData(mapOf("username" to value))
    }
    
    fun handleEmailChange(value: String) {
        println("Email changed: $value")
        updateData(mapOf("email" to value))
    }
    
    fun handlePasswordChange(value: String) {
        println("Password changed: $value")
        updateData(mapOf("password" to value))
    }
    
    fun handleNotesChange(value: String) {
        println("Notes changed: $value")
        updateData(mapOf("notes" to value))
    }
    
    // Action handlers
    fun onGetStarted() {
        // Handle button tap
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentData = _data.value
        val newData = currentData.copy(
            username = updates["username"] as? String ?: currentData.username,
            email = updates["email"] as? String ?: currentData.email,
            password = updates["password"] as? String ?: currentData.password,
            notes = updates["notes"] as? String ?: currentData.notes
        )
        _data.value = newData
    }
}
