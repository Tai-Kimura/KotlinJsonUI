package com.example.kotlinjsonui.sample.viewmodels
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.RadioIconsTestData

class RadioIconsTestViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "radio_icons_test"
    
    // Data model
    private val _data = MutableStateFlow(RadioIconsTestData())
    val data: StateFlow<RadioIconsTestData> = _data.asStateFlow()
    // Action handlers
    fun onGetStarted() {
        // Handle button tap
    }
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentData = _data.value
        val newData = currentData.copy(
            selectedColor = updates["selectedColor"] as? String ?: currentData.selectedColor
        )
        _data.value = newData
    }
}
