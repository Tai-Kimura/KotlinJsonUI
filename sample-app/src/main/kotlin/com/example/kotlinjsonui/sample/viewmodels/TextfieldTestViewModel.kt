package com.example.kotlinjsonui.sample.viewmodels
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.TextfieldTestData

class TextfieldTestViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "textfield_test"
    
    // Data model
    private val _data = MutableStateFlow(TextfieldTestData())
    val data: StateFlow<TextfieldTestData> = _data.asStateFlow()
    
    // Action handlers
    fun onGetStarted() {
        // Handle button tap
    }

    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        _data.value = _data.value.copy()
        _data.value = _data.value.copy() // Trigger recomposition
    }
}
