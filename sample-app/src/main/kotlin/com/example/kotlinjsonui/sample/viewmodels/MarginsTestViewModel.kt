package com.example.kotlinjsonui.sample.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.MarginsTestData
import com.kotlinjsonui.core.DynamicModeManager
class MarginsTestViewModel : ViewModel() {
    // JSON file reference for hot reload
    val jsonFileName = "margins_test"
    
    // Data model
    private val _data = MutableStateFlow(MarginsTestData())
    val data: StateFlow<MarginsTestData> = _data.asStateFlow()

    // Dynamic mode toggle
    fun toggleDynamicMode() {
        // Toggle the actual DynamicModeManager
        val newState = DynamicModeManager.toggleDynamicMode()
        
        // Update the UI status based on actual state
        val statusText = if (newState == true) "ON" else "OFF"
        _data.value = _data.value.copy(dynamicModeStatus = statusText)
    }
    
    // Action handlers
    fun onGetStarted() {
        // Handle button tap
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentDataMap = _data.value.toMap(this).toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = MarginsTestData.fromMap(currentDataMap)
    }
}
