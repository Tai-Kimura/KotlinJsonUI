package com.example.kotlinjsonui.sample.views.horizontal_card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.HorizontalCardData
import com.example.kotlinjsonui.sample.viewmodels.HorizontalCardViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import com.example.kotlinjsonui.sample.R
import androidx.compose.ui.text.TextStyle

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
        modifier = Modifier
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
        Text(
            text = "${data.title}",
            fontSize = 14.sp,
            color = colorResource(R.color.dark_gray),
            fontWeight = FontWeight.SemiBold,
            style = TextStyle(lineHeight = 14.sp),
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "${data.description}",
            fontSize = 12.sp,
            color = colorResource(R.color.medium_gray_4),
            style = TextStyle(lineHeight = 12.sp),
            modifier = Modifier.padding(top = 4.dp)
        )
    }    }
    // >>> GENERATED_CODE_END
}