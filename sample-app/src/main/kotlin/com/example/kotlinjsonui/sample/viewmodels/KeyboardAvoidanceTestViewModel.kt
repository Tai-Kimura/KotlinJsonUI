package com.example.kotlinjsonui.sample.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.KeyboardAvoidanceTestData

class KeyboardAvoidanceTestViewModel : ViewModel() {
    // JSON file reference for hot reload
    val jsonFileName = "keyboard_avoidance_test"
    
    // Data model
    private val _data = MutableStateFlow(KeyboardAvoidanceTestData())
    val data: StateFlow<KeyboardAvoidanceTestData> = _data.asStateFlow()

    // Dynamic mode toggle
    fun toggleDynamicMode() {
        val currentStatus = _data.value.dynamicModeStatus
        val newStatus = if (currentStatus == "ON") "OFF" else "ON"
        _data.value = _data.value.copy(dynamicModeStatus = newStatus)
    }
    
    // Action handlers
    fun submitForm() {
        // Submit form with keyboard avoidance test
        val data = _data.value
        println("Keyboard Avoidance Test Submitted:")
        println("  Field 1: ${data.textField1}")
        println("  Field 2: ${data.textField2}")
        println("  Field 3: ${data.textField3}")
        println("  Field 4: ${data.textField4}")
        println("  Field 5: ${data.textField5}")
        println("  Text View: ${data.textView}")
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentDataMap = _data.value.toMap(this).toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = KeyboardAvoidanceTestData.fromMap(currentDataMap)
    }
}
