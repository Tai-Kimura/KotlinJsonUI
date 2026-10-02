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
import com.example.kotlinjsonui.sample.data.SecureFieldTestData
import com.kotlinjsonui.core.DynamicModeManager
class SecureFieldTestViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "secure_field_test"
    
    // Data model
    private val _data = MutableStateFlow(SecureFieldTestData())
    // The layout binds each display line as one value (binding-mixed-text,
    // jsonui-cli 1.9.6: a layout holds no logic); the line is composed here,
    // from the state, every time it changes.
    val data: StateFlow<SecureFieldTestData> = _data.map(::withDisplayTexts)
        .stateIn(viewModelScope, SharingStarted.Eagerly, withDisplayTexts(_data.value))

    private fun withDisplayTexts(d: SecureFieldTestData): SecureFieldTestData = d.copy(
        regularDisplayText = "Regular: ${d.regularText}"
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
    fun onGetStarted() {
        // Handle button tap
    }

    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentDataMap = _data.value.toMap().toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = SecureFieldTestData.fromMap(currentDataMap)
    }
    
    init {
        // Wire JSON-declared event handlers: current kjui codegen invokes
        // handlers through the data model (data.<name>?.invoke(...)).
        _data.value = _data.value.copy(
            toggleDynamicMode = { toggleDynamicMode() }
        )
    }
}
