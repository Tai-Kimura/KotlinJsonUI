package com.example.kotlinjsonui.sample.views.feature_cell

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
import androidx.compose.ui.draw.alpha
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
import com.example.kotlinjsonui.sample.data.FeatureCellData
import com.example.kotlinjsonui.sample.viewmodels.FeatureCellViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

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
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color.white))
            .padding(16.dp)
    ) {
        val resolved_text337 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 10.sp,
            italic = false
        ))
        Text(
            text = "${data.badge}",
            color = colorResource(R.color.white),
            fontFamily = resolved_text337.family,
            fontWeight = resolved_text337.weight,
            fontSize = resolved_text337.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text337.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 13.0.sp),
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(colorResource(R.color.light_red))
                .padding(vertical = 4.dp, horizontal = 8.dp)
        )
        val resolved_text338 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Bold,
            size = 18.sp,
            italic = false
        ))
        Text(
            text = "${data.title}",
            color = colorResource(R.color.white),
            fontFamily = resolved_text338.family,
            fontWeight = resolved_text338.weight,
            fontSize = resolved_text338.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text338.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 23.4.sp),
            modifier = Modifier.padding(top = 12.dp)
        )
        val resolved_text339 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.description}",
            color = colorResource(R.color.white),
            fontFamily = resolved_text339.family,
            fontWeight = resolved_text339.weight,
            fontSize = resolved_text339.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text339.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 18.2.sp),
            modifier = Modifier
                .alpha(0.9f)
                .padding(top = 8.dp)
        )
    }    }
    // >>> GENERATED_CODE_END
}