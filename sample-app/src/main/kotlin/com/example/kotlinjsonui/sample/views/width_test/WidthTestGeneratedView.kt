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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.ButtonDefaults

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
            .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
    ) {
        Button(
            onClick = { viewModel.toggleDynamicMode() },
            modifier = Modifier
                .wrapContentWidth()
                .height(32.dp),
            shape = RoundedCornerShape(8.dp),
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
            text = "Width Test",
            fontSize = 24.sp,
            color = Color(android.graphics.Color.parseColor("#000000")),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 20.dp)
        )
        Text(
            text = "matchParent width",
            color = Color(android.graphics.Color.parseColor("#FFFFFF")),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 20.dp)
                .background(Color(android.graphics.Color.parseColor("#FF6B6B")))
                .fillMaxWidth()
                .height(50.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Fixed width 200",
            color = Color(android.graphics.Color.parseColor("#FFFFFF")),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 10.dp)
                .background(Color(android.graphics.Color.parseColor("#4ECDC4")))
                .width(200.dp)
                .height(50.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = "wrapContent width",
            color = Color(android.graphics.Color.parseColor("#FFFFFF")),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 10.dp)
                .background(Color(android.graphics.Color.parseColor("#45B7D1")))
                .wrapContentWidth()
                .height(50.dp),
            textAlign = TextAlign.Center
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
                    .weight(1f)
                    .background(Color(android.graphics.Color.parseColor("#96CEB4")))
                    .fillMaxHeight(),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Weight 2 (wrap)",
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .weight(2f)
                    .background(Color(android.graphics.Color.parseColor("#FFEAA7")))
                    .wrapContentHeight(),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Weight 1",
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .weight(1f)
                    .background(Color(android.graphics.Color.parseColor("#DFE6E9")))
                    .fillMaxHeight(),
                textAlign = TextAlign.Center
            )
        }
    }    // >>> GENERATED_CODE_END
}