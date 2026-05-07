package com.example.kotlinjsonui.sample.viewmodels
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.SegmentTestData

class SegmentTestViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "segment_test"
    
    // Data model
    private val _data = MutableStateFlow(SegmentTestData())
    val data: StateFlow<SegmentTestData> = _data.asStateFlow()
    // Action handlers
    fun handleSegmentChange(index: Int) {
        // Update the selected size based on the segment index
        val sizes = listOf("Small", "Medium", "Large", "Extra Large")
        val newData = _data.value.copy(
            selectedEvent = index,
            selectedSize = sizes.getOrElse(index) { "Unknown" }
        )
        _data.value = newData
    }
    // Update data from binding
    fun updateData(updates: Map<String, Any>) {
        val currentData = _data.value
        val newData = currentData.copy(
            selectedBasic = updates["selectedBasic"] as? Int ?: currentData.selectedBasic,
            selectedColor = updates["selectedColor"] as? Int ?: currentData.selectedColor,
            selectedEvent = updates["selectedEvent"] as? Int ?: currentData.selectedEvent,
            selectedDisabled = updates["selectedDisabled"] as? Int ?: currentData.selectedDisabled,
            selectedSize = updates["selectedSize"] as? String ?: currentData.selectedSize
        )
        _data.value = newData
    }
}
