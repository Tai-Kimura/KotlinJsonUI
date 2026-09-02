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
import androidx.compose.material3.LocalTextStyle
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
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun RelativeTestGeneratedView(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from relative_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
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
            }
            }
        }    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("relative_test")
    }
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
}

@Composable
private fun Section1(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    val resolved_text1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = "${data.title}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text1.family,
        fontWeight = resolved_text1.weight,
        fontSize = resolved_text1.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text1.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 31.2.sp),
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
    val resolved_text2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.relative_test_1_parent_constraints_with_margi),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text2.family,
        fontWeight = resolved_text2.weight,
        fontSize = resolved_text2.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text2.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun Section3(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    Box(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .fillMaxWidth()
            .requiredHeight(200.dp)
            .background(colorResource(R.color.pale_gray))
            .padding(top = 20.dp, end = 30.dp, bottom = 20.dp, start = 30.dp)
    ) {
        val resolved_text3 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_topleft_topmargin10_leftmargin1),
            color = colorResource(R.color.white),
            fontFamily = resolved_text3.family,
            fontWeight = resolved_text3.weight,
            fontSize = resolved_text3.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text3.style ?: FontStyle.Normal,
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
        val resolved_text4 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_topright_topmargin15_rightmargi),
            color = colorResource(R.color.white),
            fontFamily = resolved_text4.family,
            fontWeight = resolved_text4.weight,
            fontSize = resolved_text4.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text4.style ?: FontStyle.Normal,
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
        val resolved_text5 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_bottomleft_bottommargin5_leftma),
            color = colorResource(R.color.white),
            fontFamily = resolved_text5.family,
            fontWeight = resolved_text5.weight,
            fontSize = resolved_text5.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text5.style ?: FontStyle.Normal,
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
        val resolved_text6 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_bottomright_bottommargin20_righ),
            color = colorResource(R.color.white),
            fontFamily = resolved_text6.family,
            fontWeight = resolved_text6.weight,
            fontSize = resolved_text6.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text6.style ?: FontStyle.Normal,
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
}

@Composable
private fun Section4(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    val resolved_text7 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.relative_test_2_edge_alignment_self_margin_on),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text7.family,
        fontWeight = resolved_text7.weight,
        fontSize = resolved_text7.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text7.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
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
            .requiredHeight(250.dp)
            .background(colorResource(R.color.white_24))
    ) {
        val anchor_with_margin = createRef()
        val align_top_test = createRef()
        val align_bottom_test = createRef()
        val align_left_test = createRef()
        val align_right_test = createRef()

        val resolved_text8 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_anchor_margins20252025),
            color = colorResource(R.color.white),
            fontFamily = resolved_text8.family,
            fontWeight = resolved_text8.weight,
            fontSize = resolved_text8.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text8.style ?: FontStyle.Normal,
            modifier = Modifier.constrainAs(anchor_with_margin) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
                .testTag("anchor_with_margin")
                .semantics { testTagsAsResourceId = true }
                .padding(top = 20.dp, end = 25.dp, bottom = 20.dp, start = 25.dp)
                .requiredWidth(120.dp)
                .requiredHeight(60.dp)
                .background(colorResource(R.color.light_red)),
            textAlign = TextAlign.Center
        )
        val resolved_text9 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_aligntop_topmargin10),
            color = colorResource(R.color.black),
            fontFamily = resolved_text9.family,
            fontWeight = resolved_text9.weight,
            fontSize = resolved_text9.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text9.style ?: FontStyle.Normal,
            modifier = Modifier.constrainAs(align_top_test) {
                top.linkTo(anchor_with_margin.top, margin = (-10.dp))
                start.linkTo(parent.start, margin = 10.dp)
            }
                .testTag("align_top_test")
                .semantics { testTagsAsResourceId = true }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.light_lime))
                .padding(5.dp)
        )
        val resolved_text10 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_alignbottom_bottommargin10),
            color = colorResource(R.color.black),
            fontFamily = resolved_text10.family,
            fontWeight = resolved_text10.weight,
            fontSize = resolved_text10.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text10.style ?: FontStyle.Normal,
            modifier = Modifier.constrainAs(align_bottom_test) {
                bottom.linkTo(anchor_with_margin.bottom, margin = (-10.dp))
                end.linkTo(parent.end, margin = 10.dp)
            }
                .testTag("align_bottom_test")
                .semantics { testTagsAsResourceId = true }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.light_cyan))
                .padding(5.dp)
        )
        val resolved_text11 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_alignleft_leftmargin15),
            color = colorResource(R.color.black),
            fontFamily = resolved_text11.family,
            fontWeight = resolved_text11.weight,
            fontSize = resolved_text11.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text11.style ?: FontStyle.Normal,
            modifier = Modifier.constrainAs(align_left_test) {
                start.linkTo(anchor_with_margin.start, margin = (-15.dp))
                top.linkTo(parent.top, margin = 10.dp)
            }
                .testTag("align_left_test")
                .semantics { testTagsAsResourceId = true }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.light_yellow))
                .padding(5.dp)
        )
        val resolved_text12 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_alignright_rightmargin15),
            color = colorResource(R.color.black),
            fontFamily = resolved_text12.family,
            fontWeight = resolved_text12.weight,
            fontSize = resolved_text12.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text12.style ?: FontStyle.Normal,
            modifier = Modifier.constrainAs(align_right_test) {
                end.linkTo(anchor_with_margin.end, margin = (-15.dp))
                bottom.linkTo(parent.bottom, margin = 10.dp)
            }
                .testTag("align_right_test")
                .semantics { testTagsAsResourceId = true }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.pale_pink_2))
                .padding(5.dp)
        )
    }
}

