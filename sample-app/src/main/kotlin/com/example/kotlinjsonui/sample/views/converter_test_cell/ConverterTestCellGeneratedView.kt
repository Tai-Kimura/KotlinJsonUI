package com.example.kotlinjsonui.sample.views.converter_test_cell

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
import com.example.kotlinjsonui.sample.data.ConverterTestCellData
import com.example.kotlinjsonui.sample.viewmodels.ConverterTestCellViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun ConverterTestCellGeneratedView(
    data: ConverterTestCellData,
    viewModel: ConverterTestCellViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from converter_test_cell.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "converter_test_cell",
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
        Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colorResource(R.color.white))
            .padding(12.dp)
    ) {
        val resolved_text258 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.item["label"] as? String ?: "" ?: ""}",
            color = colorResource(R.color.black),
            fontFamily = resolved_text258.family,
            fontWeight = resolved_text258.weight,
            fontSize = resolved_text258.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text258.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 18.2.sp),
            modifier = Modifier.weight(1f)
        )
        val resolved_text259 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Bold,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.item["value"] as? String ?: "" ?: ""}",
            color = colorResource(R.color.medium_blue),
            fontFamily = resolved_text259.family,
            fontWeight = resolved_text259.weight,
            fontSize = resolved_text259.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text259.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 18.2.sp),
            modifier = Modifier
        )
    }    }
    // >>> GENERATED_CODE_END
}