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
                    text = "Top+Left
                    topMargin:10
                    leftMargin:15",
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
                    text = "Top+Right
                    topMargin:15
                    rightMargin:20",
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
                    text = "Bottom+Left
                    bottomMargin:5
                    leftMargin:10",
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
                    text = "Bottom+Right
                    bottomMargin:20
                    rightMargin:25",
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(bottom = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#E8E8E8")))
            ) {
                Text(
                    text = "Anchor
                    margins:[20,25,20,25]",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 20.dp, end = 25.dp, bottom = 20.dp, start = 25.dp)
                        .background(Color(android.graphics.Color.parseColor("#FF6B6B")))
                        .width(120.dp)
                        .height(60.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "alignTop
                    topMargin:10",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(5.dp)
                        .padding(top = 10.dp)
                        .padding(start = 10.dp)
                        .background(Color(android.graphics.Color.parseColor("#4ECDC4")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
                Text(
                    text = "alignBottom
                    bottomMargin:10",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(5.dp)
                        .padding(bottom = 10.dp)
                        .padding(end = 10.dp)
                        .background(Color(android.graphics.Color.parseColor("#45B7D1")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
                Text(
                    text = "alignLeft
                    leftMargin:15",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(5.dp)
                        .padding(top = 10.dp)
                        .padding(start = 15.dp)
                        .background(Color(android.graphics.Color.parseColor("#96CEB4")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
                Text(
                    text = "alignRight
                    rightMargin:15",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(5.dp)
                        .padding(bottom = 10.dp)
                        .padding(end = 15.dp)
                        .background(Color(android.graphics.Color.parseColor("#FFEAA7")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
            }
            Text(
                text = "3. Relative Position (Both Margins Applied)",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(bottom = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#D8D8D8")))
            ) {
                Text(
                    text = "Center
                    margins:[15,15,15,15]",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 15.dp, end = 15.dp, bottom = 15.dp, start = 15.dp)
                        .background(Color(android.graphics.Color.parseColor("#FF6B6B")))
                        .width(100.dp)
                        .height(50.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Above
                    bottomMargin:10",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .padding(bottom = 10.dp)
                        .background(Color(android.graphics.Color.parseColor("#4ECDC4")))
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Below
                    topMargin:10",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .padding(top = 10.dp)
                        .background(Color(android.graphics.Color.parseColor("#45B7D1")))
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "LeftOf
                    rightMargin:10",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(5.dp)
                        .padding(end = 10.dp)
                        .background(Color(android.graphics.Color.parseColor("#96CEB4")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
                Text(
                    text = "RightOf
                    leftMargin:10",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(5.dp)
                        .padding(start = 10.dp)
                        .background(Color(android.graphics.Color.parseColor("#FFEAA7")))
                        .wrapContentWidth()
                        .wrapContentHeight()
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
                    text = "Fixed Width
                    Left+Right
                    margins:[10,10,10,10]",
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
                    text = "Fixed Height
                    Top+Bottom",
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
                    text = "Stretch Horizontal
                    leftMargin:10, rightMargin:15",
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
                    text = "Stretch
                    Vertical
                    top:10
                    bottom:15",
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
                    text = "Stretch Both Directions
                    margins:[15,20,15,20]",
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(bottom = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#E8E8E8")))
            ) {
                Text(
                    text = "Start
                    left:10
                    right:5",
                    fontSize = 10.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 10.dp)
                        .padding(end = 5.dp)
                        .background(Color(android.graphics.Color.parseColor("#FF6B6B")))
                        .width(60.dp)
                        .height(40.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Middle
                    left:15
                    right:20",
                    fontSize = 10.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 15.dp)
                        .padding(end = 20.dp)
                        .background(Color(android.graphics.Color.parseColor("#4ECDC4")))
                        .width(60.dp)
                        .height(40.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "End
                    left:10
                    right:10",
                    fontSize = 10.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 10.dp)
                        .padding(end = 10.dp)
                        .background(Color(android.graphics.Color.parseColor("#45B7D1")))
                        .width(60.dp)
                        .height(40.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Above Middle
                    bottom:5",
                    modifier = Modifier
                        .padding(5.dp)
                        .padding(top = 10.dp)
                        .padding(bottom = 5.dp)
                        .background(Color(android.graphics.Color.parseColor("#FFEAA7")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
                Text(
                    text = "Below Start
                    top:8",
                    modifier = Modifier
                        .padding(5.dp)
                        .padding(top = 8.dp)
                        .padding(bottom = 10.dp)
                        .background(Color(android.graphics.Color.parseColor("#FAB1A0")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
            }
            Text(
                text = "7. Mixed Anchor References",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(top = 15.dp, end = 15.dp, bottom = 15.dp, start = 15.dp)
                    .padding(bottom = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#D8D8D8")))
            ) {
                Text(
                    text = "Ref1",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                        .padding(start = 20.dp)
                        .padding(end = 10.dp)
                        .background(Color(android.graphics.Color.parseColor("#FF6B6B")))
                        .width(60.dp)
                        .height(30.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Ref2",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                        .padding(start = 10.dp)
                        .padding(end = 20.dp)
                        .background(Color(android.graphics.Color.parseColor("#4ECDC4")))
                        .width(60.dp)
                        .height(30.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Between (stretch)",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(5.dp)
                        .padding(start = 5.dp)
                        .padding(end = 5.dp)
                        .background(Color(android.graphics.Color.parseColor("#FFD93D")))
                        .wrapContentHeight()
                )
                Text(
                    text = "AlignBoth",
                    modifier = Modifier
                        .padding(5.dp)
                        .padding(top = 10.dp)
                        .padding(end = 10.dp)
                        .background(Color(android.graphics.Color.parseColor("#6BCB77")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
                Text(
                    text = "Below Ref1",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    modifier = Modifier
                        .padding(5.dp)
                        .padding(top = 10.dp)
                        .background(Color(android.graphics.Color.parseColor("#FF6B9D")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
            }
        }
        }
    }    // >>> GENERATED_CODE_END
}