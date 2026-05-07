package com.example.kotlinjsonui.sample.views.feature_cell

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
import com.example.kotlinjsonui.sample.data.FeatureCellData
import com.example.kotlinjsonui.sample.viewmodels.FeatureCellViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import com.example.kotlinjsonui.sample.R
import androidx.compose.ui.text.TextStyle

@Composable
fun FeatureCellGeneratedView(
    data: FeatureCellData,
    viewModel: FeatureCellViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from feature_cell.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "feature_cell",
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
                android.util.Log.e("DynamicView", "Error loading feature_cell: \$error")
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
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color.white))
            .padding(16.dp)
    ) {
        Text(
            text = "${data.badge}",
            fontSize = 10.sp,
            color = colorResource(R.color.white),
            style = TextStyle(lineHeight = 10.sp),
            modifier = Modifier
                .align(Alignment.TopStart)
                .clip(RoundedCornerShape(12.dp))
                .background(colorResource(R.color.light_red))
        )
        Text(
            text = "${data.title}",
            fontSize = 18.sp,
            color = colorResource(R.color.white),
            fontWeight = FontWeight.Bold,
            style = TextStyle(lineHeight = 18.sp),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 12.dp)
        )
        Text(
            text = "${data.description}",
            fontSize = 14.sp,
            color = colorResource(R.color.white),
            style = TextStyle(lineHeight = 14.sp),
            modifier = Modifier
                .alpha(0.9f)
                .align(Alignment.TopStart)
                .padding(top = 8.dp)
        )
    }    }
    // >>> GENERATED_CODE_END
}