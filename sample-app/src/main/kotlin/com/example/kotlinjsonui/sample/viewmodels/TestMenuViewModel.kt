package com.example.kotlinjsonui.sample.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.TestMenuData

class TestMenuViewModel : ViewModel() {
    // JSON file reference for hot reload
    val jsonFileName = "test_menu"
    
    // Data model
    private val _data = MutableStateFlow(TestMenuData())
    val data: StateFlow<TestMenuData> = _data.asStateFlow()
    
    // Navigation event
    private val _navigationEvent = MutableStateFlow<String?>(null)
    val navigationEvent: StateFlow<String?> = _navigationEvent.asStateFlow()
    
    // Dynamic mode toggle
    fun toggleDynamicMode() {
        val currentStatus = _data.value.dynamicModeStatus
        val newStatus = if (currentStatus == "ON") "OFF" else "ON"
        _data.value = _data.value.copy(dynamicModeStatus = newStatus)
    }
    
    // Layout & Positioning navigation
    fun navigateToMarginsTest() {
        _navigationEvent.value = "margins_test"
    }
    
    fun navigateToAlignmentTest() {
        _navigationEvent.value = "alignment_test"
    }
    
    fun navigateToAlignmentComboTest() {
        _navigationEvent.value = "alignment_combo_test"
    }
    
    fun navigateToWeightTest() {
        _navigationEvent.value = "weight_test"
    }
    
    fun navigateToWeightTestWithFixed() {
        _navigationEvent.value = "weight_test_with_fixed"
    }
    
    // Style & Appearance navigation
    fun navigateToVisibilityTest() {
        _navigationEvent.value = "visibility_test"
    }
    
    fun navigateToDisabledTest() {
        _navigationEvent.value = "disabled_test"
    }
    
    // Text Features navigation
    fun navigateToTextStylingTest() {
        _navigationEvent.value = "text_styling_test"
    }
    
    fun navigateToComponentsTest() {
        _navigationEvent.value = "components_test"
    }
    
    fun navigateToLineBreakTest() {
        _navigationEvent.value = "line_break_test"
    }
    
    // Input Components navigation
    fun navigateToSecureFieldTest() {
        _navigationEvent.value = "secure_field_test"
    }
    
    fun navigateToDatePickerTest() {
        _navigationEvent.value = "date_picker_test"
    }
    
    fun navigateToTextviewHintTest() {
        _navigationEvent.value = "text_view_hint_test"
    }
    
    // Advanced Features navigation
    fun navigateToRelativeTest() {
        _navigationEvent.value = "relative_test"
    }
    
    fun navigateToBindingTest() {
        _navigationEvent.value = "binding_test"
    }
    
    fun navigateToConverterTest() {
        _navigationEvent.value = "converter_test"
    }
    
    fun navigateToIncludeTest() {
        _navigationEvent.value = "include_test"
    }
    
    // Forms & Keyboard navigation
    fun navigateToFormTest() {
        _navigationEvent.value = "form_test"
    }
    
    fun navigateToKeyboardAvoidanceTest() {
        _navigationEvent.value = "keyboard_avoidance_test"
    }
    
    // Additional test screens
    fun navigateToButtonEnabledTest() {
        _navigationEvent.value = "button_enabled_test"
    }
    
    fun navigateToWidthTest() {
        _navigationEvent.value = "width_test"
    }
    
    // Clear navigation event after handling
    fun clearNavigationEvent() {
        _navigationEvent.value = null
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentDataMap = _data.value.toMap(this).toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = TestMenuData.fromMap(currentDataMap)
    }
}
