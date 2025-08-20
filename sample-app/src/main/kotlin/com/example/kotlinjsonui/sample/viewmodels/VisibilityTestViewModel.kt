package com.example.kotlinjsonui.sample.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.VisibilityTestData

class VisibilityTestViewModel : ViewModel() {
    // JSON file reference for hot reload
    val jsonFileName = "visibility_test"
    
    // Data model
    private val _data = MutableStateFlow(VisibilityTestData())
    val data: StateFlow<VisibilityTestData> = _data.asStateFlow()
    
    // Action handlers
    fun onGetStarted() {
        // Handle button tap
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentDataMap = _data.value.toMap(this).toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = VisibilityTestData.fromMap(currentDataMap)
    }
}
