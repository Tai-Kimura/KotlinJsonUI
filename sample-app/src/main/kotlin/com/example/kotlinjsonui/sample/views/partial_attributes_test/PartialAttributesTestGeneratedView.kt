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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.TextStyle
import com.kotlinjsonui.components.PartialAttributesText
import com.kotlinjsonui.components.PartialAttribute
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box

@Composable
fun PartialAttributesTestGeneratedView(
    data: PartialAttributesTestData,
    viewModel: PartialAttributesTestViewModel
) {
    // Generated Compose code from partial_attributes_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "partial_attributes_test",
            data = data.toMap(viewModel),
            fallback = {
                // Show error or loading state when dynamic view is not available
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Dynamic view not available",
                        color = Color.Gray
                    )
                }
            },
            onError = { error ->
                // Log error or show error UI
                android.util.Log.e("DynamicView", "Error loading partial_attributes_test: \$error")
            },
            onLoading = {
                // Show loading indicator
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        ) { jsonContent ->
            // Parse and render the dynamic JSON content
            // This will be handled by the DynamicView implementation
        }
    } else {
        // Static Mode - use generated code
        Box(
        modifier = Modifier
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
                PartialAttributesText(
                    text = "This text has partial styling applied to different parts of the text.",
                    partialAttributes = listOf(
                        PartialAttribute.fromJsonRange(
                            range = listOf(14, 21),
                            text = "This text has partial styling applied to different parts of the text.",
                            fontColor = "#FF0000",
                            fontWeight = "bold",
                            onClick = null
                        )!!,
                        PartialAttribute.fromJsonRange(
                            range = listOf(22, 29),
                            text = "This text has partial styling applied to different parts of the text.",
                            fontColor = "#00FF00",
                            underline = true,
                            onClick = null
                        )!!,
                        PartialAttribute.fromJsonRange(
                            range = listOf(50, 55),
                            text = "This text has partial styling applied to different parts of the text.",
                            fontColor = "#0000FF",
                            fontSize = 20,
                            background = "#FFFF00",
                            onClick = null
                        )!!
                    ),
                    modifier = Modifier.padding(bottom = 20.dp),
                    style = TextStyle(fontSize = 16.sp)
                )
                PartialAttributesText(
                    text = "Click here to navigate or here for another action.",
                    partialAttributes = listOf(
                        PartialAttribute.fromJsonRange(
                            range = listOf(6, 10),
                            text = "Click here to navigate or here for another action.",
                            fontColor = "#0000FF",
                            underline = true,
                            onClick = { viewModel.navigateToPage1() }
                        )!!,
                        PartialAttribute.fromJsonRange(
                            range = listOf(27, 31),
                            text = "Click here to navigate or here for another action.",
                            fontColor = "#0000FF",
                            underline = true,
                            onClick = { viewModel.navigateToPage2() }
                        )!!
                    ),
                    modifier = Modifier.padding(bottom = 20.dp),
                    style = TextStyle(fontSize = 16.sp)
                )
                PartialAttributesText(
                    text = "Mixed styles: bold, italic, underline, strikethrough",
                    partialAttributes = listOf(
                        PartialAttribute.fromJsonRange(
                            range = listOf(14, 18),
                            text = "Mixed styles: bold, italic, underline, strikethrough",
                            fontWeight = "bold",
                            onClick = null
                        )!!,
                        PartialAttribute.fromJsonRange(
                            range = listOf(20, 26),
                            text = "Mixed styles: bold, italic, underline, strikethrough",
                            fontColor = "#FF00FF",
                            onClick = null
                        )!!,
                        PartialAttribute.fromJsonRange(
                            range = listOf(28, 37),
                            text = "Mixed styles: bold, italic, underline, strikethrough",
                            underline = true,
                            onClick = null
                        )!!,
                        PartialAttribute.fromJsonRange(
                            range = listOf(39, 53),
                            text = "Mixed styles: bold, italic, underline, strikethrough",
                            fontColor = "#999999",
                            strikethrough = true,
                            onClick = null
                        )!!
                    ),
                    modifier = Modifier,
                    style = TextStyle(fontSize = 16.sp)
                )
                PartialAttributesText(
                    text = "今日は天気がいいですね。明日も晴れるといいです。",
                    partialAttributes = listOf(
                        PartialAttribute.fromJsonRange(
                            range = "天気",
                            text = "今日は天気がいいですね。明日も晴れるといいです。",
                            fontColor = "#FF0000",
                            fontSize = 20,
                            fontWeight = "bold",
                            onClick = null
                        )!!,
                        PartialAttribute.fromJsonRange(
                            range = "晴れる",
                            text = "今日は天気がいいですね。明日も晴れるといいです。",
                            fontColor = "#0000FF",
                            underline = true,
                            onClick = null
                        )!!
                    ),
                    modifier = Modifier.padding(bottom = 20.dp),
                    style = TextStyle(fontSize = 16.sp)
                )
            }
            }
        }
    }    }
    // >>> GENERATED_CODE_END
}