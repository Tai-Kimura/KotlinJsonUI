package com.example.kotlinjsonui.sample.views.secure_field_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.SecureFieldTestData
import com.example.kotlinjsonui.sample.viewmodels.SecureFieldTestViewModel

@Composable
fun SecureFieldTestGeneratedView(
    data: SecureFieldTestData,
    viewModel: SecureFieldTestViewModel
) {
    // Generated Compose code from secure_field_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(
        modifier = Modifier.fillMaxSize().systemBarsPadding()
            .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
    ) {
        Column(
            modifier = Modifier.padding(16 dp)
        ) {
            Text(
                text = data.title,
                fontSize = 24 sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )
            Text(
                text = "Welcome to SecureFieldTest",
                fontSize = 16 sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier
            )
            Button(
                onClick = { viewModel.onGetStarted() },
                modifier = Modifier.padding([12, 24] dp)
            ) {
                Text("Get Started")
            }
        }
    }
    // >>> GENERATED_CODE_END
}
