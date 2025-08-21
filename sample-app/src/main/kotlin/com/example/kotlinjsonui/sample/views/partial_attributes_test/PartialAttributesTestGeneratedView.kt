package com.example.kotlinjsonui.sample.views.partial_attributes_test

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
import com.example.kotlinjsonui.sample.data.PartialAttributesTestData
import com.example.kotlinjsonui.sample.viewmodels.PartialAttributesTestViewModel

@Composable
fun PartialAttributesTestGeneratedView(
    data: PartialAttributesTestData,
    viewModel: PartialAttributesTestViewModel
) {
    // Generated Compose code from partial_attributes_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        Box(
        modifier = Modifier
            Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp)
            ) {
                Text(
                    text = "PartialAttributes Test",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Text(
                    text = "This is a normal label without any partial attributes.",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Text(
                    text = "This text has partial styling applied to different parts of the text.",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Text(
                    text = "Click here to navigate or here for another action.",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Text(
                    text = "Mixed styles: bold, italic, underline, strikethrough",
                    fontSize = 16.sp,
                    modifier = Modifier
                )
                Text(
                    text = "今日は天気がいいですね。明日も晴れるといいです。",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }
            }
        }
    }    // >>> GENERATED_CODE_END
}