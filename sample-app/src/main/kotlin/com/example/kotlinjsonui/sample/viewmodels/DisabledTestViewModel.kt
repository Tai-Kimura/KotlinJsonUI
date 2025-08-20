package com.example.kotlinjsonui.sample.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.DisabledTestData

class DisabledTestViewModel : ViewModel() {
    // JSON file reference for hot reload
    val jsonFileName = "disabled_test"
    
    // Data model
    private val _data = MutableStateFlow(DisabledTestData())
    val data: StateFlow<DisabledTestData> = _data.asStateFlow()
    
    // Action handlers
    fun onEnabledButtonTap() {
        println("Enabled button tapped")
    }
    
    fun onDisabledButtonTap() {
        // This should never be called
        println("ERROR: Disabled button was tapped!")
    }
    
    fun onTouchDisabledTap() {
        // This should never be called
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
        val currentDataMap = _data.value.toMap(this).toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = DisabledTestData.fromMap(currentDataMap)
    }
}
