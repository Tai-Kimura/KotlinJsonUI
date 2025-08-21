package com.example.kotlinjsonui.sample.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.BindingTestData

class BindingTestViewModel : ViewModel() {
    // JSON file reference for hot reload
    val jsonFileName = "binding_test"
    
    // Data model
    private val _data = MutableStateFlow(BindingTestData())
    val data: StateFlow<BindingTestData> = _data.asStateFlow()

    // Dynamic mode toggle
    fun toggleDynamicMode() {
        val currentStatus = _data.value.dynamicModeStatus
        val newStatus = if (currentStatus == "ON") "OFF" else "ON"
        _data.value = _data.value.copy(dynamicModeStatus = newStatus)
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
