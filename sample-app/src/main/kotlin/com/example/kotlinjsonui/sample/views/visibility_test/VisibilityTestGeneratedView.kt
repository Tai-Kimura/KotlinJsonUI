package com.example.kotlinjsonui.sample.views.visibility_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.VisibilityTestData
import com.example.kotlinjsonui.sample.viewmodels.VisibilityTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background

@Composable
fun VisibilityTestGeneratedView(
    data: VisibilityTestData,
    viewModel: VisibilityTestViewModel
) {
    // Generated Compose code from visibility_test.json
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
                text = "Visibility: visible (default)",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#D0FFD0")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "This label is invisible (takes space)",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFD0D0")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "After invisible label",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#D0D0FF")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "This label is gone (no space)",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFFFD0")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "After gone label (no gap)",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFD0FF")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Opacity Tests",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier
                    .padding(top = 30.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Opacity: 1.0 (fully visible)",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFE0E0")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Opacity: 0.7",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0FFE0")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Opacity: 0.5",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0E0FF")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Opacity: 0.3",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFFFE0")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Opacity: 0.1",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFD0FF")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Alpha Test (same as opacity)",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier
                    .padding(top = 30.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Alpha: 0.6",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#D0FFFF")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
        }
        }
    }    // >>> GENERATED_CODE_END
}