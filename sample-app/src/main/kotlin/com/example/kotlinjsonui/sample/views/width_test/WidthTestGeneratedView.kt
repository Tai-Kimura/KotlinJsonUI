package com.example.kotlinjsonui.sample.views.width_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.WidthTestData
import com.example.kotlinjsonui.sample.viewmodels.WidthTestViewModel
import androidx.compose.foundation.background

@Composable
fun WidthTestGeneratedView(
    data: WidthTestData,
    viewModel: WidthTestViewModel
) {
    // Generated Compose code from width_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(android.graphics.Color.parseColor("#F5F5F5")))
    ) {
        Button(
            onClick = { },
            modifier = Modifier
        ) {
            Text("Button")
        }
        Text(
            text = "Width Test",
            fontSize = 24.sp,
            color = Color(android.graphics.Color.parseColor("#000000")),
            modifier = Modifier.padding(top = 20.dp)
        )
        Text(
            text = "matchParent width",
            color = Color(android.graphics.Color.parseColor("#FFFFFF")),
            modifier = Modifier
                .padding(top = 20.dp)
                .background(Color(android.graphics.Color.parseColor("#FF6B6B")))
                .fillMaxWidth()
                .height(50.dp)
        )
        Text(
            text = "Fixed width 200",
            color = Color(android.graphics.Color.parseColor("#FFFFFF")),
            modifier = Modifier
                .padding(top = 10.dp)
                .background(Color(android.graphics.Color.parseColor("#4ECDC4")))
                .width(200.dp)
                .height(50.dp)
        )
        Text(
            text = "wrapContent width",
            color = Color(android.graphics.Color.parseColor("#FFFFFF")),
            modifier = Modifier
                .padding(top = 10.dp)
                .background(Color(android.graphics.Color.parseColor("#45B7D1")))
                .wrapContentWidth()
                .height(50.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(top = 20.dp)
                .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
        ) {
            Text(
                text = "Weight 1",
                color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                modifier = Modifier
                    .background(Color(android.graphics.Color.parseColor("#96CEB4")))
                    .height(matchParent.dp)
            )
            Text(
                text = "Weight 2 (wrap)",
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .background(Color(android.graphics.Color.parseColor("#FFEAA7")))
                    .wrapContentHeight()
            )
            Text(
                text = "Weight 1",
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .background(Color(android.graphics.Color.parseColor("#DFE6E9")))
                    .height(matchParent.dp)
            )
        }
    }
    // >>> GENERATED_CODE_END
}