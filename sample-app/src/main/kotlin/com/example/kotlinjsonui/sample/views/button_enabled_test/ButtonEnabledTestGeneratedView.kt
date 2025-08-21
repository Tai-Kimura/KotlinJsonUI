package com.example.kotlinjsonui.sample.views.button_enabled_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.ButtonEnabledTestData
import com.example.kotlinjsonui.sample.viewmodels.ButtonEnabledTestViewModel
import androidx.compose.foundation.background
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.PaddingValues

@Composable
fun ButtonEnabledTestGeneratedView(
    data: ButtonEnabledTestData,
    viewModel: ButtonEnabledTestViewModel
) {
    // Generated Compose code from button_enabled_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(20.dp)
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
        )
        Text(
            text = "${data.isButtonEnabled}",
            fontSize = 16.sp,
            color = Color(android.graphics.Color.parseColor("#666666")),
            modifier = Modifier
        )
        Button(
            onClick = { viewModel.testAction() },
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(10.dp),
            colors = ButtonDefaults.buttonColors(
                            containerColor = Color(android.graphics.Color.parseColor("#4CAF50"))
                        ),
            enabled = data.isButtonEnabled
        ) {
            Text(
                text = "Test Button (controlled by data)",
                color = Color(android.graphics.Color.parseColor("#FFFFFF")),
            )
        }
        Button(
            onClick = { viewModel.toggleEnabled() },
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(10.dp),
            colors = ButtonDefaults.buttonColors(
                            containerColor = Color(android.graphics.Color.parseColor("#2196F3"))
                        )
        ) {
            Text(
                text = "Toggle Enabled State",
                color = Color(android.graphics.Color.parseColor("#FFFFFF")),
            )
        }
        Button(
            onClick = { viewModel.neverCalled() },
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(10.dp),
            colors = ButtonDefaults.buttonColors(
                            containerColor = Color(android.graphics.Color.parseColor("#FF5722"))
                        ),
            enabled = false
        ) {
            Text(
                text = "Always Disabled Button",
                color = Color(android.graphics.Color.parseColor("#FFFFFF")),
            )
        }
        Button(
            onClick = { viewModel.alwaysCalled() },
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(10.dp),
            colors = ButtonDefaults.buttonColors(
                            containerColor = Color(android.graphics.Color.parseColor("#9C27B0"))
                        ),
            enabled = true
        ) {
            Text(
                text = "Always Enabled Button",
                color = Color(android.graphics.Color.parseColor("#FFFFFF")),
            )
        }
    }    // >>> GENERATED_CODE_END
}