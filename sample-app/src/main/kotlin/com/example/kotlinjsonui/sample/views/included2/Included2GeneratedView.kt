package com.example.kotlinjsonui.sample.views.included2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.Included2Data
import com.example.kotlinjsonui.sample.viewmodels.Included2ViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun Included2GeneratedView(
    data: Included2Data,
    viewModel: Included2ViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from included2.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
    DriveEmbedInitParams(viewModel)
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "included2",
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
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colorResource(R.color.white_21))
            .padding(15.dp)
    ) {
        val resolved_text1 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 18.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.included2_included_view_2),
            color = colorResource(R.color.medium_red_6),
            fontFamily = resolved_text1.family,
            fontWeight = resolved_text1.weight,
            fontSize = resolved_text1.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text1.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
            modifier = Modifier
        )
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .background(colorResource(R.color.white))
                .padding(10.dp)
        ) {
            val resolved_text2 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = "${data.viewTitle}",
                color = colorResource(R.color.dark_gray),
                fontFamily = resolved_text2.family,
                fontWeight = resolved_text2.weight,
                fontSize = resolved_text2.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text2.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
                modifier = Modifier
            )
            val resolved_text3 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = "${data.viewStatus}",
                color = colorResource(R.color.dark_gray),
                fontFamily = resolved_text3.family,
                fontWeight = resolved_text3.weight,
                fontSize = resolved_text3.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text3.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
                modifier = Modifier
            )
            val resolved_text4 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = "${data.viewCount}",
                color = colorResource(R.color.dark_gray),
                fontFamily = resolved_text4.family,
                fontWeight = resolved_text4.weight,
                fontSize = resolved_text4.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text4.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
                modifier = Modifier
            )
        }
    }    }
    // >>> GENERATED_CODE_END
}