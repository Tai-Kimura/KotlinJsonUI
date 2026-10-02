package com.example.kotlinjsonui.sample.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.BindingTestData
import com.kotlinjsonui.core.DynamicModeManager
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
class BindingTestViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "binding_test"
    
    // Data model
    private val _data = MutableStateFlow(BindingTestData())
    // The layout binds each display line as one value (binding-mixed-text,
    // jsonui-cli 1.9.6: a layout holds no logic); the line is composed here,
    // from the state, every time it changes.
    val data: StateFlow<BindingTestData> = _data.map(::withDisplayTexts)
        .stateIn(viewModelScope, SharingStarted.Eagerly, withDisplayTexts(_data.value))

    private fun withDisplayTexts(d: BindingTestData): BindingTestData = d.copy(
        typedText = "You typed: ${d.textValue}",
        toggleText = "Toggle is: ${d.toggleValue}",
        sliderText = "Slider value: ${d.sliderValue}",
        selectedOptionText = "Selected: ${d.selectedOption}"
    )
    // Dynamic mode toggle
    fun toggleDynamicMode() {
        // Toggle the actual DynamicModeManager
        val newState = DynamicModeManager.toggleDynamicMode(getApplication())
        
        // Update the UI status based on actual state
        val statusText = if (newState == true) "ON" else "OFF"
        _data.value = _data.value.copy(dynamicModeStatus = "Dynamic Mode: ${statusText}")
    }
    
    // Action handlers
    fun decreaseCounter() {
        _data.value = _data.value.copy(counter = _data.value.counter - 1)
    }
    
    fun increaseCounter() {
        _data.value = _data.value.copy(counter = _data.value.counter + 1)
    }
    
    // The Switch writes toggleValue through its two-way binding before it
    // calls this (`"onclick": "toggleChanged"`), so this only reacts: flipping
    // the value again here would undo the user's tap.
    fun toggleChanged() {
        println("Toggle is now ${_data.value.toggleValue}")
    }
    
    fun sliderChanged(value: Float) {
        _data.value = _data.value.copy(sliderValue = value.toDouble())
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentDataMap = _data.value.toMap().toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = BindingTestData.fromMap(currentDataMap)
    }
    
    init {
        // Wire JSON-declared event handlers: current kjui codegen invokes
        // handlers through the data model (data.<name>?.invoke(...)).
        _data.value = _data.value.copy(
            toggleDynamicMode = { toggleDynamicMode() },
            decreaseCounter = { decreaseCounter() },
            increaseCounter = { increaseCounter() },
            sliderChanged = { _, value -> sliderChanged(value) },
            toggleChanged = { toggleChanged() }
        )
    }
}
