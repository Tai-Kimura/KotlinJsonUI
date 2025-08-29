package com.example.kotlinjsonui.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinjsonui.sample.viewmodels.*
import com.kotlinjsonui.core.KotlinJsonUI

class XmlTestActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize KotlinJsonUI library if not already initialized
        KotlinJsonUI.initialize(applicationContext)
        
        // Get the layout name from intent
        val layoutName = intent.getStringExtra("layout_name") ?: run {
            finish()
            return
        }
        
        // Set up the appropriate layout and view model based on layout name
        when (layoutName) {
            "alignment_test" -> setupAlignmentTest()
            "alignment_combo_test" -> setupAlignmentComboTest()
            "binding_test" -> setupBindingTest()
            "button_enabled_test" -> setupButtonEnabledTest()
            "button_test" -> setupButtonTest()
            "components_test" -> setupComponentsTest()
            "converter_test" -> setupConverterTest()
            "custom_component_test" -> setupCustomComponentTest()
            "date_picker_test" -> setupDatePickerTest()
            "disabled_test" -> setupDisabledTest()
            "form_test" -> setupFormTest()
            "implemented_attributes_test" -> setupImplementedAttributesTest()
            "include_test" -> setupIncludeTest()
            "keyboard_avoidance_test" -> setupKeyboardAvoidanceTest()
            "line_break_test" -> setupLineBreakTest()
            "margins_test" -> setupMarginsTest()
            "partial_attributes_test" -> setupPartialAttributesTest()
            "radio_icons_test" -> setupRadioIconsTest()
            "relative_test" -> setupRelativeTest()
            "scroll_test" -> setupScrollTest()
            "secure_field_test" -> setupSecureFieldTest()
            "segment_test" -> setupSegmentTest()
            "switch_events_test" -> setupSwitchEventsTest()
            "text_decoration_test" -> setupTextDecorationTest()
            "text_styling_test" -> setupTextStylingTest()
            "text_view_hint_test" -> setupTextViewHintTest()
            "textfield_events_test" -> setupTextfieldEventsTest()
            "textfield_test" -> setupTextfieldTest()
            "user_profile_test" -> setupUserProfileTest()
            "visibility_test" -> setupVisibilityTest()
            "weight_test" -> setupWeightTest()
            "weight_test_with_fixed" -> setupWeightTestWithFixed()
            "width_test" -> setupWidthTest()
            "collection_test" -> setupCollectionTest()
            else -> {
                finish()
                return
            }
        }
    }
    
    private fun setupAlignmentTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.AlignmentTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.alignment_test)
        val viewModel = ViewModelProvider(this)[AlignmentTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupAlignmentComboTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.AlignmentComboTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.alignment_combo_test)
        val viewModel = ViewModelProvider(this)[AlignmentComboTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupBindingTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.BindingTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.binding_test)
        val viewModel = ViewModelProvider(this)[BindingTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupButtonEnabledTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.ButtonEnabledTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.button_enabled_test)
        val viewModel = ViewModelProvider(this)[ButtonEnabledTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupButtonTest() {
        // button_test.xml doesn't have data binding, use regular setContentView
        setContentView(R.layout.button_test)
    }
    
    private fun setupComponentsTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.ComponentsTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.components_test)
        val viewModel = ViewModelProvider(this)[ComponentsTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupConverterTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.ConverterTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.converter_test)
        val viewModel = ViewModelProvider(this)[ConverterTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupCustomComponentTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.CustomComponentTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.custom_component_test)
        val viewModel = ViewModelProvider(this)[CustomComponentTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupDatePickerTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.DatePickerTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.date_picker_test)
        val viewModel = ViewModelProvider(this)[DatePickerTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupDisabledTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.DisabledTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.disabled_test)
        val viewModel = ViewModelProvider(this)[DisabledTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupFormTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.FormTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.form_test)
        val viewModel = ViewModelProvider(this)[FormTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupImplementedAttributesTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.ImplementedAttributesTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.implemented_attributes_test)
        val viewModel = ViewModelProvider(this)[ImplementedAttributesTestViewModel::class.java]
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupIncludeTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.IncludeTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.include_test)
        val viewModel = ViewModelProvider(this)[IncludeTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupKeyboardAvoidanceTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.KeyboardAvoidanceTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.keyboard_avoidance_test)
        val viewModel = ViewModelProvider(this)[KeyboardAvoidanceTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupLineBreakTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.LineBreakTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.line_break_test)
        val viewModel = ViewModelProvider(this)[LineBreakTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupMarginsTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.MarginsTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.margins_test)
        val viewModel = ViewModelProvider(this)[MarginsTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupPartialAttributesTest() {
        // partial_attributes_test.xml doesn't have data binding, use regular setContentView
        setContentView(R.layout.partial_attributes_test)
    }
    
    private fun setupRadioIconsTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.RadioIconsTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.radio_icons_test)
        val viewModel = ViewModelProvider(this)[RadioIconsTestViewModel::class.java]
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupRelativeTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.RelativeTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.relative_test)
        val viewModel = ViewModelProvider(this)[RelativeTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupScrollTest() {
        // scroll_test.xml doesn't have data binding, use regular setContentView
        setContentView(R.layout.scroll_test)
    }
    
    private fun setupSecureFieldTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.SecureFieldTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.secure_field_test)
        val viewModel = ViewModelProvider(this)[SecureFieldTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupSegmentTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.SegmentTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.segment_test)
        val viewModel = ViewModelProvider(this)[SegmentTestViewModel::class.java]
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupSwitchEventsTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.SwitchEventsTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.switch_events_test)
        val viewModel = ViewModelProvider(this)[SwitchEventsTestViewModel::class.java]
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupTextDecorationTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.TextDecorationTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.text_decoration_test)
        val viewModel = ViewModelProvider(this)[TextDecorationTestViewModel::class.java]
    }
    
    private fun setupTextStylingTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.TextStylingTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.text_styling_test)
        val viewModel = ViewModelProvider(this)[TextStylingTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupTextViewHintTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.TextViewHintTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.text_view_hint_test)
        val viewModel = ViewModelProvider(this)[TextViewHintTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupTextfieldEventsTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.TextfieldEventsTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.textfield_events_test)
        val viewModel = ViewModelProvider(this)[TextfieldEventsTestViewModel::class.java]
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupTextfieldTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.TextfieldTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.textfield_test)
        val viewModel = ViewModelProvider(this)[TextfieldTestViewModel::class.java]
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupUserProfileTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.UserProfileTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.user_profile_test)
        val viewModel = ViewModelProvider(this)[UserProfileTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupVisibilityTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.VisibilityTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.visibility_test)
        val viewModel = ViewModelProvider(this)[VisibilityTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupWeightTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.WeightTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.weight_test)
        val viewModel = ViewModelProvider(this)[WeightTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupWeightTestWithFixed() {
        val binding: com.example.kotlinjsonui.sample.databinding.WeightTestWithFixedBinding = 
            DataBindingUtil.setContentView(this, R.layout.weight_test_with_fixed)
        val viewModel = ViewModelProvider(this)[WeightTestWithFixedViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupWidthTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.WidthTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.width_test)
        val viewModel = ViewModelProvider(this)[WidthTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
    
    private fun setupCollectionTest() {
        val binding: com.example.kotlinjsonui.sample.databinding.CollectionTestBinding = 
            DataBindingUtil.setContentView(this, R.layout.collection_test)
        val viewModel = ViewModelProvider(this)[CollectionTestViewModel::class.java]
        binding.viewModel = viewModel
        binding.data = viewModel.data.value
        binding.lifecycleOwner = this
    }
}