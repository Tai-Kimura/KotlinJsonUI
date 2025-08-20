package com.example.kotlinjsonui.sample.views.text_styling_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.TextStylingTestData
import com.example.kotlinjsonui.sample.viewmodels.TextStylingTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip

@Composable
fun TextStylingTestGeneratedView(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    // Generated Compose code from text_styling_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(android.graphics.Color.parseColor("#F8F8F8")))
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
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
                    .padding(top = 20.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Font Sizes",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "fontSize: 12",
                fontSize = 12.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = "fontSize: 16",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = "fontSize: 20",
                fontSize = 20.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = "fontSize: 24",
                fontSize = 24.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = "Font Styles",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "Bold Text",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = "Underlined Text",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = "Text Alignment",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "Left Aligned (default)",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
                    .padding(5.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Text(
                text = "Center Aligned",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
                    .padding(5.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Right Aligned",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
                    .padding(5.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.End
            )
            Text(
                text = "Line Spacing Test",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "This is a multi-line text with lineSpacing: 5 to demonstrate line spacing",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#F0F0F0")))
                    .padding(10.dp)
            )
            Text(
                text = "This is another multi-line text with lineSpacing: 10 to demonstrate line spacing (line breaks work with proper spacing)",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#F0F0F0")))
                    .padding(10.dp)
            )
            Text(
                text = "Font Colors",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "Red Text",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#FF0000")),
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = "Green Text",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#00FF00")),
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = "Blue Text",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#0000FF")),
                modifier = Modifier.padding(top = 5.dp)
            )
        }
        }
    }    // >>> GENERATED_CODE_END
}