@Composable
private fun Section6(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    val resolved_text13 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.relative_test_3_relative_position_both_margin),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text13.family,
        fontWeight = resolved_text13.weight,
        fontSize = resolved_text13.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text13.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
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
            .requiredHeight(300.dp)
            .background(colorResource(R.color.pale_gray_2))
    ) {
        val center_anchor = createRef()
        val above_test = createRef()
        val below_test = createRef()
        val left_of_test = createRef()
        val right_of_test = createRef()

        val resolved_text14 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_center_margins15151515),
            color = colorResource(R.color.white),
            fontFamily = resolved_text14.family,
            fontWeight = resolved_text14.weight,
            fontSize = resolved_text14.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text14.style ?: FontStyle.Normal,
            modifier = Modifier.constrainAs(center_anchor) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
                .testTag("center_anchor")
                .semantics { testTagsAsResourceId = true }
                .padding(top = 15.dp, end = 15.dp, bottom = 15.dp, start = 15.dp)
                .requiredWidth(100.dp)
                .requiredHeight(50.dp)
                .background(colorResource(R.color.light_red)),
            textAlign = TextAlign.Center
        )
        val resolved_text15 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_above_bottommargin10),
            color = colorResource(R.color.black),
            fontFamily = resolved_text15.family,
            fontWeight = resolved_text15.weight,
            fontSize = resolved_text15.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text15.style ?: FontStyle.Normal,
            modifier = Modifier.constrainAs(above_test) {
                bottom.linkTo(center_anchor.top, margin = 10.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
                .testTag("above_test")
                .semantics { testTagsAsResourceId = true }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.light_lime))
                .padding(5.dp),
            textAlign = TextAlign.Center
        )
        val resolved_text16 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_below_topmargin10),
            color = colorResource(R.color.black),
            fontFamily = resolved_text16.family,
            fontWeight = resolved_text16.weight,
            fontSize = resolved_text16.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text16.style ?: FontStyle.Normal,
            modifier = Modifier.constrainAs(below_test) {
                top.linkTo(center_anchor.bottom, margin = 10.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
                .testTag("below_test")
                .semantics { testTagsAsResourceId = true }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.light_cyan))
                .padding(5.dp),
            textAlign = TextAlign.Center
        )
        val resolved_text17 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_leftof_rightmargin10),
            color = colorResource(R.color.black),
            fontFamily = resolved_text17.family,
            fontWeight = resolved_text17.weight,
            fontSize = resolved_text17.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text17.style ?: FontStyle.Normal,
            modifier = Modifier.constrainAs(left_of_test) {
                end.linkTo(center_anchor.start, margin = 10.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
                .testTag("left_of_test")
                .semantics { testTagsAsResourceId = true }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.light_yellow))
                .padding(5.dp)
        )
        val resolved_text18 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_rightof_leftmargin10),
            color = colorResource(R.color.black),
            fontFamily = resolved_text18.family,
            fontWeight = resolved_text18.weight,
            fontSize = resolved_text18.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text18.style ?: FontStyle.Normal,
            modifier = Modifier.constrainAs(right_of_test) {
                start.linkTo(center_anchor.end, margin = 10.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
                .testTag("right_of_test")
                .semantics { testTagsAsResourceId = true }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.pale_pink_2))
                .padding(5.dp)
        )
    }
}

