package com.example.kotlinjsonui.sample.views.implemented_attributes_test

import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.ImplementedAttributesTestData
import com.example.kotlinjsonui.sample.viewmodels.ImplementedAttributesTestViewModel
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.components.Segment
import com.kotlinjsonui.components.SelectBox
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.dynamic.LocalSafeAreaConfig
import com.kotlinjsonui.dynamic.SafeAreaConfig
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun ImplementedAttributesTestGeneratedView(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from implemented_attributes_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
        // Check if Dynamic Mode is active
        if (DynamicModeManager.isActive()) {
            // Dynamic Mode - use SafeDynamicView for real-time updates
            SafeDynamicView(
                layoutName = "implemented_attributes_test",
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
                    android.util.Log.e("DynamicView", "Error loading implemented_attributes_test: \$error")
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
        Section25(data, viewModel, modifier)    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("implemented_attributes_test")
    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    val resolved_text1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.implemented_attributes_test_implemented_attributes_test),
        fontFamily = resolved_text1.family,
        fontWeight = resolved_text1.weight,
        fontSize = resolved_text1.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text1.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 31.2.sp),
        modifier = Modifier.padding(bottom = 20.dp)
    )
}

@Composable
private fun Section1(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    val resolved_text2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.implemented_attributes_test_1_aligncenterverticalview_align),
        fontFamily = resolved_text2.family,
        fontWeight = resolved_text2.weight,
        fontSize = resolved_text2.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text2.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun Section2(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    ConstraintLayout(
        modifier = Modifier
            .requiredHeight(200.dp)
            .background(colorResource(R.color.white_17))
    ) {
        val target1 = createRef()
        val view_1 = createRef()
        val view_2 = createRef()

        val resolved_text3 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.implemented_attributes_test_target_view),
            color = colorResource(R.color.white),
            fontFamily = resolved_text3.family,
            fontWeight = resolved_text3.weight,
            fontSize = resolved_text3.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text3.style ?: FontStyle.Normal,
            modifier = Modifier.constrainAs(target1) {
                top.linkTo(parent.top, margin = 50.dp)
                start.linkTo(parent.start, margin = 50.dp)
            }
                .testTag("target1")
                .semantics { testTagsAsResourceId = true }
                .background(colorResource(R.color.dark_red))
                .padding(10.dp)
        )
        val resolved_text4 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.implemented_attributes_test_centered_v),
            fontFamily = resolved_text4.family,
            fontWeight = resolved_text4.weight,
            fontSize = resolved_text4.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text4.style ?: FontStyle.Normal,
            modifier = Modifier.constrainAs(view_1) {
                top.linkTo(target1.top)
                bottom.linkTo(target1.bottom)
                end.linkTo(parent.end, margin = 20.dp)
            }
                .background(colorResource(R.color.dark_green_2))
                .padding(10.dp)
        )
        val resolved_text5 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.implemented_attributes_test_centered_h),
            color = colorResource(R.color.white),
            fontFamily = resolved_text5.family,
            fontWeight = resolved_text5.weight,
            fontSize = resolved_text5.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text5.style ?: FontStyle.Normal,
            modifier = Modifier.constrainAs(view_2) {
                start.linkTo(target1.start)
                end.linkTo(target1.end)
                bottom.linkTo(parent.bottom, margin = 20.dp)
            }
                .background(colorResource(R.color.dark_blue))
                .padding(10.dp)
        )
    }
}

@Composable
private fun Section3(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    val resolved_text6 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.implemented_attributes_test_2_idealwidth_idealheight),
        fontFamily = resolved_text6.family,
        fontWeight = resolved_text6.weight,
        fontSize = resolved_text6.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text6.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun Section4(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    Box(
        modifier = Modifier
            .background(colorResource(R.color.pale_red))
            .padding(10.dp)
    ) {
        val resolved_text7 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.implemented_attributes_test_ideal_size_view_200x100),
            fontFamily = resolved_text7.family,
            fontWeight = resolved_text7.weight,
            fontSize = resolved_text7.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text7.style ?: FontStyle.Normal,
            modifier = Modifier
        )
    }
}

