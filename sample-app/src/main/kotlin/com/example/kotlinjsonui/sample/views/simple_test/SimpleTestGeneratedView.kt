package com.example.kotlinjsonui.sample.views.simple_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.kotlinjsonui.sample.data.SimpleTestData
import com.example.kotlinjsonui.sample.viewmodels.SimpleTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun SimpleTestGeneratedView(
    data: SimpleTestData,
    viewModel: SimpleTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from simple_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "simple_test",
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
                android.util.Log.e("DynamicView", "Error loading simple_test: \$error")
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
        modifier = modifier.padding(16.dp)
    ) {
        val resolved_text370 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 24.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.simple_test_simple_test),
            color = colorResource(R.color.black),
            fontFamily = resolved_text370.family,
            fontWeight = resolved_text370.weight,
            fontSize = resolved_text370.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text370.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 31.2.sp),
            modifier = Modifier
        )
        val resolved_text371 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 16.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.simple_test_testing_basic_components),
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text371.family,
            fontWeight = resolved_text371.weight,
            fontSize = resolved_text371.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text371.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 20.8.sp),
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(
            onClick = { },
            modifier = Modifier.padding(top = 16.dp),
            shape = RoundedCornerShape(Configuration.Button.defaultCornerRadius.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.medium_blue_3),
                            contentColor = colorResource(R.color.white)
                        )
        ) {
            Text(stringResource(R.string.simple_test_test_button))
        }
    }    }
    // >>> GENERATED_CODE_END
}