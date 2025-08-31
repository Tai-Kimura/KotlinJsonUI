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
import androidx.compose.ui.res.stringResource
import com.example.kotlinjsonui.sample.R
import androidx.compose.ui.res.colorResource

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
            .clip(RoundedCornerShape(10.dp))
            .background(colorResource(R.color.white_21))
            .padding(15.dp)
    ) {
        Text(
            text = stringResource(R.string.included2_included_view_2),
            fontSize = 18.sp,
            color = colorResource(R.color.medium_red_6),
            modifier = Modifier
        )
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .background(colorResource(R.color.white))
                .padding(10.dp)
        ) {
            Text(
                text = "${data.viewTitle}",
                fontSize = 14.sp,
                color = colorResource(R.color.dark_gray),
                modifier = Modifier
            )
            Text(
                text = "${data.viewStatus}",
                fontSize = 14.sp,
                color = colorResource(R.color.dark_gray),
                modifier = Modifier
            )
            Text(
                text = "${data.viewCount}",
                fontSize = 14.sp,
                color = colorResource(R.color.dark_gray),
                modifier = Modifier
            )
        }
    }    }
    // >>> GENERATED_CODE_END
}