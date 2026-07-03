package com.example.kotlinjsonui.sample.views.components_test

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun ComponentsTestGeneratedView(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from components_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
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
        LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(R.color.white))
            .imePadding()
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 20.dp, end = 20.dp, bottom = 20.dp, start = 20.dp)
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
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    Button(
        onClick = { data.toggleDynamicMode?.invoke() },
        modifier = Modifier
            .wrapContentWidth()
            .height(44.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(android.graphics.Color.parseColor("#5856D6")),
                            contentColor = colorResource(R.color.white)
                        )
    ) {
        val resolved_button2 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button2.family,
            fontWeight = resolved_button2.weight,
            fontSize = resolved_button2.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button2.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section1(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    val resolved_text13 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.components_test_new_components_test),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text13.family,
        fontWeight = resolved_text13.weight,
        fontSize = resolved_text13.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text13.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 31.2.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section2(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    val resolved_text14 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.components_test_togglecheckbox_components),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text14.family,
        fontWeight = resolved_text14.weight,
        fontSize = resolved_text14.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text14.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section3(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    Switch(
        checked = data.toggle1IsOn,
        onCheckedChange = { newValue -> viewModel.updateData(mapOf("toggle1IsOn" to newValue)) },
        modifier = Modifier
            .testTag("toggle1")
            .semantics { testTagsAsResourceId = true }
    )
}

@Composable
private fun Section4(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .testTag("checkbox1")
            .semantics { testTagsAsResourceId = true }
    ) {
        Checkbox(
            checked = data.checkbox1IsOn,
            onCheckedChange = { newValue -> viewModel.updateData(mapOf("checkbox1IsOn" to newValue)) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("I agree to terms")
    }
}

@Composable
private fun Section5(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    val resolved_text15 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.components_test_progress_slider),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text15.family,
        fontWeight = resolved_text15.weight,
        fontSize = resolved_text15.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text15.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section6(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    LinearProgressIndicator(
        modifier = Modifier
            .testTag("progress1")
            .semantics { testTagsAsResourceId = true }
    )
}

@Composable
private fun Section7(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    Slider(
        value = data.slider1Value.toFloat(),
        onValueChange = { newValue -> viewModel.updateData(mapOf("slider1Value" to newValue.toDouble())) },
        valueRange = 0.0f..1.0f,
        modifier = Modifier
            .testTag("slider1")
            .semantics { testTagsAsResourceId = true }
    )
}

@Composable
private fun Section8(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    val resolved_text16 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.components_test_selection_components),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text16.family,
        fontWeight = resolved_text16.weight,
        fontSize = resolved_text16.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text16.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section9(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    Segment(
        selectedTabIndex = data.selectedSegment1,
        containerColor = Color.Transparent,
        modifier = Modifier
            .testTag("segment1")
            .semantics { testTagsAsResourceId = true }
    ) {
        Tab(
            selected = (data.selectedSegment1 == 0),
            onClick = {
                viewModel.updateData(mapOf("selectedSegment1" to 0))
            },
            text = { Text(stringResource(R.string.components_test_list)) }
        )
        Tab(
            selected = (data.selectedSegment1 == 1),
            onClick = {
                viewModel.updateData(mapOf("selectedSegment1" to 1))
            },
            text = { Text(stringResource(R.string.components_test_grid)) }
        )
        Tab(
            selected = (data.selectedSegment1 == 2),
            onClick = {
                viewModel.updateData(mapOf("selectedSegment1" to 2))
            },
            text = { Text(stringResource(R.string.components_test_map)) }
        )
    }
}

@Composable
private fun Section10(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    Column(
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
                }
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
                }
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
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Large", color = Color.Black)
        }
    }
}

@Composable
private fun Section11(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    val resolved_text17 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.components_test_loading_indicator),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text17.family,
        fontWeight = resolved_text17.weight,
        fontSize = resolved_text17.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text17.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section12(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    CircularProgressIndicator(
    )
}

@Composable
private fun Section13(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    val resolved_text18 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.components_test_circle_image),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text18.family,
        fontWeight = resolved_text18.weight,
        fontSize = resolved_text18.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text18.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section14(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    AsyncImage(
        model = "person.circle.fill",
        contentDescription = "Profile Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
    )
}

@Composable
private fun Section15(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    val resolved_text19 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.components_test_gradient_view),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text19.family,
        fontWeight = resolved_text19.weight,
        fontSize = resolved_text19.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text19.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section16(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Brush.horizontalGradient(listOf(Color(android.graphics.Color.parseColor("#FF6B6B")), Color(android.graphics.Color.parseColor("#4ECDC4")))))
            .clip(RoundedCornerShape(10.dp))
    ) {
        val resolved_text20 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 20.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.components_test_gradient_background),
            color = colorResource(R.color.white),
            fontFamily = resolved_text20.family,
            fontWeight = resolved_text20.weight,
            fontSize = resolved_text20.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text20.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 26.0.sp),
            modifier = Modifier
        )
    }
}

@Composable
private fun Section17(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    val resolved_text21 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.components_test_blur_view),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text21.family,
        fontWeight = resolved_text21.weight,
        fontSize = resolved_text21.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text21.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section18(
    data: ComponentsTestData,
    viewModel: ComponentsTestViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .blur(10.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        val resolved_text22 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 18.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.components_test_blurred_background),
            color = colorResource(R.color.dark_gray),
            fontFamily = resolved_text22.family,
            fontWeight = resolved_text22.weight,
            fontSize = resolved_text22.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text22.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 23.4.sp),
            modifier = Modifier
        )
    }
}
// >>> RESPONSIVE_HELPERS_END