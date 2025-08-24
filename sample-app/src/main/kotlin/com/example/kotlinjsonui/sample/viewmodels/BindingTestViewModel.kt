package com.example.kotlinjsonui.sample.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.BindingTestData
import com.kotlinjsonui.core.DynamicModeManager
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BindingTestViewModel : ViewModel() {
    // JSON file reference for hot reload
    val jsonFileName = "binding_test"
    
    // Data model
    private val _data = MutableStateFlow(BindingTestData())
    val data: StateFlow<BindingTestData> = _data.asStateFlow()

    // Dynamic mode toggle
    fun toggleDynamicMode() {
        // Toggle the actual DynamicModeManager
        val newState = DynamicModeManager.toggleDynamicMode()
        
        // Update the UI status based on actual state
        val statusText = if (newState == true) "ON" else "OFF"
        _data.value = _data.value.copy(dynamicModeStatus = statusText)
    }
    
    // Action handlers
    fun decreaseCounter() {
        _data.value = _data.value.copy(counter = _data.value.counter - 1)
    }
    
    fun increaseCounter() {
        _data.value = _data.value.copy(counter = _data.value.counter + 1)
    }
    
    fun toggleChanged() {
        _data.value = _data.value.copy(toggleValue = !_data.value.toggleValue)
    }
    
    fun sliderChanged(value: Float) {
        _data.value = _data.value.copy(sliderValue = value.toDouble())
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentDataMap = _data.value.toMap(this).toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = BindingTestData.fromMap(currentDataMap)
    }
}
