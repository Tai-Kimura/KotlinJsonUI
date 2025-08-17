package com.example.kotlinjsonui.sample.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.AlignmentTestData

class AlignmentTestViewModel : ViewModel() {
    // JSON file reference for hot reload
    val jsonFileName = "alignment_test"
    
    // Data model
    private val _data = MutableStateFlow(AlignmentTestData())
    val data: StateFlow<AlignmentTestData> = _data.asStateFlow()
    
    // Action handlers
    fun onGetStarted() {
        // Handle button tap
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        _data.value.update(updates)
        _data.value = _data.value.copy() // Trigger recomposition
    }
}