@Composable
private fun Section8(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    val resolved_text19 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.relative_test_4_fixed_size_with_bothside_cons),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text19.family,
        fontWeight = resolved_text19.weight,
        fontSize = resolved_text19.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text19.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun Section9(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    Box(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .fillMaxWidth()
            .requiredHeight(200.dp)
            .background(colorResource(R.color.pale_gray))
            .padding(top = 10.dp, end = 10.dp, bottom = 10.dp, start = 10.dp)
    ) {
        val resolved_text20 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_fixed_width_leftright_margins10),
            color = colorResource(R.color.white),
            fontFamily = resolved_text20.family,
            fontWeight = resolved_text20.weight,
            fontSize = resolved_text20.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text20.style ?: FontStyle.Normal,
            modifier = Modifier
                .testTag("fixed_width_center")
                .semantics { testTagsAsResourceId = true }
                .align(BiasAlignment(0f, -1f))
                .padding(top = 10.dp, end = 10.dp, bottom = 10.dp, start = 10.dp)
                .requiredWidth(100.dp)
                .wrapContentHeight()
                .background(colorResource(R.color.light_blue_2))
                .padding(10.dp),
            textAlign = TextAlign.Center
        )
        val resolved_text21 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_fixed_height_topbottom),
            color = colorResource(R.color.white),
            fontFamily = resolved_text21.family,
            fontWeight = resolved_text21.weight,
            fontSize = resolved_text21.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text21.style ?: FontStyle.Normal,
            modifier = Modifier
                .testTag("fixed_height_center")
                .semantics { testTagsAsResourceId = true }
                .align(Alignment.CenterStart)
                .padding(top = 10.dp)
                .padding(bottom = 10.dp)
                .padding(start = 10.dp)
                .wrapContentWidth()
                .requiredHeight(50.dp)
                .background(colorResource(R.color.medium_blue_5))
                .padding(10.dp),
            textAlign = TextAlign.Center
        )
        val resolved_text22 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_fixed_both),
            color = colorResource(R.color.white),
            fontFamily = resolved_text22.family,
            fontWeight = resolved_text22.weight,
            fontSize = resolved_text22.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text22.style ?: FontStyle.Normal,
            modifier = Modifier
                .testTag("fixed_both_center")
                .semantics { testTagsAsResourceId = true }
                .align(Alignment.Center)
                .padding(top = 15.dp, end = 15.dp, bottom = 15.dp, start = 15.dp)
                .requiredWidth(80.dp)
                .requiredHeight(40.dp)
                .background(colorResource(R.color.light_red_3)),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Section10(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    val resolved_text23 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.relative_test_5_dynamic_size_stretch_between_),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text23.family,
        fontWeight = resolved_text23.weight,
        fontSize = resolved_text23.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text23.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun Section11(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    Box(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .fillMaxWidth()
            .requiredHeight(250.dp)
            .background(colorResource(R.color.pale_gray_3))
            .padding(top = 20.dp, end = 30.dp, bottom = 20.dp, start = 30.dp)
    ) {
        val resolved_text24 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_stretch_horizontal_leftmargin10),
            color = colorResource(R.color.white),
            fontFamily = resolved_text24.family,
            fontWeight = resolved_text24.weight,
            fontSize = resolved_text24.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text24.style ?: FontStyle.Normal,
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
        val resolved_text25 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_stretch_vertical_top10_bottom15),
            color = colorResource(R.color.white),
            fontFamily = resolved_text25.family,
            fontWeight = resolved_text25.weight,
            fontSize = resolved_text25.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text25.style ?: FontStyle.Normal,
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
        val resolved_text26 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_stretch_both_directions_margins),
            color = colorResource(R.color.white),
            fontFamily = resolved_text26.family,
            fontWeight = resolved_text26.weight,
            fontSize = resolved_text26.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text26.style ?: FontStyle.Normal,
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
}

