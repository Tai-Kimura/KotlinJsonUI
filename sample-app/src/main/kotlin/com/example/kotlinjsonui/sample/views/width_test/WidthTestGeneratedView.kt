package com.example.kotlinjsonui.sample.views.width_test

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
import com.example.kotlinjsonui.sample.data.WidthTestData
import com.example.kotlinjsonui.sample.viewmodels.WidthTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun WidthTestGeneratedView(
    data: WidthTestData,
    viewModel: WidthTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from width_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "width_test",
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
                android.util.Log.e("DynamicView", "Error loading width_test: \$error")
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
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(R.color.white))
    ) {
        Section0(data, viewModel)
        Section1(data, viewModel)
        Section2(data, viewModel)
        Section3(data, viewModel)
        Section4(data, viewModel)
        Row(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .height(100.dp)
                .background(colorResource(R.color.pale_gray))
        ) {
            val resolved_text154 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = null,
                italic = false
            ))
            Text(
                text = stringResource(R.string.width_test_weight_1),
                color = colorResource(R.color.white),
                fontFamily = resolved_text154.family,
                fontWeight = resolved_text154.weight,
                fontSize = resolved_text154.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text154.style ?: FontStyle.Normal,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(colorResource(R.color.light_yellow)),
                textAlign = TextAlign.Center
            )
            val resolved_text155 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = null,
                italic = false
            ))
            Text(
                text = stringResource(R.string.width_test_weight_2_wrap),
                color = colorResource(R.color.black),
                fontFamily = resolved_text155.family,
                fontWeight = resolved_text155.weight,
                fontSize = resolved_text155.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text155.style ?: FontStyle.Normal,
                modifier = Modifier
                    .weight(2f)
                    .wrapContentHeight()
                    .background(colorResource(R.color.pale_pink_2)),
                textAlign = TextAlign.Center
            )
            val resolved_text156 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = null,
                italic = false
            ))
            Text(
                text = stringResource(R.string.width_test_weight_1),
                color = colorResource(R.color.black),
                fontFamily = resolved_text156.family,
                fontWeight = resolved_text156.weight,
                fontSize = resolved_text156.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text156.style ?: FontStyle.Normal,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(colorResource(R.color.pale_gray_6)),
                textAlign = TextAlign.Center
            )
        }
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: WidthTestData,
    viewModel: WidthTestViewModel
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
        val resolved_button12 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button12.family,
            fontWeight = resolved_button12.weight,
            fontSize = resolved_button12.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button12.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section1(
    data: WidthTestData,
    viewModel: WidthTestViewModel
) {
    val resolved_text150 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.test_menu_width_test),
        color = colorResource(R.color.black),
        fontFamily = resolved_text150.family,
        fontWeight = resolved_text150.weight,
        fontSize = resolved_text150.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text150.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 31.2.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section2(
    data: WidthTestData,
    viewModel: WidthTestViewModel
) {
    val resolved_text151 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = null,
        italic = false
    ))
    Text(
        text = stringResource(R.string.width_test_matchparent_width),
        color = colorResource(R.color.white),
        fontFamily = resolved_text151.family,
        fontWeight = resolved_text151.weight,
        fontSize = resolved_text151.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text151.style ?: FontStyle.Normal,
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .height(50.dp)
            .background(colorResource(R.color.light_red)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section3(
    data: WidthTestData,
    viewModel: WidthTestViewModel
) {
    val resolved_text152 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = null,
        italic = false
    ))
    Text(
        text = stringResource(R.string.width_test_fixed_width_200),
        color = colorResource(R.color.white),
        fontFamily = resolved_text152.family,
        fontWeight = resolved_text152.weight,
        fontSize = resolved_text152.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text152.style ?: FontStyle.Normal,
        modifier = Modifier
            .padding(top = 10.dp)
            .width(200.dp)
            .height(50.dp)
            .background(colorResource(R.color.light_lime)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section4(
    data: WidthTestData,
    viewModel: WidthTestViewModel
) {
    val resolved_text153 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = null,
        italic = false
    ))
    Text(
        text = stringResource(R.string.width_test_wrapcontent_width),
        color = colorResource(R.color.white),
        fontFamily = resolved_text153.family,
        fontWeight = resolved_text153.weight,
        fontSize = resolved_text153.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text153.style ?: FontStyle.Normal,
        modifier = Modifier
            .padding(top = 10.dp)
            .wrapContentWidth()
            .height(50.dp)
            .background(colorResource(R.color.light_cyan)),
        textAlign = TextAlign.Center
    )
}
// >>> RESPONSIVE_HELPERS_END