package com.example.kotlinjsonui.sample.views.text_view_hint_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.TextViewHintTestData
import com.example.kotlinjsonui.sample.viewmodels.TextViewHintTestViewModel
import androidx.compose.foundation.background

@Composable
fun TextViewHintTestGeneratedView(
    data: TextViewHintTestData,
    viewModel: TextViewHintTestViewModel
) {
    // Generated Compose code from text_view_hint_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(20.dp)
            .background(Color(android.graphics.Color.parseColor("#F5F5F5")))
    ) {
        Button(
            onClick = { },
        ) {
            Text("Button")
        }
        Text(
            text = "TextView Hint Test",
            fontSize = 24.sp,
            color = Color(android.graphics.Color.parseColor("#000000")),
            modifier = Modifier.padding(bottom = 20.dp)
        )
        Text(
            text = "Simple TextView with hint:",
            fontSize = 14.sp,
            color = Color(android.graphics.Color.parseColor("#666666")),
            modifier = Modifier.padding(bottom = 8.dp)
        )
// TODO: Implement component type: TextView
        Text(
            text = "Flexible TextView with multi-line hint:",
            fontSize = 14.sp,
            color = Color(android.graphics.Color.parseColor("#666666")),
            modifier = Modifier.padding(bottom = 8.dp)
        )
// TODO: Implement component type: TextView
    }    // >>> GENERATED_CODE_END
}