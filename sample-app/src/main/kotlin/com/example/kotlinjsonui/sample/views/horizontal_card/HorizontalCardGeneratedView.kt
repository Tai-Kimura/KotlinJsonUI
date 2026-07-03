package com.example.kotlinjsonui.sample.views.horizontal_card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.HorizontalCardData
import com.example.kotlinjsonui.sample.viewmodels.HorizontalCardViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun HorizontalCardGeneratedView(
    data: HorizontalCardData,
    viewModel: HorizontalCardViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from horizontal_card.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "horizontal_card",
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
                android.util.Log.e("DynamicView", "Error loading horizontal_card: \$error")
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
        Column(
        modifier = modifier
            .width(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colorResource(R.color.white))
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colorResource(R.color.white_16))
        ) {
        }
        val resolved_text373 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.SemiBold,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.title}",
            color = colorResource(R.color.dark_gray),
            fontFamily = resolved_text373.family,
            fontWeight = resolved_text373.weight,
            fontSize = resolved_text373.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text373.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 18.2.sp),
            modifier = Modifier.padding(top = 8.dp)
        )
        val resolved_text374 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 12.sp,
            italic = false
        ))
        Text(
            text = "${data.description}",
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text374.family,
            fontWeight = resolved_text374.weight,
            fontSize = resolved_text374.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text374.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 15.6.sp),
            modifier = Modifier.padding(top = 4.dp)
        )
    }    }
    // >>> GENERATED_CODE_END
}