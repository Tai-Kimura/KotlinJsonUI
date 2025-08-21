package com.example.kotlinjsonui.sample.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.ButtonEnabledTestData

class ButtonEnabledTestViewModel : ViewModel() {
    // JSON file reference for hot reload
    val jsonFileName = "button_enabled_test"
    
    // Data model
    private val _data = MutableStateFlow(ButtonEnabledTestData())
    val data: StateFlow<ButtonEnabledTestData> = _data.asStateFlow()

    // Dynamic mode toggle
    fun toggleDynamicMode() {
        val currentStatus = _data.value.dynamicModeStatus
        val newStatus = if (currentStatus == "ON") "OFF" else "ON"
        _data.value = _data.value.copy(dynamicModeStatus = newStatus)
    }
    
    // Action handlers
    fun testAction() {
        // Test button action
        println("Test action called")
    }
    
    fun toggleEnabled() {
        _data.value = _data.value.copy(isButtonEnabled = !_data.value.isButtonEnabled)
    }
    
    fun neverCalled() {
        // This should never be called when button is disabled
        println("This shouldn't be called when disabled")
    }
    
    fun alwaysCalled() {
        // This should always be called
        println("Always called")
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentDataMap = _data.value.toMap(this).toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = ButtonEnabledTestData.fromMap(currentDataMap)
    }
}
