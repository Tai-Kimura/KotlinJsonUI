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
                val annotatedText = buildAnnotatedString {
                    append("This text has partial styling applied to different parts of the text.")
                    addStyle(
                        style = SpanStyle(color = Color(android.graphics.Color.parseColor("#FF0000")), fontWeight = FontWeight.Bold),
                        start = 14,
                        end = 21
                    )
                    addStyle(
                        style = SpanStyle(color = Color(android.graphics.Color.parseColor("#00FF00")), textDecoration = TextDecoration.Underline),
                        start = 22,
                        end = 29
                    )
                    addStyle(
                        style = SpanStyle(color = Color(android.graphics.Color.parseColor("#0000FF")), fontSize = 20.sp, background = Color(android.graphics.Color.parseColor("#FFFF00"))),
                        start = 50,
                        end = 55
                    )
                }
                ClickableText(
                    text = annotatedText,
                    onClick = { },,
                    style = TextStyle(fontSize = 16.sp)
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                val annotatedText = buildAnnotatedString {
                    append("Click here to navigate or here for another action.")
                    addStyle(
                        style = SpanStyle(color = Color(android.graphics.Color.parseColor("#0000FF")), textDecoration = TextDecoration.Underline),
                        start = 6,
                        end = 10
                    )
                    addStringAnnotation(
                        tag = "CLICKABLE",
                        annotation = "navigateToPage1",
                        start = 6,
                        end = 10
                    )
                    addStyle(
                        style = SpanStyle(color = Color(android.graphics.Color.parseColor("#0000FF")), textDecoration = TextDecoration.Underline),
                        start = 27,
                        end = 31
                    )
                    addStringAnnotation(
                        tag = "CLICKABLE",
                        annotation = "navigateToPage2",
                        start = 27,
                        end = 31
                    )
                }
                ClickableText(
                    text = annotatedText,
                    onClick = { offset ->
                        annotatedText.getStringAnnotations("CLICKABLE", offset, offset)
                            .firstOrNull()?.let { annotation ->
                                viewModel.handlePartialClick(annotation.item)
                            }
                    },,
                    style = TextStyle(fontSize = 16.sp)
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                val annotatedText = buildAnnotatedString {
                    append("Mixed styles: bold, italic, underline, strikethrough")
                    addStyle(
                        style = SpanStyle(fontWeight = FontWeight.Bold),
                        start = 14,
                        end = 18
                    )
                    addStyle(
                        style = SpanStyle(color = Color(android.graphics.Color.parseColor("#FF00FF"))),
                        start = 20,
                        end = 26
                    )
                    addStyle(
                        style = SpanStyle(textDecoration = TextDecoration.Underline),
                        start = 28,
                        end = 37
                    )
                    addStyle(
                        style = SpanStyle(color = Color(android.graphics.Color.parseColor("#999999")), textDecoration = TextDecoration.LineThrough),
                        start = 39,
                        end = 53
                    )
                }
                ClickableText(
                    text = annotatedText,
                    onClick = { },,
                    style = TextStyle(fontSize = 16.sp)
                    modifier = Modifier
                )
                val annotatedText = buildAnnotatedString {
                    append("今日は天気がいいですね。明日も晴れるといいです。")
                }
                ClickableText(
                    text = annotatedText,
                    onClick = { },,
                    style = TextStyle(fontSize = 16.sp)
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }
            }
        }
    }    // >>> GENERATED_CODE_END
}