@Composable
private fun Section5(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    val resolved_text8 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.implemented_attributes_test_3_cliptobounds),
        fontFamily = resolved_text8.family,
        fontWeight = resolved_text8.weight,
        fontSize = resolved_text8.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text8.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun Section6(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    Box(
        modifier = Modifier
            .requiredWidth(150.dp)
            .requiredHeight(50.dp)
            .background(colorResource(R.color.pale_green))
            .clipToBounds()
    ) {
        val resolved_text9 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 16.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.implemented_attributes_test_this_text_is_very_long_and_shou),
            fontFamily = resolved_text9.family,
            fontWeight = resolved_text9.weight,
            fontSize = resolved_text9.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text9.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
            modifier = Modifier
        )
    }
}

@Composable
private fun Section7(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    val resolved_text10 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.implemented_attributes_test_4_direction_distribution),
        fontFamily = resolved_text10.family,
        fontWeight = resolved_text10.weight,
        fontSize = resolved_text10.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text10.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun Section8(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    Row(
        modifier = Modifier
            .requiredHeight(50.dp)
            .background(colorResource(R.color.pale_blue_3))
    ) {
        val resolved_text11 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = "3",
            color = colorResource(R.color.white),
            fontFamily = resolved_text11.family,
            fontWeight = resolved_text11.weight,
            fontSize = resolved_text11.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text11.style ?: FontStyle.Normal,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(colorResource(R.color.light_blue)),
            textAlign = TextAlign.Center
        )
        val resolved_text12 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = "2",
            fontFamily = resolved_text12.family,
            fontWeight = resolved_text12.weight,
            fontSize = resolved_text12.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text12.style ?: FontStyle.Normal,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(colorResource(R.color.light_green)),
            textAlign = TextAlign.Center
        )
        val resolved_text13 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = "1",
            fontFamily = resolved_text13.family,
            fontWeight = resolved_text13.weight,
            fontSize = resolved_text13.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text13.style ?: FontStyle.Normal,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(colorResource(R.color.light_red_2)),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Section9(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    val resolved_text14 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.implemented_attributes_test_5_edgeinset_label_padding),
        fontFamily = resolved_text14.family,
        fontWeight = resolved_text14.weight,
        fontSize = resolved_text14.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text14.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun Section10(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    val resolved_text15 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.implemented_attributes_test_text_with_edgeinset),
        fontFamily = resolved_text15.family,
        fontWeight = resolved_text15.weight,
        fontSize = resolved_text15.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text15.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
        modifier = Modifier
            .background(colorResource(R.color.white_18))
            .padding(20.dp)
    )
}

@Composable
private fun Section11(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    val resolved_text16 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.implemented_attributes_test_6_button_state_attributes),
        fontFamily = resolved_text16.family,
        fontWeight = resolved_text16.weight,
        fontSize = resolved_text16.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text16.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun Section12(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    val buttonInteraction = remember { MutableInteractionSource() }
    val buttonPressed by buttonInteraction.collectIsPressedAsState()
    Button(
        interactionSource = buttonInteraction,
        onClick = { },
        modifier = Modifier.padding(bottom = 10.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(10.dp),
        colors = ButtonDefaults.buttonColors(
                                containerColor = if (buttonPressed) colorResource(R.color.light) else colorResource(R.color.pale_gray_4),
                                disabledContainerColor = colorResource(R.color.pale_gray_4).copy(alpha = 0.5f),
                                contentColor = colorResource(R.color.black),
                                disabledContentColor = colorResource(R.color.black).copy(alpha = 0.5f)
                            )
    ) {
        Text(stringResource(R.string.implemented_attributes_test_tap_me_tapbackground))
    }
}

@Composable
private fun Section13(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    val buttonInteraction_2 = remember { MutableInteractionSource() }
    val buttonPressed_2 by buttonInteraction_2.collectIsPressedAsState()
    Button(
        interactionSource = buttonInteraction_2,
        onClick = { },
        modifier = Modifier.padding(bottom = 10.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(10.dp),
        colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.dark_blue),
                                disabledContainerColor = colorResource(R.color.dark_blue).copy(alpha = 0.5f),
                                contentColor = if (buttonPressed_2) Color(android.graphics.Color.parseColor("#ff0000")) else colorResource(R.color.white),
                                disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                            )
    ) {
        Text(stringResource(R.string.implemented_attributes_test_highlight_me))
    }
}

