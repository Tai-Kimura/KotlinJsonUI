package com.example.kotlinjsonui.sample.views.converter_test_cell

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.ConverterTestCellData
import com.example.kotlinjsonui.sample.viewmodels.ConverterTestCellViewModel
import androidx.compose.foundation.background
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.core.SafeDynamicView

@Composable
fun ConverterTestCellGeneratedView(
    data: ConverterTestCellData,
    viewModel: ConverterTestCellViewModel
) {
    // Generated Compose code from converter_test_cell.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "converter_test_cell",
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
                android.util.Log.e("DynamicView", "Error loading converter_test_cell: \$error")
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
            .wrapContentHeight()
            .padding(10.dp)
            .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
    ) {
        Text(
            text = "${data.title}",
            fontSize = 16.sp,
            color = Color(android.graphics.Color.parseColor("#333333")),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "${data.subtitle}",
            fontSize = 12.sp,
            color = Color(android.graphics.Color.parseColor("#666666")),
            modifier = Modifier
        )
    }    }
    // >>> GENERATED_CODE_END
}