package com.example.kotlinjsonui.sample.views.alignment_combo_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.AlignmentComboTestData
import com.example.kotlinjsonui.sample.viewmodels.AlignmentComboTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextAlign

@Composable
fun AlignmentComboTestGeneratedView(
    data: AlignmentComboTestData,
    viewModel: AlignmentComboTestViewModel
) {
    // Generated Compose code from alignment_combo_test.json
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
                text = "Corner Combinations",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
            ) {
                Text(
                    text = "Top-Left",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
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
                    text = "Top-Right",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
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
                    text = "Bottom-Left",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
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
                    text = "Bottom-Right",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#FFFFD0")))
                )
            }
            Text(
                text = "Edge + Center Combinations",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#C0C0C0")))
            ) {
                Text(
                    text = "Top-Center",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#FFD0FF"))),
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
                    text = "Bottom-Center",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#D0FFFF"))),
                    textAlign = TextAlign.Center
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
                    text = "Left-Center",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#FFCCCC")))
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#A8A8A8")))
            ) {
                Text(
                    text = "Right-Center",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#CCFFCC")))
                )
            }
            Text(
                text = "Multiple Elements Test",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#A0A0A0")))
            ) {
                Text(
                    text = "TL",
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(5.dp)
                        .background(Color(android.graphics.Color.parseColor("#FFE0E0")))
                )
                Text(
                    text = "TR",
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp)
                        .background(Color(android.graphics.Color.parseColor("#E0FFE0")))
                )
                Text(
                    text = "BL",
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(5.dp)
                        .background(Color(android.graphics.Color.parseColor("#E0E0FF")))
                )
                Text(
                    text = "BR",
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(5.dp)
                        .background(Color(android.graphics.Color.parseColor("#FFFFE0")))
                )
                Text(
                    text = "Center",
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(5.dp)
                        .background(Color(android.graphics.Color.parseColor("#FFE0FF")))
                )
            }
            Text(
                text = "HStack Mixed Alignment",
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
                    .background(Color(android.graphics.Color.parseColor("#989898")))
            ) {
                Text(
                    text = "Left-Top",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#FFB0B0")))
                )
                Text(
                    text = "Center",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#B0FFB0"))),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Right-Bottom",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#B0B0FF")))
                )
            }
            Text(
                text = "VStack Mixed Alignment",
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
                    .background(Color(android.graphics.Color.parseColor("#909090")))
            ) {
                Text(
                    text = "Top-Left",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#FFC0C0")))
                )
                Text(
                    text = "Center",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#C0FFC0"))),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Bottom-Right",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#C0C0FF")))
                )
            }
            Text(
                text = "Edge Cases",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#888888")))
            ) {
                Text(
                    text = "Only horizontal center",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#FFE8E8"))),
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(android.graphics.Color.parseColor("#808080")))
            ) {
                Text(
                    text = "Only vertical center",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(8.dp)
                        .background(Color(android.graphics.Color.parseColor("#E8FFE8")))
                )
            }
        }
        }
    }    // >>> GENERATED_CODE_END
}