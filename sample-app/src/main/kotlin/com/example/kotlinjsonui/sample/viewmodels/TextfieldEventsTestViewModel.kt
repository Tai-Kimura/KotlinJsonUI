package com.example.kotlinjsonui.sample.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.TextfieldEventsTestData

class TextfieldEventsTestViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "textfield_events_test"

    // Data model
    private val _data = MutableStateFlow(TextfieldEventsTestData())
    // The layout binds each display line as one value (binding-mixed-text,
    // jsonui-cli 1.9.6: a layout holds no logic); the line is composed here,
    // from the state, every time it changes.
    val data: StateFlow<TextfieldEventsTestData> = _data.map(::withDisplayTexts)
        .stateIn(viewModelScope, SharingStarted.Eagerly, withDisplayTexts(_data.value))

    private fun withDisplayTexts(d: TextfieldEventsTestData): TextfieldEventsTestData = d.copy(
        emailDisplayText = "Email: ${d.email.ifEmpty { "(not entered)" }}",
        passwordLengthText = "Password length: ${d.password.length}"
    )

    // TextField event handlers
    fun handleEmailChange(value: String) {
        println("Email changed: $value")
        val currentData = _data.value
        _data.value = currentData.copy(
            email = value
        )
    }

    fun handlePasswordChange(value: String) {
        println("Password changed: $value")
        val currentData = _data.value
        _data.value = currentData.copy(
            password = value
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
            password = updates["password"] as? String ?: currentData.password,
            notes = updates["notes"] as? String ?: currentData.notes
        )
        _data.value = newData
    }
    
    init {
        // Wire JSON-declared event handlers: current kjui codegen invokes
        // handlers through the data model (data.<name>?.invoke(...)).
        _data.value = _data.value.copy(
            handleEmailChange = { _, value -> handleEmailChange(value) },
            handlePasswordChange = { _, value -> handlePasswordChange(value) }
        )
    }
}
