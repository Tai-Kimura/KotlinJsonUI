package com.example.kotlinjsonui.sample.viewmodels
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.TestMenuData
import com.kotlinjsonui.core.DynamicModeManager
class TestMenuViewModel(application: Application) : AndroidViewModel(application) {
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
        // Toggle the actual DynamicModeManager
        val newState = DynamicModeManager.toggleDynamicMode(getApplication())
        
        // Update the UI status based on actual state
        val statusText = if (newState == true) "ON" else "OFF"
        _data.value = _data.value.copy(dynamicModeStatus = statusText)
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
    
    fun navigateToCollectionTest() {
        _navigationEvent.value = "collection_test"
    }
    
    fun navigateToKeyboardAvoidanceTest() {
        _navigationEvent.value = "keyboard_avoidance_test"
    }
    
    // Additional test screens
    fun navigateToButtonTest() {
        _navigationEvent.value = "button_test"
    }
    
    fun navigateToButtonEnabledTest() {
        _navigationEvent.value = "button_enabled_test"
    }
    
    fun navigateToWidthTest() {
        _navigationEvent.value = "width_test"
    }
    
    fun navigateToScrollTest() {
        _navigationEvent.value = "scroll_test"
    }
    
    fun navigateToTextFieldTest() {
        _navigationEvent.value = "textfield_test"
    }
    
    fun navigateToTextFieldEventsTest() {
        _navigationEvent.value = "textfield_events_test"
    }
    
    fun navigateToTextDecorationTest() {
        _navigationEvent.value = "text_decoration_test"
    }
    
    fun navigateToPartialAttributesTest() {
        _navigationEvent.value = "partial_attributes_test"
    }
    
    fun navigateToTextViewHintTest() {
        _navigationEvent.value = "text_view_hint_test"
    }
    
    fun navigateToSwitchEventsTest() {
        _navigationEvent.value = "switch_events_test"
    }
    
    fun navigateToRadioIconsTest() {
        _navigationEvent.value = "radio_icons_test"
    }
    
    fun navigateToSegmentTest() {
        _navigationEvent.value = "segment_test"
    }
    
    fun navigateToImplementedAttributesTest() {
        _navigationEvent.value = "implemented_attributes_test"
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
