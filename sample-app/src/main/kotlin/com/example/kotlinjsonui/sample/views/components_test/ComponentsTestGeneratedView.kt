package com.example.kotlinjsonui.sample.views.components_test

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import coil3.compose.AsyncImage
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.ComponentsTestData
import com.example.kotlinjsonui.sample.viewmodels.ComponentsTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.components.Segment
import com.kotlinjsonui.components.keyboardAvoidance
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.core.jsonUITintOr
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun ComponentsTestGeneratedView(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from components_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
        // Check if Dynamic Mode is active
        if (DynamicModeManager.isActive()) {
            // Dynamic Mode - use SafeDynamicView for real-time updates
            SafeDynamicView(
                layoutName = "components_test",
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
                    android.util.Log.e("DynamicView", "Error loading components_test: \$error")
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
        ScreenMarker("components_test")
    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel,
    modifier: Modifier
) {
    run {
        val scrollPagingState = rememberLazyListState()
        LazyColumn(
            state = scrollPagingState,
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(colorResource(R.color.white))
                .keyboardAvoidance(scrollPagingState, 20)
        ) {
            item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 20.dp, end = 20.dp, bottom = 20.dp, start = 20.dp)
            ) {
                Button(
                    onClick = { data.toggleDynamicMode?.invoke() },
                    modifier = Modifier
                        .wrapContentWidth()
                        .requiredHeight(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(android.graphics.Color.parseColor("#5856D6")),
                                            disabledContainerColor = Color(android.graphics.Color.parseColor("#5856D6")).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
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
                val resolved_text1 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 24.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.components_test_new_components_test),
                    color = colorResource(R.color.dark_gray),
                    fontFamily = resolved_text1.family,
                    fontWeight = resolved_text1.weight,
                    fontSize = resolved_text1.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text1.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 31.2.sp),
                    modifier = Modifier
                )
                val resolved_text2 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 18.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.components_test_togglecheckbox_components),
                    color = colorResource(R.color.medium_gray_4),
                    fontFamily = resolved_text2.family,
                    fontWeight = resolved_text2.weight,
                    fontSize = resolved_text2.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text2.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
                    modifier = Modifier
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .testTag("toggle1")
                        .semantics { testTagsAsResourceId = true }
                ) {
                    Text(
                        text = "Enable Notifications",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = data.toggle1IsOn,
                        onCheckedChange = { newValue -> viewModel.updateData(mapOf("toggle1IsOn" to newValue)) },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = jsonUITintOr(MaterialTheme.colorScheme.primary)
                        )
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .testTag("checkbox1")
                        .semantics { testTagsAsResourceId = true }
                ) {
                    Checkbox(
                        checked = data.checkbox1IsOn,
                        onCheckedChange = { newValue -> viewModel.updateData(mapOf("checkbox1IsOn" to newValue)) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = jsonUITintOr(MaterialTheme.colorScheme.primary)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("I agree to terms")
                }
                val resolved_text3 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 18.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.components_test_progress_slider),
                    color = colorResource(R.color.medium_gray_4),
                    fontFamily = resolved_text3.family,
                    fontWeight = resolved_text3.weight,
                    fontSize = resolved_text3.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text3.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
                    modifier = Modifier
                )
                LinearProgressIndicator(
                    progress = { data.progress1Value.toFloat() },
                    modifier = Modifier
                        .testTag("progress1")
                        .semantics { testTagsAsResourceId = true },
                    color = jsonUITintOr(MaterialTheme.colorScheme.primary)
                )
                Slider(
                    value = data.slider1Value.toFloat(),
                    onValueChange = { newValue -> viewModel.updateData(mapOf("slider1Value" to newValue.toDouble())) },
                    valueRange = 0.0f..1.0f,
                    modifier = Modifier
                        .testTag("slider1")
                        .semantics { testTagsAsResourceId = true },
                    colors = SliderDefaults.colors(
                        thumbColor = jsonUITintOr(MaterialTheme.colorScheme.primary),
                        activeTrackColor = jsonUITintOr(MaterialTheme.colorScheme.primary)
                    )
                )
                val resolved_text4 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 18.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.components_test_selection_components),
                    color = colorResource(R.color.medium_gray_4),
                    fontFamily = resolved_text4.family,
                    fontWeight = resolved_text4.weight,
                    fontSize = resolved_text4.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text4.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
                    modifier = Modifier
                )
                Segment(
                    selectedTabIndex = data.selectedSegment1,
                    containerColor = Color.Transparent,
                    modifier = Modifier
                        .testTag("segment1")
                        .semantics { testTagsAsResourceId = true }
                ) {
                    Tab(
                        modifier = Modifier.testTag("segment1_tab_0"),
                        selected = (data.selectedSegment1 == 0),
                        onClick = {
                            viewModel.updateData(mapOf("selectedSegment1" to 0))
                        },
                        text = { Text(stringResource(R.string.components_test_list)) }
                    )
                    Tab(
                        modifier = Modifier.testTag("segment1_tab_1"),
                        selected = (data.selectedSegment1 == 1),
                        onClick = {
                            viewModel.updateData(mapOf("selectedSegment1" to 1))
                        },
                        text = { Text(stringResource(R.string.components_test_grid)) }
                    )
                    Tab(
                        modifier = Modifier.testTag("segment1_tab_2"),
                        selected = (data.selectedSegment1 == 2),
                        onClick = {
                            viewModel.updateData(mapOf("selectedSegment1" to 2))
                        },
                        text = { Text(stringResource(R.string.components_test_map)) }
                    )
                }
                Column(
                    modifier = Modifier
                        .testTag("radio1")
                        .semantics { testTagsAsResourceId = true }
                ) {
                    Text("Select Size", color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.updateData(mapOf("selectedRadio1" to "Small"))
                            }
                    ) {
                        RadioButton(
                            selected = data.selectedRadio1 == "Small",
                            onClick = {
                                viewModel.updateData(mapOf("selectedRadio1" to "Small"))
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = jsonUITintOr(MaterialTheme.colorScheme.primary))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Small", color = Color.Black)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.updateData(mapOf("selectedRadio1" to "Medium"))
                            }
                    ) {
                        RadioButton(
                            selected = data.selectedRadio1 == "Medium",
                            onClick = {
                                viewModel.updateData(mapOf("selectedRadio1" to "Medium"))
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = jsonUITintOr(MaterialTheme.colorScheme.primary))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Medium", color = Color.Black)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.updateData(mapOf("selectedRadio1" to "Large"))
                            }
                    ) {
                        RadioButton(
                            selected = data.selectedRadio1 == "Large",
                            onClick = {
                                viewModel.updateData(mapOf("selectedRadio1" to "Large"))
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = jsonUITintOr(MaterialTheme.colorScheme.primary))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Large", color = Color.Black)
                    }
                }
                val resolved_text5 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 18.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.components_test_loading_indicator),
                    color = colorResource(R.color.medium_gray_4),
                    fontFamily = resolved_text5.family,
                    fontWeight = resolved_text5.weight,
                    fontSize = resolved_text5.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text5.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
                    modifier = Modifier
                )
                CircularProgressIndicator(
                )
                val resolved_text6 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 18.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.components_test_circle_image),
                    color = colorResource(R.color.medium_gray_4),
                    fontFamily = resolved_text6.family,
                    fontWeight = resolved_text6.weight,
                    fontSize = resolved_text6.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text6.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
                    modifier = Modifier
                )
                AsyncImage(
                    model = "person.circle.fill",
                    contentDescription = null,
                    modifier = Modifier
                        .requiredWidth(80.dp)
                        .requiredHeight(80.dp)
                        .clip(CircleShape)
                )
                val resolved_text7 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 18.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.components_test_gradient_view),
                    color = colorResource(R.color.medium_gray_4),
                    fontFamily = resolved_text7.family,
                    fontWeight = resolved_text7.weight,
                    fontSize = resolved_text7.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text7.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
                    modifier = Modifier
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(100.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.horizontalGradient(listOf(Color(android.graphics.Color.parseColor("#FF6B6B")), Color(android.graphics.Color.parseColor("#4ECDC4")))))
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    val resolved_text8 = Configuration.Font.resolve(FontSpec(
                        family = null,
                        weight = null,
                        size = 20.sp,
                        italic = false
                    ))
                    Text(
                        text = stringResource(R.string.components_test_gradient_background),
                        color = colorResource(R.color.white),
                        fontFamily = resolved_text8.family,
                        fontWeight = resolved_text8.weight,
                        fontSize = resolved_text8.size ?: TextUnit.Unspecified,
                        fontStyle = resolved_text8.style ?: FontStyle.Normal,
                        style = LocalTextStyle.current.copy(lineHeight = 26.0.sp),
                        modifier = Modifier
                    )
                }
                val resolved_text9 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 18.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.components_test_blur_view),
                    color = colorResource(R.color.medium_gray_4),
                    fontFamily = resolved_text9.family,
                    fontWeight = resolved_text9.weight,
                    fontSize = resolved_text9.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text9.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
                    modifier = Modifier
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .blur(10.dp)
                ) {
                    val resolved_text10 = Configuration.Font.resolve(FontSpec(
                        family = null,
                        weight = null,
                        size = 18.sp,
                        italic = false
                    ))
                    Text(
                        text = stringResource(R.string.components_test_blurred_background),
                        color = colorResource(R.color.dark_gray),
                        fontFamily = resolved_text10.family,
                        fontWeight = resolved_text10.weight,
                        fontSize = resolved_text10.size ?: TextUnit.Unspecified,
                        fontStyle = resolved_text10.style ?: FontStyle.Normal,
                        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
                        modifier = Modifier
                    )
                }
            }
            }
        }
    }
}
// >>> RESPONSIVE_HELPERS_END