package com.example.kotlinjsonui.sample.views.relative_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.RelativeTestData
import com.example.kotlinjsonui.sample.viewmodels.RelativeTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.BiasAlignment
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.ui.res.stringResource
import com.example.kotlinjsonui.sample.R
import androidx.compose.ui.res.colorResource

@Composable
fun RelativeTestGeneratedView(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    // Generated Compose code from relative_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "relative_test",
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
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 20.dp, end = 20.dp, bottom = 20.dp, start = 20.dp)
            .background(colorResource(R.color.white))
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Box(
            ) {
            }
            Button(
                onClick = { viewModel.toggleDynamicMode() },
                modifier = Modifier
                    .wrapContentWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
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
                text = "${data.title}",
                fontSize = 24.sp,
                color = colorResource(R.color.black),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(bottom = 20.dp)
            )
            Text(
                text = stringResource(R.string.relative_test_1_parent_constraints_with_margi),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 20.dp)
                    .background(colorResource(R.color.pale_gray))
                    .padding(top = 20.dp, end = 30.dp, bottom = 20.dp, start = 30.dp)
            ) {
                Text(
                    text = stringResource(R.string.relative_test_topleft_topmargin10_leftmargin1),
                    color = colorResource(R.color.white),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .padding(top = 10.dp)
                        .padding(start = 15.dp)
                        .background(colorResource(R.color.light_red))
                        .padding(8.dp)
                )
                Text(
                    text = stringResource(R.string.relative_test_topright_topmargin15_rightmargi),
                    color = colorResource(R.color.white),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .padding(top = 15.dp)
                        .padding(end = 20.dp)
                        .background(colorResource(R.color.light_lime))
                        .padding(8.dp)
                )
                Text(
                    text = stringResource(R.string.relative_test_bottomleft_bottommargin5_leftma),
                    color = colorResource(R.color.white),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .padding(bottom = 5.dp)
                        .padding(start = 10.dp)
                        .background(colorResource(R.color.light_cyan))
                        .padding(8.dp)
                )
                Text(
                    text = stringResource(R.string.relative_test_bottomright_bottommargin20_righ),
                    color = colorResource(R.color.white),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .padding(bottom = 20.dp)
                        .padding(end = 25.dp)
                        .background(colorResource(R.color.light_yellow))
                        .padding(8.dp)
                )
            }
            Text(
                text = stringResource(R.string.relative_test_2_edge_alignment_self_margin_on),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(bottom = 20.dp)
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
                        top.linkTo(anchor_with_margin.top, margin = (-10).dp)
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
                        bottom.linkTo(anchor_with_margin.bottom, margin = (-10).dp)
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
                        start.linkTo(anchor_with_margin.start, margin = (-15).dp)
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
                        end.linkTo(anchor_with_margin.end, margin = (-15).dp)
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
            Text(
                text = stringResource(R.string.relative_test_3_relative_position_both_margin),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(bottom = 20.dp)
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
            Text(
                text = stringResource(R.string.relative_test_4_fixed_size_with_bothside_cons),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 20.dp)
                    .background(colorResource(R.color.pale_gray))
                    .padding(top = 10.dp, end = 10.dp, bottom = 10.dp, start = 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.relative_test_fixed_width_leftright_margins10),
                    color = colorResource(R.color.white),
                    modifier = Modifier
                        .align(BiasAlignment(0f, -1f))
                        .width(100.dp)
                        .wrapContentHeight()
                        .padding(top = 10.dp, end = 10.dp, bottom = 10.dp, start = 10.dp)
                        .background(colorResource(R.color.light_blue_2))
                        .padding(10.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.relative_test_fixed_height_topbottom),
                    color = colorResource(R.color.white),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .wrapContentWidth()
                        .height(50.dp)
                        .padding(top = 10.dp)
                        .padding(bottom = 10.dp)
                        .padding(start = 10.dp)
                        .background(colorResource(R.color.medium_blue_5))
                        .padding(10.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.relative_test_fixed_both),
                    color = colorResource(R.color.white),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(80.dp)
                        .height(40.dp)
                        .padding(top = 15.dp, end = 15.dp, bottom = 15.dp, start = 15.dp)
                        .background(colorResource(R.color.light_red_3)),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = stringResource(R.string.relative_test_5_dynamic_size_stretch_between_),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(bottom = 20.dp)
                    .background(colorResource(R.color.pale_gray_3))
                    .padding(top = 20.dp, end = 30.dp, bottom = 20.dp, start = 30.dp)
            ) {
                Text(
                    text = stringResource(R.string.relative_test_stretch_horizontal_leftmargin10),
                    color = colorResource(R.color.white),
                    modifier = Modifier
                        .align(BiasAlignment(0f, -1f))
                        .wrapContentHeight()
                        .padding(top = 10.dp)
                        .padding(start = 10.dp)
                        .padding(end = 15.dp)
                        .background(colorResource(R.color.medium_lime))
                        .padding(10.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.relative_test_stretch_vertical_top10_bottom15),
                    color = colorResource(R.color.white),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .wrapContentWidth()
                        .padding(top = 10.dp)
                        .padding(bottom = 15.dp)
                        .padding(start = 10.dp)
                        .background(colorResource(R.color.medium_lime_2))
                        .padding(10.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.relative_test_stretch_both_directions_margins),
                    color = colorResource(R.color.white),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 15.dp, end = 20.dp, bottom = 15.dp, start = 20.dp)
                        .background(colorResource(R.color.medium_gray_5))
                        .padding(10.dp),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = stringResource(R.string.relative_test_6_complex_chaining_with_differe),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(bottom = 20.dp)
                    .background(colorResource(R.color.white_24))
            ) {
                val chain_start = createRef()
                val chain_middle = createRef()
                val chain_end = createRef()
                val chain_above = createRef()
                val chain_below = createRef()

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
                    fontSize = 10.sp,
                    color = colorResource(R.color.white),
                    textAlign = TextAlign.Center
                )
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
                    fontSize = 10.sp,
                    color = colorResource(R.color.white),
                    textAlign = TextAlign.Center
                )
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
                    fontSize = 10.sp,
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
            Text(
                text = stringResource(R.string.relative_test_7_mixed_anchor_references),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(top = 15.dp, end = 15.dp, bottom = 15.dp, start = 15.dp)
                    .padding(bottom = 20.dp)
                    .background(colorResource(R.color.pale_gray_2))
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
                        top.linkTo(ref1.top, margin = (-10).dp)
                        end.linkTo(ref2.end, margin = (-10).dp)
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
        }
    }    }
    // >>> GENERATED_CODE_END
}