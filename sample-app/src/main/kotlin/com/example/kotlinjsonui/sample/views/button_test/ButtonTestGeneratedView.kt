package com.example.kotlinjsonui.sample.views.button_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.ButtonTestData
import com.example.kotlinjsonui.sample.viewmodels.ButtonTestViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.PaddingValues

@Composable
fun ButtonTestGeneratedView(
    data: ButtonTestData,
    viewModel: ButtonTestViewModel
) {
    // Generated Compose code from button_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${data.title}",
                fontSize = 24.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )
            Text(
                text = "Welcome to ButtonTest",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier
            )
            Button(
                onClick = { viewModel.onGetStarted() },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 24.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#6200EE"))
                                )
            ) {
                Text(
                    text = "Get Started",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
        }
    }    // >>> GENERATED_CODE_END
}