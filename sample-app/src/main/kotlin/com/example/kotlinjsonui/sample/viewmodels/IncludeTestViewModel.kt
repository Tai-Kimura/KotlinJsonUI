package com.example.kotlinjsonui.sample.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.IncludeTestData
import com.kotlinjsonui.core.DynamicModeManager
class IncludeTestViewModel : ViewModel() {
    // JSON file reference for hot reload
    val jsonFileName = "include_test"
    
    // Data model
    private val _data = MutableStateFlow(IncludeTestData())
    val data: StateFlow<IncludeTestData> = _data.asStateFlow()

    // Dynamic mode toggle
    fun toggleDynamicMode() {
        // Toggle the actual DynamicModeManager
        val newState = DynamicModeManager.toggleDynamicMode()
        
        // Update the UI status based on actual state
        val statusText = if (newState == true) "ON" else "OFF"
        _data.value = _data.value.copy(dynamicModeStatus = statusText)
    }
    
    // Child ViewModels for included views
    val included1ViewModel = Included1ViewModel()
    val included2ViewModel = Included2ViewModel()
    
    // Action handlers
    fun incrementCount() {
        _data.value = _data.value.copy(mainCount = _data.value.mainCount + 1)
    }
    
    fun decrementCount() {
        _data.value = _data.value.copy(mainCount = _data.value.mainCount - 1)
    }
    
    fun resetCount() {
        _data.value = _data.value.copy(mainCount = 0)
    }
    
    fun changeUserName() {
        val names = listOf("Alice", "Bob", "Charlie", "Diana", "Eve")
        val currentIndex = names.indexOf(_data.value.userName)
        val nextIndex = (currentIndex + 1) % names.size
        _data.value = _data.value.copy(userName = names[nextIndex])
    }
    
    fun toggleStatus() {
        val newStatus = if (_data.value.mainStatus == "Main Active") "Main Inactive" else "Main Active"
        _data.value = _data.value.copy(mainStatus = newStatus)
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentDataMap = _data.value.toMap(this).toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = IncludeTestData.fromMap(currentDataMap)
    }
}
