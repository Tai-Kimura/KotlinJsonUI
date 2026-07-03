package com.example.kotlinjsonui.sample.views.relative_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.RelativeTestData
import com.example.kotlinjsonui.sample.viewmodels.RelativeTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun RelativeTestGeneratedView(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from relative_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "relative_test",
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
                android.util.Log.e("DynamicView", "Error loading relative_test: \$error")
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
            .padding(top = 20.dp, end = 20.dp, bottom = 20.dp, start = 20.dp)
            .imePadding()
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Section0(data, viewModel)
            Section1(data, viewModel)
            Section2(data, viewModel)
            Box(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(colorResource(R.color.pale_gray))
                    .padding(top = 20.dp, end = 30.dp, bottom = 20.dp, start = 30.dp)
            ) {
                val resolved_text411 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.relative_test_topleft_topmargin10_leftmargin1),
                    color = colorResource(R.color.white),
                    fontFamily = resolved_text411.family,
                    fontWeight = resolved_text411.weight,
                    fontSize = resolved_text411.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text411.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .testTag("parent_top_left")
                        .semantics { testTagsAsResourceId = true }
                        .align(Alignment.TopStart)
                        .padding(top = 10.dp)
                        .padding(start = 15.dp)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.light_red))
                        .padding(8.dp)
                )
                val resolved_text412 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.relative_test_topright_topmargin15_rightmargi),
                    color = colorResource(R.color.white),
                    fontFamily = resolved_text412.family,
                    fontWeight = resolved_text412.weight,
                    fontSize = resolved_text412.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text412.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .testTag("parent_top_right")
                        .semantics { testTagsAsResourceId = true }
                        .align(Alignment.TopEnd)
                        .padding(top = 15.dp)
                        .padding(end = 20.dp)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.light_lime))
                        .padding(8.dp)
                )
                val resolved_text413 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.relative_test_bottomleft_bottommargin5_leftma),
                    color = colorResource(R.color.white),
                    fontFamily = resolved_text413.family,
                    fontWeight = resolved_text413.weight,
                    fontSize = resolved_text413.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text413.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .testTag("parent_bottom_left")
                        .semantics { testTagsAsResourceId = true }
                        .align(Alignment.BottomStart)
                        .padding(bottom = 5.dp)
                        .padding(start = 10.dp)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.light_cyan))
                        .padding(8.dp)
                )
                val resolved_text414 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.relative_test_bottomright_bottommargin20_righ),
                    color = colorResource(R.color.white),
                    fontFamily = resolved_text414.family,
                    fontWeight = resolved_text414.weight,
                    fontSize = resolved_text414.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text414.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .testTag("parent_bottom_right")
                        .semantics { testTagsAsResourceId = true }
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 20.dp)
                        .padding(end = 25.dp)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.light_yellow))
                        .padding(8.dp)
                )
            }
            Section4(data, viewModel)
            Section5(data, viewModel)
            Section6(data, viewModel)
            Section7(data, viewModel)
            Section8(data, viewModel)
            Box(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(colorResource(R.color.pale_gray))
                    .padding(top = 10.dp, end = 10.dp, bottom = 10.dp, start = 10.dp)
            ) {
                val resolved_text418 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.relative_test_fixed_width_leftright_margins10),
                    color = colorResource(R.color.white),
                    fontFamily = resolved_text418.family,
                    fontWeight = resolved_text418.weight,
                    fontSize = resolved_text418.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text418.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .testTag("fixed_width_center")
                        .semantics { testTagsAsResourceId = true }
                        .align(BiasAlignment(0f, -1f))
                        .padding(top = 10.dp, end = 10.dp, bottom = 10.dp, start = 10.dp)
                        .width(100.dp)
                        .wrapContentHeight()
                        .background(colorResource(R.color.light_blue_2))
                        .padding(10.dp),
                    textAlign = TextAlign.Center
                )
                val resolved_text419 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.relative_test_fixed_height_topbottom),
                    color = colorResource(R.color.white),
                    fontFamily = resolved_text419.family,
                    fontWeight = resolved_text419.weight,
                    fontSize = resolved_text419.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text419.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .testTag("fixed_height_center")
                        .semantics { testTagsAsResourceId = true }
                        .align(Alignment.CenterStart)
                        .padding(top = 10.dp)
                        .padding(bottom = 10.dp)
                        .padding(start = 10.dp)
                        .wrapContentWidth()
                        .height(50.dp)
                        .background(colorResource(R.color.medium_blue_5))
                        .padding(10.dp),
                    textAlign = TextAlign.Center
                )
                val resolved_text420 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.relative_test_fixed_both),
                    color = colorResource(R.color.white),
                    fontFamily = resolved_text420.family,
                    fontWeight = resolved_text420.weight,
                    fontSize = resolved_text420.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text420.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .testTag("fixed_both_center")
                        .semantics { testTagsAsResourceId = true }
                        .align(Alignment.Center)
                        .padding(top = 15.dp, end = 15.dp, bottom = 15.dp, start = 15.dp)
                        .width(80.dp)
                        .height(40.dp)
                        .background(colorResource(R.color.light_red_3)),
                    textAlign = TextAlign.Center
                )
            }
            Section10(data, viewModel)
            Box(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(colorResource(R.color.pale_gray_3))
                    .padding(top = 20.dp, end = 30.dp, bottom = 20.dp, start = 30.dp)
            ) {
                val resolved_text422 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.relative_test_stretch_horizontal_leftmargin10),
                    color = colorResource(R.color.white),
                    fontFamily = resolved_text422.family,
                    fontWeight = resolved_text422.weight,
                    fontSize = resolved_text422.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text422.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .testTag("stretch_horizontal")
                        .semantics { testTagsAsResourceId = true }
                        .align(BiasAlignment(0f, -1f))
                        .padding(top = 10.dp)
                        .padding(start = 10.dp)
                        .padding(end = 15.dp)
                        .wrapContentHeight()
                        .background(colorResource(R.color.medium_lime))
                        .padding(10.dp),
                    textAlign = TextAlign.Center
                )
                val resolved_text423 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.relative_test_stretch_vertical_top10_bottom15),
                    color = colorResource(R.color.white),
                    fontFamily = resolved_text423.family,
                    fontWeight = resolved_text423.weight,
                    fontSize = resolved_text423.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text423.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .testTag("stretch_vertical")
                        .semantics { testTagsAsResourceId = true }
                        .align(Alignment.CenterStart)
                        .padding(top = 10.dp)
                        .padding(bottom = 15.dp)
                        .padding(start = 10.dp)
                        .wrapContentWidth()
                        .background(colorResource(R.color.medium_lime_2))
                        .padding(10.dp),
                    textAlign = TextAlign.Center
                )
                val resolved_text424 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.relative_test_stretch_both_directions_margins),
                    color = colorResource(R.color.white),
                    fontFamily = resolved_text424.family,
                    fontWeight = resolved_text424.weight,
                    fontSize = resolved_text424.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text424.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .testTag("stretch_both")
                        .semantics { testTagsAsResourceId = true }
                        .align(Alignment.Center)
                        .padding(top = 15.dp, end = 20.dp, bottom = 15.dp, start = 20.dp)
                        .background(colorResource(R.color.medium_gray_5))
                        .padding(10.dp),
                    textAlign = TextAlign.Center
                )
            }
            Section12(data, viewModel)
            Section13(data, viewModel)
            Section14(data, viewModel)
            Section15(data, viewModel)
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
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
        val resolved_button26 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button26.family,
            fontWeight = resolved_button26.weight,
            fontSize = resolved_button26.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button26.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section1(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    val resolved_text409 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = "${data.title}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text409.family,
        fontWeight = resolved_text409.weight,
        fontSize = resolved_text409.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text409.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 31.2.sp),
        modifier = Modifier
            .testTag("main_title")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 20.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section2(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    val resolved_text410 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.relative_test_1_parent_constraints_with_margi),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text410.family,
        fontWeight = resolved_text410.weight,
        fontSize = resolved_text410.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text410.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun Section4(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    val resolved_text415 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.relative_test_2_edge_alignment_self_margin_on),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text415.family,
        fontWeight = resolved_text415.weight,
        fontSize = resolved_text415.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text415.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun Section5(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .fillMaxWidth()
            .height(250.dp)
            .background(colorResource(R.color.white_24))
    ) {
        val anchor_with_margin = createRef()
        val align_top_test = createRef()
        val align_bottom_test = createRef()
        val align_left_test = createRef()
        val align_right_test = createRef()

        Text(
            modifier = Modifier.constrainAs(anchor_with_margin) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
                .padding(top = 20.dp, end = 25.dp, bottom = 20.dp, start = 25.dp)
                .width(120.dp)
                .height(60.dp)
                .background(colorResource(R.color.light_red)),
            text = "Anchor\nmargins:[20,25,20,25]",
            color = colorResource(R.color.white),
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.constrainAs(align_top_test) {
                top.linkTo(anchor_with_margin.top, margin = (-10.dp))
                start.linkTo(parent.start, margin = 10.dp)
            }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.light_lime))
                .padding(5.dp),
            text = "alignTop\ntopMargin:10",
            color = colorResource(R.color.black)
        )
        Text(
            modifier = Modifier.constrainAs(align_bottom_test) {
                bottom.linkTo(anchor_with_margin.bottom, margin = (-10.dp))
                end.linkTo(parent.end, margin = 10.dp)
            }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.light_cyan))
                .padding(5.dp),
            text = "alignBottom\nbottomMargin:10",
            color = colorResource(R.color.black)
        )
        Text(
            modifier = Modifier.constrainAs(align_left_test) {
                start.linkTo(anchor_with_margin.start, margin = (-15.dp))
                top.linkTo(parent.top, margin = 10.dp)
            }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.light_yellow))
                .padding(5.dp),
            text = "alignLeft\nleftMargin:15",
            color = colorResource(R.color.black)
        )
        Text(
            modifier = Modifier.constrainAs(align_right_test) {
                end.linkTo(anchor_with_margin.end, margin = (-15.dp))
                bottom.linkTo(parent.bottom, margin = 10.dp)
            }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.pale_pink_2))
                .padding(5.dp),
            text = "alignRight\nrightMargin:15",
            color = colorResource(R.color.black)
        )
    }
}

