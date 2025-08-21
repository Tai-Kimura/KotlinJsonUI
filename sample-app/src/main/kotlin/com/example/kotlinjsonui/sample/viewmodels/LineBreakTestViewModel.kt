package com.example.kotlinjsonui.sample.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.LineBreakTestData

class LineBreakTestViewModel : ViewModel() {
    // JSON file reference for hot reload
    val jsonFileName = "line_break_test"
    
    // Data model
    private val _data = MutableStateFlow(LineBreakTestData())
    val data: StateFlow<LineBreakTestData> = _data.asStateFlow()

    // Dynamic mode toggle
    fun toggleDynamicMode() {
        val currentStatus = _data.value.dynamicModeStatus
        val newStatus = if (currentStatus == "ON") "OFF" else "ON"
        _data.value = _data.value.copy(dynamicModeStatus = newStatus)
    }
    
    // Action handlers
    fun onGetStarted() {
        // Handle button tap
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentDataMap = _data.value.toMap(this).toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = LineBreakTestData.fromMap(currentDataMap)
    }
}
