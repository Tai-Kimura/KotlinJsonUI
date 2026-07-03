package com.example.kotlinjsonui.sample.views.image_cell

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.ImageCellData
import com.example.kotlinjsonui.sample.viewmodels.ImageCellViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun ImageCellGeneratedView(
    data: ImageCellData,
    viewModel: ImageCellViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from image_cell.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "image_cell",
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
                android.util.Log.e("DynamicView", "Error loading image_cell: \$error")
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
        modifier = modifier
            .width(IntrinsicSize.Min)
            .clip(RoundedCornerShape(12.dp))
            .background(colorResource(R.color.white))
            .padding(8.dp)
    ) {
        AsyncImage(
            model = data.imageUrl,
            contentDescription = "Image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        val resolved_text173 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.SemiBold,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.title}",
            color = colorResource(R.color.dark_gray),
            fontFamily = resolved_text173.family,
            fontWeight = resolved_text173.weight,
            fontSize = resolved_text173.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text173.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 18.2.sp),
            modifier = Modifier.padding(top = 8.dp)
        )
        val resolved_text174 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 12.sp,
            italic = false
        ))
        Text(
            text = "${data.price}",
            color = colorResource(R.color.medium_blue),
            fontFamily = resolved_text174.family,
            fontWeight = resolved_text174.weight,
            fontSize = resolved_text174.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text174.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 15.6.sp),
            modifier = Modifier.padding(top = 4.dp)
        )
    }    }
    // >>> GENERATED_CODE_END
}