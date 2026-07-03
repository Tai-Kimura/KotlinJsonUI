package com.example.kotlinjsonui.sample.views.weight_test

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.WeightTestData
import com.example.kotlinjsonui.sample.viewmodels.WeightTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun WeightTestGeneratedView(
    data: WeightTestData,
    viewModel: WeightTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from weight_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "weight_test",
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
                android.util.Log.e("DynamicView", "Error loading weight_test: \$error")
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
    ) {
        Section0(data, viewModel)
        Section1(data, viewModel)
        Section2(data, viewModel)
        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .height(60.dp)
        ) {
            val resolved_text216 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.weight_test_weight_1),
                color = colorResource(R.color.black),
                fontFamily = resolved_text216.family,
                fontWeight = resolved_text216.weight,
                fontSize = resolved_text216.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text216.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 18.2.sp),
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
                    .background(colorResource(R.color.pale_pink)),
                textAlign = TextAlign.Center
            )
            val resolved_text217 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.weight_test_weight_2),
                color = colorResource(R.color.black),
                fontFamily = resolved_text217.family,
                fontWeight = resolved_text217.weight,
                fontSize = resolved_text217.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text217.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 18.2.sp),
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .background(colorResource(R.color.pale_yellow)),
                textAlign = TextAlign.Center
            )
            val resolved_text218 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.weight_test_weight_1),
                color = colorResource(R.color.black),
                fontFamily = resolved_text218.family,
                fontWeight = resolved_text218.weight,
                fontSize = resolved_text218.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text218.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 18.2.sp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(colorResource(R.color.pale_cyan)),
                textAlign = TextAlign.Center
            )
        }
        Section4(data, viewModel)
        Column(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .height(300.dp)
        ) {
            val resolved_text220 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.weight_test_weight_1),
                color = colorResource(R.color.black),
                fontFamily = resolved_text220.family,
                fontWeight = resolved_text220.weight,
                fontSize = resolved_text220.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text220.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 18.2.sp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .height(0.dp)
                    .background(colorResource(R.color.white_2)),
                textAlign = TextAlign.Center
            )
            val resolved_text221 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.weight_test_weight_3),
                color = colorResource(R.color.black),
                fontFamily = resolved_text221.family,
                fontWeight = resolved_text221.weight,
                fontSize = resolved_text221.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text221.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 18.2.sp),
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
                    .height(0.dp)
                    .background(colorResource(R.color.white_3)),
                textAlign = TextAlign.Center
            )
            val resolved_text222 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 14.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.weight_test_weight_2),
                color = colorResource(R.color.black),
                fontFamily = resolved_text222.family,
                fontWeight = resolved_text222.weight,
                fontSize = resolved_text222.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text222.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 18.2.sp),
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
                    .height(0.dp)
                    .background(colorResource(R.color.white_4)),
                textAlign = TextAlign.Center
            )
        }
        Section6(data, viewModel)
        Section7(data, viewModel)
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: WeightTestData,
    viewModel: WeightTestViewModel
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
        val resolved_button17 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button17.family,
            fontWeight = resolved_button17.weight,
            fontSize = resolved_button17.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button17.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section1(
    data: WeightTestData,
    viewModel: WeightTestViewModel
) {
    val resolved_text214 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = "${data.title}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text214.family,
        fontWeight = resolved_text214.weight,
        fontSize = resolved_text214.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text214.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 31.2.sp),
        modifier = Modifier
            .testTag("title_label")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 20.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section2(
    data: WeightTestData,
    viewModel: WeightTestViewModel
) {
    val resolved_text215 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_horizontal_weight_distribution_),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text215.family,
        fontWeight = resolved_text215.weight,
        fontSize = resolved_text215.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text215.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section4(
    data: WeightTestData,
    viewModel: WeightTestViewModel
) {
    val resolved_text219 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_vertical_weight_distribution_13),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text219.family,
        fontWeight = resolved_text219.weight,
        fontSize = resolved_text219.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text219.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section6(
    data: WeightTestData,
    viewModel: WeightTestViewModel
) {
    val resolved_text223 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.weight_test_widthweight_test),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text223.family,
        fontWeight = resolved_text223.weight,
        fontSize = resolved_text223.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text223.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section7(
    data: WeightTestData,
    viewModel: WeightTestViewModel
) {
    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .height(60.dp)
    ) {
        val resolved_text224 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.weight_test_widthweight_1),
            color = colorResource(R.color.black),
            fontFamily = resolved_text224.family,
            fontWeight = resolved_text224.weight,
            fontSize = resolved_text224.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text224.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 18.2.sp),
            modifier = Modifier
                .width(0.dp)
                .fillMaxHeight()
                .background(colorResource(R.color.white_5)),
            textAlign = TextAlign.Center
        )
        val resolved_text225 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.weight_test_widthweight_1),
            color = colorResource(R.color.black),
            fontFamily = resolved_text225.family,
            fontWeight = resolved_text225.weight,
            fontSize = resolved_text225.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text225.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 18.2.sp),
            modifier = Modifier
                .width(0.dp)
                .fillMaxHeight()
                .background(colorResource(R.color.white_6)),
            textAlign = TextAlign.Center
        )
    }
}
// >>> RESPONSIVE_HELPERS_END