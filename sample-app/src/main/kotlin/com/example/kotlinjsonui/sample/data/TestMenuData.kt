package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.TestMenuViewModel

data class TestMenuData(
    var dynamicModeStatus: String = "OFF"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): TestMenuData {
            return TestMenuData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: TestMenuViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        
        // Add onclick action lambdas if viewModel is provided
        viewModel?.let { vm ->
            map["toggleDynamicMode"] = { vm.toggleDynamicMode() }
            map["navigateToMarginsTest"] = { vm.navigateToMarginsTest() }
            map["navigateToAlignmentTest"] = { vm.navigateToAlignmentTest() }
            map["navigateToAlignmentComboTest"] = { vm.navigateToAlignmentComboTest() }
            map["navigateToWeightTest"] = { vm.navigateToWeightTest() }
            map["navigateToWeightTestWithFixed"] = { vm.navigateToWeightTestWithFixed() }
            map["navigateToWidthTest"] = { vm.navigateToWidthTest() }
            map["navigateToRelativeTest"] = { vm.navigateToRelativeTest() }
            map["navigateToVisibilityTest"] = { vm.navigateToVisibilityTest() }
            map["navigateToDisabledTest"] = { vm.navigateToDisabledTest() }
            map["navigateToTextStylingTest"] = { vm.navigateToTextStylingTest() }
            map["navigateToTextDecorationTest"] = { vm.navigateToTextDecorationTest() }
            map["navigateToLineBreakTest"] = { vm.navigateToLineBreakTest() }
            map["navigateToPartialAttributesTest"] = { vm.navigateToPartialAttributesTest() }
            map["navigateToTextFieldTest"] = { vm.navigateToTextFieldTest() }
            map["navigateToTextFieldEventsTest"] = { vm.navigateToTextFieldEventsTest() }
            map["navigateToSecureFieldTest"] = { vm.navigateToSecureFieldTest() }
            map["navigateToTextViewHintTest"] = { vm.navigateToTextViewHintTest() }
            map["navigateToDatePickerTest"] = { vm.navigateToDatePickerTest() }
            map["navigateToComponentsTest"] = { vm.navigateToComponentsTest() }
            map["navigateToButtonTest"] = { vm.navigateToButtonTest() }
            map["navigateToButtonEnabledTest"] = { vm.navigateToButtonEnabledTest() }
            map["navigateToSwitchEventsTest"] = { vm.navigateToSwitchEventsTest() }
            map["navigateToRadioIconsTest"] = { vm.navigateToRadioIconsTest() }
            map["navigateToSegmentTest"] = { vm.navigateToSegmentTest() }
            map["navigateToBindingTest"] = { vm.navigateToBindingTest() }
            map["navigateToConverterTest"] = { vm.navigateToConverterTest() }
            map["navigateToIncludeTest"] = { vm.navigateToIncludeTest() }
            map["navigateToFormTest"] = { vm.navigateToFormTest() }
            map["navigateToKeyboardAvoidanceTest"] = { vm.navigateToKeyboardAvoidanceTest() }
            map["navigateToScrollTest"] = { vm.navigateToScrollTest() }
            map["navigateToImplementedAttributesTest"] = { vm.navigateToImplementedAttributesTest() }
        }
        
        return map
    }
}
