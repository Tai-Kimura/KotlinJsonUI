// StringManager.kt
// Auto-generated file - DO NOT EDIT
// Generated at: 2025-09-01 02:13:03

package com.kotlinjsonui.generated

import android.content.Context

object StringManager {
    // String resource IDs mapped from strings.json keys
    private val stringResources: Map<String, Int> = mapOf(
        "alignment_combo_test" to R.string.alignment_combo_test,
        "alignment_test" to R.string.alignment_test,
        "binding_test" to R.string.binding_test,
        "button_enabled_test" to R.string.button_enabled_test,
        "button_test" to R.string.button_test,
        "collection_test" to R.string.collection_test,
        "components_test" to R.string.components_test,
        "converter_test" to R.string.converter_test,
        "custom_component_test" to R.string.custom_component_test,
        "date_picker_test" to R.string.date_picker_test,
        "disabled_test" to R.string.disabled_test,
        "form_test" to R.string.form_test,
        "implemented_attributes_test" to R.string.implemented_attributes_test,
        "include_test" to R.string.include_test,
        "included2" to R.string.included2,
        "keyboard_avoidance_test" to R.string.keyboard_avoidance_test,
        "line_break_test" to R.string.line_break_test,
        "margins_test" to R.string.margins_test,
        "partial_attributes_test" to R.string.partial_attributes_test,
        "radio_icons_test" to R.string.radio_icons_test,
        "relative_test" to R.string.relative_test,
        "scroll_test" to R.string.scroll_test,
        "secure_field_test" to R.string.secure_field_test,
        "segment_test" to R.string.segment_test,
        "simple_test" to R.string.simple_test,
        "switch_events_test" to R.string.switch_events_test,
        "test_menu" to R.string.test_menu,
        "text_decoration_test" to R.string.text_decoration_test,
        "text_styling_test" to R.string.text_styling_test,
        "text_view_hint_test" to R.string.text_view_hint_test,
        "textfield_events_test" to R.string.textfield_events_test,
        "textfield_test" to R.string.textfield_test,
        "user_profile_test" to R.string.user_profile_test,
        "visibility_test" to R.string.visibility_test,
        "weight_test" to R.string.weight_test,
        "weight_test_with_fixed" to R.string.weight_test_with_fixed,
        "width_test" to R.string.width_test
    )

    // Get localized string by key
    fun getString(context: Context, key: String): String {
        val resId = stringResources[key]
        return if (resId != null) {
            context.getString(resId)
        } else {
            // Fallback to key itself if not found
            println("Warning: String key '$key' not found in strings.json")
            key
        }
    }

    // Extension function for easy access
    fun String.localized(context: Context): String {
        // Check if this is a string key (snake_case)
        return if (this.matches(Regex("^[a-z]+(_[a-z]+)*$"))) {
            getString(context, this)
        } else {
            // Return as-is if not a key
            this
        }
    }

    // Access string: alignment_combo_test
    fun getAlignmentcombotest(context: Context): String =
        getString(context, "alignment_combo_test")

    // Access string: alignment_test
    fun getAlignmenttest(context: Context): String =
        getString(context, "alignment_test")

    // Access string: binding_test
    fun getBindingtest(context: Context): String =
        getString(context, "binding_test")

    // Access string: button_enabled_test
    fun getButtonenabledtest(context: Context): String =
        getString(context, "button_enabled_test")

    // Access string: button_test
    fun getButtontest(context: Context): String =
        getString(context, "button_test")

    // Access string: collection_test
    fun getCollectiontest(context: Context): String =
        getString(context, "collection_test")

    // Access string: components_test
    fun getComponentstest(context: Context): String =
        getString(context, "components_test")

    // Access string: converter_test
    fun getConvertertest(context: Context): String =
        getString(context, "converter_test")

    // Access string: custom_component_test
    fun getCustomcomponenttest(context: Context): String =
        getString(context, "custom_component_test")

    // Access string: date_picker_test
    fun getDatepickertest(context: Context): String =
        getString(context, "date_picker_test")

    // Access string: disabled_test
    fun getDisabledtest(context: Context): String =
        getString(context, "disabled_test")

    // Access string: form_test
    fun getFormtest(context: Context): String =
        getString(context, "form_test")

    // Access string: implemented_attributes_test
    fun getImplementedattributestest(context: Context): String =
        getString(context, "implemented_attributes_test")

    // Access string: include_test
    fun getIncludetest(context: Context): String =
        getString(context, "include_test")

    // Access string: included2
    fun getIncluded2(context: Context): String =
        getString(context, "included2")

    // Access string: keyboard_avoidance_test
    fun getKeyboardavoidancetest(context: Context): String =
        getString(context, "keyboard_avoidance_test")

    // Access string: line_break_test
    fun getLinebreaktest(context: Context): String =
        getString(context, "line_break_test")

    // Access string: margins_test
    fun getMarginstest(context: Context): String =
        getString(context, "margins_test")

    // Access string: partial_attributes_test
    fun getPartialattributestest(context: Context): String =
        getString(context, "partial_attributes_test")

    // Access string: radio_icons_test
    fun getRadioiconstest(context: Context): String =
        getString(context, "radio_icons_test")

    // Access string: relative_test
    fun getRelativetest(context: Context): String =
        getString(context, "relative_test")

    // Access string: scroll_test
    fun getScrolltest(context: Context): String =
        getString(context, "scroll_test")

    // Access string: secure_field_test
    fun getSecurefieldtest(context: Context): String =
        getString(context, "secure_field_test")

    // Access string: segment_test
    fun getSegmenttest(context: Context): String =
        getString(context, "segment_test")

    // Access string: simple_test
    fun getSimpletest(context: Context): String =
        getString(context, "simple_test")

    // Access string: switch_events_test
    fun getSwitcheventstest(context: Context): String =
        getString(context, "switch_events_test")

    // Access string: test_menu
    fun getTestmenu(context: Context): String =
        getString(context, "test_menu")

    // Access string: text_decoration_test
    fun getTextdecorationtest(context: Context): String =
        getString(context, "text_decoration_test")

    // Access string: text_styling_test
    fun getTextstylingtest(context: Context): String =
        getString(context, "text_styling_test")

    // Access string: text_view_hint_test
    fun getTextviewhinttest(context: Context): String =
        getString(context, "text_view_hint_test")

    // Access string: textfield_events_test
    fun getTextfieldeventstest(context: Context): String =
        getString(context, "textfield_events_test")

    // Access string: textfield_test
    fun getTextfieldtest(context: Context): String =
        getString(context, "textfield_test")

    // Access string: user_profile_test
    fun getUserprofiletest(context: Context): String =
        getString(context, "user_profile_test")

    // Access string: visibility_test
    fun getVisibilitytest(context: Context): String =
        getString(context, "visibility_test")

    // Access string: weight_test
    fun getWeighttest(context: Context): String =
        getString(context, "weight_test")

    // Access string: weight_test_with_fixed
    fun getWeighttestwithfixed(context: Context): String =
        getString(context, "weight_test_with_fixed")

    // Access string: width_test
    fun getWidthtest(context: Context): String =
        getString(context, "width_test")

}