@Composable
private fun Section14(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    Button(
        onClick = { },
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(10.dp),
        colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.dark_blue),
                                disabledContainerColor = colorResource(R.color.white_17),
                                contentColor = colorResource(R.color.white),
                                disabledContentColor = Color(android.graphics.Color.parseColor("#999999"))
                            ),
        enabled = false
    ) {
        Text(stringResource(R.string.implemented_attributes_test_disabled_button))
    }
}

@Composable
private fun Section15(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    val resolved_text17 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.implemented_attributes_test_7_textfield_events),
        fontFamily = resolved_text17.family,
        fontWeight = resolved_text17.weight,
        fontSize = resolved_text17.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text17.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun Section16(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    val textFieldState_textField1 = rememberTextFieldState(initialText = data.textFieldValue)
    LaunchedEffect(data.textFieldValue) { if (textFieldState_textField1.text.toString() != data.textFieldValue) textFieldState_textField1.edit { replace(0, length, data.textFieldValue) } }
    LaunchedEffect(textFieldState_textField1.text) { val newValue = textFieldState_textField1.text.toString(); if (newValue != data.textFieldValue) viewModel.updateData(mapOf("textFieldValue" to newValue)) }
    val resolved_textfield1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    val focusRequester_textField1 = remember { FocusRequester() }
    val keyboardController_textField1 = LocalSoftwareKeyboardController.current
    LaunchedEffect(data.textField1IsFocused) { if (data.textField1IsFocused) { focusRequester_textField1.requestFocus(); keyboardController_textField1?.show() } }
    CustomTextFieldWithMargins(
        state = textFieldState_textField1,
        boxModifier = Modifier
            .testTag("textField1")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 10.dp),
        textFieldModifier = Modifier
            .onFocusChanged { if (it.isFocused != data.textField1IsFocused) viewModel.updateData(mapOf("textField1IsFocused" to it.isFocused)) }
            .focusRequester(focusRequester_textField1),
        placeholder = { Text(
                                text = stringResource(R.string.implemented_attributes_test_type_something),
                                color = Configuration.TextField.defaultPlaceholderColor
                            ) },
        isOutlined = true,
        textStyle = TextStyle(fontFamily = (resolved_textfield1.family ?: LocalTextStyle.current.fontFamily), fontWeight = (resolved_textfield1.weight ?: LocalTextStyle.current.fontWeight), fontSize = (resolved_textfield1.size ?: LocalTextStyle.current.fontSize), fontStyle = (resolved_textfield1.style ?: LocalTextStyle.current.fontStyle), color = Configuration.TextField.defaultTextColor),
        onFocus = { data.handleFocus?.invoke() },
        onBlur = { data.handleBlur?.invoke() },
        onBeginEditing = { data.handleBeginEditing?.invoke() },
        onEndEditing = { data.handleEndEditing?.invoke() }
    )
}

