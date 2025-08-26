package com.example.kotlinjsonui.sample.views.product_cell

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
import com.example.kotlinjsonui.sample.data.ProductCellData
import com.example.kotlinjsonui.sample.viewmodels.ProductCellViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.SafeDynamicView
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip

@Composable
fun ProductCellGeneratedView(
    data: ProductCellData,
    viewModel: ProductCellViewModel
) {
    // Generated Compose code from product_cell.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "product_cell",
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
                android.util.Log.e("DynamicView", "Error loading product_cell: \$error")
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
            .clip(RoundedCornerShape(8.dp))
            .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
            .padding(12.dp)
    ) {
        Text(
            text = "${data.item.name}",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
        )
        Text(
            text = "${data.item.price}",
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = "${data.item.stock}",
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }    }
    // >>> GENERATED_CODE_END
}