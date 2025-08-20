package com.example.kotlinjsonui.sample.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.Included2Data

class Included2ViewModel : ViewModel() {
    // JSON file reference for hot reload
    val jsonFileName = "included2"
    
    // Data model
    private val _data = MutableStateFlow(Included2Data())
    val data: StateFlow<Included2Data> = _data.asStateFlow()
    
    // Action handlers
    fun onGetStarted() {
        // Handle button tap
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentDataMap = _data.value.toMap(this).toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = Included2Data.fromMap(currentDataMap)
    }
}
