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
            map["navigateToVisibilityTest"] = { vm.navigateToVisibilityTest() }
            map["navigateToDisabledTest"] = { vm.navigateToDisabledTest() }
            map["navigateToTextStylingTest"] = { vm.navigateToTextStylingTest() }
            map["navigateToComponentsTest"] = { vm.navigateToComponentsTest() }
            map["navigateToLineBreakTest"] = { vm.navigateToLineBreakTest() }
            map["navigateToSecureFieldTest"] = { vm.navigateToSecureFieldTest() }
            map["navigateToDatePickerTest"] = { vm.navigateToDatePickerTest() }
            map["navigateToTextviewHintTest"] = { vm.navigateToTextviewHintTest() }
            map["navigateToRelativeTest"] = { vm.navigateToRelativeTest() }
            map["navigateToBindingTest"] = { vm.navigateToBindingTest() }
            map["navigateToConverterTest"] = { vm.navigateToConverterTest() }
            map["navigateToIncludeTest"] = { vm.navigateToIncludeTest() }
            map["navigateToFormTest"] = { vm.navigateToFormTest() }
        }
        
        return map
    }
}
