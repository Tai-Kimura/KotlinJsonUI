package com.example.kotlinjsonui.sample.views.test_menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.TestMenuData
import com.example.kotlinjsonui.sample.viewmodels.TestMenuViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.components.keyboardAvoidance
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun TestMenuGeneratedView(
    data: TestMenuData,
    viewModel: TestMenuViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from test_menu.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
        // Check if Dynamic Mode is active
        if (DynamicModeManager.isActive()) {
            // Dynamic Mode - use SafeDynamicView for real-time updates
            SafeDynamicView(
                layoutName = "test_menu",
                modifier = modifier,
                data = data.toMap(),
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
        Section0(data, viewModel, modifier)    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("test_menu")
    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: TestMenuData,
    viewModel: TestMenuViewModel,
    modifier: Modifier
) {
    run {
        val scrollPagingState = rememberLazyListState()
        LazyColumn(
            state = scrollPagingState,
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(colorResource(R.color.white_23))
                .keyboardAvoidance(scrollPagingState, 20)
        ) {
            item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(colorResource(R.color.white_23))
                    .padding(20.dp)
            ) {
                val resolved_text1 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = FontWeight.Bold,
                    size = 28.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.test_menu_kotlinjsonui_feature_tests),
                    color = Color(android.graphics.Color.parseColor("#000000")),
                    fontFamily = resolved_text1.family,
                    fontWeight = resolved_text1.weight,
                    fontSize = resolved_text1.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text1.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 36.4.sp),
                    modifier = Modifier.padding(bottom = 20.dp),
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { data.toggleDynamicMode?.invoke() },
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .wrapContentWidth()
                        .requiredHeight(44.dp),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(android.graphics.Color.parseColor("#5856D6")),
                                            disabledContainerColor = Color(android.graphics.Color.parseColor("#5856D6")).copy(alpha = 0.5f),
                                            contentColor = Color(android.graphics.Color.parseColor("#FFFFFF")),
                                            disabledContentColor = Color(android.graphics.Color.parseColor("#FFFFFF")).copy(alpha = 0.5f)
                                        )
                ) {
                    val resolved_button1 = Configuration.Font.resolve(FontSpec(
                        family = null,
                        weight = FontWeight.Medium,
                        size = 14.sp,
                        italic = false
                    ))
                    Text(
                        text = "${data.dynamicModeStatus}",
                        fontFamily = resolved_button1.family,
                        fontWeight = resolved_button1.weight,
                        fontSize = resolved_button1.size ?: TextUnit.Unspecified,
                        fontStyle = resolved_button1.style ?: FontStyle.Normal,
                    )
                }
                val resolved_text2 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = FontWeight.Bold,
                    size = 20.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.test_menu_layout_positioning),
                    color = colorResource(R.color.medium_gray_4),
                    fontFamily = resolved_text2.family,
                    fontWeight = resolved_text2.weight,
                    fontSize = resolved_text2.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text2.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 26.0.sp),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Button(
                    onClick = { data.navigateToMarginsTest?.invoke() },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .wrapContentWidth()
                        .requiredHeight(55.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(android.graphics.Color.parseColor("#007AFF")),
                                            disabledContainerColor = Color(android.graphics.Color.parseColor("#007AFF")).copy(alpha = 0.5f),
                                            contentColor = Color(android.graphics.Color.parseColor("#FFFFFF")),
                                            disabledContentColor = Color(android.graphics.Color.parseColor("#FFFFFF")).copy(alpha = 0.5f)
                                        )
                ) {
                    val resolved_button2 = Configuration.Font.resolve(FontSpec(
                        family = null,
                        weight = FontWeight.SemiBold,
                        size = 16.sp,
                        italic = false
                    ))
                    Text(
                        text = stringResource(R.string.test_menu_margins_padding_test),
                        fontFamily = resolved_button2.family,
                        fontWeight = resolved_button2.weight,
                        fontSize = resolved_button2.size ?: TextUnit.Unspecified,
                        fontStyle = resolved_button2.style ?: FontStyle.Normal,
                    )
                }
                Button(
                    onClick = { data.navigateToAlignmentTest?.invoke() },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .wrapContentWidth()
                        .requiredHeight(55.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(android.graphics.Color.parseColor("#007AFF")),
                                            disabledContainerColor = Color(android.graphics.Color.parseColor("#007AFF")).copy(alpha = 0.5f),
                                            contentColor = Color(android.graphics.Color.parseColor("#FFFFFF")),
                                            disabledContentColor = Color(android.graphics.Color.parseColor("#FFFFFF")).copy(alpha = 0.5f)
                                        )
                ) {
                    val resolved_button3 = Configuration.Font.resolve(FontSpec(
                        family = null,
                        weight = FontWeight.SemiBold,
                        size = 16.sp,
                        italic = false
                    ))
                    Text(
                        text = stringResource(R.string.test_menu_alignment_test_2),
                        fontFamily = resolved_button3.family,
                        fontWeight = resolved_button3.weight,
                        fontSize = resolved_button3.size ?: TextUnit.Unspecified,
                        fontStyle = resolved_button3.style ?: FontStyle.Normal,
                    )
                }
                Button(
                    onClick = { data.navigateToAlignmentComboTest?.invoke() },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .wrapContentWidth()
                        .requiredHeight(55.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(android.graphics.Color.parseColor("#007AFF")),
                                            disabledContainerColor = Color(android.graphics.Color.parseColor("#007AFF")).copy(alpha = 0.5f),
                                            contentColor = Color(android.graphics.Color.parseColor("#FFFFFF")),
                                            disabledContentColor = Color(android.graphics.Color.parseColor("#FFFFFF")).copy(alpha = 0.5f)
                                        )
                ) {
                    val resolved_button4 = Configuration.Font.resolve(FontSpec(
                        family = null,
                        weight = FontWeight.SemiBold,
                        size = 16.sp,
                        italic = false
                    ))
                    Text(
                        text = stringResource(R.string.test_menu_alignment_combo_test_2),
                        fontFamily = resolved_button4.family,
                        fontWeight = resolved_button4.weight,
                        fontSize = resolved_button4.size ?: TextUnit.Unspecified,
                        fontStyle = resolved_button4.style ?: FontStyle.Normal,
                    )
                }
                Button(
                    onClick = { data.navigateToWeightTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_blue),
                                            disabledContainerColor = colorResource(R.color.medium_blue).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_weight_distribution_test))
                }
                Button(
                    onClick = { data.navigateToWeightTestWithFixed?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_blue),
                                            disabledContainerColor = colorResource(R.color.medium_blue).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_weight_fixed_size_test))
                }
                Button(
                    onClick = { data.navigateToWidthTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_blue),
                                            disabledContainerColor = colorResource(R.color.medium_blue).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_width_test))
                }
                Button(
                    onClick = { data.navigateToRelativeTest?.invoke() },
                    modifier = Modifier.padding(bottom = 20.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_blue),
                                            disabledContainerColor = colorResource(R.color.medium_blue).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_relative_positioning_test))
                }
                val resolved_text3 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = FontWeight.Bold,
                    size = 20.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.test_menu_style_appearance),
                    color = colorResource(R.color.medium_gray_4),
                    fontFamily = resolved_text3.family,
                    fontWeight = resolved_text3.weight,
                    fontSize = resolved_text3.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text3.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 26.0.sp),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Button(
                    onClick = { data.navigateToVisibilityTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_green),
                                            disabledContainerColor = colorResource(R.color.medium_green).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_visibility_opacity_test))
                }
                Button(
                    onClick = { data.navigateToDisabledTest?.invoke() },
                    modifier = Modifier.padding(bottom = 20.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_green),
                                            disabledContainerColor = colorResource(R.color.medium_green).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_disabled_states_test))
                }
                val resolved_text4 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = FontWeight.Bold,
                    size = 20.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.test_menu_text_features),
                    color = colorResource(R.color.medium_gray_4),
                    fontFamily = resolved_text4.family,
                    fontWeight = resolved_text4.weight,
                    fontSize = resolved_text4.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text4.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 26.0.sp),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Button(
                    onClick = { data.navigateToTextStylingTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_red_3),
                                            disabledContainerColor = colorResource(R.color.medium_red_3).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_text_styling_test))
                }
                Button(
                    onClick = { data.navigateToTextDecorationTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_red_3),
                                            disabledContainerColor = colorResource(R.color.medium_red_3).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_text_decoration_test))
                }
                Button(
                    onClick = { data.navigateToLineBreakTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_red_3),
                                            disabledContainerColor = colorResource(R.color.medium_red_3).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_line_break_spacing_test))
                }
                Button(
                    onClick = { data.navigateToPartialAttributesTest?.invoke() },
                    modifier = Modifier.padding(bottom = 20.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_red_3),
                                            disabledContainerColor = colorResource(R.color.medium_red_3).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_partial_attributes_test_2))
                }
                val resolved_text5 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = FontWeight.Bold,
                    size = 20.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.test_menu_input_components),
                    color = colorResource(R.color.medium_gray_4),
                    fontFamily = resolved_text5.family,
                    fontWeight = resolved_text5.weight,
                    fontSize = resolved_text5.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text5.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 26.0.sp),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Button(
                    onClick = { data.navigateToTextFieldTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.light_purple),
                                            disabledContainerColor = colorResource(R.color.light_purple).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_textfield_test))
                }
                Button(
                    onClick = { data.navigateToTextFieldEventsTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.light_purple),
                                            disabledContainerColor = colorResource(R.color.light_purple).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_textfield_events_test))
                }
                Button(
                    onClick = { data.navigateToSecureFieldTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.light_purple),
                                            disabledContainerColor = colorResource(R.color.light_purple).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_secure_field_test_2))
                }
                Button(
                    onClick = { data.navigateToTextViewHintTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.light_purple),
                                            disabledContainerColor = colorResource(R.color.light_purple).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_textview_hint_test))
                }
                Button(
                    onClick = { data.navigateToDatePickerTest?.invoke() },
                    modifier = Modifier.padding(bottom = 20.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.light_purple),
                                            disabledContainerColor = colorResource(R.color.light_purple).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_date_picker_test_2))
                }
                val resolved_text6 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = FontWeight.Bold,
                    size = 20.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.test_menu_ui_components),
                    color = colorResource(R.color.medium_gray_4),
                    fontFamily = resolved_text6.family,
                    fontWeight = resolved_text6.weight,
                    fontSize = resolved_text6.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text6.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 26.0.sp),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Button(
                    onClick = { data.navigateToComponentsTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_blue_3),
                                            disabledContainerColor = colorResource(R.color.medium_blue_3).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_components_test_2))
                }
                Button(
                    onClick = { data.navigateToButtonTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_blue_3),
                                            disabledContainerColor = colorResource(R.color.medium_blue_3).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_button_test_2))
                }
                Button(
                    onClick = { data.navigateToButtonEnabledTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_blue_3),
                                            disabledContainerColor = colorResource(R.color.medium_blue_3).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_button_enabled_test_2))
                }
                Button(
                    onClick = { data.navigateToSwitchEventsTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_blue_3),
                                            disabledContainerColor = colorResource(R.color.medium_blue_3).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_switch_events_test_2))
                }
                Button(
                    onClick = { data.navigateToRadioIconsTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_blue_3),
                                            disabledContainerColor = colorResource(R.color.medium_blue_3).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_radio_custom_icons_test))
                }
                Button(
                    onClick = { data.navigateToSegmentTest?.invoke() },
                    modifier = Modifier.padding(bottom = 20.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_blue_3),
                                            disabledContainerColor = colorResource(R.color.medium_blue_3).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_segment_control_test))
                }
                val resolved_text7 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = FontWeight.Bold,
                    size = 20.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.test_menu_advanced_features),
                    color = colorResource(R.color.medium_gray_4),
                    fontFamily = resolved_text7.family,
                    fontWeight = resolved_text7.weight,
                    fontSize = resolved_text7.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text7.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 26.0.sp),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Button(
                    onClick = { data.navigateToBindingTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_red),
                                            disabledContainerColor = colorResource(R.color.medium_red).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_binding_properties_test))
                }
                Button(
                    onClick = { data.navigateToConverterTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_red),
                                            disabledContainerColor = colorResource(R.color.medium_red).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_converter_components_test))
                }
                Button(
                    onClick = { data.navigateToCustomComponentTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_red),
                                            disabledContainerColor = colorResource(R.color.medium_red).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_custom_component_test_2))
                }
                Button(
                    onClick = { data.navigateToUserProfileTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_red),
                                            disabledContainerColor = colorResource(R.color.medium_red).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_user_profile_test))
                }
                Button(
                    onClick = { data.navigateToIncludeTest?.invoke() },
                    modifier = Modifier.padding(bottom = 20.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_red),
                                            disabledContainerColor = colorResource(R.color.medium_red).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_include_component_test))
                }
                val resolved_text8 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = FontWeight.Bold,
                    size = 20.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.test_menu_forms_scrolling),
                    color = colorResource(R.color.medium_gray_4),
                    fontFamily = resolved_text8.family,
                    fontWeight = resolved_text8.weight,
                    fontSize = resolved_text8.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text8.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 26.0.sp),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Button(
                    onClick = { data.navigateToFormTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.light_cyan_2),
                                            disabledContainerColor = colorResource(R.color.light_cyan_2).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_form_test_2))
                }
                Button(
                    onClick = { data.navigateToCollectionTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.light_cyan_2),
                                            disabledContainerColor = colorResource(R.color.light_cyan_2).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_collection_test_2))
                }
                Button(
                    onClick = { data.navigateToKeyboardAvoidanceTest?.invoke() },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.light_cyan_2),
                                            disabledContainerColor = colorResource(R.color.light_cyan_2).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_keyboard_avoidance_test_2))
                }
                Button(
                    onClick = { data.navigateToScrollTest?.invoke() },
                    modifier = Modifier.padding(bottom = 20.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.light_cyan_2),
                                            disabledContainerColor = colorResource(R.color.light_cyan_2).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_scroll_test_2))
                }
                val resolved_text9 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = FontWeight.Bold,
                    size = 20.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.test_menu_complete_test_suite),
                    color = colorResource(R.color.dark_red),
                    fontFamily = resolved_text9.family,
                    fontWeight = resolved_text9.weight,
                    fontSize = resolved_text9.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text9.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 26.0.sp),
                    modifier = Modifier.padding(bottom = 15.dp)
                )
                Button(
                    onClick = { data.navigateToImplementedAttributesTest?.invoke() },
                    modifier = Modifier.padding(bottom = 20.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.dark_green_3),
                                            disabledContainerColor = colorResource(R.color.dark_green_3).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
                ) {
                    Text(stringResource(R.string.test_menu_all_implemented_attributes_test))
                }
            }
            }
        }
    }
}
// >>> RESPONSIVE_HELPERS_END