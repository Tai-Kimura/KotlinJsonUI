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
import com.kotlinjsonui.core.SafeDynamicView
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha

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
            .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
            .padding(16.dp)
    ) {
        Text(
            text = "${data.badge}",
            fontSize = 10.sp,
            color = Color(android.graphics.Color.parseColor("#FFFFFF")),
            modifier = Modifier
                .align(Alignment.TopStart)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(android.graphics.Color.parseColor("#FF6B6B")))
        )
        Text(
            text = "${data.title}",
            fontSize = 18.sp,
            color = Color(android.graphics.Color.parseColor("#FFFFFF")),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 12.dp)
        )
        Text(
            text = "${data.description}",
            fontSize = 14.sp,
            color = Color(android.graphics.Color.parseColor("#FFFFFF")),
            modifier = Modifier
                .alpha(0.9f)
                .align(Alignment.TopStart)
                .padding(top = 8.dp)
        )
    }    }
    // >>> GENERATED_CODE_END
}