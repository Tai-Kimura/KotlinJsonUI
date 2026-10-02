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
import com.example.kotlinjsonui.sample.data.DisabledTestData
import com.kotlinjsonui.core.DynamicModeManager
class DisabledTestViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "disabled_test"
    
    // Data model
    private val _data = MutableStateFlow(DisabledTestData())
    // The layout binds each display line as one value (binding-mixed-text,
    // jsonui-cli 1.9.6: a layout holds no logic); the line is composed here,
    // from the state, every time it changes.
    val data: StateFlow<DisabledTestData> = _data.map(::withDisplayTexts)
        .stateIn(viewModelScope, SharingStarted.Eagerly, withDisplayTexts(_data.value))

    private fun withDisplayTexts(d: DisabledTestData): DisabledTestData = d.copy(
        enabledStateText = "Current state: ${d.isEnabled}"
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
    fun onEnabledButtonTap() {
        println("Enabled button tapped")
    }

    fun onDisabledButtonTap() {
        // This should never be called
        println("ERROR: Disabled button was tapped!")
    }

    fun onTouchDisabledTap() {
        println("ERROR: Touch disabled button was tapped!")
    }

    fun toggleEnableState() {
        _data.value = _data.value.copy(isEnabled = !_data.value.isEnabled)
    }

    fun onDynamicButtonTap() {
        if (_data.value.isEnabled) {
            println("Dynamic button tapped - was enabled")
        } else {
            println("ERROR: Dynamic button tapped when disabled!")
        }
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentDataMap = _data.value.toMap().toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = DisabledTestData.fromMap(currentDataMap)
    }
    
    init {
        // Wire JSON-declared event handlers: current kjui codegen invokes
        // handlers through the data model (data.<name>?.invoke(...)).
        _data.value = _data.value.copy(
            toggleDynamicMode = { toggleDynamicMode() },
            onEnabledButtonTap = { onEnabledButtonTap() },
            onDisabledButtonTap = { onDisabledButtonTap() },
            onTouchDisabledTap = { onTouchDisabledTap() },
            toggleEnableState = { toggleEnableState() },
            onDynamicButtonTap = { onDynamicButtonTap() }
        )
    }
}