@Composable
private fun Section12(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    val resolved_text27 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.relative_test_6_complex_chaining_with_differe),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text27.family,
        fontWeight = resolved_text27.weight,
        fontSize = resolved_text27.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text27.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
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
            .requiredHeight(250.dp)
            .background(colorResource(R.color.white_24))
    ) {
        val chain_start = createRef()
        val chain_middle = createRef()
        val chain_end = createRef()
        val chain_above = createRef()
        val chain_below = createRef()

        val resolved_text28 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 10.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_start_left10_right5),
            color = colorResource(R.color.white),
            fontFamily = resolved_text28.family,
            fontWeight = resolved_text28.weight,
            fontSize = resolved_text28.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text28.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 13.0.sp),
            modifier = Modifier.constrainAs(chain_start) {
                start.linkTo(parent.start, margin = 10.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
                .testTag("chain_start")
                .semantics { testTagsAsResourceId = true }
                .requiredWidth(60.dp)
                .requiredHeight(40.dp)
                .background(colorResource(R.color.light_red)),
            textAlign = TextAlign.Center
        )
        val resolved_text29 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 10.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_middle_left15_right20),
            color = colorResource(R.color.white),
            fontFamily = resolved_text29.family,
            fontWeight = resolved_text29.weight,
            fontSize = resolved_text29.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text29.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 13.0.sp),
            modifier = Modifier.constrainAs(chain_middle) {
                start.linkTo(chain_start.end, margin = 15.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
                .testTag("chain_middle")
                .semantics { testTagsAsResourceId = true }
                .requiredWidth(60.dp)
                .requiredHeight(40.dp)
                .background(colorResource(R.color.light_lime)),
            textAlign = TextAlign.Center
        )
        val resolved_text30 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 10.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_end_left10_right10),
            color = colorResource(R.color.white),
            fontFamily = resolved_text30.family,
            fontWeight = resolved_text30.weight,
            fontSize = resolved_text30.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text30.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 13.0.sp),
            modifier = Modifier.constrainAs(chain_end) {
                start.linkTo(chain_middle.end, margin = 10.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
                .testTag("chain_end")
                .semantics { testTagsAsResourceId = true }
                .requiredWidth(60.dp)
                .requiredHeight(40.dp)
                .background(colorResource(R.color.light_cyan)),
            textAlign = TextAlign.Center
        )
        val resolved_text31 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_above_middle_bottom5),
            color = colorResource(R.color.black),
            fontFamily = resolved_text31.family,
            fontWeight = resolved_text31.weight,
            fontSize = resolved_text31.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text31.style ?: FontStyle.Normal,
            modifier = Modifier.constrainAs(chain_above) {
                bottom.linkTo(chain_middle.top, margin = 5.dp)
                start.linkTo(chain_middle.start)
            }
                .testTag("chain_above")
                .semantics { testTagsAsResourceId = true }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.pale_pink_2))
                .padding(5.dp)
        )
        val resolved_text32 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_below_start_top8),
            color = colorResource(R.color.black),
            fontFamily = resolved_text32.family,
            fontWeight = resolved_text32.weight,
            fontSize = resolved_text32.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text32.style ?: FontStyle.Normal,
            modifier = Modifier.constrainAs(chain_below) {
                top.linkTo(chain_start.bottom, margin = 8.dp)
                end.linkTo(chain_start.end)
            }
                .testTag("chain_below")
                .semantics { testTagsAsResourceId = true }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.light_red_4))
                .padding(5.dp)
        )
    }
}

