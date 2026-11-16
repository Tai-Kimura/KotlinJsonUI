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
import com.example.kotlinjsonui.sample.data.ButtonEnabledTestData
import com.kotlinjsonui.core.DynamicModeManager
class ButtonEnabledTestViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "button_enabled_test"
    
    // Data model
    private val _data = MutableStateFlow(ButtonEnabledTestData())
    // The layout binds each display line as one value (binding-mixed-text,
    // jsonui-cli 1.9.6: a layout holds no logic); the line is composed here,
    // from the state, every time it changes.
    val data: StateFlow<ButtonEnabledTestData> = _data.map(::withDisplayTexts)
        .stateIn(viewModelScope, SharingStarted.Eagerly, withDisplayTexts(_data.value))

    private fun withDisplayTexts(d: ButtonEnabledTestData): ButtonEnabledTestData = d.copy(
        buttonEnabledText = "Button enabled state: ${d.isButtonEnabled}"
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
        val currentDataMap = _data.value.toMap().toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = ButtonEnabledTestData.fromMap(currentDataMap)
    }
    
    init {
        // Wire JSON-declared event handlers: current kjui codegen invokes
        // handlers through the data model (data.<name>?.invoke(...)).
        _data.value = _data.value.copy(
            toggleDynamicMode = { toggleDynamicMode() },
            testAction = { testAction() },
            toggleEnabled = { toggleEnabled() },
            neverCalled = { neverCalled() },
            alwaysCalled = { alwaysCalled() }
        )
    }
}
