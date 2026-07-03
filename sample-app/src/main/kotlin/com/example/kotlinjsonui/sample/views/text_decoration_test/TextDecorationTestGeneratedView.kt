package com.example.kotlinjsonui.sample.views.text_decoration_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.TextDecorationTestData
import com.example.kotlinjsonui.sample.viewmodels.TextDecorationTestViewModel
import com.kotlinjsonui.components.PartialAttribute
import com.kotlinjsonui.components.PartialAttributesText
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun TextDecorationTestGeneratedView(
    data: TextDecorationTestData,
    viewModel: TextDecorationTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from text_decoration_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "text_decoration_test",
            modifier = modifier,
            data = data.toMap(),
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
        modifier = modifier.background(colorResource(R.color.white))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            val resolved_text199 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 16.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.text_decoration_test_normal_text_without_links),
                color = colorResource(R.color.black),
                fontFamily = resolved_text199.family,
                fontWeight = resolved_text199.weight,
                fontSize = resolved_text199.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text199.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 20.8.sp),
                modifier = Modifier
            )
            val resolved_text200 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 16.sp,
                italic = false
            ))
            PartialAttributesText(
                text = "Visit https://www.apple.com for more info",
                linkable = true,
                style = TextStyle(color = colorResource(R.color.black), fontFamily = resolved_text200.family, fontWeight = resolved_text200.weight, fontSize = (resolved_text200.size ?: TextUnit.Unspecified), fontStyle = (resolved_text200.style ?: FontStyle.Normal)),
                modifier = Modifier
            )
            val resolved_text201 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 16.sp,
                italic = false
            ))
            PartialAttributesText(
                text = "Multiple links: https://github.com and https://google.com are popular sites",
                linkable = true,
                style = TextStyle(color = colorResource(R.color.black), fontFamily = resolved_text201.family, fontWeight = resolved_text201.weight, fontSize = (resolved_text201.size ?: TextUnit.Unspecified), fontStyle = (resolved_text201.style ?: FontStyle.Normal)),
                modifier = Modifier
            )
            val resolved_text202 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 16.sp,
                italic = false
            ))
            PartialAttributesText(
                text = "Email: support@example.com\nWebsite: https://example.com\nPhone: 555-123-4567",
                linkable = true,
                style = TextStyle(color = colorResource(R.color.dark_blue), fontFamily = resolved_text202.family, fontWeight = resolved_text202.weight, fontSize = (resolved_text202.size ?: TextUnit.Unspecified), fontStyle = (resolved_text202.style ?: FontStyle.Normal)),
                modifier = Modifier
            )
            val resolved_text203 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 16.sp,
                italic = false
            ))
            PartialAttributesText(
                text = "Linkable with edgeInset: Check out https://anthropic.com",
                linkable = true,
                style = TextStyle(color = colorResource(R.color.white), fontFamily = resolved_text203.family, fontWeight = resolved_text203.weight, fontSize = (resolved_text203.size ?: TextUnit.Unspecified), fontStyle = (resolved_text203.style ?: FontStyle.Normal)),
                modifier = Modifier
                    .background(colorResource(R.color.dark_red))
                    .padding(10.dp)
            )
            val resolved_text204 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 16.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.text_decoration_test_no_linkable_flag_httpswwwtestco),
                color = colorResource(R.color.medium_gray_3),
                fontFamily = resolved_text204.family,
                fontWeight = resolved_text204.weight,
                fontSize = resolved_text204.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text204.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 20.8.sp),
                modifier = Modifier
            )
        }
    }    }
    // >>> GENERATED_CODE_END
}