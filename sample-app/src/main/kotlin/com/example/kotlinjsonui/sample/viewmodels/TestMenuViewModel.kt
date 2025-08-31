package com.example.kotlinjsonui.sample.viewmodels
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.TestMenuData
import com.example.kotlinjsonui.sample.XmlTestActivity
import com.kotlinjsonui.core.DynamicModeManager

class TestMenuViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "test_menu"
    
    // Data model
    private val _data = MutableStateFlow(TestMenuData())
    val data: StateFlow<TestMenuData> = _data.asStateFlow()
    
    // Navigation mode flag (true = XML, false = Compose)
    var useXmlNavigation = false
    
    // Navigation event for Compose
    private val _navigationEvent = MutableStateFlow<String?>(null)
    val navigationEvent: StateFlow<String?> = _navigationEvent.asStateFlow()
    
    // Navigation event for XML (Intent)
    private val _xmlNavigationEvent = MutableStateFlow<Intent?>(null)
    val xmlNavigationEvent: StateFlow<Intent?> = _xmlNavigationEvent.asStateFlow()
    
    // Private helper function to handle navigation
    private fun navigate(destination: String) {
        if (useXmlNavigation) {
            // Create intent for XML navigation
            val intent = Intent(getApplication(), XmlTestActivity::class.java).apply {
                putExtra("layout_name", destination)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            _xmlNavigationEvent.value = intent
        } else {
            // Use Compose navigation
            _navigationEvent.value = destination
        }
    }
    // Dynamic mode toggle
    fun toggleDynamicMode() {
        // Toggle the actual DynamicModeManager
        val newState = DynamicModeManager.toggleDynamicMode(getApplication())
        
        // Update the UI status based on actual state
        val statusText = if (newState == true) "ON" else "OFF"
        _data.value = _data.value.copy(dynamicModeStatus = "Dynamic Mode: ${statusText}")
    }
    // Layout & Positioning navigation
    fun navigateToMarginsTest() {
        navigate("margins_test")
    }
    
    fun navigateToAlignmentTest() {
        navigate("alignment_test")
    }
    
    fun navigateToAlignmentComboTest() {
        navigate("alignment_combo_test")
    }
    
    fun navigateToWeightTest() {
        navigate("weight_test")
    }
    
    fun navigateToWeightTestWithFixed() {
        navigate("weight_test_with_fixed")
    }
    
    // Style & Appearance navigation
    fun navigateToVisibilityTest() {
        navigate("visibility_test")
    }
    
    fun navigateToDisabledTest() {
        navigate("disabled_test")
    }
    
    // Text Features navigation
    fun navigateToTextStylingTest() {
        navigate("text_styling_test")
    }
    
    fun navigateToComponentsTest() {
        navigate("components_test")
    }
    
    fun navigateToLineBreakTest() {
        navigate("line_break_test")
    }
    
    // Input Components navigation
    fun navigateToSecureFieldTest() {
        navigate("secure_field_test")
    }
    
    fun navigateToDatePickerTest() {
        navigate("date_picker_test")
    }
    
    fun navigateToTextviewHintTest() {
        navigate("text_view_hint_test")
    }
    
    // Advanced Features navigation
    fun navigateToRelativeTest() {
        navigate("relative_test")
    }
    
    fun navigateToBindingTest() {
        navigate("binding_test")
    }
    
    fun navigateToConverterTest() {
        navigate("converter_test")
    }
    
    fun navigateToCustomComponentTest() {
        navigate("custom_component_test")
    }
    
    fun navigateToUserProfileTest() {
        navigate("user_profile_test")
    }
    
    fun navigateToIncludeTest() {
        navigate("include_test")
    }
    
    // Forms & Keyboard navigation
    fun navigateToFormTest() {
        navigate("form_test")
    }
    
    fun navigateToCollectionTest() {
        navigate("collection_test")
    }
    
    fun navigateToKeyboardAvoidanceTest() {
        navigate("keyboard_avoidance_test")
    }
    
    // Additional test screens
    fun navigateToButtonTest() {
        navigate("button_test")
    }
    
    fun navigateToButtonEnabledTest() {
        navigate("button_enabled_test")
    }
    
    fun navigateToWidthTest() {
        navigate("width_test")
    }
    
    fun navigateToScrollTest() {
        navigate("scroll_test")
    }
    
    fun navigateToTextFieldTest() {
        navigate("textfield_test")
    }
    
    fun navigateToTextFieldEventsTest() {
        navigate("textfield_events_test")
    }
    
    fun navigateToTextDecorationTest() {
        navigate("text_decoration_test")
    }
    
    fun navigateToPartialAttributesTest() {
        navigate("partial_attributes_test")
    }
    
    fun navigateToTextViewHintTest() {
        navigate("text_view_hint_test")
    }
    
    fun navigateToSwitchEventsTest() {
        navigate("switch_events_test")
    }
    
    fun navigateToRadioIconsTest() {
        navigate("radio_icons_test")
    }
    
    fun navigateToSegmentTest() {
        navigate("segment_test")
    }
    
    fun navigateToImplementedAttributesTest() {
        navigate("implemented_attributes_test")
    }
    
    // Clear navigation event after handling
    fun clearNavigationEvent() {
        _navigationEvent.value = null
    }
    
    fun clearXmlNavigationEvent() {
        _xmlNavigationEvent.value = null
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentDataMap = _data.value.toMap(this).toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = TestMenuData.fromMap(currentDataMap)
    }
}
