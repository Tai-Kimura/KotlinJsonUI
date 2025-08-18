package com.example.kotlinjsonui.sample.views.margins_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.MarginsTestData
import com.example.kotlinjsonui.sample.viewmodels.MarginsTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background

@Composable
fun MarginsTestGeneratedView(
    data: MarginsTestData,
    viewModel: MarginsTestViewModel
) {
    // Generated Compose code from margins_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(android.graphics.Color.parseColor("#F5F5F5")))
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
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
                    .padding(top = 20.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "All margins: [20, 20, 20, 20]",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 20.dp, end = 20.dp, bottom = 20.dp, start = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFE0E0")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Left margin: 40",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 40.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0FFE0")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Right margin: 40",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(end = 40.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0E0FF")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Top margin: 30",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 30.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFFFE0")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Bottom margin: 30",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFE0FF")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#DDDDDD")))
            ) {
                Text(
                    text = "Parent has leftPadding: 20, rightPadding: 20",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#CCCCCC")))
            ) {
                Text(
                    text = "MaxWidth: 200",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#FFCCCC")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#BBBBBB")))
            ) {
                Text(
                    text = "MinWidth: 150",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#CCFFCC")))
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
            }
        }
        }
    }    // >>> GENERATED_CODE_END
}