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
            .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
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
                    .height(32.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#5856D6"))
                                )
            ) {
                Text(
                    text = "Dynamic: \${data.dynamicModeStatus}",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Text(
                text = "${data.title}",
                fontSize = 24.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "1. Parent Constraints with Margin + Padding",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 20.dp, end = 30.dp, bottom = 20.dp, start = 30.dp)
                    .padding(bottom = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
            ) {
                Text(
                    text = "Top+Left\ntopMargin:10\nleftMargin:15",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 10.dp)
                        .padding(start = 15.dp)
                        .background(Color(android.graphics.Color.parseColor("#FF6B6B")))
                        .padding(8.dp)
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
                Text(
                    text = "Top+Right\ntopMargin:15\nrightMargin:20",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 15.dp)
                        .padding(end = 20.dp)
                        .background(Color(android.graphics.Color.parseColor("#4ECDC4")))
                        .padding(8.dp)
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
                Text(
                    text = "Bottom+Left\nbottomMargin:5\nleftMargin:10",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 5.dp)
                        .padding(start = 10.dp)
                        .background(Color(android.graphics.Color.parseColor("#45B7D1")))
                        .padding(8.dp)
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
                Text(
                    text = "Bottom+Right\nbottomMargin:20\nrightMargin:25",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 20.dp)
                        .padding(end = 25.dp)
                        .background(Color(android.graphics.Color.parseColor("#96CEB4")))
                        .padding(8.dp)
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
            }
            Text(
                text = "2. Edge Alignment (Self Margin Only, Ignore Anchor Margin)",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(bottom = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#E8E8E8")))
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
                        .background(Color(android.graphics.Color.parseColor("#FF6B6B"))),
                    text = "Anchor\nmargins:[20,25,20,25]",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.constrainAs(align_top_test) {
                        top.linkTo(anchor_with_margin.top, margin = (-10).dp)
                        start.linkTo(parent.start, margin = 10.dp)
                    }
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#4ECDC4")))
                        .padding(5.dp),
                    text = "alignTop\ntopMargin:10",
                    color = Color(android.graphics.Color.parseColor("#000000"))
                )
                Text(
                    modifier = Modifier.constrainAs(align_bottom_test) {
                        bottom.linkTo(anchor_with_margin.bottom, margin = (-10).dp)
                        end.linkTo(parent.end, margin = 10.dp)
                    }
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#45B7D1")))
                        .padding(5.dp),
                    text = "alignBottom\nbottomMargin:10",
                    color = Color(android.graphics.Color.parseColor("#000000"))
                )
                Text(
                    modifier = Modifier.constrainAs(align_left_test) {
                        start.linkTo(anchor_with_margin.start, margin = (-15).dp)
                        top.linkTo(parent.top, margin = 10.dp)
                    }
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#96CEB4")))
                        .padding(5.dp),
                    text = "alignLeft\nleftMargin:15",
                    color = Color(android.graphics.Color.parseColor("#000000"))
                )
                Text(
                    modifier = Modifier.constrainAs(align_right_test) {
                        end.linkTo(anchor_with_margin.end, margin = (-15).dp)
                        bottom.linkTo(parent.bottom, margin = 10.dp)
                    }
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#FFEAA7")))
                        .padding(5.dp),
                    text = "alignRight\nrightMargin:15",
                    color = Color(android.graphics.Color.parseColor("#000000"))
                )
            }
            Text(
                text = "3. Relative Position (Both Margins Applied)",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(bottom = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#D8D8D8")))
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
                        .background(Color(android.graphics.Color.parseColor("#FF6B6B"))),
                    text = "Center\nmargins:[15,15,15,15]",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
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
                        .background(Color(android.graphics.Color.parseColor("#4ECDC4")))
                        .padding(5.dp),
                    text = "Above\nbottomMargin:10",
                    color = Color(android.graphics.Color.parseColor("#000000"))
                )
                Text(
                    modifier = Modifier.constrainAs(below_test) {
                        top.linkTo(center_anchor.bottom, margin = 10.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#45B7D1")))
                        .padding(5.dp),
                    text = "Below\ntopMargin:10",
                    color = Color(android.graphics.Color.parseColor("#000000"))
                )
                Text(
                    modifier = Modifier.constrainAs(left_of_test) {
                        end.linkTo(center_anchor.start, margin = 10.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#96CEB4")))
                        .padding(5.dp),
                    text = "LeftOf\nrightMargin:10",
                    color = Color(android.graphics.Color.parseColor("#000000"))
                )
                Text(
                    modifier = Modifier.constrainAs(right_of_test) {
                        start.linkTo(center_anchor.end, margin = 10.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#FFEAA7")))
                        .padding(5.dp),
                    text = "RightOf\nleftMargin:10",
                    color = Color(android.graphics.Color.parseColor("#000000"))
                )
            }
            Text(
                text = "4. Fixed Size with Both-Side Constraints (Centering)",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 10.dp, end = 10.dp, bottom = 10.dp, start = 10.dp)
                    .padding(bottom = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
            ) {
                Text(
                    text = "Fixed Width\nLeft+Right\nmargins:[10,10,10,10]",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(BiasAlignment(0f, -1f))
                        .padding(top = 10.dp, end = 10.dp, bottom = 10.dp, start = 10.dp)
                        .background(Color(android.graphics.Color.parseColor("#A29BFE")))
                        .padding(10.dp)
                        .width(100.dp)
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Fixed Height\nTop+Bottom",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(top = 10.dp)
                        .padding(bottom = 10.dp)
                        .padding(start = 10.dp)
                        .background(Color(android.graphics.Color.parseColor("#6C5CE7")))
                        .padding(10.dp)
                        .wrapContentWidth()
                        .height(50.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Fixed Both",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 15.dp, end = 15.dp, bottom = 15.dp, start = 15.dp)
                        .background(Color(android.graphics.Color.parseColor("#FD79A8")))
                        .width(80.dp)
                        .height(40.dp),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = "5. Dynamic Size (Stretch Between Constraints)",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(top = 20.dp, end = 30.dp, bottom = 20.dp, start = 30.dp)
                    .padding(bottom = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#D0D0D0")))
            ) {
                Text(
                    text = "Stretch Horizontal\nleftMargin:10, rightMargin:15",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(BiasAlignment(0f, -1f))
                        .padding(top = 10.dp)
                        .padding(start = 10.dp)
                        .padding(end = 15.dp)
                        .background(Color(android.graphics.Color.parseColor("#00B894")))
                        .padding(10.dp)
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Stretch\nVertical\ntop:10\nbottom:15",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(top = 10.dp)
                        .padding(bottom = 15.dp)
                        .padding(start = 10.dp)
                        .background(Color(android.graphics.Color.parseColor("#00CEC9")))
                        .padding(10.dp)
                        .wrapContentWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Stretch Both Directions\nmargins:[15,20,15,20]",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 15.dp, end = 20.dp, bottom = 15.dp, start = 20.dp)
                        .background(Color(android.graphics.Color.parseColor("#636E72")))
                        .padding(10.dp),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = "6. Complex Chaining with Different Margins",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(bottom = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#E8E8E8")))
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
                        .background(Color(android.graphics.Color.parseColor("#FF6B6B"))),
                    text = "Start\nleft:10\nright:5",
                    fontSize = 10.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
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
                        .background(Color(android.graphics.Color.parseColor("#4ECDC4"))),
                    text = "Middle\nleft:15\nright:20",
                    fontSize = 10.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
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
                        .background(Color(android.graphics.Color.parseColor("#45B7D1"))),
                    text = "End\nleft:10\nright:10",
                    fontSize = 10.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.constrainAs(chain_above) {
                        bottom.linkTo(chain_middle.top, margin = 5.dp)
                        start.linkTo(chain_middle.start)
                    }
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#FFEAA7")))
                        .padding(5.dp),
                    text = "Above Middle\nbottom:5",
                    color = Color(android.graphics.Color.parseColor("#000000"))
                )
                Text(
                    modifier = Modifier.constrainAs(chain_below) {
                        top.linkTo(chain_start.bottom, margin = 8.dp)
                        end.linkTo(chain_start.end)
                    }
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#FAB1A0")))
                        .padding(5.dp),
                    text = "Below Start\ntop:8",
                    color = Color(android.graphics.Color.parseColor("#000000"))
                )
            }
            Text(
                text = "7. Mixed Anchor References",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(top = 15.dp, end = 15.dp, bottom = 15.dp, start = 15.dp)
                    .padding(bottom = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#D8D8D8")))
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
                        .background(Color(android.graphics.Color.parseColor("#FF6B6B"))),
                    text = "Ref1",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.constrainAs(ref2) {
                        top.linkTo(parent.top, margin = 20.dp)
                        end.linkTo(parent.end, margin = 20.dp)
                    }
                        .width(60.dp)
                        .height(30.dp)
                        .background(Color(android.graphics.Color.parseColor("#4ECDC4"))),
                    text = "Ref2",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
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
                        .background(Color(android.graphics.Color.parseColor("#FFD93D")))
                        .padding(5.dp),
                    text = "Between (stretch)",
                    color = Color(android.graphics.Color.parseColor("#000000"))
                )
                Text(
                    modifier = Modifier.constrainAs(aligned_both) {
                        top.linkTo(ref1.top, margin = (-10).dp)
                        end.linkTo(ref2.end, margin = (-10).dp)
                    }
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#6BCB77")))
                        .padding(5.dp),
                    text = "AlignBoth",
                    color = Color(android.graphics.Color.parseColor("#000000"))
                )
                Text(
                    modifier = Modifier.constrainAs(complex_chain) {
                        top.linkTo(ref1.bottom, margin = 10.dp)
                        start.linkTo(ref1.start)
                    }
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(android.graphics.Color.parseColor("#FF6B9D")))
                        .padding(5.dp),
                    text = "Below Ref1",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF"))
                )
            }
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}