@Composable
private fun Section6(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    val resolved_text416 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.relative_test_3_relative_position_both_margin),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text416.family,
        fontWeight = resolved_text416.weight,
        fontSize = resolved_text416.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text416.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun Section7(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .fillMaxWidth()
            .height(300.dp)
            .background(colorResource(R.color.pale_gray_2))
    ) {
        val center_anchor = createRef()
        val above_test = createRef()
        val below_test = createRef()
        val left_of_test = createRef()
        val right_of_test = createRef()

        Text(
            modifier = Modifier.constrainAs(center_anchor) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
                .padding(top = 15.dp, end = 15.dp, bottom = 15.dp, start = 15.dp)
                .width(100.dp)
                .height(50.dp)
                .background(colorResource(R.color.light_red)),
            text = "Center\nmargins:[15,15,15,15]",
            color = colorResource(R.color.white),
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.constrainAs(above_test) {
                bottom.linkTo(center_anchor.top, margin = 10.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.light_lime))
                .padding(5.dp),
            text = "Above\nbottomMargin:10",
            color = colorResource(R.color.black)
        )
        Text(
            modifier = Modifier.constrainAs(below_test) {
                top.linkTo(center_anchor.bottom, margin = 10.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.light_cyan))
                .padding(5.dp),
            text = "Below\ntopMargin:10",
            color = colorResource(R.color.black)
        )
        Text(
            modifier = Modifier.constrainAs(left_of_test) {
                end.linkTo(center_anchor.start, margin = 10.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.light_yellow))
                .padding(5.dp),
            text = "LeftOf\nrightMargin:10",
            color = colorResource(R.color.black)
        )
        Text(
            modifier = Modifier.constrainAs(right_of_test) {
                start.linkTo(center_anchor.end, margin = 10.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.pale_pink_2))
                .padding(5.dp),
            text = "RightOf\nleftMargin:10",
            color = colorResource(R.color.black)
        )
    }
}

