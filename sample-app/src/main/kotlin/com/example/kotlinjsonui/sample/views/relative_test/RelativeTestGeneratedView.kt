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

@Composable
fun RelativeTestGeneratedView(
    data: RelativeTestData,
    viewModel: RelativeTestViewModel
) {
    // Generated Compose code from relative_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 20.dp, end = 20.dp, bottom = 20.dp, start = 20.dp)
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color(android.graphics.Color.parseColor("#F5F5F5")))
        ) {
            Box(
            ) {
            }
            Button(
                onClick = { },
            ) {
                Text("Button")
            }
            Text(
                text = "\${data.title}",
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
                        .padding(8.dp)
                        .padding(top = 10.dp)
                        .padding(start = 15.dp)
                        .background(Color(android.graphics.Color.parseColor("#FF6B6B")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
                Text(
                    text = "Top+Right\ntopMargin:15\nrightMargin:20",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .padding(top = 15.dp)
                        .padding(end = 20.dp)
                        .background(Color(android.graphics.Color.parseColor("#4ECDC4")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
                Text(
                    text = "Bottom+Left\nbottomMargin:5\nleftMargin:10",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .padding(bottom = 5.dp)
                        .padding(start = 10.dp)
                        .background(Color(android.graphics.Color.parseColor("#45B7D1")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
                Text(
                    text = "Bottom+Right\nbottomMargin:20\nrightMargin:25",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .padding(bottom = 20.dp)
                        .padding(end = 25.dp)
                        .background(Color(android.graphics.Color.parseColor("#96CEB4")))
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
                    text = "Anchor\nmargins:[20,25,20,25]",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF"))
                )
                Text(
                    modifier = Modifier.constrainAs(align_top_test) {
                        top.linkTo(anchor_with_margin.top)
                    },
                    text = "alignTop\ntopMargin:10"
                )
                Text(
                    modifier = Modifier.constrainAs(align_bottom_test) {
                        bottom.linkTo(anchor_with_margin.bottom)
                    },
                    text = "alignBottom\nbottomMargin:10"
                )
                Text(
                    modifier = Modifier.constrainAs(align_left_test) {
                        start.linkTo(anchor_with_margin.start)
                    },
                    text = "alignLeft\nleftMargin:15"
                )
                Text(
                    modifier = Modifier.constrainAs(align_right_test) {
                        end.linkTo(anchor_with_margin.end)
                    },
                    text = "alignRight\nrightMargin:15"
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
                    text = "Center\nmargins:[15,15,15,15]",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF"))
                )
                Text(
                    modifier = Modifier.constrainAs(above_test) {
                        top.linkTo(center_anchor.bottom)
                    },
                    text = "Above\nbottomMargin:10"
                )
                Text(
                    modifier = Modifier.constrainAs(below_test) {
                        bottom.linkTo(center_anchor.top)
                    },
                    text = "Below\ntopMargin:10"
                )
                Text(
                    modifier = Modifier.constrainAs(left_of_test) {
                        start.linkTo(center_anchor.end)
                    },
                    text = "LeftOf\nrightMargin:10"
                )
                Text(
                    modifier = Modifier.constrainAs(right_of_test) {
                        end.linkTo(center_anchor.start)
                    },
                    text = "RightOf\nleftMargin:10"
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
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .padding(top = 10.dp, end = 10.dp, bottom = 10.dp, start = 10.dp)
                        .background(Color(android.graphics.Color.parseColor("#A29BFE")))
                        .width(100.dp)
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Fixed Height\nTop+Bottom",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .padding(top = 10.dp)
                        .padding(bottom = 10.dp)
                        .padding(start = 10.dp)
                        .background(Color(android.graphics.Color.parseColor("#6C5CE7")))
                        .wrapContentWidth()
                        .height(50.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Fixed Both",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.TopStart)
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
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .padding(top = 10.dp)
                        .padding(start = 10.dp)
                        .padding(end = 15.dp)
                        .background(Color(android.graphics.Color.parseColor("#00B894")))
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Stretch\nVertical\ntop:10\nbottom:15",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .padding(top = 10.dp)
                        .padding(bottom = 15.dp)
                        .padding(start = 10.dp)
                        .background(Color(android.graphics.Color.parseColor("#00CEC9")))
                        .wrapContentWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Stretch Both Directions\nmargins:[15,20,15,20]",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .padding(top = 15.dp, end = 20.dp, bottom = 15.dp, start = 20.dp)
                        .background(Color(android.graphics.Color.parseColor("#636E72"))),
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
                    text = "Start\nleft:10\nright:5",
                    fontSize = 10.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF"))
                )
                Text(
                    modifier = Modifier.constrainAs(chain_middle) {
                        end.linkTo(chain_start.start)
                    },
                    text = "Middle\nleft:15\nright:20",
                    fontSize = 10.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF"))
                )
                Text(
                    modifier = Modifier.constrainAs(chain_end) {
                        end.linkTo(chain_middle.start)
                    },
                    text = "End\nleft:10\nright:10",
                    fontSize = 10.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF"))
                )
                Text(
                    modifier = Modifier.constrainAs(chain_above) {
                        top.linkTo(chain_middle.bottom)
                        start.linkTo(chain_middle.start)
                    },
                    text = "Above Middle\nbottom:5"
                )
                Text(
                    modifier = Modifier.constrainAs(chain_below) {
                        bottom.linkTo(chain_start.top)
                        end.linkTo(chain_start.end)
                    },
                    text = "Below Start\ntop:8"
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
                    text = "Ref1",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF"))
                )
                Text(
                    text = "Ref2",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF"))
                )
                Text(
                    modifier = Modifier.constrainAs(between_refs) {
                        start.linkTo(ref2.end)
                        end.linkTo(ref1.start)
                    },
                    text = "Between (stretch)"
                )
                Text(
                    modifier = Modifier.constrainAs(aligned_both) {
                        top.linkTo(ref1.top)
                        end.linkTo(ref2.end)
                    },
                    text = "AlignBoth"
                )
                Text(
                    modifier = Modifier.constrainAs(complex_chain) {
                        bottom.linkTo(ref1.top)
                        start.linkTo(ref1.start)
                    },
                    text = "Below Ref1",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF"))
                )
            }
        }
        }
    }    // >>> GENERATED_CODE_END
}