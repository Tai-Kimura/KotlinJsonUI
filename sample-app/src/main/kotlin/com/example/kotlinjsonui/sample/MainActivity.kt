package com.example.kotlinjsonui.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kotlinjsonui.sample.ui.theme.KotlinJsonUITheme
import com.example.kotlinjsonui.sample.viewmodels.*
import com.example.kotlinjsonui.sample.views.test_menu.TestMenuView
import com.example.kotlinjsonui.sample.views.alignment_test.AlignmentTestView
import com.example.kotlinjsonui.sample.views.alignment_combo_test.AlignmentComboTestView
import com.example.kotlinjsonui.sample.views.binding_test.BindingTestView
import com.example.kotlinjsonui.sample.views.components_test.ComponentsTestView
import com.example.kotlinjsonui.sample.views.converter_test.ConverterTestView
import com.example.kotlinjsonui.sample.views.date_picker_test.DatePickerTestView
import com.example.kotlinjsonui.sample.views.disabled_test.DisabledTestView
import com.example.kotlinjsonui.sample.views.form_test.FormTestView
import com.example.kotlinjsonui.sample.views.relative_test.RelativeTestView
import com.example.kotlinjsonui.sample.views.visibility_test.VisibilityTestView
import com.example.kotlinjsonui.sample.views.button_enabled_test.ButtonEnabledTestView
import com.example.kotlinjsonui.sample.views.include_test.IncludeTestView
import com.example.kotlinjsonui.sample.views.keyboard_avoidance_test.KeyboardAvoidanceTestView
import com.example.kotlinjsonui.sample.views.line_break_test.LineBreakTestView
import com.example.kotlinjsonui.sample.views.margins_test.MarginsTestView
import com.example.kotlinjsonui.sample.views.secure_field_test.SecureFieldTestView
import com.example.kotlinjsonui.sample.views.text_styling_test.TextStylingTestView
import com.example.kotlinjsonui.sample.views.text_view_hint_test.TextViewHintTestView
import com.example.kotlinjsonui.sample.views.weight_test.WeightTestView
import com.example.kotlinjsonui.sample.views.weight_test_with_fixed.WeightTestWithFixedView
import com.example.kotlinjsonui.sample.views.width_test.WidthTestView
import com.example.kotlinjsonui.sample.views.button_test.ButtonTestView
import com.example.kotlinjsonui.sample.views.implemented_attributes_test.ImplementedAttributesTestView
import com.example.kotlinjsonui.sample.views.partial_attributes_test.PartialAttributesTestView
import com.example.kotlinjsonui.sample.views.radio_icons_test.RadioIconsTestView
import com.example.kotlinjsonui.sample.views.scroll_test.ScrollTestView
import com.example.kotlinjsonui.sample.views.segment_test.SegmentTestView
import com.example.kotlinjsonui.sample.views.switch_events_test.SwitchEventsTestView
import com.example.kotlinjsonui.sample.views.text_decoration_test.TextDecorationTestView
import com.example.kotlinjsonui.sample.views.textfield_events_test.TextfieldEventsTestView
import com.example.kotlinjsonui.sample.views.textfield_test.TextfieldTestView
import com.example.kotlinjsonui.sample.views.collection_test.CollectionTestView
import com.example.kotlinjsonui.sample.views.custom_component_test.CustomComponentTestView
import com.example.kotlinjsonui.sample.views.user_profile_test.UserProfileTestView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.KotlinJsonUI
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: TestMenuViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize KotlinJsonUI library
        // This automatically handles:
        // - Dynamic mode initialization for debug builds
        // - Dynamic component registration when available
        // - Cache clearing
        KotlinJsonUI.initialize(applicationContext)


        // Configure link color globally (optional - you can customize this)
        // Default is blue (0xFF0000EE), but you can change it to any color
        // Configuration.Colors.linkColor = Color(0xFF007AFF)  // iOS style blue
        // Configuration.Colors.linkColor = Color(0xFF1976D2)  // Material blue
        // Configuration.Colors.linkColor = Color.Red  // Red links
