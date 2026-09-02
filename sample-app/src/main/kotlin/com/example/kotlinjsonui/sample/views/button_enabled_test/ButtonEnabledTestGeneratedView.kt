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
import com.example.kotlinjsonui.sample.data.ButtonEnabledTestData
import com.example.kotlinjsonui.sample.viewmodels.ButtonEnabledTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun ButtonEnabledTestGeneratedView(
    data: ButtonEnabledTestData,
    viewModel: ButtonEnabledTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from button_enabled_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
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
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("button_enabled_test")
    }
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
            .requiredHeight(44.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
        colors = ButtonDefaults.buttonColors(
                        containerColor = Color(android.graphics.Color.parseColor("#5856D6")),
                        disabledContainerColor = Color(android.graphics.Color.parseColor("#5856D6")).copy(alpha = 0.5f),
                        contentColor = colorResource(R.color.white),
                        disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                    )
    ) {
        val resolved_button1 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button1.family,
            fontWeight = resolved_button1.weight,
            fontSize = resolved_button1.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button1.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section1(
    data: ButtonEnabledTestData,
    viewModel: ButtonEnabledTestViewModel
) {
    val resolved_text1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = "${data.title}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text1.family,
        fontWeight = resolved_text1.weight,
        fontSize = resolved_text1.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text1.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 31.2.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section2(
    data: ButtonEnabledTestData,
    viewModel: ButtonEnabledTestViewModel
) {
    val resolved_text2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = "${data.isButtonEnabled}",
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text2.family,
        fontWeight = resolved_text2.weight,
        fontSize = resolved_text2.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text2.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
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
                        disabledContainerColor = colorResource(R.color.medium_green_2).copy(alpha = 0.5f),
                        contentColor = colorResource(R.color.white),
                        disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
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
                        disabledContainerColor = colorResource(R.color.medium_blue_2).copy(alpha = 0.5f),
                        contentColor = colorResource(R.color.white),
                        disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
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
                        disabledContainerColor = colorResource(R.color.medium_red_2).copy(alpha = 0.5f),
                        contentColor = colorResource(R.color.white),
                        disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
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
                        disabledContainerColor = colorResource(R.color.medium_purple).copy(alpha = 0.5f),
                        contentColor = colorResource(R.color.white),
                        disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                    ),
        enabled = true
    ) {
        Text(stringResource(R.string.button_enabled_test_always_enabled_button))
    }
}
// >>> RESPONSIVE_HELPERS_END