@Composable
private fun Section8(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    val resolved_text417 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.relative_test_4_fixed_size_with_bothside_cons),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text417.family,
        fontWeight = resolved_text417.weight,
        fontSize = resolved_text417.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text417.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun Section10(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    val resolved_text421 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.relative_test_5_dynamic_size_stretch_between_),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text421.family,
        fontWeight = resolved_text421.weight,
        fontSize = resolved_text421.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text421.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun Section12(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    val resolved_text425 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.relative_test_6_complex_chaining_with_differe),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text425.family,
        fontWeight = resolved_text425.weight,
        fontSize = resolved_text425.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text425.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun Section13(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .fillMaxWidth()
            .height(250.dp)
            .background(colorResource(R.color.white_24))
    ) {
        val chain_start = createRef()
        val chain_middle = createRef()
        val chain_end = createRef()
        val chain_above = createRef()
        val chain_below = createRef()

        val resolved_constraint_text1 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 10.sp,
            italic = false
        ))
        Text(
            modifier = Modifier.constrainAs(chain_start) {
                start.linkTo(parent.start, margin = 10.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
                .width(60.dp)
                .height(40.dp)
                .background(colorResource(R.color.light_red)),
            text = "Start\nleft:10\nright:5",
            fontFamily = resolved_constraint_text1.family,
            fontWeight = resolved_constraint_text1.weight,
            fontSize = resolved_constraint_text1.size ?: TextUnit.Unspecified,
            fontStyle = resolved_constraint_text1.style ?: FontStyle.Normal,
            color = colorResource(R.color.white),
            textAlign = TextAlign.Center
        )
        val resolved_constraint_text2 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 10.sp,
            italic = false
        ))
        Text(
            modifier = Modifier.constrainAs(chain_middle) {
                start.linkTo(chain_start.end, margin = 15.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
                .width(60.dp)
                .height(40.dp)
                .background(colorResource(R.color.light_lime)),
            text = "Middle\nleft:15\nright:20",
            fontFamily = resolved_constraint_text2.family,
            fontWeight = resolved_constraint_text2.weight,
            fontSize = resolved_constraint_text2.size ?: TextUnit.Unspecified,
            fontStyle = resolved_constraint_text2.style ?: FontStyle.Normal,
            color = colorResource(R.color.white),
            textAlign = TextAlign.Center
        )
        val resolved_constraint_text3 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 10.sp,
            italic = false
        ))
        Text(
            modifier = Modifier.constrainAs(chain_end) {
                start.linkTo(chain_middle.end, margin = 10.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
                .width(60.dp)
                .height(40.dp)
                .background(colorResource(R.color.light_cyan)),
            text = "End\nleft:10\nright:10",
            fontFamily = resolved_constraint_text3.family,
            fontWeight = resolved_constraint_text3.weight,
            fontSize = resolved_constraint_text3.size ?: TextUnit.Unspecified,
            fontStyle = resolved_constraint_text3.style ?: FontStyle.Normal,
            color = colorResource(R.color.white),
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.constrainAs(chain_above) {
                bottom.linkTo(chain_middle.top, margin = 5.dp)
                start.linkTo(chain_middle.start)
            }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.pale_pink_2))
                .padding(5.dp),
            text = "Above Middle\nbottom:5",
            color = colorResource(R.color.black)
        )
        Text(
            modifier = Modifier.constrainAs(chain_below) {
                top.linkTo(chain_start.bottom, margin = 8.dp)
                end.linkTo(chain_start.end)
            }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.light_red_4))
                .padding(5.dp),
            text = "Below Start\ntop:8",
            color = colorResource(R.color.black)
        )
    }
}

