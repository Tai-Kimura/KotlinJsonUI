package com.example.kotlinjsonui.sample.viewmodels
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.SegmentTestData

class SegmentTestViewModel(application: Application) : AndroidViewModel(application) {
    private val SIZES = listOf("Small", "Medium", "Large", "Extra Large")
    // JSON file reference for hot reload
    val jsonFileName = "segment_test"
    
    // Data model
    private val _data = MutableStateFlow(SegmentTestData())
    // The layout binds each display line as one value (binding-mixed-text,
    // jsonui-cli 1.9.6: a layout holds no logic); the line is composed here,
    // from the state, every time it changes.
    val data: StateFlow<SegmentTestData> = _data.map(::withDisplayTexts)
        .stateIn(viewModelScope, SharingStarted.Eagerly, withDisplayTexts(_data.value))

    private fun withDisplayTexts(d: SegmentTestData): SegmentTestData = d.copy(
        selectedSizeText = "Selected: ${SIZES.getOrElse(d.selectedEvent) { "Unknown" }}"
    )
    // Action handlers
    fun handleSegmentChange(index: Int) {
        // Update the selected size based on the segment index
        val newData = _data.value.copy(selectedEvent = index)
        _data.value = newData
    }
    // Update data from binding
    fun updateData(updates: Map<String, Any>) {
        val currentData = _data.value
        val newData = currentData.copy(
            selectedBasic = updates["selectedBasic"] as? Int ?: currentData.selectedBasic,
            selectedColor = updates["selectedColor"] as? Int ?: currentData.selectedColor,
            selectedEvent = updates["selectedEvent"] as? Int ?: currentData.selectedEvent,
            selectedDisabled = updates["selectedDisabled"] as? Int ?: currentData.selectedDisabled
        )
        _data.value = newData
    }
    
    init {
        // Wire JSON-declared event handlers: current kjui codegen invokes
        // handlers through the data model (data.<name>?.invoke(...)).
        _data.value = _data.value.copy(
            handleSegmentChange = { _, value -> handleSegmentChange(value) }
        )
    }
}
