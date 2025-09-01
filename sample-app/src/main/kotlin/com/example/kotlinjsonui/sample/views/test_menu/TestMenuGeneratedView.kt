package com.example.kotlinjsonui.sample.views.test_menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.TestMenuData
import com.example.kotlinjsonui.sample.viewmodels.TestMenuViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.colorResource
import com.example.kotlinjsonui.sample.R

@Composable
fun TestMenuGeneratedView(
    data: TestMenuData,
    viewModel: TestMenuViewModel
) {
    // Generated Compose code from test_menu.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "test_menu",
            data = data.toMap(viewModel),
            fallback = {
                // Show error or loading state when dynamic view is not available
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Dynamic view not available",
                        color = Color.Gray
                    )
                }
            },
            onError = { error ->
                // Log error or show error UI
                android.util.Log.e("DynamicView", "Error loading test_menu: \$error")
            },
            onLoading = {
                // Show loading indicator
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        ) { jsonContent ->
            // Parse and render the dynamic JSON content
            // This will be handled by the DynamicView implementation
        }
    } else {
        // Static Mode - use generated code
        LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(R.color.white_23))
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.white_23))
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.test_menu_kotlinjsonui_feature_tests),
                fontSize = 28.sp,
                color = colorResource(R.color.black),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = { viewModel.toggleDynamicMode() },
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .wrapContentWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue_3)
                                )
            ) {
                Text(
                    text = "${data.dynamicModeStatus}",
                    fontSize = 14.sp,
                    color = colorResource(R.color.white),
                )
            }
            Text(
                text = stringResource(R.string.test_menu_layout_positioning),
                fontSize = 20.sp,
                color = colorResource(R.color.medium_gray_4),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Button(
                onClick = { viewModel.navigateToMarginsTest() },
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .wrapContentWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_margins_padding_test),
                    fontSize = 16.sp,
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToAlignmentTest() },
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .wrapContentWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_alignment_test_2),
                    fontSize = 16.sp,
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToAlignmentComboTest() },
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .wrapContentWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_alignment_combo_test_2),
                    fontSize = 16.sp,
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToWeightTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_weight_distribution_test),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToWeightTestWithFixed() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_weight_fixed_size_test),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToWidthTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_width_test),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToRelativeTest() },
                modifier = Modifier.padding(bottom = 20.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_relative_positioning_test),
                    color = colorResource(R.color.white),
                )
            }
            Text(
                text = stringResource(R.string.test_menu_style_appearance),
                fontSize = 20.sp,
                color = colorResource(R.color.medium_gray_4),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Button(
                onClick = { viewModel.navigateToVisibilityTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_green)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_visibility_opacity_test),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToDisabledTest() },
                modifier = Modifier.padding(bottom = 20.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_green)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_disabled_states_test),
                    color = colorResource(R.color.white),
                )
            }
            Text(
                text = stringResource(R.string.test_menu_text_features),
                fontSize = 20.sp,
                color = colorResource(R.color.medium_gray_4),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Button(
                onClick = { viewModel.navigateToTextStylingTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_red_3)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_text_styling_test),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToTextDecorationTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_red_3)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_text_decoration_test),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToLineBreakTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_red_3)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_line_break_spacing_test),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToPartialAttributesTest() },
                modifier = Modifier.padding(bottom = 20.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_red_3)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_partial_attributes_test_2),
                    color = colorResource(R.color.white),
                )
            }
            Text(
                text = stringResource(R.string.test_menu_input_components),
                fontSize = 20.sp,
                color = colorResource(R.color.medium_gray_4),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Button(
                onClick = { viewModel.navigateToTextFieldTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.light_purple)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_textfield_test),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToTextFieldEventsTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.light_purple)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_textfield_events_test),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToSecureFieldTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.light_purple)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_secure_field_test_2),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToTextViewHintTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.light_purple)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_textview_hint_test),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToDatePickerTest() },
                modifier = Modifier.padding(bottom = 20.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.light_purple)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_date_picker_test_2),
                    color = colorResource(R.color.white),
                )
            }
            Text(
                text = stringResource(R.string.test_menu_ui_components),
                fontSize = 20.sp,
                color = colorResource(R.color.medium_gray_4),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Button(
                onClick = { viewModel.navigateToComponentsTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue_3)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_components_test_2),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToButtonTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue_3)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_button_test_2),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToButtonEnabledTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue_3)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_button_enabled_test_2),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToSwitchEventsTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue_3)
                                )
            ) {
                Text(
                    text = stringResource(R.string.switch_events_test_switch_events_test),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToRadioIconsTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue_3)
                                )
            ) {
                Text(
                    text = stringResource(R.string.radio_icons_test_radio_custom_icons_test),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToSegmentTest() },
                modifier = Modifier.padding(bottom = 20.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue_3)
                                )
            ) {
                Text(
                    text = stringResource(R.string.segment_test_segment_control_test),
                    color = colorResource(R.color.white),
                )
            }
            Text(
                text = stringResource(R.string.test_menu_advanced_features),
                fontSize = 20.sp,
                color = colorResource(R.color.medium_gray_4),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Button(
                onClick = { viewModel.navigateToBindingTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_red)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_binding_properties_test),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToConverterTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_red)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_converter_components_test),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToCustomComponentTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_red)
                                )
            ) {
                Text(
                    text = stringResource(R.string.custom_component_test_custom_component_test),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToUserProfileTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_red)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_user_profile_test),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToIncludeTest() },
                modifier = Modifier.padding(bottom = 20.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_red)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_include_component_test),
                    color = colorResource(R.color.white),
                )
            }
            Text(
                text = stringResource(R.string.test_menu_forms_scrolling),
                fontSize = 20.sp,
                color = colorResource(R.color.medium_gray_4),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Button(
                onClick = { viewModel.navigateToFormTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.light_cyan_2)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_form_test_2),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToCollectionTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.light_cyan_2)
                                )
            ) {
                Text(
                    text = stringResource(R.string.converter_test_collection_test_2),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToKeyboardAvoidanceTest() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.light_cyan_2)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_keyboard_avoidance_test_2),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.navigateToScrollTest() },
                modifier = Modifier.padding(bottom = 20.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.light_cyan_2)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_scroll_test_2),
                    color = colorResource(R.color.white),
                )
            }
            Text(
                text = stringResource(R.string.test_menu_complete_test_suite),
                fontSize = 20.sp,
                color = colorResource(R.color.dark_red),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 15.dp)
            )
            Button(
                onClick = { viewModel.navigateToImplementedAttributesTest() },
                modifier = Modifier.padding(bottom = 20.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.dark_green_3)
                                )
            ) {
                Text(
                    text = stringResource(R.string.test_menu_all_implemented_attributes_test),
                    color = colorResource(R.color.white),
                )
            }
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}