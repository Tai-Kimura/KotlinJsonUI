package com.example.kotlinjsonui.sample.views.line_break_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.LineBreakTestData
import com.example.kotlinjsonui.sample.viewmodels.LineBreakTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun LineBreakTestGeneratedView(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    // Generated Compose code from line_break_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(android.graphics.Color.parseColor("#F8F8F8")))
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
                text = "lineBreakMode: Word (default)",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "\${data.longText}",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(10.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
                    .fillMaxWidth()
                    .height(60.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "lineBreakMode: Char",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "\${data.longText}",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(10.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFE0E0")))
                    .fillMaxWidth()
                    .height(60.dp),
                maxLines = 2
            )
            Text(
                text = "lineBreakMode: Clip",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "\${data.longText}",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(10.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0FFE0")))
                    .fillMaxWidth()
                    .height(60.dp),
                maxLines = 2,
                overflow = TextOverflow.Clip
            )
            Text(
                text = "lineBreakMode: Head",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "\${data.longText}",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(10.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0E0FF")))
                    .fillMaxWidth()
                    .height(60.dp),
                maxLines = 2
            )
            Text(
                text = "lineBreakMode: Middle",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "\${data.longText}",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(10.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFFFE0")))
                    .fillMaxWidth()
                    .height(60.dp),
                maxLines = 2
            )
            Text(
                text = "lineBreakMode: Tail",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "\${data.longText}",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(10.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFE0FF")))
                    .fillMaxWidth()
                    .height(60.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Lines Property Test",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 30.dp)
            )
            Text(
                text = "lines: 1",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = "\${data.longText}",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#D0FFFF")))
                    .fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "lines: 3",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = "\${data.longText}",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFD0D0")))
                    .fillMaxWidth(),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "lines: 0 (unlimited)",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = "\${data.longText}",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#D0FFD0")))
                    .fillMaxWidth(),
                maxLines = Int.MAX_VALUE
            )
        }
        }
    }    // >>> GENERATED_CODE_END
}