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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.core.SafeDynamicView
import androidx.compose.ui.res.stringResource
import com.example.kotlinjsonui.sample.R
import androidx.compose.ui.res.colorResource

@Composable
fun TextDecorationTestGeneratedView(
    data: TextDecorationTestData,
    viewModel: TextDecorationTestViewModel
) {
    // Generated Compose code from text_decoration_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "text_decoration_test",
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
                android.util.Log.e("DynamicView", "Error loading text_decoration_test: \$error")
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
        modifier = Modifier.background(colorResource(R.color.white))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.text_decoration_test_normal_text_without_links),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
            )
            PartialAttributesText(
                text = "Visit https://www.apple.com for more info",
                linkable = true,
                style = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black)),
                modifier = Modifier
            )
            PartialAttributesText(
                text = "Multiple links: https://github.com and https://google.com are popular sites",
                linkable = true,
                style = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black)),
                modifier = Modifier
            )
            PartialAttributesText(
                text = "Email: support@example.com\nWebsite: https://example.com\nPhone: 555-123-4567",
                linkable = true,
                style = TextStyle(fontSize = 16.sp, color = colorResource(R.color.dark_blue)),
                modifier = Modifier
            )
            PartialAttributesText(
                text = "Linkable with edgeInset: Check out https://anthropic.com",
                linkable = true,
                style = TextStyle(fontSize = 16.sp, color = colorResource(R.color.white)),
                modifier = Modifier
                    .padding(10.dp)
                    .background(colorResource(R.color.dark_red))
            )
            Text(
                text = stringResource(R.string.text_decoration_test_no_linkable_flag_httpswwwtestco),
                fontSize = 16.sp,
                color = colorResource(R.color.medium_gray_3),
                modifier = Modifier
            )
        }
    }    }
    // >>> GENERATED_CODE_END
}