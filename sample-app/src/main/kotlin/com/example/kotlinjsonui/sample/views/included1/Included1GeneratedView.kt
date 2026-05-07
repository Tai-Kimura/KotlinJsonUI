package com.example.kotlinjsonui.sample.views.included1

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.Included1Data
import com.example.kotlinjsonui.sample.viewmodels.Included1ViewModel
import androidx.compose.foundation.background
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.ui.res.colorResource
import com.example.kotlinjsonui.sample.R

@Composable
fun Included1GeneratedView(
    data: Included1Data,
    viewModel: Included1ViewModel
) {
    // Generated Compose code from included1.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "included1",
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
                android.util.Log.e("DynamicView", "Error loading included1: \$error")
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
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(R.color.white))
    ) {
        Text(
            text = "${data.title}",
            fontSize = 24.sp,
            color = colorResource(R.color.black),
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(top = 20.dp)
        )
    }    }
    // >>> GENERATED_CODE_END
}