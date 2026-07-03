package com.example.kotlinjsonui.sample.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.CustomComponentTestData

class CustomComponentTestViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "custom_component_test"
    
    // Data model
    private val _data = MutableStateFlow(CustomComponentTestData())
    val data: StateFlow<CustomComponentTestData> = _data.asStateFlow()
    
    // Action handlers
    fun onGetStarted() {
        // Handle button tap
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        // Update data with new values from map
        val newData = CustomComponentTestData.fromMap(updates)
        _data.value = newData
    }
    
    fun toggleDynamicMode() {
        _data.value = _data.value.copy(
            dynamicModeStatus = if (_data.value.dynamicModeStatus == "ON") "OFF" else "ON"
        )
    }
    
    fun incrementCount() {
        _data.value = _data.value.copy(itemCount = _data.value.itemCount + 1)
    }
    
    fun decrementCount() {
        _data.value = _data.value.copy(itemCount = _data.value.itemCount - 1)
    }
    
    init {
        // Wire JSON-declared event handlers: current kjui codegen invokes
        // handlers through the data model (data.<name>?.invoke(...)).
        _data.value = _data.value.copy(
            toggleDynamicMode = { toggleDynamicMode() },
            incrementCount = { incrementCount() },
            decrementCount = { decrementCount() }
        )
    }
}
