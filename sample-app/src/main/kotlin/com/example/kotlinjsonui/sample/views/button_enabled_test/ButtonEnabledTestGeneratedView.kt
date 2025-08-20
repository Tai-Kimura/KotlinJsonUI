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
            onClick = { },
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Button",
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
            modifier = Modifier.padding(10.dp),
            shape = RoundedCornerShape(5.dp),
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
            modifier = Modifier.padding(10.dp),
            shape = RoundedCornerShape(5.dp),
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
            modifier = Modifier.padding(10.dp),
            shape = RoundedCornerShape(5.dp),
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
            modifier = Modifier.padding(10.dp),
            shape = RoundedCornerShape(5.dp),
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