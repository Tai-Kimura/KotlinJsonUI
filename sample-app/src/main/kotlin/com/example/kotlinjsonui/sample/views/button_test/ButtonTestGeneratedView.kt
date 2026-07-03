package com.example.kotlinjsonui.sample.views.button_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
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
import com.example.kotlinjsonui.sample.data.ButtonTestData
import com.example.kotlinjsonui.sample.viewmodels.ButtonTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun ButtonTestGeneratedView(
    data: ButtonTestData,
    viewModel: ButtonTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from button_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "button_test",
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
                android.util.Log.e("DynamicView", "Error loading button_test: \$error")
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
        LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(R.color.white))
            .imePadding()
    ) {
        item {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Section0(data, viewModel)
            Section1(data, viewModel)
            Section2(data, viewModel)
            Section3(data, viewModel)
            Section4(data, viewModel)
            Section5(data, viewModel)
            Section6(data, viewModel)
            Section7(data, viewModel)
            Section8(data, viewModel)
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: ButtonTestData,
    viewModel: ButtonTestViewModel
) {
    val resolved_text205 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 20.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.button_test_button_height_test),
        color = colorResource(R.color.black),
        fontFamily = resolved_text205.family,
        fontWeight = resolved_text205.weight,
        fontSize = resolved_text205.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text205.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 26.0.sp),
        modifier = Modifier.padding(bottom = 20.dp)
    )
}

@Composable
private fun Section1(
    data: ButtonTestData,
    viewModel: ButtonTestViewModel
) {
    val resolved_text206 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.button_test_height_55_padding_12_20),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text206.family,
        fontWeight = resolved_text206.weight,
        fontSize = resolved_text206.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text206.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 5.dp)
    )
}

@Composable
private fun Section2(
    data: ButtonTestData,
    viewModel: ButtonTestViewModel
) {
    Button(
        onClick = { },
        modifier = Modifier
            .padding(bottom = 20.dp)
            .height(55.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.medium_blue),
                            contentColor = colorResource(R.color.white)
                        )
    ) {
        Text(stringResource(R.string.button_test_test_button_1))
    }
}

@Composable
private fun Section3(
    data: ButtonTestData,
    viewModel: ButtonTestViewModel
) {
    val resolved_text207 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.button_test_height_55_no_padding),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text207.family,
        fontWeight = resolved_text207.weight,
        fontSize = resolved_text207.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text207.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 5.dp)
    )
}

@Composable
private fun Section4(
    data: ButtonTestData,
    viewModel: ButtonTestViewModel
) {
    Button(
        onClick = { },
        modifier = Modifier
            .padding(bottom = 20.dp)
            .height(55.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.medium_green),
                            contentColor = colorResource(R.color.white)
                        )
    ) {
        Text(stringResource(R.string.button_test_test_button_2))
    }
}

@Composable
private fun Section5(
    data: ButtonTestData,
    viewModel: ButtonTestViewModel
) {
    val resolved_text208 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.button_test_no_height_padding_12_20),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text208.family,
        fontWeight = resolved_text208.weight,
        fontSize = resolved_text208.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text208.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 5.dp)
    )
}

@Composable
private fun Section6(
    data: ButtonTestData,
    viewModel: ButtonTestViewModel
) {
    Button(
        onClick = { },
        modifier = Modifier.padding(bottom = 20.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.medium_red_3),
                            contentColor = colorResource(R.color.white)
                        )
    ) {
        Text(stringResource(R.string.button_test_test_button_3))
    }
}

@Composable
private fun Section7(
    data: ButtonTestData,
    viewModel: ButtonTestViewModel
) {
    val resolved_text209 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.button_test_with_bottommargin_8_height_55_p),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text209.family,
        fontWeight = resolved_text209.weight,
        fontSize = resolved_text209.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text209.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 5.dp)
    )
}

@Composable
private fun Section8(
    data: ButtonTestData,
    viewModel: ButtonTestViewModel
) {
    Button(
        onClick = { },
        modifier = Modifier
            .padding(bottom = 8.dp)
            .height(55.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.medium_blue),
                            contentColor = colorResource(R.color.white)
                        )
    ) {
        Text(stringResource(R.string.button_test_like_primarybutton_style))
    }
}
// >>> RESPONSIVE_HELPERS_END