@Composable
private fun Section17(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    val resolved_text18 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.implemented_attributes_test_8_radio_custom_icons),
        fontFamily = resolved_text18.family,
        fontWeight = resolved_text18.weight,
        fontSize = resolved_text18.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text18.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun Section18(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    Column(
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(bottom = 5.dp)
        ) {
            RadioButton(
                selected = data.selectedRadiogroup == "radio1",
                onClick = { viewModel.updateData(mapOf("selectedRadiogroup" to "radio1")) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Option 1", color = Color.Black)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(bottom = 5.dp)
        ) {
            RadioButton(
                selected = data.selectedRadiogroup == "radio2",
                onClick = { viewModel.updateData(mapOf("selectedRadiogroup" to "radio2")) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Option 2", color = Color.Black)
        }
    }
}

@Composable
private fun Section19(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    val resolved_text19 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.implemented_attributes_test_9_segment_control),
        fontFamily = resolved_text19.family,
        fontWeight = resolved_text19.weight,
        fontSize = resolved_text19.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text19.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun Section20(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    CompositionLocalProvider(LocalContentColor provides (colorResource(R.color.dark_blue))) {
        Segment(
            selectedTabIndex = data.selectedSegment,
            containerColor = Color.Transparent,
            indicatorColor = colorResource(R.color.dark_blue),
            modifier = Modifier
                .testTag("segment")
                .semantics { testTagsAsResourceId = true }
                .padding(bottom = 10.dp)
        ) {
            Tab(
                selected = (data.selectedSegment == 0),
                onClick = {
                    viewModel.updateData(mapOf("selectedSegment" to 0))
                },
                text = { Text(stringResource(R.string.implemented_attributes_test_first)) }
            )
            Tab(
                selected = (data.selectedSegment == 1),
                onClick = {
                    viewModel.updateData(mapOf("selectedSegment" to 1))
                },
                text = { Text(stringResource(R.string.implemented_attributes_test_second)) }
            )
            Tab(
                selected = (data.selectedSegment == 2),
                onClick = {
                    viewModel.updateData(mapOf("selectedSegment" to 2))
                },
                text = { Text(stringResource(R.string.implemented_attributes_test_third)) }
            )
        }
    }
}

@Composable
private fun Section21(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    val resolved_text20 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.implemented_attributes_test_10_selectbox_attributes),
        fontFamily = resolved_text20.family,
        fontWeight = resolved_text20.weight,
        fontSize = resolved_text20.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text20.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun Section22(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    SelectBox(
        value = "",
        onValueChange = { },
        options = listOf("Option A", "Option B", "Option C"),
        placeholder = stringResource(R.string.implemented_attributes_test_choose_an_option),
        backgroundColor = colorResource(R.color.white),
        textColor = colorResource(R.color.dark_blue),
        fontSize = 16,
        cancelButtonBackgroundColor = colorResource(R.color.white_19),
        cancelButtonTextColor = colorResource(R.color.dark_red),
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 15.dp)
    )
}

@Composable
private fun Section23(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    val resolved_text21 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.implemented_attributes_test_11_web_component),
        fontFamily = resolved_text21.family,
        fontWeight = resolved_text21.weight,
        fontSize = resolved_text21.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text21.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun Section24(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                loadUrl("https://www.example.com")
                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()
            }
        },
        update = { webView ->
        },
        modifier = Modifier
            .padding(bottom = 20.dp)
            .requiredHeight(200.dp)
    )
}

@Composable
private fun Section25(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel,
    modifier: Modifier
) {
    val safeAreaConfig = LocalSafeAreaConfig.current
    val edges = mutableListOf("all").apply {
        if (safeAreaConfig.ignoreBottom) {
            remove("bottom")
            if (contains("all")) { remove("all"); addAll(listOf("top", "start", "end")) }
        }
        if (safeAreaConfig.ignoreTop) {
            remove("top")
            if (contains("all")) { remove("all"); addAll(listOf("bottom", "start", "end")) }
        }
    }.distinct()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .then(if (edges.contains("all")) Modifier.systemBarsPadding() else Modifier)
            .then(if (!edges.contains("all") && edges.contains("top")) Modifier.statusBarsPadding() else Modifier)
            .then(if (!edges.contains("all") && edges.contains("bottom")) Modifier.navigationBarsPadding() else Modifier)
            .imePadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(colorResource(R.color.white_17))
                .imePadding()
        ) {
            item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp)
            ) {
                Section0(data, viewModel)
                Section1(data, viewModel)
                Section2(data, viewModel)
                Section3(data, viewModel)
                Section4(data, viewModel)
                Section5(data, viewModel)
                Section6(data, viewModel)
                Section7(data, viewModel)
                Section8(data, viewModel)
                Section9(data, viewModel)
                Section10(data, viewModel)
                Section11(data, viewModel)
                Section12(data, viewModel)
                Section13(data, viewModel)
                Section14(data, viewModel)
                Section15(data, viewModel)
                Section16(data, viewModel)
                Section17(data, viewModel)
                Section18(data, viewModel)
                Section19(data, viewModel)
                Section20(data, viewModel)
                Section21(data, viewModel)
                Section22(data, viewModel)
                Section23(data, viewModel)
                Section24(data, viewModel)
            }
            }
        }
    }
}
// >>> RESPONSIVE_HELPERS_END