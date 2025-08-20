package com.example.kotlinjsonui.sample.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.WeightTestWithFixedData

class WeightTestWithFixedViewModel : ViewModel() {
    // JSON file reference for hot reload
    val jsonFileName = "weight_test_with_fixed"
    
    // Data model
    private val _data = MutableStateFlow(WeightTestWithFixedData())
    val data: StateFlow<WeightTestWithFixedData> = _data.asStateFlow()
    
    // Action handlers
    fun onGetStarted() {
        // Handle button tap
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentDataMap = _data.value.toMap(this).toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = WeightTestWithFixedData.fromMap(currentDataMap)
    }
}
