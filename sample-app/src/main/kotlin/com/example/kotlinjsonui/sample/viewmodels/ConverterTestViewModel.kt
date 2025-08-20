package com.example.kotlinjsonui.sample.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.ConverterTestData

class ConverterTestViewModel : ViewModel() {
    // JSON file reference for hot reload
    val jsonFileName = "converter_test"
    
    // Data model
    private val _data = MutableStateFlow(ConverterTestData())
    val data: StateFlow<ConverterTestData> = _data.asStateFlow()
    
    // Action handlers
    fun onGetStarted() {
        // Handle button tap
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentDataMap = _data.value.toMap(this).toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = ConverterTestData.fromMap(currentDataMap)
    }
}
