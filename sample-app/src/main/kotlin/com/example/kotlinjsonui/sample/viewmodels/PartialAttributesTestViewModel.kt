package com.example.kotlinjsonui.sample.viewmodels
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.PartialAttributesTestData

class PartialAttributesTestViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "partial_attributes_test"
    
    // Data model
    private val _data = MutableStateFlow(PartialAttributesTestData())
    val data: StateFlow<PartialAttributesTestData> = _data.asStateFlow()
    // Action handlers for partial text clicks
    fun navigateToPage1() {
        // Handle navigation to page 1
        println("Navigating to page 1")
    }
    fun navigateToPage2() {
        // Handle navigation to page 2
        println("Navigating to page 2")
    }
    // Generic handler for partial text clicks
    fun handlePartialClick(action: String) {
        // Handle partial text click based on action string
        println("Partial text clicked: $action")
        when (action) {
            "navigateToPage1" -> navigateToPage1()
            "navigateToPage2" -> navigateToPage2()
        }
    }
    // Action handlers
    fun onGetStarted() {
        // Handle button tap
    }

    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        // For now, just trigger recomposition
        // In a real app, you would update the data fields based on the updates map
        _data.value = _data.value.copy() // Trigger recomposition
    }
}
