package com.example.kotlinjsonui.sample.views.included2

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.Included2Data
import com.example.kotlinjsonui.sample.viewmodels.Included2ViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.core.SafeDynamicView

@Composable
fun Included2GeneratedView(
    data: Included2Data,
    viewModel: Included2ViewModel
) {
    // Generated Compose code from included2.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "included2",
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
                android.util.Log.e("DynamicView", "Error loading included2: \$error")
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
            .padding(15.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(android.graphics.Color.parseColor("#FFF4E6")))
    ) {
        Text(
            text = "Included View 2",
            fontSize = 18.sp,
            color = Color(android.graphics.Color.parseColor("#FF6600")),
            modifier = Modifier
        )
        Column(
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
        ) {
            Text(
                text = "${data.viewTitle}",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier
            )
            Text(
                text = "${data.viewStatus}",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier
            )
            Text(
                text = "${data.viewCount}",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier
            )
        }
    }    }
    // >>> GENERATED_CODE_END
}