//
//        // Use XML layout instead of Compose
//        val binding: TestMenuBinding = DataBindingUtil.setContentView(this, R.layout.test_menu)
//        viewModel = ViewModelProvider(this)[TestMenuViewModel::class.java]
//
//        // Set ViewModel to use XML navigation
//        viewModel.useXmlNavigation = true
//
//        // Bind data
//        binding.viewModel = viewModel
//        binding.data = viewModel.data.value
//        binding.lifecycleOwner = this
//
//        // Observe XML navigation events
//        lifecycleScope.launch {
//            viewModel.xmlNavigationEvent.collect { intent ->
//                intent?.let {
//                    startActivity(it)
//                    viewModel.clearXmlNavigationEvent()
//                }
//            }
//        }
//
        setContent {
            KotlinJsonUITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
    
    // onClick handlers for XML layout buttons
    fun toggleDynamicMode(view: android.view.View) {
        viewModel.toggleDynamicMode()
    }
    
    fun navigateToMarginsTest(view: android.view.View) {
        viewModel.navigateToMarginsTest()
    }
    
    fun navigateToAlignmentTest(view: android.view.View) {
        viewModel.navigateToAlignmentTest()
    }
    
    fun navigateToAlignmentComboTest(view: android.view.View) {
        viewModel.navigateToAlignmentComboTest()
    }
    
    fun navigateToWeightTest(view: android.view.View) {
        viewModel.navigateToWeightTest()
    }
    
    fun navigateToWeightTestWithFixed(view: android.view.View) {
        viewModel.navigateToWeightTestWithFixed()
    }
    
    fun navigateToVisibilityTest(view: android.view.View) {
        viewModel.navigateToVisibilityTest()
    }
    
    fun navigateToDisabledTest(view: android.view.View) {
        viewModel.navigateToDisabledTest()
    }
    
    fun navigateToTextStylingTest(view: android.view.View) {
        viewModel.navigateToTextStylingTest()
    }
    
    fun navigateToComponentsTest(view: android.view.View) {
        viewModel.navigateToComponentsTest()
    }
    
    fun navigateToLineBreakTest(view: android.view.View) {
        viewModel.navigateToLineBreakTest()
    }
    
    fun navigateToSecureFieldTest(view: android.view.View) {
        viewModel.navigateToSecureFieldTest()
    }
    
    fun navigateToDatePickerTest(view: android.view.View) {
        viewModel.navigateToDatePickerTest()
    }
    
    fun navigateToTextviewHintTest(view: android.view.View) {
        viewModel.navigateToTextviewHintTest()
    }
    
    fun navigateToRelativeTest(view: android.view.View) {
        viewModel.navigateToRelativeTest()
    }
    
    fun navigateToBindingTest(view: android.view.View) {
        viewModel.navigateToBindingTest()
    }
    
    fun navigateToConverterTest(view: android.view.View) {
        viewModel.navigateToConverterTest()
    }
    
    fun navigateToCustomComponentTest(view: android.view.View) {
        viewModel.navigateToCustomComponentTest()
    }
    
    fun navigateToUserProfileTest(view: android.view.View) {
        viewModel.navigateToUserProfileTest()
    }
    
    fun navigateToIncludeTest(view: android.view.View) {
        viewModel.navigateToIncludeTest()
    }
    
    fun navigateToFormTest(view: android.view.View) {
        viewModel.navigateToFormTest()
    }
    
    fun navigateToCollectionTest(view: android.view.View) {
        viewModel.navigateToCollectionTest()
    }
    
    fun navigateToKeyboardAvoidanceTest(view: android.view.View) {
        viewModel.navigateToKeyboardAvoidanceTest()
    }
    
    fun navigateToButtonTest(view: android.view.View) {
        viewModel.navigateToButtonTest()
    }
    
    fun navigateToButtonEnabledTest(view: android.view.View) {
        viewModel.navigateToButtonEnabledTest()
    }
    
    fun navigateToWidthTest(view: android.view.View) {
        viewModel.navigateToWidthTest()
    }
    
    fun navigateToScrollTest(view: android.view.View) {
        viewModel.navigateToScrollTest()
    }
    
    fun navigateToTextFieldTest(view: android.view.View) {
        viewModel.navigateToTextFieldTest()
    }
    
    fun navigateToTextFieldEventsTest(view: android.view.View) {
        viewModel.navigateToTextFieldEventsTest()
    }
    
    fun navigateToTextDecorationTest(view: android.view.View) {
        viewModel.navigateToTextDecorationTest()
    }
    
    fun navigateToPartialAttributesTest(view: android.view.View) {
        viewModel.navigateToPartialAttributesTest()
    }
    
    fun navigateToTextViewHintTest(view: android.view.View) {
        viewModel.navigateToTextViewHintTest()
    }
    
    fun navigateToSwitchEventsTest(view: android.view.View) {
        viewModel.navigateToSwitchEventsTest()
    }
    
    fun navigateToRadioIconsTest(view: android.view.View) {
        viewModel.navigateToRadioIconsTest()
    }
    
    fun navigateToSegmentTest(view: android.view.View) {
        viewModel.navigateToSegmentTest()
    }
    
    fun navigateToImplementedAttributesTest(view: android.view.View) {
        viewModel.navigateToImplementedAttributesTest()
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "test_menu"
    ) {
        composable("test_menu") {
            TestMenuWithNavigation(navController)
        }
        
        // Navigation screens
        composable("alignment_test") {
            AlignmentTestView()
        }
        composable("alignment_combo_test") {
            AlignmentComboTestView()
        }
        composable("binding_test") {
            BindingTestView()
        }
        composable("components_test") {
            ComponentsTestView()
        }
        composable("converter_test") {
            ConverterTestView()
        }
        composable("date_picker_test") {
            DatePickerTestView()
        }
        composable("disabled_test") {
            DisabledTestView()
        }
        composable("form_test") {
            FormTestView()
        }
        composable("relative_test") {
            RelativeTestView()
        }
        composable("visibility_test") {
            VisibilityTestView()
        }
        composable("button_enabled_test") {
            ButtonEnabledTestView()
        }
        composable("include_test") {
            IncludeTestView()
        }
        composable("keyboard_avoidance_test") {
            KeyboardAvoidanceTestView()
        }
        composable("line_break_test") {
            LineBreakTestView()
        }
        composable("margins_test") {
            MarginsTestView()
        }
        composable("secure_field_test") {
            SecureFieldTestView()
        }
        composable("text_styling_test") {
            TextStylingTestView()
        }
        composable("text_view_hint_test") {
            TextViewHintTestView()
        }
        composable("weight_test") {
            WeightTestView()
        }
        composable("weight_test_with_fixed") {
            WeightTestWithFixedView()
        }
        composable("width_test") {
            WidthTestView()
        }
        composable("button_test") {
            ButtonTestView()
        }
        composable("implemented_attributes_test") {
            ImplementedAttributesTestView()
        }
        composable("partial_attributes_test") {
            PartialAttributesTestView()
        }
        composable("radio_icons_test") {
            RadioIconsTestView()
        }
        composable("scroll_test") {
            ScrollTestView()
        }
        composable("segment_test") {
            SegmentTestView()
        }
        composable("switch_events_test") {
            SwitchEventsTestView()
        }
        composable("text_decoration_test") {
            TextDecorationTestView()
        }
        composable("textfield_events_test") {
            TextfieldEventsTestView()
        }
        composable("textfield_test") {
            TextfieldTestView()
        }
        composable("collection_test") {
            CollectionTestView()
        }
        composable("custom_component_test") {
            CustomComponentTestView()
        }
        composable("user_profile_test") {
            UserProfileTestView()
        }
    }
}

@Composable
fun TestMenuWithNavigation(navController: NavHostController) {
    val viewModel: TestMenuViewModel = viewModel()
    val navigationEvent by viewModel.navigationEvent.collectAsState()
    
    // Handle navigation events
    LaunchedEffect(navigationEvent) {
        navigationEvent?.let { destination ->
            navController.navigate(destination)
            viewModel.clearNavigationEvent()
        }
    }
    
    TestMenuView(viewModel = viewModel)
}