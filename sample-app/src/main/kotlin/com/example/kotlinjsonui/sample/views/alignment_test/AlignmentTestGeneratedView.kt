package com.example.kotlinjsonui.sample.views.alignment_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.AlignmentTestData
import com.example.kotlinjsonui.sample.viewmodels.AlignmentTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextAlign

@Composable
fun AlignmentTestGeneratedView(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel
) {
    // Generated Compose code from alignment_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(android.graphics.Color.parseColor("#F0F0F0")))
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp)
        ) {
            Button(
                onClick = { },
            ) {
                Text("Button")
            }
            Text(
                text = "\${data.title}",
                fontSize = 24.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .wrapContentWidth()
                    .wrapContentHeight(),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Parent Alignment - Single Properties",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
            ) {
                Text(
                    text = "alignTop",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#FFD0D0")))
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#D8D8D8")))
            ) {
                Text(
                    text = "alignBottom",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#D0FFD0")))
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#D0D0D0")))
            ) {
                Text(
                    text = "alignLeft",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#D0D0FF")))
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#C8C8C8")))
            ) {
                Text(
                    text = "alignRight",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#FFFFD0")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#C0C0C0")))
            ) {
                Text(
                    text = "centerHorizontal",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#FFD0FF")))
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#B8B8B8")))
            ) {
                Text(
                    text = "centerVertical",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#D0FFFF")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#B0B0B0")))
            ) {
                Text(
                    text = "centerInParent",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#FFCCCC")))
                )
            }
            Text(
                text = "HStack Alignment Tests",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(bottom = 10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#A8A8A8")))
            ) {
                Text(
                    text = "Top",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#FFE0E0")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
                Text(
                    text = "Default",
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
                Text(
                    text = "Bottom",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#E0E0FF")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
                Text(
                    text = "Center",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#E0FFE0")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
            }
            Text(
                text = "VStack Alignment Tests",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(bottom = 10.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(android.graphics.Color.parseColor("#A0A0A0")))
            ) {
                Text(
                    text = "alignLeft",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#FFE0E0")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
                Text(
                    text = "Default",
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
                Text(
                    text = "alignRight",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#E0E0FF")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
                Text(
                    text = "centerHorizontal",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#E0FFE0")))
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center
                )
            }
        }
        }
    }    // >>> GENERATED_CODE_END
}