@Composable
private fun Section14(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    val resolved_text426 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.relative_test_7_mixed_anchor_references),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text426.family,
        fontWeight = resolved_text426.weight,
        fontSize = resolved_text426.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text426.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun Section15(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .fillMaxWidth()
            .height(250.dp)
            .background(colorResource(R.color.pale_gray_2))
            .padding(top = 15.dp, end = 15.dp, bottom = 15.dp, start = 15.dp)
    ) {
        val ref1 = createRef()
        val ref2 = createRef()
        val between_refs = createRef()
        val aligned_both = createRef()
        val complex_chain = createRef()

        Text(
            modifier = Modifier.constrainAs(ref1) {
                top.linkTo(parent.top, margin = 20.dp)
                start.linkTo(parent.start, margin = 20.dp)
            }
                .width(60.dp)
                .height(30.dp)
                .background(colorResource(R.color.light_red)),
            text = "Ref1",
            color = colorResource(R.color.white),
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.constrainAs(ref2) {
                top.linkTo(parent.top, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
            }
                .width(60.dp)
                .height(30.dp)
                .background(colorResource(R.color.light_lime)),
            text = "Ref2",
            color = colorResource(R.color.white),
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.constrainAs(between_refs) {
                end.linkTo(ref2.start, margin = 5.dp)
                start.linkTo(ref1.end, margin = 5.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
                .wrapContentHeight()
                .background(colorResource(R.color.light_orange_2))
                .padding(5.dp),
            text = "Between (stretch)",
            color = colorResource(R.color.black)
        )
        Text(
            modifier = Modifier.constrainAs(aligned_both) {
                top.linkTo(ref1.top, margin = (-10.dp))
                end.linkTo(ref2.end, margin = (-10.dp))
            }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.medium_green_3))
                .padding(5.dp),
            text = "AlignBoth",
            color = colorResource(R.color.black)
        )
        Text(
            modifier = Modifier.constrainAs(complex_chain) {
                top.linkTo(ref1.bottom, margin = 10.dp)
                start.linkTo(ref1.start)
            }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.light_red_5))
                .padding(5.dp),
            text = "Below Ref1",
            color = colorResource(R.color.white)
        )
    }
}
// >>> RESPONSIVE_HELPERS_END