@Composable
private fun Section14(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    val resolved_text33 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.relative_test_7_mixed_anchor_references),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text33.family,
        fontWeight = resolved_text33.weight,
        fontSize = resolved_text33.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text33.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
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
            .requiredHeight(250.dp)
            .background(colorResource(R.color.pale_gray_2))
            .padding(top = 15.dp, end = 15.dp, bottom = 15.dp, start = 15.dp)
    ) {
        val ref1 = createRef()
        val ref2 = createRef()
        val between_refs = createRef()
        val aligned_both = createRef()
        val complex_chain = createRef()

        val resolved_text34 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_ref1),
            color = colorResource(R.color.white),
            fontFamily = resolved_text34.family,
            fontWeight = resolved_text34.weight,
            fontSize = resolved_text34.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text34.style ?: FontStyle.Normal,
            modifier = Modifier.constrainAs(ref1) {
                top.linkTo(parent.top, margin = 20.dp)
                start.linkTo(parent.start, margin = 20.dp)
            }
                .testTag("ref1")
                .semantics { testTagsAsResourceId = true }
                .requiredWidth(60.dp)
                .requiredHeight(30.dp)
                .background(colorResource(R.color.light_red)),
            textAlign = TextAlign.Center
        )
        val resolved_text35 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_ref2),
            color = colorResource(R.color.white),
            fontFamily = resolved_text35.family,
            fontWeight = resolved_text35.weight,
            fontSize = resolved_text35.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text35.style ?: FontStyle.Normal,
            modifier = Modifier.constrainAs(ref2) {
                top.linkTo(parent.top, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
            }
                .testTag("ref2")
                .semantics { testTagsAsResourceId = true }
                .requiredWidth(60.dp)
                .requiredHeight(30.dp)
                .background(colorResource(R.color.light_lime)),
            textAlign = TextAlign.Center
        )
        val resolved_text36 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_between_stretch),
            color = colorResource(R.color.black),
            fontFamily = resolved_text36.family,
            fontWeight = resolved_text36.weight,
            fontSize = resolved_text36.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text36.style ?: FontStyle.Normal,
            modifier = Modifier.constrainAs(between_refs) {
                end.linkTo(ref2.start, margin = 5.dp)
                start.linkTo(ref1.end, margin = 5.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
                .testTag("between_refs")
                .semantics { testTagsAsResourceId = true }
                .wrapContentHeight()
                .background(colorResource(R.color.light_orange_2))
                .padding(5.dp)
        )
        val resolved_text37 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_alignboth),
            color = colorResource(R.color.black),
            fontFamily = resolved_text37.family,
            fontWeight = resolved_text37.weight,
            fontSize = resolved_text37.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text37.style ?: FontStyle.Normal,
            modifier = Modifier.constrainAs(aligned_both) {
                top.linkTo(ref1.top, margin = (-10.dp))
                end.linkTo(ref2.end, margin = (-10.dp))
            }
                .testTag("aligned_both")
                .semantics { testTagsAsResourceId = true }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.medium_green_3))
                .padding(5.dp)
        )
        val resolved_text38 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.relative_test_below_ref1),
            color = colorResource(R.color.white),
            fontFamily = resolved_text38.family,
            fontWeight = resolved_text38.weight,
            fontSize = resolved_text38.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text38.style ?: FontStyle.Normal,
            modifier = Modifier.constrainAs(complex_chain) {
                top.linkTo(ref1.bottom, margin = 10.dp)
                start.linkTo(ref1.start)
            }
                .testTag("complex_chain")
                .semantics { testTagsAsResourceId = true }
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.light_red_5))
                .padding(5.dp)
        )
    }
}
// >>> RESPONSIVE_HELPERS_END