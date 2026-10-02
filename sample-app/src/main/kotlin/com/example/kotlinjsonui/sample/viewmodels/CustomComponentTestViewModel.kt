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
import com.example.kotlinjsonui.sample.data.CustomComponentTestData

class CustomComponentTestViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "custom_component_test"
    
    // Data model
    private val _data = MutableStateFlow(CustomComponentTestData())
    // The layout binds each display line as one value (binding-mixed-text,
    // jsonui-cli 1.9.6: a layout holds no logic); the line is composed here,
    // from the state, every time it changes.
    val data: StateFlow<CustomComponentTestData> = _data.map(::withDisplayTexts)
        .stateIn(viewModelScope, SharingStarted.Eagerly, withDisplayTexts(_data.value))

    private fun withDisplayTexts(d: CustomComponentTestData): CustomComponentTestData = d.copy(
        itemCountText = "Dynamic count: ${d.itemCount}"
    )
    
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
    
    // The mode is this ViewModel's own state; the layout reads the line.
    private var dynamicModeOn = false

    fun toggleDynamicMode() {
        dynamicModeOn = !dynamicModeOn
        _data.value = _data.value.copy(dynamicModeText = "Dynamic Mode: ${if (dynamicModeOn) "ON" else "OFF"}")
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
