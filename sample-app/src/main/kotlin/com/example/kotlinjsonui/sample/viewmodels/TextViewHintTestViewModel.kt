package com.example.kotlinjsonui.sample.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.TextViewHintTestData

class TextViewHintTestViewModel : ViewModel() {
    // JSON file reference for hot reload
    val jsonFileName = "text_view_hint_test"
    
    // Data model
    private val _data = MutableStateFlow(TextViewHintTestData())
    val data: StateFlow<TextViewHintTestData> = _data.asStateFlow()
    
    // Action handlers
    fun onGetStarted() {
        // Handle button tap
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentDataMap = _data.value.toMap(this).toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = TextViewHintTestData.fromMap(currentDataMap)
    }
}
