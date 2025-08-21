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
import androidx.compose.ui.draw.alpha
import com.kotlinjsonui.components.VisibilityWrapper
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip

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
            .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
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
                    text = "\${data.dynamicModeStatus}",
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
            VisibilityWrapper(
                visibility = "visible",
            ) {
            Text(
                text = "Visibility: visible (default)",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .background(Color(android.graphics.Color.parseColor("#D0FFD0")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            }
            VisibilityWrapper(
                visibility = "invisible",
            ) {
            Text(
                text = "This label is invisible (takes space)",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFD0D0")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            }
            Text(
                text = "After invisible label",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#D0D0FF")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "After gone label (no gap)",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
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
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .alpha(1.0f)
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFE0E0")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Opacity: 0.7",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .alpha(0.7f)
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0FFE0")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Opacity: 0.5",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .alpha(0.5f)
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0E0FF")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Opacity: 0.3",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .alpha(0.3f)
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFFFE0")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Opacity: 0.1",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .alpha(0.1f)
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
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .alpha(0.6f)
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#D0FFFF")))
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Dynamic Visibility Tests",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 30.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Button(
                onClick = { },
                modifier = Modifier.padding(top = 10.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#007AFF"))
                                )
            ) {
                Text(
                    text = "Toggle Visibility",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            VisibilityWrapper(
                visibility = data.textVisibility,
            ) {
            Text(
                text = "This text uses dynamic visibility",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#FFE0E0")))
                    .padding(10.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            }
            Button(
                onClick = { },
                modifier = Modifier.padding(top = 20.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#34C759"))
                                )
            ) {
                Text(
                    text = "Toggle Hidden",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            VisibilityWrapper(
                hidden = data.isHidden,
            ) {
            Text(
                text = "This text uses dynamic hidden attribute",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0FFE0")))
                    .padding(10.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            }
        }
        }
    }    // >>> GENERATED_CODE_END
}