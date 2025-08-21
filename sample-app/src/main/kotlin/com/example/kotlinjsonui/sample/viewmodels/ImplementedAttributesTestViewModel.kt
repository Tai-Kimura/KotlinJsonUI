package com.example.kotlinjsonui.sample.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.ImplementedAttributesTestData

class ImplementedAttributesTestViewModel : ViewModel() {
    // JSON file reference for hot reload
    val jsonFileName = "implemented_attributes_test"
    
    // Data model
    private val _data = MutableStateFlow(ImplementedAttributesTestData())
    val data: StateFlow<ImplementedAttributesTestData> = _data.asStateFlow()
    
    // Action handlers
    fun onGetStarted() {
        // Handle button tap
    }
    
    // TextField event handlers
    fun handleFocus() {
        println("TextField focused")
    }
    
    fun handleBlur() {
        println("TextField blurred")
    }
    
    fun handleBeginEditing() {
        println("TextField begin editing")
    }
    
    fun handleEndEditing() {
        println("TextField end editing")
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentData = _data.value
        val newData = currentData.copy(
            textFieldValue = updates["textFieldValue"] as? String ?: currentData.textFieldValue,
            selectedRadiogroup = updates["selectedRadiogroup"] as? String ?: currentData.selectedRadiogroup,
            selectedSegment = updates["selectedSegment"] as? Int ?: currentData.selectedSegment
        )
        _data.value = newData
    }
}
