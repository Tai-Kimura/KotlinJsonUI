package com.example.kotlinjsonui.sample.views.included2

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.Included2Data
import com.example.kotlinjsonui.sample.viewmodels.Included2ViewModel
import androidx.compose.foundation.background

@Composable
fun Included2GeneratedView(
    data: Included2Data,
    viewModel: Included2ViewModel
) {
    // Generated Compose code from included2.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        Column(
        modifier = Modifier
            .padding(15.dp)
            .background(Color(android.graphics.Color.parseColor("#FFF4E6")))
    ) {
        Text(
            text = "Included View 2",
            fontSize = 18.sp,
            color = Color(android.graphics.Color.parseColor("#FF6600")),
            modifier = Modifier
        )
        Column(
            modifier = Modifier
                .padding(10.dp)
                .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
        ) {
            Text(
                text = "${data.viewTitle}",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier
            )
            Text(
                text = "${data.viewStatus}",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier
            )
            Text(
                text = "${data.viewCount}",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier
            )
        }
    }
    // >>> GENERATED_CODE_END
}