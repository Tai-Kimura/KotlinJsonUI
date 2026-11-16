// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/test_menu.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class TestMenuData(
    var dynamicModeStatus: String = "Dynamic Mode: OFF",
    var toggleDynamicMode: (() -> Unit)? = null,
    var navigateToAlignmentComboTest: (() -> Unit)? = null,
    var navigateToAlignmentTest: (() -> Unit)? = null,
    var navigateToBindingTest: (() -> Unit)? = null,
    var navigateToButtonEnabledTest: (() -> Unit)? = null,
    var navigateToButtonTest: (() -> Unit)? = null,
    var navigateToCollectionTest: (() -> Unit)? = null,
    var navigateToComponentsTest: (() -> Unit)? = null,
    var navigateToConverterTest: (() -> Unit)? = null,
    var navigateToCustomComponentTest: (() -> Unit)? = null,
    var navigateToDatePickerTest: (() -> Unit)? = null,
    var navigateToDisabledTest: (() -> Unit)? = null,
    var navigateToFormTest: (() -> Unit)? = null,
    var navigateToImplementedAttributesTest: (() -> Unit)? = null,
    var navigateToIncludeTest: (() -> Unit)? = null,
    var navigateToKeyboardAvoidanceTest: (() -> Unit)? = null,
    var navigateToLineBreakTest: (() -> Unit)? = null,
    var navigateToMarginsTest: (() -> Unit)? = null,
    var navigateToPartialAttributesTest: (() -> Unit)? = null,
    var navigateToRadioIconsTest: (() -> Unit)? = null,
    var navigateToRelativeTest: (() -> Unit)? = null,
    var navigateToScrollTest: (() -> Unit)? = null,
    var navigateToSecureFieldTest: (() -> Unit)? = null,
    var navigateToSegmentTest: (() -> Unit)? = null,
    var navigateToSwitchEventsTest: (() -> Unit)? = null,
    var navigateToTextDecorationTest: (() -> Unit)? = null,
    var navigateToTextFieldEventsTest: (() -> Unit)? = null,
    var navigateToTextFieldTest: (() -> Unit)? = null,
    var navigateToTextStylingTest: (() -> Unit)? = null,
    var navigateToTextViewHintTest: (() -> Unit)? = null,
    var navigateToUserProfileTest: (() -> Unit)? = null,
    var navigateToVisibilityTest: (() -> Unit)? = null,
    var navigateToWeightTest: (() -> Unit)? = null,
    var navigateToWeightTestWithFixed: (() -> Unit)? = null,
    var navigateToWidthTest: (() -> Unit)? = null
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): TestMenuData {
            return TestMenuData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "Dynamic Mode: OFF",
                toggleDynamicMode = map["toggleDynamicMode"] as? (() -> Unit)?,
                navigateToAlignmentComboTest = map["navigateToAlignmentComboTest"] as? (() -> Unit)?,
                navigateToAlignmentTest = map["navigateToAlignmentTest"] as? (() -> Unit)?,
                navigateToBindingTest = map["navigateToBindingTest"] as? (() -> Unit)?,
                navigateToButtonEnabledTest = map["navigateToButtonEnabledTest"] as? (() -> Unit)?,
                navigateToButtonTest = map["navigateToButtonTest"] as? (() -> Unit)?,
                navigateToCollectionTest = map["navigateToCollectionTest"] as? (() -> Unit)?,
                navigateToComponentsTest = map["navigateToComponentsTest"] as? (() -> Unit)?,
                navigateToConverterTest = map["navigateToConverterTest"] as? (() -> Unit)?,
                navigateToCustomComponentTest = map["navigateToCustomComponentTest"] as? (() -> Unit)?,
                navigateToDatePickerTest = map["navigateToDatePickerTest"] as? (() -> Unit)?,
                navigateToDisabledTest = map["navigateToDisabledTest"] as? (() -> Unit)?,
                navigateToFormTest = map["navigateToFormTest"] as? (() -> Unit)?,
                navigateToImplementedAttributesTest = map["navigateToImplementedAttributesTest"] as? (() -> Unit)?,
                navigateToIncludeTest = map["navigateToIncludeTest"] as? (() -> Unit)?,
                navigateToKeyboardAvoidanceTest = map["navigateToKeyboardAvoidanceTest"] as? (() -> Unit)?,
                navigateToLineBreakTest = map["navigateToLineBreakTest"] as? (() -> Unit)?,
                navigateToMarginsTest = map["navigateToMarginsTest"] as? (() -> Unit)?,
                navigateToPartialAttributesTest = map["navigateToPartialAttributesTest"] as? (() -> Unit)?,
                navigateToRadioIconsTest = map["navigateToRadioIconsTest"] as? (() -> Unit)?,
                navigateToRelativeTest = map["navigateToRelativeTest"] as? (() -> Unit)?,
                navigateToScrollTest = map["navigateToScrollTest"] as? (() -> Unit)?,
                navigateToSecureFieldTest = map["navigateToSecureFieldTest"] as? (() -> Unit)?,
                navigateToSegmentTest = map["navigateToSegmentTest"] as? (() -> Unit)?,
                navigateToSwitchEventsTest = map["navigateToSwitchEventsTest"] as? (() -> Unit)?,
                navigateToTextDecorationTest = map["navigateToTextDecorationTest"] as? (() -> Unit)?,
                navigateToTextFieldEventsTest = map["navigateToTextFieldEventsTest"] as? (() -> Unit)?,
                navigateToTextFieldTest = map["navigateToTextFieldTest"] as? (() -> Unit)?,
                navigateToTextStylingTest = map["navigateToTextStylingTest"] as? (() -> Unit)?,
                navigateToTextViewHintTest = map["navigateToTextViewHintTest"] as? (() -> Unit)?,
                navigateToUserProfileTest = map["navigateToUserProfileTest"] as? (() -> Unit)?,
                navigateToVisibilityTest = map["navigateToVisibilityTest"] as? (() -> Unit)?,
                navigateToWeightTest = map["navigateToWeightTest"] as? (() -> Unit)?,
                navigateToWeightTestWithFixed = map["navigateToWeightTestWithFixed"] as? (() -> Unit)?,
                navigateToWidthTest = map["navigateToWidthTest"] as? (() -> Unit)?
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        toggleDynamicMode?.let { map["toggleDynamicMode"] = it }
        navigateToAlignmentComboTest?.let { map["navigateToAlignmentComboTest"] = it }
        navigateToAlignmentTest?.let { map["navigateToAlignmentTest"] = it }
        navigateToBindingTest?.let { map["navigateToBindingTest"] = it }
        navigateToButtonEnabledTest?.let { map["navigateToButtonEnabledTest"] = it }
        navigateToButtonTest?.let { map["navigateToButtonTest"] = it }
        navigateToCollectionTest?.let { map["navigateToCollectionTest"] = it }
        navigateToComponentsTest?.let { map["navigateToComponentsTest"] = it }
        navigateToConverterTest?.let { map["navigateToConverterTest"] = it }
        navigateToCustomComponentTest?.let { map["navigateToCustomComponentTest"] = it }
        navigateToDatePickerTest?.let { map["navigateToDatePickerTest"] = it }
        navigateToDisabledTest?.let { map["navigateToDisabledTest"] = it }
        navigateToFormTest?.let { map["navigateToFormTest"] = it }
        navigateToImplementedAttributesTest?.let { map["navigateToImplementedAttributesTest"] = it }
        navigateToIncludeTest?.let { map["navigateToIncludeTest"] = it }
        navigateToKeyboardAvoidanceTest?.let { map["navigateToKeyboardAvoidanceTest"] = it }
        navigateToLineBreakTest?.let { map["navigateToLineBreakTest"] = it }
        navigateToMarginsTest?.let { map["navigateToMarginsTest"] = it }
        navigateToPartialAttributesTest?.let { map["navigateToPartialAttributesTest"] = it }
        navigateToRadioIconsTest?.let { map["navigateToRadioIconsTest"] = it }
        navigateToRelativeTest?.let { map["navigateToRelativeTest"] = it }
        navigateToScrollTest?.let { map["navigateToScrollTest"] = it }
        navigateToSecureFieldTest?.let { map["navigateToSecureFieldTest"] = it }
        navigateToSegmentTest?.let { map["navigateToSegmentTest"] = it }
        navigateToSwitchEventsTest?.let { map["navigateToSwitchEventsTest"] = it }
        navigateToTextDecorationTest?.let { map["navigateToTextDecorationTest"] = it }
        navigateToTextFieldEventsTest?.let { map["navigateToTextFieldEventsTest"] = it }
        navigateToTextFieldTest?.let { map["navigateToTextFieldTest"] = it }
        navigateToTextStylingTest?.let { map["navigateToTextStylingTest"] = it }
        navigateToTextViewHintTest?.let { map["navigateToTextViewHintTest"] = it }
        navigateToUserProfileTest?.let { map["navigateToUserProfileTest"] = it }
        navigateToVisibilityTest?.let { map["navigateToVisibilityTest"] = it }
        navigateToWeightTest?.let { map["navigateToWeightTest"] = it }
        navigateToWeightTestWithFixed?.let { map["navigateToWeightTestWithFixed"] = it }
        navigateToWidthTest?.let { map["navigateToWidthTest"] = it }
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
