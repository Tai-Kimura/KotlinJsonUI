package com.example.kotlinjsonui.sample.views.weight_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.WeightTestData
import com.example.kotlinjsonui.sample.viewmodels.WeightTestViewModel
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.PaddingValues

@Composable
fun WeightTestGeneratedView(
    data: WeightTestData,
    viewModel: WeightTestViewModel
) {
    // Generated Compose code from weight_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        Column(
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
            modifier = Modifier
                .padding(top = 20.dp)
                .wrapContentWidth()
                .wrapContentHeight()
        )
        Text(
            text = "Horizontal Weight Distribution (1:2:1)",
            fontSize = 16.sp,
            color = Color(android.graphics.Color.parseColor("#333333")),
            modifier = Modifier
                .padding(top = 20.dp)
                .wrapContentWidth()
                .wrapContentHeight()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(top = 10.dp)
        ) {
            Text(
                text = "Weight: 1",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .weight(1f)
                    .background(Color(android.graphics.Color.parseColor("#FFD0D0")))
                    .wrapContentHeight(),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Weight: 2",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .weight(2f)
                    .background(Color(android.graphics.Color.parseColor("#D0FFD0")))
                    .fillMaxHeight(),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Weight: 1",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .weight(1f)
                    .background(Color(android.graphics.Color.parseColor("#D0D0FF")))
                    .fillMaxHeight(),
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = "Vertical Weight Distribution (1:3:2)",
            fontSize = 16.sp,
            color = Color(android.graphics.Color.parseColor("#333333")),
            modifier = Modifier
                .padding(top = 30.dp)
                .wrapContentWidth()
                .wrapContentHeight()
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(top = 10.dp)
        ) {
            Text(
                text = "Weight: 1",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .weight(1f)
                    .background(Color(android.graphics.Color.parseColor("#FFFFD0")))
                    .fillMaxWidth()
                    .height(0.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Weight: 3",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .weight(3f)
                    .background(Color(android.graphics.Color.parseColor("#FFD0FF")))
                    .fillMaxWidth()
                    .height(0.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Weight: 2",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .weight(2f)
                    .background(Color(android.graphics.Color.parseColor("#D0FFFF")))
                    .fillMaxWidth()
                    .height(0.dp),
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = "widthWeight Test",
            fontSize = 16.sp,
            color = Color(android.graphics.Color.parseColor("#333333")),
            modifier = Modifier
                .padding(top = 30.dp)
                .wrapContentWidth()
                .wrapContentHeight()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(top = 10.dp)
        ) {
            Text(
                text = "widthWeight: 1",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .background(Color(android.graphics.Color.parseColor("#FFE0E0")))
                    .width(0.dp)
                    .fillMaxHeight(),
                textAlign = TextAlign.Center
            )
            Text(
                text = "widthWeight: 1",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .background(Color(android.graphics.Color.parseColor("#E0FFE0")))
                    .width(0.dp)
                    .fillMaxHeight(),
                textAlign = TextAlign.Center
            )
        }
    }    // >>> GENERATED_CODE_END
}