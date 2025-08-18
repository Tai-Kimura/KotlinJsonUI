package com.example.kotlinjsonui.sample.views.included1

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.Included1Data
import com.example.kotlinjsonui.sample.viewmodels.Included1ViewModel
import androidx.compose.foundation.background

@Composable
fun Included1GeneratedView(
    data: Included1Data,
    viewModel: Included1ViewModel
) {
    // Generated Compose code from included1.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
    ) {
        Text(
            text = "\${data.title}",
            fontSize = 24.sp,
            color = Color(android.graphics.Color.parseColor("#000000")),
            modifier = Modifier
                .padding(top = 20.dp)
                .wrapContentWidth()
                .wrapContentHeight()
        )
    }    // >>> GENERATED_CODE_END
}