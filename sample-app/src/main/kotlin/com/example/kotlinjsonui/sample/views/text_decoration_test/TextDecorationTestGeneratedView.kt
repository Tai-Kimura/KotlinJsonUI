package com.example.kotlinjsonui.sample.views.text_decoration_test

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
import com.example.kotlinjsonui.sample.data.TextDecorationTestData
import com.example.kotlinjsonui.sample.viewmodels.TextDecorationTestViewModel
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import com.kotlinjsonui.components.PartialAttributesText
import com.kotlinjsonui.components.PartialAttribute

@Composable
fun TextDecorationTestGeneratedView(
    data: TextDecorationTestData,
    viewModel: TextDecorationTestViewModel
) {
    // Generated Compose code from text_decoration_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        Box(
        modifier = Modifier.background(Color(android.graphics.Color.parseColor("#FFFFFF")))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Normal text without links",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
            )
            PartialAttributesText(
                text = "Visit https://www.apple.com for more info",
                linkable = true,
                style = TextStyle(fontSize = 16.sp, color = Color(android.graphics.Color.parseColor("#000000"))),
                modifier = Modifier
            )
            PartialAttributesText(
                text = "Multiple links: https://github.com and https://google.com are popular sites",
                linkable = true,
                style = TextStyle(fontSize = 16.sp, color = Color(android.graphics.Color.parseColor("#000000"))),
                modifier = Modifier
            )
            PartialAttributesText(
                text = "Email: support@example.com\nWebsite: https://example.com\nPhone: 555-123-4567",
                linkable = true,
                style = TextStyle(fontSize = 16.sp, color = Color(android.graphics.Color.parseColor("#0000FF"))),
                modifier = Modifier
            )
            PartialAttributesText(
                text = "Linkable with edgeInset: Check out https://anthropic.com",
                linkable = true,
                style = TextStyle(fontSize = 16.sp, color = Color(android.graphics.Color.parseColor("#FFFFFF"))),
                modifier = Modifier
                    .padding(10.dp)
                    .background(Color(android.graphics.Color.parseColor("#FF0000")))
            )
            Text(
                text = "No linkable flag: https://www.test.com won't be clickable",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#808080")),
                modifier = Modifier
            )
        }
    }    // >>> GENERATED_CODE_END
}