package com.example.kotlinjsonui.sample.views.converter_test_cell

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.ConverterTestCellData
import com.example.kotlinjsonui.sample.viewmodels.ConverterTestCellViewModel
import androidx.compose.foundation.background

@Composable
fun ConverterTestCellGeneratedView(
    data: ConverterTestCellData,
    viewModel: ConverterTestCellViewModel
) {
    // Generated Compose code from converter_test_cell.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp)
            .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
    ) {
        Text(
            text = "\${data.title}",
            fontSize = 16.sp,
            color = Color(android.graphics.Color.parseColor("#333333")),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "\${data.subtitle}",
            fontSize = 12.sp,
            color = Color(android.graphics.Color.parseColor("#666666")),
            modifier = Modifier
        )
    }    // >>> GENERATED_CODE_END
}