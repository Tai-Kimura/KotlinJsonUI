package com.example.kotlinjsonui.sample.views.category_header

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.CategoryHeaderData
import com.example.kotlinjsonui.sample.viewmodels.CategoryHeaderViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun CategoryHeaderGeneratedView(
    data: CategoryHeaderData,
    viewModel: CategoryHeaderViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from category_header.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "category_header",
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
                android.util.Log.e("DynamicView", "Error loading category_header: \$error")
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
        modifier = modifier
            .background(colorResource(R.color.white))
            .padding(12.dp)
    ) {
        val resolved_text177 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 16.sp,
            italic = false
        ))
        Text(
            text = "${data.title}",
            color = colorResource(R.color.dark_gray),
            fontFamily = resolved_text177.family,
            fontWeight = resolved_text177.weight,
            fontSize = resolved_text177.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text177.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 20.8.sp),
            modifier = Modifier
        )
    }    }
    // >>> GENERATED_CODE_END
}