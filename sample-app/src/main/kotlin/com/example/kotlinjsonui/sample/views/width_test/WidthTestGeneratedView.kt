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
import com.example.kotlinjsonui.sample.data.WidthTestData
import com.example.kotlinjsonui.sample.viewmodels.WidthTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun WidthTestGeneratedView(
    data: WidthTestData,
    viewModel: WidthTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from width_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
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
            Section5(data, viewModel)
        }    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("width_test")
    }
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
    data: WidthTestData,
    viewModel: WidthTestViewModel
) {
    val resolved_text1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.width_test_width_test),
        color = colorResource(R.color.black),
        fontFamily = resolved_text1.family,
        fontWeight = resolved_text1.weight,
        fontSize = resolved_text1.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text1.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 31.2.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section2(
    data: WidthTestData,
    viewModel: WidthTestViewModel
) {
    val resolved_text2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = null,
        italic = false
    ))
    Text(
        text = stringResource(R.string.width_test_matchparent_width),
        color = colorResource(R.color.white),
        fontFamily = resolved_text2.family,
        fontWeight = resolved_text2.weight,
        fontSize = resolved_text2.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text2.style ?: FontStyle.Normal,
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .requiredHeight(50.dp)
            .background(colorResource(R.color.light_red)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section3(
    data: WidthTestData,
    viewModel: WidthTestViewModel
) {
    val resolved_text3 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = null,
        italic = false
    ))
    Text(
        text = stringResource(R.string.width_test_fixed_width_200),
        color = colorResource(R.color.white),
        fontFamily = resolved_text3.family,
        fontWeight = resolved_text3.weight,
        fontSize = resolved_text3.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text3.style ?: FontStyle.Normal,
        modifier = Modifier
            .padding(top = 10.dp)
            .requiredWidth(200.dp)
            .requiredHeight(50.dp)
            .background(colorResource(R.color.light_lime)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section4(
    data: WidthTestData,
    viewModel: WidthTestViewModel
) {
    val resolved_text4 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = null,
        italic = false
    ))
    Text(
        text = stringResource(R.string.width_test_wrapcontent_width),
        color = colorResource(R.color.white),
        fontFamily = resolved_text4.family,
        fontWeight = resolved_text4.weight,
        fontSize = resolved_text4.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text4.style ?: FontStyle.Normal,
        modifier = Modifier
            .padding(top = 10.dp)
            .wrapContentWidth()
            .requiredHeight(50.dp)
            .background(colorResource(R.color.light_cyan)),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Section5(
    data: WidthTestData,
    viewModel: WidthTestViewModel
) {
    Row(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .requiredHeight(100.dp)
            .background(colorResource(R.color.pale_gray))
    ) {
        val resolved_text5 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.width_test_weight_1),
            color = colorResource(R.color.white),
            fontFamily = resolved_text5.family,
            fontWeight = resolved_text5.weight,
            fontSize = resolved_text5.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text5.style ?: FontStyle.Normal,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(colorResource(R.color.light_yellow)),
            textAlign = TextAlign.Center
        )
        val resolved_text6 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.width_test_weight_2_wrap),
            color = colorResource(R.color.black),
            fontFamily = resolved_text6.family,
            fontWeight = resolved_text6.weight,
            fontSize = resolved_text6.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text6.style ?: FontStyle.Normal,
            modifier = Modifier
                .weight(2f)
                .wrapContentHeight()
                .background(colorResource(R.color.pale_pink_2)),
            textAlign = TextAlign.Center
        )
        val resolved_text7 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.width_test_weight_1),
            color = colorResource(R.color.black),
            fontFamily = resolved_text7.family,
            fontWeight = resolved_text7.weight,
            fontSize = resolved_text7.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text7.style ?: FontStyle.Normal,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(colorResource(R.color.pale_gray_6)),
            textAlign = TextAlign.Center
        )
    }
}
// >>> RESPONSIVE_HELPERS_END