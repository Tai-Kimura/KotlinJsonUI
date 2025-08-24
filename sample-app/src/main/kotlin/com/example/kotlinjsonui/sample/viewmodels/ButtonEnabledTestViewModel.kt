package com.example.kotlinjsonui.sample.viewmodels
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.ButtonEnabledTestData
import com.kotlinjsonui.core.DynamicModeManager
class ButtonEnabledTestViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "button_enabled_test"
    
    // Data model
    private val _data = MutableStateFlow(ButtonEnabledTestData())
    val data: StateFlow<ButtonEnabledTestData> = _data.asStateFlow()

    // Dynamic mode toggle
    fun toggleDynamicMode() {
        // Toggle the actual DynamicModeManager
        val newState = DynamicModeManager.toggleDynamicMode(getApplication())
        
        // Update the UI status based on actual state
        val statusText = if (newState == true) "ON" else "OFF"
        _data.value = _data.value.copy(dynamicModeStatus = statusText)
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
