package com.example.kotlinjsonui.sample.views.button_enabled_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import com.example.kotlinjsonui.sample.data.ButtonEnabledTestData
import com.example.kotlinjsonui.sample.viewmodels.ButtonEnabledTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun ButtonEnabledTestGeneratedView(
    data: ButtonEnabledTestData,
    viewModel: ButtonEnabledTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from button_enabled_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "button_enabled_test",
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
                android.util.Log.e("DynamicView", "Error loading button_enabled_test: \$error")
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
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(R.color.white))
            .padding(20.dp)
    ) {
        Section0(data, viewModel)
        Section1(data, viewModel)
        Section2(data, viewModel)
        Section3(data, viewModel)
        Section4(data, viewModel)
        Section5(data, viewModel)
        Section6(data, viewModel)
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: ButtonEnabledTestData,
    viewModel: ButtonEnabledTestViewModel
) {
    Button(
        onClick = { data.toggleDynamicMode?.invoke() },
        modifier = Modifier
            .wrapContentWidth()
            .height(44.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
        colors = ButtonDefaults.buttonColors(
                        containerColor = Color(android.graphics.Color.parseColor("#5856D6")),
                        contentColor = colorResource(R.color.white)
                    )
    ) {
        val resolved_button14 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button14.family,
            fontWeight = resolved_button14.weight,
            fontSize = resolved_button14.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button14.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section1(
    data: ButtonEnabledTestData,
    viewModel: ButtonEnabledTestViewModel
) {
    val resolved_text178 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = "${data.title}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text178.family,
        fontWeight = resolved_text178.weight,
        fontSize = resolved_text178.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text178.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 31.2.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section2(
    data: ButtonEnabledTestData,
    viewModel: ButtonEnabledTestViewModel
) {
    val resolved_text179 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = "${data.isButtonEnabled}",
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text179.family,
        fontWeight = resolved_text179.weight,
        fontSize = resolved_text179.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text179.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section3(
    data: ButtonEnabledTestData,
    viewModel: ButtonEnabledTestViewModel
) {
    Button(
        onClick = { data.testAction?.invoke() },
        shape = RoundedCornerShape(5.dp),
        contentPadding = PaddingValues(10.dp),
        colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.medium_green_2),
                        contentColor = colorResource(R.color.white)
                    ),
        enabled = data.isButtonEnabled
    ) {
        Text(stringResource(R.string.button_enabled_test_test_button_controlled_by_data))
    }
}

@Composable
private fun Section4(
    data: ButtonEnabledTestData,
    viewModel: ButtonEnabledTestViewModel
) {
    Button(
        onClick = { data.toggleEnabled?.invoke() },
        shape = RoundedCornerShape(5.dp),
        contentPadding = PaddingValues(10.dp),
        colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.medium_blue_2),
                        contentColor = colorResource(R.color.white)
                    )
    ) {
        Text(stringResource(R.string.button_enabled_test_toggle_enabled_state))
    }
}

@Composable
private fun Section5(
    data: ButtonEnabledTestData,
    viewModel: ButtonEnabledTestViewModel
) {
    Button(
        onClick = { data.neverCalled?.invoke() },
        shape = RoundedCornerShape(5.dp),
        contentPadding = PaddingValues(10.dp),
        colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.medium_red_2),
                        contentColor = colorResource(R.color.white)
                    ),
        enabled = false
    ) {
        Text(stringResource(R.string.button_enabled_test_always_disabled_button))
    }
}

@Composable
private fun Section6(
    data: ButtonEnabledTestData,
    viewModel: ButtonEnabledTestViewModel
) {
    Button(
        onClick = { data.alwaysCalled?.invoke() },
        shape = RoundedCornerShape(5.dp),
        contentPadding = PaddingValues(10.dp),
        colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.medium_purple),
                        contentColor = colorResource(R.color.white)
                    ),
        enabled = true
    ) {
        Text(stringResource(R.string.button_enabled_test_always_enabled_button))
    }
}
// >>> RESPONSIVE_HELPERS_END